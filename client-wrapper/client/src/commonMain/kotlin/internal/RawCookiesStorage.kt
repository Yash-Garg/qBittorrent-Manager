package qbittorrent.internal

import io.ktor.client.plugins.cookies.*
import io.ktor.http.*

/**
 * Work around for Ktor improperly encoding `SID` cookie values, causing authentication to loop
 * until a alphanumeric value is created.
 */
internal class RawCookiesStorage(private val cookiesStorage: CookiesStorage) :
    CookiesStorage by cookiesStorage {

    override suspend fun get(requestUrl: Url): List<Cookie> {
        return cookiesStorage.get(requestUrl).map { cookie ->
            cookie.copy(encoding = CookieEncoding.RAW)
        }
    }
}
