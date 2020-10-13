package com.runescape.collection

class Cacheable : Linkable() {

    var nextCacheable: Cacheable? = null
    var previousCacheable: Cacheable? = null

    fun unlinkCacheable() {
        if (previousCacheable != null) {
            previousCacheable!!.nextCacheable = nextCacheable
            nextCacheable!!.previousCacheable = previousCacheable
            nextCacheable = null
            previousCacheable = null
        }
    }

}