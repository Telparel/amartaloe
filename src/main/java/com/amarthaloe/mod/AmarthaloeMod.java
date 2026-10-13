package com.amarthaloe.mod;

import com.amarthaloe.mod.command.FactionCommands;
import com.amarthaloe.mod.faction.FactionAttachments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(AmarthaloeMod.MODID)
public class AmarthaloeMod {
    public static final String MODID = "amarthaloe";

    public AmarthaloeMod(IEventBus modEventBus) {
        FactionAttachments.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        FactionCommands.register(event.getDispatcher());
    }
}
