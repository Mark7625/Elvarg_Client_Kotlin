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

object Rasterizer2D {

    lateinit var pixels: IntArray
    var width = 0
    var height = 0
    var topY = 0
    var bottomY = 0
    var leftX = 0
    var bottomX = 0
    var lastX = 0
    var viewportCenterX = 0
    var viewportCenterY = 0
    /**
     * Sets the Rasterizer2D in the upper left corner with height, width and pixels set.
     *
     * @param height The height of the drawingArea.
     * @param width  The width of the drawingArea.
     * @param pixels The array of pixels (RGBColours) in the drawingArea.
     */
    fun initDrawingArea(height: Int, width: Int, pixels: IntArray) {
        this.pixels = pixels
        this.width = width
        this.height = height
        setDrawingArea(height, 0, width, 0)
    }

    /**
     * Draws a transparent box with a gradient that changes from top to bottom.
     *
     * @param leftX        The left edge X-Coordinate of the box.
     * @param topY         The top edge Y-Coordinate of the box.
     * @param width        The width of the box.
     * @param height       The height of the box.
     * @param topColour    The top rgbColour of the gradient.
     * @param bottomColour The bottom rgbColour of the gradient.
     * @param opacity      The opacity value ranging from 0 to 256.
     */
    fun drawTransparentGradientBox(
        leftX: Int,
        topY: Int,
        width: Int,
        height: Int,
        topColour: Int,
        bottomColour: Int,
        opacity: Int
    ) {
        var leftX = leftX
        var topY = topY
        var width = width
        var height = height
        var gradientProgress = 0
        val progressPerPixel = 0x10000 / height
        if (leftX < this.leftX) {
            width -= this.leftX - leftX
            leftX = this.leftX
        }
        if (topY < this.topY) {
            gradientProgress += (this.topY - topY) * progressPerPixel
            height -= this.topY - topY
            topY = this.topY
        }
        if (leftX + width > bottomX) width = bottomX - leftX
        if (topY + height > bottomY) height = bottomY - topY
        val leftOver = this.width - width
        val transparency = 256 - opacity
        var pixelIndex = leftX + topY * this.width
        for (rowIndex in 0 until height) {
            val gradient = 0x10000 - gradientProgress shr 8
            val inverseGradient = gradientProgress shr 8
            val gradientColour =
                ((topColour and 0xff00ff) * gradient + (bottomColour and 0xff00ff) * inverseGradient and -0xff0100) + ((topColour and 0xff00) * gradient + (bottomColour and 0xff00) * inverseGradient and 0xff0000) ushr 8
            val transparentPixel =
                ((gradientColour and 0xff00ff) * opacity shr 8 and 0xff00ff) + ((gradientColour and 0xff00) * opacity shr 8 and 0xff00)
            for (columnIndex in 0 until width) {
                var backgroundPixel = pixels[pixelIndex]
                backgroundPixel =
                    ((backgroundPixel and 0xff00ff) * transparency shr 8 and 0xff00ff) + ((backgroundPixel and 0xff00) * transparency shr 8 and 0xff00)
                pixels[pixelIndex++] = transparentPixel + backgroundPixel
            }
            pixelIndex += leftOver
            gradientProgress += progressPerPixel
        }
    }

    /**
     * Sets the drawingArea to the default size and position.
     * Position: Upper left corner.
     * Size: As specified before.
     */
    fun defaultDrawingAreaSize() {
        leftX = 0
        topY = 0
        bottomX = width
        bottomY = height
        lastX = bottomX
        viewportCenterX = bottomX / 2
    }

    /**
     * Sets the drawingArea based on the coordinates of the edges.
     *
     * @param bottomY The bottom edge Y-Coordinate.
     * @param leftX   The left edge X-Coordinate.
     * @param rightX  The right edge X-Coordinate.
     * @param topY    The top edge Y-Coordinate.
     */
    fun setDrawingArea(bottomY: Int, leftX: Int, rightX: Int, topY: Int) {
        var bottomY = bottomY
        var leftX = leftX
        var rightX = rightX
        var topY = topY
        if (leftX < 0) {
            leftX = 0
        }
        if (topY < 0) {
            topY = 0
        }
        if (rightX > width) {
            rightX = width
        }
        if (bottomY > height) {
            bottomY = height
        }
        this.leftX = leftX
        this.topY = topY
        bottomX = rightX
        this.bottomY = bottomY
        lastX = bottomX
        viewportCenterX = bottomX / 2
        viewportCenterY = this.bottomY / 2
    }
    /* Graphics2D methods */
    /**
     * Clears the drawingArea by setting every pixel to 0 (black).
     */
    fun clear() {
        val i = width * height
        for (j in 0 until i) {
            pixels[j] = 0
        }
    }

