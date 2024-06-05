package com.github.alexthe666.iceandfire.datagen;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.vanilla.VanillaBiomeTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.concurrent.CompletableFuture;

public class IafBiomeTagGenerator extends VanillaBiomeTagProvider {
    public static final TagKey<Biome> HAS_GORGON_TEMPLE = TagKey.of(RegistryKeys.BIOME, new Identifier(IceAndFire.MOD_ID, "has_structure/gorgon_temple"));
    public static final TagKey<Biome> HAS_MAUSOLEUM = TagKey.of(RegistryKeys.BIOME, new Identifier(IceAndFire.MOD_ID, "has_structure/mausoleum"));
    public static final TagKey<Biome> HAS_GRAVEYARD = TagKey.of(RegistryKeys.BIOME, new Identifier(IceAndFire.MOD_ID, "has_structure/graveyard"));


    public IafBiomeTagGenerator(DataOutput pOutput, CompletableFuture<RegistryWrapper.WrapperLookup> pProvider) {
        super(pOutput, pProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup pProvider) {
        this.getOrCreateTagBuilder(HAS_GRAVEYARD).addTag(BiomeTags.IS_OVERWORLD);
        this.getOrCreateTagBuilder(HAS_MAUSOLEUM).addTag(BiomeTags.IS_OVERWORLD);
        this.getOrCreateTagBuilder(HAS_GORGON_TEMPLE).addTag(BiomeTags.IS_OVERWORLD);
    }

    @Override
    public String getName() {
        return "Ice and Fire Biome Tags";
    }
}
