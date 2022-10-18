package dev.yashgarg.qbit.ui.torrent

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.runCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.utils.TorrentRemovedError
import dev.yashgarg.qbit.utils.TransformUtil
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient
import qbittorrent.models.TorrentPeer

@HiltViewModel
class TorrentDetailsViewModel
@Inject
constructor(private val clientManager: ClientManager, state: SavedStateHandle) : ViewModel() {
    private lateinit var client: QBittorrentClient
    private val _uiState = MutableStateFlow(TorrentDetailsState())
    val uiState = _uiState.asStateFlow()

    private val _status = MutableSharedFlow<String>()
    val status = _status.asSharedFlow()

    private val hash by lazy { state.get<String>("torrentHash") }

    init {
        viewModelScope.launch {
            val clientResult = clientManager.checkAndGetClient()
            clientResult?.let {
                client = it
                syncTorrentFlow()
                syncPeers()
            }
        }
    }

    fun toggleTorrent(pause: Boolean, hash: String) {
        val hashes = listOf(hash)
        viewModelScope.launch {
            when (
                runCatching {
                    if (pause) client.pauseTorrents(hashes) else client.resumeTorrents(hashes)
                }
            ) {
                is Ok -> _status.emit("${if (pause) "Paused" else "Resumed"} $hash")
                is Err -> _status.emit("Failed to ${if (pause) "pause" else "resume"} $hash")
            }
        }
    }

    fun removeTorrent(hash: String, deleteFiles: Boolean = false) {
        viewModelScope.launch {
            when (runCatching { client.deleteTorrents(listOf(hash), deleteFiles) }) {
                is Ok -> return@launch
                is Err -> _status.emit("Failed to remove $hash")
            }
        }
    }

    fun forceRecheck(hash: String) {
        viewModelScope.launch {
            when (runCatching { client.recheckTorrents(listOf(hash)) }) {
                is Ok -> _status.emit("Rechecking $hash")
                is Err -> _status.emit("Failed to recheck torrent")
            }
        }
    }

    fun forceReannounce(hash: String) {
        viewModelScope.launch {
            when (runCatching { client.reannounceTorrents(listOf(hash)) }) {
                is Ok -> _status.emit("Reannouncing $hash")
                is Err -> _status.emit("Failed to reannounce torrent")
            }
        }
    }

    fun renameTorrent(torrentName: String, torrentHash: String) {
        viewModelScope.launch {
            when (runCatching { client.setTorrentName(torrentHash, torrentName) }) {
                is Ok -> _status.emit("Successfully renamed $torrentHash")
                is Err -> _status.emit("Failed to rename torrent")
            }
        }
    }

    fun banPeer(peer: TorrentPeer) {
        val peerAddr = "${peer.ip}:${peer.port}"
        viewModelScope.launch {
            when (runCatching { client.banPeers(listOf(peerAddr)) }) {
                is Ok -> _status.emit("Successfully banned $peerAddr")
                is Err -> _status.emit("Failed to ban peer")
            }
        }
    }

    private suspend fun syncTorrentFlow() {
        val hash = requireNotNull(hash)
        viewModelScope
            .launch {
                client
                    .observeTorrent(hash, waitIfMissing = false)
                    .onEach { info ->
                        _uiState.update { state ->
                            state.copy(
                                loading = false,
                                torrent = info,
                                trackers = client.getTrackers(hash) ?: emptyList(),
                                torrentProperties = client.getTorrentProperties(hash)
                            )
                        }
                        getContent()
                    }
                    .collect()
            }
            .invokeOnCompletion { handleCompletion(it) }
    }

    private fun getContent() {
        viewModelScope.launch {
            val files = client.getTorrentFiles(requireNotNull(hash))
            val contentTree = TransformUtil.transformFilesToTree(files, 0)

            _uiState.update { state ->
                state.copy(contentLoading = false, contentTree = contentTree)
            }
        }
    }

    private suspend fun syncPeers() {
        viewModelScope
            .launch {
                client
                    .observeTorrentPeers(requireNotNull(hash))
                    .onEach { peers ->
                        _uiState.update { state ->
                            state.copy(
                                peers = peers,
                                peersLoading = false,
                            )
                        }
                    }
                    .collect()
            }
            .invokeOnCompletion { handleCompletion(it) }
    }

    private fun handleCompletion(throwable: Throwable?) {
        Log.e(TorrentDetailsViewModel::class.simpleName, throwable.toString())
        if (throwable == null) {
            _uiState.update { state ->
                state.copy(loading = false, error = TorrentRemovedError(), peersLoading = false)
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    loading = false,
                    error = Exception(throwable.message),
                    peersLoading = false
                )
            }
        }
    }
}
