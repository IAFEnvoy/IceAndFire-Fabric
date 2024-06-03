package com.github.alexthe666.iceandfire.pathfinding;

import net.minecraft.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.NotNull;

public class NodeProcessorFly extends BirdPathNodeMaker {

    @Override
    public void init(@NotNull ChunkCache p_225578_1_, @NotNull MobEntity p_225578_2_) {
        super.init(p_225578_1_, p_225578_2_);
    }

    public void setEntitySize(float width, float height) {
        this.entityBlockXSize = MathHelper.floor(width + 1.0F);
        this.entityBlockYSize = MathHelper.floor(height + 1.0F);
        this.entityBlockZSize = MathHelper.floor(width + 1.0F);
    }
}