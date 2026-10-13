package com.amarthaloe.mod.client;

import com.amarthaloe.mod.client.gui.TutorialScreen;
import com.amarthaloe.mod.network.SyncProfilePayload;
import com.amarthaloe.mod.profile.PlayerProfileData;
import net.minecraft.client.Minecraft;

public class ClientProfileData {
    public static String rpName = "";
    public static PlayerProfileData.Gender gender = PlayerProfileData.Gender.MALE;
    public static String selectedTitle = "";
    public static String heightWeight = "175 см / 70 кг";
    public static String build = "Среднее";
    public static String hair = "Тёмные";
    public static String eyes = "Карие";
    public static String distinctiveFeatures = "Шрам через бровь";
    public static String backstory = "Один из выживших на руинах старого мира...";

    public static void update(SyncProfilePayload payload) {
        rpName = payload.rpName();
        try {
            gender = PlayerProfileData.Gender.valueOf(payload.gender());
        } catch (Exception ignored) {}
        selectedTitle = payload.selectedTitle();
        heightWeight = payload.heightWeight();
        build = payload.build();
        hair = payload.hair();
        eyes = payload.eyes();
        distinctiveFeatures = payload.distinctiveFeatures();
        backstory = payload.backstory();

        // Запуск приветственного баннера
        WelcomeToastOverlay.trigger();

        // Если это первый заход игрока — открываем обучающее окно
        if (payload.isFirstJoin()) {
            Minecraft.getInstance().tell(() -> {
                Minecraft.getInstance().setScreen(new TutorialScreen());
            });
        }
    }
}
