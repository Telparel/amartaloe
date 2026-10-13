package com.amarthaloe.mod.client;

import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class FactionGuiOverlay implements LayeredDraw.Layer {
    private float displayedProgress = 0.0f;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        Font font = mc.font;

        Faction faction = ClientFactionData.getSelectedFaction();
        int rep = ClientFactionData.getReputation(faction);

        ClientFactionData.animOffset = Mth.lerp(0.18f, ClientFactionData.animOffset, 0.0f);

        // Увеличенная ширина и высота под комфортное разделение слоев текста
        int barWidth = 330;
        int barHeight = 34;
        int baseX = (screenWidth - barWidth) / 2;
        int x = baseX + (int) ClientFactionData.animOffset;
        int y = 4;

        int themeColor = getFactionHexColor(faction);
        int themeDark = getDarkerColor(themeColor);

        // --- 1. КОРПУС ПЛАШКИ ---
        guiGraphics.fill(x + 2, y, x + barWidth - 2, y + barHeight, 0xF2101114);
        guiGraphics.fill(x, y + 2, x + barWidth, y + barHeight - 2, 0xF2101114);

        // Внутренний градиент объема
        guiGraphics.fill(x + 3, y + 2, x + barWidth - 3, y + 12, 0x22FFFFFF);
        guiGraphics.fill(x + 3, y + 18, x + barWidth - 3, y + barHeight - 2, 0x44000000);

        // --- 2. МЕТАЛЛИЧЕСКАЯ РАМКА И ЗАКЛЕПКИ ---
        guiGraphics.fill(x + 2, y, x + barWidth - 2, y + 1, 0xFF4A4E58);
        guiGraphics.fill(x + 2, y + barHeight - 1, x + barWidth - 2, y + barHeight, 0xFF1E2024);
        guiGraphics.fill(x, y + 2, x + 1, y + barHeight - 2, 0xFF4A4E58);
        guiGraphics.fill(x + barWidth - 1, y + 2, x + barWidth, y + barHeight - 2, 0xFF1E2024);

        drawRivet(guiGraphics, x + 3, y + 2);
        drawRivet(guiGraphics, x + barWidth - 5, y + 2);
        drawRivet(guiGraphics, x + 3, y + barHeight - 4);
        drawRivet(guiGraphics, x + barWidth - 5, y + barHeight - 4);

        // --- 3. ЦЕНТРАЛЬНЫЙ РОМБ-ГЕРБ И ВЕРХНИЙ СЧЕТЧИК РЕПУТАЦИИ ---
        int crestX = x + (barWidth / 2);
        int crestY = y + 18;
        drawDiamondCrest(guiGraphics, crestX, crestY, themeColor, themeDark);

        // Очки репутации вынесены наверх по центру в свободное пространство
        String repStr = (rep >= 0 ? "+" : "") + rep;
        int repStrWidth = font.width(repStr);
        guiGraphics.drawString(font, repStr, crestX - (repStrWidth / 2), y + 3, (rep >= 0 ? 0xAAFFAA : 0xFFAAAA), true);

        // --- 4. НАЗВАНИЕ ФРАКЦИИ И ТИТУЛ (РАЗНЕСЕНЫ ПО КРАЯМ) ---
        // Слева: Название региона с припиской Барона
        Component factionName = faction.getFullLoreName();
        guiGraphics.drawString(font, factionName, x + 12, y + 8, 0xFFFFFF, true);

        // Справа: Ранг с учетом пола игрока
        Component rankTitle = FactionRank.getTitle(faction, rep, ClientProfileData.gender);
        int rankWidth = font.width(rankTitle);
        guiGraphics.drawString(font, rankTitle, x + barWidth - rankWidth - 12, y + 8, 0xFFFFFF, true);

        // --- 5. ДВОЙНАЯ ШКАЛА ПРОГРЕССА СНИЗУ ---
        int prev = FactionRank.getPrevTierTarget(rep);
        int next = FactionRank.getNextTierTarget(rep);
        float targetProgress = 1.0f;
        if (next != prev) {
            targetProgress = Math.max(0.0f, Math.min(1.0f, (float) (rep - prev) / (float) (next - prev)));
        }
        displayedProgress = Mth.lerp(0.12f, displayedProgress, targetProgress);

        int trackY = y + 24;
        int trackH = 4;

        int trackW = (barWidth / 2) - 30; // 135px на секцию
        int leftTrackX = x + 12;
        int rightTrackX = crestX + 18;

        guiGraphics.fill(leftTrackX, trackY, leftTrackX + trackW, trackY + trackH, 0xFF141518);
        guiGraphics.renderOutline(leftTrackX, trackY, leftTrackW, trackH, 0xFF2C2F36);

        guiGraphics.fill(rightTrackX, trackY, rightTrackX + trackW, trackY + trackH, 0xFF141518);
        guiGraphics.renderOutline(rightTrackX, trackY, rightTrackW, trackH, 0xFF2C2F36);

        int totalWidth = trackW * 2;
        int fillTotal = (int) (totalWidth * displayedProgress);

        if (fillTotal > 0) {
            int fillLeft = Math.min(fillTotal, trackW);
            guiGraphics.fill(leftTrackX + 1, trackY + 1, leftTrackX + fillLeft, trackY + trackH - 1, themeColor);

            if (fillTotal > trackW) {
                int fillRight = fillTotal - trackW;
                guiGraphics.fill(rightTrackX + 1, trackY + 1, rightTrackX + fillRight, trackY + trackH - 1, themeColor);
            }
        }

        // --- 6. НАВИГАЦИОННЫЕ СТРЕЛКИ ---
        guiGraphics.drawString(font, "«", x - 9, y + 12, 0x8A939E, true);
        guiGraphics.drawString(font, "»", x + barWidth + 3, y + 12, 0x8A939E, true);
    }

    private void drawRivet(GuiGraphics g, int rx, int ry) {
        g.fill(rx, ry, rx + 2, ry + 2, 0xFF727985);
        g.fill(rx + 1, ry + 1
