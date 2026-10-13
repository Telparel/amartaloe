@Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // 1. Сначала вызываем виджеты/кнопки, чтобы базовые слои не накладывали блюр поверх текста
        super.render(g, mouseX, mouseY, partialTick);

        int guiLeft = (this.width - 360) / 2;
        int guiTop = (this.height - 220) / 2;
        int guiW = 360;
        int guiH = 220;

        // 2. Плотный контрастный фон (непрозрачный темный графит)
        g.fill(guiLeft, guiTop, guiLeft + guiW, guiTop + guiH, 0xFF141519);
        // Металлическая внешняя и внутренняя окантовка
        g.renderOutline(guiLeft, guiTop, guiW, guiH, 0xFF525866);
        g.renderOutline(guiLeft + 2, guiTop + 2, guiW - 4, guiH - 4, 0xFF24272E);

        // 3. Отдельная плашка под левые вкладки
        g.fill(guiLeft + 4, guiTop + 4, guiLeft + 104, guiTop + guiH - 4, 0xFF1B1D22);
        g.fill(guiLeft + 104, guiTop + 4, guiLeft + 106, guiTop + guiH - 4, 0xFF3E434D);

        // 4. Верхняя шапка окна
        g.fill(guiLeft + 106, guiTop + 4, guiLeft + guiW - 4, guiTop + 24, 0xFF1F2228);
        g.drawString(this.font, "§6АРХИВ АМАРТАЛОЭ §7» §f" + getTabTitle(currentTab), guiLeft + 114, guiTop + 9, 0xFFFFFF, true);

        // 5. Четкие контрастные подписи полей (светло-серый/белый с четкой тенью)
        if (currentTab == Tab.PASSPORT) {
            int labelX = guiLeft + 114;
            int startY = guiTop + 34;

            // Подложка под метки для идеальной читаемости
            renderLabel(g, "Имя (RP):", labelX, startY);
            renderLabel(g, "Пол:", labelX, startY + 24);
            renderLabel(g, "Рост / Вес:", labelX, startY + 46);
            renderLabel(g, "Телосложение:", labelX, startY + 66);
            renderLabel(g, "Волосы:", labelX, startY + 86);
            renderLabel(g, "Глаза:", labelX, startY + 106);
            renderLabel(g, "Приметы:", labelX, startY + 126);
            renderLabel(g, "Предыстория:", labelX, startY + 146);
        } else if (currentTab == Tab.MAP) {
            g.drawCenteredString(this.font, "§e[Сектор генерации карты Амарталоэ строится...]", guiLeft + 230, guiTop + 100, 0xFFFFFF);
        } else if (currentTab == Tab.GROUPS) {
            g.drawCenteredString(this.font, "§e[Система фракционных отрядов строится...]", guiLeft + 230, guiTop + 100, 0xFFFFFF);
        } else if (currentTab == Tab.SETTINGS) {
            g.drawCenteredString(this.font, "§e[Настройки интерфейса и уведомлений...]", guiLeft + 230, guiTop + 100, 0xFFFFFF);
        }
    }

    private void renderLabel(GuiGraphics g, String text, int x, int y) {
        g.drawString(this.font, "§f" + text, x, y, 0xFFFFFF, true);
    }
