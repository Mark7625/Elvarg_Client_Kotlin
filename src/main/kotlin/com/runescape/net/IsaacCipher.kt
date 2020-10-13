package com.runescape.net

class IsaacCipher(seed: IntArray) {

    private val results: IntArray = IntArray(256)
    private val memory: IntArray = IntArray(256)
    private var count = 0
    private var accumulator = 0
    private var lastResult = 0
    private var counter = 0

    val nextKey: Int
        get() {
            if (count-- == 0) {
                isaac()
                count = 255
            }
            return results[count]
        }

    private fun isaac() {
        lastResult += ++counter
        for (i in 0..255) {
            val j = memory[i]
            when {
                i and 3 == 0 -> accumulator = accumulator xor accumulator shl 13
                i and 3 == 1 ->
                    accumulator =
                        accumulator xor accumulator ushr 6
                i and 3 == 2 ->
                    accumulator =
                        accumulator xor accumulator shl 2
                i and 3 == 3 ->
                    accumulator =
                        accumulator xor accumulator ushr 16
            }
            accumulator += memory[i + 128 and 0xff]
            var k: Int
            k = memory[j and 0x3fc shr 2] + accumulator + lastResult
            memory[i] = k
            lastResult = memory[k shr 8 and 0x3fc shr 2] + j
            results[i] = lastResult
        }
    }

    private fun initializeKeySet() {
        var i1: Int
        var j1: Int
        var k1: Int
        var l1: Int
        var i2: Int
        var j2: Int
        var k2: Int
        k2 = -0x61c88647
        j2 = k2
        i2 = j2
        l1 = i2
        k1 = l1
        j1 = k1
        i1 = j1
        var l = i1
        for (i in 0..3) {
            l = l xor i1 shl 11
            k1 += l
            i1 += j1
            i1 = i1 xor j1 ushr 2
            l1 += i1
            j1 += k1
            j1 = j1 xor k1 shl 8
            i2 += j1
            k1 += l1
            k1 = k1 xor l1 ushr 16
            j2 += k1
            l1 += i2
            l1 = l1 xor i2 shl 10
            k2 += l1
            i2 += j2
            i2 = i2 xor j2 ushr 4
            l += i2
            j2 += k2
            j2 = j2 xor k2 shl 8
            i1 += j2
            k2 += l
            k2 = k2 xor l ushr 9
            j1 += k2
            l += i1
        }
        var j = 0
        while (j < 256) {
            l += results[j]
            i1 += results[j + 1]
            j1 += results[j + 2]
            k1 += results[j + 3]
            l1 += results[j + 4]
            i2 += results[j + 5]
            j2 += results[j + 6]
            k2 += results[j + 7]
            l = l xor i1 shl 11
            k1 += l
            i1 += j1
            i1 = i1 xor j1 ushr 2
            l1 += i1
            j1 += k1
            j1 = j1 xor k1 shl 8
            i2 += j1
            k1 += l1
            k1 = k1 xor l1 ushr 16
            j2 += k1
            l1 += i2
            l1 = l1 xor i2 shl 10
            k2 += l1
            i2 += j2
            i2 = i2 xor j2 ushr 4
            l += i2
            j2 += k2
            j2 = j2 xor k2 shl 8
            i1 += j2
            k2 += l
            k2 = k2 xor l ushr 9
            j1 += k2
            l += i1
            memory[j] = l
            memory[j + 1] = i1
            memory[j + 2] = j1
            memory[j + 3] = k1
            memory[j + 4] = l1
            memory[j + 5] = i2
            memory[j + 6] = j2
            memory[j + 7] = k2
            j += 8
        }
        var k = 0
        while (k < 256) {
            l += memory[k]
            i1 += memory[k + 1]
            j1 += memory[k + 2]
            k1 += memory[k + 3]
            l1 += memory[k + 4]
            i2 += memory[k + 5]
            j2 += memory[k + 6]
            k2 += memory[k + 7]
            l = l xor i1 shl 11
            k1 += l
            i1 += j1
            i1 = i1 xor j1 ushr 2
            l1 += i1
            j1 += k1
            j1 = j1 xor k1 shl 8
            i2 += j1
            k1 += l1
            k1 = k1 xor l1 ushr 16
            j2 += k1
            l1 += i2
            l1 = l1 xor i2 shl 10
            k2 += l1
            i2 += j2
            i2 = i2 xor j2 ushr 4
            l += i2
            j2 += k2
            j2 = j2 xor k2 shl 8
            i1 += j2
            k2 += l
            k2 = k2 xor l ushr 9
            j1 += k2
            l += i1
            memory[k] = l
            memory[k + 1] = i1
            memory[k + 2] = j1
            memory[k + 3] = k1
            memory[k + 4] = l1
            memory[k + 5] = i2
            memory[k + 6] = j2
            memory[k + 7] = k2
            k += 8
        }
        isaac()
        count = 256
    }

    init {
        System.arraycopy(seed, 0, results, 0, seed.size)
        initializeKeySet()
    }
}
