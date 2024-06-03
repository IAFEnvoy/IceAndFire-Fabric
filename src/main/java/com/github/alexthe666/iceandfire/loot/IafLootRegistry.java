package com.github.alexthe666.iceandfire.loot;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.util.IdUtil;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;

public class IafLootRegistry {

    public static LootFunctionType CUSTOMIZE_TO_DRAGON;
    public static LootFunctionType CUSTOMIZE_TO_SERPENT;

    private static LootFunctionType register(String p_237451_0_, JsonSerializer<? extends LootFunction> p_237451_1_) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(p_237451_0_), new LootFunctionType(p_237451_1_));
    }

    public static void init() {
        CUSTOMIZE_TO_DRAGON = register(IdUtil.build(IceAndFire.MOD_ID,"customize_to_dragon"), new CustomizeToDragon.Serializer());
        CUSTOMIZE_TO_SERPENT = register(IdUtil.build(IceAndFire.MOD_ID,"customize_to_sea_serpent"), new CustomizeToSeaSerpent.Serializer());
    }

}