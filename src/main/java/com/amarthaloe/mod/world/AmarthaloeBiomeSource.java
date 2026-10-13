package com.amarthaloe.mod.world;

import com.amarthaloe.mod.AmarthaloeMod;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
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
        // Преобразуем координаты четвертей биома в реальные координаты блоков (* 4)
        int blockX = quartX << 2;
        int blockZ = quartZ << 2;

        double distSq = (double) blockX * blockX + (double) blockZ * blockZ;
        double radius = Math.sqrt(distSq);

        // 1. Центр: Исономия (радиус до 700 блоков)
        if (radius <= 700.0) {
            return isonomia;
        }

        // 2. Внешнее кольцо: 6 секторов по 60 градусов (от -PI до +PI)
        double angle = Math.atan2(blockZ, blockX); // от -π до π
        if (angle < 0) angle += 2 * Math.PI;       // нормализация от 0 до 2π

        // Секторы по 60 градусов (π / 3)
        int sector = (int) (angle / (Math.PI / 3.0)) % 6;

        return switch (sector) {
            case 0 -> gladius;   // Восток / Степи
            case 1 -> maris;     // Юго-восток / Океан
            case 2 -> fulgort;   // Юго-запад / Горы
            case 3 -> adel;      // Запад / Мангры
            case 4 -> gnostera;  // Северо-запад / Топи
            default -> caligo;   // Север / Ледяные пустоши
        };
    }
}
