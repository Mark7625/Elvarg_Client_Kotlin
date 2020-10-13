package com.runescape.collection

internal class HashTable {

    private val bucketCount: Int
    private val buckets: Array<Linkable?>
    /**
     * Gets the [Linkable] with the specified `key` from this
     * HashTable.
     *
     * @param key The key.
     * @return The Linkable, or `null` if this HashTable does not contain
     * an associated for the specified key.
     */
    operator fun get(key: Long): Linkable? {
        val linkable: Linkable? = buckets[(key and (bucketCount - 1).toLong()).toInt()]
        var next: Linkable = linkable!!.previous!!
        while (next !== linkable) {
            if (next.key === key) return next
            next = next.previous!!
        }
        return null
    }

    /**
     * Associates the specified [Linkable] with the specified `key`.
     *
     * @param key      The key.
     * @param linkable The Linkable.
     */
    fun put(linkable: Linkable, key: Long) {
        try {
            if (linkable.next != null) linkable.unlink()
            val current: Linkable? = buckets[(key and (bucketCount - 1).toLong()).toInt()]
            linkable.next = current!!.next
            linkable.previous = current
            linkable.next!!.previous = linkable
            linkable.previous!!.next = linkable
            linkable.key = key
            return
        } catch (runtimeexception: RuntimeException) {
            println(
                "91499, " + linkable + ", " + key + ", " +
                    7.toByte() + ", " + runtimeexception.toString()
            )
        }
        throw RuntimeException()
    }

    /**
     * Creates the HashTable with the specified size.
     */
    init {
        val size = 1024 // was parameter
        bucketCount = size
        buckets = arrayOfNulls<Linkable>(size)
        for (index in 0 until size) {
            buckets[index] = Linkable()
            val node: Linkable? = buckets[index]
            node!!.previous = node
            node!!.next = node
        }
    }
}
