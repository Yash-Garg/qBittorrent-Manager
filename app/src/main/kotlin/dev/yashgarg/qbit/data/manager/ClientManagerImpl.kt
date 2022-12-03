package dev.yashgarg.qbit.data.manager

import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.runCatching
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.data.models.ConfigStatus
import dev.yashgarg.qbit.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import qbittorrent.QBittorrentClient

@Singleton
class ClientManagerImpl
@Inject
constructor(
    private val configDao: ConfigDao,
    @ApplicationScope coroutineScope: CoroutineScope,
) : ClientManager {
    private val _configStatus = MutableSharedFlow<ConfigStatus>(replay = 1)
    override val configStatus = _configStatus.asSharedFlow()

    private var client: QBittorrentClient? = null

    init {
        coroutineScope.launch { checkIfConfigsExist() }
    }

    private suspend fun checkIfConfigsExist() {
        configDao.getConfigs().collect { configs ->
            if (configs.isNotEmpty()) {
                _configStatus.emit(ConfigStatus.EXISTS)
                checkAndGetClient()
            } else {
                _configStatus.emit(ConfigStatus.DOES_NOT_EXIST)
            }
        }
    }

    override suspend fun checkAndGetClient(): QBittorrentClient? {
        return when (val result = runCatching { getClient() }) {
            is Ok -> {
                this.client = result.value
                client
            }
            is Err -> {
                Log.e(this::class.simpleName, result.error.toString())
                null
            }
        }
    }

    private suspend fun getClient(): QBittorrentClient =
        withContext(Dispatchers.IO) {
            if (client == null) {
                val config = configDao.getConfigAtIndex()!!
                client =
                    QBittorrentClient(
                        "${config.connectionType.toString().lowercase()}://${config.baseUrl}:${config.port}",
                        config.username,
                        config.password,
                        syncInterval = ClientManager.syncInterval,
                        httpClient = ClientManager.httpClient(config.trustSelfSigned),
                        dispatcher = Dispatchers.Default,
                    )
            }
            return@withContext requireNotNull(client)
        }
}
