package com.amarthaloe.mod;

import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.client.FactionGuiOverlay;
import com.amarthaloe.mod.client.PartyHudOverlay;
import com.amarthaloe.mod.client.RankBannerOverlay;
import com.amarthaloe.mod.client.WelcomeToastOverlay;
import com.amarthaloe.mod.client.gui.AmarthaloeMenuScreen;
import com.amarthaloe.mod.command.FactionCommands;
import com.amarthaloe.mod.command.PartyCommands;
import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.faction.PlayerFactionData;
import com.amarthaloe.mod.network.CompleteTutorialPayload;
import com.amarthaloe.mod.network.SyncFactionDataPayload;
import com.amarthaloe.mod.network.SyncPartyPayload;
import com.amarthaloe.mod.network.SyncProfilePayload;
import com.amarthaloe.mod.network.TeleportToSectorPayload;
import com.amarthaloe.mod.network.UpdateProfilePayload;
import com.amarthaloe.mod.party.PartyManager;
import com.amarthaloe.mod.profile.PlayerProfileData;
import com.amarthaloe.mod.world.AmarthaloeBiomeSource;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.BiomeSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.Map;

@Mod(AmarthaloeMod.MODID)
public class AmarthaloeMod {
    public static final String MODID = "amarthaloe";
    public static KeyMapping FACTION_PREV_KEY;
    public static KeyMapping FACTION_NEXT_KEY;
    public static KeyMapping MENU_KEY;

    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, MODID);

    public AmarthaloeMod(IEventBus modEventBus) {
        FactionAttachments.register(modEventBus);
        BIOME_SOURCES.register("radial_biomes", () -> AmarthaloeBiomeSource.CODEC);
        BIOME_SOURCES.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.register(this);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                SyncFactionDataPayload.TYPE,
                SyncFactionDataPayload.STREAM_CODEE,
                SyncFactionDataPayload::handle
        );

        registrar.playToServer(
                UpdateProfilePayload.TYPE,
                UpdateProfilePayload.STREAM_CODEC,
                UpdateProfilePayload::handle
        );

        registrar.playToClient(
                SyncProfilePayload.TYPE,
                SyncProfilePayload.STREAM_CODEC,
                SyncProfilePayload::handle
        );

        registrar.playToClient(
                SyncPartyPayload.TYPE,
                SyncPartyPayload.STREAM_CODEC,
                SyncPartyPayload::handle
        );

        registrar.playToServer(
                CompleteTutorialPayload.TYPE,
                CompleteTutorialPayload.STREAM_CODEC,
                CompleteTutorialPayload::handle
        );

        registrar.playToServer(
                TeleportToSectorPayload.TYPE,
                TeleportToSectorPayload.STREAM_CODEC,
                TeleportToSectorPayload::handle
        );
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        FactionCommands.register(event.getDispatcher());
        PartyCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer victim) {
            if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                if (PartyManager.areInSameParty(victim.getUUID(), attacker.getUUID())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % 20 == 0) {
            var party = PartyManager.getPartyMembers(player.getUUID());
            if (!party.isEmpty()) {
                PartyManager.syncParty(player.server, party.get(0));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PartyManager.leaveParty(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayer(player);
            syncPlayerProfile(player);
        }
    }

    public static void syncPlayer(ServerPlayer player) {
        PlayerFactionData data = player.getData(FactionAttachments.FACTION_DATA);
        Map<Faction, Integer> map = new EnumMap<>(Faction.class);
        for (Faction f : Faction.values()) {
            map.put(f, data.getReputation(f));
        }
        PacketDistributor.sendToPlayer(player, new SyncFactionDataPayload(map));
    }

    public static void syncPlayerProfile(ServerPlayer player) {
        PlayerProfileData profile = player.getData(FactionAttachments.PROFILE_DATA);
        boolean isFirst = !profile.hasSeenTutorial();

        PacketDistributor.sendToPlayer(player, new SyncProfilePayload(
                profile.getRpName(),
                profile.getGender().name(),
                profile.getSelectedTitle(),
                profile.getHeightWeight(),
                profile.getBuild(),
                profile.getHair(),
                profile.getEyes(),
                profile.getDistinctiveFeatures(),
                profile.getBackstory(),
                isFirst
        ));
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAbove(
                    VanillaGuiLayers.HOTBAR,
                    ResourceLocation.fromNamespaceAndPath(MODID, "faction_overlay"),
                    new FactionGuiOverlay()
            );
            event.registerAbove(
                    VanillaGuiLayers.CHAT,
                    ResourceLocation.fromNamespaceAndPath(MODID, "rank_banner_overlay"),
                    new RankBannerOverlay()
            );
            event.registerAbove(
                    VanillaGuiLayers.HOTBAR,
                    ResourceLocation.fromNamespaceAndPath(MODID, "party_hud_overlay"),
                    new PartyHudOverlay()
            );
            event.registerAbove(
                    VanillaGuiLayers.CHAT,
                    ResourceLocation.fromNamespaceAndPath(MODID, "welcome_toast_overlay"),
                    new WelcomeToastOverlay()
            );
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            FACTION_PREV_KEY = new KeyMapping(
                    "key.amarthaloe.prev_faction",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT,
                    "key.categories.amarthaloe"
            );
            FACTION_NEXT_KEY = new KeyMapping(
                    "key.amarthaloe.next_faction",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT,
                    "key.categories.amarthaloe"
            );
            MENU_KEY = new KeyMapping(
                    "key.amarthaloe.open_menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_M,
                    "key.categories.amarthaloe"
            );
            event.register(FACTION_PREV_KEY);
            event.register(FACTION_NEXT_KEY);
            event.register(MENU_KEY);
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientGameEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            RankBannerOverlay.tick();
            WelcomeToastOverlay.tick();

            if (FACTION_PREV_KEY != null && FACTION_PREV_KEY.consumeClick()) {
                ClientFactionData.cycleLeft();
            }
            if (FACTION_NEXT_KEY != null && FACTION_NEXT_KEY.consumeClick()) {
                ClientFactionData.cycleRight();
            }
            if (MENU_KEY != null && MENU_KEY.consumeClick()) {
                Minecraft.getInstance().setScreen(new AmarthaloeMenuScreen());
            }
        }
    }
}
