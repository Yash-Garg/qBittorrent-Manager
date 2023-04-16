package qbittorrent

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.coroutines.cancellation.CancellationException
import kotlin.native.concurrent.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import qbittorrent.internal.*
import qbittorrent.internal.AtomicReference
import qbittorrent.internal.FileReader
import qbittorrent.internal.MainDataSync
import qbittorrent.internal.RawCookiesStorage
import qbittorrent.internal.bodyOrThrow
import qbittorrent.internal.orThrow
import qbittorrent.models.*

private const val PARAM_URLS = "urls"
private const val PARAM_TORRENTS = "torrents"
private const val PARAM_SAVE_PATH = "savepath"
private const val PARAM_COOKIE = "cookie"
private const val PARAM_CATEGORY = "category"
private const val PARAM_TAGS = "tags"
private const val PARAM_SKIP_CHECKING = "skip_checking"
private const val PARAM_PAUSED = "paused"
private const val PARAM_ROOT_FOLDER = "root_folder"
private const val PARAM_RENAME = "rename"
private const val PARAM_UP_LIMIT = "upLimit"
private const val PARAM_DL_LIMIT = "dlLimit"
private const val PARAM_RATIO_LIMIT = "ratioLimit"
private const val PARAM_SEEDING_TIME_LIMIT = "seedingTimeLimit"
private const val PARAM_AUTO_TTM = "autoTTM"
private const val PARAM_SEQUENTIAL_DOWNLOAD = "sequentialDownload"
private const val PARAM_FIRST_LAST_PIECE = "firstLastPiecePrio"

internal val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    useAlternativeNames = false
}

/**
 * qBittorrent Web API wrapper.
 *
 * https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-(qBittorrent-4.1)
 *
 * @param baseUrl The base URL of qBittorrent, ex. http://localhost:9000
 * @param username The qBittorrent username, default: admin
 * @param password The qBittorrent password, default: adminadmin
 * @param syncInterval The sync endpoint polling rate when subscribed to a [Flow], defaults to 5
 *   seconds.
 * @param httpClient Custom HTTPClient, useful when a default client engine is not used
 * @param dispatcher Coroutine dispatcher for flow API processing, defaults to
 *   [Dispatchers.Default].
 */
