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
package com.runescape.cache

import com.runescape.Client
import com.runescape.Constants
import com.runescape.cache.impl.MediaLoader
import com.runescape.draw.screens.LoaderScreen
import com.runescape.utils.Signlink
import mu.KotlinLogging

class CacheUnpacker(private val client: Client) {

    private val logger = KotlinLogging.logger {}

    /**
     * Main runnable method.
     */
    @ExperimentalStdlibApi
    fun load() {
        logger.info { "${Constants.NAME} Starting Up." }
        val centerX = client.frameWidth / 2
        val centerY = client.frameHeight / 2
        this.init()

        message = "Preparing packing modules."

        client.spriteCache.init()

        client.startThread(LoaderScreen(centerX, centerY, client), 8)
        load(MediaLoader(createArchive(4)))
        client.titleArchive = createArchive(1)

        finished = true
        message = ""
        System.gc()
    }

    private fun load(loader: CacheLoader) {
        message = loader.message()
        progress = loader.progress()
        logger.info { loader.message() }
        loader.run(client)
    }

    /**
     * Main Initializer usage: Unpacks the start up archive. And all important startup files.
     */
    private fun init() {
        if (Signlink.cache_dat != null) {
            for (i in FileStore.Store.values().indices) {
                client.indices[i] = FileStore(Signlink.cache_dat!!, Signlink.indices[i]!!, i + 1)
            }
        }
    }

    /**
     * Finalize stage.
     */
    fun reset() {
        successfullyLoaded = true
    }

    private fun createArchive(file: Int): FileArchive {
        var buffer: ByteArray? = null
        if (client.indices[0] != null) {
            buffer = client.indices[0]!!.decompress(file)
        }
        // Re-request archive
        return FileArchive(buffer!!)
    }

    companion object {
        var message = ""
        var finished = false
        var successfullyLoaded = false
        var progress = 0
    }
}
