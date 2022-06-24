package dev.yashgarg.qbit.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.database.AppDatabase
import dev.yashgarg.qbit.models.ServerConfig
import dev.yashgarg.qbit.validation.HostValidator
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ConfigViewModel @Inject constructor(private val db: AppDatabase) : ViewModel() {
    private val hostValidator = HostValidator()

    private val _uiState = MutableStateFlow(ConfigUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ConfigUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun validateHostUrl(url: String) {
        if (url.isEmpty()) {
            _uiState.update { state -> state.copy(isServerUrlValid = false, showUrlError = false) }
            return
        }

        val isValid = hostValidator.isValid(url)
        _uiState.update { state -> state.copy(isServerUrlValid = true, showUrlError = !isValid) }
    }

    fun insert(config: ServerConfig) {
        viewModelScope.launch { db.configDao().addConfig(config) }
    }
}
