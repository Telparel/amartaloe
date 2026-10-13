package com.amarthaloe.mod.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class WelcomeToastOverlay implements LayeredDraw.Layer {
    private static int displayTicks = 0;
    private static final int MAX_TICKS = 140; // ~7 секунд
    private static boolean soundPlayed = false;

    public static void trigger() {
        displayTicks = MAX_TICKS;
        soundPlayed = false;
    }

    public static void tick() {
        if (displayTicks > 0) {
            displayTicks--;
            if (!soundPlayed && displayTicks == (MAX_TICKS - 15)) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    // Приятный тихий перезвон аметиста и мягкий колокол
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, 0.9F, 0.45F));
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6F, 0.35F));
                }
                soundPlayed = true;
            }
        }
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker deltaTracker) {
        if (displayTicks <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        Font font = mc.font;

        float alpha = 1.0f;
        if (displayTicks > (MAX_TICKS - 25)) {
            alpha = (float) (MAX_TICKS - displayTicks) / 25.0f;
        } else if (displayTicks < 30) {
            alpha = (float) displayTicks / 30.0f;
        }

        int aByte = Math.min(255, Math.max(0, (int) (alpha * 240)));
        if (aByte <= 5) return;

        int toastW = 280;
        int toastH = 34;
        int x = (screenWidth - toastW) / 2;
        int y = 46; // Аккуратно под верхней панелью репутации

        int bgColor = (aByte << 24) | 0x0D0E12;
        int borderColor = (aByte << 24) | 0x8A7E62;

        // Корпус
        g.fill(x, y, x + toastW, y + toastH, bgColor);
        g.renderOutline(x, y, toastW, toastH, borderColor);
        g.renderOutline(x + 2, y + 2, toastW - 4, toastH - 4, (Math.min(aByte, 60) << 24) | 0xFFFFFF);

        String playerName = ClientProfileData.rpName.isBlank() ? mc.player.getName().getString() : ClientProfileData.rpName;

        Component title = Component.literal("« ДОБРО ПОЖАЛОВАТЬ В АМАРТАЛОЭ »").withStyle(net.minecraft.ChatFormatting.GOLD);
        Component sub = Component.literal("Пусть хранят тебя тени, ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(playerName).withStyle(net.minecraft.ChatFormatting.WHITE));

        g.drawCenteredString(font, title, screenWidth / 2, y + 6, (aByte << 24) | 0xFFAA00);
        g.drawCenteredString(font, sub, screenWidth / 2, y + 19, (aByte << 24) | 0xFFFFFF);
    }
}
