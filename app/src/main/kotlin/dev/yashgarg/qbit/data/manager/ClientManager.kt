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
import kotlinx.coroutines.flow.asSharedFlow
import qbittorrent.QBittorrentClient

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

    suspend fun checkAndGetClient(): QBittorrentClient? {
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
                        }://${config.baseUrl}:${config.port}",
                        config.username,
                        config.password,
                        syncInterval = syncInterval,
                        httpClient = httpClient,
                        dispatcher = Dispatchers.Default,
                    )
            }
            return@withContext requireNotNull(client)
        }

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
