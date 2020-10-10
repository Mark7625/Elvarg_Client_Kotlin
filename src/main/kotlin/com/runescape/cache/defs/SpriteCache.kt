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
package com.runescape.cache.defs

import com.runescape.draw.Sprite
import com.runescape.utils.Signlink.getCacheDir
import mu.KotlinLogging
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption.READ
import javax.imageio.ImageIO
import kotlin.experimental.and

class SpriteCache : Closeable {

    private val logger = KotlinLogging.logger {}
    private lateinit var cache: Array<Sprite?>
    private lateinit var dataChannel: FileChannel
    private lateinit var metaChannel: FileChannel

    @Throws(IOException::class)
    fun init() {

        val dataFile = File(getCacheDir() + "main_file_sprites.dat")
        val metaFile = File(getCacheDir() + "main_file_sprites.idx")
        if (!dataFile.exists()) {
            throw IOException(String.format("Could not find data file=%s", dataFile.name))
        }
        if (!metaFile.exists()) {
            throw IOException(String.format("Could not find meta file=%s", metaFile.name))
        }
        dataChannel = FileChannel.open(dataFile.toPath(), READ)
        metaChannel = FileChannel.open(metaFile.toPath(), READ)
        val spriteCount = Math.toIntExact(metaChannel.size() / 10)
        cache = arrayOfNulls(spriteCount)
        logger.info { "Sprites Loaded: ${cache.size}." }
    }

    fun contains(id: Int): Boolean {
        return id < cache.size && cache[id] != null
    }

    fun set(id: Int, sprite: Sprite) {
        if (!contains(id)) {
            return
        }
        cache[id] = sprite
    }

    fun lookup(id: Int): Sprite {
        try {
            if (contains(id)) {
                return cache[id]!!
            }
            if (!dataChannel.isOpen || !metaChannel.isOpen) {
                println("Sprite channels are closed!")
            }
            val entries = Math.toIntExact(metaChannel.size() / 10)
            if (id > entries) {
                println(String.format("id=%d > size=%d", id, entries))
            }
            metaChannel.position(id * 10.toLong())
            val metaBuf: ByteBuffer = ByteBuffer.allocate(10)
            metaChannel.read(metaBuf)
            metaBuf.flip()

            val pos = ((metaBuf.get().toInt() and 0xFF) shl 16) + ((metaBuf.get().toInt() and 0xFF) shl 8) + (metaBuf.get().toInt() and 0xFF)
            val len = ((metaBuf.get().toInt() and 0xFF) shl 16) + ((metaBuf.get().toInt() and 0xFF) shl 8) + (metaBuf.get().toInt() and 0xFF)

            val offsetX: Int = (metaBuf.short and 0xFF).toInt()
            val offsetY: Int = (metaBuf.short and 0xFF).toInt()
            val dataBuf: ByteBuffer = ByteBuffer.allocate(len)
            dataChannel.position(pos.toLong())
            dataChannel.read(dataBuf)
            dataBuf.flip()
            ByteArrayInputStream(dataBuf.array()).use { input ->
                var bimage = ImageIO.read(input)
                if (bimage == null) {
                    println(String.format("Could not read image at %d", id))
                }
                if (bimage.type != BufferedImage.TYPE_INT_ARGB) {
                    bimage = convert(bimage)
                }
                val pixels = (bimage.raster.dataBuffer as DataBufferInt).data
                val sprite = Sprite(bimage.width, bimage.height, offsetX, offsetY, pixels)
                // cache so we don't have to perform I/O calls again
                cache[id] = sprite
                return sprite
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println(String.format("No sprite found for id=%d", id))
        return Sprite(0, 0)
    }

    private fun convert(bimage: BufferedImage): BufferedImage {
        val converted = BufferedImage(bimage.width, bimage.height, BufferedImage.TYPE_INT_ARGB)
        converted.graphics.drawImage(bimage, 0, 0, null)
        return converted
    }

    override fun close() {
        dataChannel.close()
        metaChannel.close()
    }
}
