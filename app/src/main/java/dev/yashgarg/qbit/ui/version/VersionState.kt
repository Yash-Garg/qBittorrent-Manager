package dev.yashgarg.qbit.ui.version

import qbittorrent.models.BuildInfo

data class VersionState(
    val apiVersionLoading: Boolean = true,
    val appVersionLoading: Boolean = true,
    val buildInfoLoading: Boolean = true,
    val buildInfo: BuildInfo? = null,
    val appVersion: String = "",
    val apiVersion: String = ""
)
