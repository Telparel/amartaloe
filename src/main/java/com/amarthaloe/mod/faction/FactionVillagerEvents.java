package com.amarthaloe.mod.faction;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.world.AmarthaloeWorld;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

@EventBusSubscriber(modid = AmarthaloeMod.MODID)
public class FactionVillagerEvents {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Villager villager) {
            if (villager.level().dimension().equals(AmarthaloeWorld.AMARTHALOE_LEVEL)) {
                setupFactionTrader(villager);
            }
        }
    }

    private static void setupFactionTrader(Villager villager) {
        double x = villager.getX();
        double z = villager.getZ();
        double distSq = x * x + z * z;

        Faction faction;
        if (Math.sqrt(distSq) <= 2500.0) {
            faction = Faction.ISONOMIA;
        } else {
            double angle = Math.toDegrees(Math.atan2(z, x));
            if (angle < 0) angle += 360.0;
            int sector = ((int) ((angle + 30.0) % 360.0)) / 60;
            faction = switch (sector) {
                case 0 -> Faction.GLADIUS;
                case 1 -> Faction.MARIS;
                case 2 -> Faction.FULGORT;
                case 3 -> Faction.ADEL;
                case 4 -> Faction.GNOSTERA;
                default -> Faction.CALIGO;
            };
        }

        villager.setCustomName(Component.literal("§6Интендант: ").append(faction.getFullLoreName()));
        villager.setCustomNameVisible(true);

        // Настраиваем уникальные предложения торговли
        MerchantOffers offers = villager.getOffers();
        offers.clear();

        // 1. Базовые сделки снабжения
        offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                new ItemStack(Items.BREAD, 6),
                16, 2, 0.05F
        ));
        offers.add(new MerchantOffer(
                new ItemCost(Items.IRON_INGOT, 4),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));

        // 2. Специфические товары каждого Барона
        switch (faction) {
            case ISONOMIA -> {
                offers.add(new MerchantOffer(new ItemCost(Items.COPPER_INGOT, 6), new ItemStack(Items.EMERALD, 2), 12, 4, 0.05F));
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(Items.COMPASS), 4, 5, 0.05F));
            }
            case GLADIUS -> {
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(Items.IRON_SWORD), 8, 5, 0.05F));
                offers.add(new MerchantOffer(new ItemCost(Items.BASALT, 32), new ItemStack(Items.EMERALD, 1), 16, 2, 0.05F));
            }
            case CALIGO -> {
                offers.add(new MerchantOffer(new ItemCost(Items.BLUE_ICE, 8), new ItemStack(Items.EMERALD, 2), 16, 3, 0.05F));
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.LEATHER_BOOTS), 6, 4, 0.05F));
            }
            case GNOSTERA -> {
                offers.add(new MerchantOffer(new ItemCost(Items.MUSHROOM_STEW, 2), new ItemStack(Items.EMERALD, 1), 12, 3, 0.05F));
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(Items.SLIME_BALL, 4), 10, 4, 0.05F));
            }
            case ADEL -> {
                offers.add(new MerchantOffer(new ItemCost(Items.MANGROVE_LOG, 16), new ItemStack(Items.EMERALD, 2), 16, 3, 0.05F));
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(Items.SHEARS), 6, 3, 0.05F));
            }
            case MARIS -> {
                offers.add(new MerchantOffer(new ItemCost(Items.PRISMARINE_SHARD, 4), new ItemStack(Items.EMERALD, 2), 12, 4, 0.05F));
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(Items.COOKED_COD, 8), 16, 2, 0.05F));
            }
            case FULGORT -> {
                offers.add(new MerchantOffer(new ItemCost(Items.GOLD_INGOT, 3), new ItemStack(Items.EMERALD, 2), 12, 5, 0.05F));
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 7), new ItemStack(Items.CROSSBOW), 6, 6, 0.05F));
            }
        }
    }
}
