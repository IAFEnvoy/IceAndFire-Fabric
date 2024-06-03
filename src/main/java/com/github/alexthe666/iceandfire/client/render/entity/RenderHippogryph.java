package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderHippogryph extends MobEntityRenderer<EntityHippogryph, ModelHippogryph> {

    public RenderHippogryph(EntityRendererFactory.Context context) {
        super(context, new ModelHippogryph(), 0.8F);
        this.features.add(new LayerHippogriffSaddle(this));

    }

    @Override
    protected void scale(@NotNull EntityHippogryph entity, MatrixStack matrix, float partialTickTime) {
        matrix.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public Identifier getTexture(EntityHippogryph entity) {
        return entity.isBlinking() ? entity.getEnumVariant().TEXTURE_BLINK : entity.getEnumVariant().TEXTURE;
    }


    private static class LayerHippogriffSaddle extends FeatureRenderer<EntityHippogryph, ModelHippogryph> {
        private final RenderHippogryph renderer;
        private final RenderLayer SADDLE_TEXTURE = RenderLayer.getEntityNoOutline(new Identifier(IceAndFire.MOD_ID, "textures/models/hippogryph/saddle.png"));
        private final RenderLayer BRIDLE = RenderLayer.getEntityNoOutline(new Identifier(IceAndFire.MOD_ID, "textures/models/hippogryph/bridle.png"));
        private final RenderLayer CHEST = RenderLayer.getEntityTranslucent(new Identifier(IceAndFire.MOD_ID, "textures/models/hippogryph/chest.png"));
        private final RenderLayer TEXTURE_DIAMOND = RenderLayer.getEntityNoOutline(new Identifier(IceAndFire.MOD_ID, "textures/models/hippogryph/armor_diamond.png"));
        private final RenderLayer TEXTURE_GOLD = RenderLayer.getEntityNoOutline(new Identifier(IceAndFire.MOD_ID, "textures/models/hippogryph/armor_gold.png"));
        private final RenderLayer TEXTURE_IRON = RenderLayer.getEntityNoOutline(new Identifier(IceAndFire.MOD_ID, "textures/models/hippogryph/armor_iron.png"));


        public LayerHippogriffSaddle(RenderHippogryph renderer) {
            super(renderer);
            this.renderer = renderer;
        }

        @Override
        public void render(@NotNull MatrixStack matrixStackIn, @NotNull VertexConsumerProvider bufferIn, int packedLightIn, EntityHippogryph hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (hippo.getArmor() != 0) {
                RenderLayer type = switch (hippo.getArmor()) {
                    case 1 -> TEXTURE_IRON;
                    case 2 -> TEXTURE_GOLD;
                    case 3 -> TEXTURE_DIAMOND;
                    default -> null;
                };
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(type);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isSaddled()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(SADDLE_TEXTURE);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isSaddled() && hippo.getControllingPassenger() != null) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(BRIDLE);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isChested()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(CHEST);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}