package dev.yashgarg.qbit.ui.torrent

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.runCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.QbitRepository
import dev.yashgarg.qbit.utils.TransformUtil
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qbittorrent.models.TorrentPeer

@HiltViewModel
class TorrentDetailsViewModel
@Inject
constructor(private val repository: QbitRepository, state: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(TorrentDetailsState())
    val uiState = _uiState.asStateFlow()

    private val _status = MutableSharedFlow<String>()
    val status = _status.asSharedFlow()

    private val hash by lazy { state.get<String>("torrentHash") }

    init {
        Log.d("TorrentDetailsViewModel", "TorrentHash: $hash")
        viewModelScope.launch {
            launch { syncTorrentFlow() }
            launch { syncPeers() }
        }
    }

    fun toggleTorrent(pause: Boolean, hash: String) {
        val hashes = listOf(hash)
        viewModelScope.launch {
            when (val result = repository.toggleTorrentsState(hashes, pause)) {
                is Ok -> _status.emit("${if (pause) "Paused" else "Resumed"} $hash")
                is Err ->
                    _status.emit(
                        result.error.message
                            ?: "Failed to ${if (pause) "pause" else "resume"} $hash"
                    )
            }
        }
    }

    fun removeTorrent(hash: String, deleteFiles: Boolean = false) {
        viewModelScope.launch {
            when (val result = repository.removeTorrents(listOf(hash), deleteFiles)) {
                is Ok -> return@launch
                is Err -> _status.emit(result.error.message ?: "Failed to remove $hash")
            }
        }
    }

    fun forceRecheck(hash: String) {
        viewModelScope.launch {
            when (val result = repository.recheckTorrents(listOf(hash))) {
                is Ok -> _status.emit("Rechecking $hash")
                is Err -> _status.emit(result.error.message ?: "Failed to recheck torrent")
            }
        }
    }

    fun forceReannounce(hash: String) {
        viewModelScope.launch {
            when (val result = repository.reannounceTorrents(listOf(hash))) {
                is Ok -> _status.emit("Reannouncing $hash")
                is Err -> _status.emit(result.error.message ?: "Failed to reannounce torrent")
            }
        }
    }

    fun renameTorrent(torrentName: String, torrentHash: String) {
        viewModelScope.launch {
            when (val result = repository.renameTorrent(torrentHash, torrentName)) {
                is Ok -> _status.emit("Successfully renamed $torrentHash")
                is Err -> _status.emit(result.error.message ?: "Failed to rename torrent")
            }
        }
    }

    fun banPeer(peer: TorrentPeer) {
        val peerAddr = "${peer.ip}:${peer.port}"

        viewModelScope.launch {
            when (val result = repository.banPeers(listOf(peerAddr))) {
                is Ok -> _status.emit("Successfully banned $peerAddr")
                is Err -> _status.emit(result.error.message ?: "Failed to ban peer")
            }
        }
    }

    private suspend fun syncTorrentFlow() {
        val hash = requireNotNull(hash)
        val result = runCatching {
            repository.observeTorrent(hash, false).collectLatest { info ->
                val props = repository.getTorrentProperties(hash)
                val trackers = repository.getTorrentTrackers(hash)

                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        torrent = info,
                        error = null,
                        trackers =
                            when (trackers) {
                                is Ok -> trackers.value
                                is Err -> emptyList()
                            },
                        torrentProperties =
                            when (props) {
                                is Ok -> props.value
                                is Err -> null
                            }
                    )
                }
                getContent()
            }
        }

        when (result) {
            is Ok -> Unit
            is Err -> {
                _uiState.update { state ->
                    state.copy(loading = false, error = Exception(result.error.message))
                }
            }
        }
    }

    private suspend fun getContent() {
        val files = hash?.let { repository.getTorrentFiles(it) }
        if (files != null) {
            when (files) {
                is Ok -> {
                    val tree = TransformUtil.transformFilesToTree(files.value, 0)
                    _uiState.update { state -> state.copy(contentTree = tree) }
                }
                is Err -> {
                    _uiState.update { state -> state.copy(contentTree = emptyList()) }
                }
            }
        }

        _uiState.update { state -> state.copy(contentLoading = false) }
    }

    private suspend fun syncPeers() {
        val result = runCatching {
            repository
                .observeTorrentPeers(requireNotNull(hash))
                .catch {
                    _uiState.update { state ->
                        state.copy(peersLoading = false, error = Exception(it.message))
                    }
                }
                .collectLatest { peers ->
                    _uiState.update { state ->
                        state.copy(
                            peers = peers,
                            peersLoading = false,
                        )
                    }
                }
        }

        when (result) {
            is Ok -> Unit
            is Err ->
                _uiState.update { state ->
                    state.copy(peersLoading = false, error = Exception(result.error.message))
                }
        }
    }
}
