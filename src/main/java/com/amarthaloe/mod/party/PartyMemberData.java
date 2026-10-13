package com.amarthaloe.mod.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record PartyMemberData(
        UUID uuid,
        String name,
        float health,
        float maxHealth,
        boolean hasHarmfulEffects,
        String sectorDimension
) {
    public static final StreamCodec<ByteBuf, PartyMemberData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString), PartyMemberData::uuid,
            ByteBufCodecs.STRING_UTF8, PartyMemberData::name,
            ByteBufCodecs.FLOAT, PartyMemberData::health,
            ByteBufCodecs.FLOAT, PartyMemberData::maxHealth,
            ByteBufCodecs.BOOL, PartyMemberData::hasHarmfulEffects,
            ByteBufCodecs.STRING_UTF8, PartyMemberData::sectorDimension,
            PartyMemberData::new
    );
}
