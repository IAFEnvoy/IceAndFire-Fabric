package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.function.Function;
import net.minecraft.util.Util;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

@Mixin(NoiseChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    private final Function<MaterialRules.MaterialRule, MaterialRules.MaterialRule> rulesToMerge = Util.memoize(SurfaceRulesManager::mergeOverworldRules);
    private final HashMap<ChunkGeneratorSettings, MaterialRules.MaterialRule> mergedRulesMap = new HashMap<>();

    @Redirect(
            method = {"Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;")
    )
    private MaterialRules.MaterialRule citadel_buildSurface_surfaceRuleRedirect(ChunkGeneratorSettings noiseGeneratorSettings) {
        return getMergedRulesFor(noiseGeneratorSettings);
    }

    @Redirect(
            method = {"Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;applyCarvers(Lnet/minecraft/server/level/WorldGenRegion;JLnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/GenerationStep$Carving;)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;")
    )
    private MaterialRules.MaterialRule citadel_applyCarvers_surfaceRuleRedirect(ChunkGeneratorSettings noiseGeneratorSettings) {
        return getMergedRulesFor(noiseGeneratorSettings);
    }

    private MaterialRules.MaterialRule getMergedRulesFor(ChunkGeneratorSettings settings){
        MaterialRules.MaterialRule merged = mergedRulesMap.get(settings);
        if(merged == null){
            merged = rulesToMerge.apply(settings.surfaceRule());
            mergedRulesMap.put(settings, merged);
        }
        return merged;
    }
}