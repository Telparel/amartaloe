package com.amarthaloe.mod.party;

import com.amarthaloe.mod.network.SyncPartyPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {

    private static final PartyManager INSTANCE = new PartyManager();

    // Карта: UUID игрока -> Список UUID всех участников группы
    private final Map<UUID, Set<UUID>> playerParties = new ConcurrentHashMap<>();
    // Карта приглашений: UUID приглашённого -> UUID пригласившего
    private final Map<UUID, UUID> pendingInvites = new ConcurrentHashMap<>();
    // Таймеры приглашений в тиках
    private final Map<UUID, Integer> inviteTimers = new ConcurrentHashMap<>();

    private PartyManager() {
    }

    public static PartyManager get() {
        return INSTANCE;
    }

    /**
     * Создание DTO данных участника для отправки клиенту
     */
    public PartyMemberData createMemberData(ServerPlayer player) {
        boolean hasDebuffs = player.getActiveEffects().stream()
                .anyMatch(effect -> !effect.getEffect().value().isBeneficial());

        String dimensionKey = player.level().dimension().location().getPath();

        return new PartyMemberData(
                player.getUUID(),
                player.getName().getString(),
                player.getHealth(),
                player.getMaxHealth(),
                hasDebuffs,
                dimensionKey
        );
    }

    /**
     * Получить список участников группы в виде DTO для игрока
     */
    public List<PartyMemberData> getParty(UUID playerUuid) {
        Set<UUID> memberUuids = playerParties.get(playerUuid);
        if (memberUuids == null || memberUuids.isEmpty()) {
            return Collections.emptyList();
        }

        List<PartyMemberData> result = new ArrayList<>();
        MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Collections.emptyList();
        }

        for (UUID uuid : memberUuids) {
            ServerPlayer member = server.getPlayerList().getPlayer(uuid);
            if (member != null) {
                result.add(createMemberData(member));
            }
        }
        return result;
    }

    /**
     * Синхронизация группы со всеми её участниками по сети
     */
    public void syncParty(UUID anyMemberUuid) {
        Set<UUID> memberUuids = playerParties.get(anyMemberUuid);
        if (memberUuids == null) {
            return;
        }

        List<PartyMemberData> membersData = getParty(anyMemberUuid);
        MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        SyncPartyPayload payload = new SyncPartyPayload(membersData);
        for (UUID uuid : memberUuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }

    /**
     * Создать группу или добавить игрока
     */
    public void addPlayerToParty(UUID inviterUuid, UUID invitedUuid) {
        Set<UUID> party = playerParties.computeIfAbsent(inviterUuid, k -> ConcurrentHashMap.newKeySet());
        party.add(inviterUuid);
        party.add(invitedUuid);

        // Связываем пати со всеми участниками
        for (UUID member : party) {
            playerParties.put(member, party);
        }

        pendingInvites.remove(invitedUuid);
        inviteTimers.remove(invitedUuid);

        syncParty(inviterUuid);
    }

    /**
     * Удалить игрока из группы
     */
    public void removePlayerFromParty(UUID playerUuid) {
        Set<UUID> party = playerParties.remove(playerUuid);
        if (party != null) {
            party.remove(playerUuid);

            // Отправляем пустое пати вышедшему игроку
            MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);
                if (player != null) {
                    PacketDistributor.sendToPlayer(player, new SyncPartyPayload(Collections.emptyList()));
                }
            }

            if (party.size() <= 1) {
                // Если остался один — распускаем
                for (UUID remaining : party) {
                    playerParties.remove(remaining);
                    if (server != null) {
                        ServerPlayer remainingPlayer = server.getPlayerList().getPlayer(remaining);
                        if (remainingPlayer != null) {
                            PacketDistributor.sendToPlayer(remainingPlayer, new SyncPartyPayload(Collections.emptyList()));
                        }
                    }
                }
            } else {
                syncParty(party.iterator().next());
            }
        }
    }

    /**
     * Отправка инвайта
     */
    public void sendInvite(UUID from, UUID to) {
        pendingInvites.put(to, from);
        inviteTimers.put(to, 600); // 30 секунд (600 тиков)
    }

    public UUID getPendingInvite(UUID playerUuid) {
        return pendingInvites.get(playerUuid);
    }

    /**
     * Серверный тик: очистка просроченных приглашений и фоновая синхронизация HP/дебаффов
     */
    public void tick() {
        if (!inviteTimers.isEmpty()) {
            Iterator<Map.Entry<UUID, Integer>> it = inviteTimers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Integer> entry = it.next();
                int timeLeft = entry.getValue() - 1;
                if (timeLeft <= 0) {
                    pendingInvites.remove(entry.getKey());
                    it.remove();
                } else {
                    entry.setValue(timeLeft);
                }
            }
        }
    }
}
