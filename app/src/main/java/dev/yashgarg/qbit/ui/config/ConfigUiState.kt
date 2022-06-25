package dev.yashgarg.qbit.ui.config

data class ConfigUiState(
    val isServerUrlValid: Boolean = false,
    val isPortValid: Boolean = false,
    val isUsernameValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val showUrlError: Boolean = false,
    val showPortError: Boolean = false,
    val showUsernameError: Boolean = false,
    val showPasswordError: Boolean = false,
    val isSaveEnabled: Boolean = false
)
