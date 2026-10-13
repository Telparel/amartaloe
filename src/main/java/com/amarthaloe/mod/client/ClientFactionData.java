package com.amarthaloe.mod.client;

import com.amarthaloe.mod.faction.Faction;

import java.util.EnumMap;
import java.util.Map;

public class ClientFactionData {
    private static final Map<Faction, Integer> CLIENT_ALIGNMENTS = new EnumMap<>(Faction.class);
    private static Faction selectedFaction = Faction.ISONOMIA;

    public static void update(Map<Faction, Integer> data) {
        CLIENT_ALIGNMENTS.clear();
        CLIENT_ALIGNMENTS.putAll(data);
    }

    public static int getReputation(Faction faction) {
        return CLIENT_ALIGNMENTS.getOrDefault(faction, 0);
    }

    public static Faction getSelectedFaction() {
        return selectedFaction;
    }

    public static void setSelectedFaction(Faction faction) {
        selectedFaction = faction;
    }

    public static void cycleFaction() {
        Faction[] factions = Faction.values();
        int next = (selectedFaction.ordinal() + 1) % factions.length;
        selectedFaction = factions[next];
    }
}
