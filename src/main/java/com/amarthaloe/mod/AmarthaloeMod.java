package com.amarthaloe.mod;

import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.client.FactionGuiOverlay;
import com.amarthaloe.mod.client.RankBannerOverlay;
import com.amarthaloe.mod.command.FactionCommands;
import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.faction.PlayerFactionData;
import com.amarthaloe.mod.network.SyncFactionDataPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.Map;

@Mod(AmarthaloeMod.MODID)
public class AmarthaloeMod {
    public static final String MODID = "amarthaloe";
    public static KeyMapping FACTION_PREV_KEY;
    public static KeyMapping FACTION_NEXT_KEY;

    public AmarthaloeMod(IEventBus modEventBus) {
        FactionAttachments.register(modEventBus);
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
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        FactionCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayer(player);
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

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            // Верхняя постоянная плашка репутации
            event.registerAbove(
                    VanillaGuiLayers.HOTBAR,
                    ResourceLocation.fromNamespaceAndPath(MODID, "faction_overlay"),
                    new FactionGuiOverlay()
            );
            // Большой WoW-баннер поверх интерфейса
            event.registerAbove(
                    VanillaGuiLayers.CHAT,
                    ResourceLocation.fromNamespaceAndPath(MODID, "rank_banner_overlay"),
                    new RankBannerOverlay()
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
            event.register(FACTION_PREV_KEY);
            event.register(FACTION_NEXT_KEY);
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientGameEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            RankBannerOverlay.tick();

            if (FACTION_PREV_KEY != null && FACTION_PREV_KEY.consumeClick()) {
                ClientFactionData.cycleLeft();
            }
            if (FACTION_NEXT_KEY != null && FACTION_NEXT_KEY.consumeClick()) {
                ClientFactionData.cycleRight();
            }
        }
    }
}
