package com.github.alexthe666.iceandfire.item;


import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemDragonHorn extends Item {

    public ItemDragonHorn() {
        super((new Settings())/*.tab(IceAndFire.TAB_ITEMS)*/.maxCount(1));
    }

    public static int getDragonType(ItemStack stack) {
        if (stack.getNbt() != null) {
            String id = stack.getNbt().getString("DragonHornEntityID");
            if (EntityType.get(id).isPresent()) {
                EntityType<?> entityType = EntityType.get(id).get();
                if (entityType == IafEntityRegistry.FIRE_DRAGON.get())
                    return 1;

                if (entityType == IafEntityRegistry.ICE_DRAGON.get())
                    return 2;

                if (entityType == IafEntityRegistry.LIGHTNING_DRAGON.get())
                    return 3;
            }
        }

        return 0;
    }


    @Override
    public void onCraft(ItemStack itemStack, @NotNull World world, @NotNull PlayerEntity player) {
        itemStack.setNbt(new NbtCompound());
    }


    @Override
    public @NotNull ActionResult useOnEntity(@NotNull ItemStack stack, PlayerEntity playerIn, @NotNull LivingEntity target, @NotNull Hand hand) {
        ItemStack trueStack = playerIn.getStackInHand(hand);
        if (!playerIn.getWorld().isClient && hand == Hand.MAIN_HAND && target instanceof EntityDragonBase && ((EntityDragonBase) target).isOwner(playerIn) && (trueStack.getNbt() == null || trueStack.getNbt().getCompound("EntityTag").isEmpty())) {
            NbtCompound newTag = new NbtCompound();

            NbtCompound entityTag = new NbtCompound();
            target.saveNbt(entityTag);
            newTag.put("EntityTag", entityTag);

            newTag.putString("DragonHornEntityID", Registries.ENTITY_TYPE.getKey(target.getType()).toString());
            trueStack.setNbt(newTag);

            playerIn.swingHand(hand);
            playerIn.getWorld().playSound(playerIn, playerIn.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 3.0F, 0.75F);
            target.remove(Entity.RemovalReason.DISCARDED);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }


    @Override
    public @NotNull ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getSide() != Direction.UP)
            return ActionResult.FAIL;
        ItemStack stack = context.getStack();
        if (stack.getNbt() != null && !stack.getNbt().getString("DragonHornEntityID").isEmpty()) {
            World world = context.getWorld();
            String id = stack.getNbt().getString("DragonHornEntityID");
            EntityType<?> type = EntityType.get(id).orElse(null);
            if (type != null) {
                Entity entity = type.create(world);
                if (entity instanceof EntityDragonBase dragon) {
                    dragon.readNbt(stack.getNbt().getCompound("EntityTag"));
                }
                //Still needed to allow for intercompatibility
                if (stack.getNbt().contains("EntityUUID"))
                    entity.setUuid(stack.getNbt().getUuid("EntityUUID"));

                entity.updatePositionAndAngles(context.getBlockPos().getX() + 0.5D, (context.getBlockPos().getY() + 1), context.getBlockPos().getZ() + 0.5D, 180 + (context.getHorizontalPlayerFacing()).asRotation(), 0.0F);
                if (world.spawnEntity(entity)) {
                    NbtCompound tag = stack.getNbt();
                    tag.remove("DragonHornEntityID");
                    tag.remove("EntityTag");
                    tag.remove("EntityUUID");
                    stack.setNbt(tag);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, @NotNull List<Text> tooltip, @NotNull TooltipContext flagIn) {
        if (stack.getNbt() != null) {
            NbtCompound entityTag = stack.getNbt().getCompound("EntityTag");
            if (!entityTag.isEmpty()) {
                String id = stack.getNbt().getString("DragonHornEntityID");
                if (EntityType.get(id).isPresent()) {
                    EntityType<?> type = EntityType.get(id).get();
                    tooltip.add((Text.translatable(type.getTranslationKey())).formatted(this.getTextColorForEntityType(type)));
                    String name = (Text.translatable("dragon.unnamed")).getString();
                    if (!entityTag.getString("CustomName").isEmpty()) {
                        MutableText component = Text.Serializer.fromJson(entityTag.getString("CustomName"));
                        if (component != null)
                            name = component.getString();
                    }

                    tooltip.add((Text.literal(name)).formatted(Formatting.GRAY));
                    String gender = (Text.translatable("dragon.gender")).getString() + " " + (Text.translatable(entityTag.getBoolean("Gender") ? "dragon.gender.male" : "dragon.gender.female")).getString();
                    tooltip.add((Text.literal(gender)).formatted(Formatting.GRAY));
                    int stagenumber = entityTag.getInt("AgeTicks") / 24000;
                    int stage1 = 0;
                    if (stagenumber >= 100) {
                        stage1 = 5;
                    } else if (stagenumber >= 75) {
                        stage1 = 4;
                    } else if (stagenumber >= 50) {
                        stage1 = 3;
                    } else if (stagenumber >= 25) {
                        stage1 = 2;
                    } else {
                        stage1 = 1;
                    }
                    String stage = (Text.translatable("dragon.stage")).getString() + " " + stage1 + " " + (Text.translatable("dragon.days.front")).getString() + stagenumber + " " + (Text.translatable("dragon.days.back")).getString();
                    tooltip.add((Text.literal(stage)).formatted(Formatting.GRAY));
                }
            }

        }
    }

    private Formatting getTextColorForEntityType(EntityType type) {
        if (type == IafEntityRegistry.FIRE_DRAGON.get())
            return Formatting.DARK_RED;

        if (type == IafEntityRegistry.ICE_DRAGON.get())
            return Formatting.BLUE;

        if (type == IafEntityRegistry.LIGHTNING_DRAGON.get())
            return Formatting.DARK_PURPLE;

        return Formatting.GRAY;
    }
}
