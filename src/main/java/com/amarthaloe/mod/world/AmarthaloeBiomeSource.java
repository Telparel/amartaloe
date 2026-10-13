package com.amarthaloe.mod.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class AmarthaloeBiomeSource extends BiomeSource {
    public static final MapCodec<AmarthaloeBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.CODEC.fieldOf("isonomia").forGetter(s -> s.isonomia),
                    Biome.CODEC.fieldOf("caligo").forGetter(s -> s.caligo),
                    Biome.CODEC.fieldOf("gnostera").forGetter(s -> s.gnostera),
                    Biome.CODEC.fieldOf("gladius").forGetter(s -> s.gladius),
                    Biome.CODEC.fieldOf("maris").forGetter(s -> s.maris),
                    Biome.CODEC.fieldOf("fulgort").forGetter(s -> s.fulgort),
                    Biome.CODEC.fieldOf("adel").forGetter(s -> s.adel)
            ).apply(instance, AmarthaloeBiomeSource::new)
    );

    private final Holder<Biome> isonomia;
    private final Holder<Biome> caligo;
    private final Holder<Biome> gnostera;
    private final Holder<Biome> gladius;
    private final Holder<Biome> maris;
    private final Holder<Biome> fulgort;
    private final Holder<Biome> adel;

    public AmarthaloeBiomeSource(
            Holder<Biome> isonomia,
            Holder<Biome> caligo,
            Holder<Biome> gnostera,
            Holder<Biome> gladius,
            Holder<Biome> maris,
            Holder<Biome> fulgort,
            Holder<Biome> adel
    ) {
        this.isonomia = isonomia;
        this.caligo = caligo;
        this.gnostera = gnostera;
        this.gladius = gladius;
        this.maris = maris;
        this.fulgort = fulgort;
        this.adel = adel;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(isonomia, caligo, gnostera, gladius, maris, fulgort, adel);
    }

   @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        int blockX = quartX << 2;
        int blockZ = quartZ << 2;

        // Мягкое искривление границ без разрушения пропорций
        double warpX = Math.sin(blockZ * 0.002) * 120.0;
        double warpZ = Math.cos(blockX * 0.002) * 120.0;

        double sampleX = blockX + warpX;
        double sampleZ = blockZ + warpZ;

        double distSq = sampleX * sampleX + sampleZ * sampleZ;
        double radius = Math.sqrt(distSq);

        // 1. Центр: Исономия (радиус 2500 блоков)
        if (radius <= 2500.0) {
            return isonomia;
        }

        // 2. Внешние регионы: 6 равных секторов по 60°
        // Направление: Калиго строго на север (угол ~ -90° / 270°)
        double angle = Math.toDegrees(Math.atan2(sampleZ, sampleX));
        if (angle < 0) angle += 360.0;

        // Смещение на +30 градусов для центровки первого сектора
        int sector = ((int) ((angle + 30.0) % 360.0)) / 60;

        return switch (sector) {
            case 0 -> gladius;   // Восток (+X)
            case 1 -> maris;     // Юго-восток
            case 2 -> fulgort;   // Юго-запад
            case 3 -> adel;      // Запад (-X) — гарантированно существует!
            case 4 -> gnostera;  // Северо-запад
            default -> caligo;   // Север (-Z)
        };
    }
