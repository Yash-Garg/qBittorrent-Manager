package dev.yashgarg.qbit.utils

import java.time.ZoneId
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberFormatTest {
    private val bytes = 1602083870L
    private val timeInSeconds = 136L
    private val dateInMsEpoch = 1659354647L
    private val zoneId = ZoneId.of("GMT+05:30")

    @Test
    fun testCorrectSizeIsValid() {
        assertTrue(bytes.toHumanReadable() == "1.5 GiB")
    }

    @Test
    fun testIncorrectSizeIsInvalid() {
        assertFalse(bytes.toHumanReadable() == "1.1 GiB")
    }

    @Test
    fun testMillisCorrectDateIsValid() {
        assertTrue(dateInMsEpoch.toDate(zoneId) == "01/08/2022, 17:20:47")
    }

    @Test
    fun testMillisIncorrectDateIsInvalid() {
        assertFalse(dateInMsEpoch.toDate(zoneId) == "12/08/2021, 12:30")
    }

    @Test
    fun testCorrectTimeIsValid() {
        assertTrue(timeInSeconds.toTime() == "2m 16s")
    }

    @Test
    fun testIncorrectTimeIsInvalid() {
        assertFalse(timeInSeconds.toTime() == "5m 20s")
    }
}
