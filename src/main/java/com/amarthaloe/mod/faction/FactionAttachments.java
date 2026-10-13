package com.amarthaloe.mod.faction;

import com.amarthaloe.mod.AmarthaloeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class FactionAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AmarthaloeMod.MODID);

    public static final Supplier<AttachmentType<PlayerFactionData>> FACTION_DATA =
            ATTACHMENT_TYPES.register("faction_data", () ->
                    AttachmentType.serializable(PlayerFactionData::new)
                            .copyOnDeath()
                            .build()
            );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
