package com.amarthaloe.mod.client.gui;

import com.amarthaloe.mod.network.CompleteTutorialPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class TutorialScreen extends Screen {
    public TutorialScreen() {
        super(Component.literal("Путеводитель по Амарталоэ"));
    }

    @Override
    protected void init() {
        int guiLeft = (this.width - 320) / 2;
        int guiTop = (this.height - 210) / 2;

        addRenderableWidget(Button.builder(Component.literal("§aПринять и ступить в мир"), b -> {
            PacketDistributor.sendToServer(new CompleteTutorialPayload());
            this.onClose();
        }).bounds(guiLeft + 60, guiTop + 172, 200, 22).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        int guiLeft = (this.width - 320) / 2;
        int guiTop = (this.height - 210) / 2;
        int guiW = 320;
        int guiH = 210;

        // Мрачная подложка
        g.fill(guiLeft, guiTop, guiLeft + guiW, guiTop + guiH, 0xF6101115);
        g.renderOutline(guiLeft, guiTop, guiW, guiH, 0xFF7D725C);
        g.renderOutline(guiLeft + 2, guiTop + 2, guiW - 4, guiH - 4, 0xFF2A2D35);

        // Заголовок
        g.drawCenteredString(this.font, "§6ПАМЯТКА ВЫЖИВШЕГО В АМАРТАЛОЭ", this.width / 2, guiTop + 12, 0xFFAA00);
        g.fill(guiLeft + 16, guiTop + 26, guiLeft + guiW - 16, guiTop + 27, 0xFF4A4E58);

        int textX = guiLeft + 20;
        int y = guiTop + 38;

        renderTipRow(g, "[ M ]", "Архив Амарталоэ", "Личный паспорт, выбор титулов, группы и настройки.", textX, y);
        renderTipRow(g, "[ ← / → ]", "Панель Баронов", "Переключение фракций в верхней шкале репутации.", textX, y + 42);
        renderTipRow(g, "[ /party ]", "Отряды союзников", "Приглашение друзей, общий HUD и отключение урона.", textX, y + 84);

        g.drawCenteredString(this.font, "§8Бароны наблюдают за каждым твоим шагом...", this.width / 2, guiTop + 154, 0x888888);
    }

    private void renderTipRow(GuiGraphics g, String key, String title, String desc, int x, int y) {
        // Плашка клавиши
        g.fill(x, y, x + 54, y + 18, 0xFF252830);
        g.renderOutline(x, y, 54, 18, 0xFF4F5566);
        g.drawCenteredString(this.font, "§e" + key, x + 27, y + 5, 0xFFFF55);

        // Описание
        g.drawString(this.font, "§f" + title, x + 62, y + 1, 0xFFFFFF, true);
        g.drawString(this.font, "§7" + desc, x + 62, y + 11, 0xAAAAAA, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
