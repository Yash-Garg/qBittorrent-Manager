package dev.yashgarg.qbit.data.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import dev.yashgarg.qbit.BuildConfig
import dev.yashgarg.qbit.data.models.ServerPreferences
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object ServerPreferencesSerializer : Serializer<ServerPreferences> {
    const val SERVER_PREFS_NAME = "${BuildConfig.APPLICATION_ID}_preferences"

    override val defaultValue = ServerPreferences()

    override suspend fun readFrom(input: InputStream): ServerPreferences {
        try {
            return Json.decodeFromString(
                ServerPreferences.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read ServerPrefs", serialization)
        }
    }

    override suspend fun writeTo(t: ServerPreferences, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(ServerPreferences.serializer(), t).encodeToByteArray())
        }
    }
}
