package com.amarthaloe.mod.client;

import com.amarthaloe.mod.party.PartyMemberData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PartyHudOverlay implements LayeredDraw.Layer {

    private static final int BORDER_COLOR   = 0xFF141210;
    private static final int BG_COLOR       = 0xCC110E0C;
    private static final int HP_COLOR       = 0xFF851414;
    private static final int HP_BG          = 0xFF2A1F1D;
    private static final int GOLD_TEXT      = 0xFFC7A762;
    private static final int MUTED_TEXT     = 0xFF8A8275;
    private static final int DEBUFF_ALERT   = 0xFFD32F2F;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player localPlayer = mc.player;
        if (localPlayer == null || mc.options.hideGui) {
            return;
        }

        List<PartyMemberData> members = ClientPartyData.getMembers();
        if (members == null || members.isEmpty()) {
            return;
        }

        // Рендерим под фреймом игрока слева
        int startX = 20;
        int startY = 70;
        int entryWidth = 110;
        int entryHeight = 22;

        for (PartyMemberData member : members) {
            // Пропускаем самого себя
            if (member.uuid().equals(localPlayer.getUUID())) {
                continue;
            }

            // Рамка слота союзника
            guiGraphics.fill(startX - 1, startY - 1, startX + entryWidth + 1, startY + entryHeight + 1, BORDER_COLOR);
            guiGraphics.fill(startX, startY, startX + entryWidth, startY + entryHeight, BG_COLOR);

            // Имя союзника
            guiGraphics.drawString(mc.font, member.name(), startX + 3, startY + 2, GOLD_TEXT, false);

            // Индикатор дебаффа
            if (member.hasHarmfulEffects()) {
                guiGraphics.drawString(mc.font, "!", startX + entryWidth - 8, startY + 2, DEBUFF_ALERT, true);
            }

            // Расчет дистанции или статуса сектора
            String statusText;
            String currentDim = localPlayer.level().dimension().location().getPath();
            if (!currentDim.equalsIgnoreCase(member.sectorDimension())) {
                statusText = member.sectorDimension().toUpperCase();
            } else {
                Player targetEntity = mc.level != null ? mc.level.getPlayerByUUID(member.uuid()) : null;
                if (targetEntity != null) {
                    int dist = (int) Math.sqrt(localPlayer.distanceToSqr(targetEntity));
                    statusText = dist + "m";
                } else {
                    statusText = ">100m";
                }
            }
            guiGraphics.drawString(mc.font, statusText, startX + entryWidth - mc.font.width(statusText) - 10, startY + 2, MUTED_TEXT, false);

            // Полоска HP союзника
            int barY = startY + 12;
            int barWidth = entryWidth - 6;
            int barHeight = 6;
            float hpPercent = member.maxHealth() > 0 ? Math.clamp(member.health() / member.maxHealth(), 0.0f, 1.0f) : 0.0f;

            guiGraphics.fill(startX + 3, barY, startX + 3 + barWidth, barY + barHeight, HP_BG);
            guiGraphics.fill(startX + 3, barY, startX + 3 + (int) (barWidth * hpPercent), barY + barHeight, HP_COLOR);

            startY += entryHeight + 5;
        }
    }
}
