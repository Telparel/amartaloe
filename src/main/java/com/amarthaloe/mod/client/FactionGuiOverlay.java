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

        // Плавный возврат позиции после толчка анимации (Lerp)
        ClientFactionData.animOffset = Mth.lerp(0.2f, ClientFactionData.animOffset, 0.0f);

        int barWidth = 170;
        int barHeight = 28;
        int baseX = (screenWidth - barWidth) / 2;
        int x = baseX + (int) ClientFactionData.animOffset;
        int y = 6;

        // Постапокалиптическая плашка
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xDD111215);
        guiGraphics.renderOutline(x, y, barWidth, barHeight, 0xFF353942);

        // Стрелочки навигации подсказывают доступное управление
        guiGraphics.drawString(font, "§8<", x - 8, y + 9, 0x888888, false);
        guiGraphics.drawString(font, "§8>", x + barWidth + 3, y + 9, 0x888888, false);

        int factionColor = getFactionHexColor(faction);
        guiGraphics.fill(x + 2, y + 2, x + 5, y + barHeight - 2, factionColor);

        // Название и число
        String repStr = (rep >= 0 ? "+" : "") + rep;
        Component header = faction.getFormattedName().copy().append(" §8| §f" + repStr);
        guiGraphics.drawString(font, header, x + 10, y + 4, 0xFFFFFF, true);

        // Титул / Звание
        Component rankTitle = FactionRank.getTitle(faction, rep);
        guiGraphics.drawString(font, rankTitle, x + barWidth - font.width(rankTitle) - 6, y + 4, 0xFFFFFF, true);

        // Расчет и плавная интерполяция шкалы
        int prev = FactionRank.getPrevTierTarget(rep);
        int next = FactionRank.getNextTierTarget(rep);
        float targetProgress = 1.0f;
        if (next != prev) {
            targetProgress = Math.max(0.0f, Math.min(1.0f, (float) (rep - prev) / (float) (next - prev)));
        }
        displayedProgress = Mth.lerp(0.1f, displayedProgress, targetProgress);

        int progressTrackX = x + 10;
        int progressTrackY = y + 18;
        int progressTrackW = barWidth - 16;
        int progressTrackH = 4;

        // Фон прогресс-бара
        guiGraphics.fill(progressTrackX, progressTrackY, progressTrackX + progressTrackW, progressTrackY + progressTrackH, 0xFF1C1D21);

        // Заполнение полосы
        int filledW = (int) (progressTrackW * displayedProgress);
        guiGraphics.fill(progressTrackX, progressTrackY, progressTrackX + filledW, progressTrackY + progressTrackH, factionColor);
    }

    private int getFactionHexColor(Faction faction) {
        return switch (faction) {
            case ISONOMIA -> 0xFF8A939E;
            case CALIGO -> 0xFF6DE0FF;
            case GNOSTERA -> 0xFF4A6B35;
            case GLADIUS -> 0xFFFF4B2B;
            case MARIS -> 0xFF1C5D99;
            case FULGORT -> 0xFFFFD700;
            case ADEL -> 0xFF00A896;
        };
    }
}
