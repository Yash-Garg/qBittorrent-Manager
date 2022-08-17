package dev.yashgarg.qbit.utils

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService

object TextUtil {
    fun getClipboardText(context: Context): String {
        var pasteData = ""
        val clipboard = getSystemService(context, ClipboardManager::class.java)
        if (
            clipboard?.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ==
                true
        ) {
            pasteData = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        }
        return pasteData
    }
}
