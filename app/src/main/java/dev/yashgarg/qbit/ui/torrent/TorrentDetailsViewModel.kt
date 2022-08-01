package dev.yashgarg.qbit.ui.torrent

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
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
            .catch { Log.e(this::class.java.simpleName, it.toString()) }
            .collect { info ->
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
    }
}
