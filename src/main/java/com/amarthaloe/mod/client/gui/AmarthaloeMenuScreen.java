package com.amarthaloe.mod.client.gui;

import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.client.ClientProfileData;
import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import com.amarthaloe.mod.network.UpdateProfilePayload;
import com.amarthaloe.mod.profile.PlayerProfileData;
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

        // --- БОКОВЫЕ КНОПКИ НАВИГАЦИИ (ТАБЫ) ---
        int tabY = guiTop + 30;
        int tabW = 90;
        int tabH = 20;

        addRenderableWidget(Button.builder(Component.literal("Паспорт"), b -> switchTab(Tab.PASSPORT))
                .bounds(guiLeft + 8, tabY, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("Титулы"), b -> switchTab(Tab.TITLES))
                .bounds(guiLeft + 8, tabY + 24, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("§8Карта"), b -> switchTab(Tab.MAP))
                .bounds(guiLeft + 8, tabY + 48, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("§8Группы"), b -> switchTab(Tab.GROUPS))
                .bounds(guiLeft + 8, tabY + 72, tabW, tabH).build());
        addRenderableWidget(Button.builder(Component.literal("Настройки"), b -> switchTab(Tab.SETTINGS))
                .bounds(guiLeft + 8, tabY + 96, tabW, tabH).build());

        // --- СОДЕРЖИМОЕ ВКЛАДКИ "ПАСПОРТ / СОЗДАНИЕ" ---
        if (currentTab == Tab.PASSPORT) {
            int inputX = guiLeft + 195;
            int inputW = 150;
            int startY = guiTop + 30;

            nameInput = new EditBox(this.font, inputX, startY, inputW, 16, Component.literal("Имя"));
            nameInput.setValue(ClientProfileData.rpName);
            addRenderableWidget(nameInput);

            // Кнопка переключения пола
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

            // Кнопка сохранения
            addRenderableWidget(Button.builder(Component.literal("§aСохранить паспорт"), b -> saveProfile())
                    .bounds(inputX, startY + 168, inputW, 20).build());
        }

        // --- СОДЕРЖИМОЕ ВКЛАДКИ "ТИТУЛЫ" ---
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

        // Отправка пакета на сервер
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
        int guiLeft = (this.width - 360) / 2;
        int guiTop = (this.height - 220) / 2;
        int guiW = 360;
        int guiH = 220;

        // Фон интерфейса
        g.fill(guiLeft, guiTop, guiLeft + guiW, guiTop + guiH, 0xF2121316);
        g.renderOutline(guiLeft, guiTop, guiW, guiH, 0xFF353942);

        // Разделительная вертикальная линия табов
        g.fill(guiLeft + 105, guiTop + 4, guiLeft + 106, guiTop + guiH - 4, 0xFF2A2D35);

        // Заголовок окна
        g.drawString(this.font, "§6АРХИВ АМАРТАЛОЭ §7| §f" + getTabTitle(currentTab), guiLeft + 12, guiTop + 10, 0xFFFFFF, true);

        // Подписи полей паспорта
        if (currentTab == Tab.PASSPORT) {
            int labelX = guiLeft + 115;
            int startY = guiTop + 34;

            g.drawString(this.font, "§7Имя (RP):", labelX, startY, 0xAAAAAA, false);
            g.drawString(this.font, "§7Пол:", labelX, startY + 24, 0xAAAAAA, false);
            g.drawString(this.font, "§7Рост / Вес:", labelX, startY + 46, 0xAAAAAA, false);
            g.drawString(this.font, "§7Телосложение:", labelX, startY + 66, 0xAAAAAA, false);
            g.drawString(this.font, "§7Волосы:", labelX, startY + 86, 0xAAAAAA, false);
            g.drawString(this.font, "§7Глаза:", labelX, startY + 106, 0xAAAAAA, false);
            g.drawString(this.font, "§7Приметы:", labelX, startY + 126, 0xAAAAAA, false);
            g.drawString(this.font, "§7Предыстория:", labelX, startY + 146, 0xAAAAAA, false);
        } else if (currentTab == Tab.MAP) {
            g.drawCenteredString(this.font, "§7[Сектор генерации карты Амарталоэ строится...]", guiLeft + 230, guiTop + 100, 0x888888);
        } else if (currentTab == Tab.GROUPS) {
            g.drawCenteredString(this.font, "§7[Система фракционных отрядов строится...]", guiLeft + 230, guiTop + 100, 0x888888);
        } else if (currentTab == Tab.SETTINGS) {
            g.drawCenteredString(this.font, "§7[Настройки интерфейса и уведомлений...]", guiLeft + 230, guiTop + 100, 0x888888);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    private String getTabTitle(Tab tab) {
        return switch (tab) {
            case PASSPORT -> "Личное Дело / Паспорт";
            case TITLES -> "Заслуженные Титулы";
            case MAP -> "Карта Мира";
            case GROUPS -> "Отряды и Союзы";
            case SETTINGS -> "Параметры";
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