    /**
     * Draws a box filled with a certain colour.
     *
     * @param leftX     The left edge X-Coordinate of the box.
     * @param topY      The top edge Y-Coordinate of the box.
     * @param width     The width of the box.
     * @param height    The height of the box.
     * @param rgbColour The RGBColour of the box.
     */
    fun drawBox(leftX: Int, topY: Int, width: Int, height: Int, rgbColour: Int) {
        var leftX = leftX
        var topY = topY
        var width = width
        var height = height
        if (leftX < this.leftX) {
            width -= this.leftX - leftX
            leftX = this.leftX
        }
        if (topY < this.topY) {
            height -= this.topY - topY
            topY = this.topY
        }
        if (leftX + width > bottomX) width = bottomX - leftX
        if (topY + height > bottomY) height = bottomY - topY
        val leftOver = this.width - width
        var pixelIndex = leftX + topY * this.width
        for (rowIndex in 0 until height) {
            for (columnIndex in 0 until width) pixels[pixelIndex++] = rgbColour
            pixelIndex += leftOver
        }
    }

    /**
     * Draws a transparent box.
     *
     * @param leftX     The left edge X-Coordinate of the box.
     * @param topY      The top edge Y-Coordinate of the box.
     * @param width     The box width.
     * @param height    The box height.
     * @param rgbColour The box colour.
     * @param opacity   The opacity value ranging from 0 to 256.
     */
    fun drawTransparentBox(leftX: Int, topY: Int, width: Int, height: Int, rgbColour: Int, opacity: Int) {
        var leftX = leftX
        var topY = topY
        var width = width
        var height = height
        if (leftX < this.leftX) {
            width -= this.leftX - leftX
            leftX = this.leftX
        }
        if (topY < this.topY) {
            height -= this.topY - topY
            topY = this.topY
        }
        if (leftX + width > bottomX) width = bottomX - leftX
        if (topY + height > bottomY) height = bottomY - topY
        val transparency = 256 - opacity
        val red = (rgbColour shr 16 and 0xff) * opacity
        val green = (rgbColour shr 8 and 0xff) * opacity
        val blue = (rgbColour and 0xff) * opacity
        val leftOver = this.width - width
        var pixelIndex = leftX + topY * this.width
        for (rowIndex in 0 until height) {
            for (columnIndex in 0 until width) {
                val otherRed = (pixels[pixelIndex] shr 16 and 0xff) * transparency
                val otherGreen = (pixels[pixelIndex] shr 8 and 0xff) * transparency
                val otherBlue = (pixels[pixelIndex] and 0xff) * transparency
                val transparentColour =
                    (red + otherRed shr 8 shl 16) + (green + otherGreen shr 8 shl 8) + (blue + otherBlue shr 8)
                pixels[pixelIndex++] = transparentColour
            }
            pixelIndex += leftOver
        }
    }

    fun drawPixels(height: Int, posY: Int, posX: Int, color: Int, w: Int) {
        var height = height
        var posY = posY
        var posX = posX
        var w = w
        if (posX < leftX) {
            w -= leftX - posX
            posX = leftX
        }
        if (posY < topY) {
            height -= topY - posY
            posY = topY
        }
        if (posX + w > bottomX) {
            w = bottomX - posX
        }
        if (posY + height > bottomY) {
            height = bottomY - posY
        }
        val k1 = width - w
        var l1 = posX + posY * width
        for (i2 in -height..-1) {
            for (j2 in -w..-1) {
                pixels[l1++] = color
            }
            l1 += k1
        }
    }

