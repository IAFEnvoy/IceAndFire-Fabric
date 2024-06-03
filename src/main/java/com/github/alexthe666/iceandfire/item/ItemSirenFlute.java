package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.props.EntityDataProvider;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ItemSirenFlute extends Item {

    public ItemSirenFlute() {
        super(new Settings()/*.tab(IceAndFire.TAB_ITEMS)*/.maxDamage(200));
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> use(@NotNull World worldIn, PlayerEntity player, @NotNull Hand hand) {
        ItemStack itemStackIn = player.getStackInHand(hand);
        player.setCurrentHand(hand);
        player.getItemCooldownManager().set(this, 900);

        double dist = 32;
        Vec3d Vector3d = player.getCameraPosVec(1.0F);
        Vec3d Vector3d1 = player.getRotationVec(1.0F);
        Vec3d Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);

        double d1 = dist;
        Entity pointedEntity = null;
        List<Entity> list = player.getWorld().getOtherEntities(player, player.getBoundingBox().stretch(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).expand(1.0D, 1.0D, 1.0D), new Predicate<Entity>() {
            @Override
            public boolean test(Entity entity) {
                boolean blindness = entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS) || (entity instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) entity).canBeTurnedToStone());
                return entity != null && entity.canHit() && !blindness && (entity instanceof PlayerEntity || (entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity)));
            }
        });

        double d2 = d1;
        for (int j = 0; j < list.size(); ++j) {
            Entity entity1 = list.get(j);
            Box axisalignedbb = entity1.getBoundingBox().expand(entity1.getTargetingMargin());
            Optional<Vec3d> raytraceresult = axisalignedbb.raycast(Vector3d, Vector3d2);

            if (axisalignedbb.contains(Vector3d)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity1;
                    d2 = 0.0D;
                }
            } else if (raytraceresult.isPresent()) {
                double d3 = Vector3d.distanceTo(raytraceresult.get());
                if (d3 < d2 || d2 == 0.0D) {
                    if (entity1.getRootVehicle() == player.getRootVehicle() && !player.canRiderInteract()) {
                        if (d2 == 0.0D) {
                            pointedEntity = entity1;
                        }
                    } else {
                        pointedEntity = entity1;
                        d2 = d3;
                    }
                }
            }
        }

        if (pointedEntity != null) {
            if (pointedEntity instanceof LivingEntity) {
                EntityDataProvider.getCapability(pointedEntity).ifPresent(data -> data.miscData.setLoveTicks(600));
                itemStackIn.damage(2, player, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }

        player.playSound(IafSoundRegistry.SIREN_SONG, 1, 1);
        return new TypedActionResult<>(ActionResult.PASS, itemStackIn);
    }


    @Override
    public void appendTooltip(@NotNull ItemStack stack, World worldIn, List<Text> tooltip, @NotNull TooltipContext flagIn) {
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.siren_flute.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.siren_flute.desc_1").formatted(Formatting.GRAY));
    }
}