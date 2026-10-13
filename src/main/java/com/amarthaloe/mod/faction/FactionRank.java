package com.amarthaloe.mod.faction;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class FactionRank {
    public static final int[] TIERS = {-1000, -500, -100, -50, -10, 0, 10, 50, 100, 500, 1000};

    public static Component getTitle(Faction faction, int rep) {
        if (rep >= 1000) return Component.literal("Легенда").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        if (rep >= 500)  return Component.literal("Герой").withStyle(ChatFormatting.YELLOW);
        if (rep >= 100)  return Component.literal("Защитник").withStyle(ChatFormatting.GREEN);
        if (rep >= 50)   return Component.literal("Союзник").withStyle(ChatFormatting.DARK_GREEN);
        if (rep >= 10)   return Component.literal("Гость").withStyle(ChatFormatting.AQUA);
        if (rep > -10)   return Component.literal("Нейтрал").withStyle(ChatFormatting.GRAY);
        if (rep > -50)   return Component.literal("Подозрительный").withStyle(ChatFormatting.DARK_GRAY);
        if (rep > -100)  return Component.literal("Изгой").withStyle(ChatFormatting.RED);
        if (rep > -500)  return Component.literal("Враг").withStyle(ChatFormatting.DARK_RED);
        if (rep > -1000) return Component.literal("Палач").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
        return Component.literal("Бич региона").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD);
    }

    public static int getNextTierTarget(int rep) {
        if (rep >= 1000) return 1000;
        if (rep >= 500) return 1000;
        if (rep >= 100) return 500;
        if (rep >= 50) return 100;
        if (rep >= 10) return 50;
        if (rep >= 0) return 10;
        if (rep >= -10) return 0;
        if (rep >= -50) return -10;
        if (rep >= -100) return -50;
        if (rep >= -500) return -100;
        return -500;
    }

    public static int getPrevTierTarget(int rep) {
        if (rep >= 1000) return 500;
        if (rep >= 500) return 100;
        if (rep >= 100) return 50;
        if (rep >= 50) return 10;
        if (rep >= 10) return 0;
        if (rep >= 0) return -10;
        if (rep >= -10) return -50;
        if (rep >= -50) return -100;
        if (rep >= -100) return -500;
        return -1000;
    }
}
