package dev.yashgarg.qbit.validation

import java.util.regex.Pattern

class HostValidator : TextValidator {
    override fun isValid(text: String): Boolean {
        val ipMatcher = ipRegex.matcher(text)
        val hostMatcher = hostnameRegex.matcher(text)
        return hostMatcher.matches() || ipMatcher.matches()
    }

    companion object {
        val ipRegex: Pattern =
            Pattern.compile(
                "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$"
            )
        val hostnameRegex: Pattern =
            Pattern.compile(
                "^(([a-zA-Z\\d]|[a-zA-Z\\d][a-zA-Z\\d\\-]*[a-zA-Z\\d])\\.)*([A-Za-z\\d]|[A-Za-z\\d][A-Za-z\\d\\-]*[A-Za-z\\d])$"
            )
    }
}
