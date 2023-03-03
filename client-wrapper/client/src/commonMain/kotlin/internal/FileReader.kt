package qbittorrent.internal

/**
 * A simple utility to create a [ByteArray] of the contents of a given file path, if anything goes
 * wrong null is returned.
 */
internal expect object FileReader {

    fun contentOrNull(filePath: String): ByteArray?
}
