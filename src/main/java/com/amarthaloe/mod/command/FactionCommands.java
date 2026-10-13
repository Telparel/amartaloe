package com.amarthaloe.mod.command;

import com.amarthaloe.mod.faction.Faction;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.faction.PlayerFactionData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class FactionCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("amarthaloe")
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
                                    ctx.getSource().sendFailure(Component.literal("Фракция не найдена. Доступны: isonomia, caligo, gnostera, gladius, maris, fulgort, adel"));
                                    return 0;
                                }

                                PlayerFactionData data = player.getData(FactionAttachments.FACTION_DATA);
                                data.addReputation(faction, amount);
                                ctx.getSource().sendSuccess(() -> Component.literal("Изменена репутация " + player.getName().getString() + " во фракции ").append(faction.getFormattedName()).append(" на " + amount), true);
                                return 1;
                            })
                        )
                    )
                )
            )
        );
    }
}
