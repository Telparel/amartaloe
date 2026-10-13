package com.amarthaloe.mod.client;

import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

public class FactionGuiOverlay implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        Font font = mc.font;

        Faction faction = ClientFactionData.getSelectedFaction();
        int rep = ClientFactionData.getReputation(faction);

        int barWidth = 160;
        int barHeight = 28;
        int x = (screenWidth - barWidth) / 2;
        int y = 6;

        // Постапокалиптическая темная плашка: темно-серый фон с металлическим контуром
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xCC121316);
        guiGraphics.renderOutline(x, y, barWidth, barHeight, 0xFF353942);

        // Цветовой акцент региона (левая цветная метка фракции)
        int factionColor = getFactionHexColor(faction);
        guiGraphics.fill(x + 2, y + 2, x + 5, y + barHeight - 2, factionColor);

        // Название региона и текущие очки
        String repStr = (rep >= 0 ? "+" : "") + rep;
        Component header = faction.getFormattedName().copy().append(" §8| §f" + repStr);
        guiGraphics.drawString(font, header, x + 10, y + 4, 0xFFFFFF, true);

        // Текущий титул/звание
        Component rankTitle = FactionRank.getTitle(faction, rep);
        guiGraphics.drawString(font, rankTitle, x + barWidth - font.width(rankTitle) - 6, y + 4, 0xFFFFFF, true);

        // Прогресс-бар до следующего ранга
        int prev = FactionRank.getPrevTierTarget(rep);
        int next = FactionRank.getNextTierTarget(rep);
        float progress = 1.0f;
        if (next != prev) {
            progress = Math.max(0.0f, Math.min(1.0f, (float) (rep - prev) / (float) (next - prev)));
        }

        int progressTrackX = x + 10;
        int progressTrackY = y + 18;
        int progressTrackW = barWidth - 16;
        int progressTrackH = 4;

        // Фон шкалы
        guiGraphics.fill(progressTrackX, progressTrackY, progressTrackX + progressTrackW, progressTrackY + progressTrackH, 0xFF222225);
        // Заполнение шкалы прогресса
        int filledW = (int) (progressTrackW * progress);
        guiGraphics.fill(progressTrackX, progressTrackY, progressTrackX + filledW, progressTrackY + progressTrackH, factionColor);
    }

    private int getFactionHexColor(Faction faction) {
        return switch (faction) {
            case ISONOMIA -> 0xFF8A939E; // Стальной/хрущевочный бетон
            case CALIGO -> 0xFF6DE0FF;   // Ледяной циан
            case GNOSTERA -> 0xFF4A6B35; // Трупно-болотный
            case GLADIUS -> 0xFFFF4B2B;  // Багрово-огненный
            case MARIS -> 0xFF1C5D99;    // Глубокий океанический
            case FULGORT -> 0xFFFFD700;  // Золотистый райский
            case ADEL -> 0xFF00A896;     // Темный мангровый изумруд
        };
    }
}
