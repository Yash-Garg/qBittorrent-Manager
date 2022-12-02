package dev.yashgarg.qbit.ui.config

data class ConfigState(
    val isServerNameValid: Boolean = false,
    val isServerUrlValid: Boolean = false,
    val isPortValid: Boolean = false,
    val isUsernameValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isConnectionTypeValid: Boolean = false,
    val showServerNameError: Boolean = false,
    val showUrlError: Boolean = false,
    val showPortError: Boolean = false,
    val showUsernameError: Boolean = false,
    val showPasswordError: Boolean = false,
    val showConnectionTypeError: Boolean = false,
    val isTrustSelfSignedChecked: Boolean = false
)
