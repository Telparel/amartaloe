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

        // Плавная физика качания при переключении (Lerp)
        ClientFactionData.animOffset = Mth.lerp(0.18f, ClientFactionData.animOffset, 0.0f);

        int barWidth = 204;
        int barHeight = 26;
        int baseX = (screenWidth - barWidth) / 2;
        int x = baseX + (int) ClientFactionData.animOffset;
        int y = 4;

        int themeColor = getFactionHexColor(faction);
        int themeDark = getDarkerColor(themeColor);

        // --- 1. ТЕМНАЯ ПОДЛОЖКА СО СКОШЕННЫМИ УГЛАМИ ---
        // Основной корпус
        guiGraphics.fill(x + 2, y, x + barWidth - 2, y + barHeight, 0xF0101114);
        guiGraphics.fill(x, y + 2, x + barWidth, y + barHeight - 2, 0xF0101114);

        // Внутренний темный градиент для эффекта глубины
        guiGraphics.fill(x + 3, y + 2, x + barWidth - 3, y + 10, 0x33FFFFFF);
        guiGraphics.fill(x + 3, y + 14, x + barWidth - 3, y + barHeight - 2, 0x55000000);

        // --- 2. МЕТАЛЛИЧЕСКАЯ ОКАНТОВКА И ЗАКЛЕПКИ (LOTR РАМКА) ---
        // Верхний и нижний железный кант
        guiGraphics.fill(x + 2, y, x + barWidth - 2, y + 1, 0xFF4A4E58);
        guiGraphics.fill(x + 2, y + barHeight - 1, x + barWidth - 2, y + barHeight, 0xFF1E2024);
        // Боковые стороны
        guiGraphics.fill(x, y + 2, x + 1, y + barHeight - 2, 0xFF4A4E58);
        guiGraphics.fill(x + barWidth - 1, y + 2, x + barWidth, y + barHeight - 2, 0xFF1E2024);

        // Угловые заклепки (бронзово-железные точки)
        drawRivet(guiGraphics, x + 3, y + 2);
        drawRivet(guiGraphics, x + barWidth - 5, y + 2);
        drawRivet(guiGraphics, x + 3, y + barHeight - 4);
        drawRivet(guiGraphics, x + barWidth - 5, y + barHeight - 4);

        // --- 3. ЦЕНТРАЛЬНЫЙ РОМБ-ГЕРБ (СИМВОЛ РЕГИОНА) ---
        int crestX = x + (barWidth / 2);
        int crestY = y + 12;
        drawDiamondCrest(guiGraphics, crestX, crestY, themeColor, themeDark);

        // --- 4. ТЕКСТ: РЕГИОН (СЛЕВА) И ЗВАНИЕ (СПРАВА) ---
        // Левая часть: название фракции
        Component factionName = faction.getFormattedName();
        guiGraphics.drawString(font, factionName, x + 14, y + 4, 0xFFFFFF, true);

        // Правая часть: текущий ранг
        Component rankTitle = FactionRank.getTitle(faction, rep);
        int rankWidth = font.width(rankTitle);
        guiGraphics.drawString(font, rankTitle, x + barWidth - rankWidth - 14, y + 4, 0xFFFFFF, true);

        // Числовое значение очков репутации по центру над гербом
        String repStr = (rep >= 0 ? "+" : "") + rep;
        int repStrWidth = font.width(repStr);
        guiGraphics.drawString(font, repStr, crestX - (repStrWidth / 2), y + 2, (rep >= 0 ? 0xAAFFAA : 0xFFAAAA), true);

        // --- 5. ДВОЙНАЯ ЛОР-ШКАЛА ПРОГРЕССА С КАНВАСОМ ---
        int prev = FactionRank.getPrevTierTarget(rep);
        int next = FactionRank.getNextTierTarget(rep);
        float targetProgress = 1.0f;
        if (next != prev) {
            targetProgress = Math.max(0.0f, Math.min(1.0f, (float) (rep - prev) / (float) (next - prev)));
        }
        displayedProgress = Mth.lerp(0.12f, displayedProgress, targetProgress);

        int trackY = y + 17;
        int trackH = 4;

        // Левая половина шкалы (отступаем от центрального герба)
        int leftTrackX = x + 14;
        int leftTrackW = (barWidth / 2) - 24;
        guiGraphics.fill(leftTrackX, trackY, leftTrackX + leftTrackW, trackY + trackH, 0xFF141518);
        guiGraphics.renderOutline(leftTrackX, trackY, leftTrackW, trackH, 0xFF2C2F36);

        // Правая половина шкалы
        int rightTrackX = crestX + 10;
        int rightTrackW = (barWidth / 2) - 24;
        guiGraphics.fill(rightTrackX, trackY, rightTrackX + rightTrackW, trackY + trackH, 0xFF141518);
        guiGraphics.renderOutline(rightTrackX, trackY, rightTrackW, trackH, 0xFF2C2F36);

        // Заполнение полосы (прогресс идет слева направо через обе половины)
        int totalWidth = leftTrackW + rightTrackW;
        int fillTotal = (int) (totalWidth * displayedProgress);

        if (fillTotal > 0) {
            int fillLeft = Math.min(fillTotal, leftTrackW);
            guiGraphics.fill(leftTrackX + 1, trackY + 1, leftTrackX + fillLeft, trackY + trackH - 1, themeColor);

            if (fillTotal > leftTrackW) {
                int fillRight = fillTotal - leftTrackW;
                guiGraphics.fill(rightTrackX + 1, trackY + 1, rightTrackX + fillRight, trackY + trackH - 1, themeColor);
            }
        }

        // --- 6. ГОТИЧЕСКИЕ СТРЕЛКИ НАВИГАЦИИ ПО БОКАМ ---
        guiGraphics.drawString(font, "«", x - 7, y + 8, 0x8A939E, true);
        guiGraphics.drawString(font, "»", x + barWidth + 2, y + 8, 0x8A939E, true);
    }

    private void drawRivet(GuiGraphics g, int rx, int ry) {
        g.fill(rx, ry, rx + 2, ry + 2, 0xFF727985);
        g.fill(rx + 1, ry + 1, rx + 2, ry + 2, 0xFF2F3238);
    }

    private void drawDiamondCrest(GuiGraphics g, int cx, int cy, int primaryColor, int darkColor) {
        // Отрисовка стилизованного ромбовидного щита 9x9 пикселей
        g.fill(cx - 1, cy - 4, cx + 2, cy + 5, 0xFF0D0E11);
        g.fill(cx - 3, cy - 2, cx + 4, cy + 3, 0xFF0D0E11);
        g.fill(cx - 4, cy - 1, cx + 5, cy + 2, 0xFF0D0E11);

        // Цветное сияние региона внутри щита
        g.fill(cx - 2, cy - 1, cx + 3, cy + 2, primaryColor);
        g.fill(cx - 1, cy - 2, cx + 2, cy + 3, primaryColor);
        g.fill(cx, cy, cx + 1, cy + 1, 0xFFFFFFFF); // центральный блик
    }

    private int getFactionHexColor(Faction faction) {
        return switch (faction) {
            case ISONOMIA -> 0xFF9EA7B0; // Стальной бетон панелек
            case CALIGO -> 0xFF5DE2E7;   // Холодный инеевый неон
            case GNOSTERA -> 0xFF658A42; // Темная болотная топь
            case GLADIUS -> 0xFFFF5722;  // Раскаленная шлаковая магма
            case MARIS -> 0xFF2196F3;    // Глубокий абиссальный ультрамарин
            case FULGORT -> 0xFFFFC107;  // Золотое сияние пиков
            case ADEL -> 0xFF00BFA5;     // Ядовитая мангровая бирюза
        };
    }

    private int getDarkerColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (0xFF << 24) | ((r / 2) << 16) | ((g / 2) << 8) | (b / 2);
    }
}