class QBittorrentClient(
    baseUrl: String,
    username: String = "admin",
    password: String = "adminadmin",
    syncInterval: Duration = 5.seconds,
    httpClient: HttpClient = HttpClient(),
    dispatcher: CoroutineDispatcher = Default,
) {
    companion object {
        const val RATIO_LIMIT_NONE = -1
        const val RATIO_LIMIT_GLOBAL = -2
        const val SEEDING_LIMIT_NONE = -1
        const val SEEDING_LIMIT_GLOBAL = -2

        private val allList = listOf("all")
    }

    internal data class Config(
        val baseUrl: String,
        val username: String,
        val password: String,
        val syncInterval: Duration,
    )

    @Suppress("HttpUrlsUsage")
    private val config =
        Config(
            if (baseUrl.startsWith("http", true)) baseUrl else "http://$baseUrl",
            username,
            password,
            syncInterval,
        )

    internal val http: HttpClient =
        httpClient.config {
            install(ErrorTransformer)
            install(AuthHandler) { config = this@QBittorrentClient.config }
            install(ContentNegotiation) { json(json) }
            install(HttpCookies) { storage = RawCookiesStorage(AcceptAllCookiesStorage()) }
        }
    private val syncScope = CoroutineScope(SupervisorJob() + dispatcher + http.coroutineContext)
    private val mainDataSync = MainDataSync(http, config, syncScope)
    private val peerDataSyncMapAtomic = AtomicReference(emptyMap<String, TorrentPeersSync>())
    private var peerDataSyncMap: Map<String, TorrentPeersSync>
        get() = peerDataSyncMapAtomic.value
        set(value) {
            peerDataSyncMapAtomic.value = value
        }

    private fun getPeersSync(hash: String): TorrentPeersSync? {
        return peerDataSyncMap[hash.lowercase()]
    }

    private fun createPeersSync(hash: String): TorrentPeersSync {
        return TorrentPeersSync(hash.lowercase(), http, config, syncScope).also { peersSync ->
            peerDataSyncMap = peerDataSyncMap + (hash to peersSync)
        }
    }

    private fun removePeersSync(hash: String) {
        peerDataSyncMap[hash]?.close()
        peerDataSyncMap = peerDataSyncMap - hash
    }

    /**
     * Create a session with the username and password provided in the constructor.
     *
     * NOTE: Calling [login] is not required as authentication is managed internally.
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun login() {
        http.plugin(AuthHandler).run {
            if (!tryAuth(http)) {
                val response = lastAuthResponseState.filterNotNull().first()
                throw QBittorrentException(response, response.bodyAsText())
            }
        }
    }

    /** End the current session. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun logout() {
        http.get("${config.baseUrl}/api/v2/auth/logout").orThrow()
    }

    /**
     * Returns true when [observeMainData] or [observeTorrent] have at least one subscriber, meaning
     * the syncing endpoint is being polled at [syncInterval].
     */
    val isSyncing: Boolean
        get() = mainDataSync.isSyncing()

    /**
     * Emits the next [MainData] every [syncInterval] while subscribed.
     *
     * NOTE: The underlying logic and network requests will be started only once, no matter how many
     * times you invoke [observeMainData].
     */
    fun observeMainData(): Flow<MainData> {
        return mainDataSync.observeData().transform { (mainData, error) ->
            error?.let { throw it }
            mainData?.let { emit(it) }
        }
    }

    /**
     * Emits the latest [Torrent] data for the [hash]. If the torrent is removed or not found, the
     * flow will complete unless [waitIfMissing] is true.
     *
     * @param hash The info hash of the torrent to observe.
     * @param waitIfMissing When true, wait for the [hash] if it does not exist
     */
    fun observeTorrent(hash: String, waitIfMissing: Boolean = false): Flow<Torrent> {
        return if (waitIfMissing) {
                observeMainData().takeWhile { mainData -> !mainData.torrentsRemoved.contains(hash) }
            } else {
                observeMainData().takeWhile { mainData -> mainData.torrents.contains(hash) }
            }
            .mapNotNull { mainData -> mainData.torrents[hash] }
            .distinctUntilChanged()
    }

    /**
     * Emits the latest [TorrentPeers] data for the [hash]. If the torrent is removed or not found,
     * the flow will complete.
     *
     * @param hash The info hash of the torrent to observe.
     */
    fun observeTorrentPeers(hash: String): Flow<TorrentPeers> {
        val peersSync = getPeersSync(hash) ?: createPeersSync(hash)
        return peersSync
            .observeData()
            .takeWhile { (_, error) -> (error?.response?.status != HttpStatusCode.NotFound) }
            .transform { (mainData, error) ->
                error?.let { throw it }
                mainData?.let { emit(it) }
            }
            .onCompletion {
                syncScope.launch {
                    if (getPeersSync(hash)?.isSyncing() == false) {
                        removePeersSync(hash)
                    }
                }
            }
    }

    /**
     * This method can add torrents from server local file or from URLs. http://, https://, magnet:
     * and bc://bt/ links are supported.
     *
     * To include torrents, add HTTP and magnet urls to [AddTorrentBody.urls], or file paths to
     * [AddTorrentBody.torrents]. Only one [AddTorrentBody.urls] or [AddTorrentBody.torrents] entry
     * is required for the request to succeed.
     *
     * @param configure A function to configure the request body with [AddTorrentBody].
     * @see AddTorrentBody for all available options.
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun addTorrent(configure: AddTorrentBody.() -> Unit) {
        val body = AddTorrentBody().apply(configure)

        http
            .submitFormWithBinaryData(
                "${config.baseUrl}/api/v2/torrents/add",
                formData {
                    fun appendUnlessNull(param: String, value: Any?) {
                        value?.toString()?.let { append(param, it) }
                    }
                    appendUnlessNull(
                        PARAM_URLS,
                        body.urls.joinToString("|").takeUnless(String::isBlank)
                    )
                    appendUnlessNull(PARAM_SAVE_PATH, body.savepath)
                    appendUnlessNull(PARAM_COOKIE, body.cookie)
                    appendUnlessNull(PARAM_CATEGORY, body.category)
                    appendUnlessNull(
                        PARAM_TAGS,
                        body.tags.joinToString(",").takeUnless(String::isBlank)
                    )
                    appendUnlessNull(PARAM_SKIP_CHECKING, body.skipChecking)
                    appendUnlessNull(PARAM_PAUSED, body.paused)
                    appendUnlessNull(PARAM_ROOT_FOLDER, body.rootFolder)
                    appendUnlessNull(PARAM_RENAME, body.rename)
                    appendUnlessNull(PARAM_UP_LIMIT, body.upLimit)
                    appendUnlessNull(PARAM_DL_LIMIT, body.dlLimit)
                    appendUnlessNull(PARAM_RATIO_LIMIT, body.ratioLimit)
                    appendUnlessNull(
                        PARAM_SEEDING_TIME_LIMIT,
                        body.seedingTimeLimit?.inWholeSeconds
                    )
                    appendUnlessNull(PARAM_AUTO_TTM, body.autoTMM)
                    appendUnlessNull(PARAM_SEQUENTIAL_DOWNLOAD, body.sequentialDownload)
                    appendUnlessNull(PARAM_FIRST_LAST_PIECE, body.firstLastPiecePriority)
                    val torrentFiles =
                        body.torrents
                            .mapNotNull { filePath ->
                                FileReader.contentOrNull(filePath)?.let { filePath to it }
                            }
                            .toMap()
                            .plus(body.rawTorrents)
                    torrentFiles.forEach { (torrentPath, fileContent) ->
                        val filename = torrentPath.substringAfterLast('/').substringAfterLast('\\')
                        append(
                            PARAM_TORRENTS,
                            fileContent,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=${filename.escapeIfNeeded()}"
                                )
                            }
                        )
                    }
                }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getTorrents(
        filter: TorrentFilter = TorrentFilter.ALL,
        category: String = "",
        sort: String = "",
        reverse: Boolean = false,
        limit: Int = 0,
        offset: Int = 0,
        tag: String? = null,
        hashes: List<String> = emptyList()
    ): List<Torrent> {
        return http
            .get("${config.baseUrl}/api/v2/torrents/info") {
                parameter("filter", filter.name.lowercase())
                parameter("reverse", reverse)
                parameter("limit", limit)
                parameter("offset", offset)
                if (hashes.isNotEmpty()) {
                    parameter("hashes", hashes.joinToString("|"))
                }
                if (category.isNotBlank()) {
                    parameter("category", category)
                }
                if (sort.isNotBlank()) {
                    parameter("sort", sort)
                }
                if (tag != null) {
                    parameter("tag", tag)
                }
            }
            .bodyOrThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getTorrentProperties(hash: String): TorrentProperties {
        return http
            .get("${config.baseUrl}/api/v2/torrents/properties") { parameter("hash", hash) }
            .bodyOrThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getGlobalTransferInfo(): GlobalTransferInfo {
        return http.get("${config.baseUrl}/api/v2/transfer/info").bodyOrThrow()
    }

    /** Get the [TorrentFile]s for [hash] or an empty list if not yet not available. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getTorrentFiles(hash: String, indexes: List<Int> = emptyList()): List<TorrentFile> {
        return http
            .get("${config.baseUrl}/api/v2/torrents/files") {
                parameter("hash", hash)
                if (indexes.isNotEmpty()) {
                    parameter("indexes", indexes.joinToString("|"))
                }
            }
            .bodyOrThrow()
    }

    /** Get piece states for the torrent at [hash]. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getPieceStates(hash: String): List<PieceState> {
        return http
            .get("${config.baseUrl}/api/v2/torrents/pieceStates") { parameter("hash", hash) }
            .bodyOrThrow()
    }

    /** Get piece hashes for the torrent at [hash]. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getPieceHashes(hash: String): List<String> {
        return http
            .get("${config.baseUrl}/api/v2/torrents/pieceHashes") { parameter("hash", hash) }
            .bodyOrThrow()
    }

    /**
     * Pause one or more torrents
     *
     * @param hashes A single torrent hash, list of torrents, or 'all'.
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun pauseTorrents(hashes: List<String> = allList) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/pause",
                formParameters = Parameters.build { append("hashes", hashes.joinToString("|")) }
            )
            .orThrow()
    }

    /**
     * Resume one or more torrents
     *
     * @param hashes A single torrent hash, list of torrents, or 'all'.
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun resumeTorrents(hashes: List<String> = allList) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/resume",
                formParameters = Parameters.build { append("hashes", hashes.joinToString("|")) }
            )
            .orThrow()
    }

    /**
     * Delete one or more torrents.
     *
     * @param hashes A single torrent hash, list of torrents, or 'all'.
     * @param deleteFiles If true, delete all the torrents files.
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun deleteTorrents(hashes: List<String>, deleteFiles: Boolean = false) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/delete",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("deleteFiles", deleteFiles.toString())
                    }
            )
            .orThrow()
    }

    /** Recheck a torrent in qBittorrent. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun recheckTorrents(hashes: List<String> = allList) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/recheck",
                formParameters = Parameters.build { append("hashes", hashes.joinToString("|")) }
            )
            .orThrow()
    }

    /** Reannounce a torrent. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun reannounceTorrents(hashes: List<String> = allList) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/reannounce",
                formParameters = Parameters.build { append("hashes", hashes.joinToString("|")) }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getWebseeds(hash: String): List<Webseed> {
        return http
            .get("${config.baseUrl}/api/v2/torrents/webseeds") { parameter("hash", hash) }
            .bodyOrThrow()
    }

    /** Get the qBittorrent application preferences. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getPreferences(): JsonObject =
        http.get("${config.baseUrl}/api/v2/app/preferences").bodyOrThrow()

    /** Set one or more qBittorrent application preferences. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setPreferences(prefs: JsonObject) {
        http
            .post("${config.baseUrl}/api/v2/app/setPreferences") {
                contentType(ContentType.Application.Json)
                setBody(buildJsonObject { put("json", prefs) })
            }
            .orThrow()
    }

    /** Get the application version. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getVersion(): String =
        http.get("${config.baseUrl}/api/v2/app/version").bodyOrThrow()

    /** Get the Web API version. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getApiVersion(): String =
        http.get("${config.baseUrl}/api/v2/app/webapiVersion").bodyOrThrow()

    /** Get the build info */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getBuildInfo(): BuildInfo =
        http.get("${config.baseUrl}/api/v2/app/buildInfo").bodyOrThrow()

    /** Shutdown qBittorrent */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun shutdown() {
        http.get("${config.baseUrl}/api/v2/app/shutdown").orThrow()
    }

    /** Get the default torrent save path, ex. /user/home/downloads */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getDefaultSavePath(): String =
        http.get("${config.baseUrl}/api/v2/app/defaultSavePath").bodyOrThrow()

    /** @param lastKnownId Exclude messages with "message id" <= last_known_id (default: -1) */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getPeerLogs(lastKnownId: Int = -1): List<PeerLog> =
        http
            .get("${config.baseUrl}/api/v2/log/peers") { parameter("last_known_id", lastKnownId) }
            .bodyOrThrow()

    /**
     * @param normal Include normal messages (default: true)
     * @param info Include info messages (default: true)
     * @param warning Include warning messages (default: true)
     * @param critical Include critical messages (default: true)
     * @param lastKnownId Exclude messages with "message id" <= last_known_id (default: -1)
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getLogs(
        normal: Boolean = true,
        info: Boolean = true,
        warning: Boolean = true,
        critical: Boolean = true,
        lastKnownId: Int = -1
    ): List<LogEntry> =
        http
            .get("${config.baseUrl}/api/v2/log/main") {
                parameter("normal", normal)
                parameter("info", info)
                parameter("warning", warning)
                parameter("critical", critical)
                parameter("last_known_id", lastKnownId)
            }
            .bodyOrThrow()

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun editTrackers(hash: String, originalUrl: String, newUrl: String) {
        http
            .get("${config.baseUrl}/api/v2/torrents/editTracker") {
                parameter("hash", hash)
                parameter("origUrl", originalUrl)
                parameter("newUrl", newUrl)
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun addTrackers(hash: String, urls: List<String>) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/addTrackers",
                formParameters =
                    Parameters.build {
                        append("hash", hash)
                        append("urls", urls.joinToString("\n"))
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun removeTrackers(hash: String, urls: List<String>) {
        http
            .get("${config.baseUrl}/api/v2/torrents/removeTrackers") {
                parameter("hash", hash)
                parameter("urls", urls.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getTrackers(hash: String): List<TorrentTracker>? {
        val response =
            http.get("${config.baseUrl}/api/v2/torrents/trackers") { parameter("hash", hash) }
        return if (response.status.isSuccess()) {
            response.body()
        } else {
            if (response.status != HttpStatusCode.NotFound) {
                response.orThrow()
            }
            null
        }
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun increasePriority(hashes: List<String> = allList) {
        http
            .get("${config.baseUrl}/api/v2/torrents/increasePrio") {
                parameter("hashes", hashes.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun decreasePriority(hashes: List<String> = allList) {
        http
            .get("${config.baseUrl}/api/v2/torrents/decreasePrio") {
                parameter("hashes", hashes.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun maxPriority(hashes: List<String> = allList) {
        http
            .get("${config.baseUrl}/api/v2/torrents/topPrio") {
                parameter("hashes", hashes.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun minPriority(hashes: List<String> = allList) {
        http
            .get("${config.baseUrl}/api/v2/torrents/bottomPrio") {
                parameter("hashes", hashes.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setFilePriority(hash: String, ids: List<Int>, priority: Int) {
        http
            .get("${config.baseUrl}/api/v2/torrents/filePrio") {
                parameter("hash", hash)
                parameter("id", ids.joinToString("|"))
                parameter("priority", priority)
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getTorrentDownloadLimit(hashes: List<String> = allList): Map<String, Long> {
        return http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/downloadLimit",
                formParameters = Parameters.build { append("hashes", hashes.joinToString("|")) }
            )
            .bodyOrThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setTorrentDownloadLimit(hashes: List<String> = allList) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/downloadLimit",
                formParameters = Parameters.build { append("hashes", hashes.joinToString("|")) }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setTorrentShareLimits(
        hashes: List<String> = allList,
        ratioLimit: Float,
        seedingTimeLimit: Duration
    ) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/setShareLimits",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("ratioLimit", ratioLimit.toString())
                        append("seedingTimeLimit", seedingTimeLimit.inWholeSeconds.toString())
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getTorrentUploadLimit(hashes: List<String> = allList): Map<String, Long> {
        return http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/uploadLimit",
                formParameters = Parameters.build { append("hashes", hashes.joinToString("|")) }
            )
            .bodyOrThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setTorrentUploadLimit(hashes: List<String> = allList, limit: Long) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/setUploadLimit",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("limit", limit.toString())
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setTorrentLocation(hashes: List<String> = allList, location: String) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/setLocation",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("location", location)
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setTorrentName(hash: String, name: String) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/rename",
                formParameters =
                    Parameters.build {
                        append("hash", hash)
                        append("name", name)
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setTorrentCategory(hashes: List<String> = allList, category: String) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/setCategory",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("category", category)
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getCategories(): List<Category> {
        return http
            .get("${config.baseUrl}/api/v2/torrents/categories")
            .bodyOrThrow<Map<String, Category>>()
            .values
            .toList()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun createCategory(name: String, savePath: String) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/createCategory",
                formParameters =
                    Parameters.build {
                        append("category", name)
                        append("savePath", savePath)
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun editCategory(name: String, savePath: String) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/editCategory",
                formParameters =
                    Parameters.build {
                        append("category", name)
                        append("savePath", savePath)
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun removeCategories(names: List<String>) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/removeCategory",
                formParameters = Parameters.build { appendAll("category", names) }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun addTorrentTags(hashes: List<String> = allList, tags: List<String>) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/addTags",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("tags", tags.joinToString(","))
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun removeTorrentTags(hashes: List<String> = allList, tags: List<String>) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/removeTags",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("tags", tags.joinToString(","))
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getTags(): List<String> =
        http.get("${config.baseUrl}/api/v2/torrents/tags").bodyOrThrow()

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun createTags(tags: List<String>) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/createTags",
                formParameters = Parameters.build { append("tags", tags.joinToString(",")) }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun deleteTags(tags: List<String>) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/deleteTags",
                formParameters = Parameters.build { append("tags", tags.joinToString(",")) }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setAutoTorrentManagement(hashes: List<String> = allList, enabled: Boolean) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/setAutoManagement",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("enabled", enabled.toString())
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun toggleSequentialDownload(hashes: List<String> = allList) {
        http
            .get("${config.baseUrl}/api/v2/torrents/toggleSequentialDownload") {
                parameter("hashes", hashes.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun toggleFirstLastPriority(hashes: List<String> = allList) {
        http
            .get("${config.baseUrl}/api/v2/torrents/toggleFirstLastPiecePrio") {
                parameter("hashes", hashes.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setForceStart(hashes: List<String> = allList, value: Boolean) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/setForceStart",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("value", value.toString())
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setSuperSeeding(hashes: List<String> = allList, value: Boolean) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/torrents/setSuperSeeding",
                formParameters =
                    Parameters.build {
                        append("hashes", hashes.joinToString("|"))
                        append("value", value.toString())
                    }
            )
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun renameFile(hash: String, id: Int, newName: String) {
        http
            .get("${config.baseUrl}/api/v2/torrents/renameFile") {
                parameter("hash", hash)
                parameter("id", id)
                parameter("name", newName)
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun renameFolder(hash: String, id: Int, newName: String) {
        http
            .get("${config.baseUrl}/api/v2/torrents/renameFolder") {
                parameter("hash", hash)
                parameter("id", id)
                parameter("name", newName)
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun addPeers(hashes: List<String>, peers: List<String>) {
        http
            .get("${config.baseUrl}/api/v2/torrents/addPeers") {
                parameter("hashes", hashes.joinToString("|"))
                parameter("peers", peers.joinToString("|"))
            }
            .orThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun banPeers(peers: List<String>) {
        http
            .submitForm(
                "${config.baseUrl}/api/v2/transfer/banPeers",
                formParameters = Parameters.build { append("peers", peers.joinToString("|")) }
            )
            .orThrow()
    }

    /** The response is 1 if alternative speed limits are enabled, 0 otherwise. */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getSpeedLimitsMode(): Int {
        return http.get("${config.baseUrl}/api/v2/transfer/speedLimitsMode").bodyOrThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun toggleSpeedLimitsMode() {
        http.post("${config.baseUrl}/api/v2/transfer/toggleSpeedLimitsMode").orThrow()
    }

    /**
     * The response is the value of current global download speed limit in bytes/second; this value
     * will be zero if no limit is applied.
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getGlobalDownloadLimit(): Int {
        return http.get("${config.baseUrl}/api/v2/transfer/downloadLimit").bodyOrThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setGlobalDownloadLimit(limit: Int) {
        http
            .get("${config.baseUrl}/api/v2/transfer/setDownloadLimit") { parameter("limit", limit) }
            .orThrow()
    }

    /**
     * The response is the value of current global upload speed limit in bytes/second; this value
     * will be zero if no limit is applied.
     */
    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun getGlobalUploadLimit(): Int {
        return http.get("${config.baseUrl}/api/v2/transfer/uploadLimit").bodyOrThrow()
    }

    @Throws(QBittorrentException::class, CancellationException::class)
    suspend fun setGlobalUploadLimit(limit: Int) {
        http
            .get("${config.baseUrl}/api/v2/transfer/setUploadLimit") { parameter("limit", limit) }
            .orThrow()
    }
}

internal suspend fun login(http: HttpClient, config: QBittorrentClient.Config): HttpResponse {
    return http.submitForm(
        "${config.baseUrl}/api/v2/auth/login",
        formParameters =
            Parameters.build {
                append("username", config.username)
                append("password", config.password)
            }
    ) {
        header("Referer", config.baseUrl)
    }
}
