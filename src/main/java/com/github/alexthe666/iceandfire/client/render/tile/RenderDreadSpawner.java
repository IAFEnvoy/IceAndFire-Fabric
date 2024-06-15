package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSpawner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerLogic;
import org.jetbrains.annotations.NotNull;

public class RenderDreadSpawner<T extends TileEntityDreadSpawner> implements BlockEntityRenderer<T> {

    public RenderDreadSpawner(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(TileEntityDreadSpawner tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5D, 0.0D, 0.5D);
        MobSpawnerLogic abstractspawner = tileEntityIn.getLogic();
        Entity entity = abstractspawner.getRenderedEntity(tileEntityIn.getWorld(), Random.create(), tileEntityIn.getPos());
        if (entity != null) {
            float f = 0.53125F;
            float f1 = Math.max(entity.getWidth(), entity.getHeight());
            if ((double) f1 > 1.0D) {
                f /= f1;
            }

            matrixStackIn.translate(0.0D, 0.4F, 0.0D);
            matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) MathHelper.lerp(partialTicks, abstractspawner.getLastRotation(), abstractspawner.getRotation()) * 10.0F));
            matrixStackIn.translate(0.0D, -0.2F, 0.0D);
            matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0F));
            matrixStackIn.scale(f, f, f);
            MinecraftClient.getInstance().getEntityRenderDispatcher().render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        }

        matrixStackIn.pop();
    }
}