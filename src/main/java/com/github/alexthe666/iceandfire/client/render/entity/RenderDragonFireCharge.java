package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

public class RenderDragonFireCharge extends EntityRenderer<AbstractFireballEntity> {

    public boolean isFire;

    public RenderDragonFireCharge(EntityRendererFactory.Context context, boolean isFire) {
        super(context);
        this.isFire = isFire;
    }

    @Override
    public @NotNull Identifier getTexture(@NotNull AbstractFireballEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public void render(@NotNull AbstractFireballEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn) {
        BlockRenderManager blockrendererdispatcher = MinecraftClient.getInstance().getBlockRenderManager();
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
        matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(isFire ? Blocks.MAGMA_BLOCK.getDefaultState() : IafBlockRegistry.DRAGON_ICE.get().getDefaultState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV);
        matrixStackIn.pop();
    }

}