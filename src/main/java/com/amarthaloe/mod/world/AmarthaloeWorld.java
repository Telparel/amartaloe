package com.amarthaloe.mod.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class AmarthaloeWorld {
    public static final ResourceLocation AMARTHALOE_DIM_ID = ResourceLocation.fromNamespaceAndPath("amarthaloe", "amarthaloe");
    public static final ResourceKey<Level> AMARTHALOE_LEVEL = ResourceKey.create(Registries.DIMENSION, AMARTHALOE_DIM_ID);
}
