package dev.yashgarg.qbit.data.manager

import dev.yashgarg.qbit.data.daos.ConfigDao
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class ConfigStatus {
    EXISTS,
    DOES_NOT_EXIST
}

class ClientManager @Inject constructor(private val configDao: ConfigDao) {
    private val _configStatus = MutableSharedFlow<ConfigStatus>()
    val configStatus = _configStatus.asSharedFlow()

    suspend fun checkIfConfigsExist() {
        configDao.getConfigs().collect { configs ->
            if (configs.isNotEmpty()) {
                _configStatus.emit(ConfigStatus.EXISTS)
            } else {
                _configStatus.emit(ConfigStatus.DOES_NOT_EXIST)
            }
        }
    }
}
