package com.amarthaloe.mod.client.gui;

import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.client.ClientPartyData;
import com.amarthaloe.mod.client.ClientProfileData;
import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import com.amarthaloe.mod.party.PartyMemberData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class AmarthaloeMenuScreen extends Screen {

    public enum Tab {
        PROFILE("Профиль"),
        FACTIONS("Фракции"),
        PARTY("Группа");

        private final String title;

        Tab(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private Tab currentTab = Tab.PROFILE;
    private int currentFactionIndex = 0;

    private int leftPos;
    private int topPos;
    private final int imageWidth = 240;
    private final int imageHeight = 170;

    // Кнопки переключения фракций
    private Button prevFactionBtn;
    private Button nextFactionBtn;

    public AmarthaloeMenuScreen() {
        super(Component.literal("Меню Амарталоэ"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        rebuildMenuWidgets();
    }

    private void rebuildMenuWidgets() {
        this.clearWidgets();

        // 1. Верхние вкладки меню (только 3 вкладки: Профиль, Фракции, Группа)
        Tab[] tabs = Tab.values();
        int tabWidth = 70;
        int tabSpacing = 5;
        int totalTabsWidth = (tabs.length * tabWidth) + ((tabs.length - 1) * tabSpacing);
        int tabStartX = this.leftPos + (this.imageWidth - totalTabsWidth) / 2;

        for (int i = 0; i < tabs.length; i++) {
            Tab tab = tabs[i];
            int x = tabStartX + i * (tabWidth + tabSpacing);
            int y = this.topPos - 22;

            Button tabBtn = Button.builder(Component.literal(tab.getTitle()), b -> {
                this.currentTab = tab;
                rebuildMenuWidgets();
            }).bounds(x, y, tabWidth, 20).build();

            tabBtn.active = (this.currentTab != tab);
            this.addRenderableWidget(tabBtn);
        }

        // 2. Стрелки переключения фракций на вкладке FACTIONS
        if (this.currentTab == Tab.FACTIONS) {
            int arrowY = this.topPos + 18;
            int arrowSize = 20;

            this.prevFactionBtn = Button.builder(Component.literal("<"), b -> {
                Faction[] factions = Faction.values();
                this.currentFactionIndex = (this.currentFactionIndex - 1 + factions.length) % factions.length;
            }).bounds(this.leftPos + 15, arrowY, arrowSize, arrowSize).build();

            this.nextFactionBtn = Button.builder(Component.literal(">"), b -> {
                Faction[] factions = Faction.values();
                this.currentFactionIndex = (this.currentFactionIndex + 1) % factions.length;
            }).bounds(this.leftPos + this.imageWidth - 15 - arrowSize, arrowY, arrowSize, arrowSize).build();

            this.addRenderableWidget(this.prevFactionBtn);
            this.addRenderableWidget(this.nextFactionBtn);
        }

        // 3. Нижняя кнопка закрытия
        this.addRenderableWidget(Button.builder(Component.literal("Закрыть"), b -> this.onClose())
                .bounds(this.leftPos + (this.imageWidth - 80) / 2, this.topPos + this.imageHeight - 24, 80, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Затемнение заднего фона
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Основное окно
        guiGraphics.fill(this.leftPos - 1, this.topPos - 1, this.leftPos + this.imageWidth + 1, this.topPos + this.imageHeight + 1, 0xFF141210);
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xEE1A1715);

        // Отрисовка контента активной вкладки
        switch (this.currentTab) {
            case PROFILE -> renderProfileTab(guiGraphics);
            case FACTIONS -> renderFactionsTab(guiGraphics);
            case PARTY -> renderPartyTab(guiGraphics);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderProfileTab(GuiGraphics g) {
        int contentX = this.leftPos + 16;
        int contentY = this.topPos + 16;

        g.drawString(this.font, "§6§lПРОФИЛЬ ПЕРСОНАЖА", contentX, contentY, 0xFFFFFF, false);
        contentY += 16;

        String name = ClientProfileData.rpName.isEmpty() ? (this.minecraft.player != null ? this.minecraft.player.getName().getString() : "Неизвестный") : ClientProfileData.rpName;
        g.drawString(this.font, "§7Имя: §f" + name, contentX, contentY, 0xFFFFFF, false);
        contentY += 12;

        g.drawString(this.font, "§7Пол: §f" + ClientProfileData.gender, contentX, contentY, 0xFFFFFF, false);
        contentY += 12;

        if (!ClientProfileData.selectedTitle.isEmpty()) {
            g.drawString(this.font, "§7Титул: §e" + ClientProfileData.selectedTitle, contentX, contentY, 0xFFFFFF, false);
            contentY += 12;
        }

        g.drawString(this.font, "§7Телосложение: §f" + ClientProfileData.build, contentX, contentY, 0xFFFFFF, false);
        contentY += 12;

        g.drawString(this.font, "§7Глаза / Волосы: §f" + ClientProfileData.eyes + " / " + ClientProfileData.hair, contentX, contentY, 0xFFFFFF, false);
        contentY += 12;

        g.drawString(this.font, "§7Особые приметы: §f" + ClientProfileData.distinctiveFeatures, contentX, contentY, 0xFFFFFF, false);
        contentY += 14;

        g.drawString(this.font, "§7Предыстория:", contentX, contentY, 0xFFFFFF, false);
        contentY += 10;

        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(Component.literal("§8" + ClientProfileData.backstory), this.imageWidth - 32);
        for (int i = 0; i < Math.min(lines.size(), 4); i++) {
            g.drawString(this.font, lines.get(i), contentX, contentY, 0xFFFFFF, false);
            contentY += 9;
        }
    }

    private void renderFactionsTab(GuiGraphics g) {
        Faction[] factions = Faction.values();
        if (this.currentFactionIndex < 0 || this.currentFactionIndex >= factions.length) {
            this.currentFactionIndex = 0;
        }
        Faction currentFaction = factions[this.currentFactionIndex];

        int centerX = this.leftPos + this.imageWidth / 2;
        int contentY = this.topPos + 22;

        // Название текущей фракции по центру
        String factionTitle = "§6§l" + currentFaction.getName();
        g.drawString(this.font, factionTitle, centerX - this.font.width(factionTitle) / 2, contentY, 0xFFFFFF, false);
        contentY += 24;

        // Репутация и ранг
        int rep = ClientFactionData.getReputation(currentFaction);
        FactionRank rank = FactionRank.fromReputation(rep);

        String repText = "§7Репутация: §f" + rep + " §8| §7Ранг: " + rank.getColor() + rank.getTitle();
        g.drawString(this.font, repText, centerX - this.font.width(repText) / 2, contentY, 0xFFFFFF, false);
        contentY += 18;

        // Полоса прогресса репутации
        int barWidth = 140;
        int barHeight = 6;
        int barX = centerX - barWidth / 2;

        g.fill(barX - 1, contentY - 1, barX + barWidth + 1, contentY + barHeight + 1, 0xFF141210);
        g.fill(barX, contentY, barX + barWidth, contentY + barHeight, 0xFF2B2622);

        float progress = Math.clamp((rep % 1000) / 1000.0f, 0.0f, 1.0f);
        if (progress > 0) {
            g.fill(barX, contentY, barX + (int) (barWidth * progress), contentY + barHeight, 0xFFC7A762);
        }
        contentY += 16;

        // Описание фракции
        List<net.minecraft.util.FormattedCharSequence> descLines = this.font.split(Component.literal("§7" + currentFaction.getDescription()), this.imageWidth - 40);
        for (net.minecraft.util.FormattedCharSequence line : descLines) {
            g.drawString(this.font, line, this.leftPos + 20, contentY, 0xFFFFFF, false);
            contentY += 10;
        }
    }

    private void renderPartyTab(GuiGraphics g) {
        int contentX = this.leftPos + 16;
        int contentY = this.topPos + 16;

        g.drawString(this.font, "§6§lСОСТАВ ГРУППЫ", contentX, contentY, 0xFFFFFF, false);
        contentY += 18;

        List<PartyMemberData> members = ClientPartyData.getMembers();
        if (members == null || members.isEmpty()) {
            g.drawString(this.font, "§8Вы не состоите в группе.", contentX, contentY, 0xFFFFFF, false);
            contentY += 12;
            g.drawString(this.font, "§7Используйте §f/party invite <игрок>§7,", contentX, contentY, 0xFFFFFF, false);
            contentY += 10;
            g.drawString(this.font, "§7чтобы собрать отряд союзников.", contentX, contentY, 0xFFFFFF, false);
            return;
        }

        for (int i = 0; i < members.size(); i++) {
            PartyMemberData m = members.get(i);
            String info = "§f" + (i + 1) + ". " + m.getName() + " §7(HP: " + (int) m.getHealth() + "/" + (int) m.getMaxHealth() + ") " + (m.getTitle().isEmpty() ? "" : "§6[" + m.getTitle() + "]");
            g.drawString(this.font, info, contentX, contentY, 0xFFFFFF, false);
            contentY += 12;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
