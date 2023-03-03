package qbittorrent.internal

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import qbittorrent.QBittorrentException

internal suspend fun HttpResponse.orThrow() {
    if (!status.isSuccess()) {
        throw call.attributes
            .takeOrNull(ErrorTransformer.KEY_INTERNAL_ERROR)
            ?.run(::QBittorrentException)
            ?: QBittorrentException(this, bodyAsText())
    }
}

internal suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    return if (status.isSuccess()) {
        when (T::class) {
            String::class -> bodyAsText() as T
            else -> body()
        }
    } else {
        throw call.attributes
            .takeOrNull(ErrorTransformer.KEY_INTERNAL_ERROR)
            ?.run(::QBittorrentException)
            ?: QBittorrentException(this, bodyAsText())
    }
}

internal suspend fun <T> HttpResponse.bodyOrThrow(typeInfo: TypeInfo): T {
    return if (status.isSuccess()) {
        body(typeInfo)
    } else {
        throw call.attributes
            .takeOrNull(ErrorTransformer.KEY_INTERNAL_ERROR)
            ?.run(::QBittorrentException)
            ?: QBittorrentException(this, bodyAsText())
    }
}
