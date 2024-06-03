package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

public class EntityStymphalianFeather extends PersistentProjectileEntity {

    public EntityStymphalianFeather(EntityType<? extends PersistentProjectileEntity> t, World worldIn) {
        super(t, worldIn);
    }

    public EntityStymphalianFeather(EntityType<? extends PersistentProjectileEntity> t, World worldIn, LivingEntity shooter) {
        super(t, shooter, worldIn);
        this.setDamage(IafConfig.stymphalianBirdFeatherAttackStength);
    }

    public EntityStymphalianFeather(PlayMessages.SpawnEntity spawnEntity, World world) {
        this(IafEntityRegistry.STYMPHALIAN_FEATHER.get(), world);
    }

    @Override
    public @NotNull Packet<ClientPlayPacketListener> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        if (IafConfig.stymphalianBirdFeatherDropChance > 0) {
            if (this.getWorld().isClient && this.random.nextInt(IafConfig.stymphalianBirdFeatherDropChance) == 0) {
                this.dropStack(asItemStack(), 0.1F);
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > 100) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onEntityHit(@NotNull EntityHitResult entityHit) {
        Entity shootingEntity = this.getOwner();
        if (shootingEntity instanceof EntityStymphalianBird && entityHit.getEntity() != null && entityHit.getEntity() instanceof EntityStymphalianBird) {
        } else {
            super.onEntityHit(entityHit);
            if (entityHit.getEntity() != null && entityHit.getEntity() instanceof EntityStymphalianBird) {
                LivingEntity LivingEntity = (LivingEntity) entityHit.getEntity();
                LivingEntity.setStuckArrowCount(LivingEntity.getStuckArrowCount() - 1);
                ItemStack itemstack1 = LivingEntity.isUsingItem() ? LivingEntity.getActiveItem() : ItemStack.EMPTY;
                if (itemstack1.getItem().canPerformAction(itemstack1, ToolActions.SHIELD_BLOCK)) {
                    damageShield(LivingEntity, 1.0F);
                }
            }

        }
    }

    protected void damageShield(LivingEntity entity, float damage) {
        if (damage >= 3.0F && entity.getActiveItem().getItem().canPerformAction(entity.getActiveItem(), ToolActions.SHIELD_BLOCK)) {
            ItemStack copyBeforeUse = entity.getActiveItem().copy();
            int i = 1 + MathHelper.floor(damage);
            Hand Hand = entity.getActiveHand();
            copyBeforeUse.damage(i, entity, (player1) -> {
                player1.sendToolBreakStatus(Hand);
            });
            if (entity.getActiveItem().isEmpty()) {
                if (entity instanceof PlayerEntity) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((PlayerEntity) entity, copyBeforeUse, Hand);
                }

                if (Hand == net.minecraft.util.Hand.MAIN_HAND) {
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F);
            }
        }
    }

    @Override
    protected @NotNull ItemStack asItemStack() {
        return new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER.get());
    }
}