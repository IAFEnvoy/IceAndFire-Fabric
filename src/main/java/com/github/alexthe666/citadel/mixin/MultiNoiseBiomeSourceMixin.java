package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.github.alexthe666.citadel.server.generation.IMultiNoiseBiomeSourceAccessor;
import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiNoiseBiomeSource.class, priority = -420)
public class MultiNoiseBiomeSourceMixin implements IMultiNoiseBiomeSourceAccessor {

    private int lastSampledX;
    private int lastSampledY;
    private int lastSampledZ;

    private long lastSampledWorldSeed;

    private RegistryKey<World> lastSampledDimension;

    @Inject(at = @At("HEAD"),
            remap = CitadelConstants.REMAPREFS,
            method = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            cancellable = true
    )
    private void citadel_getNoiseBiomeCoords(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler sampler, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {
        MultiNoiseUtil.NoiseValuePoint targetPoint = sampler.sample(x, y, z);
        float f = MultiNoiseUtil.toFloat(targetPoint.continentalnessNoise());
        float f1 = MultiNoiseUtil.toFloat(targetPoint.erosionNoise());
        float f2 = MultiNoiseUtil.toFloat(targetPoint.temperatureNoise());
        float f3 = MultiNoiseUtil.toFloat(targetPoint.humidityNoise());
        float f4 = MultiNoiseUtil.toFloat(targetPoint.weirdnessNoise());
        float f5 = MultiNoiseUtil.toFloat(targetPoint.depth());
        EventReplaceBiome event = new EventReplaceBiome((ExpandedBiomeSource) this, cir.getReturnValue(), x, y, z, f, f1, f2, f3, f4, f5, lastSampledWorldSeed, lastSampledDimension, sampler);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.ALLOW) {
            cir.setReturnValue(event.getBiomeToGenerate());
        }
    }

    @Override
    public void setLastSampledSeed(long seed) {
        lastSampledWorldSeed = seed;
    }

    @Override
    public void setLastSampledDimension(RegistryKey<World> dimension) {
        lastSampledDimension = dimension;
    }
}