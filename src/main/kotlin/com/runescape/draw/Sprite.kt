package com.runescape.draw

import com.runescape.Client
import java.awt.Color

class Sprite {

    var myPixels: IntArray
    var myWidth = 0
    var myHeight = 0
    var drawOffsetY = 0
    var maxWidth = 0
    var maxHeight = 0
    private var drawOffsetX = 0

    constructor(width: Int, height: Int, offsetX: Int, offsetY: Int, pixels: IntArray?) {
        myWidth = width
        myHeight = height
        drawOffsetX = offsetX
        drawOffsetY = offsetY
        myPixels = pixels!!
        val color = Color.MAGENTA
        setTransparency(color.red, color.green, color.blue)
    }

    constructor(i: Int, j: Int) {
        myPixels = IntArray(i * j)
        maxWidth = i
        myWidth = maxWidth
        maxHeight = j
        myHeight = maxHeight
        drawOffsetY = 0
        drawOffsetX = drawOffsetY
    }

    private fun setTransparency(red: Int, green: Int, blue: Int) {
        for (index in myPixels.indices) {
            if (myPixels[index] shr 16 and 255 === red && myPixels[index] shr 8 and 255 === green && myPixels[index] and 255 === blue) {
                myPixels[index] = 0
            }
        }
    }

    enum class Action {
        CLICK, HOVER
    }

    fun drawAdvancedSprite(x: Int, y: Int, action: Action, task: () -> Unit) {
        this.drawAdvancedSprite(x, y)
        fireEvent(x, y, action, task)
    }

    fun drawAdvancedSprite(x: Int, y: Int, sprite: Sprite, action: Action, task: () -> Unit) {
        if (Client.instance.mouseInRegion(x, y, sprite)) {
            sprite.drawAdvancedSprite(x, y)
        } else {
            this.drawAdvancedSprite(x, y)
        }
        fireEvent(x, y, action, task)
    }

    fun drawAdvancedSprite(x: Int, y: Int, sprite: Sprite) {
        if (Client.instance.mouseInRegion(x, y, sprite)) {
            this.drawAdvancedSprite(x, y)
        } else {
            sprite.drawAdvancedSprite(x, y)
        }
    }

    fun drawSprite(x: Int, y: Int, action: Action, task: () -> Unit) {
        this.drawSprite(x, y)
        fireEvent(x, y, action, task)
    }

    fun drawSprite(x: Int, y: Int, sprite: Sprite, action: Action, task: () -> Unit) {
        if (Client.instance.mouseInRegion(x, y, sprite)) {
            sprite.drawSprite(x, y)
        } else {
            this.drawSprite(x, y)
        }
        fireEvent(x, y, action, task)
    }

    fun drawSprite(x: Int, y: Int, sprite: Sprite) {
        if (Client.instance.mouseInRegion(x, y, sprite)) {
            sprite.drawSprite(x, y)
        } else {
            this.drawSprite(x, y)
        }
    }

    private fun fireEvent(x: Int, y: Int, action: Action, task: () -> Unit) {
        when (action) {
            Action.CLICK -> {
                if (Client.instance.clickButton == 1 && Client.instance.clickInRegion(x, y, this)) {
                    task.invoke()
                }
            }
            Action.HOVER -> {
                if (Client.instance.mouseInRegion(x, y, this)) {
                    task.invoke()
                }
            }
        }
    }

    fun drawAdvancedSprite(posX: Int, posY: Int) {
        var posX = posX
        var posY = posY
        posX += drawOffsetX
        posY += drawOffsetY
        var containerPixel: Int = posX + posY * Rasterizer2D.width
        var pixelOffset = 0
        var height = myHeight
        var width = myWidth
        var containerScanSize: Int = Rasterizer2D.width - width
        var spriteScanSize = 0
        if (posY < Rasterizer2D.topY) {
            val heightLeft: Int = Rasterizer2D.topY - posY
            height -= heightLeft
            posY = Rasterizer2D.topY
            pixelOffset += heightLeft * width
            containerPixel += heightLeft * Rasterizer2D.width
        }
        if (posY + height > Rasterizer2D.bottomY) {
            height -= posY + height - Rasterizer2D.bottomY
        }
        if (posX < Rasterizer2D.leftX) {
            val widthLeft: Int = Rasterizer2D.leftX - posX
            width -= widthLeft
            posX = Rasterizer2D.leftX
            pixelOffset += widthLeft
            containerPixel += widthLeft
            spriteScanSize += widthLeft
            containerScanSize += widthLeft
        }
        if (posX + width > Rasterizer2D.bottomX) {
            val widthLeft: Int = posX + width - Rasterizer2D.bottomX
            width -= widthLeft
            spriteScanSize += widthLeft
            containerScanSize += widthLeft
        }
        if (width > 0 && height > 0) {
            drawToContainer(width, height, pixelOffset, myPixels, spriteScanSize, containerPixel, Rasterizer2D.pixels, containerScanSize)
        }
    }

    private fun drawToContainer(width: Int, height: Int, spritePixel: Int, spriteData: IntArray, spriteScanSize: Int, containerPixel: Int, containerData: IntArray, containerScanSize: Int) {
        var spritePixel = spritePixel
        var containerPixel = containerPixel
        for (y in 0 until height) {
            for (x in 0 until width) {
                val argb: Int = spriteData.get(spritePixel++)
                if (argb != 0) {
                    val alpha = argb shr 24 and 0xFF
                    val transparency = 256 - alpha
                    val originalColor: Int = containerData[containerPixel]
                    containerData[containerPixel++] = ((argb and 0xff00ff) * alpha + (originalColor and 0xff00ff) * transparency and -0xff0100) + ((argb and 0xff00) * alpha + (originalColor and 0xff00) * transparency and 0xff0000) shr 8
                } else {
                    containerPixel++
                }
            }
            containerPixel += containerScanSize
            spritePixel += spriteScanSize
        }
    }

    fun drawSprite(x: Int, y: Int) {
        var x = x
        var y = y
        x += drawOffsetX
        y += drawOffsetY
        var rasterClip = x + y * Rasterizer2D.width
        var imageClip = 0
        var height = myHeight
        var width = myWidth
        var rasterOffset = Rasterizer2D.width - width
        var imageOffset = 0
        if (y < Rasterizer2D.topY) {
            val dy = Rasterizer2D.topY - y
            height -= dy
            y = Rasterizer2D.topY
            imageClip += dy * width
            rasterClip += dy * Rasterizer2D.width
        }
        if (y + height > Rasterizer2D.bottomY) height -= y + height - Rasterizer2D.bottomY
        if (x < Rasterizer2D.leftX) {
            val dx = Rasterizer2D.leftX - x
            width -= dx
            x = Rasterizer2D.leftX
            imageClip += dx
            rasterClip += dx
            imageOffset += dx
            rasterOffset += dx
        }
        if (x + width > Rasterizer2D.bottomX) {
            val dx = x + width - Rasterizer2D.bottomX
            width -= dx
            imageOffset += dx
            rasterOffset += dx
        }
        if (!(width <= 0 || height <= 0)) {
            drawPixels(Rasterizer2D.pixels, myPixels, imageClip, rasterClip, width, height, rasterOffset, imageOffset)
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
                imagePixelColor = imagePixels[imagePixel++]
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
