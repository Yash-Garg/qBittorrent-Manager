package dev.yashgarg.qbit.ui.server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.runCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.di.ApplicationScope
import dev.yashgarg.qbit.utils.ClientConnectionError
import dev.yashgarg.qbit.utils.ExceptionHandler
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
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
            val clientResult = clientManager.checkAndGetClient()
            if (clientResult != null) {
                client = clientResult
                syncData()
            } else {
                emitException(ClientConnectionError())
            }
        }
    }

    fun addTorrentUrl(url: String) {
        viewModelScope.launch {
            when (val result = runCatching { client.addTorrent { urls.add(url) } }) {
                is Ok -> return@launch
                is Err -> emitException(result.error)
            }
        }
    }

    fun addTorrentFile(bytes: ByteArray) {
        viewModelScope.launch {
            when (
                val result = runCatching {
                    client.addTorrent { rawTorrents["torrent_file"] = bytes }
                }
            ) {
                is Ok -> return@launch
                is Err -> emitException(result.error)
            }
        }
    }

    private fun emitException(e: Throwable) {
        val error = ExceptionHandler.mapException(e)
        _uiState.update { state -> state.copy(hasError = true, error = error) }
    }

    private suspend fun syncData() {
        client
            .observeMainData()
            .catch { e -> emitException(e) }
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
