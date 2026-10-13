package com.amarthaloe.mod.client.gui;

import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.client.ClientPartyData;
import com.amarthaloe.mod.client.ClientProfileData;
import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import com.amarthaloe.mod.party.PartyMemberData;
import com.amarthaloe.mod.profile.PlayerProfileData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class AmarthaloeMenuScreen extends Screen {

    public enum Tab {
        PROFILE("Профиль"),
        FACTIONS("Фракции"),
        PARTY("Группа");

        private final String label;

        Tab(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private Tab currentTab = Tab.PROFILE;
    private int selectedFactionIndex = 0;

    private int leftPos;
    private int topPos;
    private final int bgWidth = 240;
    private final int bgHeight = 175;

    // Цветовая палитра оригинального меню
    private static final int COLOR_BG          = 0xEE110E0C;
    private static final int COLOR_BORDER      = 0xFF141210;
    private static final int COLOR_INNER_BG    = 0xFF1A1715;
    private static final int COLOR_GOLD_TITLE  = 0xFFC7A762;
    private static final int COLOR_WHITE_TEXT  = 0xFFFFFFFF;
    private static final int COLOR_MUTED_TEXT  = 0xFF8A8275;
    private static final int COLOR_BAR_BG      = 0xFF2A1F1D;
    private static final int COLOR_BAR_FILL    = 0xFF851414;

    public AmarthaloeMenuScreen() {
        super(Component.literal("Меню Амарталоэ"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.bgWidth) / 2;
        this.topPos = (this.height - this.bgHeight) / 2;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // 1. Клик по верхним вкладкам
            Tab[] tabs = Tab.values();
            int tabWidth = 72;
            int tabHeight = 18;
            int tabSpacing = 6;
            int totalTabsWidth = tabs.length * tabWidth + (tabs.length - 1) * tabSpacing;
            int startX = this.leftPos + (this.bgWidth - totalTabsWidth) / 2;
            int tabY = this.topPos - 22;

            for (int i = 0; i < tabs.length; i++) {
                int tx = startX + i * (tabWidth + tabSpacing);
                if (mouseX >= tx && mouseX <= tx + tabWidth && mouseY >= tabY && mouseY <= tabY + tabHeight) {
                    this.currentTab = tabs[i];
                    return true;
                }
            }

            // 2. Клик по стрелочкам переключения фракций (< и >)
            if (this.currentTab == Tab.FACTIONS) {
                int arrowY = this.topPos + 12;
                int arrowSize = 14;

                // Левая стрелка
                int leftArrowX = this.leftPos + 14;
                if (mouseX >= leftArrowX && mouseX <= leftArrowX + arrowSize && mouseY >= arrowY && mouseY <= arrowY + arrowSize) {
                    Faction[] factions = Faction.values();
                    this.selectedFactionIndex = (this.selectedFactionIndex - 1 + factions.length) % factions.length;
                    return true;
                }

                // Правая стрелка
                int rightArrowX = this.leftPos + this.bgWidth - 14 - arrowSize;
                if (mouseX >= rightArrowX && mouseX <= rightArrowX + arrowSize && mouseY >= arrowY && mouseY <= arrowY + arrowSize) {
                    Faction[] factions = Faction.values();
                    this.selectedFactionIndex = (this.selectedFactionIndex + 1) % factions.length;
                    return true;
                }
            }

            // 3. Кнопка "Закрыть" внизу
            int closeBtnWidth = 80;
            int closeBtnHeight = 18;
            int closeBtnX = this.leftPos + (this.bgWidth - closeBtnWidth) / 2;
            int closeBtnY = this.topPos + this.bgHeight - 22;

            if (mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnWidth && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnHeight) {
                this.onClose();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Верхние вкладки
        renderTabButtons(guiGraphics, mouseX, mouseY);

        // Основное окно
        guiGraphics.fill(this.leftPos - 1, this.topPos - 1, this.leftPos + this.bgWidth + 1, this.topPos + this.bgHeight + 1, COLOR_BORDER);
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.bgWidth, this.topPos + this.bgHeight, COLOR_BG);

        // Контент выбранной вкладки
        switch (this.currentTab) {
            case PROFILE -> renderProfileTab(guiGraphics);
            case FACTIONS -> renderFactionsTab(guiGraphics, mouseX, mouseY);
            case PARTY -> renderPartyTab(guiGraphics);
        }

        // Кнопка закрытия
        renderCloseButton(guiGraphics, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderTabButtons(GuiGraphics g, int mouseX, int mouseY) {
        Tab[] tabs = Tab.values();
        int tabWidth = 72;
        int tabHeight = 18;
        int tabSpacing = 6;
        int totalTabsWidth = tabs.length * tabWidth + (tabs.length - 1) * tabSpacing;
        int startX = this.leftPos + (this.bgWidth - totalTabsWidth) / 2;
        int tabY = this.topPos - 22;

        for (int i = 0; i < tabs.length; i++) {
            Tab tab = tabs[i];
            int tx = startX + i * (tabWidth + tabSpacing);
            boolean isCurrent = (this.currentTab == tab);
            boolean isHovered = (mouseX >= tx && mouseX <= tx + tabWidth && mouseY >= tabY && mouseY <= tabY + tabHeight);

            int tabBg = isCurrent ? COLOR_INNER_BG : (isHovered ? 0xFF2A231F : 0xFF181513);
            int textColor = isCurrent ? COLOR_GOLD_TITLE : (isHovered ? COLOR_WHITE_TEXT : COLOR_MUTED_TEXT);

            g.fill(tx - 1, tabY - 1, tx + tabWidth + 1, tabY + tabHeight, COLOR_BORDER);
            g.fill(tx, tabY, tx + tabWidth, tabY + tabHeight, tabBg);

            String label = tab.getLabel();
            g.drawString(this.font, label, tx + (tabWidth - this.font.width(label)) / 2, tabY + 5, textColor, false);
        }
    }

    private void renderProfileTab(GuiGraphics g) {
        int x = this.leftPos + 14;
        int y = this.topPos + 12;

        g.drawString(this.font, "§6§lПРОФИЛЬ ПЕРСОНАЖА", x, y, COLOR_GOLD_TITLE, false);
        y += 15;

        String name = ClientProfileData.rpName.isEmpty()
                ? (this.minecraft.player != null ? this.minecraft.player.getName().getString() : "Неизвестный")
                : ClientProfileData.rpName;
        g.drawString(this.font, "§7Имя: §f" + name, x, y, COLOR_WHITE_TEXT, false);
        y += 11;

        String genderStr = ClientProfileData.gender != null ? ClientProfileData.gender.getDisplay() : "Мужской";
        g.drawString(this.font, "§7Пол: §f" + genderStr, x, y, COLOR_WHITE_TEXT, false);
        y += 11;

        if (!ClientProfileData.selectedTitle.isEmpty()) {
            g.drawString(this.font, "§7Титул: §e" + ClientProfileData.selectedTitle, x, y, COLOR_WHITE_TEXT, false);
            y += 11;
        }

        g.drawString(this.font, "§7Телосложение: §f" + ClientProfileData.build, x, y, COLOR_WHITE_TEXT, false);
        y += 11;

        g.drawString(this.font, "§7Внешность: §f" + ClientProfileData.eyes + " глаза, " + ClientProfileData.hair + " волосы", x, y, COLOR_WHITE_TEXT, false);
        y += 11;

        g.drawString(this.font, "§7Особые приметы: §f" + ClientProfileData.distinctiveFeatures, x, y, COLOR_WHITE_TEXT, false);
        y += 14;

        g.drawString(this.font, "§7Предыстория:", x, y, COLOR_MUTED_TEXT, false);
        y += 10;

        List<FormattedCharSequence> lines = this.font.split(Component.literal("§8" + ClientProfileData.backstory), this.bgWidth - 28);
        for (int i = 0; i < Math.min(lines.size(), 4); i++) {
            g.drawString(this.font, lines.get(i), x, y, COLOR_WHITE_TEXT, false);
            y += 9;
        }
    }

    private void renderFactionsTab(GuiGraphics g, int mouseX, int mouseY) {
        Faction[] factions = Faction.values();
        if (this.selectedFactionIndex < 0 || this.selectedFactionIndex >= factions.length) {
            this.selectedFactionIndex = 0;
        }
        Faction faction = factions[this.selectedFactionIndex];

        int arrowY = this.topPos + 12;
        int arrowSize = 14;

        // Левая стрелка
        int leftArrowX = this.leftPos + 14;
        boolean hoverLeft = (mouseX >= leftArrowX && mouseX <= leftArrowX + arrowSize && mouseY >= arrowY && mouseY <= arrowY + arrowSize);
        g.fill(leftArrowX, arrowY, leftArrowX + arrowSize, arrowY + arrowSize, hoverLeft ? 0xFF352B24 : COLOR_INNER_BG);
        g.drawString(this.font, "<", leftArrowX + 4, arrowY + 3, hoverLeft ? COLOR_WHITE_TEXT : COLOR_GOLD_TITLE, false);

        // Правая стрелка
        int rightArrowX = this.leftPos + this.bgWidth - 14 - arrowSize;
        boolean hoverRight = (mouseX >= rightArrowX && mouseX <= rightArrowX + arrowSize && mouseY >= arrowY && mouseY <= arrowY + arrowSize);
        g.fill(rightArrowX, arrowY, rightArrowX + arrowSize, arrowY + arrowSize, hoverRight ? 0xFF352B24 : COLOR_INNER_BG);
        g.drawString(this.font, ">", rightArrowX + 5, arrowY + 3, hoverRight ? COLOR_WHITE_TEXT : COLOR_GOLD_TITLE, false);

        // Верхняя строка названия фракции по центру
        Component loreName = faction.getFullLoreName();
        int titleX = this.leftPos + (this.bgWidth - this.font.width(loreName)) / 2;
        g.drawString(this.font, loreName, titleX, arrowY + 3, COLOR_WHITE_TEXT, false);

        int contentY = arrowY + 24;

        // Репутация и ранг
        int rep = ClientFactionData.getReputation(faction);
        PlayerProfileData.Gender gender = ClientProfileData.gender != null ? ClientProfileData.gender : PlayerProfileData.Gender.MALE;
        Component rankComponent = FactionRank.getTitle(faction, rep, gender);

        Component repComponent = Component.literal("§7Репутация: §f" + rep + " §8| §7Ранг: ").append(rankComponent);
        int repX = this.leftPos + (this.bgWidth - this.font.width(repComponent)) / 2;
        g.drawString(this.font, repComponent, repX, contentY, COLOR_WHITE_TEXT, false);
        contentY += 16;

        // Полоса прогресса
        int barWidth = 150;
        int barHeight = 6;
        int barX = this.leftPos + (this.bgWidth - barWidth) / 2;

        g.fill(barX - 1, contentY - 1, barX + barWidth + 1, contentY + barHeight + 1, COLOR_BORDER);
        g.fill(barX, contentY, barX + barWidth, contentY + barHeight, COLOR_BAR_BG);

        int prevTier = FactionRank.getPrevTierTarget(rep);
        int nextTier = FactionRank.getNextTierTarget(rep);
        float progress = 0.0f;
        if (nextTier > prevTier) {
            progress = Math.clamp((float) (rep - prevTier) / (nextTier - prevTier), 0.0f, 1.0f);
        }
        if (progress > 0) {
            g.fill(barX, contentY, barX + (int) (barWidth * progress), contentY + barHeight, COLOR_GOLD_TITLE);
        }
        contentY += 18;

        // Покровитель
        String patronText = "§8Покровитель сектора: §7" + faction.getBaronTitle();
        int patronX = this.leftPos + (this.bgWidth - this.font.width(patronText)) / 2;
        g.drawString(this.font, patronText, patronX, contentY, COLOR_WHITE_TEXT, false);
    }

    private void renderPartyTab(GuiGraphics g) {
        int x = this.leftPos + 14;
        int y = this.topPos + 12;

        g.drawString(this.font, "§6§lСОСТАВ ГРУППЫ", x, y, COLOR_GOLD_TITLE, false);
        y += 16;

        List<PartyMemberData> members = ClientPartyData.getMembers();
        if (members == null || members.isEmpty()) {
            g.drawString(this.font, "§8Вы не состоите в группе.", x, y, COLOR_MUTED_TEXT, false);
            y += 12;
            g.drawString(this.font, "§7Используйте §f/party invite <игрок>§7,", x, y, COLOR_WHITE_TEXT, false);
            y += 10;
            g.drawString(this.font, "§7чтобы собрать отряд союзников.", x, y, COLOR_WHITE_TEXT, false);
            return;
        }

        for (int i = 0; i < members.size(); i++) {
            PartyMemberData m = members.get(i);
            String info = "§f" + (i + 1) + ". " + m.getName() + " §7(HP: " + (int) m.getHealth() + "/" + (int) m.getMaxHealth() + ") " + (m.getTitle().isEmpty() ? "" : "§6[" + m.getTitle() + "]");
            g.drawString(this.font, info, x, y, COLOR_WHITE_TEXT, false);
            y += 12;
        }
    }

    private void renderCloseButton(GuiGraphics g, int mouseX, int mouseY) {
        int w = 80;
        int h = 18;
        int x = this.leftPos + (this.bgWidth - w) / 2;
        int y = this.topPos + this.bgHeight - 22;

        boolean hovered = (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h);
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, COLOR_BORDER);
        g.fill(x, y, x + w, y + h, hovered ? 0xFF2A231F : COLOR_INNER_BG);

        String text = "Закрыть";
        g.drawString(this.font, text, x + (w - this.font.width(text)) / 2, y + 5, hovered ? COLOR_WHITE_TEXT : COLOR_MUTED_TEXT, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
