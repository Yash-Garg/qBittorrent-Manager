package dev.yashgarg.qbit.ui.server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.runCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.QbitRepository
import dev.yashgarg.qbit.utils.ExceptionHandler
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ServerViewModel @Inject constructor(private val repository: QbitRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ServerState())
    val uiState = _uiState.asStateFlow()

    private val _status = MutableSharedFlow<String>()
    val status = _status.asSharedFlow()

    private val _intent = MutableSharedFlow<Unit>()
    val intent = _intent.asSharedFlow()

    private var syncJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch { syncData() }
    }

    fun addTorrentUrl(url: String) {
        viewModelScope.launch {
            when (val result = repository.addTorrentUrl(url)) {
                is Ok -> _status.emit("Successfully added torrent")
                is Err -> _status.emit(result.error.message ?: "Failed to add torrent url")
            }
        }
    }

    fun addTorrentFile(bytes: ByteArray) {
        viewModelScope.launch {
            when (val result = repository.addTorrentFile(bytes)) {
                is Ok -> _status.emit("Successfully added file")
                is Err -> _status.emit(result.error.message ?: "Failed to add file")
            }
        }
    }

    fun removeTorrents(hashes: List<String>, deleteFiles: Boolean = false) {
        viewModelScope.launch {
            when (val result = repository.removeTorrents(hashes, deleteFiles)) {
                is Ok -> _status.emit("Successfully deleted ${hashes.size} file(s)")
                is Err -> _status.emit(result.error.message ?: "Failed to remove")
            }
        }
    }

    fun toggleTorrentsState(pause: Boolean, hashes: List<String>) {
        viewModelScope.launch {
            when (val result = repository.toggleTorrentsState(hashes, pause)) {
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
            when (val result = repository.toggleSpeedLimitsMode()) {
                is Ok -> getSpeedLimitMode(true)
                is Err -> _status.emit(result.error.message ?: "Failed to toggle speed limits")
            }
        }
    }

    private fun getSpeedLimitMode(showToast: Boolean = false) {
        viewModelScope.launch {
            when (val result = repository.getSpeedLimitMode()) {
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

    private suspend fun syncData() {
        getSpeedLimitMode()
        val result = runCatching {
            repository
                .observeMainData()
                .catch { e ->
                    val error = ExceptionHandler.mapException(e)
                    _uiState.update { state ->
                        state.copy(hasError = true, error = error, data = null)
                    }
                }
                .collectLatest { mainData ->
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

        when (result) {
            is Ok -> Unit
            is Err -> _status.emit(result.error.message ?: "Failed to sync data")
        }
    }
}
