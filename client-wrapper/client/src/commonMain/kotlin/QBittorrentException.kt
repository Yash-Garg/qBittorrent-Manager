package qbittorrent

import io.ktor.client.statement.*

/**
 * A generic exception thrown by every API method in [QBittorrentClient], simplifying error
 * handling.
 *
 * If a [response] is set, the error occurred because the server responded with an error. If no
 * [response] is provided, the [cause] will contain an exception that was produced before executing
 * the request.
 */
class QBittorrentException : Exception {

    private var body: String? = null
    var response: HttpResponse? = null
        private set

    constructor(
        response: HttpResponse,
        body: String,
    ) : super() {
        this.response = response
        this.body = body
    }

    constructor(cause: Throwable) : super(cause)

    override val message: String
        get() =
            if (response == null) {
                super.message ?: "<no message>"
            } else {
                body?.ifBlank { "${response?.status?.value}: <no message>" }.orEmpty()
            }
}
