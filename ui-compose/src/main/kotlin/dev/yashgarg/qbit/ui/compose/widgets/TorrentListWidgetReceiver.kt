package dev.yashgarg.qbit.ui.compose.widgets

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TorrentListWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TorrentListWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.i(this::class.simpleName, "onReceive() -> $intent | ${intent.data} ")
    }
}
