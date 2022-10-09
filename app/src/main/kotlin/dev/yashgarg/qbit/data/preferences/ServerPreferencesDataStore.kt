package dev.yashgarg.qbit.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import dev.yashgarg.qbit.BuildConfig
import dev.yashgarg.qbit.data.models.ServerPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val SERVER_PREFS_NAME = "${BuildConfig.APPLICATION_ID}_preferences"

val Context.serverPreferencesStore: DataStore<ServerPreferences> by
    dataStore(
        fileName = SERVER_PREFS_NAME,
        serializer = ServerPreferencesSerializer,
        corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { ServerPreferences() }),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    )
