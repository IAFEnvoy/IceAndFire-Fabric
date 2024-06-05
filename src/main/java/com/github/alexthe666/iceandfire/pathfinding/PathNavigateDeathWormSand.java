package com.github.alexthe666.iceandfire.pathfinding;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class PathNavigateDeathWormSand extends SwimNavigation {

    public PathNavigateDeathWormSand(EntityDeathWorm deathworm, World worldIn) {
        super(deathworm, worldIn);
    }

    @Override
    public boolean canSwim() {
        return this.nodeMaker.canSwim();
    }

    @Override
    protected @NotNull PathNodeNavigator createPathNodeNavigator(int i) {
        this.nodeMaker = new NodeProcessorDeathWorm();
        this.nodeMaker.setCanEnterOpenDoors(true);
        this.nodeMaker.setCanSwim(true);
        return new PathNodeNavigator(this.nodeMaker, i);
    }

    @Override
    protected boolean isAtValidPosition() {
        return true;
    }

    @Override
    protected @NotNull Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.entity.getY() + 0.5D, this.entity.getZ());
    }

    @Override
    protected boolean canPathDirectlyThrough(@NotNull final Vec3d start, @NotNull final Vec3d end) {
        HitResult raytraceresult = this.world.raycast(new CustomRayTraceContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this.entity));

        if (raytraceresult.getType() == HitResult.Type.BLOCK) {
            Vec3d vec3i = raytraceresult.getPos();
            return this.entity.getWorld().getBlockState(BlockPos.ofFloored(vec3i)).isIn(BlockTags.SAND);
        }

        return raytraceresult.getType() == HitResult.Type.MISS;
    }

    @Override
    public boolean isValidPosition(@NotNull BlockPos pos) {
        return this.world.getBlockState(pos).isOpaque();
    }

    public static class CustomRayTraceContext extends RaycastContext {

        private final ShapeType blockMode;
        private final ShapeContext context;

        public CustomRayTraceContext(Vec3d startVecIn, Vec3d endVecIn, ShapeType blockModeIn, FluidHandling fluidModeIn, Entity entityIn) {
            super(startVecIn, endVecIn, blockModeIn, fluidModeIn, entityIn);
            this.blockMode = blockModeIn;
            this.context = entityIn == null ? ShapeContext.absent() : ShapeContext.of(entityIn);
        }

        @Override
        public @NotNull VoxelShape getBlockShape(BlockState blockState, @NotNull BlockView world, @NotNull BlockPos pos) {
            if (blockState.isIn(BlockTags.SAND))
                return VoxelShapes.empty();
            return this.blockMode.get(blockState, world, pos, this.context);
        }
    }
}