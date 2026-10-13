package com.amarthaloe.mod.network;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.profile.PlayerProfileData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CompleteTutorialPayload() implements CustomPacketPayload {
    public static final Type<CompleteTutorialPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "complete_tutorial"));

    public static final StreamCodec<ByteBuf, CompleteTutorialPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {},
            buf -> new CompleteTutorialPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CompleteTutorialPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerProfileData profile = player.getData(FactionAttachments.PROFILE_DATA);
                profile.setHasSeenTutorial(true);
            }
        });
    }
}
