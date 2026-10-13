package com.amarthaloe.mod.client.gui;

import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.client.ClientPartyData;
import com.amarthaloe.mod.client.ClientProfileData;
import com.amarthaloe.mod.client.ModClientConfig;
import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import com.amarthaloe.mod.network.UpdateProfilePayload;
import com.amarthaloe.mod.party.PartyMemberData;
import com.amarthaloe.mod.profile.PlayerProfileData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class AmarthaloeMenuScreen extends Screen {
    private enum Tab { PASSPORT, TITLES, MAP, GROUPS, SETTINGS }
    private Tab currentTab = Tab.PASSPORT;

    // Поля ввода для создания персонажа / паспорта
    private EditBox nameInput;
    private EditBox heightWeightInput;
    private EditBox buildInput;
    private EditBox hairInput;
    private EditBox eyesInput;
    private EditBox featuresInput;
    private EditBox backstoryInput;

    // Поле ввода для приглашения в группу
    private EditBox partyInviteInput;

    private PlayerProfileData.Gender tempGender = ClientProfileData.gender;
    private String tempTitle = ClientProfileData.selectedTitle;

    public AmarthaloeMenuScreen() {
        super(Component.literal("Мир Амарталоэ"));
    }

    @Override
    protected void init() {
        int guiLeft = (this.width - 360) / 2;
        int guiTop = (this.height - 220) / 2;

        this.clearWidgets();

        // --- БОКОВЫЕ ВКЛАДКИ (ТАБЫ) ---
        int tabY = guiTop + 30;
        int tabW = 90;
        int tabH = 20;

        addRenderableWidget(Button.builder(Component.literal("Паспорт"), b -> switchTab(Tab.PASSPORT))
                .bounds(guiLeft + 8, tabY, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("Титулы"), b -> switchTab(Tab.TITLES))
                .bounds(guiLeft + 8, tabY + 24, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("§8Карта"), b -> switchTab(Tab.MAP))
                .bounds(guiLeft + 8, tabY + 48, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("Группы"), b -> switchTab(Tab.GROUPS))
                .bounds(guiLeft + 8, tabY + 72, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("Настройки"), b -> switchTab(Tab.SETTINGS))
                .bounds(guiLeft + 8, tabY + 96, tabW, tabH).build());

        // --- ВКЛАДКА "ПАСПОРТ" ---
        if (currentTab == Tab.PASSPORT) {
            int inputX = guiLeft + 195;
            int inputW = 150;
            int startY = guiTop + 30;

            nameInput = new EditBox(this.font, inputX, startY, inputW, 16, Component.literal("Имя"));
            nameInput.setValue(ClientProfileData.rpName);
            addRenderableWidget(nameInput);

            addRenderableWidget(Button.builder(Component.literal("Пол: " + tempGender.getDisplay()), b -> {
                tempGender = (tempGender == PlayerProfileData.Gender.MALE) ? PlayerProfileData.Gender.FEMALE : PlayerProfileData.Gender.MALE;
                b.setMessage(Component.literal("Пол: " + tempGender.getDisplay()));
            }).bounds(inputX, startY + 22, inputW, 18).build());

            heightWeightInput = new EditBox(this.font, inputX, startY + 44, inputW, 16, Component.literal("Рост/Вес"));
            heightWeightInput.setValue(ClientProfileData.heightWeight);
            addRenderableWidget(heightWeightInput);

            buildInput = new EditBox(this.font, inputX, startY + 64, inputW, 16, Component.literal("Телосложение"));
            buildInput.setValue(ClientProfileData.build);
            addRenderableWidget(buildInput);

            hairInput = new EditBox(this.font, inputX, startY + 84, inputW, 16, Component.literal("Волосы"));
            hairInput.setValue(ClientProfileData.hair);
            addRenderableWidget(hairInput);

            eyesInput = new EditBox(this.font, inputX, startY + 104, inputW, 16, Component.literal("Глаза"));
            eyesInput.setValue(ClientProfileData.eyes);
            addRenderableWidget(eyesInput);

            featuresInput = new EditBox(this.font, inputX, startY + 124, inputW, 16, Component.literal("Приметы"));
            featuresInput.setValue(ClientProfileData.distinctiveFeatures);
            addRenderableWidget(featuresInput);

            backstoryInput = new EditBox(this.font, inputX, startY + 144, inputW, 16, Component.literal("Квента"));
            backstoryInput.setValue(ClientProfileData.backstory);
            addRenderableWidget(backstoryInput);

            addRenderableWidget(Button.builder(Component.literal("§aСохранить паспорт"), b -> saveProfile())
                    .bounds(inputX, startY + 168, inputW, 20).build());
        }

        // --- ВКЛАДКА "ТИТУЛЫ" ---
        if (currentTab == Tab.TITLES) {
            int titleListX = guiLeft + 115;
            int titleListY = guiTop + 30;

            Faction[] factions = Faction.values();
            for (int i = 0; i < factions.length; i++) {
                Faction f = factions[i];
                int rep = ClientFactionData.getReputation(f);
                Component rankComp = FactionRank.getTitle(f, rep, tempGender);

                int btnY = titleListY + (i * 22);
                addRenderableWidget(Button.builder(Component.literal("Выбрать: ").append(rankComp), b -> {
                    tempTitle = rankComp.getString();
                    saveProfile();
                }).bounds(titleListX, btnY, 230, 18).build());
            }
        }

        // --- ВКЛАДКА "ГРУППЫ" ---
        if (currentTab == Tab.GROUPS) {
            int startX = guiLeft + 115;
            int startY = guiTop + 140;

            partyInviteInput = new EditBox(this.font, startX, startY, 130, 18, Component.literal("Ник игрока"));
            addRenderableWidget(partyInviteInput);

            addRenderableWidget(Button.builder(Component.literal("§aПригласить"), b -> {
                String target = partyInviteInput.getValue().trim();
                if (!target.isEmpty() && Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.connection.sendCommand("party invite " + target);
                    partyInviteInput.setValue("");
                }
            }).bounds(startX + 135, startY, 95, 18).build());

            if (ClientPartyData.isInParty()) {
                addRenderableWidget(Button.builder(Component.literal("§cПокинуть группу"), b -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.connection.sendCommand("party leave");
                    }
                }).bounds(startX, startY + 24, 230, 18).build());
            }
        }

        // --- ВКЛАДКА "НАСТРОЙКИ" ---
        if (currentTab == Tab.SETTINGS) {
            int startX = guiLeft + 115;
            int startY = guiTop + 40;

            addRenderableWidget(Button.builder(
                    Component.literal("Позиция HUD группы: §e" + ModClientConfig.partyHudPosition.getDisplay()),
                    b -> {
                        ModClientConfig.cyclePartyHudPosition();
                        b.setMessage(Component.literal("Позиция HUD группы: §e" + ModClientConfig.partyHudPosition.getDisplay()));
                    }
            ).bounds(startX, startY, 230, 20).build());
        }
    }

    private void switchTab(Tab tab) {
        this.currentTab = tab;
        this.init();
    }

    private void saveProfile() {
        if (nameInput != null) {
            ClientProfileData.rpName = nameInput.getValue();
            ClientProfileData.heightWeight = heightWeightInput.getValue();
            ClientProfileData.build = buildInput.getValue();
            ClientProfileData.hair = hairInput.getValue();
            ClientProfileData.eyes = eyesInput.getValue();
            ClientProfileData.distinctiveFeatures = featuresInput.getValue();
            ClientProfileData.backstory = backstoryInput.getValue();
        }
        ClientProfileData.gender = tempGender;
        ClientProfileData.selectedTitle = tempTitle;

        PacketDistributor.sendToServer(new UpdateProfilePayload(
                ClientProfileData.rpName,
                ClientProfileData.gender.name(),
                ClientProfileData.selectedTitle,
                ClientProfileData.heightWeight,
                ClientProfileData.build,
                ClientProfileData.hair,
                ClientProfileData.eyes,
                ClientProfileData.distinctiveFeatures,
                ClientProfileData.backstory
        ));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // 1. Вызываем базовую отрисовку кнопок
        super.render(g, mouseX, mouseY, partialTick);

        int guiLeft = (this.width - 360) / 2;
        int guiTop = (this.height - 220) / 2;
        int guiW = 360;
        int guiH = 220;

        // 2. Непрозрачный темный фон
        g.fill(guiLeft, guiTop, guiLeft + guiW, guiTop + guiH, 0xFF141519);
        g.renderOutline(guiLeft, guiTop, guiW, guiH, 0xFF525866);
        g.renderOutline(guiLeft + 2, guiTop + 2, guiW - 4, guiH - 4, 0xFF24272E);

        // 3. Плашка под боковые вкладки
        g.fill(guiLeft + 4, guiTop + 4, guiLeft + 104, guiTop + guiH - 4, 0xFF1B1D22);
        g.fill(guiLeft + 104, guiTop + 4, guiLeft + 106, guiTop + guiH - 4, 0xFF3E434D);

        // 4. Верхняя шапка окна
        g.fill(guiLeft + 106, guiTop + 4, guiLeft + guiW - 4, guiTop + 24, 0xFF1F2228);
        g.drawString(this.font, "§6АРХИВ АМАРТАЛОЭ §7» §f" + getTabTitle(currentTab), guiLeft + 114, guiTop + 9, 0xFFFFFF, true);

        // 5. Отрисовка текстового контента выбранной вкладки
        if (currentTab == Tab.PASSPORT) {
            int labelX = guiLeft + 114;
            int startY = guiTop + 34;
            renderLabel(g, "Имя (RP):", labelX, startY);
            renderLabel(g, "Пол:", labelX, startY + 24);
            renderLabel(g, "Рост / Вес:", labelX, startY + 46);
            renderLabel(g, "Телосложение:", labelX, startY + 66);
            renderLabel(g, "Волосы:", labelX, startY + 86);
            renderLabel(g, "Глаза:", labelX, startY + 106);
            renderLabel(g, "Приметы:", labelX, startY + 126);
            renderLabel(g, "Предыстория:", labelX, startY + 146);
        } else if (currentTab == Tab.GROUPS) {
            int startX = guiLeft + 114;
            int startY = guiTop + 32;
            g.drawString(this.font, "§6Состав группы (макс. 5 чел):", startX, startY, 0xFFFFFF, true);

            var members = ClientPartyData.getMembers();
            if (members.isEmpty()) {
                g.drawString(this.font, "§7Вы не состоите в группе.", startX, startY + 18, 0xAAAAAA, true);
            } else {
                for (int i = 0; i < members.size(); i++) {
                    PartyMemberData m = members.get(i);
                    String info = "§f" + (i + 1) + ". " + m.getName() + " §7(HP: " + (int) m.getHealth() + "/" + (int) m.getMaxHealth() + ") " + (m.getTitle().isEmpty() ? "" : "§6[" + m.getTitle() + "]");
                    g.drawString(this.font, info, startX, startY + 16 + (i * 18), 0xFFFFFF, true);
                }
            }
        } else if (currentTab == Tab.MAP) {
            g.drawCenteredString(this.font, "§e[Сектор генерации карты Амарталоэ строится...]", guiLeft + 230, guiTop + 100, 0xFFFFFF);
        } else if (currentTab == Tab.SETTINGS) {
            g.drawString(this.font, "§7Настройте положение индикаторов отряда под ваш экран.", guiLeft + 115, guiTop + 68, 0xAAAAAA, true);
        }
    }

    private void renderLabel(GuiGraphics g, String text, int x, int y) {
        g.drawString(this.font, "§f" + text, x, y, 0xFFFFFF, true);
    }

    private String getTabTitle(Tab tab) {
        return switch (tab) {
            case PASSPORT -> "Личное Дело / Паспорт";
            case TITLES -> "Заслуженные Титулы";
            case MAP -> "Карта Мира";
            case GROUPS -> "Отряды и Пати";
            case SETTINGS -> "Параметры и HUD";
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
