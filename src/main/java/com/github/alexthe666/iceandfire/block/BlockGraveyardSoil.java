package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockGraveyardSoil extends Block {

    public BlockGraveyardSoil() {
        super(
            Settings
                .create()
                .mapColor(MapColor.DIRT_BROWN)
                .sounds(BlockSoundGroup.GRAVEL)
                .strength(5, 1F)
                .ticksRandomly()
		);
    }


    @Override
    public void scheduledTick(@NotNull BlockState state, ServerWorld worldIn, @NotNull BlockPos pos, @NotNull Random rand) {
        if (!worldIn.isClient) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (!worldIn.isDay() && !worldIn.getBlockState(pos.up()).isOpaque() && rand.nextInt(9) == 0 && worldIn.getDifficulty() != Difficulty.PEACEFUL) {
                int checkRange = 32;
                int k = worldIn.getNonSpectatingEntities(EntityGhost.class, (new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).expand(checkRange)).size();
                if (k < 10) {
                    EntityGhost ghost = IafEntityRegistry.GHOST.get().create(worldIn);
                    ghost.updatePositionAndAngles(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        ThreadLocalRandom.current().nextFloat() * 360F, 0);
                    if (!worldIn.isClient) {
                        ghost.initialize(worldIn, worldIn.getLocalDifficulty(pos), SpawnReason.SPAWNER, null, null);
                        worldIn.spawnEntity(ghost);
                    }
                    ghost.setAnimation(EntityGhost.ANIMATION_SCARE);
                    ghost.setPositionTarget(pos, 16);
                }
            }
        }
    }
}