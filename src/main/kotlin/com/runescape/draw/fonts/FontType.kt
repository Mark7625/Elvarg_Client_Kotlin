package com.runescape.draw.fonts

import com.runescape.Client

enum class FontType(val file: String) {
    SMALL("p11_full"),
    FANCY("q8_full"),
    REGULAR("p12_full"),
    BOLD("b12_full");

    lateinit var font: RSFont

    companion object {
        fun initialize() {
            values().forEach {
                it.font = RSFont(true, it.file, Client.instance.titleArchive)
            }
        }
    }
}
