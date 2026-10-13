package com.amarthaloe.mod.party;

import java.util.UUID;

public class PartyMemberData {
    private final UUID uuid;
    private final String name;
    private final String title;
    private final float health;
    private final float maxHealth;
    private final int foodLevel;

    public PartyMemberData(UUID uuid, String name, String title, float health, float maxHealth, int foodLevel) {
        this.uuid = uuid;
        this.name = name;
        this.title = title != null ? title : "";
        this.health = health;
        this.maxHealth = maxHealth;
        this.foodLevel = foodLevel;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public int getFoodLevel() {
        return foodLevel;
    }
}