    /**
     * Draws a 1 pixel thick box outline in a certain colour.
     *
     * @param leftX     The left edge X-Coordinate.
     * @param topY      The top edge Y-Coordinate.
     * @param width     The width.
     * @param height    The height.
     * @param rgbColour The RGB-Colour.
     */
    fun drawBoxOutline(leftX: Int, topY: Int, width: Int, height: Int, rgbColour: Int) {
        drawHorizontalLine(leftX, topY, width, rgbColour)
        drawHorizontalLine(leftX, topY + height - 1, width, rgbColour)
        drawVerticalLine(leftX, topY, height, rgbColour)
        drawVerticalLine(leftX + width - 1, topY, height, rgbColour)
    }

    /**
     * Draws a coloured horizontal line in the drawingArea.
     *
     * @param xPosition The start X-Position of the line.
     * @param yPosition The Y-Position of the line.
     * @param width     The width of the line.
     * @param rgbColour The colour of the line.
     */
    fun drawHorizontalLine(xPosition: Int, yPosition: Int, width: Int, rgbColour: Int) {
        var xPosition = xPosition
        var width = width
        if (yPosition < topY || yPosition >= bottomY) return
        if (xPosition < leftX) {
            width -= leftX - xPosition
            xPosition = leftX
        }
        if (xPosition + width > bottomX) width = bottomX - xPosition
        val pixelIndex = xPosition + yPosition * this.width
        for (i in 0 until width) pixels[pixelIndex + i] = rgbColour
    }

    fun drawHorizontalLine(x: Int, y: Int, width: Int, color: Int, alpha: Int) {
        var x = x
        var width = width
        if (y < topY || y >= bottomY) return
        if (x < leftX) {
            width -= leftX - x
            x = leftX
        }
        if (x + width > bottomX) width = bottomX - x
        val transparency = 256 - alpha
        val red = (color shr 16 and 0xff) * alpha
        val green = (color shr 8 and 0xff) * alpha
        val blue = (color and 0xff) * alpha
        var pixelIndex = x + y * this.width
        for (j3 in 0 until width) {
            val otherRed = (pixels[pixelIndex] shr 16 and 0xff) * transparency
            val otherGreen = (pixels[pixelIndex] shr 8 and 0xff) * transparency
            val otherBlue = (pixels[pixelIndex] and 0xff) * transparency
            val transparentColour =
                (red + otherRed shr 8 shl 16) + (green + otherGreen shr 8 shl 8) + (blue + otherBlue shr 8)
            pixels[pixelIndex++] = transparentColour
        }
    }

    fun fillRectangle(h: Int, yPos: Int, xPos: Int, color: Int, w: Int) {
        var h = h
        var yPos = yPos
        var xPos = xPos
        var w = w
        if (xPos < leftX) {
            w -= leftX - xPos
            xPos = leftX
        }
        if (yPos < topY) {
            h -= topY - yPos
            yPos = topY
        }
        if (xPos + w > bottomX) w = bottomX - xPos
        if (yPos + h > bottomY) h = bottomY - yPos
        val k1 = width - w
        var l1 = xPos + yPos * width
        for (i2 in -h..-1) {
            for (j2 in -w..-1) pixels[l1++] = color
            l1 += k1
        }
    }

    fun fillRectangle(x: Int, y: Int, w: Int, h: Int, color: Int, alpha: Int) {
        var x = x
        var y = y
        var w = w
        var h = h
        if (x < leftX) {
            w -= leftX - x
            x = leftX
        }
        if (y < topY) {
            h -= topY - y
            y = topY
        }
        if (x + w > bottomX) w = bottomX - x
        if (y + h > bottomY) h = bottomY - y
        val a2 = 256 - alpha
        val r1 = (color shr 16 and 0xff) * alpha
        val g1 = (color shr 8 and 0xff) * alpha
        val b1 = (color and 0xff) * alpha
        val k3 = width - w
        var pixel = x + y * width
        for (i4 in 0 until h) {
            for (index in -w..-1) {
                val r2 = (pixels[pixel] shr 16 and 0xff) * a2
                val g2 = (pixels[pixel] shr 8 and 0xff) * a2
                val b2 = (pixels[pixel] and 0xff) * a2
                val rgb = (r1 + r2 shr 8 shl 16) + (g1 + g2 shr 8 shl 8) + (b1 + b2 shr 8)
                pixels[pixel++] = rgb
            }
            pixel += k3
        }
    }

