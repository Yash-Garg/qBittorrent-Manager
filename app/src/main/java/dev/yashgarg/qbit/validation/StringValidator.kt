package dev.yashgarg.qbit.validation

class StringValidator : TextValidator {
    override fun isValid(text: String): Boolean {
        return text.isNotEmpty()
    }
}
