package com.runescape.cache.defs

import com.runescape.cache.FileArchive
import com.runescape.io.Buffer
import mu.KotlinLogging

abstract class Definitions {

    private val logger = KotlinLogging.logger {}

    abstract var count: Int
    abstract val typename: String

    abstract fun init(archie : FileArchive)

    fun init(archive : FileArchive, task: () -> Unit) {
        task.invoke()
        logger.info { "Loaded: $count $typename" }
    }

}