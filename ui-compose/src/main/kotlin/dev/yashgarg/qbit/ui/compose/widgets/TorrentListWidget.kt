package dev.yashgarg.qbit.ui.compose.widgets

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class TorrentListWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        Column(
            modifier =
                GlanceModifier.fillMaxSize()
                    .background(Color("#353A4E".toColorInt()))
                    .clickable(onClick = actionRunCallback<TitleClickAction>()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Test Widget with Glance (Compose)",
                style =
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ColorProvider(Color.White)
                    ),
            )
        }
    }
}

class TitleClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d(TorrentListWidget::class.simpleName, "widget onClick() callback")
    }
}
