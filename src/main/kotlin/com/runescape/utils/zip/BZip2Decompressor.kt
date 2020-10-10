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

import kotlin.experimental.and

object BZip2Decompressor {

    private val state = BZip2DecompressionState()

    fun decompress(output: ByteArray, length: Int, compressed: ByteArray, decompressedLength: Int, minLen: Int): Int {
        var length = length
        synchronized(state) {
            state.compressed = compressed
            state.nextIn = minLen
            state.decompressed = output
            state.nextOut = 0
            state.decompressedLength = decompressedLength
            state.length = length
            state.bsLive = 0
            state.bsBuff = 0
            state.totalInLo32 = 0
            state.totalInHi32 = 0
            state.totalOutLo32 = 0
            state.totalOutHigh32 = 0
            state.currentBlock = 0
            decompress(state)
            length -= state.length
            return length
        }
    }

    fun decompress(state: BZip2DecompressionState) {
        var gMinLen = 0
        var gLimit = IntArray(90)
        var gBase = IntArray(90)
        var gPerm = IntArray(90)
        state.anInt578 = 1
        if (state.tt == null) state.tt = IntArray(state.anInt578 * 0x186a0)
        var flag19 = true
        while (flag19) {
            var uc = getUnsignedChar(state)
            if (uc.toInt() == 23) return
            state.currentBlock++
            uc = getBit(state)
            state.aBoolean575 = uc.toInt() != 0
            state.randomised = 0
            uc = getUnsignedChar(state)
            state.randomised = state.randomised shl 8 or uc.toInt() and 0xff
            uc = getUnsignedChar(state)
            state.randomised = state.randomised shl 8 or uc.toInt() and 0xff
            uc = getUnsignedChar(state)
            state.randomised = state.randomised shl 8 or uc.toInt() and 0xff
            for (j in 0..15) {
                val bit = getBit(state)
                state.inUse16[j] = bit.toInt() == 1
            }
            for (k in 0..255) state.inUse[k] = false
            for (l in 0..15) if (state.inUse16[l]) {
                for (i3 in 0..15) {
                    val byte2 = getBit(state)
                    if (byte2.toInt() == 1) state.inUse[l * 16 + i3] = true
                }
            }
            makeMaps(state)
            val alphabetSize = state.nInUse + 2
            val huffmanTableCount = getBits(3, state)
            val swapCount = getBits(15, state)
            for (i1 in 0 until swapCount) {
                var count = 0
                do {
                    val byte3 = getBit(state)
                    if (byte3.toInt() == 0) break
                    count++
                } while (true)
                state.selectorMtf[i1] = count.toByte()
            }
            val pos = ByteArray(6)
            for (v in 0 until huffmanTableCount) pos[v] = v.toByte()
            for (j1 in 0 until swapCount) {
                var v = state.selectorMtf[j1]
                val tmp = pos[v.toInt()]
                while (v > 0) {
                    pos[v.toInt()] = pos[v - 1]
                    v--
                }
                pos[0] = tmp
                state.selector[j1] = tmp
            }
            for (k3 in 0 until huffmanTableCount) {
                var l6 = getBits(5, state)
                for (k1 in 0 until alphabetSize) {
                    do {
                        var byte4 = getBit(state)
                        if (byte4.toInt() == 0) break
                        byte4 = getBit(state)
                        if (byte4.toInt() == 0) l6++ else l6--
                    } while (true)
                    state.len[k3][k1] = l6.toByte()
                }
            }
            for (l3 in 0 until huffmanTableCount) {
                var byte8: Byte = 32
                var i = 0
                for (l1 in 0 until alphabetSize) {
                    if (state.len[l3][l1] > i) i = state.len[l3][l1].toInt()
                    if (state.len[l3][l1] < byte8) byte8 = state.len[l3][l1]
                }
                createDecodeTables(
                    state.limit[l3],
                    state.base[l3],
                    state.perm[l3],
                    state.len[l3],
                    byte8.toInt(),
                    i,
                    alphabetSize
                )
                state.minLens[l3] = byte8.toInt()
            }
            val l4 = state.nInUse + 1
            var i5 = -1
            var j5 = 0
            for (i2 in 0..255) state.unzftab[i2] = 0
            var j9 = 4095
            for (l8 in 15 downTo 0) {
                for (i9 in 15 downTo 0) {
                    state.mtfa[j9] = (l8 * 16 + i9).toByte()
                    j9--
                }
                state.mtfbase[l8] = j9 + 1
            }
            var i6 = 0
            if (j5 == 0) {
                i5++
                j5 = 50
                val byte12 = state.selector[i5]
                gMinLen = state.minLens[byte12.toInt()]
                gLimit = state.limit[byte12.toInt()]
                gPerm = state.perm[byte12.toInt()]
                gBase = state.base[byte12.toInt()]
            }
            j5--
            var i7 = gMinLen
            var l7: Int
            var byte9: Byte
            l7 = getBits(i7, state)
            while (l7 > gLimit.get(i7)) {
                i7++
                byte9 = getBit(state)
                l7 = l7 shl 1 or byte9.toInt()
            }
            var k5: Int = gPerm.get(l7 - gBase.get(i7))
            while (k5 != l4) {
                if (k5 == 0 || k5 == 1) {
                    var j6 = -1
                    var k6 = 1
                    do {
                        if (k5 == 0) j6 += k6 else if (k5 == 1) j6 += 2 * k6
                        k6 *= 2
                        if (j5 == 0) {
                            i5++
                            j5 = 50
                            val byte13 = state.selector[i5]
                            gMinLen = state.minLens[byte13.toInt()]
                            gLimit = state.limit[byte13.toInt()]
                            gPerm = state.perm[byte13.toInt()]
                            gBase = state.base[byte13.toInt()]
                        }
                        j5--
                        var j7 = gMinLen
                        var i8: Int
                        var byte10: Byte
                        i8 = getBits(j7, state)
                        while (i8 > gLimit[j7]) {
                            j7++
                            byte10 = getBit(state)
                            i8 = i8 shl 1 or byte10.toInt()
                        }
                        k5 = gPerm[i8 - gBase[j7]]
                    } while (k5 == 0 || k5 == 1)
                    j6++
                    val byte5 = state.seqToUnseq[(state.mtfa[state.mtfbase[0]] and 0xff.toByte()).toInt()]
                    state.unzftab[(byte5 and 0xff.toByte()).toInt()] += j6
                    while (j6 > 0) {
                        state.tt[i6] = (byte5 and 0xff.toByte()).toInt()
                        i6++
                        j6--
                    }
                } else {
                    var j11 = k5 - 1
                    var byte6: Byte
                    if (j11 < 16) {
                        val j10 = state.mtfbase[0]
                        byte6 = state.mtfa[j10 + j11]
                        while (j11 > 3) {
                            val k11 = j10 + j11
                            state.mtfa[k11] = state.mtfa[k11 - 1]
                            state.mtfa[k11 - 1] = state.mtfa[k11 - 2]
                            state.mtfa[k11 - 2] = state.mtfa[k11 - 3]
                            state.mtfa[k11 - 3] = state.mtfa[k11 - 4]
                            j11 -= 4
                        }
                        while (j11 > 0) {
                            state.mtfa[j10 + j11] = state.mtfa[j10 + j11 - 1]
                            j11--
                        }
                        state.mtfa[j10] = byte6
                    } else {
                        var l10 = j11 / 16
                        val i11 = j11 % 16
                        var k10 = state.mtfbase[l10] + i11
                        byte6 = state.mtfa[k10]
                        while (k10 > state.mtfbase[l10]) {
                            state.mtfa[k10] = state.mtfa[k10 - 1]
                            k10--
                        }
                        state.mtfbase[l10]++
                        while (l10 > 0) {
                            state.mtfbase[l10]--
                            state.mtfa[state.mtfbase[l10]] =
                                state.mtfa[state.mtfbase[l10 - 1] + 16 - 1]
                            l10--
                        }
                        state.mtfbase[0]--
                        state.mtfa[state.mtfbase[0]] = byte6
                        if (state.mtfbase[0] == 0) {
                            var i10 = 4095
                            for (k9 in 15 downTo 0) {
                                for (l9 in 15 downTo 0) {
                                    state.mtfa[i10] = state.mtfa[state.mtfbase[k9] + l9]
                                    i10--
                                }
                                state.mtfbase[k9] = i10 + 1
                            }
                        }
                    }
                    state.unzftab[(state.seqToUnseq[(byte6 and 0xff.toByte()).toInt()] and 0xff.toByte()).toInt()]++
                    state.tt[i6] = (state.seqToUnseq[(byte6 and 0xff.toByte()).toInt()] and 0xff.toByte()).toInt()
                    i6++
                    if (j5 == 0) {
                        i5++
                        j5 = 50
                        val byte14 = state.selector[i5]
                        gMinLen = state.minLens[byte14.toInt()]
                        gLimit = state.limit[byte14.toInt()]
                        gPerm = state.perm[byte14.toInt()]
                        gBase = state.base[byte14.toInt()]
                    }
                    j5--
                    var k7 = gMinLen
                    var j8: Int
                    var byte11: Byte
                    j8 = getBits(k7, state)
                    while (j8 > gLimit[k7]) {
                        k7++
                        byte11 = getBit(state)
                        j8 = j8 shl 1 or byte11.toInt()
                    }
                    k5 = gPerm[j8 - gBase[k7]]
                }
            }
            state.anInt574 = 0
            state.aByte573 = 0
            state.cftab[0] = 0
            for (j2 in 1..256) state.cftab[j2] = state.unzftab[j2 - 1]
            for (k2 in 1..256) state.cftab[k2] += state.cftab[k2 - 1]
            for (l2 in 0 until i6) {
                val byte7 = (state.tt[l2] and 0xff) as Byte
                state.tt[state.cftab[(byte7 and 0xff.toByte()).toInt()]] = state.tt[state.cftab[(byte7 and 0xff.toByte()).toInt()]] or l2 shl 8
                state.cftab[(byte7 and 0xff.toByte()).toInt()]++
            }
            state.anInt581 = state.tt[state.randomised] shr 8
            state.anInt584 = 0
            state.anInt581 = state.tt[state.anInt581]
            state.anInt582 = (state.anInt581 and 0xff).toByte().toInt()
            state.anInt581 = state.anInt581 shr 8
            state.anInt584++
            state.anInt601 = i6
            method226(state)
            flag19 = state.anInt584 == state.anInt601 + 1 && state.anInt574 == 0
        }
    }

