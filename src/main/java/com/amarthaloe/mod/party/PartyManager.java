package com.amarthaloe.mod.party;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.network.SyncPartyPayload;
import com.amarthaloe.mod.profile.PlayerProfileData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class PartyManager {
    public static final int MAX_PARTY_MEMBERS = 5;

    // UUID игрока -> UUID группы
    private static final Map<UUID, UUID> playerParties = new HashMap<>();
    // UUID группы -> список UUID участников (первый - лидер)
    private static final Map<UUID, List<UUID>> parties = new HashMap<>();
    // UUID приглашаемого -> UUID пригласившего игрока
    private static final Map<UUID, UUID> pendingInvites = new HashMap<>();

    public static boolean areInSameParty(UUID u1, UUID u2) {
        if (u1.equals(u2)) return false;
        UUID p1 = playerParties.get(u1);
        UUID p2 = playerParties.get(u2);
        return p1 != null && p1.equals(p2);
    }

    public static void invitePlayer(ServerPlayer leader, ServerPlayer target) {
        if (leader.getUUID().equals(target.getUUID())) {
            leader.sendSystemMessage(Component.literal("§cНельзя пригласить самого себя!"));
            return;
        }

        UUID partyId = playerParties.get(leader.getUUID());
        if (partyId != null && parties.get(partyId).size() >= MAX_PARTY_MEMBERS) {
            leader.sendSystemMessage(Component.literal("§cГруппа уже заполнена (максимум " + MAX_PARTY_MEMBERS + " человек)!"));
            return;
        }

        if (playerParties.containsKey(target.getUUID())) {
            leader.sendSystemMessage(Component.literal("§cЭтот игрок уже состоит в группе!"));
            return;
        }

        pendingInvites.put(target.getUUID(), leader.getUUID());
        leader.sendSystemMessage(Component.literal("§aПриглашение отправлено игроку §e" + target.getName().getString()));
        target.sendSystemMessage(Component.literal("§6" + leader.getName().getString() + " §aприглашает вас в группу! Введите §e/party accept§a."));
    }

    public static void acceptInvite(ServerPlayer target) {
        UUID leaderUuid = pendingInvites.remove(target.getUUID());
        if (leaderUuid == null) {
            target.sendSystemMessage(Component.literal("§cУ вас нет активных приглашений в группу!"));
            return;
        }

        ServerPlayer leader = target.server.getPlayerList().getPlayer(leaderUuid);
        if (leader == null) {
            target.sendSystemMessage(Component.literal("§cЛидер группы не в сети!"));
            return;
        }

        UUID partyId = playerParties.get(leaderUuid);
        if (partyId == null) {
            // Создаем новую группу
            partyId = UUID.randomUUID();
            parties.put(partyId, new ArrayList<>(List.of(leaderUuid)));
            playerParties.put(leaderUuid, partyId);
        }

        List<UUID> members = parties.get(partyId);
        if (members.size() >= MAX_PARTY_MEMBERS) {
            target.sendSystemMessage(Component.literal("§cВ группе больше нет свободных мест!"));
            return;
        }

        members.add(target.getUUID());
        playerParties.put(target.getUUID(), partyId);

        broadcastPartyMessage(target.server, partyId, "§e" + target.getName().getString() + " §aприсоединился к группе!");
        syncParty(target.server, partyId);
    }

    public static void leaveParty(ServerPlayer player) {
        UUID partyId = playerParties.remove(player.getUUID());
        if (partyId == null) {
            player.sendSystemMessage(Component.literal("§cВы не состоите в группе!"));
            return;
        }

        List<UUID> members = parties.get(partyId);
        if (members != null) {
            members.remove(player.getUUID());
            // Отправляем пустой список покинувшему игроку
            PacketDistributor.sendToPlayer(player, new SyncPartyPayload(Collections.emptyList()));

            if (members.isEmpty()) {
                parties.remove(partyId);
            } else {
                broadcastPartyMessage(player.server, partyId, "§c" + player.getName().getString() + " покинул группу.");
                syncParty(player.server, partyId);
            }
        }
    }

    public static void syncParty(net.minecraft.server.MinecraftServer server, UUID partyId) {
        List<UUID> members = parties.get(partyId);
        if (members == null) return;

        List<PartyMemberData> memberDataList = new ArrayList<>();
        for (UUID u : members) {
            ServerPlayer sp = server.getPlayerList().getPlayer(u);
            if (sp != null) {
                PlayerProfileData prof = sp.getData(FactionAttachments.PROFILE_DATA);
                String display = prof.getRpName().isBlank() ? sp.getName().getString() : prof.getRpName();
                memberDataList.add(new PartyMemberData(
                        sp.getUUID(),
                        display,
                        prof.getSelectedTitle(),
                        sp.getHealth(),
                        sp.getMaxHealth(),
                        sp.getFoodData().getFoodLevel()
                ));
            }
        }

        SyncPartyPayload payload = new SyncPartyPayload(memberDataList);
        for (UUID u : members) {
            ServerPlayer sp = server.getPlayerList().getPlayer(u);
            if (sp != null) {
                PacketDistributor.sendToPlayer(sp, payload);
            }
        }
    }

    private static void broadcastPartyMessage(net.minecraft.server.MinecraftServer server, UUID partyId, String message) {
        List<UUID> members = parties.get(partyId);
        if (members == null) return;
        for (UUID u : members) {
            ServerPlayer sp = server.getPlayerList().getPlayer(u);
            if (sp != null) {
                sp.sendSystemMessage(Component.literal("§6[Пати] " + message));
            }
        }
    }

    public static List<UUID> getPartyMembers(UUID playerUuid) {
        UUID partyId = playerParties.get(playerUuid);
        if (partyId == null) return Collections.emptyList();
        return parties.getOrDefault(partyId, Collections.emptyList());
    }
}
