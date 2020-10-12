package com.runescape.clientframe

import com.runescape.clientframe.ui.ClientUI
import com.runescape.clientframe.ui.SplashScreen
import com.runescape.io.RichPresence
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

class ApplicationFrame {

    private val logger = KotlinLogging.logger {}

    val splash : SplashScreen = SplashScreen()
    lateinit var clientUI : ClientUI

    fun launch() {

        val time = measureTimeMillis {

            splash.init()
            splash.stage(10, "Starting Client","","")
            start()

        }
        logger.info { "Client initialization took ${time}ms." }
    }

    private fun start() {
        splash.stage(75, "", "Starting core interface","")

        // Initialize UI
        clientUI = ClientUI()
        clientUI.init()

        //Discord
        RichPresence.initialize()

        //clientUI.show()

    }

}

fun main() {
    ApplicationFrame().launch()
}