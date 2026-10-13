package com.amarthaloe.mod.party;

import java.util.UUID;

public class PartyMemberData {
    private final UUID uuid;
    private final String name;
    private final String title;
    private float health;
    private float maxHealth;
    private int foodLevel;

    public PartyMemberData(UUID uuid, String name, String title, float health, float maxHealth, int foodLevel) {
        this.uuid = uuid;
        this.name = name;
        this.title = title;
        this.health = health;
        this.maxHealth = maxHealth;
        this.foodLevel = foodLevel;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }
    public float getMaxHealth() { return maxHealth; }
    public void setMaxHealth(float maxHealth) { this.maxHealth = maxHealth; }
    public int getFoodLevel() { return foodLevel; }
    public void setFoodLevel(int foodLevel) { this.foodLevel = foodLevel; }
}
