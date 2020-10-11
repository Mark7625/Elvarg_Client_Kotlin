/*
 * Copyright (c) 2020, Mark <https://github.com/Mark7625>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.runescape.draw.fonts

import com.runescape.cache.FileArchive
import com.runescape.draw.Rasterizer2D
import com.runescape.io.Buffer
import java.awt.Color

class RSFont(typefont: Boolean, name: String, archive: FileArchive) {

    var aRSString_4135: String = "nbsp"
    var startTransparency: String = "trans="
    var startDefaultShadow: String = "shad"
    var endShadow = "/shad"
    var endEffect: String = "gt"
    var aRSString_4143: String = "100"
    var endStrikethrough = "/str"
    var aRSString_4147: String = "euro"
    var startColor: String = "col="
    var lineBreak: String = "br"
    var startStrikethrough: String = "str="
    var endColor: String = "/col"
    var startImage: String = "img="
    var startClanImage: String = "clan="
    var endUnderline: String = "/u"
    var defaultStrikethrough: String = "str"
    var startShadow: String = "shad="
    var startEffect: String = "lt"
    var aRSString_4162: String = "shy"
    var aRSString_4163: String = "copy"
    var endTransparency: String = "/trans"
    var aRSString_4165: String = "times"
    var startUnderline: String = "u="
    var startDefaultUnderline: String = "u"
    var aRSString_4169: String = "reg"
    var splitTextStrings: Array<String?> = arrayOfNulls<String>(100)
    var defaultColor = 0
    var textShadowColor = 0
    var strikethroughColor = 0
    var defaultTransparency = 256
    var anInt4175 = 0
    var underlineColor = -1
    var defaultShadow = -1
    var anInt4178 = 0
    var transparency = 256
    var textColor = 0

    var baseCharacterHeight = 0
    var anInt4142 = 0
    var anInt4144 = 0
    var characterDrawYOffsets: IntArray = IntArray(256)
    var characterHeights: IntArray = IntArray(256)
    private var characterDrawXOffsets: IntArray = IntArray(256)
    private var characterWidths: IntArray = IntArray(256)
    private var fontPixels: Array<ByteArray?> = arrayOfNulls(256)
    private var characterScreenWidths: IntArray = IntArray(256)

    init {

        val stream = Buffer(archive.readFile("$name.dat"))
        val stream_1 = Buffer(archive.readFile("index.dat"))
        stream_1.currentPosition = stream.readUShort() + 4
        val k: Int = stream_1.readUnsignedByte()
        if (k > 0) {
            stream_1.currentPosition += 3 * (k - 1)
        }
        for (l in 0..255) {
            characterDrawXOffsets[l] = stream_1.readUnsignedByte()
            characterDrawYOffsets[l] = stream_1.readUnsignedByte()
            characterWidths[l] = stream_1.readUShort()
            val i1: Int = characterWidths[l]
            characterHeights[l] = stream_1.readUShort()
            val j1: Int = characterHeights[l]
            val k1: Int = stream_1.readUnsignedByte()
            val l1 = i1 * j1
            fontPixels[l] = ByteArray(l1)
            if (k1 == 0) {
                for (i2 in 0 until l1) {
                    fontPixels[l]!![i2] = stream.readSignedByte()
                }
            } else if (k1 == 1) {
                for (j2 in 0 until i1) {
                    for (l2 in 0 until j1) {
                        fontPixels[l]!![j2 + l2 * i1] = stream.readSignedByte()
                    }
                }
            }
            if (j1 > baseCharacterHeight && l < 128) {
                baseCharacterHeight = j1
            }
            characterDrawXOffsets[l] = 1
            characterScreenWidths[l] = i1 + 2
            var k2 = 0
            for (i3 in j1 / 7 until j1) {
                k2 += fontPixels[l]!![i3 * i1]
            }
            if (k2 <= j1 / 7) {
                characterScreenWidths[l]--
                characterDrawXOffsets[l] = 0
            }
            k2 = 0
            for (j3 in j1 / 7 until j1) {
                k2 += fontPixels[l]!![i1 - 1 + j3 * i1]
            }
            if (k2 <= j1 / 7) {
                characterScreenWidths[l]--
            }
        }
        characterScreenWidths[32] = characterScreenWidths[if (typefont) 73 else 105]
    }

    private val replaceSyntax: Map<String, String> = mapOf(
        "@red@" to "<col=ff0000>",
        "@gre@" to "<col=65280>",
        "@blu@" to "<col=255>",
        "@yel@" to "<col=ffff00>",
        "@cya@" to "<col=65535>",
        "@mag@" to "<col=ff00ff>",
        "@whi@" to "<col=ffffff>",
        "@lre@" to "<col=ff9040>",
        "@dre@" to "<col=800000>",
        "@bla@" to "<col=0>",
        "@or1@" to "<col=ffb000>",
        "@or2@" to "<col=ff7000>",
        "@or3@" to "<col=ff3000>",
        "@gr1@" to "<col=c0ff00>",
        "@gr2@" to "<col=80ff00>",
        "@gr3@" to "<col=40ff00>",
        "@RED@" to "<col=ffff00>",
        "@GRE@" to "<col=65280>",
        "@BLU@" to "<col=255>",
        "@YEL@" to "<col=ff0000>",
        "@CYA@" to "<col=65535>",
        "@MAG@" to "<col=ff00ff>",
        "@WHI@" to "<col=ffffff>",
        "@LRE@" to "<col=ff9040>",
        "@DRE@" to "<col=800000>",
        "@BLA@" to "<col=0>",
        "@OR1@" to "<col=ffb000>",
        "@OR2@" to "<col=ff7000>",
        "@OR3@" to "<col=ff3000>",
        "@GR1@" to "<col=c0ff00>",
        "@GR2@" to "<col=80ff00>",
        "@GR3@" to "<col=40ff00>",
        "@cr1@" to "<img=0>",
        "@cr2@" to "<img=2>",
        "@cr3@" to "<img=3>",
        "@cr4@" to "<img=4>",
        "@cr5@" to "<img=5>",
        "@cr6@" to "<img=6>"
    )

    private fun handleOldSyntax(text: String): String {
        for ((k, v) in replaceSyntax) {
            if (text.contains(k)) {
                return text.replace(k, v)
            }
        }
        return text
    }

    private fun drawBasicString(string: String, drawX: Int, drawY: Int) {
        var string = string
        var drawX = drawX
        var drawY = drawY
        drawY -= baseCharacterHeight
        var startIndex = -1
        string = handleOldSyntax(string)
        for (currentCharacter in string.indices) {
            var character = string[currentCharacter].toInt()
            if (character > 255) {
                character = 32
            }
            if (character == 60) {
                startIndex = currentCharacter
            } else {
                if (character == 62 && startIndex != -1) {
                    val effectString = string.substring(startIndex + 1, currentCharacter)
                    startIndex = -1
                    when (effectString) {
                        startEffect -> character = 60
                        endEffect -> character = 62
                        aRSString_4135 -> character = 160
                        aRSString_4162 -> character = 173
                        aRSString_4165 -> character = 215
                        aRSString_4147 -> character = 128
                        aRSString_4163 -> character = 169
                        aRSString_4169 -> character = 174
                    }
                }
                if (startIndex == -1) {
                    val width: Int = characterWidths[character]
                    val height: Int = characterHeights[character]
                    if (character != 32) {
                        if (transparency == 256) {
                            if (textShadowColor != -1) {
                                drawCharacter(character, drawX + characterDrawXOffsets[character] + 1, drawY + characterDrawYOffsets[character] + 1, width, height, textShadowColor, 225)
                            }
                            drawCharacter(character, drawX + characterDrawXOffsets[character], drawY + characterDrawYOffsets[character], width, height, textColor, 225)
                        } else {
                            if (textShadowColor != -1) {
                                drawCharacter(character, drawX + characterDrawXOffsets[character] + 1, drawY + characterDrawYOffsets[character] + 1, width, height, textShadowColor, transparency)
                            }
                            drawCharacter(character, drawX + characterDrawXOffsets[character], drawY + characterDrawYOffsets[character], width, height, textColor, transparency)
                        }
                    } else if (anInt4178 > 0) {
                        anInt4175 += anInt4178
                        drawX += anInt4175 shr 8
                        anInt4175 = anInt4175 and 0xff
                    }
                    val lineWidth: Int = characterScreenWidths[character]
                    if (strikethroughColor != -1) {
                        Rasterizer2D.drawHorizontalLine(drawX, drawY + (baseCharacterHeight.toDouble() * 0.69999999999999996).toInt(), lineWidth, strikethroughColor)
                    }
                    if (underlineColor != -1) {
                        Rasterizer2D.drawHorizontalLine(drawX, drawY + baseCharacterHeight, lineWidth, underlineColor)
                    }
                    drawX += lineWidth
                }
            }
        }
    }

    private fun drawCharacter(character: Int, x: Int, y: Int, width: Int, height: Int, color: Int, alpha: Int) {
        var x = x
        var y = y
        var width = width
        var height = height
        var destPos = x + y * Rasterizer2D.width
        var destOffset = Rasterizer2D.width - width
        var maskOffset = 0
        var maskPos = 0
        if (y < Rasterizer2D.topY) {
            val d = Rasterizer2D.topY - y
            height -= d
            y = Rasterizer2D.topY
            maskPos += d * width
            destPos += d * Rasterizer2D.width
        }
        if (y + height > Rasterizer2D.bottomY) {
            height -= y + height - Rasterizer2D.bottomY
        }
        if (x < Rasterizer2D.leftX) {
            val d = Rasterizer2D.leftX - x
            width -= d
            x = Rasterizer2D.leftX
            maskPos += d
            destPos += d
            maskOffset += d
            destOffset += d
        }
        if (x + width > Rasterizer2D.bottomX) {
            val d = x + width - Rasterizer2D.bottomX
            width -= d
            maskOffset += d
            destOffset += d
        }
        if (width > 0 && height > 0) {
            createTransparentCharacterPixels(Rasterizer2D.pixels, fontPixels[character]!!, color, maskPos, destPos, width, height, destOffset, maskOffset, alpha)
        }
    }

    private fun createTransparentCharacterPixels(dest: IntArray, mask: ByteArray, color: Int, maskPos: Int, destPos: Int, width: Int, height: Int, destOffset: Int, maskOffset: Int, alpha: Int) {
        var maskPos = maskPos
        var destPos = destPos
        var color = color
        var alpha = alpha
        color = ((color and 0xff00ff) * alpha and -0xff0100) + ((color and 0xff00) * alpha and 0xff0000) shr 8
        alpha = 256 - alpha
        for (i2 in 0 until height) {
            for (k2 in 0 until width) {
                if (mask[maskPos++] != 0.toByte()) {
                    val areaPixelColor: Int = dest[destPos]
                    dest[destPos] = (((areaPixelColor and 0xff00ff) * alpha and -0xff0100) + ((areaPixelColor and 0xff00) * alpha and 0xff0000) shr 8) + color
                }
                destPos++
            }
            destPos += destOffset
            maskPos += maskOffset
        }
    }

    fun getTextWidth(string: String?): Int {
        if (string == null) {
            return 0
        }
        var startIndex = -1
        var finalWidth = 0
        for (currentCharacter in string.indices) {
            var character = string[currentCharacter].toInt()
            if (character > 255) {
                character = 32
            }
            if (character == 60) {
                startIndex = currentCharacter
            } else {
                if (character == 62 && startIndex != -1) {
                    val effectString = string.substring(startIndex + 1, currentCharacter)
                    startIndex = -1
                    when (effectString) {
                        startEffect -> character = 60
                        endEffect -> character = 62
                        aRSString_4135 -> character = 160
                        aRSString_4162 -> character = 173
                        aRSString_4165 -> character = 215
                        aRSString_4147 -> character = 128
                        aRSString_4163 -> character = 169
                        aRSString_4169 -> character = 174
                    }
                }
                if (startIndex == -1) {
                    finalWidth += characterScreenWidths.get(character)
                }
            }
        }
        return finalWidth
    }

    fun drawBasicString(string: String, drawX: Int, drawY: Int, color: Int, shadow: Int) {
        if (string != null) {
            setColorAndShadow(color, shadow)
            setTextEffects(string)
            drawBasicString(string, drawX, drawY)
        }
    }

    fun drawCenteredString(string: String, drawX: Int, drawY: Int, color: Int, shadow: Int) {
        var string = string
        if (string != null) {
            setColorAndShadow(color, shadow)
            string = handleOldSyntax(string)
            drawBasicString(string, drawX - getTextWidth(string) / 2, drawY)
        }
    }

    fun setColorAndShadow(color: Int, shadow: Int) {
        strikethroughColor = -1
        underlineColor = -1
        defaultShadow = shadow
        textShadowColor = defaultShadow
        defaultColor = color
        textColor = defaultColor
        defaultTransparency = 256
        transparency = defaultTransparency
        anInt4178 = 0
        anInt4175 = 0
    }

    fun setTextEffects(string: String) {
        do {
            try {
                if (string.startsWith(startColor)) {
                    val color = string.substring(4)
                    textColor = if (color.length < 6) Color.decode(color).rgb else color.toInt(16)
                } else if (string == endColor) {
                    textColor = defaultColor
                } else if (string.startsWith(startTransparency)) {
                    transparency = Integer.valueOf(string.substring(6))
                } else if (string == endTransparency) {
                    transparency = defaultTransparency
                } else if (string.startsWith(startStrikethrough)) {
                    strikethroughColor = Integer.valueOf(string.substring(4))
                } else if (string == defaultStrikethrough) {
                    strikethroughColor = 8388608
                } else if (string == endStrikethrough) {
                    strikethroughColor = -1
                } else if (string.startsWith(startUnderline)) {
                    underlineColor = Integer.valueOf(string.substring(2))
                } else if (string == startDefaultUnderline) {
                    underlineColor = 0
                } else if (string == endUnderline) {
                    underlineColor = -1
                } else if (string.startsWith(startShadow)) {
                    textShadowColor = Integer.valueOf(string.substring(5))
                } else if (string == startDefaultShadow) {
                    textShadowColor = 0
                } else if (string == endShadow) {
                    textShadowColor = defaultShadow
                } else {
                    if (string != lineBreak) {
                        break
                    }
                    setDefaultTextEffectValues(defaultColor, defaultShadow, defaultTransparency)
                }
            } catch (exception: Exception) {
                break
            }
            break
        } while (false)
    }

    fun setDefaultTextEffectValues(color: Int, shadow: Int, trans: Int) {
        strikethroughColor = -1
        underlineColor = -1
        defaultShadow = shadow
        textShadowColor = defaultShadow
        defaultColor = color
        textColor = defaultColor
        defaultTransparency = trans
        transparency = defaultTransparency
        anInt4178 = 0
        anInt4175 = 0
    }

    companion object {
        fun FontType.getTextWidth(string: String) = this.font.getTextWidth(string)
        fun FontType.drawBasicString(string: String, drawX: Int, drawY: Int, color: Int, shadow: Int) = this.font.drawBasicString(string, drawX, drawY, color, shadow)
        fun FontType.drawCenteredString(string: String, drawX: Int, drawY: Int, color: Int, shadow: Int) = this.font.drawCenteredString(string, drawX, drawY, color, shadow)
    }
}
