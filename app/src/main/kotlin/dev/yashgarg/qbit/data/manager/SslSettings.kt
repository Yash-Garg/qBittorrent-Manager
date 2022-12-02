package dev.yashgarg.qbit.data.manager

import android.annotation.SuppressLint
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object SslSettings {
    fun getSslContext(): SSLContext? {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(TrustAllX509TrustManager()), SecureRandom())
        return sslContext
    }

    fun getTrustManager() = TrustAllX509TrustManager()
}

@SuppressLint("TrustAllX509TrustManager", "CustomX509TrustManager")
class TrustAllX509TrustManager : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)

    override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}

    override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
}
