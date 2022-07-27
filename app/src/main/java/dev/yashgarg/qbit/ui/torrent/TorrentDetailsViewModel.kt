package dev.yashgarg.qbit.ui.torrent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        CoroutineScope(viewModelScope.coroutineContext).launch {
            val clientResponse = clientManager.checkAndGetClient()
            clientResponse.fold(
                {
                    client = it
                    syncTorrentFlow()
                },
                { e -> println(e.message) }
            )
        }
    }

    private suspend fun syncTorrentFlow() {
        client.torrentFlow(requireNotNull(hash)).collect { info ->
            _uiState.update { state -> state.copy(loading = false, torrent = info) }
        }
    }
}
