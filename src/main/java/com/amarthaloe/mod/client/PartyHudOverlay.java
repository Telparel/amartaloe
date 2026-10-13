package com.amarthaloe.mod.client;

import com.amarthaloe.mod.party.PartyMemberData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PartyHudOverlay implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics g, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;
        if (!ClientPartyData.isInParty()) return;

        List<PartyMemberData> members = ClientPartyData.getMembers();
        Font font = mc.font;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int cardW = 120;
        int cardH = 26;
        int spacing = 3;

        // Фильтруем себя, чтобы видеть только союзников по пати (или оставьте всех)
        List<PartyMemberData> allies = members.stream()
                .filter(m -> !m.getUuid().equals(mc.player.getUUID()))
                .toList();

        if (allies.isEmpty()) return;

        int totalH = allies.size() * (cardH + spacing);

        // Расчет координат в зависимости от позиции из настроек
        int startX = switch (ModClientConfig.partyHudPosition) {
            case TOP_LEFT, BOTTOM_LEFT -> 6;
            case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - cardW - 6;
        };

        int startY = switch (ModClientConfig.partyHudPosition) {
            case TOP_LEFT, TOP_RIGHT -> 42; // ниже верхней плашки репутации
            case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight - totalH - 12;
        };

        for (int i = 0; i < allies.size(); i++) {
            PartyMemberData member = allies.get(i);
            int y = startY + i * (cardH + spacing);

            // Темный металлический фон карточки сопартийца
            g.fill(startX, y, startX + cardW, y + cardH, 0xD9101114);
            g.renderOutline(startX, y, cardW, cardH, 0xFF353942);

            // Отрисовка скина головы (Player Head)
            PlayerInfo info = mc.getConnection() != null ? mc.getConnection().getPlayerInfo(member.getUuid()) : null;
            if (info != null) {
                PlayerSkin skin = info.getSkin();
                g.blit(skin.texture(), startX + 3, y + 3, 20, 20, 8.0F, 8.0F, 8, 8, 64, 64);
                // Оверлей шлема скина
                g.blit(skin.texture(), startX + 3, y + 3, 20, 20, 40.0F, 8.0F, 8, 8, 64, 64);
            } else {
                g.fill(startX + 3, y + 3, startX + 23, y + 23, 0xFF555555);
            }

            // Имя сопартийца и титул
            int textX = startX + 26;
            g.drawString(font, member.getName(), textX, y + 3, 0xFFFFFF, true);

            if (!member.getTitle().isBlank()) {
                g.drawString(font, "§6" + member.getTitle(), textX, y + 11, 0xFFD4AF37, false);
            }

            // --- Полоска Здоровья (HP) ---
            int barX = textX;
            int barY = y + 19;
            int barW = cardW - 30;
            int barH = 3;

            float hpPct = Math.max(0.0f, Math.min(1.0f, member.getHealth() / Math.max(1.0f, member.getMaxHealth())));
            g.fill(barX, barY, barX + barW, barY + barH, 0xFF2B0A0A);
            g.fill(barX, barY, barX + (int) (barW * hpPct), barY + barH, 0xFFE02424);

            // --- Мини-полоска Сытости (Food) ---
            int foodY = y + 23;
            float foodPct = Math.max(0.0f, Math.min(1.0f, member.getFoodLevel() / 20.0f));
            g.fill(barX, foodY, barX + barW, foodY + 2, 0xFF2B1D0A);
            g.fill(barX, foodY, barX + (int) (barW * foodPct), foodY + 2, 0xFFD97706);
        }
    }
}