    /**
     * Draws a coloured vertical line in the drawingArea.
     *
     * @param xPosition The X-Position of the line.
     * @param yPosition The start Y-Position of the line.
     * @param height    The height of the line.
     * @param rgbColour The colour of the line.
     */
    fun drawVerticalLine(xPosition: Int, yPosition: Int, height: Int, rgbColour: Int) {
        var yPosition = yPosition
        var height = height
        if (xPosition < leftX || xPosition >= bottomX) return
        if (yPosition < topY) {
            height -= topY - yPosition
            yPosition = topY
        }
        if (yPosition + height > bottomY) height = bottomY - yPosition
        val pixelIndex = xPosition + yPosition * width
        for (rowIndex in 0 until height) pixels[pixelIndex + rowIndex * width] = rgbColour
    }

    /**
     * Draws a 1 pixel thick transparent box outline in a certain colour.
     *
     * @param leftX     The left edge X-Coordinate
     * @param topY      The top edge Y-Coordinate.
     * @param width     The width.
     * @param height    The height.
     * @param rgbColour The RGB-Colour.
     * @param opacity   The opacity value ranging from 0 to 256.
     */
    fun drawTransparentBoxOutline(
        leftX: Int,
        topY: Int,
        width: Int,
        height: Int,
        rgbColour: Int,
        opacity: Int
    ) {
        drawTransparentHorizontalLine(leftX, topY, width, rgbColour, opacity)
        drawTransparentHorizontalLine(leftX, topY + height - 1, width, rgbColour, opacity)
        if (height >= 3) {
            drawTransparentVerticalLine(leftX, topY + 1, height - 2, rgbColour, opacity)
            drawTransparentVerticalLine(leftX + width - 1, topY + 1, height - 2, rgbColour, opacity)
        }
    }

    /**
     * Draws a transparent coloured horizontal line in the drawingArea.
     *
     * @param xPosition The start X-Position of the line.
     * @param yPosition The Y-Position of the line.
     * @param width     The width of the line.
     * @param rgbColour The colour of the line.
     * @param opacity   The opacity value ranging from 0 to 256.
     */
    fun drawTransparentHorizontalLine(
        xPosition: Int,
        yPosition: Int,
        width: Int,
        rgbColour: Int,
        opacity: Int
    ) {
        var xPosition = xPosition
        var width = width
        if (yPosition < topY || yPosition >= bottomY) {
            return
        }
        if (xPosition < leftX) {
            width -= leftX - xPosition
            xPosition = leftX
        }
        if (xPosition + width > bottomX) {
            width = bottomX - xPosition
        }
        val transparency = 256 - opacity
        val red = (rgbColour shr 16 and 0xff) * opacity
        val green = (rgbColour shr 8 and 0xff) * opacity
        val blue = (rgbColour and 0xff) * opacity
        var pixelIndex = xPosition + yPosition * this.width
        for (i in 0 until width) {
            val otherRed = (pixels[pixelIndex] shr 16 and 0xff) * transparency
            val otherGreen = (pixels[pixelIndex] shr 8 and 0xff) * transparency
            val otherBlue = (pixels[pixelIndex] and 0xff) * transparency
            val transparentColour =
                (red + otherRed shr 8 shl 16) + (green + otherGreen shr 8 shl 8) + (blue + otherBlue shr 8)
            pixels[pixelIndex++] = transparentColour
        }
    }