    fun method226(state: BZip2DecompressionState) {
        var byte4 = state.aByte573
        var i = state.anInt574
        var j = state.anInt584
        var k = state.anInt582
        val ai: IntArray = state.tt
        var l = state.anInt581
        val abyte0 = state.decompressed
        var i1 = state.nextOut
        var j1 = state.length
        val k1 = j1
        val l1 = state.anInt601 + 1
        label0@ do {
            if (i > 0) {
                do {
                    if (j1 == 0) break@label0
                    if (i == 1) break
                    abyte0[i1] = byte4
                    i--
                    i1++
                    j1--
                } while (true)
                if (j1 == 0) {
                    i = 1
                    break
                }
                abyte0[i1] = byte4
                i1++
                j1--
            }
            var flag = true
            while (flag) {
                flag = false
                if (j == l1) {
                    i = 0
                    break@label0
                }
                byte4 = k.toByte()
                l = ai.get(l)
                val byte0 = (l and 0xff).toByte()
                l = l shr 8
                j++
                if (byte0.toInt() != k) {
                    k = byte0.toInt()
                    if (j1 == 0) {
                        i = 1
                    } else {
                        abyte0[i1] = byte4
                        i1++
                        j1--
                        flag = true
                        continue
                    }
                    break@label0
                }
                if (j != l1) continue
                if (j1 == 0) {
                    i = 1
                    break@label0
                }
                abyte0[i1] = byte4
                i1++
                j1--
                flag = true
            }
            i = 2
            l = ai.get(l)
            val byte1 = (l and 0xff).toByte()
            l = l shr 8
            if (++j != l1) if (byte1.toInt() != k) {
                k = byte1.toInt()
            } else {
                i = 3
                l = ai.get(l)
                val byte2 = (l and 0xff).toByte()
                l = l shr 8
                if (++j != l1) if (byte2.toInt() != k) {
                    k = byte2.toInt()
                } else {
                    l = ai.get(l)
                    val byte3 = (l and 0xff).toByte()
                    l = l shr 8
                    j++
                    i = (byte3 and 0xff.toByte()) + 4
                    l = ai[l]
                    k = (l and 0xff)
                    l = l shr 8
                    j++
                }
            }
        } while (true)
        val i2 = state.totalOutLo32
        state.totalOutLo32 += k1 - j1
        if (state.totalOutLo32 < i2) state.totalOutHigh32++
        state.aByte573 = byte4
        state.anInt574 = i
        state.anInt584 = j
        state.anInt582 = k
        state.tt = ai
        state.anInt581 = l
        state.decompressed = abyte0
        state.nextOut = i1
        state.length = j1
    }

