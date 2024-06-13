package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.inventory.ContainerDragonForge;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class GuiDragonForge extends HandledScreen<ContainerDragonForge> {
    private static final Identifier TEXTURE_FIRE = new Identifier(IceAndFire.MOD_ID, "textures/gui/dragonforge_fire.png");
    private static final Identifier TEXTURE_ICE = new Identifier(IceAndFire.MOD_ID, "textures/gui/dragonforge_ice.png");
    private static final Identifier TEXTURE_LIGHTNING = new Identifier(IceAndFire.MOD_ID, "textures/gui/dragonforge_lightning.png");
    private final ContainerDragonForge tileFurnace;

    public GuiDragonForge(ContainerDragonForge container, PlayerInventory inv, Text name) {
        super(container, inv, name);
        this.tileFurnace = container;
    }

    @Override
    protected void drawForeground(DrawContext pGuiGraphics, int mouseX, int mouseY) {
        assert this.client != null;
        TextRenderer font = this.client.textRenderer;
        if (this.tileFurnace != null) {
            String s = I18n.translate("block.iceandfire.dragonforge_" + DragonType.getNameFromInt(this.tileFurnace.getFireType()) + "_core");
            pGuiGraphics.drawText(this.textRenderer, s, this.backgroundWidth / 2 - font.getWidth(s) / 2, 6, 4210752, false);
        }
        pGuiGraphics.drawText(this.textRenderer, this.playerInventoryTitle, 8, this.backgroundHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void drawBackground(DrawContext pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Identifier texture = switch (this.tileFurnace.getFireType()) {
            case 1 -> TEXTURE_ICE;
            case 2 -> TEXTURE_LIGHTNING;
            default -> TEXTURE_FIRE;
        };

        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        pGuiGraphics.drawTexture(texture, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int i1 = this.getCookTime(this.handler.getCookTime());
        pGuiGraphics.drawTexture(texture, k + 12, l + 23, 0, 166, i1, 38);
    }

    private int getCookTime(int p_175381_1_) {
        BlockEntity te = IceAndFire.PROXY.getRefrencedTE();
        int j = 0;

        assert this.client != null;
        assert this.client.world != null;
        List<DragonForgeRecipe> recipes = this.client.world.getRecipeManager()
                .listAllOfType(IafRecipeRegistry.DRAGON_FORGE_TYPE)
                .stream().filter(item ->
                        item.isValidInput(this.tileFurnace.getSlot(0).getStack()) && item.isValidBlood(this.tileFurnace.getSlot(1).getStack())).toList();
        int maxCookTime = recipes.isEmpty() ? 100 : recipes.get(0).getCookTime();
        if (te instanceof TileEntityDragonforge) {
            j = Math.min(((TileEntityDragonforge) te).cookTime, maxCookTime);
        }
        return j != 0 ? j * p_175381_1_ / maxCookTime : 0;
    }

    @Override
    public void render(DrawContext pGuiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(pGuiGraphics, mouseX, mouseY);
    }

}