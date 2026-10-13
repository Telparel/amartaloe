package com.amarthaloe.mod.world;

import com.amarthaloe.mod.AmarthaloeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class AmarthaloeWorld {
    // Ключ самого измерения: amarthaloe:amarthaloe
    public static final ResourceKey<Level> AMARTHALOE_LEVEL =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "amarthaloe"));

    public static final ResourceKey<DimensionType> AMARTHALOE_DIM_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, "amarthaloe_type"));

    // 7 Биомов Амарталоэ
    public static final ResourceKey<Biome> BIOME_ISONOMIA = createBiomeKey("isonomia");
    public static final ResourceKey<Biome> BIOME_CALIGO   = createBiomeKey("caligo");
    public static final ResourceKey<Biome> BIOME_GNOSTERA = createBiomeKey("gnostera");
    public static final ResourceKey<Biome> BIOME_GLADIUS  = createBiomeKey("gladius");
    public static final ResourceKey<Biome> BIOME_MARIS    = createBiomeKey("maris");
    public static final ResourceKey<Biome> BIOME_FULGORT  = createBiomeKey("fulgort");
    public static final ResourceKey<Biome> BIOME_ADEL     = createBiomeKey("adel");

    private static ResourceKey<Biome> createBiomeKey(String name) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AmarthaloeMod.MODID, name));
    }
}
