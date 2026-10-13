package com.amarthaloe.mod.client;

import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionRank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.EnumMap;
import java.util.Map;

public class ClientFactionData {
    private static final Map<Faction, Integer> CLIENT_ALIGNMENTS = new EnumMap<>(Faction.class);
    private static Faction selectedFaction = Faction.ISONOMIA;

    // Параметры плавной анимации перехода
    public static float animOffset = 0.0f; // сдвиг по X при переключении
    public static float animProgress = 0.0f; // плавная интерполяция полосы

    public static void update(Map<Faction, Integer> data) {
        for (Map.Entry<Faction, Integer> entry : data.entrySet()) {
            Faction f = entry.getKey();
            int newRep = entry.getValue();
            if (CLIENT_ALIGNMENTS.containsKey(f)) {
                int oldRep = CLIENT_ALIGNMENTS.get(f);
                int oldTier = FactionRank.getTierIndex(oldRep);
                int newTier = FactionRank.getTierIndex(newRep);

                // Если ранг изменился — запускаем баннер в стиле WoW
                if (oldTier != newTier) {
                    boolean isPromotion = newTier > oldTier;
                    RankBannerOverlay.trigger(f, newRep, isPromotion);

                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player != null) {
                        if (isPromotion) {
                            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F));
                            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 0.9F));
                        } else {
                            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 0.6F));
                            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WITHER_SPAWN, 0.5F, 0.5F));
                        }
                    }
                }
            }
        }
        CLIENT_ALIGNMENTS.clear();
        CLIENT_ALIGNMENTS.putAll(data);
    }

    public static int getReputation(Faction faction) {
        return CLIENT_ALIGNMENTS.getOrDefault(faction, 0);
    }

    public static Faction getSelectedFaction() {
        return selectedFaction;
    }

    public static void cycleLeft() {
        Faction[] factions = Faction.values();
        int prev = (selectedFaction.ordinal() - 1 + factions.length) % factions.length;
        selectedFaction = factions[prev];
        animOffset = -22.0f; // толчок влево
        playClick();
    }

    public static void cycleRight() {
        Faction[] factions = Faction.values();
        int next = (selectedFaction.ordinal() + 1) % factions.length;
        selectedFaction = factions[next];
        animOffset = 22.0f; // толчок вправо
        playClick();
    }

    private static void playClick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.4F, 0.25F));
        }
    }
}
