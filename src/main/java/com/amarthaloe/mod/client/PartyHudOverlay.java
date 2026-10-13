package com.amarthaloe.mod.client;

import com.amarthaloe.mod.party.PartyMemberData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PartyHudOverlay implements LayeredDraw.Layer {

    private static final int BORDER_COLOR   = 0xFF141210;
    private static final int BG_COLOR       = 0xCC110E0C;
    private static final int HP_COLOR       = 0xFF851414;
    private static final int HP_BG          = 0xFF2A1F1D;
    private static final int GOLD_TEXT      = 0xFFC7A762;
    private static final int MUTED_TEXT     = 0xFF8A8275;

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

        int startX = 20;
        int startY = 70;
        int entryWidth = 110;
        int entryHeight = 22;

        for (PartyMemberData member : members) {
            if (member.getUuid().equals(localPlayer.getUUID())) {
                continue;
            }

            guiGraphics.fill(startX - 1, startY - 1, startX + entryWidth + 1, startY + entryHeight + 1, BORDER_COLOR);
            guiGraphics.fill(startX, startY, startX + entryWidth, startY + entryHeight, BG_COLOR);

            guiGraphics.drawString(mc.font, member.getName(), startX + 3, startY + 2, GOLD_TEXT, false);

            String statusText;
            Player targetEntity = mc.level != null ? mc.level.getPlayerByUUID(member.getUuid()) : null;
            if (targetEntity != null) {
                int dist = (int) Math.sqrt(localPlayer.distanceToSqr(targetEntity));
                statusText = dist + "m";
            } else {
                statusText = ">100m";
            }
            guiGraphics.drawString(mc.font, statusText, startX + entryWidth - mc.font.width(statusText) - 3, startY + 2, MUTED_TEXT, false);

            int barY = startY + 12;
            int barWidth = entryWidth - 6;
            int barHeight = 6;
            float maxHp = member.getMaxHealth();
            float hpPercent = maxHp > 0.0f ? Mth.clamp(member.getHealth() / maxHp, 0.0f, 1.0f) : 0.0f;

            guiGraphics.fill(startX + 3, barY, startX + 3 + barWidth, barY + barHeight, HP_BG);
            guiGraphics.fill(startX + 3, barY, startX + 3 + (int) (barWidth * hpPercent), barY + barHeight, HP_COLOR);

            startY += entryHeight + 5;
        }
    }
}
