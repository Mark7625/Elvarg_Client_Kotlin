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
package com.runescape.utils.zip

class BZip2DecompressionState {

    lateinit var tt: IntArray
    var unzftab: IntArray = IntArray(256)
    var cftab: IntArray = IntArray(257)
    var inUse: BooleanArray = BooleanArray(256)
    var inUse16: BooleanArray = BooleanArray(16)
    var seqToUnseq: ByteArray = ByteArray(256)
    var mtfa: ByteArray = ByteArray(4096)
    var mtfbase: IntArray = IntArray(16)
    var selector: ByteArray = ByteArray(18002)
    var selectorMtf: ByteArray = ByteArray(18002)
    var len: Array<ByteArray> = Array(6) { ByteArray(258) }
    var limit: Array<IntArray> = Array(6) { IntArray(258) }
    var base: Array<IntArray> = Array(6) { IntArray(258) }
    var perm: Array<IntArray> = Array(6) { IntArray(258) }
    var minLens: IntArray = IntArray(6)
    lateinit var compressed: ByteArray
    var nextIn = 0
    var decompressedLength = 0
    var totalInLo32 = 0
    var totalInHi32 = 0
    lateinit var decompressed: ByteArray
    var nextOut = 0
    var length = 0
    var totalOutLo32 = 0
    var totalOutHigh32 = 0
    var aByte573: Byte = 0
    var anInt574 = 0
    var aBoolean575 = false
    var bsBuff = 0
    var bsLive = 0
    var anInt578 = 0
    var currentBlock = 0
    var randomised = 0
    var anInt581 = 0
    var anInt582 = 0
    var anInt584 = 0
    var nInUse = 0
    var anInt601 = 0
}
