package com.amarthaloe.mod.command;

import com.amarthaloe.mod.party.PartyManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class PartyCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("party")
            .then(Commands.literal("invite")
                .then(Commands.argument("target", EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayer leader = ctx.getSource().getPlayerOrException();
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                        PartyManager.invitePlayer(leader, target);
                        return 1;
                    })
                )
            )
            .then(Commands.literal("accept")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PartyManager.acceptInvite(player);
                    return 1;
                })
            )
            .then(Commands.literal("leave")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PartyManager.leaveParty(player);
                    return 1;
                })
            )
        );
    }
}
