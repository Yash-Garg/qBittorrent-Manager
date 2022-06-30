package dev.yashgarg.qbit.ui.version

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.di.ApplicationScope
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient

@HiltViewModel
class VersionViewModel
@Inject
constructor(
    private val clientManager: ClientManager,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VersionState())
    val uiState = _uiState.asStateFlow()

    private lateinit var client: QBittorrentClient

    init {
        coroutineScope.launch {
            client = clientManager.getClient()
            fetchInfo()
        }
    }

    private suspend fun fetchInfo() {
        getVersions()
        getBuildInfo()
    }

    private suspend fun getBuildInfo() {
        _uiState.update { state ->
            state.copy(
                buildInfo = client.getBuildInfo(),
                buildInfoLoading = false,
            )
        }
    }

    private suspend fun getVersions() {
        _uiState.update { state ->
            state.copy(
                apiVersion = client.getApiVersion(),
                appVersion = client.getVersion(),
                apiVersionLoading = false,
                appVersionLoading = false
            )
        }
    }
}
