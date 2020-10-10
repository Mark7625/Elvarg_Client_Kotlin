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

    /**
     * Run / Starts the run process.
     */
    override fun run() {
        if (!CacheUnpacker.successfullyLoaded) {
            super.run()
        }
    }

    @ExperimentalStdlibApi
    override fun initialize() {
        Signlink.run()
        cacheHandler.load()
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
