package com.amarthaloe.mod.party;

import com.amarthaloe.mod.network.SyncPartyPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {

    private static final Map<UUID, Set<UUID>> parties = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> pendingInvites = new ConcurrentHashMap<>();

    public static void invitePlayer(ServerPlayer leader, ServerPlayer target) {
        pendingInvites.put(target.getUUID(), leader.getUUID());
    }

    public static void acceptInvite(ServerPlayer player) {
        UUID inviterUuid = pendingInvites.remove(player.getUUID());
        if (inviterUuid != null) {
            ServerPlayer leader = player.server.getPlayerList().getPlayer(inviterUuid);
            if (leader != null) {
                Set<UUID> party = parties.computeIfAbsent(inviterUuid, k -> ConcurrentHashMap.newKeySet());
                party.add(inviterUuid);
                party.add(player.getUUID());
                parties.put(player.getUUID(), party);
                syncParty(party, player.server);
            }
        }
    }

    public static void leaveParty(ServerPlayer player) {
        Set<UUID> party = parties.remove(player.getUUID());
        if (party != null) {
            party.remove(player.getUUID());
            PacketDistributor.sendToPlayer(player, new SyncPartyPayload(Collections.emptyList()));

            if (party.size() <= 1) {
                for (UUID memberUuid : party) {
                    parties.remove(memberUuid);
                    ServerPlayer remaining = player.server.getPlayerList().getPlayer(memberUuid);
                    if (remaining != null) {
                        PacketDistributor.sendToPlayer(remaining, new SyncPartyPayload(Collections.emptyList()));
                    }
                }
            } else {
                syncParty(party, player.server);
            }
        }
    }

    public static List<PartyMemberData> getParty(UUID playerUuid, net.minecraft.server.MinecraftServer server) {
        Set<UUID> memberUuids = parties.get(playerUuid);
        if (memberUuids == null || server == null) {
            return Collections.emptyList();
        }

        List<PartyMemberData> list = new ArrayList<>();
        for (UUID uuid : memberUuids) {
            ServerPlayer member = server.getPlayerList().getPlayer(uuid);
            if (member != null) {
                list.add(new PartyMemberData(
                        member.getUUID(),
                        member.getName().getString(),
                        "",
                        member.getHealth(),
                        member.getMaxHealth(),
                        member.getFoodData().getFoodLevel()
                ));
            }
        }
        return list;
    }

    public static void syncParty(Set<UUID> party, net.minecraft.server.MinecraftServer server) {
        if (party == null || server == null) return;
        List<PartyMemberData> dataList = new ArrayList<>();
        for (UUID id : party) {
            ServerPlayer member = server.getPlayerList().getPlayer(id);
            if (member != null) {
                dataList.add(new PartyMemberData(
                        member.getUUID(),
                        member.getName().getString(),
                        "",
                        member.getHealth(),
                        member.getMaxHealth(),
                        member.getFoodData().getFoodLevel()
                ));
            }
        }
        SyncPartyPayload payload = new SyncPartyPayload(dataList);
        for (UUID id : party) {
            ServerPlayer member = server.getPlayerList().getPlayer(id);
            if (member != null) {
                PacketDistributor.sendToPlayer(member, payload);
            }
        }
    }

    public static void tick() {
        // Фоновый тик при необходимости
    }
}
