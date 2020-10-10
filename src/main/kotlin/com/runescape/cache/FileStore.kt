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

import java.io.IOException
import java.io.RandomAccessFile
import kotlin.experimental.and

class FileStore(val dataFile: RandomAccessFile, val indexFile: RandomAccessFile, val storeIndex: Int) {

    private val buffer = ByteArray(520)

    @Synchronized
    fun decompress(id: Int): ByteArray {
        return try {
            seek(indexFile, id * 6)
            var input = 0
            var read = 0
            while (read < 6) {
                input = indexFile.read(buffer, read, 6 - read)
                if (input == -1) {
                    return ByteArray(0)
                }
                read += input
            }
            val size: Int = (buffer[0].toInt() and 0xff shl 16) + (buffer[1].toInt() and 0xff shl 8) + (buffer[2].toInt() and 0xff)
            var sector: Int = (buffer[3].toInt() and 0xff shl 16) + (buffer[4].toInt() and 0xff shl 8) + (buffer[5].toInt() and 0xff)
            if (sector <= 0 || sector.toLong() > dataFile.length() / 520L) {
                return ByteArray(0)
            }
            val buf = ByteArray(size)
            var totalRead = 0
            var part = 0
            while (totalRead < size) {
                if (sector == 0) {
                    return ByteArray(0)
                }
                seek(dataFile, sector * 520)
                var unread = size - totalRead
                if (unread > 512) {
                    unread = 512
                }
                var input = 0
                var read = 0
                while (read < unread + 8) {
                    input = dataFile.read(buffer, read, unread + 8 - read)
                    if (input == -1) {
                        return ByteArray(0)
                    }
                    read += input
                }
                val currentIndex: Int = (buffer[0].toInt() and 0xff shl 8) + (buffer[1].toInt() and 0xff)
                val currentPart: Int = (buffer[2].toInt() and 0xff shl 8) + (buffer[3].toInt() and 0xff)
                val nextSector: Int = (buffer[4].toInt() and 0xff shl 16) + (buffer[5].toInt() and 0xff shl 8) + (buffer[6].toInt() and 0xff)

                val currentFile: Int = buffer[7].toInt() and 0xff
                if (currentIndex != id || currentPart != part || currentFile != storeIndex) {
                    return ByteArray(0)
                }
                if (nextSector < 0 || nextSector.toLong() > dataFile.length() / 520L) {
                    return ByteArray(0)
                }
                for (i in 0 until unread) {
                    buf[totalRead++] = buffer[i + 8]
                }
                sector = nextSector
                part++
            }
            buf
        } catch (_ex: IOException) {
            return ByteArray(0)
        }
    }

    @Synchronized
    private fun writeFile(bytes: ByteArray, position: Int, length: Int, exists: Boolean): Boolean {
        var exists = exists
        return try {
            var sector: Int
            if (exists) {
                seek(indexFile, position * 6)
                var input = 0
                var read = 0
                while (read < 6) {
                    input = indexFile.read(buffer, read, 6 - read)
                    if (input == -1) {
                        return false
                    }
                    read += input
                }
                sector = (buffer[3].toInt() and 0xff shl 16) + (buffer[4].toInt() and 0xff shl 8) + (buffer[5].toInt() and 0xff)
                if (sector <= 0 || sector.toLong() > dataFile.length() / 520L) {
                    return false
                }
            } else {
                sector = ((dataFile.length() + 519L) / 520L).toInt()
                if (sector == 0) {
                    sector = 1
                }
            }
            buffer[0] = (length shr 16).toByte()
            buffer[1] = (length shr 8).toByte()
            buffer[2] = length.toByte()
            buffer[3] = (sector shr 16).toByte()
            buffer[4] = (sector shr 8).toByte()
            buffer[5] = sector.toByte()
            seek(indexFile, position * 6)
            indexFile.write(buffer, 0, 6)
            var part = 0
            var written = 0
            while (written < length) {
                var nextSector = 0
                if (exists) {
                    seek(dataFile, sector * 520)
                    var read = 0
                    var `in` = 0
                    while (read < 8) {
                        `in` = dataFile.read(buffer, read, 8 - read)
                        if (`in` == -1) {
                            break
                        }
                        read += `in`
                    }
                    if (read == 8) {
                        val currentIndex: Int = (buffer[0].toInt() and 0xff shl 8) + (buffer[1].toInt() and 0xff)
                        val currentPart: Int = (buffer[2].toInt() and 0xff shl 8) + (buffer[3].toInt() and 0xff)
                        nextSector = (buffer[4].toInt() and 0xff shl 16) + (buffer[5].toInt() and 0xff shl 8) + (buffer[6].toInt() and 0xff)
                        val currentFile = (buffer[7].toInt() and 0xff)
                        if (currentIndex != position || currentPart != part || currentFile != storeIndex) {
                            return false
                        }
                        if (nextSector < 0 || nextSector.toLong() > dataFile.length() / 520L) {
                            return false
                        }
                    }
                }
                if (nextSector == 0) {
                    exists = false
                    nextSector = ((dataFile.length() + 519L) / 520L).toInt()
                    if (nextSector == 0) {
                        nextSector++
                    }
                    if (nextSector == sector) {
                        nextSector++
                    }
                }
                if (length - written <= 512) {
                    nextSector = 0
                }
                buffer[0] = (position shr 8).toByte()
                buffer[1] = position.toByte()
                buffer[2] = (part shr 8).toByte()
                buffer[3] = part.toByte()
                buffer[4] = (nextSector shr 16).toByte()
                buffer[5] = (nextSector shr 8).toByte()
                buffer[6] = nextSector.toByte()
                buffer[7] = storeIndex.toByte()
                seek(dataFile, sector * 520)
                dataFile.write(buffer, 0, 8)
                var unwritten = length - written
                if (unwritten > 512) {
                    unwritten = 512
                }
                dataFile.write(bytes, written, unwritten)
                written += unwritten
                sector = nextSector
                part++
            }
            true
        } catch (ex: IOException) {
            false
        }
    }

    @Synchronized
    fun writeFile(length: Int, data: ByteArray, index: Int): Boolean {
        return if (writeFile(data, index, length, true)) true else writeFile(data, index, length, false)
    }

    @Synchronized
    private fun seek(file: RandomAccessFile, position: Int) {
        file.seek(position.toLong())
    }

    /**
     * Returns the number of files in the cache index.
     * @return
     */
    fun getFileCount(): Long {
        if (indexFile != null) {
            return indexFile.length() / 6
        }
        return -1
    }

    enum class Store(val index: Int) {
        ARCHIVE(0),
        MODEL(1),
        ANIMATION(2),
        MUSIC(3),
        MAP(4);
    }
}
