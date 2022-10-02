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
import java.net.UnknownHostException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient
import qbittorrent.QBittorrentException

@HiltViewModel
class ServerViewModel
@Inject
constructor(
    private val clientManager: ClientManager,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ServerState())
    val uiState = _uiState.asStateFlow()

    private val _status = MutableSharedFlow<String>()
    val status = _status.asSharedFlow()

    private lateinit var client: QBittorrentClient
    private var syncJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        syncJob?.cancel()
        syncJob =
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
                is Ok -> _status.emit("Successfully added torrent")
                is Err -> _status.emit(result.error.message ?: "Failed to add torrent url")
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
                is Ok -> _status.emit("Successfully added file")
                is Err -> _status.emit(result.error.message ?: "Failed to add file")
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
            .retryWhen { cause, attempt ->
                if ((cause as QBittorrentException).cause is UnknownHostException) {
                    emitException(cause)
                    println("Retrying $attempt")
                    delay(1000)
                    true
                } else false
            }
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
