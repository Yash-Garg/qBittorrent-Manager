package dev.yashgarg.qbit.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PortValidatorTest {
    private val portValidator = PortValidator()

    @Test
    fun testPortInRangeIsValid() {
        assertTrue(portValidator.isValid("8080"))
    }

    @Test
    fun testPortOutOfRangeIsInvalid() {
        assertFalse(portValidator.isValid("79590"))
    }

    @Test
    fun testPortCharacterIsInvalid() {
        assertFalse(portValidator.isValid("808L"))
    }
}
