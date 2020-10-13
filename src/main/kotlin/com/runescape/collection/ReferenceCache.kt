package com.runescape.collection


/**
 * A least-recently used cache of references, backed by a [HashTable] and a [Queue].
 */
class ReferenceCache(index: Int) {
    /**
     * The empty cacheable.
     */
    private val empty: Cacheable = Cacheable()
    /**
     * The capacity of this cache.
     */
    private val capacity: Int = index
    /**
     * The HashTable backing this cache.
     */
    private val table: HashTable = HashTable()
    /**
     * The queue of references, used for LRU behaviour.
     */
    private val references: Queue = Queue()
    /**
     * The amount of unused slots in this cache.
     */
    private var spaceLeft: Int = index

    /**
     * Gets the [Cacheable] with the specified key.
     *
     * @param key The key.
     * @return The Cacheable.
     */
    operator fun get(key: Long): Cacheable? {
        val cacheable: Cacheable = table[key] as Cacheable
        if (cacheable != null) {
            references.insertHead(cacheable)
        }
        return cacheable
    }

    fun put(node: Cacheable, key: Long) {
        try {
            if (spaceLeft == 0) {
                var front: Cacheable = references.popTail()!!
                front.unlink()
                front.unlinkCacheable()
                if (front === empty) {
                    front = references.popTail()!!
                    front.unlink()
                    front.unlinkCacheable()
                }
            } else {
                spaceLeft--
            }
            table.put(node, key)
            references.insertHead(node)
            return
        } catch (runtimeexception: RuntimeException) {
            println("47547, " + node + ", " + key + ", " + 2.toByte() + ", " + runtimeexception.toString())
        }
        throw RuntimeException()
    }

    /**
     * Clears the contents of this ReferenceCache.
     */
    fun clear() {
        do {
            val front: Cacheable = references.popTail()!!
            if (front != null) {
                front.unlink()
                front.unlinkCacheable()
            } else {
                spaceLeft = capacity
                return
            }
        } while (true)
    }

}
