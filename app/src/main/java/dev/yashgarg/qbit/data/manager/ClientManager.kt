package dev.yashgarg.qbit.data.manager

import android.util.Log
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.di.ApplicationScope
import io.ktor.client.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import qbittorrent.QBittorrentClient

enum class ConfigStatus {
    EXISTS,
    DOES_NOT_EXIST
}

@Singleton
class ClientManager
@Inject
constructor(
    private val configDao: ConfigDao,
    @ApplicationScope coroutineScope: CoroutineScope,
) {
    private val _configStatus = MutableSharedFlow<ConfigStatus>()
    val configStatus = _configStatus.asSharedFlow()

    private var client: QBittorrentClient? = null

    init {
        coroutineScope.launch { checkIfConfigsExist() }
    }

    private suspend fun checkIfConfigsExist() {
        configDao.getConfigs().collect { configs ->
            if (configs.isNotEmpty()) {
                _configStatus.emit(ConfigStatus.EXISTS)
                getClient()
            } else {
                _configStatus.emit(ConfigStatus.DOES_NOT_EXIST)
            }
        }
    }

    suspend fun getClient(): QBittorrentClient =
        withContext(Dispatchers.IO) {
            if (client == null) {
                val config = configDao.getConfigAtIndex()!!
                client =
                    QBittorrentClient(
                        "${config.connectionType.toString().lowercase()}://${config.baseUrl}:${config.port}",
                        config.username,
                        config.password,
                        mainDataSyncMs = 1000L,
                        httpClient = HttpClient(),
                        dispatcher = Dispatchers.Default,
                    )
                Log.i(tag, "Client App Version - ${client!!.getVersion()}")
            }
            return@withContext requireNotNull(client)
        }

    companion object {
        const val tag = "qbit-client"
    }
}
