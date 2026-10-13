package com.amarthaloe.mod.client;

import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

public class RankBannerOverlay implements LayeredDraw.Layer {
    private static Faction activeFaction = null;
    private static int activeRep = 0;
    private static boolean isPositive = true;
    private static int displayTicks = 0;
    private static final int MAX_TICKS = 120;

    public static void trigger(Faction faction, int rep, boolean positive) {
        activeFaction = faction;
        activeRep = rep;
        isPositive = positive;
        displayTicks = MAX_TICKS;
    }

    public static void tick() {
        if (displayTicks > 0) {
            displayTicks--;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (displayTicks <= 0 || activeFaction == null) return;

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        Font font = mc.font;

        float alpha = 1.0f;
        if (displayTicks > (MAX_TICKS - 20)) {
            alpha = (float) (MAX_TICKS - displayTicks) / 20.0f;
        } else if (displayTicks < 30) {
            alpha = (float) displayTicks / 30.0f;
        }
        int aByte = Math.min(255, Math.max(0, (int) (alpha * 240)));
        if (aByte <= 4) return;

        // Расширенный баннер под длинный текст
        int bannerWidth = 320;
        int bannerHeight = 46;
        int x = (screenWidth - bannerWidth) / 2;
        int y = screenHeight / 4;

        int bgColor = (aByte << 24) | 0x0A0B0E;
        int borderColor = isPositive ? ((aByte << 24) | 0xD4AF37) : ((aByte << 24) | 0x8A1414);

        guiGraphics.fill(x, y, x + bannerWidth, y + bannerHeight, bgColor);
        guiGraphics.renderOutline(x, y, bannerWidth, bannerHeight, borderColor);
        guiGraphics.renderOutline(x + 2, y + 2, bannerWidth - 4, bannerHeight - 4, (Math.min(aByte, 70) << 24) | 0xFFFFFF);

        Component subtitle = isPositive
                ? Component.literal("— НОВЫЙ РАНГ РЕПУТАЦИИ —")
                : Component.literal("— ПАДЕНИЕ РЕПУТАЦИИ —");
        int subColor = isPositive ? ((aByte << 24) | 0xFFAA00) : ((aByte << 24) | 0xFF4444);
        int subX = (screenWidth - font.width(subtitle)) / 2;
        guiGraphics.drawString(font, subtitle, subX, y + 7, subColor, true);

        Component title = Component.literal("")
                .append(activeFaction.getFullLoreName())
                .append("§f: ")
                .append(FactionRank.getTitle(activeFaction, activeRep));

        int titleX = (screenWidth - font.width(title)) / 2;
        guiGraphics.drawString(font, title, titleX, y + 24, (aByte << 24) | 0xFFFFFF, true);
    }
}
