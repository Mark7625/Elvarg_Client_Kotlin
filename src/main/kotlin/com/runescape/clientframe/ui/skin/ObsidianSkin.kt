/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
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
package com.runescape.clientframe.ui.skin

import com.runescape.Constants
import com.runescape.utils.FileUtils
import org.pushingpixels.substance.api.ComponentState
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle
import org.pushingpixels.substance.api.SubstanceSkin
import org.pushingpixels.substance.api.SubstanceSlices.ColorSchemeAssociationKind
import org.pushingpixels.substance.api.SubstanceSlices.DecorationAreaType
import org.pushingpixels.substance.api.colorscheme.ColorSchemeSingleColorQuery
import org.pushingpixels.substance.api.colorscheme.ColorSchemeTransform
import org.pushingpixels.substance.api.colorscheme.SubstanceColorScheme
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter
import org.pushingpixels.substance.api.painter.border.CompositeBorderPainter
import org.pushingpixels.substance.api.painter.border.DelegateBorderPainter
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter
import org.pushingpixels.substance.api.painter.fill.FractionBasedFillPainter
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter
import org.pushingpixels.substance.api.painter.overlay.BottomLineOverlayPainter
import org.pushingpixels.substance.api.painter.overlay.BottomShadowOverlayPainter
import org.pushingpixels.substance.api.painter.overlay.TopBezelOverlayPainter
import org.pushingpixels.substance.api.painter.overlay.TopLineOverlayPainter
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities
import javax.swing.AbstractButton

class ObsidianSkin : SubstanceSkin() {

    override fun getDisplayName(): String {
        return "Runelite"
    }

