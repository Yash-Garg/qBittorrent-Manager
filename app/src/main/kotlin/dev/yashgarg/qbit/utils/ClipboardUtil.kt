package dev.yashgarg.qbit.utils

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService

object ClipboardUtil {
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

    fun copyToClipboard(
        context: Context,
        label: String,
        text: String,
        message: String = "Copied to clipboard"
    ) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
