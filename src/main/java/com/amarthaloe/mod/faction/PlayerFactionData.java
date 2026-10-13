package com.amarthaloe.mod.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class PlayerFactionData {
    private final Map<Faction, Integer> alignments = new EnumMap<>(Faction.class);

    public PlayerFactionData() {
        for (Faction f : Faction.values()) {
            alignments.put(f, 0);
        }
    }

    public int getReputation(Faction faction) {
        return alignments.getOrDefault(faction, 0);
    }

    public void setReputation(Faction faction, int value) {
        alignments.put(faction, value);
    }

    public void addReputation(Faction faction, int delta) {
        setReputation(faction, getReputation(faction) + delta);
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        for (Faction f : Faction.values()) {
            tag.putInt(f.getId(), alignments.getOrDefault(f, 0));
        }
        return tag;
    }

    public static PlayerFactionData loadNBT(CompoundTag tag) {
        PlayerFactionData data = new PlayerFactionData();
        for (Faction f : Faction.values()) {
            if (tag.contains(f.getId())) {
                data.setReputation(f, tag.getInt(f.getId()));
            }
        }
        return data;
    }
}
