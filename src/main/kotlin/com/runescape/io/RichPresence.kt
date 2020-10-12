package com.runescape.io


import com.runescape.Constants
import mu.KotlinLogging
import net.arikia.dev.drpc.DiscordEventHandlers
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence

object RichPresence {

    private val logger = KotlinLogging.logger {}

    val presence = DiscordRichPresence.Builder("")
    val applicationID = "765251420751790100"

    fun initialize() {

        val handlers = DiscordEventHandlers.Builder().build()
        DiscordRPC.discordInitialize(applicationID, handlers, false)
        DiscordRPC.discordRegister(applicationID, "")

        setTopText("Loading ${Constants.NAME}...")
        setLargeImage("large_logo")

        logger.info { "Discord RPC Loaded" }

    }

    fun setTopText(text : String) {
        presence.setDetails(text)
        DiscordRPC.discordUpdatePresence(presence.build())
    }

    fun setBottomText(text : String) {
        presence.build().state = text
        DiscordRPC.discordUpdatePresence(presence.build())
    }

    fun setLargeImage(key : String,tooltip : String = "") {
        presence.setBigImage(key,tooltip)
        DiscordRPC.discordUpdatePresence(presence.build())
    }

    fun setSmallImage(key : String,tooltip : String = "") {
        presence.setSmallImage(key,tooltip)
        DiscordRPC.discordUpdatePresence(presence.build())
    }


}