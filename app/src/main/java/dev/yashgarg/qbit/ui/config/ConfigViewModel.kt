package dev.yashgarg.qbit.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.database.AppDatabase
import dev.yashgarg.qbit.models.ServerConfig
import dev.yashgarg.qbit.validation.HostValidator
import dev.yashgarg.qbit.validation.PortValidator
import dev.yashgarg.qbit.validation.StringValidator
import io.ktor.client.*
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient

@HiltViewModel
class ConfigViewModel @Inject constructor(private val db: AppDatabase) : ViewModel() {
    private val hostValidator = HostValidator()
    private val portValidator = PortValidator()
    private val textValidator = StringValidator()

    private val _uiState = MutableStateFlow(ConfigUiState())
    val uiState = _uiState.asStateFlow()

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }

    fun validateHostUrl(url: String) {
        if (url.isEmpty()) {
            _uiState.update { state -> state.copy(isServerUrlValid = false, showUrlError = false) }
            return
        }

        val isValid = hostValidator.isValid(url)
        _uiState.update { state -> state.copy(isServerUrlValid = true, showUrlError = !isValid) }
    }

    fun validatePort(port: String) {
        if (port.isEmpty()) {
            _uiState.update { state -> state.copy(isPortValid = false, showPortError = false) }
            return
        }

        val isValid = portValidator.isValid(port)
        _uiState.update { state -> state.copy(isPortValid = true, showPortError = !isValid) }
    }

    fun validateUsername(user: String) {
        if (user.isEmpty()) {
            _uiState.update { state ->
                state.copy(isUsernameValid = false, showUsernameError = false)
            }
            return
        }

        val isValid = textValidator.isValid(user)
        _uiState.update { state ->
            state.copy(isUsernameValid = true, showUsernameError = !isValid)
        }
    }

    fun validatePassword(text: String) {
        if (text.isEmpty()) {
            _uiState.update { state ->
                state.copy(isPasswordValid = false, showPasswordError = false)
            }
            return
        }

        val isValid = textValidator.isValid(text)
        _uiState.update { state ->
            state.copy(isPasswordValid = true, showPasswordError = !isValid)
        }
    }

    fun validateName(name: String) {
        if (name.isEmpty()) {
            _uiState.update { state ->
                state.copy(isServerNameValid = false, showServerNameError = false)
            }
            return
        }

        val isValid = textValidator.isValid(name)
        _uiState.update { state ->
            state.copy(isServerNameValid = true, showServerNameError = !isValid)
        }
    }

    fun validateConnectionType(type: String) {
        if (type.isEmpty()) {
            _uiState.update { state ->
                state.copy(isConnectionTypeValid = false, showConnectionTypeError = false)
            }
            return
        }

        val isValid = textValidator.isValid(type)
        _uiState.update { state ->
            state.copy(isConnectionTypeValid = true, showConnectionTypeError = !isValid)
        }
    }

    fun validateForm(
        serverName: String,
        serverHost: String,
        port: String,
        connectionType: String,
        username: String,
        password: String
    ) {
        val serverNameValid = textValidator.isValid(serverName)
        val serverHostValid = hostValidator.isValid(serverHost)
        val portValid = portValidator.isValid(port)
        val usernameValid = textValidator.isValid(username)
        val passwordValid = textValidator.isValid(password)
        val connectionTypeValid = textValidator.isValid(connectionType)

        val hasError =
            listOf(
                    serverNameValid,
                    serverHostValid,
                    portValid,
                    usernameValid,
                    passwordValid,
                    connectionTypeValid,
                )
                .any { it == false }

        if (hasError) {
            _uiState.update { state ->
                state.copy(
                    isServerNameValid = serverNameValid,
                    isServerUrlValid = serverHostValid,
                    isPortValid = portValid,
                    isUsernameValid = usernameValid,
                    isPasswordValid = passwordValid,
                    isConnectionTypeValid = connectionTypeValid,
                    showServerNameError = !serverNameValid,
                    showUsernameError = !usernameValid,
                    showPasswordError = !passwordValid,
                    showPortError = !portValid,
                    showUrlError = !serverHostValid,
                    showConnectionTypeError = !connectionTypeValid,
                )
            }
        } else {
            viewModelScope.launch { validationEventChannel.send(ValidationEvent.Success) }
        }
    }

    fun insert(config: ServerConfig) {
        viewModelScope.launch { db.configDao().addConfig(config) }
    }

    suspend fun testConfig(baseUrl: String, username: String, password: String): String {
        val client =
            QBittorrentClient(
                baseUrl,
                username,
                password,
                httpClient = HttpClient(),
                dispatcher = Dispatchers.Default
            )

        return client.getVersion()
    }
}
