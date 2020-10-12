/*
 * Copyright (c) 2019 Abex
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
package com.runescape.clientframe.ui

import com.apple.eawt.Application
import com.runescape.Constants
import com.runescape.clientframe.ui.skin.SubstanceRuneLiteLookAndFeel
import com.runescape.utils.FileUtils
import org.pushingpixels.substance.internal.SubstanceSynapse
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.lang.reflect.InvocationTargetException
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.plaf.basic.BasicProgressBarUI

class SplashScreen : JFrame(), ActionListener {

    private val action = JLabel("Loading")
    private val progress = JProgressBar()
    private val subAction = JLabel()
    private val timer: Timer
    private var overallProgress = 0
    private var actionText = "Loading"
    private var subActionText = ""
    private var progressText: String = ""
    private val WIDTH = 200
    private val PAD = 10

    override fun actionPerformed(e: ActionEvent) {
        action.text = actionText
        subAction.text = subActionText
        progress.maximum = 1000
        progress.value = overallProgress
        val progressText = progressText
        if (progressText.isEmpty()) {
            progress.isStringPainted = false
        } else {
            progress.isStringPainted = true
            progress.string = progressText
        }
    }


    fun init() {
        try {
            SwingUtilities.invokeAndWait {
                try {
                    val hasLAF = UIManager.getLookAndFeel() is SubstanceRuneLiteLookAndFeel
                    if (!hasLAF) {
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
                    }
                    if (hasLAF) {
                        getRootPane().putClientProperty(SubstanceSynapse.COLORIZATION_FACTOR, 1.0)
                    }
                } catch (e: Exception) {
                    println(e)
                }
            }
        } catch (bs: InterruptedException) {
            throw RuntimeException(bs)
        } catch (bs: InvocationTargetException) {
            throw RuntimeException(bs)
        }
    }

    fun stop() {
        SwingUtilities.invokeLater {
            timer.stop()
            dispose()
        }
    }

    fun stage(overallProgress: Int, actionText: String, subActionText: String, progressText: String) {
            this.overallProgress = overallProgress
            if (actionText.isNotEmpty()) {
                this.actionText = actionText
            }
            this.subActionText = subActionText
            this.progressText = progressText
    }

    init {
        title = "${Constants.NAME}"
        defaultCloseOperation = EXIT_ON_CLOSE
        isUndecorated = true
        layout = null

        var iconImages: Array<Image> = arrayOf(
            ImageIO.read(FileUtils.getResource("16x16.png")),
            ImageIO.read(FileUtils.getResource("32x32.png")),
            ImageIO.read(FileUtils.getResource("64x64.png")),
            ImageIO.read(FileUtils.getResource("128x128.png"))
        )

        this.iconImages = iconImages.toMutableList()

        val pane = contentPane
        pane.background = ColorScheme.DARKER_GRAY_COLOR
        val font = Font(Font.DIALOG, Font.PLAIN, 12)
        val logoLabel = JLabel(ImageIcon(ImageIO.read(FileUtils.getResource("200x200.png"))))
        pane.add(logoLabel)
        logoLabel.setBounds(0, 0, 200, 200)
        var y = 200
        pane.add(action)
        action.foreground = Color.WHITE
        action.setBounds(0, y, 200, 16)
        action.horizontalAlignment = SwingConstants.CENTER
        action.font = font
        y += action.height + PAD
        pane.add(progress)
        progress.foreground = ColorScheme.BRAND_ORANGE
        progress.background = ColorScheme.BRAND_ORANGE.darker().darker()
        progress.border = EmptyBorder(0, 0, 0, 0)
        progress.setBounds(0, y, JFrame.WIDTH, 14)
        progress.font = font

        progress.setUI(object : BasicProgressBarUI() {
            override fun getSelectionBackground(): Color {
                return Color.BLACK
            }

            override fun getSelectionForeground(): Color {
                return Color.BLACK
            }
        })

        y += 12 + PAD
        pane.add(subAction)
        subAction.foreground = Color.LIGHT_GRAY
        subAction.setBounds(0, y, 200, 16)
        subAction.horizontalAlignment = SwingConstants.CENTER
        subAction.font = font
        y += subAction.height + PAD
        setSize(200, y)
        setLocationRelativeTo(null)
        timer = Timer(100, this)
        timer.isRepeats = true
        timer.start()
        isVisible = true
    }

}