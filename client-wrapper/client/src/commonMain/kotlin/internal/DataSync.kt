package qbittorrent.internal

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import qbittorrent.QBittorrentClient
import qbittorrent.QBittorrentException
import qbittorrent.json
import qbittorrent.models.MainData
import qbittorrent.models.TorrentPeers

typealias DataStatePair<T> = Pair<T?, QBittorrentException?>

internal abstract class DataSync<T>(
    private val typeInfo: TypeInfo,
    private val http: HttpClient,
    private val config: QBittorrentClient.Config,
    syncScope: CoroutineScope,
) {

    abstract val endpointUrl: String
    abstract val nestedObjectKeys: List<String>
    open fun HttpRequestBuilder.configureRequest() = Unit

    private val serializer = serializer(requireNotNull(typeInfo.kotlinType))
    private val state = MutableStateFlow<DataStatePair<T>>(null to null)
    private val isSyncingState =
        state.subscriptionCount.map { it > 0 }.stateIn(syncScope, Eagerly, false)
    private val atomicSyncRid = AtomicReference(0L)
    private var syncRid: Long
        get() = atomicSyncRid.value
        set(value) {
            atomicSyncRid.value = value
        }
    private val syncLoopJob =
        syncScope.launch {
            while (true) {
                // Wait for the first subscribers
                isSyncingState.first { it }
                syncData()
            }
        }

    fun isSyncing(): Boolean {
        return isSyncingState.value
    }

    fun observeData(): Flow<DataStatePair<T>> {
        return state
    }

    fun close() {
        syncLoopJob.cancel()
    }

    private suspend fun syncData() {
        try {
            // Get the current MainData value, fetching the initial data if required
            val (initialMainData, _) =
                state.updateAndGet { (mainData, error) ->
                    if (error == null) {
                        (mainData ?: fetchData(0).bodyOrThrow(typeInfo)) to null
                    } else {
                        // Last request produced an error, try it again
                        fetchData(syncRid).bodyOrThrow<T>(typeInfo) to null
                    }
                }

            delay(config.syncInterval)

            val mainDataJson = json.encodeToJsonElement(serializer, initialMainData).mutateJson()
            // Patch MainData while there is at least one subscriber
            while (isSyncingState.value) {
                if (syncRid == Long.MAX_VALUE) syncRid = 0

                // Fetch the next MainData patch and merge into existing model, remove any error
                state.value = mainDataJson.applyPatch(fetchData(++syncRid).bodyOrThrow()) to null

                delay(config.syncInterval)
            }
        } catch (e: QBittorrentException) {
            // Failed to fetch patch, keep current MainData and add the error
            state.update { (mainData, _) -> mainData to e }
            yield()
            // Active state subscribers have seen the error, clear it
            state.update { (mainData, _) -> mainData to null }
        }
    }

    private suspend fun fetchData(rid: Long): HttpResponse {
        return http.get("${config.baseUrl}/${endpointUrl.trimStart('/')}") {
            parameter("rid", rid)
            configureRequest()
        }
    }

    private fun MutableMap<String, JsonElement>.applyPatch(newObject: JsonObject): T {
        merge(newObject, nestedObjectKeys)
        nestedObjectKeys.forEach { key -> dropRemoved(key) }
        dropRemovedStrings("tags")

        // Note: Create new model instance here so that one update event includes
        // identifiers of removed data
        @Suppress("UNCHECKED_CAST")
        val mainData: T = json.decodeFromJsonElement(serializer, JsonObject(this)) as T

        nestedObjectKeys.forEach { key -> resetRemoved(key) }
        resetRemoved("tags")
        return mainData
    }
}

/** Manages a single [MainData] instance and updates it periodically when it is being observed. */
internal class MainDataSync(
    http: HttpClient,
    config: QBittorrentClient.Config,
    syncScope: CoroutineScope,
) :
    DataSync<MainData>(
        typeInfo = typeInfo<MainData>(),
        http = http,
        config = config,
        syncScope = syncScope
    ) {
    override val endpointUrl: String = "/api/v2/sync/maindata"
    override val nestedObjectKeys: List<String> = listOf("torrents", "categories")
}

/**
 * Manages a single [TorrentPeers] instance and updates it periodically when it is being observed.
 */
internal class TorrentPeersSync(
    private val hash: String,
    http: HttpClient,
    config: QBittorrentClient.Config,
    syncScope: CoroutineScope,
) :
    DataSync<TorrentPeers>(
        typeInfo = typeInfo<TorrentPeers>(),
        http = http,
        config = config,
        syncScope = syncScope,
    ) {
    override val endpointUrl: String = "/api/v2/sync/torrentPeers"
    override val nestedObjectKeys: List<String> = listOf("peers")

    override fun HttpRequestBuilder.configureRequest() {
        parameter("hash", hash)
    }
}
