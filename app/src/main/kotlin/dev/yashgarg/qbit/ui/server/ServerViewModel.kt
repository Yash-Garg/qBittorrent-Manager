package dev.yashgarg.qbit.ui.server

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.di.ApplicationScope
import dev.yashgarg.qbit.utils.ClientConnectionError
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient

@HiltViewModel
class ServerViewModel
@Inject
constructor(
    private val clientManager: ClientManager,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ServerState())
    val uiState = _uiState.asStateFlow()

    private lateinit var client: QBittorrentClient

    init {
        refresh()
    }

    fun refresh() {
        coroutineScope.launch {
            val clientResponse = clientManager.checkAndGetClient()
            clientResponse.fold(
                {
                    client = it
                    syncData()
                },
                { e -> emitException(e) }
            )
        }
    }

    fun addTorrent(url: String) {
        viewModelScope.launch {
            try {
                client.addTorrent { urls.add(url) }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.toString())
            }
        }
    }

    fun addFile(bytes: ByteArray) {
        viewModelScope.launch {
            try {
                client.addTorrent { rawTorrents["torrent_file"] = bytes }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.toString())
            }
        }
    }

    private fun emitException(e: Exception) {
        _uiState.update { state -> state.copy(hasError = true, error = e) }
    }

    private suspend fun syncData() {
        client
            .observeMainData()
            .catch { emitException(ClientConnectionError()) }
            .collect { mainData ->
                _uiState.update { state ->
                    state.copy(
                        dataLoading = false,
                        data = mainData,
                        hasError = false,
                        error = null,
                    )
                }
            }
    }
}
