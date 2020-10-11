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
package com.runescape

import com.runescape.Client.Companion.instance
import com.runescape.cache.CacheUnpacker
import com.runescape.cache.FileArchive
import com.runescape.cache.FileStore
import com.runescape.cache.defs.SpriteCache
import com.runescape.draw.ProducingGraphicsBuffer
import com.runescape.draw.Rasterizer2D
import com.runescape.draw.Rasterizer2D.drawBox
import com.runescape.draw.Rasterizer2D.drawBoxOutline
import com.runescape.draw.Sprite
import com.runescape.draw.fonts.FontType
import com.runescape.draw.fonts.RSFont.Companion.drawBasicString
import com.runescape.draw.fonts.RSFont.Companion.getTextWidth
import com.runescape.draw.screens.LoginScreen
import com.runescape.draw.screens.World
import com.runescape.io.LoginMessages
import com.runescape.utils.Signlink
import java.awt.Color
import java.io.IOException
import java.net.InetAddress
import java.net.Socket

class Client : ClientEngine() {

    var indices: Array<FileStore?> = arrayOfNulls(FileStore.Store.values().size)
    var cacheHandler: CacheUnpacker = CacheUnpacker(this)
    lateinit var titleArchive: FileArchive
    val spriteCache = SpriteCache()
    lateinit var loginBoxImageProducer: ProducingGraphicsBuffer
    val loggedIn = false
    var loadingError = false
    private var loginScreen: LoginScreen = LoginScreen(this)
    var firstLoginMessage: String = LoginMessages.DEFUALT.message1
    var secondLoginMessage: String = LoginMessages.DEFUALT.message2
    var myUsername = "Mark"
    var myPassword = "abc123"
    var myPin = ""
    var loginScreenCursorPos = 0
    var tick = 0
    var currentWorld: World? = null

    /**
     * Run / Starts the run process.
     */
    override fun run() {
        if (!CacheUnpacker.finished) {
            super.run()
        }
    }

    fun clickInRegion(x: Int, y: Int, drawnSprite: Sprite) = clickInRegion(x, y, drawnSprite.myWidth, drawnSprite.myHeight)

    fun clickInRegion(x: Int, y: Int, width: Int, height: Int) = super.clickX >= x && super.clickX <= x + width && super.clickY >= y && super.clickY <= y + height

    fun mouseInRegion(x: Int, y: Int, drawnSprite: Sprite) = mouseInRegion(x, y, drawnSprite.myWidth, drawnSprite.myHeight)

    fun mouseInRegion(x: Int, y: Int, width: Int, height: Int) = super.mouseX >= x && super.mouseX <= x + width && super.mouseY >= y && super.mouseY <= y + height

    override fun initialize() {
        Signlink.run()
        cacheHandler.load()
    }

    override fun update() {
        if (!loggedIn && CacheUnpacker.finished) {
            loginScreen.renderScreen()
        }
    }

    override fun process() {
        if (loadingError) {
            return
        }
        tick++
        if (!loggedIn && CacheUnpacker.finished) {
            loginScreen.processInput()
        }
    }

    /**
     * Run / Starts the run process.
     */
    override fun init() {
        try {
            background = Color.BLACK
            requestFocusInWindow()
            startApplet()
        } catch (exception: Exception) {
            println("Failed to launch the applet.")
        }
    }

    @Throws(IOException::class)
    fun openSocket(port: Int): Socket {
        synchronized(true) {
            return Socket(InetAddress.getByName("127.0.0.1"), port)
        }
    }

    fun setupLoginScreen() {
        loginBoxImageProducer = ProducingGraphicsBuffer(frameWidth, frameHeight)
        Rasterizer2D.clear()
    }

    fun login(username: String, password: String, pin: String, reconnecting: Boolean) {
        println(username)
    }

    fun drawHoverBox(xPos: Int, yPos: Int, text: String) {
        var positionY = yPos
        val results = text.split("\n").toTypedArray()
        val height = results.size * 16 + 6
        var width: Int
        width = FontType.REGULAR.getTextWidth(results[0]) + 6
        for (i in 1 until results.size) {
            if (width <= FontType.REGULAR.getTextWidth(results[i]) + 6) {
                width = FontType.REGULAR.getTextWidth(results[i]) + 6
            }
        }
        drawBox(xPos, positionY, width, height - 3 * results.size, 0xFFFFA0)
        drawBoxOutline(xPos, positionY, width, height - 3 * results.size, 0)
        positionY += 14
        for (i in results.indices) {
            FontType.REGULAR.drawBasicString(results[i], xPos + 3, positionY, 0x000000, 0)
            positionY += 13
        }
    }

    companion object {
        var instance: Client = Client()
    }

    override fun reset() {
        cacheHandler.reset()
    }
}

fun main() {
    instance.startApplication()
}
