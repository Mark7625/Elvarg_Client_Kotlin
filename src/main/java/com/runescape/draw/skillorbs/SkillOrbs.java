package com.runescape.draw.skillorbs;

import com.runescape.ClientKT;
import com.runescape.draw.Rasterizer2D;
import com.runescape.util.SkillConstants;

import java.awt.*;

/**
 * Handles skill orbs.
 *
 * @author Professor Oak
 * @author Christian_
 */
public class SkillOrbs {

    /**
     * The array containing all skill orbs.
     * Each skill orb per available skill.
     */
    public static final SkillOrb[] orbs = new SkillOrb[SkillConstants.SKILL_COUNT];

    public static Graphics2D g2d;

    /**
     * Initializes orbs and their sprites.
     */
    public static void init() {

        for (int i = 0; i < SkillConstants.SKILL_COUNT; i++) {
            orbs[i] = new SkillOrb(i, ClientKT.spriteCache.lookup(361 + i));
        }
    }

    /**
     * Processes all orbs.
     */
    public static void process() {

        g2d = Rasterizer2D.createGraphics(true);

        // Our counter
        int totalOrbs = 0;

        // Count valid orbs..
        for (SkillOrb orb : orbs) {
            if (draw(orb)) {
                totalOrbs++;
            }
        }

        // If the bounty hunter interface open, then orbs may need to be re-positioned
        final boolean blockingInterfaceOpen = ClientKT.instance.openWalkableInterface == 23300;
        boolean hpOverlay = ClientKT.instance.shouldDrawCombatBox();

        // Positioning of orbs
        int y = 12;
        int x = ClientKT.frameMode == ClientKT.ScreenMode.FIXED ? (int) (ClientKT.frameWidth / 3.1) - (totalOrbs * 30) : (ClientKT.frameWidth / 2) - (totalOrbs * 30);

        if (blockingInterfaceOpen) {
            x -= (totalOrbs * 10);
        } else {
            if (hpOverlay) {
                if (x < 130) {
                    x = 130;
                }
                y = 12;
            }
        }

        if (x < 5) {
            x = 5;
        }

        // Current skillorb hover
        SkillOrb hover = null;

        // Draw orbs and get current hover...
        for (SkillOrb orb : orbs) {
            if (draw(orb)) {

                // Fade orb if needed
                if (orb.getShowTimer().finished()) {
                    orb.decrementAlpha();
                }

                // Draw orb
                orb.draw(x, y);

                // Check if this orb is being hovered
                if (ClientKT.instance.hover(x, y, ClientKT.spriteCache.lookup(359))) {
                    hover = orb;
                }

                // Increase x, space between orbs
                x += 62;

                int xLimit = ClientKT.frameMode == ClientKT.ScreenMode.FIXED ? 300 : ClientKT.frameWidth - 203;
                if (x > (blockingInterfaceOpen ? xLimit : xLimit + 160)) {
                    break;
                }
            }
        }

        // Draw hover tooltip
        if (hover != null) {
            hover.drawTooltip();
        }
        g2d.dispose();
    }

    /**
     * Should a skillorb be drawn?
     *
     * @param orb
     * @return
     */
    private static boolean draw(SkillOrb orb) {
        return !orb.getShowTimer().finished() || orb.getAlpha() > 0;
    }
}
