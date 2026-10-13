package com.amarthaloe.mod.faction;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.EnumMap;
import java.util.Map;

public class PlayerFactionData implements INBTSerializable<CompoundTag> {
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

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (Faction f : Faction.values()) {
            tag.putInt(f.getId(), alignments.getOrDefault(f, 0));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        for (Faction f : Faction.values()) {
            if (tag.contains(f.getId())) {
                alignments.put(f, tag.getInt(f.getId()));
            }
        }
    }
}
