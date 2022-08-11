package dev.yashgarg.qbit.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HostValidatorTest {
    private val hostValidator = HostValidator()

    @Test
    fun testCorrectIpisValid() {
        assertTrue(hostValidator.isValid("192.168.1.1"))
    }

    @Test
    fun testCorrectHostIsValid() {
        assertTrue(hostValidator.isValid("qbit.yashgarg.dev"))
    }

    @Test
    fun testIncorrectIpIsInvalid() {
        assertFalse(hostValidator.isValid("192.168.*"))
    }

    @Test
    fun testIncorrectHostIsInvalid() {
        assertFalse(hostValidator.isValid("qbit.#*example"))
    }
}
