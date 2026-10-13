package com.amarthaloe.mod.faction;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum Faction {
    ISONOMIA("isonomia", "Исономия", "Либра", ChatFormatting.WHITE),
    GLADIUS("gladius", "Гладиус", "Барон Войны", ChatFormatting.RED),
    ADEL("adel", "Адэль", "Барон Обжорства", ChatFormatting.DARK_AQUA),
    GNOSTERA("gnostera", "Гностера", "Барон Слияния", ChatFormatting.DARK_GREEN),
    MARIS("maris", "Марис", "Барон Тишины", ChatFormatting.BLUE),
    FULGORT("fulgort", "Фульгорт", "Барон Завоевания", ChatFormatting.GOLD),
    CALIGO("caligo", "Калиго", "Барон Печали", ChatFormatting.AQUA);

    private final String id;
    private final String displayName;
    private final String baronTitle;
    private final ChatFormatting color;

    Faction(String id, String displayName, String baronTitle, ChatFormatting color) {
        this.id = id;
        this.displayName = displayName;
        this.baronTitle = baronTitle;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getBaronTitle() {
        return baronTitle;
    }

    public Component getFormattedName() {
        return Component.literal(displayName).withStyle(color);
    }

    public Component getFullLoreName() {
        return Component.literal(displayName).withStyle(color)
                .append(Component.literal(" (" + baronTitle + ")").withStyle(ChatFormatting.DARK_GRAY));
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
