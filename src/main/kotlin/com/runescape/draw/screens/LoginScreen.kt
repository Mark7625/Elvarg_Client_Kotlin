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
package com.runescape.draw.screens

import com.runescape.Client
import com.runescape.draw.Rasterizer2D
import com.runescape.draw.Sprite.Action
import com.runescape.draw.fonts.FontType
import com.runescape.draw.fonts.RSFont.Companion.drawBasicString
import com.runescape.draw.fonts.RSFont.Companion.drawCenteredString
import com.runescape.io.RichPresence
import com.runescape.utils.StringUtils

class LoginScreen(private val client: Client) {

    private var state: State = State.START

    private enum class State() {
        START, LOGIN, REGISTER, WORLD_SELECT, TWO_FACTOR
    }

    fun renderScreen() {
        RichPresence.setTopText("Login Screen")
        val centerX = client.frameWidth / 2 - 45
        val centerY = client.frameHeight / 2
        client.setupLoginScreen()
        client.loginBoxImageProducer.initDrawingArea()

        client.spriteCache.lookup(0).drawSprite(0, 0)

        when (state) {
            State.START -> {
                client.spriteCache.lookup(1).drawSprite(203, 152)
                client.spriteCache.lookup(2).drawSprite(227, 252, Action.CLICK) { state = State.REGISTER }
                client.spriteCache.lookup(2).drawSprite(388, 252, Action.CLICK) { state = State.LOGIN }

                FontType.BOLD.drawBasicString("Welcome to Elvarg", 320, 212, 0xffff00, 1)
                FontType.BOLD.drawBasicString("New User", 270, 276, 0xffffff, 1)
                FontType.BOLD.drawBasicString("Existing User", 415, 276, 0xffffff, 1)
            }
            State.LOGIN -> {
                if (WorldSelector.data == null) {
                    WorldSelector.load()
                    client.currentWorld = WorldSelector.data!!.worlds[0]
                }

                client.spriteCache.lookup(1).drawSprite(203, 152)
                client.spriteCache.lookup(2).drawSprite(388, 282, Action.CLICK) { state = State.START }

                client.spriteCache.lookup(2).drawSprite(227, 282, Action.CLICK) {
                    client.login(client.myUsername, client.myPassword, client.myPin, false)
                }

                client.spriteCache.lookup(26).drawSprite(723, 463)

                if (client.firstLoginMessage.isNotEmpty()) {
                    FontType.BOLD.drawCenteredString(client.firstLoginMessage, 350, 210 - 15, 0xffff00, 1)
                    FontType.BOLD.drawCenteredString(client.secondLoginMessage, 350, 210, 0xffff00, 1)
                } else {
                    FontType.BOLD.drawCenteredString(client.secondLoginMessage, 350, 210 - 7, 0xffff00, 1)
                }

                FontType.BOLD.drawBasicString("Login: " + client.myUsername + flash(0), 290, 240 - 7, 0xffffff, 1)
                FontType.BOLD.drawBasicString("Password: " + client.myPassword + flash(1), 294, 257 - 7, 0xffffff, 1)

                FontType.BOLD.drawBasicString("Login", 280, 306, 0xffffff, 1)
                FontType.BOLD.drawBasicString("Cancel", 440, 306, 0xffffff, 1)

                client.spriteCache.lookup(3).drawSprite(9, 464, client.spriteCache.lookup(4), Action.CLICK) {
                    state = State.WORLD_SELECT
                }

                FontType.BOLD.drawCenteredString(client.currentWorld!!.name, 77, 485, 0xFFFFFF, 1)
            }
            State.REGISTER -> {}
            State.TWO_FACTOR -> {}
            State.WORLD_SELECT -> {

                Rasterizer2D.drawTransparentBox(0, 44, 764, 414, 0x000000, 150)

                WorldSelector.data!!.worlds.forEach {
                    client.spriteCache.lookup(6).drawSprite(centerX, centerY, Action.CLICK) {
                        client.currentWorld = it
                        state = State.LOGIN
                    }
                    client.spriteCache.lookup(it.icon.sprite).drawSprite(centerX + 25, centerY)
                    FontType.SMALL.drawBasicString(it.name, centerX + 3, centerY + 13, 0x000000, 0)
                    FontType.SMALL.drawCenteredString("0", centerX + 57, centerY + 14, 0xFFFFFF, 0)
                    if (client.mouseInRegion(centerX, centerY, client.spriteCache.lookup(6))) {
                        client.drawHoverBox(client.mouseX - 38, client.mouseY + 20, it.text)
                    }
                    client.spriteCache.lookup(24).drawAdvancedSprite(332, 464, client.spriteCache.lookup(25), Action.CLICK) {
                        state = State.LOGIN
                    }
                    FontType.BOLD.drawBasicString("Back to Login", 337, 485, 0xFFFFFF, 1)
                }
            }
        }
        client.spriteCache.lookup(5).drawAdvancedSprite(349, 0)
        client.loginBoxImageProducer.drawGraphics(0, 0, client.graphics)
    }

    private fun flash(state: Int): String = if ((client.loginScreenCursorPos == state) and (client.tick % 40 < 20)) "@yel@|" else ""

    fun processInput() {
        do {
            val typed = client.key
            if (typed == -1) {
                break
            }
            var valid = false
            if (client.loginScreenCursorPos == 0 || client.loginScreenCursorPos == 1) {
                for (key in validUserPassChars) {
                    if (typed != key.toInt()) {
                        continue
                    }
                    valid = true
                    break
                }
            } else {
                for (key in validUserPinChars) {
                    if (typed != key.toInt()) {
                        continue
                    }
                    valid = true
                    break
                }
            }
            if (client.loginScreenCursorPos == 0) {
                if (typed == 8 && client.myUsername.isNotEmpty()) {
                    client.myUsername = client.myUsername.substring(0, client.myUsername.length - 1)
                } else if (typed == 9 || typed == 10 || typed == 13) {
                    client.loginScreenCursorPos = 1
                }
                if (valid) {
                    client.myUsername += typed.toChar()
                }
                if (client.myUsername.length > 16) {
                    client.myUsername = StringUtils.capitalize(client.myUsername.substring(0, 16))
                }
            } else if (client.loginScreenCursorPos == 1) {
                if (typed == 8 && client.myPassword.isNotEmpty()) {
                    client.myPassword = client.myPassword.substring(0, client.myPassword.length - 1)
                }
                if (typed == 9 || typed == 10 || typed == 13) {
                    client.loginScreenCursorPos = 0
                }
                if (valid) {
                    client.myPassword += typed.toChar()
                }
                if (client.myPassword.length > 20) {
                    client.myPassword = client.myPassword.substring(0, 20)
                }
            } else if (client.loginScreenCursorPos == 2) {
                if (typed == 8 && client.myPin.isNotEmpty()) {
                    client.myPin = client.myPin.substring(0, client.myPin.length - 1)
                }
                if (typed == 9 || typed == 10 || typed == 13) {
                    client.login(client.myUsername, client.myPassword, client.myPin, false)
                }
                if (valid) {
                    client.myPin += typed.toChar()
                }
                if (client.myPin.length > 6) {
                    client.myPin = client.myPin.substring(0, 6)
                }
            }
        } while (true)
        return
    }

    private val validUserPinChars = "1234567890"
    private val validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\u00a3$%^&*()-_=+[{]};:'@#~,<.>/?\\| "
}
