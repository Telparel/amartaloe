package com.amarthaloe.mod.network;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.faction.FactionAttachments;
import com.amarthaloe.mod.profile.PlayerProfileData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateProfilePayload(
        String rpName,
        String gender,
        String selectedTitle,
        String heightWeight,
        String build,
        String hair,
        String eyes,
        String distinctiveFeatures,
        String backstory
) implements CustomPacketPayload {

    public static final Type<UpdateProfilePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "update_profile"));

    public static final StreamCodec<ByteBuf, UpdateProfilePayload> STREAM_CODEC = StreamCodec.of(
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
            },
            buf -> new UpdateProfilePayload(
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateProfilePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerProfileData data = player.getData(FactionAttachments.PROFILE_DATA);
                data.setRpName(payload.rpName);
                try {
                    data.setGender(PlayerProfileData.Gender.valueOf(payload.gender));
                } catch (Exception ignored) {}
                data.setSelectedTitle(payload.selectedTitle);
                data.setHeightWeight(payload.heightWeight);
                data.setBuild(payload.build);
                data.setHair(payload.hair);
                data.setEyes(payload.eyes);
                data.setDistinctiveFeatures(payload.distinctiveFeatures);
                data.setBackstory(payload.backstory);

                // Оповещаем о сохранении и синхронизируем
                AmarthaloeMod.syncPlayerProfile(player);
            }
        });
    }
}
