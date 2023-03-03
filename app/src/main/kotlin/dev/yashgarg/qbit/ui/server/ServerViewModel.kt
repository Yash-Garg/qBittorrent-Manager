package dev.yashgarg.qbit.ui.server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.runCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.utils.ClientConnectionError
import dev.yashgarg.qbit.utils.ExceptionHandler
import java.net.UnknownHostException
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient
import qbittorrent.QBittorrentException

@HiltViewModel
class ServerViewModel @Inject constructor(private val clientManager: ClientManager) : ViewModel() {
    private val _uiState = MutableStateFlow(ServerState())
    val uiState = _uiState.asStateFlow()

    private val _status = MutableSharedFlow<String>()
    val status = _status.asSharedFlow()

    private val _intent = MutableSharedFlow<Unit>()
    val intent = _intent.asSharedFlow()

    private lateinit var client: QBittorrentClient
    private var syncJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        syncJob?.cancel()
        syncJob =
            viewModelScope.launch {
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

    fun removeTorrents(hashes: List<String>, deleteFiles: Boolean = false) {
        viewModelScope.launch {
            when (val result = runCatching { client.deleteTorrents(hashes, deleteFiles) }) {
                is Ok -> _status.emit("Successfully deleted ${hashes.size} file(s)")
                is Err -> _status.emit(result.error.message ?: "Failed to remove")
            }
        }
    }

    fun toggleTorrentsState(pause: Boolean, hashes: List<String>) {
        viewModelScope.launch {
            when (
                val result = runCatching {
                    if (pause) client.pauseTorrents(hashes) else client.resumeTorrents(hashes)
                }
            ) {
                is Ok ->
                    _status.emit("${if (pause) "Paused" else "Resumed"} ${hashes.size} torrent(s)")
                is Err ->
                    _status.emit(
                        result.error.message
                            ?: "Failed to ${if (pause) "pause" else "resume"} ${hashes.size} torrent(s)"
                    )
            }
        }
    }

    fun toggleSpeedLimits() {
        viewModelScope.launch {
            when (val result = runCatching { client.toggleSpeedLimitsMode() }) {
                is Ok -> getSpeedLimitMode(true)
                is Err -> _status.emit(result.error.message ?: "Failed to toggle speed limits")
            }
        }
    }

    private fun getSpeedLimitMode(showToast: Boolean = false) {
        viewModelScope.launch {
            when (val result = runCatching { client.getSpeedLimitsMode() }) {
                is Ok -> {
                    _uiState.update { it.copy(speedLimitMode = result.value) }
                    if (showToast) {
                        _status.emit(
                            "Alternative speed limits are ${if (result.value == 0) "disabled" else "enabled"}"
                        )
                    }
                }
                is Err -> _status.emit(result.error.message ?: "Failed to get speed limit mode")
            }
        }
    }

    private fun emitException(e: Throwable) {
        val error = ExceptionHandler.mapException(e)
        _uiState.update { state -> state.copy(hasError = true, error = error, data = null) }
    }

    private suspend fun syncData() {
        getSpeedLimitMode()
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
            .catch { emitException(it) }
            .collect { mainData ->
                _uiState.update { state ->
                    state.copy(
                        dataLoading = false,
                        data = mainData,
                        hasError = false,
                        error = null,
                    )
                }
                _intent.emit(Unit)
            }
    }
}
