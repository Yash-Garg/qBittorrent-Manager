package dev.yashgarg.qbit.data.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep @Serializable data class ServerPreferences(val showNotification: Boolean = false)
