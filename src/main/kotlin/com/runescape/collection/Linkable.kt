package com.runescape.collection

open class Linkable {

    var key: Long = 0
    var previous: Linkable? = null
    var next: Linkable? = null

    fun unlink() {
        if (next != null) {
            next!!.previous = previous
            previous!!.next = next
            previous = null
            next = null
        }
    }
}
