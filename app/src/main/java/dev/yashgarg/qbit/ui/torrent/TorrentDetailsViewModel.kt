package dev.yashgarg.qbit.ui.torrent

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.utils.TorrentRemovedError
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
                },
                { e -> Log.e(this::class.java.simpleName, e.toString()) }
            )
        }
    }

    private suspend fun syncTorrentFlow() {
        val hash = requireNotNull(hash)
        client
            .observeTorrent(hash, waitIfMissing = false)
            .onEach { info ->
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        torrent = info,
                        torrentFiles = client.getTorrentFiles(hash),
                        trackers = client.getTrackers(hash) ?: emptyList(),
                        torrentProperties = client.getTorrentProperties(hash)
                    )
                }
            }
            .onCompletion { throwable ->
                Log.e(this::class.java.simpleName, throwable.toString())
                if (throwable == null) {
                    _uiState.update { state ->
                        state.copy(loading = false, error = TorrentRemovedError())
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(loading = false, error = Exception(throwable.message))
                    }
                }
            }
            .collect()
    }
}
