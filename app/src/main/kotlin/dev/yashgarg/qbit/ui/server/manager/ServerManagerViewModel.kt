package dev.yashgarg.qbit.ui.server.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.daos.ConfigDao
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ServerManagerViewModel @Inject constructor(private val configDao: ConfigDao) : ViewModel() {
    private val _uiState = MutableStateFlow(ServerManagerState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadConfigs() }
    }

    private suspend fun loadConfigs() {
        val configs = configDao.getConfigs()
        configs
            .catch { _uiState.update { it.copy(error = true, configsLoading = false) } }
            .collectLatest {
                _uiState.update { state -> state.copy(configs = it, configsLoading = false) }
            }
    }
}
