package com.github.alexthe666.iceandfire.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemCannoli extends ItemGenericFood {

    public ItemCannoli() {
        super(20, 2.0F, false, false, true);
    }

    @Override
    public void onFoodEaten(ItemStack stack, World worldIn, LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 3600, 2));
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, World worldIn, List<Text> tooltip, @NotNull TooltipContext flagIn) {
        tooltip.add(Text.translatable("item.iceandfire.cannoli.desc").formatted(Formatting.GRAY));
    }
}