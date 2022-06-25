package dev.yashgarg.qbit.validation

import java.util.regex.Pattern

class PortValidator : TextValidator {
    override fun isValid(text: String): Boolean {
        val portMatcher = portRegex.matcher(text)
        return portMatcher.matches()
    }

    companion object {
        val portRegex: Pattern =
            Pattern.compile(
                "^((6553[0-5])|(655[0-2]\\d)|(65[0-4]\\d{2})|(6[0-4]\\d{3})|([1-5]\\d{4})|([0-5]{0,5})|(\\d{1,4}))\$"
            )
    }
}
