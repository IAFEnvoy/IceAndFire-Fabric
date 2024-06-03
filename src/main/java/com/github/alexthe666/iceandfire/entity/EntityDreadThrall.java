package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.entity.ai.DreadAITargetNonDread;
import com.github.alexthe666.iceandfire.entity.util.*;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityDreadThrall extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHasArmorVariant {

    private static final TrackedData<Boolean> CUSTOM_ARMOR_HEAD = DataTracker.registerData(EntityDreadThrall.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CUSTOM_ARMOR_CHEST = DataTracker.registerData(EntityDreadThrall.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CUSTOM_ARMOR_LEGS = DataTracker.registerData(EntityDreadThrall.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CUSTOM_ARMOR_FEET = DataTracker.registerData(EntityDreadThrall.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> CUSTOM_ARMOR_INDEX = DataTracker.registerData(EntityDreadThrall.class, TrackedDataHandlerRegistry.INTEGER);
    public static Animation ANIMATION_SPAWN = Animation.create(40);
    private int animationTick;
    private Animation currentAnimation;

    public EntityDreadThrall(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, IDreadMob.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity entity) {
                return DragonUtils.canHostilesTarget(entity);
            }
        }));
        this.targetSelector.add(3, new DreadAITargetNonDread(this, LivingEntity.class, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity entity) {
                return entity instanceof LivingEntity && DragonUtils.canHostilesTarget(entity);
            }
        }));
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
            //HEALTH
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
            //SPEED
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
            //ATTACK
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D)
            //FOLLOW RANGE
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 128.0D)
            //ARMOR
            .add(EntityAttributes.GENERIC_ARMOR, 2.0D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CUSTOM_ARMOR_INDEX, Integer.valueOf(0));
        this.dataTracker.startTracking(CUSTOM_ARMOR_HEAD, Boolean.valueOf(false));
        this.dataTracker.startTracking(CUSTOM_ARMOR_CHEST, Boolean.valueOf(false));
        this.dataTracker.startTracking(CUSTOM_ARMOR_LEGS, Boolean.valueOf(false));
        this.dataTracker.startTracking(CUSTOM_ARMOR_FEET, Boolean.valueOf(false));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = getWorld().getBlockState(this.getBlockPos().down());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, belowBlock), this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getBoundingBox().minY, this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            this.setVelocity(0, this.getVelocity().y, 0);
        }
        if (this.getMainHandStack().getItem() == Items.BOW) {
            this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BONE));
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    protected void initEquipment(Random randomSource, @NotNull LocalDifficulty difficulty) {
        super.initEquipment(randomSource, difficulty);
        if (random.nextFloat() < 0.75F) {
            double chance = random.nextFloat();
            if (chance < 0.0025F) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(IafItemRegistry.DRAGONSTEEL_ICE_SWORD.get()));
            }
            if (chance < 0.01F) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
            }
            if (chance < 0.1F) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            }
            if (chance < 0.75F) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(IafItemRegistry.DREAD_SWORD.get()));
            }
        }
        if (random.nextFloat() < 0.75F) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
            setCustomArmorHead(random.nextInt(8) != 0);
        }
        if (random.nextFloat() < 0.75F) {
            this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            setCustomArmorChest(random.nextInt(8) != 0);
        }
        if (random.nextFloat() < 0.75F) {
            this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
            setCustomArmorLegs(random.nextInt(8) != 0);
        }
        if (random.nextFloat() < 0.75F) {
            this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
            setCustomArmorFeet(random.nextInt(8) != 0);
        }
        setBodyArmorVariant(random.nextInt(8));
    }

    @Override
    public EntityData initialize(@NotNull ServerWorldAccess worldIn, @NotNull LocalDifficulty difficultyIn, @NotNull SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        EntityData data = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setAnimation(ANIMATION_SPAWN);
        this.initEquipment(worldIn.getRandom(), difficultyIn);
        return data;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("ArmorVariant", getBodyArmorVariant());
        compound.putBoolean("HasCustomHelmet", hasCustomArmorHead());
        compound.putBoolean("HasCustomChestplate", hasCustomArmorChest());
        compound.putBoolean("HasCustomLeggings", hasCustomArmorLegs());
        compound.putBoolean("HasCustomBoots", hasCustomArmorFeet());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        setBodyArmorVariant(compound.getInt("ArmorVariant"));
        setCustomArmorHead(compound.getBoolean("HasCustomHelmet"));
        setCustomArmorChest(compound.getBoolean("HasCustomChestplate"));
        setCustomArmorLegs(compound.getBoolean("HasCustomLeggings"));
        setCustomArmorFeet(compound.getBoolean("HasCustomBoots"));
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    public boolean hasCustomArmorHead() {
        return this.dataTracker.get(CUSTOM_ARMOR_HEAD).booleanValue();
    }

    public void setCustomArmorHead(boolean head) {
        this.dataTracker.set(CUSTOM_ARMOR_HEAD, head);
    }

    public boolean hasCustomArmorChest() {
        return this.dataTracker.get(CUSTOM_ARMOR_CHEST).booleanValue();
    }

    public void setCustomArmorChest(boolean head) {
        this.dataTracker.set(CUSTOM_ARMOR_CHEST, head);
    }

    public boolean hasCustomArmorLegs() {
        return this.dataTracker.get(CUSTOM_ARMOR_LEGS).booleanValue();
    }

    public void setCustomArmorLegs(boolean head) {
        this.dataTracker.set(CUSTOM_ARMOR_LEGS, head);
    }

    public boolean hasCustomArmorFeet() {
        return this.dataTracker.get(CUSTOM_ARMOR_FEET).booleanValue();
    }

    public void setCustomArmorFeet(boolean head) {
        this.dataTracker.set(CUSTOM_ARMOR_FEET, head);
    }

    @Override
    public int getBodyArmorVariant() {
        return this.dataTracker.get(CUSTOM_ARMOR_INDEX).intValue();
    }

    @Override
    public void setBodyArmorVariant(int variant) {
        this.dataTracker.set(CUSTOM_ARMOR_INDEX, variant);
    }

    @Override
    public int getLegArmorVariant() {
        return 0;
    }

    @Override
    public void setLegArmorVariant(int variant) {

    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPAWN};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean shouldFear() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRAY_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_STRAY_STEP, 0.15F, 1.0F);
    }

}