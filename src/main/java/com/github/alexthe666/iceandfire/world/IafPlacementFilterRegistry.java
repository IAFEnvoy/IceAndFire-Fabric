package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class IafPlacementFilterRegistry {
    public static final PlacementModifierType<CustomBiomeFilter> CUSTOM_BIOME_FILTER = register("biome_extended", () -> CustomBiomeFilter.CODEC);

    public static <T extends PlacementModifier> PlacementModifierType<T> register(String name, PlacementModifierType<T> type) {
        return Registry.register(Registries.PLACEMENT_MODIFIER_TYPE, new Identifier(IceAndFire.MOD_ID, name), type);
    }

    public static void init() {
    }
}
