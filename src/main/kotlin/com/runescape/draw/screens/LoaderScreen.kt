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
package com.runescape.draw.screens

import com.runescape.Client
import com.runescape.cache.CacheUnpacker
import com.runescape.cache.CacheUnpacker.Companion.progress
import com.runescape.draw.Rasterizer2D

class LoaderScreen(private val x: Int, private val y: Int, private val client: Client) : Runnable {

    override fun run() {
        var time = System.currentTimeMillis()
        do {
            updateLoading(x, y)
            val t2 = System.currentTimeMillis()
            if (t2 - time < 10) {
                Thread.sleep(10 - (t2 - time))
            }
            time = t2
        } while (!CacheUnpacker.finished)
    }

    private var lastPercent = 0
    private var above100 = false

    @Synchronized
    private fun updateLoading(centerX: Int, centerY: Int) {
        client.setupLoginScreen()
        client.loginBoxImageProducer.initDrawingArea()

        client.spriteCache.lookup(0).drawSprite(0, 0)

        when {
            progress == 0 -> {
                lastPercent = -1
            }
            progress >= 100 -> {
                progress = 100
                above100 = true
            }
            above100 && lastPercent > progress -> {
                lastPercent = 1
                progress = 0
                above100 = false
            }
        }

        while (lastPercent < progress) {
            lastPercent++
            val progresspixels = lastPercent * 3
            Rasterizer2D.drawBoxOutline(centerX - 152, centerY, 304, 34, 0x8c1111)
            Rasterizer2D.drawBoxOutline(centerX - 151, centerY + 1, 302, 32, 0)
            Rasterizer2D.drawBox(centerX - 150, centerY + 2, progresspixels, 30, 0x8c1111)
            Rasterizer2D.drawBox(centerX - 150 + progresspixels, centerY + 2, 300 - progresspixels, 30, 0)
        }
        client.spriteCache.lookup(5).drawSprite(349, 0)
        Client.instance.loginBoxImageProducer.drawGraphics(0, 0, Client.instance.graphics)
    }
}
