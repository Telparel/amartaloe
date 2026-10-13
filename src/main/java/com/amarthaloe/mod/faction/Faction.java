package com.amarthaloe.mod.faction;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum Faction {
    ISONOMIA("isonomia", "Исономия", ChatFormatting.DARK_GRAY),
    CALIGO("caligo", "Калиго", ChatFormatting.AQUA),
    GNOSTERA("gnostera", "Гностера", ChatFormatting.DARK_GREEN),
    GLADIUS("gladius", "Гладиус", ChatFormatting.RED),
    MARIS("maris", "Марис", ChatFormatting.BLUE),
    FULGORT("fulgort", "Фульгорт", ChatFormatting.GOLD),
    ADEL("adel", "Адэль", ChatFormatting.DARK_AQUA);

    private final String id;
    private final String displayName;
    private final ChatFormatting color;

    Faction(String id, String displayName, ChatFormatting color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public Component getFormattedName() {
        return Component.literal(displayName).withStyle(color);
    }

    public static Faction fromId(String id) {
        for (Faction faction : values()) {
            if (faction.id.equalsIgnoreCase(id)) {
                return faction;
            }
        }
        return null;
    }
}
