package dev.yashgarg.qbit.data.manager

import android.util.Log
import arrow.core.Either
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.di.ApplicationScope
import dev.yashgarg.qbit.utils.ClientConnectionError
import io.ktor.client.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import qbittorrent.QBittorrentClient
import javax.inject.Inject
import javax.inject.Singleton

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
                checkAndGetClient()
            } else {
                _configStatus.emit(ConfigStatus.DOES_NOT_EXIST)
            }
        }
    }

    suspend fun checkAndGetClient(): Either<QBittorrentClient, Exception> {
        val client = getClient()
        return try {
            Log.i(tag, "Client App Version - ${client.getVersion()}")
            this.client = client
            Either.Left(client)
        } catch (_: Exception) {
            Either.Right(ClientConnectionError())
        }
    }

    private suspend fun getClient(): QBittorrentClient =
        withContext(Dispatchers.IO) {
            if (client == null) {
                val config = configDao.getConfigAtIndex()!!
                client =
                    QBittorrentClient(
                        "${
                            config.connectionType.toString().lowercase()
                        }://${config.baseUrl}:${config.port}",
                        config.username,
                        config.password,
                        mainDataSyncMs = 1000L,
                        httpClient = httpClient,
                        dispatcher = Dispatchers.Default,
                    )
            }
            return@withContext requireNotNull(client)
        }

    companion object {
        const val tag = "qbit-client"
        val httpClient = HttpClient { install(HttpTimeout) { connectTimeoutMillis = 3000 } }
    }
}
