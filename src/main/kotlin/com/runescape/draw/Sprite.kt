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
package com.runescape.draw

import java.awt.Color

class Sprite {

    var pixels: IntArray
    var xOffset = 0
    var yOffset = 0
    var width = 0
    var height = 0

    constructor(width: Int, height: Int) {
        pixels = IntArray(width * height)
        this.width = width
        this.height = height
        yOffset = 0
        xOffset = yOffset
    }

    constructor(width: Int, height: Int, offsetX: Int, offsetY: Int, pixels: IntArray) {
        this.xOffset = offsetX
        this.yOffset = offsetY
        this.pixels = pixels
        this.width = width
        this.height = height
        val color: Color = Color.MAGENTA
        setTransparency(color.red, color.green, color.blue)
    }

    private fun setTransparency(red: Int, green: Int, blue: Int) {
        for (index in pixels.indices) {
            if (pixels[index] shr 16 and 255 === red && pixels[index] shr 8 and 255 === green && pixels[index] and 255 === blue) {
                pixels[index] = 0
            }
        }
    }

    fun drawSprite(x: Int, y: Int) {
        var x = x
        var y = y
        x += xOffset
        y += yOffset
        var rasterClip: Int = x + y * Rasterizer2D.width
        var imageClip = 0
        var height: Int = width
        var width: Int = height
        var rasterOffset: Int = Rasterizer2D.width - width
        var imageOffset = 0
        if (y < Rasterizer2D.topY) {
            val dy: Int = Rasterizer2D.topY - y
            height -= dy
            y = Rasterizer2D.topY
            imageClip += dy * width
            rasterClip += dy * Rasterizer2D.width
        }
        if (y + height > Rasterizer2D.bottomY) height -= y + height - Rasterizer2D.bottomY
        if (x < Rasterizer2D.leftX) {
            val dx: Int = Rasterizer2D.leftX - x
            width -= dx
            x = Rasterizer2D.leftX
            imageClip += dx
            rasterClip += dx
            imageOffset += dx
            rasterOffset += dx
        }
        if (x + width > Rasterizer2D.bottomX) {
            val dx: Int = x + width - Rasterizer2D.bottomX
            width -= dx
            imageOffset += dx
            rasterOffset += dx
        }
        if (!(width <= 0 || height <= 0)) {
            drawPixels(Rasterizer2D.pixels, pixels, imageClip, rasterClip, width, height, rasterOffset, imageOffset)
        }
    }

    private fun drawPixels(areaPixels: IntArray, imagePixels: IntArray, imagePixel: Int, areaPixel: Int, l: Int, i1: Int, areaWidth: Int, imageWidth: Int) {
        var imagePixel = imagePixel
        var areaPixel = areaPixel
        var l = l
        var imagePixelColor: Int
        val l1 = -(l shr 2)
        l = -(l and 3)
        for (i2 in -i1..-1) {
            for (j2 in l1..-1) {
                imagePixelColor = imagePixels.get(imagePixel++)
                if (imagePixelColor != 0 && imagePixelColor != -1) {
                    areaPixels[areaPixel] = imagePixelColor
                }
                areaPixel++
                imagePixelColor = imagePixels[imagePixel++]
                if (imagePixelColor != 0 && imagePixelColor != -1) {
                    areaPixels[areaPixel] = imagePixelColor
                }
                areaPixel++
                imagePixelColor = imagePixels.get(imagePixel++)
                if (imagePixelColor != 0 && imagePixelColor != -1) {
                    areaPixels[areaPixel] = imagePixelColor
                }
                areaPixel++
                imagePixelColor = imagePixels.get(imagePixel++)
                if (imagePixelColor != 0 && imagePixelColor != -1) {
                    areaPixels[areaPixel] = imagePixelColor
                }
                areaPixel++
            }
            for (k2 in l..-1) {
                imagePixelColor = imagePixels.get(imagePixel++)
                if (imagePixelColor != 0 && imagePixelColor != -1) {
                    areaPixels[areaPixel] = imagePixelColor
                }
                areaPixel++
            }
            areaPixel += areaWidth
            imagePixel += imageWidth
        }
    }
}
