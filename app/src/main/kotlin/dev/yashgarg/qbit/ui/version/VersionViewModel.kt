package dev.yashgarg.qbit.ui.version

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qbittorrent.QBittorrentClient

@HiltViewModel
class VersionViewModel @Inject constructor(private val clientManager: ClientManager) : ViewModel() {
    private val _uiState = MutableStateFlow(VersionState())
    val uiState = _uiState.asStateFlow()

    private lateinit var client: QBittorrentClient

    init {
        viewModelScope.launch {
            val clientResponse = clientManager.checkAndGetClient()
            clientResponse.fold(
                {
                    client = it
                    getVersions()
                },
                { e -> Log.e(VersionViewModel::class.simpleName, e.toString()) }
            )
        }
    }

    private suspend fun getVersions() {
        _uiState.update { state ->
            state.copy(
                apiVersion = client.getApiVersion(),
                appVersion = client.getVersion(),
                loading = false,
            )
        }
    }
}
