package dev.yashgarg.qbit.utils

object CountryFlags {
    private val A = getEmojiByUnicode(0x1F1E6)
    private val B = getEmojiByUnicode(0x1F1E7)
    private val C = getEmojiByUnicode(0x1F1E8)
    private val D = getEmojiByUnicode(0x1F1E9)
    private val E = getEmojiByUnicode(0x1F1EA)
    private val F = getEmojiByUnicode(0x1F1EB)
    private val G = getEmojiByUnicode(0x1F1EC)
    private val H = getEmojiByUnicode(0x1F1ED)
    private val I = getEmojiByUnicode(0x1F1EE)
    private val J = getEmojiByUnicode(0x1F1EF)
    private val K = getEmojiByUnicode(0x1F1F0)
    private val L = getEmojiByUnicode(0x1F1F1)
    private val M = getEmojiByUnicode(0x1F1F2)
    private val N = getEmojiByUnicode(0x1F1F3)
    private val O = getEmojiByUnicode(0x1F1F4)
    private val P = getEmojiByUnicode(0x1F1F5)
    private val Q = getEmojiByUnicode(0x1F1F6)
    private val R = getEmojiByUnicode(0x1F1F7)
    private val S = getEmojiByUnicode(0x1F1F8)
    private val T = getEmojiByUnicode(0x1F1F9)
    private val U = getEmojiByUnicode(0x1F1FA)
    private val V = getEmojiByUnicode(0x1F1FB)
    private val W = getEmojiByUnicode(0x1F1FC)
    private val X = getEmojiByUnicode(0x1F1FD)
    private val Y = getEmojiByUnicode(0x1F1FE)
    private val Z = getEmojiByUnicode(0x1F1FF)

    private fun getCodeByCharacter(character: Char): String {
        return when (character.uppercaseChar()) {
            'A' -> A
            'B' -> B
            'C' -> C
            'D' -> D
            'E' -> E
            'F' -> F
            'G' -> G
            'H' -> H
            'I' -> I
            'J' -> J
            'K' -> K
            'L' -> L
            'M' -> M
            'N' -> N
            'O' -> O
            'P' -> P
            'Q' -> Q
            'R' -> R
            'S' -> S
            'T' -> T
            'U' -> U
            'V' -> V
            'W' -> W
            'X' -> X
            'Y' -> Y
            'Z' -> Z
            else -> ""
        }
    }

    private fun getEmojiByUnicode(unicode: Int) = String(Character.toChars(unicode))

    fun getCountryFlagByCountryCode(countryCode: String): String {
        return if (countryCode.length == 2) {
            getCodeByCharacter(countryCode.first()) + getCodeByCharacter(countryCode.last())
        } else countryCode
    }
}
