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
package com.runescape.io

class Buffer(val payload: ByteArray) {

    var currentPosition = 0

    init {
        currentPosition = 0
    }

    fun readTriByte(): Int {
        currentPosition += 3
        return (
            (payload[currentPosition - 3].toInt() and 0xff shl 16) +
                (payload[currentPosition - 2].toInt() and 0xff shl 8) +
                (payload[currentPosition - 1].toInt() and 0xff)
            )
    }

    fun readUShort(): Int {
        currentPosition += 2
        return (
            (payload[currentPosition - 2].toInt() and 0xff shl 8) +
                (payload[currentPosition - 1].toInt() and 0xff)
            )
    }

    fun readInt(): Int {
        currentPosition += 4
        return (
            (payload[currentPosition - 4].toInt() and 0xff shl 24) +
                (payload[currentPosition - 3].toInt() and 0xff shl 16) +
                (payload[currentPosition - 2].toInt() and 0xff shl 8) +
                (payload[currentPosition - 1].toInt() and 0xff)
            )
    }
}
