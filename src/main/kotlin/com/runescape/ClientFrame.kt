
package com.runescape

import com.apple.eawt.Application
import com.runescape.utils.FileUtils
import java.awt.Color
import java.awt.Dimension
import java.awt.Frame
import java.awt.Graphics
import java.awt.Image
import java.awt.Insets
import javax.imageio.ImageIO

class ClientFrame(gameStub: ClientEngine, gameFrameWidth: Int, gameFrameHeight: Int, undecorative: Boolean, resizable: Boolean) : Frame() {

    val os = System.getProperty("os.name").toLowerCase()

    var screenSize: Dimension = toolkit.screenSize
    var screenWidth = screenSize.getWidth().toInt()
    var screenHeight = screenSize.getHeight().toInt()
    var clientinsets: Insets

    private val clientStub: ClientEngine = gameStub

    init {
        title = "Elvarg"
        isResizable = resizable
        isUndecorated = undecorative

        isVisible = true
        clientinsets = insets
        setSize(gameFrameWidth + insets.left + insets.right, gameFrameHeight + insets.top + insets.bottom) // Sets the size.

        var iconImages: Array<Image> = arrayOf(
            ImageIO.read(FileUtils.getResource("16x16.png")),
            ImageIO.read(FileUtils.getResource("32x32.png")),
            ImageIO.read(FileUtils.getResource("64x64.png")),
            ImageIO.read(FileUtils.getResource("128x128.png"))
        )

        if (isWindows()) {
            this.iconImages = iconImages.toMutableList()
        } else if (isMac()) {
            val application: Application = Application.getApplication()
            application.dockIconImage = iconImages[3]
        }

        if (!isLinux()) {
            setLocation((screenWidth - gameFrameWidth) / 2, (screenHeight - gameFrameHeight) / 2) // Sets the location to middle of the screen.
            setLocationRelativeTo(null) // Sets the location of the window relative to the specified component.
        }

        background = Color.BLACK
        requestFocus()
        toFront()
    }

    fun isWindows() = os.indexOf("win") >= 0

    fun isMac() = os.indexOf("mac") >= 0

    fun isLinux() = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0

    /**
     * Gets the game height.
     * @return The game height.
     */
    fun getContentHeight(): Int = height - (clientinsets.top + clientinsets.bottom)

    /**
     * Gets the frame size.
     * @return The frame size.
     */
    fun getContentSize(): Dimension {
        return Dimension(
            this.size.width - (insets.left + insets.right),
            this.size.height - (insets.top + insets.bottom)
        )
    }

    /**
     * Gets the frame width.
     * @return The frame width.
     */
    fun getContentWidth(): Int = width - (clientinsets.left + clientinsets.right)

    /**
     * Gets the graphics.
     * @return graphics
     */
    override fun getGraphics(): Graphics {
        val graphics: Graphics = super.getGraphics()
        graphics.fillRect(0, 0, width, height)
        graphics.translate(if (insets != null) insets.left else 0, if (insets != null) insets.top else 0)
        return graphics
    }

    /**
     * Paint the graphics that was gathered.
     * @param graphics
     */
    override fun paint(graphics: Graphics) = clientStub.paint(graphics)

    /**
     * Update the graphics that was painted.
     * @param graphics
     */
    override fun update(graphics: Graphics) = clientStub.update(graphics)
}
