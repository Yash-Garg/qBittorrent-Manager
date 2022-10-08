package dev.yashgarg.qbit.data.manager

import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.runCatching
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.data.models.ConfigStatus
import dev.yashgarg.qbit.di.ApplicationScope
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
                        "${
                            config.connectionType.toString().lowercase()
                        }://${config.baseUrl}" +
                            if (config.port != 443) ":${config.port}" else "",
                        config.username,
                        config.password,
                        syncInterval = ClientManager.syncInterval,
                        httpClient = ClientManager.httpClient,
                        dispatcher = Dispatchers.Default,
                    )
            }
            return@withContext requireNotNull(client)
        }
}

interface ClientManager {
    val configStatus: SharedFlow<ConfigStatus>
    suspend fun checkAndGetClient(): QBittorrentClient?

    companion object {
        const val tag = "ClientManager"
        val syncInterval = 1.seconds
        val httpClient = HttpClient {
            install(HttpTimeout) { connectTimeoutMillis = 3000 }
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            Log.i("QbitClient", message)
                        }
                    }
                level = LogLevel.NONE
            }
        }
    }
}
