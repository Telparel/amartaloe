package com.amarthaloe.mod;

import com.amarthaloe.mod.client.ClientProfileData;
import com.amarthaloe.mod.client.FactionGuiOverlay;
import com.amarthaloe.mod.client.ModClientConfig;
import com.amarthaloe.mod.client.PartyHudOverlay;
import com.amarthaloe.mod.client.RankBannerOverlay;
import com.amarthaloe.mod.client.SoulsWarcraftHudOverlay;
import com.amarthaloe.mod.client.WelcomeToastOverlay;
import com.amarthaloe.mod.client.gui.AmarthaloeMenuScreen;
import com.amarthaloe.mod.client.gui.TutorialScreen;
import com.amarthaloe.mod.command.FactionCommands;
import com.amarthaloe.mod.command.PartyCommands;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.faction.FactionVillagerEvents;
import com.amarthaloe.mod.network.CompleteTutorialPayload;
import com.amarthaloe.mod.network.SyncFactionDataPayload;
import com.amarthaloe.mod.network.SyncPartyPayload;
import com.amarthaloe.mod.network.SyncProfilePayload;
import com.amarthaloe.mod.network.TeleportToSectorPayload;
import com.amarthaloe.mod.network.UpdateProfilePayload;
import com.amarthaloe.mod.party.PartyManager;
import com.amarthaloe.mod.party.PartyMemberData;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.lwjgl.glfw.GLFW;

@Mod(AmarthaloeMod.MODID)
public class AmarthaloeMod {

    public static final String MODID = "amarthaloe";

    public AmarthaloeMod(IEventBus modEventBus, ModContainer modContainer) {
        if (ModClientConfig.CONFIG_SPEC != null) {
            modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfig.CONFIG_SPEC);
        }

        FactionAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(FactionVillagerEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0");

        registrar.playToClient(
                SyncFactionDataPayload.TYPE,
                SyncFactionDataPayload.CODEC,
                SyncFactionDataPayload::handle
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
                UpdateProfilePayload.TYPE,
                UpdateProfilePayload.STREAM_CODEC,
                UpdateProfilePayload::handle
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

    public static void syncPlayer(ServerPlayer player) {
        var factionData = player.getData(FactionAttachments.PLAYER_FACTION_DATA);
        PacketDistributor.sendToPlayer(player, new SyncFactionDataPayload(factionData));
    }

    public static void syncPlayerProfile(ServerPlayer player) {
        var profileData = player.getData(FactionAttachments.PLAYER_PROFILE_DATA);
        PacketDistributor.sendToPlayer(player, new SyncProfilePayload(profileData));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        FactionCommands.register(event.getDispatcher());
        PartyCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayer(player);
            syncPlayerProfile(player);

            var party = PartyManager.getInstance().getPartyForPlayer(player.getUUID());
            if (party != null) {
                PacketDistributor.sendToPlayer(player, new SyncPartyPayload(party));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayer(player);
            syncPlayerProfile(player);
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        PartyManager.getInstance().tick();
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        public static final KeyMapping OPEN_MENU_KEY = new KeyMapping(
                "key.amarthaloe.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.categories.amarthaloe"
        );

        public static final ResourceLocation SOULS_HUD_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "souls_warcraft_hud");
        public static final ResourceLocation FACTION_HUD_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "faction_hud");
        public static final ResourceLocation PARTY_HUD_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "party_hud");
        public static final ResourceLocation RANK_BANNER_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "rank_banner");
        public static final ResourceLocation TOAST_OVERLAY_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "welcome_toast");

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_MENU_KEY);
        }

        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, SOULS_HUD_LAYER, new SoulsWarcraftHudOverlay());
            event.registerAbove(SOULS_HUD_LAYER, FACTION_HUD_LAYER, new FactionGuiOverlay());
            event.registerAbove(FACTION_HUD_LAYER, PARTY_HUD_LAYER, new PartyHudOverlay());
            event.registerAbove(PARTY_HUD_LAYER, RANK_BANNER_LAYER, new RankBannerOverlay());
            event.registerAbove(RANK_BANNER_LAYER, TOAST_OVERLAY_LAYER, new WelcomeToastOverlay());
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientGameEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) {
                return;
            }

            if (ClientModEvents.OPEN_MENU_KEY.consumeClick()) {
                if (!ClientProfileData.tutorialCompleted) {
                    mc.setScreen(new TutorialScreen());
                } else {
                    mc.setScreen(new AmarthaloeMenuScreen());
                }
            }
        }

        @SubscribeEvent
        public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
            if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)
                    || event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)
                    || event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
                event.setCanceled(true);
            }
        }
    }
}
