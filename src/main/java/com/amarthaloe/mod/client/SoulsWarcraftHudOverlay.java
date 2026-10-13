package com.amarthaloe.mod.client;

import com.amarthaloe.mod.faction.Faction;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SoulsWarcraftHudOverlay implements LayeredDraw.Layer {

    private static final int BORDER_OUTER_COLOR = 0xFF141210;
    private static final int BORDER_INNER_COLOR = 0xFF352F2B;
    private static final int BG_BAR_COLOR       = 0xCC0D0B0A;
    private static final int HP_COLOR           = 0xFF851414;
    private static final int HP_HIGHLIGHT       = 0xFFA62424;
    private static final int STAMINA_COLOR      = 0xFF2D5A38;
    private static final int MANA_COLOR         = 0xFF1F3A6E;
    private static final int GOLD_TEXT          = 0xFFC7A762;
    private static final int SUB_TEXT           = 0xFF8A8275;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }

        int startX = 20;
        int startY = 16;
        int portraitSize = 36;

        // Портрет
        drawFrame(guiGraphics, startX, startY, portraitSize, portraitSize);
        guiGraphics.fill(startX + 2, startY + 2, startX + portraitSize - 2, startY + portraitSize - 2, 0xFF181513);

        ResourceLocation skinTexture = mc.getSkinManager().getInsecureSkin(player.getGameProfile()).texture();
        RenderSystem.enableBlend();
        // Приведение типов к int для избежания lossy conversion
        guiGraphics.blit(skinTexture, startX + 4, startY + 4, 28, 28, 8, 8, 8, 8, 64, 64);

        // Уровень персонажа
        int badgeX = startX + portraitSize - 8;
        int badgeY = startY + portraitSize - 8;
        guiGraphics.fill(badgeX - 1, badgeY - 1, badgeX + 15, badgeY + 13, BORDER_OUTER_COLOR);
        guiGraphics.fill(badgeX, badgeY, badgeX + 14, badgeY + 12, 0xFF26211C);
        String levelStr = String.valueOf(player.experienceLevel);
        guiGraphics.drawString(mc.font, levelStr, badgeX + 7 - mc.font.width(levelStr) / 2, badgeY + 2, GOLD_TEXT, false);

        // Имя игрока
        int barsStartX = startX + portraitSize + 8;
        int currentY = startY;

        String playerName = player.getName().getString().toUpperCase();
        guiGraphics.drawString(mc.font, playerName, barsStartX, currentY, GOLD_TEXT, true);

        // Фракция игрока
        Faction faction = ClientFactionData.faction;
        if (faction != null) {
            String factionTag = "<" + faction.name() + ">";
            guiGraphics.drawString(mc.font, factionTag, barsStartX + mc.font.width(playerName) + 6, currentY, SUB_TEXT, false);
        }
        currentY += 12;

        // Шкала здоровья
        float maxHealth = player.getMaxHealth();
        float currentHealth = Mth.clamp(player.getHealth(), 0.0f, maxHealth);
        float hpPercent = currentHealth / maxHealth;

        int barWidth = 140;
        int barHeight = 8;
        drawStatBar(guiGraphics, barsStartX, currentY, barWidth, barHeight, hpPercent, HP_COLOR, HP_HIGHLIGHT);

        String hpText = (int) currentHealth + " / " + (int) maxHealth;
        guiGraphics.drawString(mc.font, hpText, barsStartX + barWidth - mc.font.width(hpText), currentY - 10, SUB_TEXT, false);
        currentY += barHeight + 4;

        // Шкала сытости / выносливости
        float maxFood = 20.0f;
        float currentFood = player.getFoodData().getFoodLevel();
        float foodPercent = currentFood / maxFood;
        drawStatBar(guiGraphics, barsStartX, currentY, barWidth - 25, 5, foodPercent, STAMINA_COLOR, 0xFF437A51);
        currentY += 8;

        // Шкала опыта / маны
        float xpPercent = player.experienceProgress;
        drawStatBar(guiGraphics, barsStartX, currentY, barWidth - 45, 4, xpPercent, MANA_COLOR, 0xFF355E9E);
    }

    private void drawStatBar(GuiGraphics g, int x, int y, int width, int height, float fillPercent, int colorBase, int colorLight) {
        g.fill(x - 1, y - 1, x + width + 1, y + height + 1, BORDER_OUTER_COLOR);
        g.fill(x, y, x + width, y + height, BG_BAR_COLOR);

        int filledWidth = (int) (width * fillPercent);
        if (filledWidth > 0) {
            g.fill(x, y, x + filledWidth, y + height, colorBase);
            g.fill(x, y, x + filledWidth, y + 1, colorLight);
        }

        for (int notch = 20; notch < width; notch += 20) {
            g.fill(x + notch, y, x + notch + 1, y + height, 0x44000000);
        }
    }

    private void drawFrame(GuiGraphics g, int x, int y, int width, int height) {
        g.fill(x - 1, y - 1, x + width + 1, y + height + 1, BORDER_OUTER_COLOR);
        g.fill(x, y, x + width, y + height, BORDER_INNER_COLOR);
        g.fill(x - 2, y - 2, x, y, GOLD_TEXT);
        g.fill(x + width, y - 2, x + width + 2, y, GOLD_TEXT);
        g.fill(x - 2, y + height, x, y + height + 2, GOLD_TEXT);
        g.fill(x + width, y + height, x + width + 2, y + height + 2, GOLD_TEXT);
    }
}
