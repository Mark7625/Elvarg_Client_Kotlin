package com.runescape.utils

object StringUtils {

    fun capitalize(s: String): String {
        var s = s
        for (i in s.indices) {
            if (i == 0) {
                s = String.format("%s%s", Character.toUpperCase(s[0]), s.substring(1))
            }
            if (!Character.isLetterOrDigit(s[i])) {
                if (i + 1 < s.length) {
                    s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s[i + 1]), s.substring(i + 2))
                }
            }
        }
        return s
    }
}
