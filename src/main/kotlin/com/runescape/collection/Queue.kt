package com.runescape.collection

class Queue {

    private val head: Cacheable = Cacheable()

    private var current: Cacheable? = null
    fun insertHead(node: Cacheable) {
        if (node.previousCacheable != null) node.unlinkCacheable()
        node.previousCacheable = head.previousCacheable
        node.nextCacheable = head
        node.previousCacheable!!.nextCacheable = node
        node.nextCacheable!!.previousCacheable = node
    }

    fun popTail(): Cacheable? {
        val next = head.nextCacheable
        return if (next == head) {
            null
        } else {
            next!!.unlinkCacheable()
            next
        }
    }

    fun reverseGetFirst(): Cacheable? {
        val nodeSub = head.nextCacheable
        return if (nodeSub == head) {
            current = null
            null
        } else {
            current = nodeSub!!.nextCacheable
            nodeSub
        }
    }

    fun reverseGetNext(): Cacheable? {
        val next = current
        return if (next == head) {
            current = null
            null
        } else {
            current = next!!.nextCacheable
            next
        }
    }

    fun size(): Int {
        var count = 0
        var nodeSub = head.nextCacheable
        while (nodeSub != head) {
            count++
            nodeSub = nodeSub!!.nextCacheable
        }
        return count
    }

    init {
        head.nextCacheable = head
        head.previousCacheable = head
    }

}