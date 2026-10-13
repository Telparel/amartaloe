package com.amarthaloe.mod.network;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.client.ClientPartyData;
import com.amarthaloe.mod.party.PartyMemberData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SyncPartyPayload(List<PartyMemberData> members) implements CustomPacketPayload {
    public static final Type<SyncPartyPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "sync_party"));

    public static final StreamCodec<ByteBuf, SyncPartyPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeInt(p.members.size());
                for (PartyMemberData m : p.members) {
                    UUIDUtil.STREAM_CODEC.encode(buf, m.getUuid());
                    ByteBufCodecs.STRING_UTF8.encode(buf, m.getName());
                    ByteBufCodecs.STRING_UTF8.encode(buf, m.getTitle());
                    buf.writeFloat(m.getHealth());
                    buf.writeFloat(m.getMaxHealth());
                    buf.writeInt(m.getFoodLevel());
                }
            },
            buf -> {
                int count = buf.readInt();
                List<PartyMemberData> list = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    UUID uuid = UUIDUtil.STREAM_CODEC.decode(buf);
                    String name = ByteBufCodecs.STRING_UTF8.decode(buf);
                    String title = ByteBufCodecs.STRING_UTF8.decode(buf);
                    float hp = buf.readFloat();
                    float maxHp = buf.readFloat();
                    int food = buf.readInt();
                    list.add(new PartyMemberData(uuid, name, title, hp, maxHp, food));
                }
                return new SyncPartyPayload(list);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncPartyPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPartyData.update(payload.members());
        });
    }
}
