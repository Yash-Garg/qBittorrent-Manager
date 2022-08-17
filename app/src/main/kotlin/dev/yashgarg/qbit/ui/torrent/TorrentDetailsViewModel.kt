package dev.yashgarg.qbit.ui.torrent

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.utils.TorrentRemovedError
import dev.yashgarg.qbit.utils.TransformUtil
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient

@HiltViewModel
class TorrentDetailsViewModel
@Inject
constructor(private val clientManager: ClientManager, state: SavedStateHandle) : ViewModel() {
    private lateinit var client: QBittorrentClient
    private val _uiState = MutableStateFlow(TorrentDetailsState())
    val uiState = _uiState.asStateFlow()

    private val hash by lazy { state.get<String>("torrentHash") }

    init {
        viewModelScope.launch {
            val clientResponse = clientManager.checkAndGetClient()
            clientResponse.fold(
                {
                    client = it
                    syncTorrentFlow()
                    syncPeers()
                },
                { e -> Log.e(this::class.java.simpleName, e.toString()) }
            )
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
