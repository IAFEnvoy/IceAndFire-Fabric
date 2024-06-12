package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.config.BiomeConfig;
import com.github.alexthe666.iceandfire.datagen.IafPlacedFeatures;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.feature.*;
import com.github.alexthe666.iceandfire.world.gen.*;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.function.Supplier;

public class IafWorldRegistry {
    public static final LazyRegistrar<Feature<?>> FEATURES = LazyRegistrar.create(Registries.FEATURE, IceAndFire.MOD_ID);

    public static final RegistryObject<Feature<DefaultFeatureConfig>> FIRE_DRAGON_ROOST = register("fire_dragon_roost", () -> new WorldGenFireDragonRoosts(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> ICE_DRAGON_ROOST = register("ice_dragon_roost", () -> new WorldGenIceDragonRoosts(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> LIGHTNING_DRAGON_ROOST = register("lightning_dragon_roost", () -> new WorldGenLightningDragonRoosts(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> FIRE_DRAGON_CAVE = register("fire_dragon_cave", () -> new WorldGenFireDragonCave(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> ICE_DRAGON_CAVE = register("ice_dragon_cave", () -> new WorldGenIceDragonCave(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> LIGHTNING_DRAGON_CAVE = register("lightning_dragon_cave", () -> new WorldGenLightningDragonCave(DefaultFeatureConfig.CODEC));
    //TODO: Should be a structure
    public static final RegistryObject<Feature<DefaultFeatureConfig>> CYCLOPS_CAVE = register("cyclops_cave", () -> new WorldGenCyclopsCave(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> PIXIE_VILLAGE = register("pixie_village", () -> new WorldGenPixieVillage(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SIREN_ISLAND = register("siren_island", () -> new WorldGenSirenIsland(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> HYDRA_CAVE = register("hydra_cave", () -> new WorldGenHydraCave(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> MYRMEX_HIVE_DESERT = register("myrmex_hive_desert", () -> new WorldGenMyrmexHive(false, false, DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> MYRMEX_HIVE_JUNGLE = register("myrmex_hive_jungle", () -> new WorldGenMyrmexHive(false, true, DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_DEATH_WORM = register("spawn_death_worm", () -> new SpawnDeathWorm(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_DRAGON_SKELETON_L = register("spawn_dragon_skeleton_lightning", () -> new SpawnDragonSkeleton(IafEntityRegistry.LIGHTNING_DRAGON.get(), DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_DRAGON_SKELETON_F = register("spawn_dragon_skeleton_fire", () -> new SpawnDragonSkeleton(IafEntityRegistry.FIRE_DRAGON.get(), DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_DRAGON_SKELETON_I = register("spawn_dragon_skeleton_ice", () -> new SpawnDragonSkeleton(IafEntityRegistry.ICE_DRAGON.get(), DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_HIPPOCAMPUS = register("spawn_hippocampus", () -> new SpawnHippocampus(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_SEA_SERPENT = register("spawn_sea_serpent", () -> new SpawnSeaSerpent(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_STYMPHALIAN_BIRD = register("spawn_stymphalian_bird", () -> new SpawnStymphalianBird(DefaultFeatureConfig.CODEC));
    public static final RegistryObject<Feature<DefaultFeatureConfig>> SPAWN_WANDERING_CYCLOPS = register("spawn_wandering_cyclops", () -> new SpawnWanderingCyclops(DefaultFeatureConfig.CODEC));

    private static <C extends FeatureConfig, F extends Feature<C>> RegistryObject<F> register(final String name, final Supplier<? extends F> supplier) {
        return FEATURES.register(name, supplier);
    }

    public static boolean isFarEnoughFromSpawn(final WorldAccess level, final BlockPos position) {
        WorldProperties spawnPoint = level.getLevelProperties();
        BlockPos spawnRelative = new BlockPos(spawnPoint.getSpawnX(), position.getY(), spawnPoint.getSpawnY());
        return !spawnRelative.isWithinDistance(position, IafConfig.dangerousWorldGenDistanceLimit);
    }

    public static boolean isFarEnoughFromDangerousGen(final ServerWorldAccess level, final BlockPos position, final String id) {
        return isFarEnoughFromDangerousGen(level, position, id, IafWorldData.FeatureType.SURFACE);
    }

    public static boolean isFarEnoughFromDangerousGen(final ServerWorldAccess level, final BlockPos position, final String id, final IafWorldData.FeatureType type) {
        IafWorldData data = IafWorldData.get(level.toServerWorld());
        return data.check(type, position, id);
    }

    public static void addFeatures() {
        addFeatureToBiome(BiomeConfig.fireLilyBiomes.getRight(), IafPlacedFeatures.PLACED_FIRE_LILY, GenerationStep.Feature.VEGETAL_DECORATION);
        addFeatureToBiome(BiomeConfig.lightningLilyBiomes.getRight(), IafPlacedFeatures.PLACED_LIGHTNING_LILY, GenerationStep.Feature.VEGETAL_DECORATION);
        addFeatureToBiome(BiomeConfig.frostLilyBiomes.getRight(), IafPlacedFeatures.PLACED_FROST_LILY, GenerationStep.Feature.VEGETAL_DECORATION);
        addFeatureToBiome(BiomeConfig.oreGenBiomes.getRight(), IafPlacedFeatures.PLACED_SILVER_ORE, GenerationStep.Feature.UNDERGROUND_ORES);
        addFeatureToBiome(BiomeConfig.sapphireBiomes.getRight(), IafPlacedFeatures.PLACED_SAPPHIRE_ORE, GenerationStep.Feature.UNDERGROUND_ORES);
        addFeatureToBiome(BiomeConfig.fireDragonBiomes.getRight(), IafPlacedFeatures.PLACED_FIRE_DRAGON_ROOST);
        addFeatureToBiome(BiomeConfig.lightningDragonBiomes.getRight(), IafPlacedFeatures.PLACED_LIGHTNING_DRAGON_ROOST);
        addFeatureToBiome(BiomeConfig.iceDragonBiomes.getRight(), IafPlacedFeatures.PLACED_ICE_DRAGON_ROOST);
        addFeatureToBiome(BiomeConfig.fireDragonCaveBiomes.getRight(), IafPlacedFeatures.PLACED_FIRE_DRAGON_CAVE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        addFeatureToBiome(BiomeConfig.lightningDragonCaveBiomes.getRight(), IafPlacedFeatures.PLACED_LIGHTNING_DRAGON_CAVE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        addFeatureToBiome(BiomeConfig.iceDragonCaveBiomes.getRight(), IafPlacedFeatures.PLACED_ICE_DRAGON_CAVE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        addFeatureToBiome(BiomeConfig.cyclopsCaveBiomes.getRight(), IafPlacedFeatures.PLACED_CYCLOPS_CAVE);
        addFeatureToBiome(BiomeConfig.pixieBiomes.getRight(), IafPlacedFeatures.PLACED_PIXIE_VILLAGE);
        addFeatureToBiome(BiomeConfig.hydraBiomes.getRight(), IafPlacedFeatures.PLACED_HYDRA_CAVE);
        addFeatureToBiome(BiomeConfig.desertMyrmexBiomes.getRight(), IafPlacedFeatures.PLACED_MYRMEX_HIVE_DESERT);
        addFeatureToBiome(BiomeConfig.jungleMyrmexBiomes.getRight(), IafPlacedFeatures.PLACED_MYRMEX_HIVE_JUNGLE);
        addFeatureToBiome(BiomeConfig.sirenBiomes.getRight(), IafPlacedFeatures.PLACED_SIREN_ISLAND);
        addFeatureToBiome(BiomeConfig.deathwormBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_DEATH_WORM);
        addFeatureToBiome(BiomeConfig.wanderingCyclopsBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_WANDERING_CYCLOPS);
        addFeatureToBiome(BiomeConfig.lightningDragonSkeletonBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_DRAGON_SKELETON_L);
        addFeatureToBiome(BiomeConfig.fireDragonSkeletonBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_DRAGON_SKELETON_F);
        addFeatureToBiome(BiomeConfig.iceDragonSkeletonBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_DRAGON_SKELETON_I);
        addFeatureToBiome(BiomeConfig.hippocampusBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_HIPPOCAMPUS);
        addFeatureToBiome(BiomeConfig.seaSerpentBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_SEA_SERPENT);
        addFeatureToBiome(BiomeConfig.stymphalianBiomes.getRight(), IafPlacedFeatures.PLACED_SPAWN_STYMPHALIAN_BIRD);
    }

    private static void addFeatureToBiome(SpawnBiomeData data, RegistryKey<PlacedFeature> feature) {
        addFeatureToBiome(data, feature, GenerationStep.Feature.SURFACE_STRUCTURES);
    }

    private static void addFeatureToBiome(SpawnBiomeData data, RegistryKey<PlacedFeature> featureResource, GenerationStep.Feature step) {
        BiomeModifications.addFeature(context -> data.matches(context.getBiomeRegistryEntry(), context.getBiomeKey().getValue()), step, featureResource);
    }
}
