package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.*;
import com.github.alexthe666.iceandfire.message.MessageDeathWormHitbox;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateDeathWormLand;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateDeathWormSand;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public class EntityDeathWorm extends TameableEntity implements ISyncMount, ICustomCollisions, IBlacklistedFromStatues, IAnimatedEntity, IVillagerFear, IAnimalFear, IGroundMount, IHasCustomizableAttributes, ICustomMoveController {

    public static final Identifier TAN_LOOT = new Identifier(IceAndFire.MOD_ID, "entities/deathworm_tan");
    public static final Identifier WHITE_LOOT = new Identifier(IceAndFire.MOD_ID, "entities/deathworm_white");
    public static final Identifier RED_LOOT = new Identifier(IceAndFire.MOD_ID, "entities/deathworm_red");
    public static final Identifier TAN_GIANT_LOOT = new Identifier(IceAndFire.MOD_ID, "entities/deathworm_tan_giant");
    public static final Identifier WHITE_GIANT_LOOT = new Identifier(IceAndFire.MOD_ID, "entities/deathworm_white_giant");
    public static final Identifier RED_GIANT_LOOT = new Identifier(IceAndFire.MOD_ID, "entities/deathworm_red_giant");
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(EntityDeathWorm.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(EntityDeathWorm.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> JUMP_TICKS = DataTracker.registerData(EntityDeathWorm.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> CONTROL_STATE = DataTracker.registerData(EntityDeathWorm.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> WORM_AGE = DataTracker.registerData(EntityDeathWorm.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> HOME = DataTracker.registerData(EntityDeathWorm.class, TrackedDataHandlerRegistry.BLOCK_POS);
    public static Animation ANIMATION_BITE = Animation.create(10);

    public ChainBuffer tail_buffer;
    public float jumpProgress;
    public float prevJumpProgress;
    private int animationTick;
    private boolean willExplode = false;
    private int ticksTillExplosion = 60;
    private Animation currentAnimation;
    private EntityMutlipartPart[] segments = new EntityMutlipartPart[6];
    private boolean isSandNavigator;
    private final float prevScale = 0.0F;
    private final LookControl lookHelper;
    private int growthCounter = 0;
    private PlayerEntity thrower;
    public DeathwormAITargetItems targetItemsGoal;

    public EntityDeathWorm(EntityType<EntityDeathWorm> type, World worldIn) {
        super(type, worldIn);
        setPathfindingPenalty(PathNodeType.OPEN, 2.0f); // FIXME :: Death worms are trying to go upwards -> figure out why (or if this really helps)
        setPathfindingPenalty(PathNodeType.WATER, 4.0f);
        setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0f);
        this.lookHelper = new IAFLookHelper(this);
        this.ignoreCameraFrustum = true;
        if (worldIn.isClient) {
            tail_buffer = new ChainBuffer();
        }
        this.setStepHeight(1F);
        this.switchNavigator(false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new EntityGroundAIRide<>(this));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DeathWormAIAttack(this));
        this.goalSelector.add(3, new DeathWormAIJump(this, 12));
        this.goalSelector.add(4, new DeathWormAIFindSandTarget(this, 10));
        this.goalSelector.add(5, new DeathWormAIGetInSand(this, 1.0D));
        this.goalSelector.add(6, new DeathWormAIWander(this, 1));
        this.targetSelector.add(2, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(3, new AttackWithOwnerGoal(this));
        this.targetSelector.add(4, new RevengeGoal(this));
        this.targetSelector.add(4, targetItemsGoal = new DeathwormAITargetItems<>(this, false, false));
        this.targetSelector.add(5, new DeathWormAITarget<>(this, LivingEntity.class, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity input) {
                if (EntityDeathWorm.this.isTamed()) {
                    return input instanceof HostileEntity;
                } else if (input != null) {
                    if (input.isTouchingWater() || !DragonUtils.isAlive(input) || isOwner(input)) {
                        return false;
                    }

                    if (input instanceof PlayerEntity || input instanceof AnimalEntity) {
                        return true;
                    }

                    return IafConfig.deathWormAttackMonsters;
                }

                return false;
            }
        }));
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, IafConfig.deathWormMaxHealth)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, IafConfig.deathWormAttackStrength)
                //FOLLOW RANGE
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, IafConfig.deathWormTargetSearchLength)
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 3);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(6, IafConfig.deathWormMaxHealth));
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.max(1, IafConfig.deathWormAttackStrength));
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(IafConfig.deathWormTargetSearchLength);
    }

    @Override
    public @NotNull LookControl getLookControl() {
        return this.lookHelper;
    }

    @Override
    public @NotNull SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.getX());
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.getZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        this.getWorld().getBlockState(blockpos.down()).isIn(BlockTags.SAND);
        return this.getWorld().getBlockState(blockpos.down()).isIn(BlockTags.SAND)
                && this.getWorld().getLightLevel(blockpos) > 8;
    }

    public void onUpdateParts() {
        addSegmentsToWorld();
        // FIXME :: Unused
//        if (isSandBelow()) {
//            int i = Mth.floor(this.getX());
//            int j = Mth.floor(this.getY() - 1);
//            int k = Mth.floor(this.getZ());
//            BlockPos blockpos = new BlockPos(i, j, k);
//            BlockState BlockState = this.level.getBlockState(blockpos);
//
//            if (level.isClientSide) {
//                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getPosX()), (int) Math.floor(this.getPosY()), (int) Math.floor(this.getPosZ())) + 0.5F, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
//            }
//        }
    }

    @Override
    public int getXpToDrop() {
        return this.getScaleFactor() > 3 ? 20 : 10;
    }

    public void initSegments(float scale) {
        segments = new EntityMutlipartPart[7];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new EntitySlowPart(this, (-0.8F - (i * 0.8F)) * scale, 0, 0, 0.7F * scale, 0.7F * scale, 1);
            segments[i].copyPositionAndRotation(this);
            segments[i].setParent(this);
        }
    }

    private void addSegmentsToWorld() {
        for (EntityMutlipartPart entity : segments) {
            EntityUtil.updatePart(entity, this);
        }
    }

    private void clearSegments() {
        for (Entity entity : segments) {
            if (entity != null) {
                entity.kill();
                entity.remove(RemovalReason.KILLED);
            }
        }
    }

    public void setExplosive(boolean explosive, PlayerEntity thrower) {
        this.willExplode = true;
        this.ticksTillExplosion = 60;
        this.thrower = thrower;
    }

    @Override
    public boolean tryAttack(@NotNull Entity entityIn) {
        if (this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_BITE);
            this.playSound(this.getScaleFactor() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_ATTACK : IafSoundRegistry.DEATHWORM_ATTACK, 1, 1);
        }
        if (this.getRandom().nextInt(3) == 0 && this.getScaleFactor() > 1 && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            if (!MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, entityIn.getX(), entityIn.getY(), entityIn.getZ()))) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(getWorld(), this, entityIn.getX(), entityIn.getY(), entityIn.getZ(), this.getScaleFactor());
                explosion.collectBlocksAndDamageEntities();
                explosion.affectWorld(true);
            }
        }
        return false;
    }

    @Override
    public void onDeath(@NotNull DamageSource cause) {
        clearSegments();
        super.onDeath(cause);
    }

    @Override
    protected void fall(double y, boolean onGroundIn, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    protected Identifier getLootTableId() {
        switch (this.getVariant()) {
            case 0:
                return this.getScaleFactor() > 3 ? TAN_GIANT_LOOT : TAN_LOOT;
            case 1:
                return this.getScaleFactor() > 3 ? RED_GIANT_LOOT : RED_LOOT;
            case 2:
                return this.getScaleFactor() > 3 ? WHITE_GIANT_LOOT : WHITE_LOOT;
        }
        return null;
    }

    @Override
    public PassiveEntity createChild(@NotNull ServerWorld serverWorld, @NotNull PassiveEntity ageable) {
        return null;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(SCALE, 1F);
        this.dataTracker.startTracking(CONTROL_STATE, (byte) 0);
        this.dataTracker.startTracking(WORM_AGE, 10);
        this.dataTracker.startTracking(HOME, BlockPos.ORIGIN);
        this.dataTracker.startTracking(JUMP_TICKS, 0);
    }

    @Override
    public void writeCustomDataToNbt(@NotNull NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("GrowthCounter", this.growthCounter);
        compound.putFloat("Scale", this.getDeathwormScale());
        compound.putInt("WormAge", this.getWormAge());
        compound.putLong("WormHome", this.getWormHome().asLong());
        compound.putBoolean("WillExplode", this.willExplode);
    }

    @Override
    public void readCustomDataFromNbt(@NotNull NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getInt("Variant"));
        this.growthCounter = compound.getInt("GrowthCounter");
        this.setDeathWormScale(compound.getFloat("Scale"));
        this.setWormAge(compound.getInt("WormAge"));
        this.setWormHome(BlockPos.fromLong(compound.getLong("WormHome")));
        this.willExplode = compound.getBoolean("WillExplode");
        this.setConfigurableAttributes();
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = dataTracker.get(CONTROL_STATE).byteValue();
        if (newState) {
            dataTracker.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            dataTracker.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    @Override
    public byte getControlState() {
        return dataTracker.get(CONTROL_STATE);
    }

    @Override
    public void setControlState(byte state) {
        dataTracker.set(CONTROL_STATE, state);
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public int getWormJumping() {
        return this.dataTracker.get(JUMP_TICKS);
    }

    public void setWormJumping(int jump) {
        this.dataTracker.set(JUMP_TICKS, jump);
    }

    public BlockPos getWormHome() {
        return this.dataTracker.get(HOME);
    }

    public void setWormHome(BlockPos home) {
        if (home instanceof BlockPos) {
            this.dataTracker.set(HOME, home);
        }
    }

    public int getWormAge() {
        return Math.max(1, dataTracker.get(WORM_AGE).intValue());
    }

    public void setWormAge(int age) {
        this.dataTracker.set(WORM_AGE, age);
    }

    @Override
    public float getScaleFactor() {
        return Math.min(this.getDeathwormScale() * (this.getWormAge() / 5F), 7F);
    }

    public float getDeathwormScale() {
        return this.dataTracker.get(SCALE).floatValue();
    }

    public void setDeathWormScale(float scale) {
        this.dataTracker.set(SCALE, scale);
        this.updateAttributes();
        clearSegments();
        if (!this.getWorld().isClient) {
            initSegments(scale * (this.getWormAge() / 5F));
            IceAndFire.sendMSGToAll(new MessageDeathWormHitbox(this.getId(), scale * (this.getWormAge() / 5F)));
        }
    }

    @Override
    public EntityData initialize(@NotNull ServerWorldAccess worldIn, @NotNull LocalDifficulty difficultyIn, @NotNull SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRandom().nextInt(3));
        float size = 0.25F + (float) (Math.random() * 0.35F);
        this.setDeathWormScale(this.getRandom().nextInt(20) == 0 ? size * 4 : size);
        return spawnDataIn;
    }

    @Override
    public void updatePassengerPosition(@NotNull Entity passenger, @NotNull PositionUpdater callback) {
        super.updatePassengerPosition(passenger, callback);
        if (this.hasPassenger(passenger)) {
            this.setBodyYaw(passenger.getYaw());
            float radius = -0.5F * this.getScaleFactor();
            float angle = (0.01745329251F * this.bodyYaw);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getX() + extraX, this.getY() + this.getStandingEyeHeight() - 0.55F, this.getZ() + extraZ);
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengerList()) {
            if (passenger instanceof PlayerEntity player) {
                return player;
            }
        }
        return null;
    }

    @Override
    public @NotNull ActionResult interactMob(PlayerEntity player, @NotNull Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        if (this.getWormAge() > 4 && player.getVehicle() == null && player.getMainHandStack().getItem() == Items.FISHING_ROD && player.getOffHandStack().getItem() == Items.FISHING_ROD && !this.getWorld().isClient) {
            player.startRiding(this);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    private void switchNavigator(boolean inSand) {
        if (inSand) {
            this.moveControl = new SandMoveHelper();
            this.navigation = new PathNavigateDeathWormSand(this, getWorld());
            this.isSandNavigator = true;
        } else {
            this.moveControl = new MoveControl(this);
            this.navigation = new PathNavigateDeathWormLand(this, getWorld());
            this.isSandNavigator = false;
        }
    }

    @Override
    public boolean damage(@NotNull DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_WALL) || source.isOf(DamageTypes.FALLING_BLOCK)) {
            return false;
        }
        if (this.hasPassengers() && source.getAttacker() != null && this.getControllingPassenger() != null && source.getAttacker() == this.getControllingPassenger()) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public void move(@NotNull MovementType typeIn, @NotNull Vec3d pos) {
        super.move(typeIn, pos);
    }

    @Override
    public @NotNull Vec3d adjustMovementForCollisions(@NotNull Vec3d vec) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec);
    }

    @Override
    public boolean isInsideWall() {
        if (this.isInSand()) {
            return false;
        } else {
            return super.isInsideWall();
        }
    }


    @Override
    protected void pushOutOfBlocks(double x, double y, double z) {
        PositionImpl blockpos = new PositionImpl(x, y, z);
        Vec3i vec3i = new Vec3i((int) Math.round(blockpos.getX()), (int) Math.round(blockpos.getY()), (int) Math.round(blockpos.getZ()));
        Vec3d vector3d = new Vec3d(x - blockpos.getX(), y - blockpos.getY(), z - blockpos.getZ());
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        Direction direction = Direction.UP;
        double d0 = Double.MAX_VALUE;

        for (Direction direction1 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            blockpos$mutable.set(vec3i, direction1);
            if (!this.getWorld().getBlockState(blockpos$mutable).isFullCube(this.getWorld(), blockpos$mutable)
                    || getWorld().getBlockState(blockpos$mutable).isIn(BlockTags.SAND)) {
                double d1 = vector3d.getComponentAlongAxis(direction1.getAxis());
                double d2 = direction1.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
                if (d2 < d0) {
                    d0 = d2;
                    direction = direction1;
                }
            }
        }

        float f = this.random.nextFloat() * 0.2F + 0.1F;
        float f1 = (float) direction.getDirection().offset();
        Vec3d vector3d1 = this.getVelocity().multiply(0.75D);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setVelocity(f1 * f, vector3d1.y, vector3d1.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setVelocity(vector3d1.x, f1 * f, vector3d1.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setVelocity(vector3d1.x, vector3d1.y, f1 * f);
        }
    }

    private void updateAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(Math.min(0.2D, 0.15D * this.getScaleFactor()));
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.max(1, IafConfig.deathWormAttackStrength * this.getScaleFactor()));
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(6, IafConfig.deathWormMaxHealth * this.getScaleFactor()));
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(IafConfig.deathWormTargetSearchLength);
        this.setHealth((float) this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue());
    }

    @Override
    public boolean onKilledOther(@NotNull ServerWorld world, @NotNull LivingEntity entity) {
        if (this.isTamed()) {
            this.heal(14);
            return false;
        }
        return true;
    }

    @Override
    public boolean isTeammate(@NotNull Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TameableEntity) {
                return ((TameableEntity) entityIn).isOwner(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isTeammate(entityIn);
            }
        }

        return super.isTeammate(entityIn);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        prevJumpProgress = jumpProgress;
        if (this.getWormJumping() > 0 && jumpProgress < 5F) {
            jumpProgress++;
        }
        if (this.getWormJumping() == 0 && jumpProgress > 0F) {
            jumpProgress--;
        }
        if (this.isInSand() && this.horizontalCollision) {
            this.setVelocity(this.getVelocity().add(0, 0.05, 0));
        }
        if (this.getWormJumping() > 0) {
            float f2 = (float) -((float) this.getVelocity().y * (double) (180F / (float) Math.PI));
            this.setPitch(f2);
            if (this.isInSand() || this.isOnGround()) {
                this.setWormJumping(this.getWormJumping() - 1);
            }
        }
        if (getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity) {
            this.setTarget(null);
        }
        if (this.getTarget() != null && (!this.getTarget().isAlive() || !DragonUtils.isAlive(this.getTarget()))) {
            this.setTarget(null);
        }
        if (this.willExplode) {
            if (this.ticksTillExplosion == 0) {
                boolean b = !MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, this.getX(), this.getY(), this.getZ()));
                if (b) {
                    getWorld().createExplosion(this.thrower, this.getX(), this.getY(), this.getZ(), 2.5F * this.getScaleFactor(), false, World.ExplosionSourceType.MOB);
                }
                this.thrower = null;
            } else {
                this.ticksTillExplosion--;
            }
        }
        if (this.age == 1) {
            initSegments(this.getScaleFactor());
        }
        if (isInSandStrict()) {
            this.setVelocity(this.getVelocity().add(0, 0.08D, 0));
        }
        if (growthCounter > 1000 && this.getWormAge() < 5) {
            growthCounter = 0;
            this.setWormAge(Math.min(5, this.getWormAge() + 1));
            this.clearSegments();
            this.heal(15);
            this.setDeathWormScale(this.getDeathwormScale());
            if (getWorld().isClient) {
                for (int i = 0; i < 10 * this.getScaleFactor(); i++) {
                    this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getX()), (int) Math.floor(this.getY()), (int) Math.floor(this.getZ())) + 0.5F, this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                    /*
                    for (int j = 0; j < segments.length; j++) {
                        this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, segments[j].getPosX() + (double) (this.rand.nextFloat() * segments[j].getWidth() * 2.0F) - (double) segments[j].getWidth(), this.getSurface((int) Math.floor(segments[j].getPosX()), (int) Math.floor(segments[j].getPosY()), (int) Math.floor(segments[j].getPosZ())) + 0.5F, segments[j].getPosZ() + (double) (this.rand.nextFloat() * segments[j].getWidth() * 2.0F) - (double) segments[j].getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
                    }
                     */
                }
            }
        }
        if (this.getWormAge() < 5) {
            growthCounter++;
        }
        if (this.getControllingPassenger() != null && this.getTarget() != null) {
            this.getNavigation().stop();
            this.setTarget(null);
        }
        //this.faceEntity(this.getAttackTarget(), 10.0F, 10.0F);
           /* if (dist >= 4.0D * getRenderScale() && dist <= 16.0D * getRenderScale() && (this.isInSand() || this.onGround)) {
                this.setWormJumping(true);
                double d0 = this.getAttackTarget().getPosX() - this.getPosX();
                double d1 = this.getAttackTarget().getPosZ() - this.getPosZ();
                float leap = MathHelper.sqrt(d0 * d0 + d1 * d1);
                if ((double) leap >= 1.0E-4D) {
                    this.setMotion(this.getMotion().add(d0 / (double) leap * 0.5D, 0.15F, d1 / (double) leap * 0.5D));
                }
                this.setAnimation(ANIMATION_BITE);
            }*/
        if (this.getTarget() != null && this.distanceTo(this.getTarget()) < Math.min(4, 4D * getScaleFactor()) && this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5) {
            float f = (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
            this.getTarget().damage(this.getWorld().getDamageSources().mobAttack(this), f);
            this.setVelocity(this.getVelocity().add(0, -0.4F, 0));
        }

    }

    public int getWormBrightness(boolean sky) {
        Vec3d vec3 = this.getCameraPosVec(1.0F);
        BlockPos eyePos = BlockPos.ofFloored(vec3);
        while (eyePos.getY() < 256 && !getWorld().isAir(eyePos)) {
            eyePos = eyePos.up();
        }
        int light = this.getWorld().getLightLevel(sky ? LightType.SKY : LightType.BLOCK, eyePos.up());
        return light;
    }

    public int getSurface(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        while (!getWorld().isAir(pos)) {
            pos = pos.up();
        }
        return pos.getY();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getScaleFactor() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_IDLE : IafSoundRegistry.DEATHWORM_IDLE;
    }


    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return this.getScaleFactor() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_HURT : IafSoundRegistry.DEATHWORM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.getScaleFactor() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_DIE : IafSoundRegistry.DEATHWORM_DIE;
    }

    @Override
    public void tick() {
        super.tick();
        calculateDimensions();
        onUpdateParts();
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), 3);
            if (this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_BITE);
                this.playSound(this.getScaleFactor() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_ATTACK : IafSoundRegistry.DEATHWORM_ATTACK, 1, 1);
                if (this.getRandom().nextInt(3) == 0 && this.getScaleFactor() > 1) {
                    float radius = 1.5F * this.getScaleFactor();
                    float angle = (0.01745329251F * this.bodyYaw);
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    BlockLaunchExplosion explosion = new BlockLaunchExplosion(getWorld(), this, this.getX() + extraX, this.getY() - this.getStandingEyeHeight(), this.getZ() + extraZ, this.getScaleFactor() * 0.75F);
                    explosion.collectBlocksAndDamageEntities();
                    explosion.affectWorld(true);
                }
            }
            if (target != null) {
                target.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.isInSand()) {
            BlockPos pos = new BlockPos(this.getBlockX(), this.getSurface(this.getBlockX(), this.getBlockY(), this.getBlockZ()), this.getBlockZ()).down();
            BlockState state = getWorld().getBlockState(pos);
            if (state.isOpaqueFullCube(getWorld(), pos)) {
                if (getWorld().isClient) {
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getX()), (int) Math.floor(this.getY()), (int) Math.floor(this.getZ())) + 0.5F, this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            if (this.age % 10 == 0) {
                this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1, 0.5F);
            }
        }
        if (this.up() && this.isOnGround()) {
            this.jump();
        }
        boolean inSand = isInSand() || this.getControllingPassenger() == null;
        if (inSand && !this.isSandNavigator) {
            switchNavigator(true);
        }
        if (!inSand && this.isSandNavigator) {
            switchNavigator(false);
        }
        if (getWorld().isClient) {
            tail_buffer.calculateChainSwingBuffer(90, 20, 5F, this);
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean up() {
        return (dataTracker.get(CONTROL_STATE).byteValue() & 1) == 1;
    }

    public boolean dismountIAF() {
        return (dataTracker.get(CONTROL_STATE).byteValue() >> 1 & 1) == 1;
    }

    public boolean attack() {
        return (dataTracker.get(CONTROL_STATE).byteValue() >> 2 & 1) == 1;
    }

    @Override
    public void up(boolean up) {
        setStateField(0, up);
    }

    @Override
    public void down(boolean down) {

    }

    @Override
    public void dismount(boolean dismount) {
        setStateField(1, dismount);
    }

    @Override
    public void attack(boolean attack) {
        setStateField(2, attack);
    }

    @Override
    public void strike(boolean strike) {

    }

    public boolean isSandBelow() {
        int i = MathHelper.floor(this.getX());
        int j = MathHelper.floor(this.getY() + 1);
        int k = MathHelper.floor(this.getZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState BlockState = this.getWorld().getBlockState(blockpos);
        return BlockState.isIn(BlockTags.SAND);
    }

    public boolean isInSand() {
        return this.getControllingPassenger() == null && isInSandStrict();
    }

    public boolean isInSandStrict() {
        return getWorld().getBlockState(getBlockPos()).isIn(BlockTags.SAND);
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
        return new Animation[]{ANIMATION_BITE};
    }

    public Entity[] getWormParts() {
        return segments;
    }

    @Override
    public int getMaxHeadRotation() {
        return 10;
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    public boolean canPassThrough(BlockPos pos, BlockState state, VoxelShape shape) {
        return getWorld().getBlockState(pos).isIn(BlockTags.SAND);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public boolean isRidingPlayer(PlayerEntity player) {
        return getRidingPlayer() != null && player != null && getRidingPlayer().getUuid().equals(player.getUuid());
    }

    @Override
    public PlayerEntity getRidingPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity) {
            return (PlayerEntity) this.getControllingPassenger();
        }
        return null;
    }

    @Override
    public double getRideSpeedModifier() {
        return isInSand() ? 1.5F : 1F;
    }

    public double processRiderY(double y) {
        return this.isInSand() ? y + 0.2F : y;
    }

    public class SandMoveHelper extends MoveControl {
        private final EntityDeathWorm worm = EntityDeathWorm.this;

        public SandMoveHelper() {
            super(EntityDeathWorm.this);
        }

        @Override
        public void tick() {
            if (this.state == State.MOVE_TO) {
                double d1 = this.targetY - this.worm.getY();
                double d2 = this.targetZ - this.worm.getZ();
                Vec3d Vector3d = new Vec3d(this.targetX - worm.getX(), this.targetY - worm.getY(), this.targetZ - worm.getZ());
                double d0 = Vector3d.length();
                if (d0 < (double) 2.5000003E-7F) {
                    this.entity.setForwardSpeed(0.0F);
                } else {
                    this.speed = 1.0F;
                    worm.setVelocity(worm.getVelocity().add(Vector3d.multiply(this.speed * 0.05D / d0)));
                    Vec3d Vector3d1 = worm.getVelocity();
                    worm.setYaw(-((float) MathHelper.atan2(Vector3d1.x, Vector3d1.z)) * (180F / (float) Math.PI));
                }

            }
        }
    }
}