    private fun getBits(index: Int, state: BZip2DecompressionState): Int {
        val pos: Int
        do {
            if (state.bsLive >= index) {
                val k = state.bsBuff shr state.bsLive - index and (1 shl index) - 1
                state.bsLive -= index
                pos = k
                break
            }
            state.bsBuff = state.bsBuff shl 8 or state.compressed[state.nextIn].toInt() and 0xff
            state.bsLive += 8
            state.nextIn++
            state.decompressedLength--
            state.totalInLo32++
            if (state.totalInLo32 == 0) state.totalInHi32++
        } while (true)
        return pos
    }

    private fun getUnsignedChar(state: BZip2DecompressionState): Byte {
        return getBits(8, state).toByte()
    }

    private fun getBit(state: BZip2DecompressionState): Byte {
        return getBits(1, state).toByte()
    }

    private fun makeMaps(state: BZip2DecompressionState) {
        state.nInUse = 0
        for (i in 0..255) if (state.inUse[i]) {
            state.seqToUnseq[state.nInUse] = i.toByte()
            state.nInUse++
        }
    }

    private fun createDecodeTables(limit: IntArray, base: IntArray, perm: IntArray, length: ByteArray, i: Int, maxLength: Int, alphabetSize: Int) {
        var pp = 0
        for (i1 in i..maxLength) {
            for (l2 in 0 until alphabetSize) {
                if (length[l2] == i1.toByte()) {
                    perm[pp] = l2
                    pp++
                }
            }
        }
        for (j1 in 0..22) base[j1] = 0
        for (k1 in 0 until alphabetSize) base[length[k1] + 1]++
        for (l1 in 1..22) base[l1] += base[l1 - 1]
        for (i2 in 0..22) limit[i2] = 0
        var vec = 0
        for (j2 in i..maxLength) {
            vec += base[j2 + 1] - base[j2]
            limit[j2] = vec - 1
            vec = vec shl 1
        }
        for (k2 in i + 1..maxLength) {
            base[k2] = (limit[k2 - 1] + 1 shl 1) - base[k2]
        }
    }
}
