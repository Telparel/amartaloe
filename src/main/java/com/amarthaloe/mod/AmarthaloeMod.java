package com.amarthaloe.mod;

import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.client.ClientPartyData;
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
import com.amarthaloe.mod.profile.PlayerProfileData;
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
        // Регистрация конфигурации клиента
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);

        // Регистрация кастомных вложений данных сущностей (Attachments)
        FactionAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // События жизненного цикла
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        // Регистрация в глобальной шине игры (GAME BUS)
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(FactionVillagerEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Общая инициализация мода
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0");

        // Пакеты: Сервер -> Клиент
        registrar.playToClient(
                SyncFactionDataPayload.TYPE,
                SyncFactionDataPayload.STREAM_CODEC,
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

        // Пакеты: Клиент -> Сервер
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

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // Регистрация консольных команд
        FactionCommands.register(event.getDispatcher());
        PartyCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Синхронизация фракции
            var factionData = player.getData(FactionAttachments.PLAYER_FACTION);
            PacketDistributor.sendToPlayer(player, new SyncFactionDataPayload(factionData));

            // Синхронизация профиля персонажа
            var profileData = player.getData(FactionAttachments.PLAYER_PROFILE);
            PacketDistributor.sendToPlayer(player, new SyncProfilePayload(profileData));

            // Синхронизация группы/пати
            var party = PartyManager.get().getParty(player.getUUID());
            if (party != null) {
                PacketDistributor.sendToPlayer(player, new SyncPartyPayload(party));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var factionData = player.getData(FactionAttachments.PLAYER_FACTION);
            PacketDistributor.sendToPlayer(player, new SyncFactionDataPayload(factionData));

            var profileData = player.getData(FactionAttachments.PLAYER_PROFILE);
            PacketDistributor.sendToPlayer(player, new SyncProfilePayload(profileData));
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        // Обновление таймеров пати и приглашений
        PartyManager.get().tick();
    }

    /**
     * ==========================================
     * КЛИЕНТСКИЕ СОБЫТИЯ ШИНЫ МОДА (MOD BUS)
     * ==========================================
     */
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        // Клавиша открытия главного меню мода (M по умолчанию)
        public static final KeyMapping OPEN_MENU_KEY = new KeyMapping(
                "key.amarthaloe.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.categories.amarthaloe"
        );

        // Идентификаторы слоёв HUD
        public static final ResourceLocation SOULS_HUD_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "souls_warcraft_hud");
        public static final ResourceLocation FACTION_HUD_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "faction_hud");
        public static final ResourceLocation PARTY_HUD_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "party_hud");
        public static final ResourceLocation RANK_BANNER_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "rank_banner");
        public static final ResourceLocation TOAST_OVERLAY_LAYER = ResourceLocation.fromNamespaceAndPath(MODID, "welcome_toast");

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Дополнительная настройка клиента
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_MENU_KEY);
        }

        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            // 1. Наш основной Souls/WoW-фрейм персонажа
            event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, SOULS_HUD_LAYER, new SoulsWarcraftHudOverlay());

            // 2. Фрейм информации о фракции
            event.registerAbove(SOULS_HUD_LAYER, FACTION_HUD_LAYER, new FactionGuiOverlay());

            // 3. Фрейм союзников и группы (Party HUD)
            event.registerAbove(FACTION_HUD_LAYER, PARTY_HUD_LAYER, new PartyHudOverlay());

            // 4. Всплывающий баннер изменения ранга фракции
            event.registerAbove(PARTY_HUD_LAYER, RANK_BANNER_LAYER, new RankBannerOverlay());

            // 5. Всплывающее приветственное уведомление
            event.registerAbove(RANK_BANNER_LAYER, TOAST_OVERLAY_LAYER, new WelcomeToastOverlay());
        }
    }

    /**
     * ==========================================
     * КЛИЕНТСКИЕ СОБЫТИЯ ШИНЫ ИГРЫ (GAME BUS)
     * ==========================================
     */
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientGameEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) {
                return;
            }

            // Обработка открытия окна меню персонажа/мода
            if (ClientModEvents.OPEN_MENU_KEY.consumeClick()) {
                // Если туториал еще не завершен — открываем экран создания персонажа/обучения
                if (!ClientProfileData.isTutorialCompleted()) {
                    mc.setScreen(new TutorialScreen());
                } else {
                    mc.setScreen(new AmarthaloeMenuScreen());
                }
            }
        }

        @SubscribeEvent
        public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
            // Отключаем ванильные элементы по центру, дублируемые новым Unit Frame
            if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)
                    || event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)
                    || event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
                event.setCanceled(true);
            }
        }
    }
}
