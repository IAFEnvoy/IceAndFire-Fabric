package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadHorse;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderDreadHorse extends MobEntityRenderer<EntityDreadHorse, HorseEntityModel<EntityDreadHorse>> {
    public static final Identifier TEXTURE = new Identifier(IceAndFire.MOD_ID,"textures/models/dread/dread_knight_horse.png");
    public static final Identifier TEXTURE_EYES = new Identifier(IceAndFire.MOD_ID,"textures/models/dread/dread_knight_horse_eyes.png");

    public RenderDreadHorse(EntityRendererFactory.Context context) {
        super(context, new HorseEntityModel<>(context.getPart(EntityModelLayers.HORSE)), 0.75F);
        this.addFeature(new LayerGenericGlowing<>(this, TEXTURE_EYES));
    }

    @Override
    public @NotNull Identifier getTexture(@NotNull EntityDreadHorse entity) {
        return TEXTURE;
    }
}