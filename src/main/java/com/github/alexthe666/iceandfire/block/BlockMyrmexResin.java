package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockMyrmexResin extends Block {
    private final boolean sticky;

    public BlockMyrmexResin(boolean sticky) {
        super(Settings.create().mapColor(MapColor.LIGHT_BLUE_GRAY).strength(2.5F).sounds(sticky ? BlockSoundGroup.SLIME : BlockSoundGroup.GRAVEL));
        this.sticky = sticky;
    }

    public static String name(boolean sticky, String suffix) {
        return sticky ? "myrmex_resin_sticky_%s".formatted(suffix) : "myrmex_resin_%s".formatted(suffix);
    }

    @Deprecated
    public boolean canEntitySpawn(BlockState state, BlockView worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
        if (this.sticky)
            if (!(entity instanceof EntityMyrmexBase))
                entity.setVelocity(entity.getVelocity().multiply(0.4D, 0.4D, 0.4D));
    }
}
