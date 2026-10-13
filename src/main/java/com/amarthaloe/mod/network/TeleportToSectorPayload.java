package com.amarthaloe.mod.network;

import com.amarthaloe.mod.AmarthaloeMod;
import com.amarthaloe.mod.world.AmarthaloeWorld;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TeleportToSectorPayload(int targetX, int targetZ) implements CustomPacketPayload {
    public static final Type<TeleportToSectorPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "teleport_sector"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, TeleportToSectorPayload> STREAM_CODEC =
            net.minecraft.network.codec.StreamCodec.of(
                    (buf, p) -> {
                        buf.writeInt(p.targetX);
                        buf.writeInt(p.targetZ);
                    },
                    buf -> new TeleportToSectorPayload(buf.readInt(), buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TeleportToSectorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ServerLevel targetLevel = player.server.getLevel(AmarthaloeWorld.AMARTHALOE_LEVEL);
                if (targetLevel != null) {
                    int x = payload.targetX();
                    int z = payload.targetZ();
                    int y = targetLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                    if (y <= targetLevel.getMinBuildHeight()) y = 75;

                    player.teleportTo(targetLevel, x + 0.5, y + 1.0, z + 0.5, player.getYRot(), player.getXRot());
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aТелепортация в сектор §6[" + x + ", " + z + "]"));
                }
            }
        });
    }
}
