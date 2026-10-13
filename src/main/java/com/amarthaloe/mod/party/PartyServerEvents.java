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

        var server = serverPlayer.getServer();
        if (server == null) {
            return;
        }

        List<PartyMemberData> party = PartyManager.getParty(serverPlayer.getUUID(), server);
        if (party == null || party.isEmpty()) {
            return;
        }

        List<ServerPlayer> nearbyMembers = new ArrayList<>();
        nearbyMembers.add(serverPlayer);

        for (PartyMemberData memberData : party) {
            if (memberData.getUuid().equals(serverPlayer.getUUID())) {
                continue;
            }
            ServerPlayer member = server.getPlayerList().getPlayer(memberData.getUuid());
            if (member != null && member.isAlive()
                    && member.level().dimension() == serverPlayer.level().dimension()
                    && member.distanceToSqr(serverPlayer) <= EXP_SHARE_RADIUS_SQ) {
                nearbyMembers.add(member);
            }
        }

        if (nearbyMembers.size() <= 1) {
            return;
        }

        int originalXp = event.getOrb().getValue();
        if (originalXp <= 0) {
            return;
        }

        double bonusMultiplier = 1.0 + (nearbyMembers.size() - 1) * 0.15;
        int totalXp = (int) Math.round(originalXp * bonusMultiplier);
        int sharedXp = Math.max(1, totalXp / nearbyMembers.size());

        for (ServerPlayer member : nearbyMembers) {
            member.giveExperiencePoints(sharedXp);
        }

        event.getOrb().discard();
        event.setCanceled(true);
    }
}
