package com.amarthaloe.mod.network;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.client.ClientProfileData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncProfilePayload(
        String rpName,
        String gender,
        String selectedTitle,
        String heightWeight,
        String build,
        String hair,
        String eyes,
        String distinctiveFeatures,
        String backstory,
        boolean isFirstJoin
) implements CustomPacketPayload {

    public static final Type<SyncProfilePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "sync_profile"));

    public static final StreamCodec<ByteBuf, SyncProfilePayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                ByteBufCodecs.STRING_UTF8.encode(buf, p.rpName);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.gender);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.selectedTitle);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.heightWeight);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.build);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.hair);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.eyes);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.distinctiveFeatures);
                ByteBufCodecs.STRING_UTF8.encode(buf, p.backstory);
                buf.writeBoolean(p.isFirstJoin);
            },
            buf -> new SyncProfilePayload(
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    buf.readBoolean()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncProfilePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientProfileData.update(payload);
        });
    }
}