    /**
     * Creates a new `RuneLite` skin.
     */
    init {
        val schemes = getColorSchemes(FileUtils.getResource("Runelite.colorschemes"))
        val activeScheme = schemes["RuneLite Active"]
        val enabledScheme = schemes["RuneLite Enabled"]
        val defaultSchemeBundle = SubstanceColorSchemeBundle(activeScheme, enabledScheme, enabledScheme)
        defaultSchemeBundle.registerColorScheme(enabledScheme, 0.6f, ComponentState.DISABLED_UNSELECTED)
        defaultSchemeBundle.registerColorScheme(activeScheme, 0.6f, ComponentState.DISABLED_SELECTED)
        // borders
        val borderDisabledSelectedScheme = schemes["Runelite Selected Disabled Border"]
        val borderScheme = schemes["Runelite Border"]
        defaultSchemeBundle.registerColorScheme(borderDisabledSelectedScheme, ColorSchemeAssociationKind.BORDER, ComponentState.DISABLED_SELECTED)
        defaultSchemeBundle.registerColorScheme(borderScheme, ColorSchemeAssociationKind.BORDER)
        // marks
        val markActiveScheme = schemes["Runelite Mark Active"]
        defaultSchemeBundle.registerColorScheme(markActiveScheme, ColorSchemeAssociationKind.MARK, *ComponentState.getActiveStates())
        defaultSchemeBundle.registerColorScheme(markActiveScheme, 0.6f, ColorSchemeAssociationKind.MARK, ComponentState.DISABLED_SELECTED, ComponentState.DISABLED_UNSELECTED)
        // separators
        val separatorScheme = schemes["Runelite Separator"]
        defaultSchemeBundle.registerColorScheme(separatorScheme, ColorSchemeAssociationKind.SEPARATOR)
        // tab borders
        defaultSchemeBundle.registerColorScheme(schemes["Runelite Tab Border"], ColorSchemeAssociationKind.TAB_BORDER, *ComponentState.getActiveStates())
        val watermarkScheme = schemes["Runelite Watermark"]
        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, watermarkScheme, DecorationAreaType.NONE)
        val decorationsSchemeBundle = SubstanceColorSchemeBundle(activeScheme, enabledScheme, enabledScheme)
        decorationsSchemeBundle.registerColorScheme(enabledScheme, 0.5f, ComponentState.DISABLED_UNSELECTED)
        // borders
        decorationsSchemeBundle.registerColorScheme(borderDisabledSelectedScheme, ColorSchemeAssociationKind.BORDER, ComponentState.DISABLED_SELECTED)
        decorationsSchemeBundle.registerColorScheme(borderScheme, ColorSchemeAssociationKind.BORDER)
        // marks
        decorationsSchemeBundle.registerColorScheme(markActiveScheme, ColorSchemeAssociationKind.MARK, *ComponentState.getActiveStates())
        // separators
        val separatorDecorationsScheme = schemes["Runelite Decorations Separator"]
        decorationsSchemeBundle.registerColorScheme(separatorDecorationsScheme, ColorSchemeAssociationKind.SEPARATOR)
        val decorationsWatermarkScheme = schemes["Runelite Decorations Watermark"]
        this.registerDecorationAreaSchemeBundle(decorationsSchemeBundle, decorationsWatermarkScheme, DecorationAreaType.TOOLBAR, DecorationAreaType.GENERAL, DecorationAreaType.FOOTER
        )
        val headerSchemeBundle = SubstanceColorSchemeBundle(activeScheme, enabledScheme, enabledScheme)
        headerSchemeBundle.registerColorScheme(enabledScheme, 0.5f, ComponentState.DISABLED_UNSELECTED)
        // borders
        val headerBorderScheme = schemes["Runelite Header Border"]
        headerSchemeBundle.registerColorScheme(borderDisabledSelectedScheme, ColorSchemeAssociationKind.BORDER, ComponentState.DISABLED_SELECTED)
        headerSchemeBundle.registerColorScheme(headerBorderScheme, ColorSchemeAssociationKind.BORDER)
        // marks
        headerSchemeBundle.registerColorScheme(markActiveScheme, ColorSchemeAssociationKind.MARK, *ComponentState.getActiveStates())
        headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f, ComponentState.ROLLOVER_UNSELECTED, ComponentState.ROLLOVER_ARMED, ComponentState.ARMED)
        headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.8f, ComponentState.SELECTED)
        headerSchemeBundle.registerHighlightColorScheme(activeScheme, 1.0f, ComponentState.ROLLOVER_SELECTED)
        val headerWatermarkScheme = schemes["Runelite Header Watermark"]
        this.registerDecorationAreaSchemeBundle(headerSchemeBundle, headerWatermarkScheme, DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE, DecorationAreaType.HEADER)
        setTabFadeStart(0.2)
        setTabFadeEnd(0.9)

        addOverlayPainter(BottomShadowOverlayPainter.getInstance(), DecorationAreaType.TOOLBAR)
        addOverlayPainter(BottomShadowOverlayPainter.getInstance(), DecorationAreaType.FOOTER)

        val toolbarBottomLineOverlayPainter = BottomLineOverlayPainter(ColorSchemeSingleColorQuery { scheme: SubstanceColorScheme -> scheme.ultraDarkColor.darker() })
        addOverlayPainter(toolbarBottomLineOverlayPainter, DecorationAreaType.TOOLBAR)

        val toolbarTopLineOverlayPainter = TopLineOverlayPainter(
            ColorSchemeSingleColorQuery {
                SubstanceColorUtilities.getAlphaColor(it.foregroundColor, 32)
            }
        )

        addOverlayPainter(toolbarTopLineOverlayPainter, DecorationAreaType.TOOLBAR)

        val footerTopBezelOverlayPainter = TopBezelOverlayPainter(
            ColorSchemeSingleColorQuery { it.ultraDarkColor.darker() },
            ColorSchemeSingleColorQuery {
                SubstanceColorUtilities.getAlphaColor(it.foregroundColor, 32)
            }
        )
        addOverlayPainter(footerTopBezelOverlayPainter, DecorationAreaType.FOOTER)
        setTabFadeStart(0.18)
        setTabFadeEnd(0.18)
        // Set button shaper to use "flat" design
        buttonShaper = object : ClassicButtonShaper() {
            override fun getCornerRadius(button: AbstractButton, insets: Float): Float {
                return 0F
            }
        }
        watermark = null
        fillPainter = FractionBasedFillPainter(Constants.NAME, floatArrayOf(0.0f, 0.5f, 1.0f), arrayOf(ColorSchemeSingleColorQuery.ULTRALIGHT, ColorSchemeSingleColorQuery.LIGHT, ColorSchemeSingleColorQuery.LIGHT))
        decorationPainter = MatteDecorationPainter()
        highlightPainter = ClassicHighlightPainter()
        borderPainter = CompositeBorderPainter(Constants.NAME, ClassicBorderPainter(), DelegateBorderPainter("Runelite Inner", ClassicBorderPainter(), 0x40FFFFFF, 0x20FFFFFF, 0x00FFFFFF, ColorSchemeTransform { scheme: SubstanceColorScheme -> scheme.tint(0.2) }))
    }
}