package com.github.alexthe666.iceandfire.datagen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.datagen.tags.IafItemTags;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDragonArmor;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.item.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Generates recipes without advancements
 */
public class IafRecipes extends RecipeProvider {

    public IafRecipes(DataOutput output) {
        super(output);
    }

    @Override
    protected void generate(Consumer<RecipeJsonProvider> consumer) {
        createShaped(consumer);
        createShapeless(consumer);

    }

    private void createShaped(@NotNull final Consumer<RecipeJsonProvider> consumer) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.AMPHITHERE_ARROW.get(), 4)
                .pattern("X")
                .pattern("#")
                .pattern("Y")
                .define('#', Tags.Items.RODS_WOODEN)
                .define('X', Items.FLINT)
                .define('Y', IafItemRegistry.AMPHITHERE_FEATHER.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.AMPHITHERE_FEATHER.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.AMPHITHERE_MACUAHUITL.get())
                .pattern("OXO")
                .pattern("FXF")
                .pattern("OSO")
                .define('X', ItemTags.PLANKS)
                .define('S', Tags.Items.RODS_WOODEN)
                .define('O', Tags.Items.OBSIDIAN)
                .define('F', IafItemRegistry.AMPHITHERE_FEATHER.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.AMPHITHERE_FEATHER.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.CHARCOAL)
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .input('B', IafBlockRegistry.ASH.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.ASH.get()))
                .save(consumer, location("ash_to_charcoal"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.BLINDFOLD.get())
                .pattern("SLS")
                .define('L', Tags.Items.LEATHER)
                .define('S', Tags.Items.STRING)
                .unlockedBy("has_item", conditionsFromItem(Tags.Items.LEATHER))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.CHAIN.get())
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', Items.CHAIN)
                .unlockedBy("has_item", conditionsFromItem(Items.CHAIN))
                .save(consumer);

        // FIXME :: Currently uses `minecraft` namespace
        armorSet(consumer, Items.CHAIN,
                Items.CHAINMAIL_HELMET,
                Items.CHAINMAIL_CHESTPLATE,
                Items.CHAINMAIL_LEGGINGS,
                Items.CHAINMAIL_BOOTS
        );

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.ITEM_COCKATRICE_SCEPTER.get())
                .pattern("S")
                .pattern("E")
                .pattern("W")
                .define('W', IafItemTags.BONES_WITHER)
                .define('S', IafItemRegistry.WITHER_SHARD.get())
                .define('E', IafItemRegistry.COCKATRICE_EYE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.COCKATRICE_EYE.get()))
                .save(consumer);

        armorSet(consumer, Tags.Items.INGOTS_COPPER,
                IafItemRegistry.COPPER_HELMET.get(),
                IafItemRegistry.COPPER_CHESTPLATE.get(),
                IafItemRegistry.COPPER_LEGGINGS.get(),
                IafItemRegistry.COPPER_BOOTS.get()
        );

        toolSet(consumer, Tags.Items.INGOTS_COPPER, Tags.Items.RODS_WOODEN,
                IafItemRegistry.COPPER_SWORD.get(),
                IafItemRegistry.COPPER_PICKAXE.get(),
                IafItemRegistry.COPPER_AXE.get(),
                IafItemRegistry.COPPER_SHOVEL.get(),
                IafItemRegistry.COPPER_HOE.get()
        );

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DEATHWORM_GAUNTLET_RED.get())
                .pattern(" T ")
                .pattern("CHC")
                .pattern("CCC")
                .define('C', IafItemRegistry.DEATH_WORM_CHITIN_RED.get())
                .define('H', IafItemRegistry.CHAIN.get())
                .define('T', IafItemRegistry.DEATHWORM_TOUNGE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DEATHWORM_TOUNGE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DEATHWORM_GAUNTLET_WHITE.get())
                .pattern(" T ")
                .pattern("CHC")
                .pattern("CCC")
                .define('C', IafItemRegistry.DEATH_WORM_CHITIN_WHITE.get())
                .define('H', IafItemRegistry.CHAIN.get())
                .define('T', IafItemRegistry.DEATHWORM_TOUNGE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DEATHWORM_TOUNGE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DEATHWORM_GAUNTLET_YELLOW.get())
                .pattern(" T ")
                .pattern("CHC")
                .pattern("CCC")
                .define('C', IafItemRegistry.DEATH_WORM_CHITIN_YELLOW.get())
                .define('H', IafItemRegistry.CHAIN.get())
                .define('T', IafItemRegistry.DEATHWORM_TOUNGE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DEATHWORM_TOUNGE.get()))
                .save(consumer);

        armorSet(consumer, IafItemRegistry.DEATH_WORM_CHITIN_RED.get(),
                IafItemRegistry.DEATHWORM_RED_HELMET.get(),
                IafItemRegistry.DEATHWORM_RED_CHESTPLATE.get(),
                IafItemRegistry.DEATHWORM_RED_LEGGINGS.get(),
                IafItemRegistry.DEATHWORM_RED_BOOTS.get()
        );

        armorSet(consumer, IafItemRegistry.DEATH_WORM_CHITIN_WHITE.get(),
                IafItemRegistry.DEATHWORM_WHITE_HELMET.get(),
                IafItemRegistry.DEATHWORM_WHITE_CHESTPLATE.get(),
                IafItemRegistry.DEATHWORM_WHITE_LEGGINGS.get(),
                IafItemRegistry.DEATHWORM_WHITE_BOOTS.get()
        );

        armorSet(consumer, IafItemRegistry.DEATH_WORM_CHITIN_YELLOW.get(),
                IafItemRegistry.DEATHWORM_YELLOW_HELMET.get(),
                IafItemRegistry.DEATHWORM_YELLOW_CHESTPLATE.get(),
                IafItemRegistry.DEATHWORM_YELLOW_LEGGINGS.get(),
                IafItemRegistry.DEATHWORM_YELLOW_BOOTS.get()
        );

        dragonArmorSet(consumer, Tags.Items.STORAGE_BLOCKS_COPPER,
                IafItemRegistry.DRAGONARMOR_COPPER_0.get(),
                IafItemRegistry.DRAGONARMOR_COPPER_1.get(),
                IafItemRegistry.DRAGONARMOR_COPPER_2.get(),
                IafItemRegistry.DRAGONARMOR_COPPER_3.get()
        );

        dragonArmorSet(consumer, Tags.Items.STORAGE_BLOCKS_IRON,
                IafItemRegistry.DRAGONARMOR_IRON_0.get(),
                IafItemRegistry.DRAGONARMOR_IRON_1.get(),
                IafItemRegistry.DRAGONARMOR_IRON_2.get(),
                IafItemRegistry.DRAGONARMOR_IRON_3.get()
        );

        dragonArmorSet(consumer, IafItemTags.STORAGE_BLOCKS_SILVER,
                IafItemRegistry.DRAGONARMOR_SILVER_0.get(),
                IafItemRegistry.DRAGONARMOR_SILVER_1.get(),
                IafItemRegistry.DRAGONARMOR_SILVER_2.get(),
                IafItemRegistry.DRAGONARMOR_SILVER_3.get()
        );

        dragonArmorSet(consumer, Tags.Items.STORAGE_BLOCKS_DIAMOND,
                IafItemRegistry.DRAGONARMOR_DIAMOND_0.get(),
                IafItemRegistry.DRAGONARMOR_DIAMOND_1.get(),
                IafItemRegistry.DRAGONARMOR_DIAMOND_2.get(),
                IafItemRegistry.DRAGONARMOR_DIAMOND_3.get()
        );

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT,IafItemRegistry.IRON_HIPPOGRYPH_ARMOR.get())
                .pattern("FDF")
                .define('F', Tags.Items.FEATHERS)
                .define('D', Items.IRON_HORSE_ARMOR)
                .unlockedBy("has_item", conditionsFromItem(Items.IRON_HORSE_ARMOR))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT,IafItemRegistry.GOLD_HIPPOGRYPH_ARMOR.get())
                .pattern("FDF")
                .define('F', Tags.Items.FEATHERS)
                .define('D', Items.GOLDEN_HORSE_ARMOR)
                .unlockedBy("has_item", conditionsFromItem(Items.GOLDEN_HORSE_ARMOR))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT,IafItemRegistry.DIAMOND_HIPPOGRYPH_ARMOR.get())
                .pattern("FDF")
                .define('F', Tags.Items.FEATHERS)
                .define('D', Items.DIAMOND_HORSE_ARMOR)
                .unlockedBy("has_item", conditionsFromItem(Items.DIAMOND_HORSE_ARMOR))
                .save(consumer);

        offerReversibleCompactingRecipes(consumer, RecipeCategory.MISC, IafItemRegistry.DRAGON_BONE.get(), RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DRAGON_BONE_BLOCK.get()
                , locationString("dragon_bone_block"), null
                , locationString("dragonbone"), null);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafBlockRegistry.DRAGON_BONE_BLOCK_WALL.get())
                .pattern("BBB")
                .pattern("BBB")
                .define('B', IafItemRegistry.DRAGON_BONE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DRAGON_BONE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.DRAGON_FLUTE.get())
                .pattern("B  ")
                .pattern(" B ")
                .pattern("  I")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', IafItemRegistry.DRAGON_BONE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DRAGON_BONE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.DRAGON_HORN.get())
                .pattern("  B")
                .pattern(" BB")
                .pattern("IB ")
                .define('I', Tags.Items.RODS_WOODEN)
                .define('B', IafItemRegistry.DRAGON_BONE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DRAGON_BONE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafBlockRegistry.DRAGON_ICE_SPIKES.get(), 4)
                .pattern("I I")
                .pattern("I I")
                .define('I', IafBlockRegistry.DRAGON_ICE.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DRAGON_ICE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafBlockRegistry.NEST.get(), 8)
                .pattern("HHH")
                .pattern("HBH")
                .pattern("HHH")
                .define('H', Blocks.HAY_BLOCK)
                .define('B', IafItemRegistry.DRAGON_BONE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DRAGON_BONE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.DRAGON_STAFF.get())
                .pattern("S")
                .pattern("T")
                .pattern("T")
                .define('T', Tags.Items.RODS_WOODEN)
                .define('S', IafItemTags.DRAGON_SKULLS)
                .unlockedBy("has_item", conditionsFromTag(IafItemTags.DRAGON_SKULLS))
                .save(consumer);

        toolSet(consumer, IafItemRegistry.DRAGON_BONE.get(), IafItemTags.BONES_WITHER,
                IafItemRegistry.DRAGONBONE_SWORD.get(),
                IafItemRegistry.DRAGONBONE_PICKAXE.get(),
                IafItemRegistry.DRAGONBONE_AXE.get(),
                IafItemRegistry.DRAGONBONE_SHOVEL.get(),
                IafItemRegistry.DRAGONBONE_HOE.get()
        );

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DRAGON_BOW.get())
                .pattern(" DS")
                .pattern("W S")
                .pattern(" DS")
                .define('S', Tags.Items.STRING)
                .define('W', IafItemTags.BONES_WITHER)
                .define('D', IafItemRegistry.DRAGON_BONE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DRAGON_BONE.get()))
                .save(consumer);

        forgeBrick(consumer, Items.STONE_BRICKS, IafItemTags.STORAGE_BLOCKS_SCALES_DRAGON_FIRE, IafBlockRegistry.DRAGONFORGE_FIRE_BRICK.get());
        forgeCore(consumer, IafBlockRegistry.DRAGONFORGE_FIRE_BRICK.get(), IafItemRegistry.FIRE_DRAGON_HEART.get(), IafBlockRegistry.DRAGONFORGE_FIRE_CORE_DISABLED.get());
        forgeInput(consumer, IafBlockRegistry.DRAGONFORGE_FIRE_BRICK.get(), Tags.Items.INGOTS_IRON, IafBlockRegistry.DRAGONFORGE_FIRE_INPUT.get());

        forgeBrick(consumer, Items.STONE_BRICKS, IafItemTags.STORAGE_BLOCKS_SCALES_DRAGON_ICE, IafBlockRegistry.DRAGONFORGE_ICE_BRICK.get());
        forgeCore(consumer, IafBlockRegistry.DRAGONFORGE_ICE_BRICK.get(), IafItemRegistry.ICE_DRAGON_HEART.get(), IafBlockRegistry.DRAGONFORGE_ICE_CORE_DISABLED.get());
        forgeInput(consumer, IafBlockRegistry.DRAGONFORGE_ICE_BRICK.get(), Tags.Items.INGOTS_IRON, IafBlockRegistry.DRAGONFORGE_ICE_INPUT.get());

        forgeBrick(consumer, Items.STONE_BRICKS, IafItemTags.STORAGE_BLOCKS_SCALES_DRAGON_LIGHTNING, IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK.get());
        forgeCore(consumer, IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK.get(), IafItemRegistry.LIGHTNING_DRAGON_HEART.get(), IafBlockRegistry.DRAGONFORGE_LIGHTNING_CORE_DISABLED.get());
        forgeInput(consumer, IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK.get(), Tags.Items.INGOTS_IRON, IafBlockRegistry.DRAGONFORGE_LIGHTNING_INPUT.get());

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.DRAGON_MEAL.get())
                .pattern("BMB")
                .pattern("MBM")
                .pattern("BMB")
                .define('B', Tags.Items.BONES)
                .define('M', IafItemTags.DRAGON_FOOD_MEAT)
                .unlockedBy("has_item", conditionsFromTag(IafItemTags.DRAGON_FOOD_MEAT))
                .save(consumer);

        compact(consumer, IafItemRegistry.DRAGONSCALES_RED.get(), IafBlockRegistry.DRAGON_SCALE_RED.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_GREEN.get(), IafBlockRegistry.DRAGON_SCALE_GREEN.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_BRONZE.get(), IafBlockRegistry.DRAGON_SCALE_BRONZE.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_GRAY.get(), IafBlockRegistry.DRAGON_SCALE_GRAY.get());

        compact(consumer, IafItemRegistry.DRAGONSCALES_BLUE.get(), IafBlockRegistry.DRAGON_SCALE_BLUE.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_WHITE.get(), IafBlockRegistry.DRAGON_SCALE_WHITE.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_SAPPHIRE.get(), IafBlockRegistry.DRAGON_SCALE_SAPPHIRE.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_SILVER.get(), IafBlockRegistry.DRAGON_SCALE_SILVER.get());

        compact(consumer, IafItemRegistry.DRAGONSCALES_ELECTRIC.get(), IafBlockRegistry.DRAGON_SCALE_ELECTRIC.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_AMYTHEST.get(), IafBlockRegistry.DRAGON_SCALE_AMYTHEST.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_COPPER.get(), IafBlockRegistry.DRAGON_SCALE_COPPER.get());
        compact(consumer, IafItemRegistry.DRAGONSCALES_BLACK.get(), IafBlockRegistry.DRAGON_SCALE_BLACK.get());

        for (EnumDragonArmor type : EnumDragonArmor.values()) {
            armorSet(consumer, type.armorMaterial.getRepairIngredient(),
                    type.helmet.get(),
                    type.chestplate.get(),
                    type.leggings.get(),
                    type.boots.get()
            );
        }

        for (EnumSeaSerpent type : EnumSeaSerpent.values()) {
            armorSet(consumer, type.scale.get(),
                    type.helmet.get(),
                    type.chestplate.get(),
                    type.leggings.get(),
                    type.boots.get()
            );

            compact(consumer, type.scale.get(), type.scaleBlock.get());
        }

        compact(consumer, IafItemRegistry.DRAGONSTEEL_FIRE_INGOT.get(), IafBlockRegistry.DRAGONSTEEL_FIRE_BLOCK.get());

        toolSet(consumer, IafItemRegistry.DRAGONSTEEL_FIRE_INGOT.get(), IafItemTags.BONES_WITHER,
                IafItemRegistry.DRAGONSTEEL_FIRE_SWORD.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_PICKAXE.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_AXE.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_SHOVEL.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_HOE.get()
        );

        armorSet(consumer, IafItemRegistry.DRAGONSTEEL_FIRE_INGOT.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_HELMET.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_CHESTPLATE.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_LEGGINGS.get(),
                IafItemRegistry.DRAGONSTEEL_FIRE_BOOTS.get()
        );

        dragonArmorSet(consumer, IafBlockRegistry.DRAGONSTEEL_FIRE_BLOCK.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_FIRE_0.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_FIRE_1.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_FIRE_2.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_FIRE_3.get()
        );

        compact(consumer, IafItemRegistry.DRAGONSTEEL_ICE_INGOT.get(), IafBlockRegistry.DRAGONSTEEL_ICE_BLOCK.get());

        toolSet(consumer, IafItemRegistry.DRAGONSTEEL_ICE_INGOT.get(), IafItemTags.BONES_WITHER,
                IafItemRegistry.DRAGONSTEEL_ICE_SWORD.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_PICKAXE.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_AXE.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_SHOVEL.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_HOE.get()
        );

        armorSet(consumer, IafItemRegistry.DRAGONSTEEL_ICE_INGOT.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_HELMET.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_CHESTPLATE.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_LEGGINGS.get(),
                IafItemRegistry.DRAGONSTEEL_ICE_BOOTS.get()
        );

        dragonArmorSet(consumer, IafBlockRegistry.DRAGONSTEEL_ICE_BLOCK.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_ICE_0.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_ICE_1.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_ICE_2.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_ICE_3.get()
        );

        compact(consumer, IafItemRegistry.DRAGONSTEEL_LIGHTNING_INGOT.get(), IafBlockRegistry.DRAGONSTEEL_LIGHTNING_BLOCK.get());

        toolSet(consumer, IafItemRegistry.DRAGONSTEEL_LIGHTNING_INGOT.get(), IafItemTags.BONES_WITHER,
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_PICKAXE.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_AXE.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_SHOVEL.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_HOE.get()
        );

        armorSet(consumer, IafItemRegistry.DRAGONSTEEL_LIGHTNING_INGOT.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_HELMET.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_CHESTPLATE.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_LEGGINGS.get(),
                IafItemRegistry.DRAGONSTEEL_LIGHTNING_BOOTS.get()
        );

        dragonArmorSet(consumer, IafBlockRegistry.DRAGONSTEEL_LIGHTNING_BLOCK.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_LIGHTNING_0.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_LIGHTNING_1.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_LIGHTNING_2.get(),
                IafItemRegistry.DRAGONARMOR_DRAGONSTEEL_LIGHTNING_3.get()
        );

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE.get(), 8)
                .pattern("DDD")
                .pattern("DSD")
                .pattern("DDD")
                .define('S', Tags.Items.STONE)
                .define('D', IafItemRegistry.DREAD_SHARD.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DREAD_SHARD.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE_BRICKS.get(), 4)
                .pattern("DD")
                .pattern("DD")
                .define('D', IafBlockRegistry.DREAD_STONE.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREAD_STONE.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE_BRICKS_CHISELED.get())
                .pattern("D")
                .pattern("D")
                .define('D', IafBlockRegistry.DREAD_STONE_BRICKS_SLAB.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREAD_STONE_BRICKS_SLAB.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE_FACE.get(), 8)
                .pattern("DDD")
                .pattern("DSD")
                .pattern("DDD")
                .define('S', Items.SKELETON_SKULL)
                .define('D', IafBlockRegistry.DREAD_STONE_BRICKS.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREAD_STONE_BRICKS.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE_BRICKS_SLAB.get(), 6)
                .pattern("DDD")
                .define('D', IafBlockRegistry.DREAD_STONE_BRICKS.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREAD_STONE_BRICKS.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE_BRICKS_STAIRS.get(), 4)
                .pattern("D  ")
                .pattern("DD ")
                .pattern("DDD")
                .define('D', IafBlockRegistry.DREAD_STONE_BRICKS.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREAD_STONE_BRICKS.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE_TILE.get(), 8)
                .pattern("DDD")
                .pattern("D D")
                .pattern("DDD")
                .define('D', IafBlockRegistry.DREAD_STONE_BRICKS.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREAD_STONE_BRICKS.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_TORCH.get(), 4)
                .pattern("D")
                .pattern("S")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('D', IafItemRegistry.DREAD_SHARD.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.DREAD_SHARD.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.EARPLUGS.get())
                .pattern("B B")
                .define('B', ItemTags.PLANKS)
                .unlockedBy("has_item", conditionsFromTag(ItemTags.PLANKS))
                .save(consumer);

        for (EnumTroll type : EnumTroll.values()) {
            armorSet(consumer, type.leather.get(),
                    type.chestplate.get(),
                    type.leggings.get(),
                    type.boots.get()
            );

            ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, type.helmet.get())
                    .pattern("TTT")
                    .pattern("U U")
                    .input('T', type.leather.get())
                    .input('U', IafItemRegistry.TROLL_TUSK.get())
                    .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.TROLL_TUSK.get()))
                    .save(consumer);
        }

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafBlockRegistry.GHOST_CHEST.get())
                .pattern(" E ")
                .pattern("ECE")
                .pattern(" E ")
                .define('C', Tags.Items.RODS_WOODEN)
                .define('E', IafItemRegistry.ECTOPLASM.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.ECTOPLASM.get()))
                .save(consumer);

        dragonArmorSet(consumer, Tags.Items.STORAGE_BLOCKS_GOLD,
                IafItemRegistry.DRAGONARMOR_GOLD_0.get(),
                IafItemRegistry.DRAGONARMOR_GOLD_1.get(),
                IafItemRegistry.DRAGONARMOR_GOLD_2.get(),
                IafItemRegistry.DRAGONARMOR_GOLD_3.get()
        );

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.GRAVEYARD_SOIL.get())
                .pattern(" E ")
                .pattern("ECE")
                .pattern(" E ")
                .define('C', Items.COARSE_DIRT)
                .define('E', IafItemRegistry.ECTOPLASM.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.ECTOPLASM.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafBlockRegistry.MYRMEX_DESERT_RESIN.get())
                .pattern("RR")
                .pattern("RR")
                .define('R', IafItemRegistry.MYRMEX_DESERT_RESIN.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.MYRMEX_DESERT_RESIN.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IafBlockRegistry.MYRMEX_JUNGLE_RESIN.get())
                .pattern("RR")
                .pattern("RR")
                .define('R', IafItemRegistry.MYRMEX_JUNGLE_RESIN.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.MYRMEX_JUNGLE_RESIN.get()))
                .save(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.SEA_SERPENT_ARROW.get(), 4)
                .pattern("X")
                .pattern("#")
                .pattern("Y")
                .define('#', Tags.Items.RODS_WOODEN)
                .define('X', IafItemRegistry.SERPENT_FANG.get())
                .define('Y', IafItemTags.SCALES_SEA_SERPENT)
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.SERPENT_FANG.get()))
                .save(consumer);

        armorSet(consumer, IafItemRegistry.MYRMEX_DESERT_CHITIN.get(),
                IafItemRegistry.MYRMEX_DESERT_HELMET.get(),
                IafItemRegistry.MYRMEX_DESERT_CHESTPLATE.get(),
                IafItemRegistry.MYRMEX_DESERT_LEGGINGS.get(),
                IafItemRegistry.MYRMEX_DESERT_BOOTS.get()
        );

        toolSet(consumer, IafItemRegistry.MYRMEX_DESERT_CHITIN.get(), IafItemTags.BONES_WITHER,
                IafItemRegistry.MYRMEX_DESERT_SWORD.get(),
                IafItemRegistry.MYRMEX_DESERT_PICKAXE.get(),
                IafItemRegistry.MYRMEX_DESERT_AXE.get(),
                IafItemRegistry.MYRMEX_DESERT_SHOVEL.get(),
                IafItemRegistry.MYRMEX_DESERT_HOE.get()
        );

        armorSet(consumer, IafItemRegistry.MYRMEX_JUNGLE_CHITIN.get(),
                IafItemRegistry.MYRMEX_JUNGLE_HELMET.get(),
                IafItemRegistry.MYRMEX_JUNGLE_CHESTPLATE.get(),
                IafItemRegistry.MYRMEX_JUNGLE_LEGGINGS.get(),
                IafItemRegistry.MYRMEX_JUNGLE_BOOTS.get()
        );

        toolSet(consumer, IafItemRegistry.MYRMEX_JUNGLE_CHITIN.get(), IafItemTags.BONES_WITHER,
                IafItemRegistry.MYRMEX_JUNGLE_SWORD.get(),
                IafItemRegistry.MYRMEX_JUNGLE_PICKAXE.get(),
                IafItemRegistry.MYRMEX_JUNGLE_AXE.get(),
                IafItemRegistry.MYRMEX_JUNGLE_SHOVEL.get(),
                IafItemRegistry.MYRMEX_JUNGLE_HOE.get()
        );

        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(IafItemRegistry.RAW_SILVER.get()), RecipeCategory.TOOLS, IafItemRegistry.SILVER_INGOT.get(), 0.7f, 200)
                .group("raw_silver")
                .unlockedBy(hasItem(IafItemRegistry.RAW_SILVER.get()), conditionsFromItem(IafItemRegistry.RAW_SILVER.get())).save(consumer, location(getItemPath(IafItemRegistry.SILVER_INGOT.get())) + "_from_smelting_" + getItemPath(IafItemRegistry.RAW_SILVER.get()));
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(IafItemRegistry.RAW_SILVER.get()), RecipeCategory.TOOLS, IafItemRegistry.SILVER_INGOT.get(), 0.7f, 100)
                .group("raw_silver")
                .unlockedBy(hasItem(IafItemRegistry.RAW_SILVER.get()), conditionsFromItem(IafItemRegistry.RAW_SILVER.get())).save(consumer, location(getItemPath(IafItemRegistry.SILVER_INGOT.get())) + "_from_blasting_" + getItemPath(IafItemRegistry.RAW_SILVER.get()));
        compact(consumer, IafItemRegistry.SILVER_INGOT.get(), IafBlockRegistry.SILVER_BLOCK.get());
        compact(consumer, IafItemRegistry.RAW_SILVER.get(), IafBlockRegistry.RAW_SILVER_BLOCK.get());
        compact(consumer, IafItemRegistry.SILVER_NUGGET.get(), IafItemRegistry.SILVER_INGOT.get());

        armorSet(consumer, IafItemTags.INGOTS_SILVER,
                IafItemRegistry.SILVER_HELMET.get(),
                IafItemRegistry.SILVER_CHESTPLATE.get(),
                IafItemRegistry.SILVER_LEGGINGS.get(),
                IafItemRegistry.SILVER_BOOTS.get()
        );

        toolSet(consumer, IafItemTags.INGOTS_SILVER, Tags.Items.RODS_WOODEN,
                IafItemRegistry.SILVER_SWORD.get(),
                IafItemRegistry.SILVER_PICKAXE.get(),
                IafItemRegistry.SILVER_AXE.get(),
                IafItemRegistry.SILVER_SHOVEL.get(),
                IafItemRegistry.SILVER_HOE.get()
        );

        compact(consumer, IafItemRegistry.SAPPHIRE_GEM.get(), IafBlockRegistry.SAPPHIRE_BLOCK.get());

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.TIDE_TRIDENT.get())
                .pattern("TTT")
                .pattern("SDS")
                .pattern(" B ")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('S', IafItemTags.SCALES_SEA_SERPENT)
                .define('T', IafItemRegistry.SERPENT_FANG.get())
                .define('B', IafItemRegistry.DRAGON_BONE.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.SERPENT_FANG.get()))
                .save(consumer);
    }

    private void createShapeless(@NotNull final Consumer<RecipeJsonProvider> consumer) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, IafItemRegistry.AMBROSIA.get())
                .requires(IafItemRegistry.PIXIE_DUST.get())
                .requires(Items.BOWL)
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.PIXIE_DUST.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.ASH.get())
                .requires(Ingredient.fromTag(IafItemTags.CHARRED_BLOCKS), 9)
                .unlockedBy("has_item", conditionsFromTag(IafItemTags.CHARRED_BLOCKS))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.BESTIARY.get())
                .requires(IafItemRegistry.MANUSCRIPT.get(), 3)
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.MANUSCRIPT.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.CHAIN_STICKY.get())
                .requires(Tags.Items.SLIMEBALLS)
                .requires(IafItemRegistry.CHAIN.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.CHAIN.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.COPPER_INGOT)
                .input(Ingredient.fromTag(IafItemTags.NUGGETS_COPPER), 9)
                .criterion("has_item", conditionsFromTag(IafItemTags.NUGGETS_COPPER))
                .offerTo(consumer, location("copper_nuggets_to_ingot"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, IafItemRegistry.COPPER_NUGGET.get(), 9)
                .requires(Tags.Items.INGOTS_COPPER)
                .unlockedBy("has_item", conditionsFromItem(Tags.Items.INGOTS_COPPER))
                .save(consumer, location("copper_ingot_to_nuggets"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, IafBlockRegistry.COPPER_PILE.get())
                .requires(Ingredient.fromTag(IafItemTags.NUGGETS_COPPER), 2)
                .unlockedBy("has_item", conditionsFromTag(IafItemTags.NUGGETS_COPPER))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, IafBlockRegistry.DRAGON_ICE.get())
                .requires(Ingredient.fromTag(IafItemTags.FROZEN_BLOCKS), 9)
                .unlockedBy("has_item", conditionsFromTag(IafItemTags.FROZEN_BLOCKS))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BONE_MEAL, 5)
                .input(IafItemTags.MOB_SKULLS)
                .criterion("has_item", conditionsFromTag(IafItemTags.MOB_SKULLS))
                .offerTo(consumer, location("skull_to_bone_meal"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DRAGONBONE_ARROW.get(), 5)
                .requires(IafItemRegistry.DRAGON_BONE.get())
                .requires(IafItemRegistry.WITHER_SHARD.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.WITHER_SHARD.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY.get())
                .requires(Items.VINE)
                .requires(IafBlockRegistry.DREAD_STONE_BRICKS.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREAD_STONE_BRICKS.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, IafBlockRegistry.DREADWOOD_PLANKS.get(), 4)
                .requires(IafBlockRegistry.DREADWOOD_LOG.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.DREADWOOD_LOG.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, IafItemRegistry.FIRE_STEW.get())
                .requires(Items.BOWL)
                .requires(Items.BLAZE_ROD)
                .requires(IafBlockRegistry.FIRE_LILY.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.FIRE_LILY.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, IafItemRegistry.FROST_STEW.get())
                .requires(Items.BOWL)
                .requires(Items.PRISMARINE_CRYSTALS)
                .requires(IafBlockRegistry.FROST_LILY.get())
                .unlockedBy("has_item", conditionsFromItem(IafBlockRegistry.FROST_LILY.get()))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, IafBlockRegistry.GOLD_PILE.get())
                .requires(Ingredient.ofItems(Tags.Items.NUGGETS_GOLD), 2)
                .unlockedBy("has_item", conditionsFromItem(Tags.Items.NUGGETS_GOLD))
                .save(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.GRAVEL)
                .input(Ingredient.fromTag(IafItemTags.CRACKLED_BLOCKS), 9)
                .criterion("has_item", conditionsFromTag(IafItemTags.CRACKLED_BLOCKS))
                .offerTo(consumer, location("crackled_to_gravel"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DRAGONBONE_SWORD_FIRE.get())
                .requires(IafItemRegistry.DRAGONBONE_SWORD.get())
                .requires(IafItemRegistry.FIRE_DRAGON_BLOOD.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.FIRE_DRAGON_BLOOD.get()))
                .save(consumer, location("dragonbone_sword_fire"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DRAGONBONE_SWORD_ICE.get())
                .requires(IafItemRegistry.DRAGONBONE_SWORD.get())
                .requires(IafItemRegistry.ICE_DRAGON_BLOOD.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.ICE_DRAGON_BLOOD.get()))
                .save(consumer, location("dragonbone_sword_ice"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.DRAGONBONE_SWORD_LIGHTNING.get())
                .requires(IafItemRegistry.DRAGONBONE_SWORD.get())
                .requires(IafItemRegistry.LIGHTNING_DRAGON_BLOOD.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.LIGHTNING_DRAGON_BLOOD.get()))
                .save(consumer, location("dragonbone_sword_lightning"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, IafItemRegistry.GHOST_SWORD.get())
                .requires(IafItemRegistry.DRAGONBONE_SWORD.get())
                .requires(IafItemRegistry.GHOST_INGOT.get())
                .unlockedBy("has_item", conditionsFromItem(IafItemRegistry.GHOST_INGOT.get()))
                .save(consumer, location("ghost_sword"));
    }

    private void compact(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible unpacked, final ItemConvertible packed) {
        String packedPath = ForgeRegistries.ITEMS.getKey(packed.asItem()).getPath();
        String unpackedPath = ForgeRegistries.ITEMS.getKey(unpacked.asItem()).getPath();


        offerReversibleCompactingRecipes(consumer, RecipeCategory.MISC, unpacked, RecipeCategory.BUILDING_BLOCKS, packed
                , locationString(unpackedPath + "_to_" + packedPath), null
                , locationString(packedPath + "_to_" + unpackedPath), null);
    }

    private void toolSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final TagKey<Item> material, final TagKey<Item> handle, final ItemConvertible... items) {
        toolSet(consumer, Ingredient.fromTag(material), Ingredient.fromTag(handle), items);
    }

    private void toolSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible material, final TagKey<Item> handle, final ItemConvertible... items) {
        toolSet(consumer, Ingredient.ofItems(material), Ingredient.fromTag(handle), items);
    }

    private void toolSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final TagKey<Item> material, final ItemConvertible handle, final ItemConvertible... items) {
        toolSet(consumer, Ingredient.fromTag(material), Ingredient.ofItems(handle), items);
    }

    private void toolSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible material, final ItemConvertible handle, final ItemConvertible... items) {
        toolSet(consumer, Ingredient.ofItems(material), Ingredient.ofItems(handle), items);
    }

    private void toolSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient material, final Ingredient handle, final ItemConvertible... results) {
        for (ItemConvertible result : results) {
            Item item = result.asItem();

            if (item instanceof SwordItem) {
                sword(consumer, material, handle, result);
            } else if (item instanceof PickaxeItem) {
                pickaxe(consumer, material, handle, result);
            } else if (item instanceof AxeItem) {
                axe(consumer, material, handle, result);
            } else if (item instanceof ShovelItem) {
                shovel(consumer, material, handle, result);
            } else if (item instanceof HoeItem) {
                hoe(consumer, material, handle, result);
            } else {
                throw new IllegalArgumentException("Result is not a valid tool: [" + result + "]");
            }
        }
    }

    private void armorSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final TagKey<Item> tag, final ItemConvertible... results) {
        armorSet(consumer, Ingredient.fromTag(tag), results);
    }

    private void armorSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible item, final ItemConvertible... results) {
        armorSet(consumer, Ingredient.ofItems(item), results);
    }

    private void armorSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible... results) {
        for (ItemConvertible result : results) {
            if (result.asItem() instanceof ArmorItem armorItem) {
                switch (armorItem.getType()) {
                    case HELMET -> helmet(consumer, ingredient, result);
                    case CHESTPLATE -> chestPlate(consumer, ingredient, result);
                    case LEGGINGS -> leggings(consumer, ingredient, result);
                    case BOOTS -> boots(consumer, ingredient, result);
                    default -> throw new IllegalArgumentException("Result is not a valid armor item: [" + result + "]");
                }
            } else {
                throw new IllegalArgumentException("Result is not an armor item: [" + result + "]");
            }
        }
    }

    private void helmet(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("###")
                .pattern("# #")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void chestPlate(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("# #")
                .pattern("###")
                .pattern("###")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void leggings(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("###")
                .pattern("# #")
                .pattern("# #")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void boots(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("# #")
                .pattern("# #")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void sword(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient material, final Ingredient handle, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, result)
                .pattern("M")
                .pattern("M")
                .pattern("H")
                .input('M', material)
                .input('H', handle)
                .criterion("has_item", conditionsFromItem(Arrays.stream(material.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void pickaxe(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient material, final Ingredient handle, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, result)
                .pattern("MMM")
                .pattern(" H ")
                .pattern(" H ")
                .input('M', material)
                .input('H', handle)
                .criterion("has_item", conditionsFromItem(Arrays.stream(material.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void axe(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient material, final Ingredient handle, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, result)
                .pattern("MM")
                .pattern("MH")
                .pattern(" H")
                .input('M', material)
                .input('H', handle)
                .criterion("has_item", conditionsFromItem(Arrays.stream(material.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void shovel(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient material, final Ingredient handle, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, result)
                .pattern("M")
                .pattern("H")
                .pattern("H")
                .input('M', material)
                .input('H', handle)
                .criterion("has_item", conditionsFromItem(Arrays.stream(material.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void hoe(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient material, final Ingredient handle, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, result)
                .pattern("MM")
                .pattern(" H")
                .pattern(" H")
                .input('M', material)
                .input('H', handle)
                .criterion("has_item", conditionsFromItem(Arrays.stream(material.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void dragonArmorSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible material, final ItemConvertible... results) {
        dragonArmorSet(consumer, Ingredient.ofItems(material), results);
    }

    private void dragonArmorSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final TagKey<Item> tag, final ItemConvertible... results) {
        dragonArmorSet(consumer, Ingredient.fromTag(tag), results);
    }

    private void dragonArmorSet(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible... results) {
        for (ItemConvertible result : results) {
            if (result instanceof ItemDragonArmor dragonArmor) {
                switch (dragonArmor.dragonSlot) {
                    case 0 -> dragonHead(consumer, ingredient, result);
                    case 1 -> dragonNeck(consumer, ingredient, result);
                    case 2 -> dragonBody(consumer, ingredient, result);
                    case 3 -> dragonTail(consumer, ingredient, result);
                    default ->
                            throw new IllegalArgumentException("Result is not a valid dragon armor [" + result + "]");
                }
            } else {
                throw new IllegalArgumentException("Result is not a dragon armor [" + result + "]");
            }
        }
    }

    private void dragonHead(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("   ")
                .pattern(" ##")
                .pattern("###")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void dragonNeck(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("   ")
                .pattern("###")
                .pattern(" ##")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void dragonBody(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("###")
                .pattern("###")
                .pattern("# #")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void dragonTail(@NotNull final Consumer<RecipeJsonProvider> consumer, final Ingredient ingredient, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
                .pattern("   ")
                .pattern("  #")
                .pattern("## ")
                .input('#', ingredient)
                .criterion("has_item", conditionsFromItem(Arrays.stream(ingredient.getMatchingStacks()).findFirst().get().getItem()))
                .offerTo(consumer);
    }

    private void forgeBrick(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible brick, final TagKey<Item> scales, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, result, 4)
                .pattern("SBS")
                .pattern("BSB")
                .pattern("SBS")
                .input('S', Ingredient.fromTag(scales))
                .input('B', brick)
                .criterion("has_item", conditionsFromItem(brick.asItem()))
                .offerTo(consumer);
    }

    private void forgeCore(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible brick, final ItemConvertible heart, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("BBB")
                .pattern("BHB")
                .pattern("BBB")
                .input('H', heart)
                .input('B', brick)
                .criterion("has_item", conditionsFromItem(brick.asItem()))
                .offerTo(consumer);
    }

    private void forgeInput(@NotNull final Consumer<RecipeJsonProvider> consumer, final ItemConvertible brick, final TagKey<Item> material, final ItemConvertible result) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("BIB")
                .pattern("I I")
                .pattern("BIB")
                .input('I', Ingredient.fromTag(material))
                .input('B', brick)
                .criterion("has_item", conditionsFromItem(brick.asItem()))
                .offerTo(consumer);
    }

    private static Identifier location(final String path) {
        return new Identifier(IceAndFire.MOD_ID, path);
    }

    private static String locationString(final String path) {
        return IceAndFire.MOD_ID + ":" + path;
    }

}