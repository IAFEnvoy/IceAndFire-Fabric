package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemBestiary;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class ContainerLectern extends ScreenHandler {
    private final Inventory tileFurnace;
    private final int[] possiblePagesInt = new int[3];
    private final PropertyDelegate propertyDelegate;

    public ContainerLectern(int i, PlayerInventory playerInventory) {
        this(i, new SimpleInventory(2), playerInventory, new ArrayPropertyDelegate(3));
    }


    public ContainerLectern(int id, Inventory furnaceInventory, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
        super(IafContainerRegistry.IAF_LECTERN_CONTAINER.get(), id);
        this.tileFurnace = furnaceInventory;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        this.addSlot(new SlotLectern(furnaceInventory, 0, 15, 47) {
            @Override
            public boolean canInsert(@NotNull ItemStack stack) {
                return super.canInsert(stack) && !stack.isEmpty() && stack.getItem() instanceof ItemBestiary;
            }
        });
        this.addSlot(new Slot(furnaceInventory, 1, 35, 47) {
            @Override
            public boolean canInsert(@NotNull ItemStack stack) {
                return super.canInsert(stack) && !stack.isEmpty() && stack.getItem() == IafItemRegistry.MANUSCRIPT.get();
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    private int getPageField(int i) {
        return this.propertyDelegate.get(i);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
    }

    public void onUpdate() {
        this.possiblePagesInt[0] = this.getPageField(0);
        this.possiblePagesInt[1] = this.getPageField(1);
        this.possiblePagesInt[2] = this.getPageField(2);
    }

    @Override
    public boolean canUse(@NotNull PlayerEntity playerIn) {
        return this.tileFurnace.canPlayerUse(playerIn);
    }

    @Override
    public @NotNull ItemStack quickMove(@NotNull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.tileFurnace.size()) {
                if (!this.insertItem(itemstack1, this.tileFurnace.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(itemstack1) && !this.getSlot(0).hasStack()) {
                if (!this.insertItem(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(1).canInsert(itemstack1) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.tileFurnace.size() <= 5 || !this.insertItem(itemstack1, 5, this.tileFurnace.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemstack;
    }

    public int getManuscriptAmount() {
        ItemStack itemstack = this.tileFurnace.getStack(1);
        return itemstack.isEmpty() || itemstack.getItem() != IafItemRegistry.MANUSCRIPT.get() ? 0 : itemstack.getCount();
    }

    public EnumBestiaryPages[] getPossiblePages() {
        this.possiblePagesInt[0] = this.getPageField(0);
        this.possiblePagesInt[1] = this.getPageField(1);
        this.possiblePagesInt[2] = this.getPageField(2);
        EnumBestiaryPages[] pages = new EnumBestiaryPages[3];
        if (this.tileFurnace.getStack(0).getItem() == IafItemRegistry.BESTIARY.get()) {
            if (this.possiblePagesInt[0] < 0)
                pages[0] = null;
            else
                pages[0] = EnumBestiaryPages.values()[Math.min(EnumBestiaryPages.values().length, this.possiblePagesInt[0])];
            if (this.possiblePagesInt[1] < 0)
                pages[1] = null;
            else
                pages[1] = EnumBestiaryPages.values()[Math.min(EnumBestiaryPages.values().length, this.possiblePagesInt[1])];
            if (this.possiblePagesInt[2] < 0)
                pages[2] = null;
            else
                pages[2] = EnumBestiaryPages.values()[Math.min(EnumBestiaryPages.values().length, this.possiblePagesInt[2])];
        }
        return pages;
    }

    @Override
    public boolean onButtonClick(PlayerEntity playerIn, int id) {
        this.onUpdate();
        ItemStack bookStack = this.tileFurnace.getStack(0);
        ItemStack manuscriptStack = this.tileFurnace.getStack(1);
        int i = 3;
        if ((manuscriptStack.isEmpty() || manuscriptStack.getCount() < i || manuscriptStack.getItem() != IafItemRegistry.MANUSCRIPT.get())) {
            return false;
        } else if (this.possiblePagesInt[id] > 0 && !bookStack.isEmpty() && bookStack.getItem() == IafItemRegistry.BESTIARY.get()) {
            EnumBestiaryPages page = this.getPossiblePages()[MathHelper.clamp(id, 0, 2)];
            if (page != null) {
                if (!playerIn.getWorld().isClient) {
                    manuscriptStack.decrement(i);
                    if (manuscriptStack.isEmpty())
                        this.tileFurnace.setStack(1, ItemStack.EMPTY);
                    EnumBestiaryPages.addPage(EnumBestiaryPages.fromInt(page.ordinal()), bookStack);
                    if (this.tileFurnace instanceof TileEntityLectern entityLectern)
                        entityLectern.randomizePages(bookStack, manuscriptStack);
                }
                this.tileFurnace.setStack(0, bookStack);
                this.tileFurnace.markDirty();
                this.onContentChanged(this.tileFurnace);
                playerIn.getWorld().playSound(null, playerIn.getBlockPos(), IafSoundRegistry.BESTIARY_PAGE, SoundCategory.BLOCKS, 1.0F, playerIn.getWorld().random.nextFloat() * 0.1F + 0.9F);
            }
            this.onUpdate();
            return true;
        } else
            return false;
    }
}