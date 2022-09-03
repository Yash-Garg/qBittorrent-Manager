package dev.yashgarg.qbit.utils

import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.abs

private object NumberFormat {
    fun bytesToHumanReadable(bytes: Long): String {
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= java.lang.Long.signum(bytes).toLong()
        return String.format("%.2f %ciB", value / 1024.0, ci.current()).trim()
    }

    fun millisToDate(millis: Long, zoneId: ZoneId?): String {
        val millisEpoch = millis * 1000
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss")
        val instant = Instant.ofEpochMilli(millisEpoch)
        val date = LocalDateTime.ofInstant(instant, zoneId ?: ZoneId.systemDefault())
        return formatter.format(date).trim()
    }

    fun secondsToTime(seconds: Long): String {
        var duration = seconds
        val days: Long = TimeUnit.SECONDS.toDays(duration)
        duration -= TimeUnit.DAYS.toSeconds(days)
        val hours: Long = TimeUnit.SECONDS.toHours(duration)
        duration -= TimeUnit.HOURS.toSeconds(hours)
        val minutes: Long = TimeUnit.SECONDS.toMinutes(duration)
        duration -= TimeUnit.MINUTES.toSeconds(minutes)
        val secs: Long = TimeUnit.SECONDS.toSeconds(duration)
        val timeStr = StringBuilder()
        if (days != 0L) {
            timeStr.append("${days}d")
        }
        if (hours != 0L) {
            timeStr.append(" ${hours}h")
        }
        if (minutes != 0L) {
            timeStr.append(" ${minutes}m")
        }
        if (secs != 0L) {
            timeStr.append(" ${secs}s")
        }

        return timeStr.toString().trim()
    }
}

fun Long.toHumanReadable(): String = NumberFormat.bytesToHumanReadable(this)

fun Long.toTime(): String = NumberFormat.secondsToTime(this)

fun Long.toDate(zoneId: ZoneId? = null): String = NumberFormat.millisToDate(this, zoneId)
