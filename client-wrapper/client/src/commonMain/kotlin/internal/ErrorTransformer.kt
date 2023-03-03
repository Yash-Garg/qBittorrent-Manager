package qbittorrent.internal

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import io.ktor.utils.io.*

/**
 * A Ktor client plugin which forwards errors that occur before requests execute with a dummy
 * [HttpResponse].
 *
 * The call attributes will contain a [KEY_INTERNAL_ERROR] containing the exception which can be
 * wrapped and rethrown for consistent handling.
 */
@OptIn(InternalAPI::class)
internal object ErrorTransformer : HttpClientPlugin<ErrorTransformer, ErrorTransformer> {

    val KEY_INTERNAL_ERROR = AttributeKey<Throwable>("INTERNAL_ERROR")

    override val key: AttributeKey<ErrorTransformer> = AttributeKey("ErrorTransformer")

    override fun prepare(block: ErrorTransformer.() -> Unit): ErrorTransformer = this

    override fun install(plugin: ErrorTransformer, scope: HttpClient) {
        scope.requestPipeline.intercept(HttpRequestPipeline.State) {
            try {
                proceed()
            } catch (e: Throwable) {
                val responseData =
                    HttpResponseData(
                        statusCode = HttpStatusCode(-1, ""),
                        requestTime = GMTDate(),
                        body = ByteReadChannel(byteArrayOf()),
                        callContext = context.executionContext,
                        headers = Headers.Empty,
                        version = HttpProtocolVersion.HTTP_1_0,
                    )
                context.attributes.put(KEY_INTERNAL_ERROR, e)
                subject = HttpClientCall(scope, context.build(), responseData)
                proceed()
            }
        }
    }
}
