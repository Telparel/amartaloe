package com.amarthaloe.mod.network;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.client.ClientFactionData;
import com.amarthaloe.mod.faction.Faction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.EnumMap;
import java.util.Map;

public record SyncFactionDataPayload(Map<Faction, Integer> data) implements CustomPacketPayload {
    public static final Type<SyncFactionDataPayload> TYPE = 
            new Type<>(ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "sync_faction_data"));

    public static final StreamCodec<ByteBuf, SyncFactionDataPayload> STREAM_CODEE = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.data.size());
                for (Map.Entry<Faction, Integer> entry : payload.data.entrySet()) {
                    ByteBufCodecs.STRING_UTF8.encode(buf, entry.getKey().getId());
                    buf.writeInt(entry.getValue());
                }
            },
            buf -> {
                int size = buf.readInt();
                Map<Faction, Integer> map = new EnumMap<>(Faction.class);
                for (int i = 0; i < size; i++) {
                    String id = ByteBufCodecs.STRING_UTF8.decode(buf);
                    int val = buf.readInt();
                    Faction f = Faction.fromId(id);
                    if (f != null) map.put(f, val);
                }
                return new SyncFactionDataPayload(map);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncFactionDataPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientFactionData.update(payload.data());
        });
    }
}