    /**
     * Draws a transparent coloured vertical line in the drawingArea.
     *
     * @param xPosition The X-Position of the line.
     * @param yPosition The start Y-Position of the line.
     * @param height    The height of the line.
     * @param rgbColour The colour of the line.
     * @param opacity   The opacity value ranging from 0 to 256.
     */
    fun drawTransparentVerticalLine(
        xPosition: Int,
        yPosition: Int,
        height: Int,
        rgbColour: Int,
        opacity: Int
    ) {
        var yPosition = yPosition
        var height = height
        if (xPosition < leftX || xPosition >= bottomX) {
            return
        }
        if (yPosition < topY) {
            height -= topY - yPosition
            yPosition = topY
        }
        if (yPosition + height > bottomY) {
            height = bottomY - yPosition
        }
        val transparency = 256 - opacity
        val red = (rgbColour shr 16 and 0xff) * opacity
        val green = (rgbColour shr 8 and 0xff) * opacity
        val blue = (rgbColour and 0xff) * opacity
        var pixelIndex = xPosition + yPosition * width
        for (i in 0 until height) {
            val otherRed = (pixels[pixelIndex] shr 16 and 0xff) * transparency
            val otherGreen = (pixels[pixelIndex] shr 8 and 0xff) * transparency
            val otherBlue = (pixels[pixelIndex] and 0xff) * transparency
            val transparentColour =
                (red + otherRed shr 8 shl 16) + (green + otherGreen shr 8 shl 8) + (blue + otherBlue shr 8)
            pixels[pixelIndex] = transparentColour
            pixelIndex += width
        }
    }

    fun drawFilledCircle(x: Int, y: Int, radius: Int, color: Int, alpha: Int) {
        var y1 = y - radius
        if (y1 < 0) {
            y1 = 0
        }
        var y2 = y + radius
        if (y2 >= height) {
            y2 = height - 1
        }
        val a2 = 256 - alpha
        val r1 = (color shr 16 and 0xff) * alpha
        val g1 = (color shr 8 and 0xff) * alpha
        val b1 = (color and 0xff) * alpha
        for (iy in y1..y2) {
            val dy = iy - y
            val dist = Math.sqrt(radius * radius - dy * dy.toDouble()).toInt()
            var x1 = x - dist
            if (x1 < 0) {
                x1 = 0
            }
            var x2 = x + dist
            if (x2 >= width) {
                x2 = width - 1
            }
            var pos = x1 + iy * width
            for (ix in x1..x2) { /*  Tried replacing all pixels[pos] with:
                    Client.instance.gameScreenImageProducer.canvasRaster[pos]
					AND Rasterizer3D.pixels[pos] */
                val r2 = (pixels[pos] shr 16 and 0xff) * a2
                val g2 = (pixels[pos] shr 8 and 0xff) * a2
                val b2 = (pixels[pos] and 0xff) * a2
                pixels[pos++] = (r1 + r2 shr 8 shl 16) + (g1 + g2 shr 8 shl 8) + (b1 + b2 shr 8)
            }
        }
    }

    fun fillGradientRectangle(x: Int, y: Int, w: Int, h: Int, startColour: Int, endColour: Int) {
        var x = x
        var y = y
        var w = w
        var h = h
        var k1 = 0
        val l1 = 0x10000 / h
        if (x < leftX) {
            w -= leftX - x
            x = leftX
        }
        if (y < topY) {
            k1 += (topY - y) * l1
            h -= topY - y
            y = topY
        }
        if (x + w > bottomX) w = bottomX - x
        if (y + h > bottomY) h = bottomY - y
        val lineGap = width - w
        var pixelOffset = x + y * width
        for (yi in -h..-1) {
            val blendAmount = 0x10000 - k1 shr 8
            val blendInverse = k1 shr 8
            val combinedColour =
                ((startColour and 0xff00ff) * blendAmount + (endColour and 0xff00ff) * blendInverse and -0xff0100) + ((startColour and 0xff00) * blendAmount + (endColour and 0xff00) * blendInverse and 0xff0000) ushr 8
            val alpha =
                ((startColour shr 24 and 0xff) * blendAmount + (endColour shr 24 and 0xff) * blendInverse ushr 8) + 5
            for (index in -w..-1) {
                val backingPixel = pixels[pixelOffset]
                pixels[pixelOffset++] =
                    ((backingPixel and 0xff00ff) * (256 - alpha) + (combinedColour and 0xff00ff) * alpha and -0xff0100) + ((backingPixel and 0xff00) * (256 - alpha) + (combinedColour and 0xff00) * alpha and 0xff0000) ushr 8
            }
            pixelOffset += lineGap
            k1 += l1
        }
    }
}
