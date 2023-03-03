package qbittorrent.internal

import java.io.File

internal actual object FileReader {
    actual fun contentOrNull(filePath: String): ByteArray? {
        val actualFilePath =
            if (filePath.startsWith("~/")) {
                filePath.replaceFirst("~", System.getProperty("user.home").ifBlank { "~" })
            } else {
                filePath
            }
        val file = File(actualFilePath)
        return if (file.exists()) {
            try {
                file.readBytes()
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }
    }
}
