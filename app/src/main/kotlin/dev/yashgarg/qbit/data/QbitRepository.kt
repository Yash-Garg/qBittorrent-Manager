package dev.yashgarg.qbit.data

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import dev.yashgarg.qbit.data.manager.ClientManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient
import qbittorrent.models.MainData
import qbittorrent.models.Torrent
import qbittorrent.models.TorrentFile
import qbittorrent.models.TorrentPeers
import qbittorrent.models.TorrentProperties
import qbittorrent.models.TorrentTracker

class QbitRepository @Inject constructor(private val clientManager: ClientManager) {
    private lateinit var client: QBittorrentClient
    private val scope by lazy { CoroutineScope(Dispatchers.IO) }

    init {
        scope.launch { client = clientManager.checkAndGetClient() ?: return@launch }
    }

    fun observeMainData(): Flow<MainData> {
        return client.observeMainData()
    }

    fun observeTorrent(hash: String, waitIfMissing: Boolean): Flow<Torrent> {
        return client.observeTorrent(hash, waitIfMissing)
    }

    fun observeTorrentPeers(hash: String): Flow<TorrentPeers> {
        return client.observeTorrentPeers(hash)
    }

    suspend fun addTorrentUrl(url: String): Result<Unit, Throwable> {
        return runCatching { client.addTorrent { urls.add(url) } }
    }

    suspend fun addTorrentFile(bytes: ByteArray): Result<Unit, Throwable> {
        return runCatching { client.addTorrent { rawTorrents["torrent_file"] = bytes } }
    }

    suspend fun removeTorrents(
        hashes: List<String>,
        deleteFiles: Boolean = false
    ): Result<Unit, Throwable> {
        return runCatching { client.deleteTorrents(hashes, deleteFiles) }
    }

    suspend fun toggleTorrentsState(hashes: List<String>, pause: Boolean): Result<Unit, Throwable> {
        return runCatching {
            if (pause) client.pauseTorrents(hashes) else client.resumeTorrents(hashes)
        }
    }

    suspend fun getSpeedLimitMode(): Result<Int, Throwable> {
        return runCatching { client.getSpeedLimitsMode() }
    }

    suspend fun toggleSpeedLimitsMode(): Result<Unit, Throwable> {
        return runCatching { client.toggleSpeedLimitsMode() }
    }

    suspend fun recheckTorrents(hashes: List<String>): Result<Unit, Throwable> {
        return runCatching { client.recheckTorrents(hashes) }
    }

    suspend fun reannounceTorrents(hashes: List<String>): Result<Unit, Throwable> {
        return runCatching { client.reannounceTorrents(hashes) }
    }

    suspend fun renameTorrent(hash: String, name: String): Result<Unit, Throwable> {
        return runCatching { client.setTorrentName(hash, name) }
    }

    suspend fun banPeers(peers: List<String>): Result<Unit, Throwable> {
        return runCatching { client.banPeers(peers) }
    }

    suspend fun getTorrentProperties(hash: String): Result<TorrentProperties, Throwable> {
        return runCatching { client.getTorrentProperties(hash) }
    }

    suspend fun getTorrentTrackers(hash: String): Result<List<TorrentTracker>, Throwable> {
        return runCatching { client.getTrackers(hash) ?: emptyList() }
    }

    suspend fun getTorrentFiles(hash: String): Result<List<TorrentFile>, Throwable> {
        return runCatching { client.getTorrentFiles(hash) }
    }
}
