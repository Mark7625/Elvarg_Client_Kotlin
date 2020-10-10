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

import com.runescape.io.Buffer
import com.runescape.utils.zip.BZip2Decompressor

class FileArchive(data: ByteArray) {

    /**
     * The buffer containing the decompressed data in this Archive.
     */
    private val buffer: ByteArray

    /**
     * The amount of entries in this Archive.
     */
    private var entries = 0

    /**
     * The identifiers (i.e. hashed names) of each of the entries in this Archive.
     */
    private val identifiers: IntArray

    /**
     * The raw (i.e. decompressed) sizes of each of the entries in this Archive.
     */
    private val extractedSizes: IntArray

    /**
     * The compressed sizes of each of the entries in this Archive.
     */
    private val sizes: IntArray
    private val indices: IntArray

    /**
     * Whether or not this Archive was compressed as a whole: if false, decompression will be performed on each of the
     * individual entries.
     */
    private var extracted = false

    /**
     * Initialize the archive.
     */
    init {
        var buffer = Buffer(data)

        val decompressedLength: Int = buffer.readTriByte()
        val compressedLength: Int = buffer.readTriByte()

        if (compressedLength != decompressedLength) {
            val output = ByteArray(decompressedLength)
            BZip2Decompressor.decompress(output, decompressedLength, data, compressedLength, 6)
            this.buffer = output
            buffer = Buffer(this.buffer)
            extracted = true
        } else {
            this.buffer = data
            extracted = false
        }
        entries = buffer.readUShort()
        identifiers = IntArray(entries)
        extractedSizes = IntArray(entries)
        sizes = IntArray(entries)
        indices = IntArray(entries)
        var offset: Int = buffer.currentPosition + entries * 10
        for (file in 0 until entries) {
            identifiers[file] = buffer.readInt()
            extractedSizes[file] = buffer.readTriByte()
            sizes[file] = buffer.readTriByte()
            indices[file] = offset
            offset += sizes[file]
        }
    }

    /**
     * Read the hash of the file
     */
    fun getHash(name: String): Int {
        var hash = 0
        val file = name.toUpperCase()
        for (element in file) {
            hash = hash * 61 + element.toInt() - 32
        }
        for (file in 0 until entries) {
            if (identifiers[file] === hash) {
                return hash
            }
        }
        return -1
    }

    /**
     * Read the file by name
     */
    fun readFile(name: String): ByteArray {
        var output: ByteArray? = null
        var hash = 0
        val name = name.toUpperCase()
        for (element in name) {
            hash = hash * 61 + element.toInt() - 32
        }
        for (file in 0 until entries) {
            if (identifiers[file] === hash) {
                if (output == null) {
                    output = ByteArray(extractedSizes[file])
                }
                if (!extracted) {
                    BZip2Decompressor.decompress(output, extractedSizes[file], buffer, sizes[file], indices[file])
                } else {
                    System.arraycopy(buffer, indices[file], output, 0, extractedSizes[file])
                }
                return output
            }
        }
        return ByteArray(0)
    }
}
