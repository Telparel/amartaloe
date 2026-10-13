package com.amarthaloe.mod.party;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "amarthaloe", bus = EventBusSubscriber.Bus.GAME)
public class PartyServerEvents {

    private static final double EXP_SHARE_RADIUS = 48.0;
    private static final double EXP_SHARE_RADIUS_SQ = EXP_SHARE_RADIUS * EXP_SHARE_RADIUS;

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Проверяем наличие пати
        var partyManager = PartyManager.get();
        if (partyManager == null) {
            return;
        }

        var party = partyManager.getParty(serverPlayer.getUUID());
        if (party == null || party.isEmpty()) {
            return;
        }

        // Ищем союзников в радиусе и в том же измерении
        List<ServerPlayer> nearbyMembers = new ArrayList<>();
        nearbyMembers.add(serverPlayer);

        var server = serverPlayer.getServer();
        if (server == null) {
            return;
        }

        for (PartyMemberData memberData : party) {
            if (memberData.uuid().equals(serverPlayer.getUUID())) {
                continue;
            }
            ServerPlayer member = server.getPlayerList().getPlayer(memberData.uuid());
            if (member != null && member.isAlive() 
                    && member.level().dimension() == serverPlayer.level().dimension()
                    && member.distanceToSqr(serverPlayer) <= EXP_SHARE_RADIUS_SQ) {
                nearbyMembers.add(member);
            }
        }

        // Если союзников рядом нет — ванильный подбор
        if (nearbyMembers.size() <= 1) {
            return;
        }

        int originalXp = event.getOrb().getValue();
        if (originalXp <= 0) {
            return;
        }

        // Бонус +15% за каждого дополнительного союзника
        double bonusMultiplier = 1.0 + (nearbyMembers.size() - 1) * 0.15;
        int totalXp = (int) Math.round(originalXp * bonusMultiplier);
        int sharedXp = Math.max(1, totalXp / nearbyMembers.size());

        // Распределяем опыт между всеми участниками рядом
        for (ServerPlayer member : nearbyMembers) {
            member.giveExperiencePoints(sharedXp);
        }

        // Отменяем стандартное начисление игроку (сферу удаляем вручную)
        event.getOrb().discard();
        event.setCanceled(true);
    }
}
