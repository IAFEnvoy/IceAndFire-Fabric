package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.*;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntitySeaSerpent extends AnimalEntity implements IAnimatedEntity, IMultipartEntity, IVillagerFear, IAnimalFear, IHasCustomizableAttributes {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_ROAR = Animation.create(40);
    public static final int TIME_BETWEEN_ROARS = 300;
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(EntitySeaSerpent.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(EntitySeaSerpent.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> JUMPING = DataTracker.registerData(EntitySeaSerpent.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BREATHING = DataTracker.registerData(EntitySeaSerpent.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ANCIENT = DataTracker.registerData(EntitySeaSerpent.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final Predicate<Entity> NOT_SEA_SERPENT = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity instanceof LivingEntity && !(entity instanceof EntitySeaSerpent) && DragonUtils.isAlive((LivingEntity) entity);
        }
    };
    private static final Predicate<Entity> NOT_SEA_SERPENT_IN_WATER = new Predicate<Entity>() {
        @Override
        public boolean apply(Entity entity) {
            return entity instanceof LivingEntity && !(entity instanceof EntitySeaSerpent) && DragonUtils.isAlive((LivingEntity) entity) && entity.isInsideWaterOrBubbleColumn();
        }
    };
    public int swimCycle;
    public float jumpProgress = 0.0F;
    public float wantJumpProgress = 0.0F;
    public float jumpRot = 0.0F;
    public float prevJumpRot = 0.0F;
    public float breathProgress = 0.0F;
    //true  = melee, false = ranged
    public boolean attackDecision = false;
    private int animationTick;
    private Animation currentAnimation;
    private EntityMutlipartPart[] segments = new EntityMutlipartPart[9];
    private float lastScale;
    private boolean isLandNavigator;
    private boolean changedSwimBehavior = false;
    public int jumpCooldown = 0;
    private int ticksSinceRoar = 0;
    private boolean isBreathing;
    private final float[] tailYaw = new float[5];
    private final float[] prevTailYaw = new float[5];
    private final float[] tailPitch = new float[5];
    private final float[] prevTailPitch = new float[5];

    public EntitySeaSerpent(EntityType<EntitySeaSerpent> t, World worldIn) {
        super(t, worldIn);
        switchNavigator(false);
        this.ignoreCameraFrustum = true;
        resetParts(1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    private static BlockPos clampBlockPosToWater(Entity entity, World world, BlockPos pos) {
        BlockPos topY = new BlockPos(pos.getX(), entity.getBlockY(), pos.getZ());
        BlockPos bottomY = new BlockPos(pos.getX(), entity.getBlockY(), pos.getZ());
        while (isWaterBlock(world, topY) && topY.getY() < world.getTopY()) {
            topY = topY.up();
        }
        while (isWaterBlock(world, bottomY) && bottomY.getY() > 0) {
            bottomY = bottomY.down();
        }
        return new BlockPos(pos.getX(), MathHelper.clamp(pos.getY(), bottomY.getY() + 1, topY.getY() - 1), pos.getZ());
    }

    public static boolean isWaterBlock(World world, BlockPos pos) {
        return world.getFluidState(pos).isIn(FluidTags.WATER);
    }

    @Override
    public @NotNull SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SeaSerpentAIGetInWater(this));
        this.goalSelector.add(1, new SeaSerpentAIMeleeJump(this));
        this.goalSelector.add(1, new SeaSerpentAIAttackMelee(this, 1.0D, true));
        this.goalSelector.add(2, new SeaSerpentAIRandomSwimming(this, 1.0D, 2));
        this.goalSelector.add(3, new SeaSerpentAIJump(this, 4));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, EntityMutlipartPart.class)).setGroupRevenge());
        this.targetSelector.add(2, new FlyingAITarget(this, LivingEntity.class, 150, false, false, NOT_SEA_SERPENT_IN_WATER));
        this.targetSelector.add(3, new FlyingAITarget(this, PlayerEntity.class, 0, false, false, NOT_SEA_SERPENT));
    }

    @Override
    public int getXpToDrop() {
        return this.isAncient() ? 30 : 15;
    }

    @Override
    public void tickCramming() {
        List<Entity> entities = this.getWorld().getOtherEntities(this, this.getBoundingBox().stretch(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        entities.stream().filter(entity -> !(entity instanceof EntityMutlipartPart) && entity.isPushable()).forEach(entity -> entity.pushAwayFrom(this));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new MobNavigation(this, getWorld());
            this.navigation.setCanSwim(true);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new SwimmingMoveHelper(this);
            this.navigation = new SeaSerpentPathNavigator(this, getWorld());
            this.isLandNavigator = false;
        }
    }

    public boolean isDirectPathBetweenPoints(BlockPos pos) {
        Vec3d vector3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
        Vec3d bector3d1 = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        return this.getWorld().raycast(new RaycastContext(vector3d, bector3d1, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;

    }

    @Override
    public @NotNull EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
            //HEALTH
            .add(EntityAttributes.GENERIC_MAX_HEALTH, IafConfig.seaSerpentBaseHealth)
            //SPEED
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
            //ATTACK
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
            //FALLOW RANGE
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, Math.min(2048, IafConfig.dragonTargetSearchLength))
            //ARMOR
            .add(EntityAttributes.GENERIC_ARMOR, 3.0D);
    }


    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(IafConfig.seaSerpentBaseHealth);
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(Math.min(2048, IafConfig.dragonTargetSearchLength));
        this.updateAttributes();
    }

    public void resetParts(float scale) {
        clearParts();
        segments = new EntityMutlipartPart[9];
        for (int i = 0; i < segments.length; i++) {
            if (i > 3) {
                Entity parentToSet = i <= 4 ? this : segments[i-1];
                segments[i] = new EntitySlowPart(parentToSet, 0.5F * scale, 180, 0, 0.5F * scale, 0.5F * scale, 1);
            } else {
                Entity parentToSet = i == 0 ? this : segments[i-1];
                segments[i] = new EntitySlowPart(parentToSet, -0.4F * scale, 180, 0, 0.45F * scale, 0.4F * scale, 1);
            }
            segments[i].copyPositionAndRotation(this);
        }
    }

    public void onUpdateParts() {
        for (EntityMutlipartPart entity : segments) {
            EntityUtil.updatePart(entity, this);
        }
    }

    private void clearParts() {
        for (EntityMutlipartPart entity : segments) {
            if (entity != null) {
                entity.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        clearParts();
        super.remove(reason);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull EntityPose poseIn) {
        return this.getType().getDimensions().scaled(this.getScaleFactor());
    }

    @Override
    public float getScaleFactor() {
        return this.getSeaSerpentScale();
    }

    @Override
    public void calculateDimensions() {
        super.calculateDimensions();
        float scale = this.getSeaSerpentScale();
        if (scale != lastScale) {
            resetParts(this.getSeaSerpentScale());
        }
        lastScale = scale;
    }


    @Override
    public boolean tryAttack(@NotNull Entity entityIn) {
        if (this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_BITE);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(jumpCooldown > 0){
            jumpCooldown--;
        }
        calculateDimensions();
        onUpdateParts();
        if (this.isTouchingWater()) {
            spawnParticlesAroundEntity(ParticleTypes.BUBBLE, this, (int) this.getSeaSerpentScale());

        }
        if (!this.getWorld().isClient && this.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget(null);
        }
        System.arraycopy(tailYaw, 0, prevTailYaw, 0, tailYaw.length);
        System.arraycopy(tailPitch, 0, prevTailPitch, 0, tailPitch.length);
        this.tailYaw[0] = this.bodyYaw;
        this.tailPitch[0] = this.getPitch();
        System.arraycopy(prevTailYaw, 0, tailYaw, 1, tailYaw.length - 1);
        System.arraycopy(prevTailPitch, 0, tailPitch, 1, tailPitch.length - 1);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public float getPieceYaw(int index, float partialTicks){
        if(index < segments.length && index >= 0){
            return prevTailYaw[index] + (tailYaw[index] - prevTailYaw[index]) * partialTicks;
        }
        return 0;
    }

    public float getPiecePitch(int index, float partialTicks){
        if(index < segments.length && index >= 0){
            return prevTailPitch[index] + (tailPitch[index] - prevTailPitch[index]) * partialTicks;
        }
        return 0;
    }


    private void spawnParticlesAroundEntity(ParticleEffect type, Entity entity, int count) {
        for (int i = 0; i < count; i++) {
            int x = (int) Math.round(entity.getX() + this.random.nextFloat() * entity.getWidth() * 2.0F - entity.getWidth());
            int y = (int) Math.round(entity.getY() + 0.5D + this.random.nextFloat() * entity.getHeight());
            int z = (int) Math.round(entity.getZ() + this.random.nextFloat() * entity.getWidth() * 2.0F - entity.getWidth());
            if (this.getWorld().getBlockState(new BlockPos(x, y, z)).isOf(Blocks.WATER)) {
                this.getWorld().addParticle(type, x, y, z, 0, 0, 0);
            }
        }
    }

    private void spawnSlamParticles(ParticleEffect type) {
        for (int i = 0; i < this.getSeaSerpentScale() * 3; i++) {
            for (int i1 = 0; i1 < 5; i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float radius = 1.25F * getSeaSerpentScale();
                float angle = (0.01745329251F * this.bodyYaw) + i1 * 1F;
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * MathHelper.cos(angle);
                if (getWorld().isClient) {
                    getWorld().addParticle(type, true, this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(SCALE, 0F);
        this.dataTracker.startTracking(JUMPING, false);
        this.dataTracker.startTracking(BREATHING, false);
        this.dataTracker.startTracking(ANCIENT, false);
    }

    @Override
    public void writeCustomDataToNbt(@NotNull NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("TicksSinceRoar", ticksSinceRoar);
        compound.putInt("JumpCooldown", jumpCooldown);
        compound.putFloat("Scale", this.getSeaSerpentScale());
        compound.putBoolean("JumpingOutOfWater", this.isJumpingOutOfWater());
        compound.putBoolean("AttackDecision", attackDecision);
        compound.putBoolean("Breathing", this.isBreathing());
        compound.putBoolean("Ancient", this.isAncient());
    }

    @Override
    public void readCustomDataFromNbt(@NotNull NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getInt("Variant"));
        ticksSinceRoar = compound.getInt("TicksSinceRoar");
        jumpCooldown = compound.getInt("JumpCooldown");
        this.setSeaSerpentScale(compound.getFloat("Scale"));
        this.setJumpingOutOfWater(compound.getBoolean("JumpingOutOfWater"));
        attackDecision = compound.getBoolean("AttackDecision");
        this.setBreathing(compound.getBoolean("Breathing"));
        this.setAncient(compound.getBoolean("Ancient"));
        this.setConfigurableAttributes();
    }

    private void updateAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(Math.min(0.25D, 0.15D * this.getSeaSerpentScale() * this.getAncientModifier()));
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.max(4, IafConfig.seaSerpentAttackStrength * this.getSeaSerpentScale() * this.getAncientModifier()));
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(10, IafConfig.seaSerpentBaseHealth * this.getSeaSerpentScale() * this.getAncientModifier()));
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(Math.min(2048, IafConfig.dragonTargetSearchLength));
        this.heal(30F * this.getSeaSerpentScale());
    }

    private float getAncientModifier() {
        return this.isAncient() ? 1.5F : 1.0F;
    }

    public float getSeaSerpentScale() {
        return this.dataTracker.get(SCALE).floatValue();
    }

    private void setSeaSerpentScale(float scale) {
        this.dataTracker.set(SCALE, scale);
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public boolean isJumpingOutOfWater() {
        return this.dataTracker.get(JUMPING).booleanValue();
    }

    public void setJumpingOutOfWater(boolean jump) {
        this.dataTracker.set(JUMPING, jump);
    }

    public boolean isAncient() {
        return this.dataTracker.get(ANCIENT).booleanValue();
    }

    public void setAncient(boolean ancient) {
        this.dataTracker.set(ANCIENT, ancient);
    }

    public boolean isBreathing() {
        if (getWorld().isClient) {
            boolean breathing = this.dataTracker.get(BREATHING).booleanValue();
            this.isBreathing = breathing;
            return breathing;
        }
        return isBreathing;
    }

    public void setBreathing(boolean breathing) {
        this.dataTracker.set(BREATHING, breathing);
        if (!getWorld().isClient) {
            this.isBreathing = breathing;
        }
    }

    @Override
    protected void fall(double y, boolean onGroundIn, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!getWorld().isClient) {
            if (getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity) {
                this.setTarget(null);
            }
        }
        boolean breathing = isBreathing() && this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_ROAR;
        boolean jumping = !this.isTouchingWater() && !this.isOnGround() && this.getVelocity().y >= 0;
        boolean wantJumping = false; //(ticksSinceJump > TIME_BETWEEN_JUMPS) && this.isInWater();
        boolean ground = !isTouchingWater() && this.isOnGround();
        boolean prevJumping = this.isJumpingOutOfWater();
        this.ticksSinceRoar++;
        this.jumpCooldown++;
        this.prevJumpRot = jumpRot;
        if (this.ticksSinceRoar > TIME_BETWEEN_ROARS && isAtSurface() && this.getAnimation() != ANIMATION_BITE && jumpProgress == 0 && !isJumpingOutOfWater()) {
            this.setAnimation(ANIMATION_ROAR);
            this.ticksSinceRoar = 0;
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 1) {
            this.playSound(IafSoundRegistry.SEA_SERPENT_ROAR, this.getSoundVolume() + 1, 1);
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5) {
            this.playSound(IafSoundRegistry.SEA_SERPENT_BITE, this.getSoundVolume(), 1);
        }
        if (isJumpingOutOfWater() && isWaterBlock(getWorld(), this.getBlockPos().up(2))) {
            setJumpingOutOfWater(false);
        }
        if (this.swimCycle < 38) {
            this.swimCycle += 2;
        } else {
            this.swimCycle = 0;
        }
        if (breathing && breathProgress < 20.0F) {
            breathProgress += 0.5F;
        } else if (!breathing && breathProgress > 0.0F) {
            breathProgress -= 0.5F;
        }
        if (jumping && jumpProgress < 10.0F) {
            jumpProgress += 0.5F;
        } else if (!jumping && jumpProgress > 0.0F) {
            jumpProgress -= 0.5F;
        }
        if (wantJumping && wantJumpProgress < 10.0F) {
            wantJumpProgress += 2F;
        } else if (!wantJumping && wantJumpProgress > 0.0F) {
            wantJumpProgress -= 2F;
        }
        if (this.isJumpingOutOfWater() && jumpRot < 1.0F) {
            jumpRot += 0.1F;
        } else if (!this.isJumpingOutOfWater() && jumpRot > 0.0F) {
            jumpRot -= 0.1F;
        }
        if (prevJumping && !this.isJumpingOutOfWater()) {
            this.playSound(IafSoundRegistry.SEA_SERPENT_SPLASH, 5F, 0.75F);
            spawnSlamParticles(ParticleTypes.BUBBLE);
            this.doSplashDamage();
        }
        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }
        setPitch(MathHelper.clamp((float) this.getVelocity().y * 20F, -90, 90));
        if (changedSwimBehavior) {
            changedSwimBehavior = false;
        }
        if (!getWorld().isClient) {
            if (attackDecision) {
                this.setBreathing(false);
            }
            if (this.getTarget() != null && this.getAnimation() != ANIMATION_ROAR) {
                if (!attackDecision) {
                    if (!this.getTarget().isTouchingWater() || !this.canSee(this.getTarget()) || this.distanceTo(this.getTarget()) < 30 * this.getSeaSerpentScale()) {
                        attackDecision = true;
                    }
                    if (!attackDecision) {
                        shoot(this.getTarget());
                    }
                } else {
                    if (this.squaredDistanceTo(this.getTarget()) > 200 * this.getSeaSerpentScale()) {
                        attackDecision = false;
                    }
                }
            } else {
                this.setBreathing(false);
            }
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getTarget() != null && (this.isTouchingMob(this.getTarget()) || this.squaredDistanceTo(this.getTarget()) < 50)) {
            this.hurtMob(this.getTarget());
        }
        breakBlock();
        if (!getWorld().isClient && this.hasVehicle() && this.getRootVehicle() instanceof BoatEntity boat) {
            boat.remove(RemovalReason.KILLED);
            this.stopRiding();
        }
    }

    private boolean isAtSurface() {
        BlockPos pos = this.getBlockPos();
        return isWaterBlock(getWorld(), pos.down()) && !isWaterBlock(getWorld(), pos.up());
    }

    private void doSplashDamage() {
        double getWidth = 2D * this.getSeaSerpentScale();
        List<Entity> list = getWorld().getOtherEntities(this, this.getBoundingBox().expand(getWidth, getWidth * 0.5D, getWidth), NOT_SEA_SERPENT);
        for (Entity entity : list) {
            if (entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity)) {
                entity.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
                destroyBoat(entity);
                double xRatio = this.getX() - entity.getX();
                double zRatio = this.getZ() - entity.getZ();
                float f = MathHelper.sqrt((float) (xRatio * xRatio + zRatio * zRatio));
                float strength = 0.3F * this.getSeaSerpentScale();
                entity.setVelocity(entity.getVelocity().multiply(0.5D, 1D, 0.5D));
                entity.setVelocity(entity.getVelocity().add(xRatio / f * strength, strength, zRatio / f * strength));
            }
        }

    }

    public void destroyBoat(Entity sailor) {
        if (sailor.getVehicle() != null && sailor.getVehicle() instanceof BoatEntity boat && !getWorld().isClient) {
            boat.remove(RemovalReason.KILLED);
            if (this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                for (int i = 0; i < 3; ++i) {
                    boat.dropStack(new ItemStack(boat.getVariant().getBaseBlock().asItem()), 0.0F);
                }
                for (int j = 0; j < 2; ++j) {
                    boat.dropStack(new ItemStack(Items.STICK));
                }
            }
        }
    }

    private boolean isPreyAtSurface() {
        if (this.getTarget() != null) {
            BlockPos pos = this.getTarget().getBlockPos();
            return !isWaterBlock(getWorld(), pos.up((int) Math.ceil(this.getTarget().getHeight())));
        }
        return false;
    }

    private void hurtMob(LivingEntity entity) {
        if (this.getAnimation() == ANIMATION_BITE && entity != null && this.getAnimationTick() == 6) {
            this.getTarget().damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
            EntitySeaSerpent.this.attackDecision = this.getRandom().nextBoolean();
        }
    }

    public void moveJumping() {
        float velocity = 0.5F;
        double x = -MathHelper.sin(this.getYaw() * 0.017453292F) * MathHelper.cos(this.getPitch() * 0.017453292F);
        double z = MathHelper.cos(this.getYaw() * 0.017453292F) * MathHelper.cos(this.getPitch() * 0.017453292F);
        float f = MathHelper.sqrt((float) (x * x + z * z));
        x = x / f;
        z = z / f;
        x = x * velocity;
        z = z * velocity;
        this.setVelocity(x, this.getVelocity().y, z);
    }

    public boolean isTouchingMob(Entity entity) {
        if (this.getBoundingBox().stretch(1, 1, 1).intersects(entity.getBoundingBox())) {
            return true;
        }
        for (Entity segment : segments) {
            if (segment.getBoundingBox().stretch(1, 1, 1).intersects(entity.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    public void breakBlock() {
        if (IafConfig.seaSerpentGriefing) {
            for (int a = (int) Math.round(this.getBoundingBox().minX) - 2; a <= (int) Math.round(this.getBoundingBox().maxX) + 2; a++) {
                for (int b = (int) Math.round(this.getBoundingBox().minY) - 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 2) && (b <= 127); b++) {
                    for (int c = (int) Math.round(this.getBoundingBox().minZ) - 2; c <= (int) Math.round(this.getBoundingBox().maxZ) + 2; c++) {
                        BlockPos pos = new BlockPos(a, b, c);
                        BlockState state = getWorld().getBlockState(pos);
                        FluidState fluidState = getWorld().getFluidState(pos);
                        Block block = state.getBlock();
                        if (!state.isAir() && !state.getOutlineShape(getWorld(), pos).isEmpty() && (state.getBlock() instanceof IPlantable || state.getBlock() instanceof LeavesBlock) && fluidState.isEmpty()) {
                            if (block != Blocks.AIR) {
                                if (!getWorld().isClient) {
                                    getWorld().breakBlock(pos, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public EntityData initialize(@NotNull ServerWorldAccess worldIn, @NotNull LocalDifficulty difficultyIn, @NotNull SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRandom().nextInt(7));
        boolean ancient = this.getRandom().nextInt(16) == 1;
        if (ancient) {
            this.setAncient(true);
            this.setSeaSerpentScale(6.0F + this.getRandom().nextFloat() * 3.0F);

        } else {
            this.setSeaSerpentScale(1.5F + this.getRandom().nextFloat() * 4.0F);
        }
        this.updateAttributes();
        return spawnDataIn;
    }

    public void onWorldSpawn(Random random) {
        this.setVariant(random.nextInt(7));
        boolean ancient = random.nextInt(15) == 1;
        if (ancient) {
            this.setAncient(true);
            this.setSeaSerpentScale(6.0F + random.nextFloat() * 3.0F);

        } else {
            this.setSeaSerpentScale(1.5F + random.nextFloat() * 4.0F);
        }
        this.updateAttributes();
    }

    @Override
    public PassiveEntity createChild(@NotNull ServerWorld serverWorld, @NotNull PassiveEntity ageable) {
        return null;
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
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BITE, ANIMATION_ROAR, ANIMATION_SPEAK};
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.SEA_SERPENT_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return IafSoundRegistry.SEA_SERPENT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.SEA_SERPENT_DIE;
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == this.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playAmbientSound();
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource source) {
        if (this.getAnimation() == this.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playHurtSound(source);
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    public boolean isBlinking() {
        return this.age % 50 > 43;
    }

    private void shoot(LivingEntity entity) {
        if (!this.attackDecision) {
            if (!this.isTouchingWater()) {
                this.setBreathing(false);
                this.attackDecision = true;
            }
            if (this.isBreathing()) {
                if (this.age % 40 == 0) {
                    this.playSound(IafSoundRegistry.SEA_SERPENT_BREATH, 4, 1);
                }
                if (this.age % 10 == 0) {
                    setYaw(bodyYaw);
                    float f1 = 0;
                    float f2 = 0;
                    float f3 = 0;
                    float headPosX = f1 + (float) (this.segments[0].getX() + 1.3F * getSeaSerpentScale() * MathHelper.cos((float) ((getYaw() + 90) * Math.PI / 180)));
                    float headPosZ = f2 + (float) (this.segments[0].getZ() + 1.3F * getSeaSerpentScale() * MathHelper.sin((float) ((getYaw() + 90) * Math.PI / 180)));
                    float headPosY = f3 + (float) (this.segments[0].getY() + 0.2F * getSeaSerpentScale());
                    double d2 = entity.getX() - headPosX;
                    double d3 = entity.getY() - headPosY;
                    double d4 = entity.getZ() - headPosZ;
                    float inaccuracy = 1.0F;
                    d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    EntitySeaSerpentBubbles entitylargefireball = new EntitySeaSerpentBubbles(
                        IafEntityRegistry.SEA_SERPENT_BUBBLES.get(), getWorld(), this, d2, d3, d4);
                    entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
                    if (!getWorld().isClient) {
                        getWorld().spawnEntity(entitylargefireball);
                    }
                    if (!entity.isAlive() || entity == null) {
                        this.setBreathing(false);
                        this.attackDecision = this.getRandom().nextBoolean();
                    }
                }
            } else {
                this.setBreathing(true);
            }
        }
        this.lookAtEntity(entity, 360, 360);
    }

    public EnumSeaSerpent getEnum() {
        switch (this.getVariant()) {
            default:
                return EnumSeaSerpent.BLUE;
            case 1:
                return EnumSeaSerpent.BRONZE;
            case 2:
                return EnumSeaSerpent.DEEPBLUE;
            case 3:
                return EnumSeaSerpent.GREEN;
            case 4:
                return EnumSeaSerpent.PURPLE;
            case 5:
                return EnumSeaSerpent.RED;
            case 6:
                return EnumSeaSerpent.TEAL;
        }
    }

    @Override
    public void travel(@NotNull Vec3d vec) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), vec);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9D));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(vec);
        }
    }

    @Override
    public boolean onKilledOther(@NotNull ServerWorld world, @NotNull LivingEntity entity) {
        this.attackDecision = this.getRandom().nextBoolean();
        return attackDecision;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public int getSafeFallDistance() {
        return 1000;
    }

    public void onJumpHit(LivingEntity target) {
    }

    public boolean shouldUseJumpAttack(LivingEntity attackTarget) {
        return !attackTarget.isTouchingWater() || isPreyAtSurface();
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        DamageSources damageSources = this.getWorld().getDamageSources();
        return source == damageSources.fall() || source == damageSources.drown() || source == damageSources.inWall()
                || (source.getAttacker() != null && source == damageSources.fallingBlock(source.getAttacker()))
                || source == damageSources.lava() || source.isOf(DamageTypes.IN_FIRE) || super.isInvulnerableTo(source);
    }

    public class SwimmingMoveHelper extends MoveControl {
        private final EntitySeaSerpent dolphin;

        public SwimmingMoveHelper(EntitySeaSerpent dolphinIn) {
            super(dolphinIn);
            this.dolphin = dolphinIn;
        }

        @Override
        public void tick() {
            if (this.dolphin.isTouchingWater()) {
                this.dolphin.setVelocity(this.dolphin.getVelocity().add(0.0D, 0.005D, 0.0D));
            }

            if (this.state == State.MOVE_TO && !this.dolphin.getNavigation().isIdle()) {
                double d0 = this.targetX - this.dolphin.getX();
                double d1 = this.targetY - this.dolphin.getY();
                double d2 = this.targetZ - this.dolphin.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < 2.5000003E-7F) {
                    this.entity.setForwardSpeed(0.0F);
                } else {
                    float f = (float) (MathHelper.atan2(d2, d0) * (180F / (float) Math.PI)) - 90.0F;
                    this.dolphin.setYaw(this.wrapDegrees(this.dolphin.getYaw(), f, 10.0F));
                    this.dolphin.bodyYaw = this.dolphin.getYaw();
                    this.dolphin.headYaw = this.dolphin.getYaw();
                    float f1 = (float) (this.speed * 3);
                    if (this.dolphin.isTouchingWater()) {
                        this.dolphin.setMovementSpeed(f1 * 0.02F);
                        float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt((float) (d0 * d0 + d2 * d2))) * (180F / (float) Math.PI)));
                        f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                        this.dolphin.setVelocity(this.dolphin.getVelocity().add(0.0D, this.dolphin.getMovementSpeed() * d1 * 0.6D, 0.0D));
                        this.dolphin.setPitch(this.wrapDegrees(this.dolphin.getPitch(), f2, 1.0F));
                        float f3 = MathHelper.cos(this.dolphin.getPitch() * ((float) Math.PI / 180F));
                        float f4 = MathHelper.sin(this.dolphin.getPitch() * ((float) Math.PI / 180F));
                        this.dolphin.forwardSpeed = f3 * f1;
                        this.dolphin.upwardSpeed = -f4 * f1;
                    } else {
                        this.dolphin.setMovementSpeed(f1 * 0.1F);
                    }

                }
            } else {
                this.dolphin.setMovementSpeed(0.0F);
                this.dolphin.setSidewaysSpeed(0.0F);
                this.dolphin.setUpwardSpeed(0.0F);
                this.dolphin.setForwardSpeed(0.0F);
            }
        }
    }
}