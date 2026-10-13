package com.amarthaloe.mod.client;

import com.amarthaloe.mod.party.PartyMemberData;

import java.util.ArrayList;
import java.util.List;

public class ClientPartyData {
    private static final List<PartyMemberData> members = new ArrayList<>();

    public static void update(List<PartyMemberData> newMembers) {
        members.clear();
        members.addAll(newMembers);
    }

    public static List<PartyMemberData> getMembers() {
        return members;
    }

    public static boolean isInParty() {
        return members.size() > 1;
    }
}
