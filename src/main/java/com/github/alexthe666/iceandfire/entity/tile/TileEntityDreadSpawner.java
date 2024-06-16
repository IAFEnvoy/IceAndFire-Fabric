package com.github.alexthe666.iceandfire.entity.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class TileEntityDreadSpawner extends MobSpawnerBlockEntity {
    private final BlockEntityType<?> type;
    private final DreadSpawnerBaseLogic spawner = new DreadSpawnerBaseLogic() {
        @Override
        public void sendStatus(World world, BlockPos pos, int status) {
            world.addSyncedBlockEvent(pos, Blocks.SPAWNER, status, 0);
        }

        @Override
        public void setSpawnEntry(World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
            super.setSpawnEntry(world, pos, spawnEntry);
            if (world != null) {
                BlockState blockstate = world.getBlockState(pos);
                world.updateListeners(pos, blockstate, blockstate, 4);
            }

        }
    };

    public TileEntityDreadSpawner(BlockPos pos, BlockState state) {
        super(pos, state);
        this.type = IafTileEntityRegistry.DREAD_SPAWNER;
    }

    @Override
    public void readNbt(NbtCompound p_155760_) {
        super.readNbt(p_155760_);
        this.spawner.readNbt(this.world, this.pos, p_155760_);
    }

    public NbtCompound save(NbtCompound p_59795_) {
        super.writeNbt(p_59795_);
        this.spawner.writeNbt(p_59795_);
        return p_59795_;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound compoundtag = this.save(new NbtCompound());
        compoundtag.remove("SpawnPotentials");
        return compoundtag;
    }

    @Override
    public boolean onSyncedBlockEvent(int p_59797_, int p_59798_) {
        return this.spawner.handleStatus(this.world, p_59797_) || super.onSyncedBlockEvent(p_59797_, p_59798_);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    @Override
    public MobSpawnerLogic getLogic() {
        return this.spawner;
    }

    @Override
    public BlockEntityType<?> getType() {
        return this.type != null ? this.type : super.getType();
    }

}