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
import com.runescape.draw.Rasterizer2D.clear
import com.runescape.draw.Rasterizer2D.drawBox
import com.runescape.draw.Rasterizer2D.drawBoxOutline
import com.runescape.draw.Sprite
import com.runescape.draw.fonts.FontType
import com.runescape.draw.fonts.RSFont.Companion.drawBasicString
import com.runescape.draw.fonts.RSFont.Companion.getTextWidth
import com.runescape.draw.screens.LoginScreen
import com.runescape.draw.screens.World
import com.runescape.io.Buffer
import com.runescape.io.LoginResponse
import com.runescape.io.PacketSender
import com.runescape.io.RichPresence
import com.runescape.net.BufferedConnection
import com.runescape.net.IsaacCipher
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
    lateinit var gameScreenImageProducer: ProducingGraphicsBuffer

    var loggedIn = false
    var loadingError = false
    private var loginScreen: LoginScreen = LoginScreen(this)
    var firstLoginMessage: String = LoginResponse.DEFUALT.message1
    var secondLoginMessage: String = LoginResponse.DEFUALT.message2
    var myUsername = "Mark"
    var myPassword = "abc123"
    var myPin = ""
    var loginScreenCursorPos = 0
    var tick = 0
    var currentWorld: World? = null
    var packetSender : PacketSender = PacketSender(null)
    lateinit var socketStream: BufferedConnection
    private val loginBuffer: Buffer = Buffer(ByteArray(5000))
    private val incoming: Buffer = Buffer(ByteArray(5000))
    private var serverSeed: Long = 0
    private var myPrivilege = 0
    private var loadingStage = 0
    private var loginFailures = 0
    private var packetSize = 0
    private var opcode = 0

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
        } else {
            mainGameProcessor()
        }
    }

    override fun process() {
        if (loadingError) {
            return
        }
        tick++
        if (!loggedIn && CacheUnpacker.finished) {
            loginScreen.processInput()
        } else {
            drawGameScreen()
        }
    }

    private fun drawGameScreen() {
        if (loadingStage == 2) {
            moveCameraWithPlayer()
        }
    }

    private fun mainGameProcessor() {

        for (j in 0..99) {
            if (!readPacket()) {
                break
            }
        }

        if (!loggedIn) {
            return
        }

        loadingStages()
    }

    private fun moveCameraWithPlayer() {
        gameScreenImageProducer.drawGraphics(4,4,super.graphics)
    }

    private fun loadingStages() {
        if (loadingStage === 2) {
            gameScreenImageProducer.initDrawingArea()
            drawLoadingMessages(1, "Loading - please wait.")
            gameScreenImageProducer.drawGraphics(4, 4,super.graphics)
            loadingStage = 1
        }

    }

    fun readPacket() : Boolean {
        return true
    }

    private fun drawLoadingMessages(used: Int, s: String) {
        val width: Int = FontType.REGULAR.getTextWidth(s)
        val height = 25
        drawBox(1, 1, width + 6, height, 0)
        drawBox(1, 1, width + 6, 1, 0xffffff)
        drawBox(1, 1, 1, height, 0xffffff)
        drawBox(1, height, width + 6, 1, 0xffffff)
        drawBox(width + 6, 1, 1, height, 0xffffff)
        FontType.REGULAR.drawBasicString(s, 18, width / 2 + 5,0xffffff,1)
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
        clear()
    }

    private fun setupGameplayScreen() {
        gameScreenImageProducer = ProducingGraphicsBuffer(512, 334)
        clear()
    }

    private fun setMessages(message : LoginResponse) {
        firstLoginMessage = message.message1
        secondLoginMessage = message.message2
        return
    }

    fun login(username: String, password: String, pin: String, reconnecting: Boolean) {
        try {
            if (username.length < 3) {
                setMessages(LoginResponse.USERNAME)
            }
            if (password.length < 3) {
                setMessages(LoginResponse.PASSWORD)
                return
            }
            if (!reconnecting) {
                setMessages(LoginResponse.CONNECTING)
            }

            socketStream = BufferedConnection(this, openSocket(currentWorld!!.port))
            packetSender.buffer.resetPosition()
            packetSender.buffer.writeByte(14)
            socketStream.queueBytes(1, packetSender.buffer.payload)
            var cipher: IsaacCipher? = null
            var response = LoginResponse.getResponse(socketStream.read())

            val copy = response

            if (response == LoginResponse.CONNECTING_SERVER) {
                socketStream.flushInputStream(incoming.payload, 8)
                incoming.currentPosition = 0
                serverSeed = incoming.readLong() // aka server session key
                incoming.currentPosition = 0
                val seed = IntArray(4)
                seed[0] = (Math.random() * 99999999.0).toInt()
                seed[1] = (Math.random() * 99999999.0).toInt()
                seed[2] = (serverSeed shr 32).toInt()
                seed[3] = serverSeed.toInt()
                packetSender.buffer.resetPosition()
                packetSender.buffer.writeByte(10)
                packetSender.buffer.writeInt(seed[0])
                packetSender.buffer.writeInt(seed[1])
                packetSender.buffer.writeInt(seed[2])
                packetSender.buffer.writeInt(seed[3])
                packetSender.buffer.writeInt(8784521)
                packetSender.buffer.writeString(username)
                packetSender.buffer.writeString(password)
                packetSender.buffer.encryptRSAContent()
                loginBuffer.currentPosition = 0
                loginBuffer.writeByte(if (reconnecting) 18 else 16)
                loginBuffer.writeByte(packetSender.buffer.currentPosition + 2) // size of the
                loginBuffer.writeByte(255)
                loginBuffer.writeByte(1) // low mem or not
                loginBuffer.writeBytes(packetSender.buffer.payload, packetSender.buffer.currentPosition, 0)
                cipher = IsaacCipher(seed)
                for (index in 0..3) seed[index] += 50
                socketStream.queueBytes(loginBuffer.currentPosition, loginBuffer.payload)
                response = LoginResponse.getResponse(socketStream.read())

            }

            if (response == LoginResponse.LOGIN) {
                myPrivilege = socketStream.read()
                super.awtFocus = true
                loggedIn = true
                packetSender = PacketSender(cipher)
                super.idleTime = 0

                RichPresence.setTopText("Logged in: $username")

                setupGameplayScreen()
                return
            }

            if(response != LoginResponse.NONE) {
                println("hey123")
                setMessages(response)
            }

        } catch (_ex : IOException) {
            firstLoginMessage = ""
        } catch (e : Exception) {
            println("Error while generating uid. Skipping step.");
            e.printStackTrace();
        }
        secondLoginMessage = "Error connecting to server.";

    }

    fun setBounds() {
        if (loggedIn) {
            gameScreenImageProducer = ProducingGraphicsBuffer(512, 334)
        }
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
