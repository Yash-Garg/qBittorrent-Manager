package dev.yashgarg.qbit.validation

class LinkValidator : TextValidator {
    override fun isValid(text: String): Boolean {
        return text.startsWith("http://") ||
            text.startsWith("https://") ||
            text.startsWith("magnet:?xt=urn:") ||
            text.startsWith("bc://bt/")
    }
}
