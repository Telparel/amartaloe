package com.amarthaloe.mod.command;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.faction.FactionRank;
import com.amarthaloe.mod.faction.PlayerFactionData;
import com.amarthaloe.mod.profile.PlayerProfileData;
import com.amarthaloe.mod.world.AmarthaloeWorld;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FactionCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("amarthaloe")
            .then(Commands.literal("tp")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    ServerLevel targetLevel = player.server.getLevel(AmarthaloeWorld.AMARTHALOE_LEVEL);
                    if (targetLevel == null) {
                        ctx.getSource().sendFailure(Component.literal("§cМир Амарталоэ не найден!"));
                        return 0;
                    }
                    player.teleportTo(targetLevel, 0.5, 70.0, 0.5, player.getYRot(), player.getXRot());
                    ctx.getSource().sendSuccess(() -> Component.literal("§aВы перенеслись в сердце Амарталоэ — Исономию."), true);
                    return 1;
                })
            )
            .then(Commands.literal("check")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PlayerFactionData data = player.getData(FactionAttachments.FACTION_DATA);

                    player.sendSystemMessage(Component.literal("§6--- Репутация в Амарталоэ ---"));
                    for (Faction f : Faction.values()) {
                        int rep = data.getReputation(f);
                        String repFormatted = (rep >= 0 ? "§a+" : "§c") + rep;
                        player.sendSystemMessage(Component.literal("§7- ").append(f.getFormattedName()).append("§7: " + repFormatted));
                    }
                    return 1;
                })
            )
            .then(Commands.literal("passport")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    showPassport(player, player);
                    return 1;
                })
                .then(Commands.argument("target", EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayer viewer = ctx.getSource().getPlayerOrException();
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                        showPassport(viewer, target);
                        return 1;
                    })
                )
            )
            .then(Commands.literal("add")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.player())
                    .then(Commands.argument("faction", StringArgumentType.word())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(ctx -> {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "target");
                                String factionId = StringArgumentType.getString(ctx, "faction");
                                int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                Faction faction = Faction.fromId(factionId);
                                if (faction == null) {
                                    ctx.getSource().sendFailure(Component.literal("Фракция не найдена."));
                                    return 0;
                                }

                                PlayerFactionData data = player.getData(FactionAttachments.FACTION_DATA);
                                data.addReputation(faction, amount);
                                AmarthaloeMod.syncPlayer(player);

                                ctx.getSource().sendSuccess(() -> Component.literal("Репутация обновлена."), true);
                                return 1;
                            })
                        )
                    )
                )
            )
        );
    }

    private static void showPassport(ServerPlayer viewer, ServerPlayer target) {
        PlayerProfileData profile = target.getData(FactionAttachments.PROFILE_DATA);
        PlayerFactionData factionData = target.getData(FactionAttachments.FACTION_DATA);

        String displayName = profile.getRpName().isBlank() ? target.getName().getString() : profile.getRpName();

        viewer.sendSystemMessage(Component.literal("§6======= [ПАСПОРТ АМАРТАЛОЭ] ======="));
        viewer.sendSystemMessage(Component.literal("§fИмя / Позывной: §e" + displayName));
        viewer.sendSystemMessage(Component.literal("§fПол: §b" + profile.getGender().getDisplay()));
        if (!profile.getSelectedTitle().isBlank()) {
            viewer.sendSystemMessage(Component.literal("§fАктивный Титул: §6" + profile.getSelectedTitle()));
        }
        viewer.sendSystemMessage(Component.literal("§fВнешность: §7" + profile.getHeightWeight() + ", " + profile.getBuild() + " телосложение"));
        viewer.sendSystemMessage(Component.literal("§fЧерты: §7волосы " + profile.getHair() + ", глаза " + profile.getEyes()));
        viewer.sendSystemMessage(Component.literal("§fОсобые приметы: §c" + profile.getDistinctiveFeatures()));
        viewer.sendSystemMessage(Component.literal("§fПредыстория: §o" + profile.getBackstory()));
        viewer.sendSystemMessage(Component.literal("§6--- Репутация в ключевых землях ---"));
        for (Faction f : Faction.values()) {
            int rep = factionData.getReputation(f);
            Component rank = FactionRank.getTitle(f, rep, profile.getGender());
            viewer.sendSystemMessage(Component.literal("§8- ").append(f.getFormattedName()).append("§7: ").append(rank));
        }
        viewer.sendSystemMessage(Component.literal("§6=================================="));
    }
}
