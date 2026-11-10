package windanesz.byg.registry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictionaryEntries {
    private OreDictionaryEntries() {
    }

    public static void init() {
        registerItems();
        registerBlocks();
    }

    private static void registerItems() {
        registerItem("berry", ModItems.hawthorn_berries, ModItems.holly_berries, ModItems.rowan_berries, ModItems.salal_berry);
        registerItem("cattail", ModItems.cattail);
        registerItem("chainPlatingKasai", ModItems.kasai_chain_plating);
        registerItem("dustCrystals", ModItems.light_blue_crystals, ModItems.purple_crystals, ModItems.red_crystals, ModItems.white_crystals);
        registerItem("dustGlowCane", ModItems.glowcane_dust_blue, ModItems.glowcane_dust_pink, ModItems.glowcane_dust_purple, ModItems.glowcane_dust_red);
        registerItem("dustMud", ModItems.mud_balls);
        registerItem("egg", Items.EGG, ModItems.kiwi_egg);
        registerItem("gemLatharium", ModItems.latharium_gem);
        registerItem("gemPendorite", ModItems.pendorite_gem);
        registerItem("gemTamrelite", ModItems.tamrelite_gem);
        registerItem("glowcane", ModItems.glowcane_stalk_blue, ModItems.glowcane_stalk_pink, ModItems.glowcane_stalk_purple, ModItems.glowcane_stalk_red);
        registerItem("glowshroom", ModItems.blue_glowshroom, ModItems.green_glowshroom, ModItems.purple_glowshroom);
        registerItem("ingotKasai", ModItems.kasai_ingot);
        registerItem("reed", ModItems.reeds);
        registerItem("stickEnchanted", ModItems.enchanted_stick);
        registerItem("stickStone", ModItems.stone_stick);
        registerItem("doorWood",
                ModItems.aspen_door, ModItems.baobab_door, ModItems.blue_enchanted_door,
                ModItems.cherry_door, ModItems.cika_door, ModItems.cypress_door,
                ModItems.ebony_door, ModItems.eucalyptus_door, ModItems.fir_door,
                ModItems.frozen_oak_door, ModItems.great_oak_door, ModItems.green_enchanted_door,
                ModItems.hawthorn_door, ModItems.holly_door, ModItems.ironwood_door,
                ModItems.jacaranda_door, ModItems.mahogany_door, ModItems.mangrove_door,
                ModItems.maple_door, ModItems.palm_door, ModItems.pine_door,
                ModItems.rowan_door, ModItems.skyris_door, ModItems.willow_door,
                ModItems.witch_hazel_door,
                ModItems.zelkova_door);
    }

    private static void registerBlocks() {
        registerBlock("blockBrickMud", ModBlocks.mud_bricks);
        registerBlock("blockCactus", ModBlocks.sonoran_cactus, ModBlocks.sonoran_cactus_flowering);
        registerBlock("blockCrystal", ModBlocks.light_blue_crystal_block, ModBlocks.purple_crystal_block, ModBlocks.red_crystal_block, ModBlocks.white_crystal_block);
        registerBlock("blockGlowCane", ModBlocks.glowcane_block_blue, ModBlocks.glowcane_block_pink, ModBlocks.glowcane_block_purple, ModBlocks.glowcane_block_red);
        registerBlock("blockMelon", ModBlocks.carved_melon, ModBlocks.jackomelon);
        registerBlock("blockMud", ModBlocks.mud_block);
        registerBlock("bookshelfWood",
                ModBlocks.aspen_bookshelf, ModBlocks.baobab_bookshelf, ModBlocks.cherry_bookshelf, ModBlocks.cika_bookshelf, ModBlocks.ebony_bookshelf,
                ModBlocks.enchanted_bookshelf, ModBlocks.fir_bookshelf, ModBlocks.frozen_oak_bookshelf, ModBlocks.great_oak_bookshelf, ModBlocks.green_enchanted_bookshelf,
                ModBlocks.hawthorn_bookshelf, ModBlocks.holly_bookshelf, ModBlocks.ironwood_bookshelf, ModBlocks.jacaranda_bookshelf, ModBlocks.mangrove_bookshelf,
                ModBlocks.maple_bookshelf, ModBlocks.pine_bookshelf, ModBlocks.rainbow_eucalyptus_bookshelf, ModBlocks.redwood_bookshelf, ModBlocks.rowan_bookshelf,
                ModBlocks.skyris_bookshelf, ModBlocks.willow_bookshelf, ModBlocks.witch_hazel_bookshelf, ModBlocks.zelkova_bookshelf);
        registerBlock("cobblestone", ModBlocks.scoria, ModBlocks.soapstone);
        registerBlock("dirt", ModBlocks.hardened_dirt, ModBlocks.meadow_dirt, ModBlocks.pasture_dirt, ModBlocks.peat_dirt, ModBlocks.sandy_dirt);
        registerBlock("fenceGateWood",
                ModBlocks.aspen_fence_gate, ModBlocks.baobab_fence_gate, ModBlocks.blue_enchanted_fence_gate, ModBlocks.cherry_fence_gate, ModBlocks.cika_fence_gate,
                ModBlocks.cypress_fence_gate, ModBlocks.ebony_fence_gate, ModBlocks.fir_fence_gate, ModBlocks.frozen_oak_fence_gate, ModBlocks.great_oak_fence_gate,
                ModBlocks.green_enchanted_fence_gate, ModBlocks.hawthorn_fence_gate, ModBlocks.holly_fence_gate, ModBlocks.ironwood_fence_gate, ModBlocks.jacaranda_fence_gate,
                ModBlocks.mahogany_fence_gate, ModBlocks.mangrove_fence_gate, ModBlocks.maple_fence_gate, ModBlocks.palm_fence_gate, ModBlocks.pine_fence_gate,
                ModBlocks.rainbow_eucalyptus_fence_gate, ModBlocks.redwood_fence_gate, ModBlocks.rowan_fence_gate, ModBlocks.skyris_fence_gate, ModBlocks.willow_fence_gate,
                ModBlocks.witch_hazel_fence_gate, ModBlocks.zelkova_fence_gate);
        registerBlock("fenceWood",
                ModBlocks.aspen_fence, ModBlocks.aspen_wood_wall, ModBlocks.baobab_fence, ModBlocks.baobab_wall, ModBlocks.blue_enchanted_fence, ModBlocks.blue_enchanted_wood_wall,
                ModBlocks.cherry_fence, ModBlocks.cherry_wood_wall, ModBlocks.cika_fence, ModBlocks.cika_wood_wall, ModBlocks.cypress_fence, ModBlocks.cypress_wood_wall,
                ModBlocks.ebony_fence, ModBlocks.ebony_wood_wall, ModBlocks.fir_fence, ModBlocks.fir_wood_wall, ModBlocks.frozen_oak_fence, ModBlocks.frozen_oak_wood_wall,
                ModBlocks.great_oak_fence, ModBlocks.great_oak_wood_wall, ModBlocks.green_enchanted_fence, ModBlocks.green_enchanted_wood_wall, ModBlocks.hawthorn_fence, ModBlocks.hawthorn_wood_wall,
                ModBlocks.holly_fence, ModBlocks.holly_wood_wall, ModBlocks.ironwood_fence, ModBlocks.ironwood_wood_wall, ModBlocks.jacaranda_fence, ModBlocks.jacaranda_wood_wall,
                ModBlocks.mahogany_fence, ModBlocks.mahogany_wood_wall, ModBlocks.mangrove_fence, ModBlocks.mangrove_wood_wall, ModBlocks.maple_fence, ModBlocks.maple_wood_wall,
                ModBlocks.palm_fence, ModBlocks.palm_wood_wall, ModBlocks.palo_verde_wood_wall, ModBlocks.pine_fence, ModBlocks.pine_wood_wall, ModBlocks.rainbow_eucalyptus_fence,
                ModBlocks.rainbow_eucalyptus_wood_wall, ModBlocks.redwood_fence, ModBlocks.redwood_wood_wall, ModBlocks.rowan_wood_wall, ModBlocks.rowan_fence, ModBlocks.skyris_fence,
                ModBlocks.skyris_wood_wall, ModBlocks.willow_fence, ModBlocks.willow_wood_wall, ModBlocks.witch_hazel_fence, ModBlocks.witch_hazel_wood_wall, ModBlocks.zelkova_fence,
                ModBlocks.zelkova_wood_wall);
        registerBlock("flowerBlack", ModBlocks.black_rose);
        registerBlock("flowerBlue", ModBlocks.bluesage, ModBlocks.violet_leather_flower);
        registerBlock("flowerCyan", ModBlocks.alpine_bellflower, ModBlocks.azalea, ModBlocks.cyan_amaranth, ModBlocks.cyan_rose, ModBlocks.cyan_tulip, ModBlocks.delphinium, ModBlocks.winter_cyclamen);
        registerBlock("flowerGreen", ModBlocks.green_tulip);
        registerBlock("flowerLightBlue", ModBlocks.winter_scilla);
        registerBlock("flowerLightPink", ModBlocks.bistort, ModBlocks.japanese_orchid, ModBlocks.osiria_rose, ModBlocks.salal_bush, ModBlocks.silver_vase_flower);
        registerBlock("flowerMagenta", ModBlocks.fairy_slipper, ModBlocks.magenta_amaranth, ModBlocks.magenta_celosia, ModBlocks.magenta_tulip);
        registerBlock("flowerOrange", ModBlocks.california_poppy, ModBlocks.guzmania, ModBlocks.incan_lily, ModBlocks.orange_amaranth, ModBlocks.orange_celosia, ModBlocks.orange_daisy);
        registerBlock("flowerPink", ModBlocks.daffodil, ModBlocks.firecracker, ModBlocks.peach_leather_flower, ModBlocks.pink_allium, ModBlocks.pink_allium_bush, ModBlocks.pink_anemone, ModBlocks.pink_daffodil, ModBlocks.pink_orchid, ModBlocks.protea_flower);
        registerBlock("flowerPurple", ModBlocks.allium_bush, ModBlocks.crocus, ModBlocks.iris, ModBlocks.purple_amaranth, ModBlocks.purple_celosia, ModBlocks.purple_orchid, ModBlocks.purple_sage, ModBlocks.purple_tulip);
        registerBlock("flowerRed", ModBlocks.amaranth, ModBlocks.begonia, ModBlocks.kovan, ModBlocks.lazarus_bell_flower, ModBlocks.red_celosia, ModBlocks.red_corn_flower, ModBlocks.red_orchid, ModBlocks.rose, ModBlocks.torch_ginger);
        registerBlock("flowerWhite", ModBlocks.angelica, ModBlocks.richea, ModBlocks.sacred_datura, ModBlocks.snowdrops, ModBlocks.white_anemone, ModBlocks.white_celosia, ModBlocks.white_sage, ModBlocks.winter_rose);
        registerBlock("flowerYellow", ModBlocks.lollipop_flower, ModBlocks.yellow_celosia, ModBlocks.yellow_daffodil, ModBlocks.yellow_tulip);

        registerBlock("grass", ModBlocks.glowcelium, ModBlocks.meadow_grass, ModBlocks.overgrown_stone, ModBlocks.pasture_grass, ModBlocks.peat_grass, ModBlocks.rocky_grass, ModBlocks.sandy_grass);
        registerBlock("leafpile", ModBlocks.leafpile, ModBlocks.leaf_pile_dead);
        registerBlock("lilypad", ModBlocks.tiny_lilypad);
        registerBlock("logWood",
                ModBlocks.aspen_log, ModBlocks.aspen_wood, ModBlocks.baobab_log, ModBlocks.baobab_wood, ModBlocks.blue_enchanted_wood, ModBlocks.cherry_log, ModBlocks.cherry_wood,
                ModBlocks.cika_log, ModBlocks.cika_wood, ModBlocks.cypress_log, ModBlocks.cypress_wood, ModBlocks.ebony_log, ModBlocks.ebony_wood, ModBlocks.enchanted_log,
                ModBlocks.fir_log, ModBlocks.fir_wood, ModBlocks.frozen_oak_log, ModBlocks.frozen_oak_wood, ModBlocks.great_oak_log, ModBlocks.great_oak_wood, ModBlocks.green_enchanted_log,
                ModBlocks.green_enchanted_wood, ModBlocks.hawthorn_log, ModBlocks.hawthorn_wood, ModBlocks.holly_log, ModBlocks.holly_wood, ModBlocks.ironwood_log, ModBlocks.ironwood_wood,
                ModBlocks.jacaranda_log, ModBlocks.jacaranda_wood, ModBlocks.mahogany_log, ModBlocks.mahogany_wood, ModBlocks.mangrove_log, ModBlocks.mangrove_wood, ModBlocks.maple_log,
                ModBlocks.maple_wood, ModBlocks.palm_log, ModBlocks.palm_wood, ModBlocks.palo_verde_log, ModBlocks.palo_verde_wood, ModBlocks.pine_log, ModBlocks.pine_wood,
                ModBlocks.plant_stem, ModBlocks.rainbow_eucalyptus_log, ModBlocks.rainbow_eucalyptus_wood, ModBlocks.redwood_log, ModBlocks.redwood_wood, ModBlocks.rowan_log,
                ModBlocks.rowan_wood, ModBlocks.skyris_log, ModBlocks.skyris_wood, ModBlocks.stripped_aspen_log, ModBlocks.stripped_aspen_wood, ModBlocks.stripped_baobab_log,
                ModBlocks.stripped_baobab_wood, ModBlocks.stripped_blue_enchanted_log, ModBlocks.stripped_blue_enchanted_wood, ModBlocks.stripped_cherry_log, ModBlocks.stripped_cherry_wood,
                ModBlocks.stripped_cika_log, ModBlocks.stripped_cika_wood, ModBlocks.stripped_cypress_log, ModBlocks.stripped_cypress_wood, ModBlocks.stripped_ebony_log,
                ModBlocks.stripped_ebony_wood, ModBlocks.stripped_great_oak_log, ModBlocks.stripped_great_oak_wood, ModBlocks.stripped_green_enchanted_log, ModBlocks.stripped_green_enchanted_wood,
                ModBlocks.stripped_hawthorn_log, ModBlocks.stripped_hawthorn_wood, ModBlocks.stripped_holly_log, ModBlocks.stripped_holly_wood, ModBlocks.stripped_ironwood_log,
                ModBlocks.stripped_ironwood_wood, ModBlocks.stripped_jacaranda_log, ModBlocks.stripped_jacaranda_wood, ModBlocks.stripped_mangrove_log, ModBlocks.stripped_mangrove_wood,
                ModBlocks.stripped_maple_log, ModBlocks.stripped_maple_wood, ModBlocks.stripped_palo_verde_log, ModBlocks.stripped_palo_verde_wood, ModBlocks.stripped_pine_log,
                ModBlocks.stripped_pine_wood, ModBlocks.stripped_rainbow_eucalyptus_log, ModBlocks.stripped_rainbow_eucalyptus_wood, ModBlocks.stripped_redwood_log, ModBlocks.stripped_redwood_wood,
                ModBlocks.stripped_rowan_log, ModBlocks.stripped_rowan_wood, ModBlocks.stripped_skyris_log, ModBlocks.stripped_skyris_wood, ModBlocks.stripped_willow_log, ModBlocks.stripped_willow_wood,
                ModBlocks.stripped_witch_hazel_log, ModBlocks.stripped_witch_hazel_wood, ModBlocks.stripped_zelkova_log, ModBlocks.stripped_zelkova_wood, ModBlocks.stripped_fir_log,
                ModBlocks.stripped_fir_wood, ModBlocks.thorn_block, ModBlocks.thorn_branches, ModBlocks.willow_log, ModBlocks.willow_wood, ModBlocks.witch_hazel_log, ModBlocks.witch_hazel_wood,
                ModBlocks.zelkova_log, ModBlocks.zelkova_wood);
        registerBlock("mushroomBrown", ModBlocks.black_puff, ModBlocks.weeping_milk_cap, ModBlocks.wood_blewit);
        registerBlock("plankWood",
                ModBlocks.aspen_planks, ModBlocks.baobab_planks, ModBlocks.cherry_planks, ModBlocks.cika_planks, ModBlocks.cypress_planks, ModBlocks.ebony_planks, ModBlocks.enchanted_planks,
                ModBlocks.fir_planks, ModBlocks.frozen_oak_planks, ModBlocks.great_oak_planks, ModBlocks.green_enchanted_planks, ModBlocks.hawthorn_planks, ModBlocks.holly_planks, ModBlocks.ironwood_planks,
                ModBlocks.jacaranda_planks, ModBlocks.mahogany_planks, ModBlocks.mangrove_planks, ModBlocks.maple_planks, ModBlocks.palm_planks, ModBlocks.pine_planks, ModBlocks.rainbow_eucalyptus_planks,
                ModBlocks.redwood_planks, ModBlocks.rowan_planks, ModBlocks.skyris_planks, ModBlocks.willow_planks, ModBlocks.witch_hazel_planks, ModBlocks.zelkova_planks);
        registerBlock("sand", ModBlocks.black_sand, ModBlocks.cracked_sand, ModBlocks.light_blue_sand, ModBlocks.pink_sand, ModBlocks.purple_sand, ModBlocks.red_cracked_sand, ModBlocks.white_sand);
        registerBlock("sandstone",
                ModBlocks.black_chiseled_sandstone, ModBlocks.black_sandstone, ModBlocks.black_smooth_sandstone, ModBlocks.light_blue_chiseled_sandstone, ModBlocks.light_blue_sandstone,
                ModBlocks.light_blue_smooth_sandstone, ModBlocks.pink_chiseled_sandstone, ModBlocks.pink_sandstone, ModBlocks.pink_smooth_sandstone, ModBlocks.purple_chiseled_sandstone,
                ModBlocks.purple_sandstone, ModBlocks.purple_smooth_sandstone, ModBlocks.white_chiseled_sandstone, ModBlocks.white_sandstone, ModBlocks.white_smooth_sandstone);
        registerBlock("slabWood", ModBlocks.aspen_slab, ModBlocks.cypress_slab, ModBlocks.fir_slab, ModBlocks.frozen_oak_slab, ModBlocks.green_enchanted_slab, ModBlocks.mahogany_slab, ModBlocks.palm_slab, ModBlocks.baobab_slab, ModBlocks.cherry_slab, ModBlocks.cika_slab, ModBlocks.ebony_slab, ModBlocks.enchanted_slab, ModBlocks.great_oak_slab, ModBlocks.hawthorn_slab,
                ModBlocks.holly_slab, ModBlocks.ironwood_slab, ModBlocks.jacaranda_slab, ModBlocks.mangrove_slab, ModBlocks.maple_slab, ModBlocks.pine_slab, ModBlocks.rainbow_eucalyptus_slab,
                ModBlocks.redwood_slab, ModBlocks.rowan_slab, ModBlocks.skyris_slab, ModBlocks.willow_slab, ModBlocks.witch_hazel_slab, ModBlocks.zelkova_slab);
        registerBlock("stairWood",
                ModBlocks.aspen_stairs, ModBlocks.baobab_stairs, ModBlocks.blue_enchanted_stairs, ModBlocks.cherry_stairs, ModBlocks.cika_stairs, ModBlocks.cypress_stairs,
                ModBlocks.ebony_stairs, ModBlocks.fir_stairs, ModBlocks.frozen_oak_stairs, ModBlocks.great_oak_stairs, ModBlocks.green_enchanted_stairs, ModBlocks.hawthorn_stairs,
                ModBlocks.holly_stairs, ModBlocks.ironwood_stairs, ModBlocks.jacaranda_stairs, ModBlocks.mahogany_stairs, ModBlocks.mangrove_stairs, ModBlocks.maple_stairs,
                ModBlocks.palm_stairs, ModBlocks.pine_stairs, ModBlocks.rainbow_eucalyptus_stairs, ModBlocks.redwood_stairs, ModBlocks.rowan_stairs, ModBlocks.skyris_stairs,
                ModBlocks.willow_stairs, ModBlocks.witch_hazel_stairs, ModBlocks.zelkova_stairs);
        registerBlock("stickWood", ModBlocks.aspen_stick_pile);
        registerBlock("stone", ModBlocks.rocky_stone);

        registerBlock("treeLeaves",
                ModBlocks.aspen_leaves, ModBlocks.baobab_leaves, ModBlocks.birch_leaves_yellow, ModBlocks.blue_petal, ModBlocks.brown_birch_leaves, ModBlocks.cherry_leaves_pink,
                ModBlocks.cherry_leaves_white, ModBlocks.cika_leaves, ModBlocks.cypress_leaves, ModBlocks.ebony_leaves, ModBlocks.enchanted_leaves_blue, ModBlocks.enchanted_leaves_pink,
                ModBlocks.enchanted_leaves_purple, ModBlocks.fir_leaves, ModBlocks.frozen_oak_leaves, ModBlocks.great_oak_leaves, ModBlocks.hawthorn_berry_leaves, ModBlocks.hawthorn_leaves,
                ModBlocks.hawthorn_leaves_flowering, ModBlocks.holly_berry_leaves, ModBlocks.holly_leaves, ModBlocks.ironwood_leaves, ModBlocks.jacaranda_leaves, ModBlocks.light_blue_petal,
                ModBlocks.mahogany_leaves, ModBlocks.mangrove_leaves, ModBlocks.maple_leaves_red, ModBlocks.maple_leaves_silver, ModBlocks.oak_leaves_dry_brown, ModBlocks.oak_leaves_dry_green,
                ModBlocks.oak_leaves_orange, ModBlocks.oak_leaves_red, ModBlocks.orange_birch_leaves, ModBlocks.orchard_leaves, ModBlocks.orchard_leaves_apple, ModBlocks.orchard_leaves_flowering,
                ModBlocks.palm_leaves, ModBlocks.palo_verde_leaves, ModBlocks.palo_verde_leaves_flowering, ModBlocks.pine_leaves, ModBlocks.purple_petal, ModBlocks.rainbow_eucalyptus_leaves,
                ModBlocks.red_petal, ModBlocks.red_birch_leaves, ModBlocks.redwood_leaves, ModBlocks.rowan_berry_leaves, ModBlocks.rowan_leaves, ModBlocks.silver_apple_white_skyris_leaves,
                ModBlocks.skyris_leaves, ModBlocks.skyris_leaves_green_apple, ModBlocks.spruce_leaves_blue, ModBlocks.spruce_leaves_orange, ModBlocks.spruce_leaves_red, ModBlocks.spruce_leaves_yellow,
                ModBlocks.stellata_leaves_pink, ModBlocks.stellata_leaves_white, ModBlocks.white_petal, ModBlocks.white_skyris_leaves, ModBlocks.willow_leaves, ModBlocks.witch_hazel_leaves,
                ModBlocks.witch_hazel_leaves_blooming, ModBlocks.yellow_petal, ModBlocks.zelkova_leaves);
        registerBlock("dyeBlue", ModBlocks.blue_petal);
        registerBlock("dyeLightBlue", ModBlocks.light_blue_petal);
        registerBlock("dyePurple", ModBlocks.purple_petal);
        registerBlock("dyeRed", ModBlocks.red_petal);
        registerBlock("dyeWhite", ModBlocks.white_petal);
        registerBlock("dyeYellow", ModBlocks.yellow_petal);
        registerBlock("treeSapling",
                ModBlocks.aspen_sapling, ModBlocks.baobab_sapling, ModBlocks.blue_enchanted_sapling, ModBlocks.blue_spruce_sapling, ModBlocks.brown_birch_sapling, ModBlocks.cika_sapling,
                ModBlocks.cypress_sapling, ModBlocks.dry_brown_oak_sapling, ModBlocks.dry_green_oak_sapling, ModBlocks.ebony_sapling, ModBlocks.fir_sapling, ModBlocks.frozen_oak_sapling,
                ModBlocks.great_oak_sapling, ModBlocks.hawthorn_sapling, ModBlocks.holly_sapling, ModBlocks.ironwood_sapling, ModBlocks.jacaranda_sapling, ModBlocks.mahogany_sapling,
                ModBlocks.mangrove_sapling, ModBlocks.orange_birch_sapling, ModBlocks.orange_oak_sapling, ModBlocks.orange_spruce_sapling, ModBlocks.orchard_sapling, ModBlocks.palm_sapling,
                ModBlocks.palo_verde_sapling, ModBlocks.pine_sapling, ModBlocks.pink_enchanted_sapling, ModBlocks.pink_cherry_sapling, ModBlocks.pink_stellata_sapling, ModBlocks.purple_enchanted_sapling,
                ModBlocks.rainbow_eucalyptus_sapling, ModBlocks.red_birch_sapling, ModBlocks.red_maple_sapling, ModBlocks.red_oak_sapling, ModBlocks.red_spruce_sapling, ModBlocks.redwood_sapling,
                ModBlocks.rowan_sapling, ModBlocks.silver_maple_sapling, ModBlocks.skyris_sapling, ModBlocks.white_cherry_sapling, ModBlocks.white_stellata_sapling, ModBlocks.willow_sapling,
                ModBlocks.witch_hazel_sapling, ModBlocks.yellow_birch_sapling, ModBlocks.yellow_spruce_sapling, ModBlocks.zelkova_sapling);
    }

    private static void registerBlock(String oreName, Block... blocks) {
        for (Block block : blocks) {
            if (block == null || block == Blocks.AIR || block.getRegistryName() == null) {
                continue;
            }
            Item item = Item.getItemFromBlock(block);
            if (item == null || item == Items.AIR) {
                continue;
            }
            OreDictionary.registerOre(oreName, block);
        }
    }

    private static void registerItem(String oreName, Item... items) {
        for (Item item : items) {
            if (item == null || item == Items.AIR || item.getRegistryName() == null) {
                continue;
            }
            OreDictionary.registerOre(oreName, item);
        }
    }
}
