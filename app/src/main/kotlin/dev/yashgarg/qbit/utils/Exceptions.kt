package dev.yashgarg.qbit.utils

import io.ktor.client.network.sockets.*
import qbittorrent.QBittorrentException

class ClientConnectionError : Throwable("Failed to connect client")

class TorrentRemovedError : Exception("Torrent has been removed")

object ExceptionHandler {
    fun mapException(ex: Throwable): Throwable {
        return when (ex) {
            is UninitializedPropertyAccessException -> ClientConnectionError()
            is QBittorrentException -> {
                return when (ex.cause) {
                    is ConnectTimeoutException -> ClientConnectionError()
                    else -> ex
                }
            }
            else -> ex
        }
    }
}
