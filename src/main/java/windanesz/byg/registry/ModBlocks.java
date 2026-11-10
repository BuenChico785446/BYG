package windanesz.byg.registry;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import windanesz.byg.BiomesYouGo;
import windanesz.byg.Config;
import windanesz.byg.blocks.*;
import windanesz.byg.blocks.BlockReed;
import windanesz.byg.client.BYGTab;
import windanesz.byg.worldgen.treegenerator.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

@ObjectHolder(BiomesYouGo.MODID)
@Mod.EventBusSubscriber
public final class ModBlocks {
    public static final Block algae = placeholder();
    public static final Block allium_bush = placeholder();
    public static final Block alpine_bellflower = placeholder();
    public static final Block amaranth = placeholder();
    public static final Block angelica = placeholder();
    public static final Block aspen_bookshelf = placeholder();
    public static final Block aspen_door_bottom = placeholder();
    public static final Block aspen_fence = placeholder();
    public static final Block aspen_fence_gate = placeholder();
    public static final Block aspen_leaves = placeholder();
    public static final Block aspen_log = placeholder();
    public static final Block aspen_planks = placeholder();
    public static final Block aspen_slab = placeholder();
    public static final Block aspen_sapling = placeholder();
    public static final Block aspen_stairs = placeholder();
    public static final Block aspen_wood = placeholder();
    public static final Block aspen_wood_wall = placeholder();
    public static final Block aspen_stick_pile = placeholder();
    public static final Block azalea = placeholder();
    public static final Block baobab_door_bottom = placeholder();
    public static final Block baobab_fence = placeholder();
    public static final Block baobab_fence_gate = placeholder();
    public static final Block baobab_stairs = placeholder();
    public static final Block baobab_wall = placeholder();
    public static final Block baobab_wood = placeholder();
    public static final Block baobab_bookshelf = placeholder();
    public static final Block baobab_fruit_block = placeholder();
    public static final Block baobab_leaves = placeholder();
    public static final Block baobab_log = placeholder();
    public static final Block baobab_planks = placeholder();
    public static final Block baobab_sapling = placeholder();
    public static final Block baobab_slab = placeholder();
    public static final Block begonia = placeholder();
    public static final Block birch_leaves_yellow = placeholder();
    public static final Block bistort = placeholder();
    public static final Block black_puff = placeholder();
    public static final Block black_rose = placeholder();
    public static final Block black_chiseled_sandstone = placeholder();
    public static final Block black_sand = placeholder();
    public static final Block black_sandstone = placeholder();
    public static final Block black_smooth_sandstone = placeholder();
    public static final Block blanket_weed = placeholder();
    public static final Block blue_enchanted_door_bottom = placeholder();
    public static final Block blue_enchanted_fence = placeholder();
    public static final Block blue_enchanted_fence_gate = placeholder();
    public static final Block blue_enchanted_sapling = placeholder();
    public static final Block blue_enchanted_stairs = placeholder();
    public static final Block blue_enchanted_wood = placeholder();
    public static final Block blue_enchanted_wood_wall = placeholder();
    public static final Block blue_petal = placeholder();
    public static final Block blueberry_bush = placeholder();
    public static final Block blue_glowshroom = placeholder();
    public static final Block bluesage = placeholder();
    public static final Block blue_spruce_sapling = placeholder();
    public static final Block brown_birch_sapling = placeholder();
    public static final Block brown_birch_leaves = placeholder();
    public static final Block california_poppy = placeholder();
    public static final Block carved_melon = placeholder();
    public static final Block cattails = placeholder();
    public static final Block cherry_door_bottom = placeholder();
    public static final Block cherry_fence = placeholder();
    public static final Block cherry_fence_gate = placeholder();
    public static final Block cherry_stairs = placeholder();
    public static final Block cherry_wood = placeholder();
    public static final Block cherry_wood_wall = placeholder();
    public static final Block cherry_bookshelf = placeholder();
    public static final Block cherry_leaves_pink = placeholder();
    public static final Block cherry_leaves_white = placeholder();
    public static final Block cherry_log = placeholder();
    public static final Block cherry_planks = placeholder();
    public static final Block cherry_slab = placeholder();
    public static final Block cika_door_bottom = placeholder();
    public static final Block cika_fence = placeholder();
    public static final Block cika_fence_gate = placeholder();
    public static final Block cika_stairs = placeholder();
    public static final Block cika_wood = placeholder();
    public static final Block cika_wood_wall = placeholder();
    public static final Block cika_bookshelf = placeholder();
    public static final Block cika_leaves = placeholder();
    public static final Block cika_log = placeholder();
    public static final Block cika_planks = placeholder();
    public static final Block cika_sapling = placeholder();
    public static final Block cika_slab = placeholder();
    public static final Block clover = placeholder();
    public static final Block cracked_sand = placeholder();
    public static final Block crate = placeholder();
    public static final Block crocus = placeholder();
    public static final Block cyan_amaranth = placeholder();
    public static final Block cyan_rose = placeholder();
    public static final Block cyan_tulip = placeholder();
    public static final Block cypress_door_bottom = placeholder();
    public static final Block cypress_fence = placeholder();
    public static final Block cypress_fence_gate = placeholder();
    public static final Block cypress_stairs = placeholder();
    public static final Block cypress_wood = placeholder();
    public static final Block cypress_wood_wall = placeholder();
    public static final Block cypress_leaves = placeholder();
    public static final Block cypress_log = placeholder();
    public static final Block cypress_planks = placeholder();
    public static final Block cypress_slab = placeholder();
    public static final Block cypress_sapling = placeholder();
    public static final Block daffodil = placeholder();
    public static final Block dead_grass = placeholder();
    public static final Block delphinium = placeholder();
    public static final Block dry_brown_oak_sapling = placeholder();
    public static final Block dry_green_oak_sapling = placeholder();
    public static final Block ebony_door_bottom = placeholder();
    public static final Block ebony_fence = placeholder();
    public static final Block ebony_fence_gate = placeholder();
    public static final Block ebony_stairs = placeholder();
    public static final Block ebony_wood = placeholder();
    public static final Block ebony_wood_wall = placeholder();
    public static final Block ebony_bookshelf = placeholder();
    public static final Block ebony_leaves = placeholder();
    public static final Block ebony_log = placeholder();
    public static final Block ebony_planks = placeholder();
    public static final Block ebony_sapling = placeholder();
    public static final Block ebony_slab = placeholder();
    public static final Block enchanted_bookshelf = placeholder();
    public static final Block enchanted_leaves_blue = placeholder();
    public static final Block enchanted_leaves_pink = placeholder();
    public static final Block enchanted_leaves_purple = placeholder();
    public static final Block enchanted_log = placeholder();
    public static final Block enchanted_planks = placeholder();
    public static final Block enchanted_slab = placeholder();
    public static final Block eucalyptus_door_bottom = placeholder();
    public static final Block fairy_slipper = placeholder();
    public static final Block fir_door_bottom = placeholder();
    public static final Block fir_fence = placeholder();
    public static final Block fir_fence_gate = placeholder();
    public static final Block fir_stairs = placeholder();
    public static final Block fir_wood_wall = placeholder();
    public static final Block fir_bookshelf = placeholder();
    public static final Block firecracker = placeholder();
    public static final Block fir_leaves = placeholder();
    public static final Block fir_log = placeholder();
    public static final Block fir_planks = placeholder();
    public static final Block fir_slab = placeholder();
    public static final Block fir_sapling = placeholder();
    public static final Block fir_wood = placeholder();
    public static final Block flowers = placeholder();
    public static final Block foxglove = placeholder();
    public static final Block frozen_oak_door_bottom = placeholder();
    public static final Block frozen_oak_fence = placeholder();
    public static final Block frozen_oak_fence_gate = placeholder();
    public static final Block frozen_oak_sapling = placeholder();
    public static final Block frozen_oak_stairs = placeholder();
    public static final Block frozen_oak_wood = placeholder();
    public static final Block frozen_oak_wood_wall = placeholder();
    public static final Block frozen_oak_bookshelf = placeholder();
    public static final Block frozen_oak_leaves = placeholder();
    public static final Block frozen_oak_log = placeholder();
    public static final Block frozen_oak_planks = placeholder();
    public static final Block frozen_oak_slab = placeholder();
    public static final Block glowcane_block_blue = placeholder();
    public static final Block glowcane_block_pink = placeholder();
    public static final Block glowcane_block_purple = placeholder();
    public static final Block glowcane_block_red = placeholder();
    public static final Block glowcane_blue = placeholder();
    public static final Block glowcane_pink = placeholder();
    public static final Block glowcane_purple = placeholder();
    public static final Block glowcane_red = placeholder();
    public static final Block glowcelium = placeholder();
    public static final Block glowshroom_block_blue = placeholder();
    public static final Block glowshroom_block_purple = placeholder();
    public static final Block glowshroom_stem_yellow = placeholder();
    public static final Block golden_spined_cactus = placeholder();
    public static final Block great_oak_door_bottom = placeholder();
    public static final Block great_oak_fence = placeholder();
    public static final Block great_oak_fence_gate = placeholder();
    public static final Block great_oak_stairs = placeholder();
    public static final Block great_oak_wood = placeholder();
    public static final Block great_oak_wood_wall = placeholder();
    public static final Block great_oak_bookshelf = placeholder();
    public static final Block great_oak_leaves = placeholder();
    public static final Block great_oak_log = placeholder();
    public static final Block great_oak_planks = placeholder();
    public static final Block great_oak_sapling = placeholder();
    public static final Block great_oak_slab = placeholder();
    public static final Block green_enchanted_bookshelf = placeholder();
    public static final Block green_enchanted_door_bottom = placeholder();
    public static final Block green_enchanted_fence = placeholder();
    public static final Block green_enchanted_fence_gate = placeholder();
    public static final Block green_enchanted_stairs = placeholder();
    public static final Block green_enchanted_wood = placeholder();
    public static final Block green_enchanted_wood_wall = placeholder();
    public static final Block green_glowshroom = placeholder();
    public static final Block green_enchanted_log = placeholder();
    public static final Block green_enchanted_planks = placeholder();
    public static final Block green_enchanted_slab = placeholder();
    public static final Block green_tulip = placeholder();
    public static final Block guzmania = placeholder();
    public static final Block hardened_dirt = placeholder();
    public static final Block hawthorn_door_bottom = placeholder();
    public static final Block hawthorn_fence = placeholder();
    public static final Block hawthorn_fence_gate = placeholder();
    public static final Block hawthorn_leaves_flowering = placeholder();
    public static final Block hawthorn_stairs = placeholder();
    public static final Block hawthorn_wood = placeholder();
    public static final Block hawthorn_wood_wall = placeholder();
    public static final Block hawthorn_berry_leaves = placeholder();
    public static final Block hawthorn_bookshelf = placeholder();
    public static final Block hawthorn_leaves = placeholder();
    public static final Block hawthorn_log = placeholder();
    public static final Block hawthorn_planks = placeholder();
    public static final Block hawthorn_sapling = placeholder();
    public static final Block hawthorn_slab = placeholder();
    public static final Block holly_door_bottom = placeholder();
    public static final Block holly_fence = placeholder();
    public static final Block holly_fence_gate = placeholder();
    public static final Block holly_stairs = placeholder();
    public static final Block holly_wood = placeholder();
    public static final Block holly_wood_wall = placeholder();
    public static final Block holly_berry_leaves = placeholder();
    public static final Block holly_bookshelf = placeholder();
    public static final Block holly_leaves = placeholder();
    public static final Block holly_log = placeholder();
    public static final Block holly_planks = placeholder();
    public static final Block holly_sapling = placeholder();
    public static final Block holly_slab = placeholder();
    public static final Block horseweed = placeholder();
    public static final Block incan_lily = placeholder();
    public static final Block iris = placeholder();
    public static final Block ironwood_door_bottom = placeholder();
    public static final Block ironwood_fence = placeholder();
    public static final Block ironwood_fence_gate = placeholder();
    public static final Block ironwood_stairs = placeholder();
    public static final Block ironwood_wood = placeholder();
    public static final Block ironwood_wood_wall = placeholder();
    public static final Block ironwood_bookshelf = placeholder();
    public static final Block ironwood_leaves = placeholder();
    public static final Block ironwood_log = placeholder();
    public static final Block ironwood_planks = placeholder();
    public static final Block ironwood_sapling = placeholder();
    public static final Block ironwood_slab = placeholder();
    public static final Block ivy = placeholder();
    public static final Block jacaranda_door_bottom = placeholder();
    public static final Block jacaranda_fence = placeholder();
    public static final Block jacaranda_fence_gate = placeholder();
    public static final Block jacaranda_stairs = placeholder();
    public static final Block jacaranda_wood = placeholder();
    public static final Block jacaranda_wood_wall = placeholder();
    public static final Block jacaranda_bookshelf = placeholder();
    public static final Block jacaranda_leaves = placeholder();
    public static final Block jacaranda_log = placeholder();
    public static final Block jacaranda_planks = placeholder();
    public static final Block jacaranda_sapling = placeholder();
    public static final Block jacaranda_slab = placeholder();
    public static final Block jackomelon = placeholder();
    public static final Block japanese_orchid = placeholder();
    public static final Block kasai_block = placeholder();
    public static final Block kasai_ore = placeholder();
    public static final Block kovan = placeholder();
    public static final Block latharium_block = placeholder();
    public static final Block latharium_ore = placeholder();
    public static final Block lazarus_bell_flower = placeholder();
    public static final Block leafpile = placeholder();
    public static final Block leaf_pile_dead = placeholder();
    public static final Block light_blue_chiseled_sandstone = placeholder();
    public static final Block light_blue_crystal_block = placeholder();
    public static final Block light_blue_petal = placeholder();
    public static final Block light_blue_sand = placeholder();
    public static final Block light_blue_sandstone = placeholder();
    public static final Block light_blue_smooth_sandstone = placeholder();
    public static final Block lollipop_flower = placeholder();
    public static final Block magenta_amaranth = placeholder();
    public static final Block magenta_celosia = placeholder();
    public static final Block magenta_tulip = placeholder();
    public static final Block mahogany_bookshelf = placeholder();
    public static final Block mahogany_door_bottom = placeholder();
    public static final Block mahogany_fence = placeholder();
    public static final Block mahogany_fence_gate = placeholder();
    public static final Block mahogany_leaves = placeholder();
    public static final Block mahogany_log = placeholder();
    public static final Block mahogany_planks = placeholder();
    public static final Block mahogany_slab = placeholder();
    public static final Block mahogany_sapling = placeholder();
    public static final Block mahogany_stairs = placeholder();
    public static final Block mahogany_wood = placeholder();
    public static final Block mahogany_wood_wall = placeholder();
    public static final Block mangrove_door_bottom = placeholder();
    public static final Block mangrove_fence = placeholder();
    public static final Block mangrove_fence_gate = placeholder();
    public static final Block mangrove_stairs = placeholder();
    public static final Block mangrove_wood = placeholder();
    public static final Block mangrove_wood_wall = placeholder();
    public static final Block mangrove_bookshelf = placeholder();
    public static final Block mangrove_leaves = placeholder();
    public static final Block mangrove_log = placeholder();
    public static final Block mangrove_planks = placeholder();
    public static final Block mangrove_sapling = placeholder();
    public static final Block mangrove_slab = placeholder();
    public static final Block maple_door_bottom = placeholder();
    public static final Block maple_fence = placeholder();
    public static final Block maple_fence_gate = placeholder();
    public static final Block maple_stairs = placeholder();
    public static final Block maple_wood = placeholder();
    public static final Block maple_wood_wall = placeholder();
    public static final Block maple_bookshelf = placeholder();
    public static final Block maple_leaves_red = placeholder();
    public static final Block maple_leaves_silver = placeholder();
    public static final Block maple_log = placeholder();
    public static final Block maple_tap = placeholder();
    public static final Block sappy_maple_log = placeholder();
    public static final Block maple_planks = placeholder();
    public static final Block maple_slab = placeholder();
    public static final Block meadow_dirt = placeholder();
    public static final Block meadow_grass = placeholder();
    public static final Block medium_blue_glowshroom = placeholder();
    public static final Block medium_green_glowshroom = placeholder();
    public static final Block medium_purple_glowshroom = placeholder();
    public static final Block mini_cactus = placeholder();
    public static final Block mud_block = placeholder();
    public static final Block mud_bricks = placeholder();
    public static final Block nether_furnace = placeholder();
    public static final Block nether_furnace_lit = placeholder();
    public static final Block oak_leaves_dry_brown = placeholder();
    public static final Block oak_leaves_dry_green = placeholder();
    public static final Block oak_leaves_orange = placeholder();
    public static final Block oak_leaves_red = placeholder();
    public static final Block orange_amaranth = placeholder();
    public static final Block orange_birch_sapling = placeholder();
    public static final Block orange_birch_leaves = placeholder();
    public static final Block orange_celosia = placeholder();
    public static final Block orange_daisy = placeholder();
    public static final Block orange_oak_sapling = placeholder();
    public static final Block orange_spruce_sapling = placeholder();
    public static final Block orchard_leaves = placeholder();
    public static final Block orchard_leaves_apple = placeholder();
    public static final Block orchard_leaves_flowering = placeholder();
    public static final Block orchard_sapling = placeholder();
    public static final Block osiria_rose = placeholder();
    public static final Block overgrown_stone = placeholder();
    public static final Block palm_bookshelf = placeholder();
    public static final Block palm_door_bottom = placeholder();
    public static final Block palm_fence = placeholder();
    public static final Block palm_fence_gate = placeholder();
    public static final Block palm_leaves = placeholder();
    public static final Block palm_log = placeholder();
    public static final Block palm_planks = placeholder();
    public static final Block palm_slab = placeholder();
    public static final Block palm_sapling = placeholder();
    public static final Block palm_stairs = placeholder();
    public static final Block palm_wood = placeholder();
    public static final Block palm_wood_wall = placeholder();
    public static final Block palo_verde_wood = placeholder();
    public static final Block palo_verde_wood_wall = placeholder();
    public static final Block palo_verde_leaves = placeholder();
    public static final Block palo_verde_leaves_flowering = placeholder();
    public static final Block palo_verde_log = placeholder();
    public static final Block palo_verde_sapling = placeholder();
    public static final Block pasture_dirt = placeholder();
    public static final Block pasture_grass = placeholder();
    public static final Block peach_leather_flower = placeholder();
    public static final Block peat_dirt = placeholder();
    public static final Block peat_grass = placeholder();
    public static final Block pendorite_block = placeholder();
    public static final Block pendorite_ore = placeholder();
    public static final Block pine_door_bottom = placeholder();
    public static final Block pine_fence = placeholder();
    public static final Block pine_fence_gate = placeholder();
    public static final Block pine_stairs = placeholder();
    public static final Block pine_wood = placeholder();
    public static final Block pine_wood_wall = placeholder();
    public static final Block pine_bookshelf = placeholder();
    public static final Block pine_leaves = placeholder();
    public static final Block pine_log = placeholder();
    public static final Block pine_planks = placeholder();
    public static final Block pine_sapling = placeholder();
    public static final Block pine_slab = placeholder();
    public static final Block pink_allium_bush = placeholder();
    public static final Block pink_chiseled_sandstone = placeholder();
    public static final Block pink_enchanted_sapling = placeholder();
    public static final Block pink_sand = placeholder();
    public static final Block pink_sandstone = placeholder();
    public static final Block pink_smooth_sandstone = placeholder();
    public static final Block pink_allium = placeholder();
    public static final Block pink_anemone = placeholder();
    public static final Block pink_cherry_sapling = placeholder();
    public static final Block pink_daffodil = placeholder();
    public static final Block pink_orchid = placeholder();
    public static final Block pink_stellata_sapling = placeholder();
    public static final Block plant_stem = placeholder();
    public static final Block poison_ivy = placeholder();
    public static final Block polished_soapstone = placeholder();
    public static final Block polished_soapstone_stairs = placeholder();
    public static final Block polished_soapstone_walls = placeholder();
    public static final Block polished_sodalite = placeholder();
    public static final Block polished_sodalite_stairs = placeholder();
    public static final Block polished_sodalite_walls = placeholder();
    public static final Block prairie_grass = placeholder();
    public static final Block prairie_grass_tall = placeholder();
    public static final Block prickly_pear = placeholder();
    public static final Block protea_flower = placeholder();
    public static final Block purple_amaranth = placeholder();
    public static final Block purple_chiseled_sandstone = placeholder();
    public static final Block purple_crystal_block = placeholder();
    public static final Block purple_enchanted_sapling = placeholder();
    public static final Block purple_glowshroom = placeholder();
    public static final Block purple_petal = placeholder();
    public static final Block purple_sand = placeholder();
    public static final Block purple_sandstone = placeholder();
    public static final Block purple_smooth_sandstone = placeholder();
    public static final Block purple_celosia = placeholder();
    public static final Block purple_orchid = placeholder();
    public static final Block purple_sage = placeholder();
    public static final Block purple_tulip = placeholder();
    public static final Block rainbow_eucalyptus_fence = placeholder();
    public static final Block rainbow_eucalyptus_fence_gate = placeholder();
    public static final Block rainbow_eucalyptus_stairs = placeholder();
    public static final Block rainbow_eucalyptus_wood = placeholder();
    public static final Block rainbow_eucalyptus_wood_wall = placeholder();
    public static final Block rainbow_eucalyptus_bookshelf = placeholder();
    public static final Block rainbow_eucalyptus_leaves = placeholder();
    public static final Block rainbow_eucalyptus_log = placeholder();
    public static final Block rainbow_eucalyptus_planks = placeholder();
    public static final Block rainbow_eucalyptus_sapling = placeholder();
    public static final Block rainbow_eucalyptus_slab = placeholder();
    public static final Block red_birch_sapling = placeholder();
    public static final Block red_cracked_sand = placeholder();
    public static final Block red_crystal_block = placeholder();
    public static final Block red_petal = placeholder();
    public static final Block red_birch_leaves = placeholder();
    public static final Block red_celosia = placeholder();
    public static final Block red_corn_flower = placeholder();
    public static final Block red_maple_sapling = placeholder();
    public static final Block red_oak_sapling = placeholder();
    public static final Block red_orchid = placeholder();
    public static final Block red_spruce_sapling = placeholder();
    public static final Block redwood_fence = placeholder();
    public static final Block redwood_fence_gate = placeholder();
    public static final Block redwood_stairs = placeholder();
    public static final Block redwood_wood = placeholder();
    public static final Block redwood_wood_wall = placeholder();
    public static final Block redwood_bookshelf = placeholder();
    public static final Block redwood_leaves = placeholder();
    public static final Block redwood_log = placeholder();
    public static final Block redwood_planks = placeholder();
    public static final Block redwood_sapling = placeholder();
    public static final Block redwood_slab = placeholder();
    public static final Block reed = placeholder();
    public static final Block richea = placeholder();
    public static final Block rowan_wood_wall = placeholder();
    public static final Block rocky_grass = placeholder();
    public static final Block rocky_stone = placeholder();
    public static final Block rose = placeholder();
    public static final Block rowan_door_bottom = placeholder();
    public static final Block rowan_fence = placeholder();
    public static final Block rowan_fence_gate = placeholder();
    public static final Block rowan_stairs = placeholder();
    public static final Block rowan_wood = placeholder();
    public static final Block rowan_berry_leaves = placeholder();
    public static final Block rowan_bookshelf = placeholder();
    public static final Block rowan_leaves = placeholder();
    public static final Block rowan_log = placeholder();
    public static final Block rowan_planks = placeholder();
    public static final Block rowan_sapling = placeholder();
    public static final Block rowan_slab = placeholder();
    public static final Block rudo_stalk = placeholder();
    public static final Block sacred_datura = placeholder();
    public static final Block salal_bush = placeholder();
    public static final Block salal_bush_ripe = placeholder();
    public static final Block sandy_dirt = placeholder();
    public static final Block sandy_grass = placeholder();
    public static final Block scoria = placeholder();
    public static final Block scoria_bricks = placeholder();
    public static final Block scoria_pillars = placeholder();
    public static final Block scoria_stairs = placeholder();
    public static final Block scoria_walls = placeholder();
    public static final Block sepinite = placeholder();
    public static final Block sepinite_bricks = placeholder();
    public static final Block sepinite_pillars = placeholder();
    public static final Block sepinite_stairs = placeholder();
    public static final Block sepinite_tile = placeholder();
    public static final Block sepinite_walls = placeholder();
    public static final Block shelf_fungi = placeholder();
    public static final Block short_dead_grass = placeholder();
    public static final Block silver_apple_white_skyris_leaves = placeholder();
    public static final Block silver_vase_flower = placeholder();
    public static final Block silver_maple_sapling = placeholder();
    public static final Block skyris_door_bottom = placeholder();
    public static final Block skyris_fence = placeholder();
    public static final Block skyris_fence_gate = placeholder();
    public static final Block skyris_stairs = placeholder();
    public static final Block skyris_wood = placeholder();
    public static final Block skyris_wood_wall = placeholder();
    public static final Block skyris_bookshelf = placeholder();
    public static final Block skyris_leaves = placeholder();
    public static final Block skyris_leaves_green_apple = placeholder();
    public static final Block skyris_log = placeholder();
    public static final Block skyris_planks = placeholder();
    public static final Block skyris_sapling = placeholder();
    public static final Block skyris_slab = placeholder();
    public static final Block small_blue_glowshroom = placeholder();
    public static final Block small_green_glowshroom = placeholder();
    public static final Block small_purple_glowshroom = placeholder();
    public static final Block snowdrops = placeholder();
    public static final Block soapstone = placeholder();
    public static final Block soapstone_pillars = placeholder();
    public static final Block soapstone_tile = placeholder();
    public static final Block sodalite = placeholder();
    public static final Block sodalite_brick_stairs = placeholder();
    public static final Block sodalite_brick_walls = placeholder();
    public static final Block sodalite_bricks = placeholder();
    public static final Block sodalite_tile = placeholder();
    public static final Block sonoran_cactus = placeholder();
    public static final Block sonoran_cactus_flowering = placeholder();
    public static final Block springwater = placeholder();
    public static final Block spruce_leaves_blue = placeholder();
    public static final Block spruce_leaves_orange = placeholder();
    public static final Block spruce_leaves_red = placeholder();
    public static final Block spruce_leaves_yellow = placeholder();
    public static final Block stellata_leaves_pink = placeholder();
    public static final Block stellata_leaves_white = placeholder();
    public static final Block stone_pebbles = placeholder();
    public static final Block stone_spike = placeholder();
    public static final Block strawberry_bush = placeholder();
    public static final Block stripped_aspen_log = placeholder();
    public static final Block stripped_aspen_wood = placeholder();
    public static final Block stripped_baobab_log = placeholder();
    public static final Block stripped_baobab_wood = placeholder();
    public static final Block stripped_blue_enchanted_log = placeholder();
    public static final Block stripped_blue_enchanted_wood = placeholder();
    public static final Block stripped_cherry_log = placeholder();
    public static final Block stripped_cherry_wood = placeholder();
    public static final Block stripped_cika_log = placeholder();
    public static final Block stripped_cika_wood = placeholder();
    public static final Block stripped_cypress_log = placeholder();
    public static final Block stripped_cypress_wood = placeholder();
    public static final Block stripped_ebony_log = placeholder();
    public static final Block stripped_ebony_wood = placeholder();
    public static final Block stripped_great_oak_log = placeholder();
    public static final Block stripped_great_oak_wood = placeholder();
    public static final Block stripped_green_enchanted_log = placeholder();
    public static final Block stripped_green_enchanted_wood = placeholder();
    public static final Block stripped_hawthorn_log = placeholder();
    public static final Block stripped_hawthorn_wood = placeholder();
    public static final Block stripped_holly_log = placeholder();
    public static final Block stripped_holly_wood = placeholder();
    public static final Block stripped_ironwood_log = placeholder();
    public static final Block stripped_ironwood_wood = placeholder();
    public static final Block stripped_jacaranda_log = placeholder();
    public static final Block stripped_jacaranda_wood = placeholder();
    public static final Block stripped_mangrove_log = placeholder();
    public static final Block stripped_mangrove_wood = placeholder();
    public static final Block stripped_maple_log = placeholder();
    public static final Block stripped_maple_wood = placeholder();
    public static final Block stripped_palo_verde_log = placeholder();
    public static final Block stripped_palo_verde_wood = placeholder();
    public static final Block stripped_pine_log = placeholder();
    public static final Block stripped_pine_wood = placeholder();
    public static final Block stripped_rainbow_eucalyptus_log = placeholder();
    public static final Block stripped_rainbow_eucalyptus_wood = placeholder();
    public static final Block stripped_redwood_log = placeholder();
    public static final Block stripped_redwood_wood = placeholder();
    public static final Block stripped_rowan_log = placeholder();
    public static final Block stripped_rowan_wood = placeholder();
    public static final Block stripped_skyris_log = placeholder();
    public static final Block stripped_skyris_wood = placeholder();
    public static final Block stripped_willow_log = placeholder();
    public static final Block stripped_willow_wood = placeholder();
    public static final Block stripped_witch_hazel_log = placeholder();
    public static final Block stripped_witch_hazel_wood = placeholder();
    public static final Block stripped_zelkova_log = placeholder();
    public static final Block stripped_zelkova_wood = placeholder();
    public static final Block stripped_fir_log = placeholder();
    public static final Block stripped_fir_wood = placeholder();
    public static final Block tamrelite_block = placeholder();
    public static final Block tamrelite_ore = placeholder();
    public static final Block thorn_branches = placeholder();
    public static final Block thorn_block = placeholder();
    public static final Block tiny_lilypad = placeholder();
    public static final Block torch_ginger = placeholder();
    public static final Block violet_leather_flower = placeholder();
    public static final Block weeping_milk_cap = placeholder();
    public static final Block white_crystal_block = placeholder();
    public static final Block white_petal = placeholder();
    public static final Block white_skyris_leaves = placeholder();
    public static final Block white_anemone = placeholder();
    public static final Block white_celosia = placeholder();
    public static final Block white_cherry_sapling = placeholder();
    public static final Block white_chiseled_sandstone = placeholder();
    public static final Block white_sage = placeholder();
    public static final Block white_sand = placeholder();
    public static final Block white_sandstone = placeholder();
    public static final Block white_smooth_sandstone = placeholder();
    public static final Block white_stellata_sapling = placeholder();
    public static final Block wild_rudo = placeholder();
    public static final Block wild_strawberry = placeholder();
    public static final Block willow_door_bottom = placeholder();
    public static final Block willow_fence = placeholder();
    public static final Block willow_fence_gate = placeholder();
    public static final Block willow_stairs = placeholder();
    public static final Block willow_wood = placeholder();
    public static final Block willow_wood_wall = placeholder();
    public static final Block willow_bookshelf = placeholder();
    public static final Block willow_leaves = placeholder();
    public static final Block willow_log = placeholder();
    public static final Block willow_planks = placeholder();
    public static final Block willow_sapling = placeholder();
    public static final Block willow_slab = placeholder();
    public static final Block winter_rose = placeholder();
    public static final Block winter_succulent = placeholder();
    public static final Block winter_cyclamen = placeholder();
    public static final Block winter_scilla = placeholder();
    public static final Block witch_hazel_door = placeholder();
    public static final Block witch_hazel_fence = placeholder();
    public static final Block witch_hazel_fence_gate = placeholder();
    public static final Block witch_hazel_stairs = placeholder();
    public static final Block witch_hazel_wood = placeholder();
    public static final Block witch_hazel_wood_wall = placeholder();
    public static final Block witch_hazel_bookshelf = placeholder();
    public static final Block witch_hazel_leaves = placeholder();
    public static final Block witch_hazel_leaves_blooming = placeholder();
    public static final Block witch_hazel_log = placeholder();
    public static final Block witch_hazel_planks = placeholder();
    public static final Block witch_hazel_sapling = placeholder();
    public static final Block witch_hazel_slab = placeholder();
    public static final Block wood_blewit = placeholder();
    public static final Block yellow_birch_sapling = placeholder();
    public static final Block yellow_petal = placeholder();
    public static final Block yellow_celosia = placeholder();
    public static final Block yellow_daffodil = placeholder();
    public static final Block yellow_spruce_sapling = placeholder();
    public static final Block yellow_tulip = placeholder();
    public static final Block zelkova_door_bottom = placeholder();
    public static final Block zelkova_fence = placeholder();
    public static final Block zelkova_fence_gate = placeholder();
    public static final Block zelkova_stairs = placeholder();
    public static final Block zelkova_wood = placeholder();
    public static final Block zelkova_wood_wall = placeholder();
    public static final Block zelkova_bookshelf = placeholder();
    public static final Block zelkova_leaves = placeholder();
    public static final Block zelkova_log = placeholder();
    public static final Block zelkova_planks = placeholder();
    public static final Block zelkova_sapling = placeholder();
    public static final Block zelkova_slab = placeholder();

    private ModBlocks() {
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder() {
        return null;
    }

    private static Block createGeneratedSapling(String name, BlockGeneratedSaplingBase.TreePlacement[] placements,
                                                double bonemealSuccessChance, BiPredicate<World, BlockPos> growthCondition) {
        return new BlockGeneratedSaplingBase(name, placements, bonemealSuccessChance, growthCondition);
    }

    private static Block createGeneratedSapling(String name, BlockGeneratedSaplingBase.TreePlacement[] tickPlacements,
                                                double bonemealSuccessChance, BlockGeneratedSaplingBase.TreePlacement[] bonemealPlacements,
                                                BiPredicate<World, BlockPos> growthCondition) {
        return new BlockGeneratedSaplingBase(name, tickPlacements, bonemealSuccessChance, bonemealPlacements, growthCondition);
    }

    private static Block createGeneratedSapling(String name, BlockGeneratedSaplingBase.TreePlacement[] tickPlacements,
                                                double bonemealSuccessChance, BlockGeneratedSaplingBase.TreePlacement[] bonemealPlacements,
                                                BiPredicate<World, BlockPos> growthCondition,
                                                BlockGeneratedSaplingBase.SaplingFormation formation) {
        return new BlockGeneratedSaplingBase(name, tickPlacements, bonemealSuccessChance, bonemealPlacements, growthCondition,
                growthCondition, formation);
    }

    private static Block createGeneratedSapling(String name, BlockGeneratedSaplingBase.TreePlacement[] tickPlacements,
                                                double bonemealSuccessChance, BlockGeneratedSaplingBase.TreePlacement[] bonemealPlacements,
                                                BiPredicate<World, BlockPos> tickGrowthCondition,
                                                BiPredicate<World, BlockPos> bonemealGrowthCondition) {
        return new BlockGeneratedSaplingBase(name, tickPlacements, bonemealSuccessChance, bonemealPlacements,
                tickGrowthCondition, bonemealGrowthCondition);
    }

    private static Block createGeneratedSapling(String name, BlockGeneratedSaplingBase.TreePlacement[] tickPlacements,
                                                double bonemealSuccessChance, BlockGeneratedSaplingBase.TreePlacement[] bonemealPlacements,
                                                BiPredicate<World, BlockPos> tickGrowthCondition,
                                                BiPredicate<World, BlockPos> bonemealGrowthCondition,
                                                BlockGeneratedSaplingBase.SaplingFormation formation) {
        return new BlockGeneratedSaplingBase(name, tickPlacements, bonemealSuccessChance, bonemealPlacements,
                tickGrowthCondition, bonemealGrowthCondition, formation);
    }

    private static final ResourceLocation REMOVED_ROCKY_GRASS_ALPS = new ResourceLocation(BiomesYouGo.MODID, "rocky_grass_alps");
    private static final ResourceLocation RENAMED_PURPLE_SAGE = new ResourceLocation(BiomesYouGo.MODID, "purple_age");

    /** Remaps the removed Rocky Grass (Alps) block in old saves to regular Rocky Grass. */
    @SubscribeEvent
    public static void remapBlocks(RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getMappings()) {
            if (REMOVED_ROCKY_GRASS_ALPS.equals(mapping.key) && rocky_grass != null) {
                mapping.remap(rocky_grass);
            } else if (RENAMED_PURPLE_SAGE.equals(mapping.key) && purple_sage != null) {
                mapping.remap(purple_sage);
            }
        }
    }

    @SubscribeEvent
    public static void remapItems(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
            if (REMOVED_ROCKY_GRASS_ALPS.equals(mapping.key)) {
                Item item = Item.getItemFromBlock(rocky_grass);
                if (item != Items.AIR) {
                    mapping.remap(item);
                }
            } else if (RENAMED_PURPLE_SAGE.equals(mapping.key)) {
                Item item = Item.getItemFromBlock(purple_sage);
                if (item != Items.AIR) {
                    mapping.remap(item);
                }
            }
        }
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registerBlock(registry, new BlockSimpleFlowerBase("allium_bush", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("alpine_bellflower", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("amaranth", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("angelica", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBookshelfBase("aspen_bookshelf", 0, 1.0f, 8.0f, 2, false));
        registerBlock(registry, new BlockWoodDoorBase("aspen_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.aspen_door));

        registerBlock(registry, new BlockFenceBase("aspen_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("aspen_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockShearableLeavesBase("aspen_leaves", 0, () -> ModBlocks.aspen_leaves,
                () -> new ItemStack(ModBlocks.aspen_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("aspen_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("aspen_planks",  0, 2.0f, 10.0f, 255, false));
        registerBlock(registry, new BlockWoodSlabBase("aspen_slab", 0));

        registerBlock(registry, createGeneratedSapling("aspen_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.ASPEN, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.ASPEN_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockStairsBase("aspen_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("aspen_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("aspen_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockAspenstickpile());

        registerBlock(registry, new BlockSimpleFlowerBase("azalea", BYGTab.tab, 0.0f));
        if (Config.isBaobabContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("baobab_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.baobab_door));

            registerBlock(registry, new BlockFenceBase("baobab_fence", 0, 2.0f, 8.0f, true));

            registerBlock(registry, new BlockFenceGateBase("baobab_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("baobab_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 8.0f, true));

            registerBlock(registry, new BlockWoodWallBase("baobab_wall", 0, 2.0f, 10.0f, 0, false, false));

            registerBlock(registry, new BlockWoodBase("baobab_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockBookshelfBase("baobab_bookshelf", 0, 1.0f, 10.0f, 255, false));

            registerBlock(registry, new BlockBaobabFruit());

            registerBlock(registry, new BlockShearableLeavesBase("baobab_leaves", 0, () -> ModBlocks.baobab_leaves, () -> new ItemStack(ModBlocks.baobab_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("baobab_log", 0, true));

            registerBlock(registry, new BlockPlanksBase("baobab_planks",  0, 2.0f, 8.0f, 255, true));

            registerBlock(registry, createGeneratedSapling("baobab_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.BAOBAB, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

            registerBlock(registry, new BlockWoodSlabBase("baobab_slab", 0));
        }

        registerBlock(registry, new BlockSimpleFlowerBase("begonia", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockShearableLeavesBase("birch_leaves_yellow", 0, () -> ModBlocks.birch_leaves_yellow, () -> new ItemStack(ModBlocks.yellow_birch_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockSimpleFlowerBase("bistort", BYGTab.tab, 0.0f));

        registerBlock(registry, createStackingPlantBlock("black_puff", BYGTab.tab, 0.0f, EnumPlantType.Cave, 1, false, false, true, false,
                () -> new ItemStack(Item.getItemFromBlock(ModBlocks.black_puff), 1)));

        registerBlock(registry, new BlockAlgae());

        registerBlock(registry, new BlockSimpleFlowerBase("black_rose", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBasicBase("black_chiseled_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockFallingBase("black_sand", Material.SAND, SoundType.SAND, "shovel", 0, 2.0f, 10.0f, 0.0f, 255) {
            @Override
            public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
                return false;
            }
        });

        registerBlock(registry, new BlockBasicBase("black_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockBasicBase("black_smooth_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockBlanketWeed());
        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("blue_enchanted_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.blue_enchanted_door));

            registerBlock(registry, new BlockFenceBase("blue_enchanted_fence", 0, 2.0f, 10.0f, false));

            registerBlock(registry, new BlockFenceGateBase("blue_enchanted_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, createGeneratedSapling("blue_enchanted_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.BLUE_ENCHANTED_STEPPED, 0.35),
                    BlockGeneratedSaplingBase.tree(BygTrees.BLUE_ENCHANTED_TIERED, 0.5),
                    BlockGeneratedSaplingBase.tree(BygTrees.BLUE_ENCHANTED_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));

            registerBlock(registry, new BlockStairsBase("blue_enchanted_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockWoodBase("blue_enchanted_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("blue_enchanted_wood_wall", 0, 2.0f, 10.0f, 0, false, false));
        }

        registerBlock(registry, new BlockPetalBase("blue_petal"));

        registerBlock(registry, new BlockBlueberryBush());

        registerBlock(registry, createDirectionalGlowshroomBlock("blue_glowshroom", 0.8f, () -> ModItems.blue_glowshroom, 4, null));

        registerBlock(registry, new BlockSimpleFlowerBase("bluesage", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("blue_spruce_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.BLUE_SPRUCE, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.BLUE_SPRUCE_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, createGeneratedSapling("brown_birch_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.BROWN_BIRCH, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.BROWN_BIRCH_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));

        registerBlock(registry, new BlockShearableLeavesBase("brown_birch_leaves", 1, () -> ModBlocks.brown_birch_leaves, () -> new ItemStack(ModBlocks.brown_birch_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockSimpleFlowerBase("california_poppy", BYGTab.tab, 0.0f));

        registerBlock(registry, createFacingMelonBlock("carved_melon", 0.0f));

        registerBlock(registry, new BlockCattails());
        registerBlock(registry, new BlockWoodDoorBase("cherry_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.cherry_door));

        registerBlock(registry, new BlockFenceBase("cherry_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("cherry_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("cherry_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("cherry_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("cherry_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockBookshelfBase("cherry_bookshelf", 0, 1.0f, 8.0f, 255, false));

        registerBlock(registry, new BlockShearableLeavesBase("cherry_leaves_pink", 0, () -> ModBlocks.cherry_leaves_pink, () -> new ItemStack(ModBlocks.pink_cherry_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("cherry_leaves_white", 0, () -> ModBlocks.cherry_leaves_white, () -> new ItemStack(ModBlocks.white_cherry_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("cherry_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("cherry_planks",  0, 2.0f, 10.0f, 255, false));

        registerBlock(registry, new BlockWoodSlabBase("cherry_slab", 0));
        if (Config.isCikaContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("cika_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.cika_door));

            registerBlock(registry, new BlockFenceBase("cika_fence", 0, 2.0f, 10.0f, false));

            registerBlock(registry, new BlockFenceGateBase("cika_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("cika_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockWoodBase("cika_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("cika_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

            registerBlock(registry, new BlockBookshelfBase("cika_bookshelf", 0, 1.0f, 8.0f, 2, false));

            registerBlock(registry, new BlockShearableLeavesBase("cika_leaves", 0, () -> ModBlocks.cika_leaves, () -> new ItemStack(ModBlocks.cika_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("cika_log", 0, false));

            registerBlock(registry, new BlockPlanksBase("cika_planks",  0, 2.0f, 10.0f, 255, false));

            SaplingTreeGenerator cikaTree = BygTrees.CIKA;
            BlockGeneratedSaplingBase.TreePlacement[] cikaPlacements = new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree((world, random, center) -> cikaTree.growFromSaplings(world, random, center, ModBlocks.cika_sapling), 1.0)};
            registerBlock(registry, createGeneratedSapling("cika_sapling", cikaPlacements, 0.4, cikaPlacements,
                BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35), BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.59),
                BlockGeneratedSaplingBase.squareFormation(3)));

            registerBlock(registry, new BlockWoodSlabBase("cika_slab", 0));
        }

        registerBlock(registry, createFlatDirectionalBlock("clover", BlockRenderLayer.TRANSLUCENT, false, false));

        registerBlock(registry, createPlantableFallingBlock("cracked_sand", Material.SAND, SoundType.SAND, "shovel", 0, 1.0f, 5.0f, 0.0f, 255, true));

        registerBlock(registry, new BlockCrate());

        registerBlock(registry, new BlockSimpleFlowerBase("crocus", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("cyan_amaranth", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("cyan_rose", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("cyan_tulip", BYGTab.tab, 0.0f));
        if (Config.isCypressContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("cypress_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.cypress_door));

            registerBlock(registry, new BlockFenceBase("cypress_fence", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockFenceGateBase("cypress_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("cypress_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockWoodBase("cypress_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("cypress_wood_wall", 0, 2.0f, 10.0f, 0, true, true));

            registerBlock(registry, new BlockShearableLeavesBase("cypress_leaves", -1, () -> ModBlocks.cypress_leaves, () -> new ItemStack(ModBlocks.cypress_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("cypress_log", 0, true));

            registerBlock(registry, new BlockPlanksBase("cypress_planks",  0, 2.0f, 10.0f, 255, true));
            registerBlock(registry, new BlockWoodSlabBase("cypress_slab", 0));

            registerBlock(registry, createGeneratedSapling("cypress_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.CYPRESS, 0.5),
                    BlockGeneratedSaplingBase.tree(BygTrees.CYPRESS_TALL, 1.0)}, 0.4, new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.CYPRESS, 0.5),
                    BlockGeneratedSaplingBase.tree(BygTrees.CYPRESS_TALL, 1.0)}, BlockGeneratedSaplingBase.alwaysGrow()));
        }

        registerBlock(registry, new BlockSimpleFlowerBase("daffodil", BYGTab.tab, 0.0f));

        registerBlock(registry, createDeadGrassBlock("dead_grass"));

        registerBlock(registry, new BlockSimpleFlowerBase("delphinium", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("dry_brown_oak_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.DRY_BROWN_OAK, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.DRY_BROWN_OAK_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));

        registerBlock(registry, createGeneratedSapling("dry_green_oak_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.DRY_GREEN_OAK, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.DRY_GREEN_OAK_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));
        if (Config.isEbonyContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("ebony_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.ebony_door));

            registerBlock(registry, new BlockFenceBase("ebony_fence", 1, 2.0f, 8.0f, true));

            registerBlock(registry, new BlockFenceGateBase("ebony_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("ebony_stairs", Material.WOOD, SoundType.WOOD, "axe", 1, 2.0f, 8.0f, true));

            registerBlock(registry, new BlockWoodBase("ebony_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("ebony_wood_wall", 1, 2.0f, 10.0f, 0, true, true));

            registerBlock(registry, new BlockBookshelfBase("ebony_bookshelf", 1, 1.0f, 8.0f, 255, true));

            registerBlock(registry, new BlockShearableLeavesBase("ebony_leaves", 1, () -> ModBlocks.ebony_leaves, () -> new ItemStack(ModBlocks.ebony_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("ebony_log", 1, true));

            registerBlock(registry, new BlockPlanksBase("ebony_planks",  1, 2.0f, 8.0f, 255, true));

            registerBlock(registry, createGeneratedSapling("ebony_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.EBONY, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

            registerBlock(registry, new BlockWoodSlabBase("ebony_slab", 1));
        }

        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, new BlockBookshelfBase("enchanted_bookshelf", 0, 1.0f, 8.0f, 255, false));

            registerBlock(registry, new BlockShearableLeavesBase("enchanted_leaves_blue", 0, () -> ModBlocks.enchanted_leaves_blue, () -> new ItemStack(ModBlocks.blue_enchanted_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockShearableLeavesBase("enchanted_leaves_pink", 0, () -> ModBlocks.enchanted_leaves_pink, () -> new ItemStack(ModBlocks.pink_enchanted_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockShearableLeavesBase("enchanted_leaves_purple", 0, () -> ModBlocks.enchanted_leaves_purple, () -> new ItemStack(ModBlocks.purple_enchanted_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("enchanted_log", 0, false));

            registerBlock(registry, new BlockPlanksBase("enchanted_planks",  0, 2.0f, 10.0f, 255, false));

            registerBlock(registry, new BlockWoodSlabBase("enchanted_slab", 1));
        }
        registerBlock(registry, new BlockWoodDoorBase("eucalyptus_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.eucalyptus_door));

        registerBlock(registry, new BlockSimpleFlowerBase("fairy_slipper", BYGTab.tab, 0.8f));
        registerBlock(registry, new BlockWoodDoorBase("fir_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.fir_door));

        registerBlock(registry, new BlockFenceBase("fir_fence", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockFenceGateBase("fir_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("fir_stairs", Material.WOOD, SoundType.GROUND, "axe", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockWoodWallBase("fir_wood_wall", 1, 2.0f, 10.0f, 0, true, true));

        registerBlock(registry, new BlockBookshelfBase("fir_bookshelf", 1, 1.0f, 8.0f, 255, true));

        registerBlock(registry, new BlockSimpleFlowerBase("firecracker", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockShearableLeavesBase("fir_leaves", 1, () -> ModBlocks.fir_leaves, () -> new ItemStack(ModBlocks.fir_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("fir_log", 1, true));

        registerBlock(registry, new BlockPlanksBase("fir_planks",  1, 2.0f, 8.0f, 255, true));
        registerBlock(registry, new BlockWoodSlabBase("fir_slab", 1));

        registerBlock(registry, createGeneratedSapling("fir_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.FIR, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.FIR_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodBase("fir_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, createFlatDirectionalBlock("flowers", BlockRenderLayer.TRANSLUCENT, false, false));

        registerBlock(registry, new BlockSimpleFlowerBase("foxglove", BYGTab.tab, 0.2f));
        registerBlock(registry, new BlockWoodDoorBase("frozen_oak_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.frozen_oak_door));

        registerBlock(registry, new BlockFenceBase("frozen_oak_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("frozen_oak_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, createGeneratedSapling("frozen_oak_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(new FrozenOakTreeGenerator(), 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));

        registerBlock(registry, new BlockStairsBase("frozen_oak_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("frozen_oak_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("frozen_oak_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockBookshelfBase("frozen_oak_bookshelf", 0, 1.0f, 8.0f, 255, false));

        registerBlock(registry, new BlockShearableLeavesBase("frozen_oak_leaves", 0, () -> ModBlocks.frozen_oak_leaves, () -> new ItemStack(ModBlocks.frozen_oak_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("frozen_oak_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("frozen_oak_planks",  0, 2.0f, 10.0f, 255, false));
        registerBlock(registry, new BlockWoodSlabBase("frozen_oak_slab", 0));

        registerBlock(registry, new BlockBasicBase("glowcane_block_blue", Material.REDSTONE_LIGHT, SoundType.GLASS, "pickaxe", 0, 1.5f, 8.0f, 1.0f, 1, false));

        registerBlock(registry, new BlockBasicBase("glowcane_block_pink", Material.REDSTONE_LIGHT, SoundType.GLASS, "pickaxe", 1, 1.5f, 10.0f, 1.0f, 1, false));

        registerBlock(registry, new BlockBasicBase("glowcane_block_purple", Material.REDSTONE_LIGHT, SoundType.GLASS, "pickaxe", 1, 1.5f, 8.0f, 1.0f, 1, false));

        registerBlock(registry, new BlockBasicBase("glowcane_block_red", Material.REDSTONE_LIGHT, SoundType.GLASS, "pickaxe", 0, 1.5f, 8.0f, 1.0f, 1, false));

        registerBlock(registry, createStackingPlantBlock("glowcane_blue", null, 0.6f, EnumPlantType.Cave, 5, false, false, false, false,
                () -> new ItemStack(ModItems.glowcane_stalk_blue, 1)));

        registerBlock(registry, createStackingPlantBlock("glowcane_pink", null, 0.6f, EnumPlantType.Cave, 5, false, false, false, false,
                () -> new ItemStack(ModItems.glowcane_stalk_pink, 1)));

        registerBlock(registry, createStackingPlantBlock("glowcane_purple", null, 0.6f, EnumPlantType.Cave, 5, false, false, false, false,
                () -> new ItemStack(ModItems.glowcane_stalk_purple, 1)));

        registerBlock(registry, createStackingPlantBlock("glowcane_red", null, 0.6f, EnumPlantType.Cave, 5, false, false, false, false,
                () -> new ItemStack(ModItems.glowcane_stalk_red, 1)));

        registerBlock(registry, new BlockBasicBase("glowcelium", Material.GRASS, SoundType.PLANT, "shovel", 0, 1.0f, 10.0f, 0.5f, 255, true) {
            @Override
            public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
                return true;
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(new ItemStack(Blocks.DIRT, 1, 0));
            }
        });

        registerBlock(registry, createTranslucentDropBlock("glowshroom_block_blue", Material.LEAVES, 0.7f, true,
                () -> new ItemStack(ModItems.blue_glowshroom, 1)));

        registerBlock(registry, createTranslucentDropBlock("glowshroom_block_purple", Material.LEAVES, 0.7f, true,
                () -> new ItemStack(ModItems.purple_glowshroom, 1)));

        registerBlock(registry, createTranslucentDropBlock("glowshroom_stem_yellow", Material.WOOD, 0.6f, true,
                () -> new ItemStack(Blocks.AIR, 1)));

        registerBlock(registry, createStackingPlantBlock("golden_spined_cactus", BYGTab.tab, 0.0f, EnumPlantType.Desert, 1, false, false, false, true,
                () -> new ItemStack(Blocks.AIR, 1)));
        if (Config.isGreatOakContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("great_oak_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.great_oak_door));

            registerBlock(registry, new BlockFenceBase("great_oak_fence", 0, 2.0f, 10.0f, false));

            registerBlock(registry, new BlockFenceGateBase("great_oak_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("great_oak_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockWoodBase("great_oak_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("great_oak_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

            registerBlock(registry, new BlockBookshelfBase("great_oak_bookshelf", 0, 1.0f, 8.0f, 255, false));

            registerBlock(registry, new BlockShearableLeavesBase("great_oak_leaves", 0, () -> ModBlocks.great_oak_leaves, () -> new ItemStack(ModBlocks.great_oak_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("great_oak_log", 0, false));

            registerBlock(registry, new BlockPlanksBase("great_oak_planks",  0, 2.0f, 10.0f, 255, false));

            registerBlock(registry, createGeneratedSapling("great_oak_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.GREAT_OAK, 1.0)}, 0.4, new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.GREAT_OAK, 1.0)}, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.7, 0.89),
                    BlockGeneratedSaplingBase.squareFormation(3)));

            registerBlock(registry, new BlockWoodSlabBase("great_oak_slab", 0));
        }

        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, new BlockBookshelfBase("green_enchanted_bookshelf", 0, 1.0f, 8.0f, 255, false));
            registerBlock(registry, new BlockWoodDoorBase("green_enchanted_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.green_enchanted_door));

            registerBlock(registry, new BlockFenceBase("green_enchanted_fence", 1, 2.0f, 8.0f, true));

            registerBlock(registry, new BlockFenceGateBase("green_enchanted_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("green_enchanted_stairs", Material.WOOD, SoundType.WOOD, "axe", 1, 2.0f, 8.0f, true));

            registerBlock(registry, new BlockWoodBase("green_enchanted_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("green_enchanted_wood_wall", 1, 2.0f, 10.0f, 0, true, true));
        }

        registerBlock(registry, createDirectionalGlowshroomBlock("green_glowshroom", 0.8f, () -> ModItems.green_glowshroom, 4, null));

        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("green_enchanted_log", 1, true));
            registerBlock(registry, new BlockPlanksBase("green_enchanted_planks",  1, 2.0f, 8.0f, 255, true));
            registerBlock(registry, new BlockWoodSlabBase("green_enchanted_slab", 1));
        }

        registerBlock(registry, new BlockSimpleFlowerBase("green_tulip", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("guzmania", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBasicBase("hardened_dirt", Material.ROCK, SoundType.GROUND, "pickaxe", 1, 1.8f, 10.0f, 0.0f, 255, true));
        registerBlock(registry, new BlockWoodDoorBase("hawthorn_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.hawthorn_door));

        registerBlock(registry, new BlockFenceBase("hawthorn_fence", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockFenceGateBase("hawthorn_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockShearableLeavesBase("hawthorn_leaves_flowering", 1, () -> ModBlocks.hawthorn_leaves, () -> new ItemStack(ModBlocks.hawthorn_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockStairsBase("hawthorn_stairs", Material.WOOD, SoundType.WOOD, "axe", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockWoodBase("hawthorn_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("hawthorn_wood_wall", 1, 2.0f, 10.0f, 0, true, true));

        registerBlock(registry, new BlockFruitLeavesBase("hawthorn_berry_leaves", 1, () -> new ItemStack(ModItems.hawthorn_berries, 2)));

        registerBlock(registry, new BlockBookshelfBase("hawthorn_bookshelf", 1, 1.0f, 8.0f, 255, true));

        registerBlock(registry, new BlockShearableLeavesBase("hawthorn_leaves", 1, () -> ModBlocks.hawthorn_leaves, () -> new ItemStack(ModBlocks.hawthorn_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("hawthorn_log", 1, true));

        registerBlock(registry, new BlockPlanksBase("hawthorn_planks",  1, 2.0f, 8.0f, 255, true));

        registerBlock(registry, createGeneratedSapling("hawthorn_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(new HawthornTreeGenerator(), 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("hawthorn_slab", 1));
        registerBlock(registry, new BlockWoodDoorBase("holly_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.holly_door));

        registerBlock(registry, new BlockFenceBase("holly_fence", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockFenceGateBase("holly_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("holly_stairs", Material.WOOD, SoundType.WOOD, "axe", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockWoodBase("holly_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("holly_wood_wall", 0, 2.0f, 10.0f, 0, true, true));

        registerBlock(registry, new BlockFruitLeavesBase("holly_berry_leaves", 0, () -> new ItemStack(ModItems.holly_berries, 2)));

        registerBlock(registry, new BlockBookshelfBase("holly_bookshelf", 0, 1.0f, 8.0f, 255, true));

        registerBlock(registry, new BlockShearableLeavesBase("holly_leaves", 0, () -> ModBlocks.holly_leaves, () -> new ItemStack(ModBlocks.holly_sapling, 1), 0.03, 0));

        registerBlock(registry, new BlockDirectionalLogBase("holly_log", 0, true));

        registerBlock(registry, new BlockPlanksBase("holly_planks",  1, 2.0f, 8.0f, 255, true));

        registerBlock(registry, createGeneratedSapling("holly_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.HOLLY, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.HOLLY_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("holly_slab", 0));

        registerBlock(registry, new BlockConfiguredFlower("horseweed", BYGTab.tab, false, true, null, null));

        registerBlock(registry, new BlockSimpleFlowerBase("incan_lily", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("iris", BYGTab.tab, 0.0f));
        registerBlock(registry, new BlockWoodDoorBase("ironwood_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.ironwood_door));

        registerBlock(registry, new BlockFenceBase("ironwood_fence", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockFenceGateBase("ironwood_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("ironwood_stairs", Material.WOOD, SoundType.WOOD, "axe", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockWoodBase("ironwood_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("ironwood_wood_wall", 1, 2.0f, 10.0f, 0, true, true));

        registerBlock(registry, new BlockBookshelfBase("ironwood_bookshelf", 1, 1.0f, 8.0f, 255, true));

        registerBlock(registry, new BlockShearableLeavesBase("ironwood_leaves", 1, () -> ModBlocks.ironwood_leaves, () -> new ItemStack(ModBlocks.ironwood_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("ironwood_log", 1, true));

        registerBlock(registry, new BlockPlanksBase("ironwood_planks",  1, 2.0f, 8.0f, 255, true));

        registerBlock(registry, createGeneratedSapling("ironwood_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.IRONWOOD, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("ironwood_slab", 1));

        registerBlock(registry, new BlockIvy());
        registerBlock(registry, new BlockWoodDoorBase("jacaranda_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.jacaranda_door));

        registerBlock(registry, new BlockFenceBase("jacaranda_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("jacaranda_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("jacaranda_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("jacaranda_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("jacaranda_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockBookshelfBase("jacaranda_bookshelf", 0, 1.0f, 8.0f, 255, false));

        registerBlock(registry, new BlockShearableLeavesBase("jacaranda_leaves", 0, () -> ModBlocks.jacaranda_leaves,
                () -> new ItemStack(ModBlocks.jacaranda_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("jacaranda_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("jacaranda_planks",  0, 2.0f, 10.0f, 255, false));

        registerBlock(registry, createGeneratedSapling("jacaranda_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.JACARANDA, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.JACARANDA_LARGE, 1.0),
                BlockGeneratedSaplingBase.tree(BygTrees.JACARANDA_TALL, 0.6)}, 0.4, new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.JACARANDA, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.JACARANDA_LARGE, 1.0),
                BlockGeneratedSaplingBase.tree(BygTrees.JACARANDA_TALL, 0.6)}, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.7, 0.89)));

        registerBlock(registry, new BlockWoodSlabBase("jacaranda_slab", 0));

        registerBlock(registry, createFacingMelonBlock("jackomelon", 1.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("japanese_orchid", BYGTab.tab, 0.0f));

        if (Config.isOreContentEnabled("kasai")) {
            registerBlock(registry, createBeaconBaseBlock("kasai_block", Material.IRON, SoundType.METAL, "pickaxe", 4, 25.0f, 16.0f, 0.0f, 255, true));
            registerBlock(registry, createOreBlock("kasai_ore", 5, 25.0f, 16.0f, null));
        }

        registerBlock(registry, new BlockSimpleFlowerBase("kovan", BYGTab.tab, 0.0f));

        if (Config.isOreContentEnabled("latharium")) {
            registerBlock(registry, createBeaconBaseBlock("latharium_block", Material.IRON, SoundType.METAL, "pickaxe", 5, 3.5f, 16.0f, 0.0f, 255, false));
            registerBlock(registry, createOreBlock("latharium_ore", 5, 6.5f, 10.0f, () -> new ItemStack(ModItems.latharium_gem, 1)));
        }

        registerBlock(registry, new BlockSimpleFlowerBase("lazarus_bell_flower", BYGTab.tab, 0.0f));

        registerBlock(registry, createFlatDirectionalBlock("leafpile", BlockRenderLayer.CUTOUT_MIPPED, true, true));

        registerBlock(registry, createFlatDirectionalBlock("leaf_pile_dead", BlockRenderLayer.CUTOUT_MIPPED, true, true));

        registerBlock(registry, new BlockBasicBase("light_blue_chiseled_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, createCrystalBlock("light_blue_crystal_block", () -> ModItems.light_blue_crystals, 3, false));

        registerBlock(registry, new BlockPetalBase("light_blue_petal"));

        registerBlock(registry, new BlockFallingBase("light_blue_sand", Material.SAND, SoundType.SAND, "shovel", 0, 1.0f, 5.0f, 0.0f, 255));

        registerBlock(registry, new BlockBasicBase("light_blue_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockBasicBase("light_blue_smooth_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockSimpleFlowerBase("lollipop_flower", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("magenta_amaranth", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("magenta_celosia", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("magenta_tulip", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBookshelfBase("mahogany_bookshelf", 0, 1.0f, 8.0f, 2, false));
        registerBlock(registry, new BlockWoodDoorBase("mahogany_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.mahogany_door));

        registerBlock(registry, new BlockFenceBase("mahogany_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("mahogany_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockShearableLeavesBase("mahogany_leaves", 0, () -> ModBlocks.mahogany_leaves, () -> new ItemStack(ModBlocks.mahogany_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("mahogany_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("mahogany_planks",  0, 2.0f, 10.0f, 255, false));
        registerBlock(registry, new BlockWoodSlabBase("mahogany_slab", 0));

        registerBlock(registry, createGeneratedSapling("mahogany_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.MAHOGANY, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockStairsBase("mahogany_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("mahogany_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("mahogany_wood_wall", 0, 2.0f, 10.0f, 0, false, true));
        if (Config.isMangroveContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("mangrove_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.mangrove_door));

            registerBlock(registry, new BlockFenceBase("mangrove_fence", 0, 2.0f, 10.0f, false));

            registerBlock(registry, new BlockFenceGateBase("mangrove_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("mangrove_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockWoodBase("mangrove_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("mangrove_wood_wall", 0, 2.0f, 10.0f, 0, true, true));

            registerBlock(registry, new BlockBookshelfBase("mangrove_bookshelf", 0, 1.0f, 8.0f, 255, false));

            registerBlock(registry, new BlockShearableLeavesBase("mangrove_leaves", 0, () -> ModBlocks.mangrove_leaves,
                    () -> new ItemStack(ModBlocks.mangrove_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("mangrove_log", 0, true));

            registerBlock(registry, new BlockPlanksBase("mangrove_planks",  0, 2.0f, 10.0f, 255, false));

            registerBlock(registry, createGeneratedSapling("mangrove_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.MANGROVE, 0.5),
                    BlockGeneratedSaplingBase.tree(BygTrees.MANGROVE_LARGE, 1.0)},
                    0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.7, 0.85)));

            registerBlock(registry, new BlockWoodSlabBase("mangrove_slab", 0));
        }
        registerBlock(registry, new BlockWoodDoorBase("maple_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.maple_door));

        registerBlock(registry, new BlockFenceBase("maple_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("maple_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("maple_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("maple_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("maple_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockBookshelfBase("maple_bookshelf", 0, 1.0f, 8.0f, 255, false));

        registerBlock(registry, new BlockShearableLeavesBase("maple_leaves_red", 0, () -> ModBlocks.maple_leaves_red, () -> new ItemStack(ModBlocks.red_maple_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("maple_leaves_silver", 0, () -> ModBlocks.maple_leaves_silver, () -> new ItemStack(ModBlocks.silver_maple_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("maple_log", 0, false));
        registerBlock(registry, new windanesz.byg.blocks.BlockSappyMapleLog());
        registerBlock(registry, new windanesz.byg.blocks.BlockMapleTap());

        registerBlock(registry, new BlockPlanksBase("maple_planks",  0, 2.0f, 10.0f, 255, false));

        registerBlock(registry, new BlockWoodSlabBase("maple_slab", 0));

        registerBlock(registry, new BlockBasicBase("meadow_dirt", Material.GROUND, SoundType.GROUND, "shovel", 0, 0.5f, 8.0f, 0.0f, 255, false) {
            @Override
            public int tickRate(World world) {
                return 900;
            }
        });

        registerBlock(registry, new BlockBasicBase("meadow_grass", Material.GRASS, SoundType.PLANT, "shovel", 0, 0.6f, 8.0f, 0.0f, 255, true) {
            @Override
            public int tickRate(World world) {
                return 900;
            }

            @Override
            public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
                return true;
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(new ItemStack(ModBlocks.meadow_dirt, 1));
            }
        });

        registerBlock(registry, createDirectionalGlowshroomBlock("medium_blue_glowshroom", 0.65f, () -> ModItems.blue_glowshroom, 3,
                () -> ModBlocks.blue_glowshroom));

        registerBlock(registry, createDirectionalGlowshroomBlock("medium_green_glowshroom", 0.65f, () -> ModItems.green_glowshroom, 3,
                () -> ModBlocks.green_glowshroom));

        registerBlock(registry, createDirectionalGlowshroomBlock("medium_purple_glowshroom", 0.65f, () -> ModItems.purple_glowshroom, 3,
                () -> ModBlocks.purple_glowshroom));

        registerBlock(registry, new BlockMiniCactus());

        registerBlock(registry, new BlockMud());

        registerBlock(registry, createDroppingBasicBlock("mud_bricks", Material.CLAY, SoundType.GROUND, "shovel", 0, 0.8f, 12.0f, 0.0f, 255, false,
                () -> new ItemStack(ModItems.mud_balls, 4)));

        registerBlock(registry, new BlockNetherFurnace());

        registerBlock(registry, new BlockNetherFurnaceLit());

        registerBlock(registry, new BlockShearableLeavesBase("oak_leaves_dry_brown", 0, () -> ModBlocks.oak_leaves_dry_brown, () -> new ItemStack(ModBlocks.dry_brown_oak_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("oak_leaves_dry_green", 0, () -> ModBlocks.oak_leaves_dry_green, () -> new ItemStack(ModBlocks.dry_green_oak_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("oak_leaves_orange", 0, () -> ModBlocks.oak_leaves_orange, () -> new ItemStack(ModBlocks.orange_oak_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("oak_leaves_red", 0, () -> ModBlocks.oak_leaves_red, () -> new ItemStack(ModBlocks.red_oak_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockSimpleFlowerBase("orange_amaranth", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("orange_birch_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.ORANGE_BIRCH, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.ORANGE_BIRCH_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));

        registerBlock(registry, new BlockShearableLeavesBase("orange_birch_leaves", 1, () -> ModBlocks.orange_birch_leaves, () -> new ItemStack(ModBlocks.orange_birch_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockSimpleFlowerBase("orange_celosia", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("orange_daisy", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("orange_oak_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.ORANGE_OAK, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.ORANGE_OAK_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.2, 0.7)));

        registerBlock(registry, createGeneratedSapling("orange_spruce_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.ORANGE_SPRUCE, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.ORANGE_SPRUCE_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockShearableLeavesBase("orchard_leaves", 0, () -> ModBlocks.orchard_leaves, () -> new ItemStack(net.minecraft.init.Blocks.AIR, 1), 0.0, 1));

        registerBlock(registry, new BlockFruitLeavesBase("orchard_leaves_apple", 0, () -> new ItemStack(Items.APPLE, 1)));

        registerBlock(registry, new BlockShearableLeavesBase("orchard_leaves_flowering", 0, () -> ModBlocks.orchard_leaves_flowering, () -> new ItemStack(ModBlocks.orchard_sapling, 1), 0.03, 1));

        registerBlock(registry, createGeneratedSapling("orchard_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.ORCHARD, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockSimpleFlowerBase("osiria_rose", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBasicBase("overgrown_stone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 2.0f, 10.0f, 0.0f, 255, true) {
            @Override
            public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
                return true;
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(new ItemStack(Blocks.COBBLESTONE, 1));
            }
        });

        if (Config.isPalmContentEnabled()) {
            registerBlock(registry, new BlockBookshelfBase("palm_bookshelf", 0, 1.0f, 8.0f, 2, false));
            registerBlock(registry, new BlockWoodDoorBase("palm_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.palm_door));

            registerBlock(registry, new BlockFenceBase("palm_fence", 0, 2.0f, 10.0f, false));

            registerBlock(registry, new BlockFenceGateBase("palm_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockShearableLeavesBase("palm_leaves", 0, () -> ModBlocks.palm_leaves, () -> new ItemStack(ModBlocks.palm_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("palm_log", 0, false));

            registerBlock(registry, new BlockPlanksBase("palm_planks",  0, 2.0f, 10.0f, 255, false));
            registerBlock(registry, new BlockWoodSlabBase("palm_slab", 0));

            registerBlock(registry, createGeneratedSapling("palm_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.PALM, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

            registerBlock(registry, new BlockStairsBase("palm_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockWoodBase("palm_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("palm_wood_wall", 0, 2.0f, 10.0f, 0, false, true));
        }

        registerBlock(registry, new BlockWoodBase("palo_verde_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("palo_verde_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockShearableLeavesBase("palo_verde_leaves", 0, () -> ModBlocks.palo_verde_leaves, () -> new ItemStack(net.minecraft.init.Blocks.AIR, 1), 0.0, 1));

        registerBlock(registry, new BlockShearableLeavesBase("palo_verde_leaves_flowering", 0, () -> ModBlocks.palo_verde_leaves_flowering, () -> new ItemStack(ModBlocks.palo_verde_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("palo_verde_log", 0, false));

        registerBlock(registry, createGeneratedSapling("palo_verde_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.PALO_VERDE, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockBasicBase("pasture_dirt", Material.GROUND, SoundType.GROUND, "shovel", 0, 0.5f, 8.0f, 0.0f, 255, true) {
            @Override
            public int tickRate(World world) {
                return 900;
            }
        });

        registerBlock(registry, new BlockBasicBase("pasture_grass", Material.GRASS, SoundType.PLANT, "shovel", 0, 0.6f, 8.0f, 0.0f, 255, true) {
            @Override
            public int tickRate(World world) {
                return 900;
            }

            @Override
            public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
                return true;
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(new ItemStack(ModBlocks.pasture_dirt, 1));
            }
        });

        registerBlock(registry, new BlockSimpleFlowerBase("peach_leather_flower", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBasicBase("peat_dirt", Material.GROUND, SoundType.GROUND, "shovel", 0, 0.5f, 8.0f, 0.0f, 255, false) {
            @Override
            public int tickRate(World world) {
                return 900;
            }

            @Override
            public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
                return true;
            }
        });

        registerBlock(registry, new BlockPeatgrass());

        if (Config.isOreContentEnabled("pendorite")) {
            registerBlock(registry, createBeaconBaseBlock("pendorite_block", Material.IRON, SoundType.METAL, "pickaxe", 4, 3.0f, 16.0f, 0.0f, 255, false));
            registerBlock(registry, createOreBlock("pendorite_ore", 4, 3.0f, 25.0f, () -> new ItemStack(ModItems.pendorite_gem, 1)));
        }
        registerBlock(registry, new BlockWoodDoorBase("pine_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.pine_door));

        registerBlock(registry, new BlockFenceBase("pine_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("pine_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("pine_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("pine_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("pine_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockBookshelfBase("pine_bookshelf", 0, 1.0f, 8.0f, 255, false));

        registerBlock(registry, new BlockShearableLeavesBase("pine_leaves", 0, () -> ModBlocks.pine_leaves, () -> new ItemStack(ModBlocks.pine_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("pine_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("pine_planks",  0, 2.0f, 10.0f, 255, false));

        registerBlock(registry, createGeneratedSapling("pine_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(new PineTreeGenerator(PineTreeGenerator.Size.SAPLING), 1.0)},
                0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("pine_slab", 0));

        registerBlock(registry, new BlockSimpleFlowerBase("pink_allium_bush", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBasicBase("pink_chiseled_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, createGeneratedSapling("pink_enchanted_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.PINK_ENCHANTED_STEPPED, 0.35),
                    BlockGeneratedSaplingBase.tree(BygTrees.PINK_ENCHANTED_TIERED, 0.5),
                    BlockGeneratedSaplingBase.tree(BygTrees.PINK_ENCHANTED_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));
        }

        registerBlock(registry, new BlockFallingBase("pink_sand", Material.SAND, SoundType.SAND, "shovel", 0, 1.0f, 5.0f, 0.0f, 255));

        registerBlock(registry, new BlockBasicBase("pink_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockBasicBase("pink_smooth_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockSimpleFlowerBase("pink_allium", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("pink_anemone", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("pink_cherry_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.PINK_CHERRY, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.PINK_CHERRY_LARGE, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.7, 0.89)));

        registerBlock(registry, new BlockSimpleFlowerBase("pink_daffodil", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("pink_orchid", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("pink_stellata_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(new StellataTreeGenerator(false), 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockDirectionalLogBase("plant_stem", 0, false));

        registerBlock(registry, createPoisonFlowerBlock("poison_ivy"));

        registerBlock(registry, new BlockBasicBase("polished_soapstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockStairsBase("polished_soapstone_stairs", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, false));

        registerBlock(registry, new BlockStoneWallBase("polished_soapstone_walls", 1, 1.5f, 30.0f, true));

        registerBlock(registry, new BlockBasicBase("polished_sodalite", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockStairsBase("polished_sodalite_stairs", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, false));

        registerBlock(registry, new BlockStoneWallBase("polished_sodalite_walls", 1, 1.5f, 30.0f, true));

        registerBlock(registry, new BlockShearableGrass("prairie_grass", BYGTab.tab, false, false));

        registerBlock(registry, new BlockTallPrairieGrass());

        registerBlock(registry, createStackingPlantBlock("prickly_pear", BYGTab.tab, 0.0f, EnumPlantType.Desert, 1, false, false, false, true,
                () -> new ItemStack(Blocks.AIR, 1)));

        registerBlock(registry, new BlockSimpleFlowerBase("protea_flower", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("purple_amaranth", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBasicBase("purple_chiseled_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, createCrystalBlock("purple_crystal_block", () -> ModItems.purple_crystals, 3, false));

        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, createGeneratedSapling("purple_enchanted_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                    BlockGeneratedSaplingBase.tree(BygTrees.PURPLE_ENCHANTED_STEPPED, 0.35),
                    BlockGeneratedSaplingBase.tree(BygTrees.PURPLE_ENCHANTED_TIERED, 0.5),
                    BlockGeneratedSaplingBase.tree(BygTrees.PURPLE_ENCHANTED_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));
        }

        registerBlock(registry, createDirectionalGlowshroomBlock("purple_glowshroom", 0.8f, () -> ModItems.purple_glowshroom, 4, null));

        registerBlock(registry, new BlockPetalBase("purple_petal"));

        registerBlock(registry, new BlockFallingBase("purple_sand", Material.SAND, SoundType.SAND, "shovel", 0, 1.0f, 5.0f, 0.0f, 255));

        registerBlock(registry, new BlockBasicBase("purple_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockBasicBase("purple_smooth_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockSimpleFlowerBase("purple_celosia", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("purple_orchid", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("purple_sage", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("purple_tulip", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockFenceBase("rainbow_eucalyptus_fence", 0, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockFenceGateBase("rainbow_eucalyptus_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("rainbow_eucalyptus_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockWoodBase("rainbow_eucalyptus_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("rainbow_eucalyptus_wood_wall", 0, 2.0f, 10.0f, 0, true, true));

        registerBlock(registry, new BlockBookshelfBase("rainbow_eucalyptus_bookshelf", 0, 1.0f, 8.0f, 255, true));

        registerBlock(registry, new BlockShearableLeavesBase("rainbow_eucalyptus_leaves", -1, () -> ModBlocks.rainbow_eucalyptus_leaves, () -> new ItemStack(ModBlocks.rainbow_eucalyptus_sapling, 1), 0.03, 0, 0.0f, net.minecraft.util.BlockRenderLayer.CUTOUT_MIPPED, false));

        registerBlock(registry, new BlockRainboweucalyptuslog());

        registerBlock(registry, new BlockPlanksBase("rainbow_eucalyptus_planks",  0, 2.0f, 8.0f, 255, true));

        registerBlock(registry, createGeneratedSapling("rainbow_eucalyptus_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.RAINBOW_EUCALYPTUS, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.RAINBOW_EUCALYPTUS_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("rainbow_eucalyptus_slab", 0));

        registerBlock(registry, createGeneratedSapling("red_birch_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.RED_BIRCH, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.RED_BIRCH_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));

        registerBlock(registry, createPlantableFallingBlock("red_cracked_sand", Material.SAND, SoundType.SAND, "shovel", 0, 1.0f, 5.0f, 0.0f, 255, true));

        registerBlock(registry, createCrystalBlock("red_crystal_block", () -> ModItems.red_crystals, 1, true));

        registerBlock(registry, new BlockPetalBase("red_petal"));

        registerBlock(registry, new BlockShearableLeavesBase("red_birch_leaves", 1, () -> ModBlocks.red_birch_leaves, () -> new ItemStack(ModBlocks.red_birch_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockSimpleFlowerBase("red_celosia", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("red_corn_flower", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("red_maple_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.RED_MAPLE, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.RED_MAPLE_LARGE, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.7, 0.89)));

        registerBlock(registry, createGeneratedSapling("red_oak_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.RED_OAK, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.RED_OAK_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.2, 0.7)));

        registerBlock(registry, new BlockSimpleFlowerBase("red_orchid", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("red_spruce_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.RED_SPRUCE, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.RED_SPRUCE_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        if (Config.isRedwoodContentEnabled()) {
            registerBlock(registry, new BlockFenceBase("redwood_fence", 0, 2.0f, 8.0f, false));

            registerBlock(registry, new BlockFenceGateBase("redwood_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("redwood_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 8.0f, true));

            registerBlock(registry, new BlockWoodBase("redwood_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("redwood_wood_wall", 0, 1.0f, 10.0f, 0, false, true));

            registerBlock(registry, new BlockBookshelfBase("redwood_bookshelf", 0, 1.0f, 8.0f, 255, false));

            registerBlock(registry, new BlockShearableLeavesBase("redwood_leaves", 0, () -> ModBlocks.redwood_leaves, () -> new ItemStack(ModBlocks.redwood_sapling, 1), 0.03, 1));

            registerBlock(registry, new BlockDirectionalLogBase("redwood_log", 0, false));

            registerBlock(registry, new BlockPlanksBase("redwood_planks",  0, 2.0f, 8.0f, 255, false));

            registerBlock(registry, createGeneratedSapling("redwood_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(new RedwoodTreeGenerator()::growFromSaplings, 1.0)}, 0.4,
                new BlockGeneratedSaplingBase.TreePlacement[]{
                        BlockGeneratedSaplingBase.tree(new RedwoodTreeGenerator()::growFromSaplings, 1.0)},
                BlockGeneratedSaplingBase.alwaysGrow(), BlockGeneratedSaplingBase.squareFormation(3)));

            registerBlock(registry, new BlockWoodSlabBase("redwood_slab", 0));
        }

        registerBlock(registry, new BlockReed());

        registerBlock(registry, new BlockSimpleFlowerBase("richea", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockWoodWallBase("rowan_wood_wall", 1, 2.0f, 10.0f, 0, true, true));

        registerBlock(registry, new BlockRockyGrass());

        registerBlock(registry, new BlockRockystone());

        registerBlock(registry, new BlockSimpleFlowerBase("rose", BYGTab.tab, 0.0f));
        registerBlock(registry, new BlockWoodDoorBase("rowan_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.rowan_door));

        registerBlock(registry, new BlockFenceBase("rowan_fence", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockFenceGateBase("rowan_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("rowan_stairs", Material.WOOD, SoundType.WOOD, "axe", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockWoodBase("rowan_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockFruitLeavesBase("rowan_berry_leaves", 1, () -> new ItemStack(ModItems.rowan_berries, 2)));

        registerBlock(registry, new BlockBookshelfBase("rowan_bookshelf", 1, 1.0f, 8.0f, 255, true));

        registerBlock(registry, new BlockShearableLeavesBase("rowan_leaves", 1, () -> ModBlocks.rowan_leaves, () -> new ItemStack(ModBlocks.rowan_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("rowan_log", 1, true));

        registerBlock(registry, new BlockPlanksBase("rowan_planks",  1, 2.0f, 8.0f, 255, true));

        registerBlock(registry, createGeneratedSapling("rowan_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(new RowanTreeGenerator(), 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("rowan_slab", 1));

        registerBlock(registry, new BlockRudostalk());

        registerBlock(registry, new BlockSimpleFlowerBase("sacred_datura", BYGTab.tab, 0.6f));

        registerBlock(registry, createTransitionFlowerBlock("salal_bush", "flowerLightPink", BYGTab.tab, 0.3, () -> ModBlocks.salal_bush_ripe));

        registerBlock(registry, createHarvestFlowerBlock("salal_bush_ripe", null, null, () -> ModBlocks.salal_bush,
                () -> new ItemStack(ModItems.salal_berry, 2)));

        registerBlock(registry, new BlockBasicBase("sandy_dirt", Material.GROUND, SoundType.GROUND, "shovel", 1, 0.5f, 8.0f, 0.0f, 255, false) {
            @Override
            public int tickRate(World world) {
                return 900;
            }
        });

        registerBlock(registry, new BlockSandygrass());

        registerBlock(registry, new BlockBasicBase("scoria", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockBasicBase("scoria_bricks", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, createDirectionalPillarBlock("scoria_pillars"));

        registerBlock(registry, new BlockStairsBase("scoria_stairs", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, false));

        registerBlock(registry, new BlockStoneWallBase("scoria_walls", 1, 1.5f, 30.0f, true));

        registerBlock(registry, new BlockBasicBase("sepinite", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockBasicBase("sepinite_bricks", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, createDirectionalPillarBlock("sepinite_pillars"));

        registerBlock(registry, new BlockStairsBase("sepinite_stairs", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, false));

        registerBlock(registry, new BlockBasicBase("sepinite_tile", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockStoneWallBase("sepinite_walls", 1, 1.5f, 30.0f, true));

        registerBlock(registry, new BlockShelfFungi());

        registerBlock(registry, createDeadGrassBlock("short_dead_grass"));

        if (Config.isSkyrisContentEnabled()) {
            registerBlock(registry, new BlockFruitLeavesBase("silver_apple_white_skyris_leaves", 0, () -> new ItemStack(ModItems.silver_apple, 1)));
        }

        registerBlock(registry, new BlockSimpleFlowerBase("silver_vase_flower", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("silver_maple_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.SILVER_MAPLE, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.SILVER_MAPLE_LARGE, 1.0)}, 0.4, new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.SILVER_MAPLE, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.SILVER_MAPLE_LARGE, 1.0)},
                BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35), BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.7, 0.89)));
        if (Config.isSkyrisContentEnabled()) {
            registerBlock(registry, new BlockWoodDoorBase("skyris_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.skyris_door));

            registerBlock(registry, new BlockFenceBase("skyris_fence", 0, 2.0f, 10.0f, false));

            registerBlock(registry, new BlockFenceGateBase("skyris_fence_gate", 1, 1.0f, 10.0f));

            registerBlock(registry, new BlockStairsBase("skyris_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

            registerBlock(registry, new BlockWoodBase("skyris_wood", 1, 2.0f, 10.0f, 255, true));

            registerBlock(registry, new BlockWoodWallBase("skyris_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

            registerBlock(registry, new BlockBookshelfBase("skyris_bookshelf", 0, 1.0f, 8.0f, 255, false));

            registerBlock(registry, createAltitudeSwapShearableLeavesBlock("skyris_leaves", () -> ModBlocks.white_skyris_leaves,
                () -> ModBlocks.skyris_leaves, () -> new ItemStack(ModBlocks.skyris_sapling, 1), 0.03));

            registerBlock(registry, createAltitudeSwapFruitLeavesBlock("skyris_leaves_green_apple",
                () -> ModBlocks.silver_apple_white_skyris_leaves, () -> new ItemStack(ModItems.green_apple, 1)));

            registerBlock(registry, new BlockDirectionalLogBase("skyris_log", 0, false));

            registerBlock(registry, new BlockPlanksBase("skyris_planks",  0, 2.0f, 10.0f, 255, false));

            registerBlock(registry, createGeneratedSapling("skyris_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.SKYRIS, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.SKYRIS_TALL, 1.0)}, 0.4, new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.SKYRIS, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.SKYRIS_TALL, 1.0)}, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(-0.5, 0.35)));

            registerBlock(registry, new BlockWoodSlabBase("skyris_slab", 1));
        }

        registerBlock(registry, createDirectionalGlowshroomBlock("small_blue_glowshroom", 0.5f, () -> ModItems.blue_glowshroom, 2,
                () -> ModBlocks.medium_blue_glowshroom));

        registerBlock(registry, createDirectionalGlowshroomBlock("small_green_glowshroom", 0.5f, () -> ModItems.green_glowshroom, 2,
                () -> ModBlocks.medium_green_glowshroom));

        registerBlock(registry, createDirectionalGlowshroomBlock("small_purple_glowshroom", 0.5f, () -> ModItems.purple_glowshroom, 2,
                () -> ModBlocks.medium_purple_glowshroom));

        registerBlock(registry, new BlockSimpleFlowerBase("snowdrops", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockBasicBase("soapstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, createDirectionalPillarBlock("soapstone_pillars"));

        registerBlock(registry, new BlockBasicBase("soapstone_tile", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockSodalite());

        registerBlock(registry, new BlockStairsBase("sodalite_brick_stairs", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, false));

        registerBlock(registry, new BlockStoneWallBase("sodalite_brick_walls", 1, 1.5f, 30.0f, true));

        registerBlock(registry, new BlockBasicBase("sodalite_bricks", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockBasicBase("sodalite_tile", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 30.0f, 0.0f, 255, false));

        registerBlock(registry, new BlockSonoranCactus("sonoran_cactus"));

        registerBlock(registry, new BlockSonoranCactus("sonoran_cactus_flowering"));

        registerBlock(registry, new BlockSpringwater());

        registerBlock(registry, new BlockShearableLeavesBase("spruce_leaves_blue", 0, () -> ModBlocks.spruce_leaves_blue, () -> new ItemStack(ModBlocks.blue_spruce_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("spruce_leaves_orange", 0, () -> ModBlocks.spruce_leaves_orange, () -> new ItemStack(ModBlocks.orange_spruce_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("spruce_leaves_red", 0, () -> ModBlocks.spruce_leaves_red, () -> new ItemStack(ModBlocks.red_spruce_sapling, 1), 0.09, 1));

        registerBlock(registry, new BlockShearableLeavesBase("spruce_leaves_yellow", 0, () -> ModBlocks.spruce_leaves_yellow, () -> new ItemStack(ModBlocks.yellow_spruce_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("stellata_leaves_pink", 0, () -> ModBlocks.stellata_leaves_pink, () -> new ItemStack(ModBlocks.pink_stellata_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("stellata_leaves_white", 0, () -> ModBlocks.stellata_leaves_white, () -> new ItemStack(ModBlocks.white_stellata_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockStonePebbles());

        registerBlock(registry, createCutoutDropBlock("stone_spike", Material.ROCK, SoundType.STONE, "pickaxe", 1, 2.0f, 10.0f,
                new AxisAlignedBB(5.0 / 16.0, 0.0, 5.0 / 16.0, 11.0 / 16.0, 31.0 / 16.0, 11.0 / 16.0),
                () -> new ItemStack(Blocks.COBBLESTONE, 1)));

        registerBlock(registry, new BlockStrawberryBush());

        registerBlock(registry, new BlockDirectionalLogBase("stripped_aspen_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_aspen_wood", 1, 2.0f, 10.0f, 255, true));

        if (Config.isBaobabContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_baobab_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_baobab_wood", 1, 2.0f, 10.0f, 255, true));
        }

        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_blue_enchanted_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_blue_enchanted_wood", 1, 2.0f, 10.0f, 255, true));
        }

        registerBlock(registry, new BlockDirectionalLogBase("stripped_cherry_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_cherry_wood", 1, 2.0f, 10.0f, 255, true));

        if (Config.isCikaContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_cika_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_cika_wood", 1, 2.0f, 10.0f, 255, true));
        }

        if (Config.isCypressContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_cypress_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_cypress_wood", 1, 2.0f, 10.0f, 255, true));
        }

        if (Config.isEbonyContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_ebony_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_ebony_wood", 1, 2.0f, 10.0f, 255, true));
        }

        if (Config.isGreatOakContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_great_oak_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_great_oak_wood", 1, 2.0f, 10.0f, 255, true));
        }

        if (Config.isEnchantedTreeContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_green_enchanted_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_green_enchanted_wood", 1, 2.0f, 10.0f, 255, true));
        }

        registerBlock(registry, new BlockDirectionalLogBase("stripped_hawthorn_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_hawthorn_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_holly_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_holly_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_ironwood_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_ironwood_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_jacaranda_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_jacaranda_wood", 1, 2.0f, 10.0f, 255, true));

        if (Config.isMangroveContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_mangrove_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_mangrove_wood", 1, 2.0f, 10.0f, 255, true));
        }

        registerBlock(registry, new BlockDirectionalLogBase("stripped_maple_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_maple_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_palo_verde_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_palo_verde_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_pine_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_pine_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_rainbow_eucalyptus_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_rainbow_eucalyptus_wood", 1, 2.0f, 10.0f, 255, true));

        if (Config.isRedwoodContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_redwood_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_redwood_wood", 1, 2.0f, 10.0f, 255, true));
        }

        registerBlock(registry, new BlockDirectionalLogBase("stripped_rowan_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_rowan_wood", 1, 2.0f, 10.0f, 255, true));

        if (Config.isSkyrisContentEnabled()) {
            registerBlock(registry, new BlockDirectionalLogBase("stripped_skyris_log", 1, true));
            registerBlock(registry, new BlockWoodBase("stripped_skyris_wood", 1, 2.0f, 10.0f, 255, true));
        }

        registerBlock(registry, new BlockDirectionalLogBase("stripped_willow_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_willow_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_witch_hazel_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_witch_hazel_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_zelkova_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_zelkova_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockDirectionalLogBase("stripped_fir_log", 1, true));

        registerBlock(registry, new BlockWoodBase("stripped_fir_wood", 1, 2.0f, 10.0f, 2, true));

        if (Config.isOreContentEnabled("tamrelite")) {
            registerBlock(registry, new BlockBasicBase("tamrelite_block", Material.IRON, SoundType.METAL, "pickaxe", 3, 3.0f, 16.0f, 0.0f, 255, true));
            registerBlock(registry, createOreBlock("tamrelite_ore", 3, 3.0f, 15.0f, () -> new ItemStack(ModItems.tamrelite_gem, 1)));
        }

        registerBlock(registry, new BlockThornBranches());

        registerBlock(registry, new BlockThornblock());

        registerBlock(registry, new BlockTinyLilypad());

        registerBlock(registry, new BlockSimpleFlowerBase("torch_ginger", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("violet_leather_flower", BYGTab.tab, 0.0f));

        registerBlock(registry, createStackingPlantBlock("weeping_milk_cap", BYGTab.tab, 0.0f, EnumPlantType.Cave, 1, false, false, true, false,
                () -> new ItemStack(Item.getItemFromBlock(ModBlocks.weeping_milk_cap), 1)));

        registerBlock(registry, createCrystalBlock("white_crystal_block", () -> ModItems.white_crystals, 3, false));

        registerBlock(registry, new BlockPetalBase("white_petal"));

        if (Config.isSkyrisContentEnabled()) {
            registerBlock(registry, new BlockShearableLeavesBase("white_skyris_leaves", 0, () -> ModBlocks.skyris_leaves, () -> new ItemStack(ModBlocks.skyris_sapling, 1), 0.03, 1));
        }

        registerBlock(registry, new BlockSimpleFlowerBase("white_anemone", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("white_celosia", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("white_cherry_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.WHITE_CHERRY, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.WHITE_CHERRY_LARGE, 1.0)}, 0.4, BlockGeneratedSaplingBase.outsideScaledTemperatureRange(0.7, 0.89)));

        registerBlock(registry, new BlockBasicBase("white_chiseled_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockSimpleFlowerBase("white_sage", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockFallingBase("white_sand", Material.SAND, SoundType.SAND, "shovel", 0, 1.0f, 5.0f, 0.0f, 255));

        registerBlock(registry, new BlockBasicBase("white_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, new BlockBasicBase("white_smooth_sandstone", Material.ROCK, SoundType.STONE, "pickaxe", 1, 1.5f, 10.0f, 0.0f, 2, true));

        registerBlock(registry, createGeneratedSapling("white_stellata_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(new StellataTreeGenerator(true), 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockConfiguredFlower("wild_rudo", null, false, false, () -> new ItemStack(ModItems.rudo_beans, 3), null));

        registerBlock(registry, new BlockConfiguredFlower("wild_strawberry", null, false, false, () -> new ItemStack(ModItems.strawberry, 2), null));
        registerBlock(registry, new BlockWoodDoorBase("willow_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.willow_door));

        registerBlock(registry, new BlockFenceBase("willow_fence", 0, 2.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("willow_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("willow_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 2.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("willow_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("willow_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockBookshelfBase("willow_bookshelf", 0, 1.0f, 8.0f, 255, false));

        registerBlock(registry, new BlockShearableLeavesBase("willow_leaves", 0, () -> ModBlocks.willow_leaves, () -> new ItemStack(ModBlocks.willow_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("willow_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("willow_planks",  0, 2.0f, 10.0f, 255, false));

        registerBlock(registry, createGeneratedSapling("willow_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.WILLOW, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.WILLOW_LARGE, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("willow_slab", 0));

        registerBlock(registry, new BlockSimpleFlowerBase("winter_rose", BYGTab.tab, 0.8f));

        registerBlock(registry, new BlockConfiguredFlower("winter_succulent", BYGTab.tab, false, true, null, null));

        registerBlock(registry, new BlockSimpleFlowerBase("winter_cyclamen", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("winter_scilla", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockWoodDoorBase("witch_hazel_door", 1, 2.0f, 8.0f, () -> ModItems.witch_hazel_door));

        registerBlock(registry, new BlockFenceBase("witch_hazel_fence", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockFenceGateBase("witch_hazel_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("witch_hazel_stairs", Material.WOOD, SoundType.WOOD, "axe", 1, 2.0f, 8.0f, true));

        registerBlock(registry, new BlockWoodBase("witch_hazel_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("witch_hazel_wood_wall", 1, 2.0f, 10.0f, 0, true, true));

        registerBlock(registry, new BlockBookshelfBase("witch_hazel_bookshelf", 1, 1.0f, 8.0f, 255, true));

        registerBlock(registry, new BlockShearableLeavesBase("witch_hazel_leaves", 1, () -> ModBlocks.witch_hazel_leaves, () -> new ItemStack(ModBlocks.witch_hazel_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockShearableLeavesBase("witch_hazel_leaves_blooming", 1, () -> ModBlocks.witch_hazel_leaves_blooming, () -> new ItemStack(ModBlocks.witch_hazel_sapling, 1), 0.03, 1, 0.5f, net.minecraft.util.BlockRenderLayer.TRANSLUCENT, true));

        registerBlock(registry, new BlockDirectionalLogBase("witch_hazel_log", 1, true));

        registerBlock(registry, new BlockPlanksBase("witch_hazel_planks",  1, 2.0f, 8.0f, 255, true));

        registerBlock(registry, createGeneratedSapling("witch_hazel_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.WITCH_HAZEL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("witch_hazel_slab", 1));

        registerBlock(registry, createLightBreakingFlowerBlock("wood_blewit", BYGTab.tab));

        registerBlock(registry, createGeneratedSapling("yellow_birch_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.YELLOW_BIRCH, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.YELLOW_BIRCH_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockPetalBase("yellow_petal"));

        registerBlock(registry, new BlockSimpleFlowerBase("yellow_celosia", BYGTab.tab, 0.0f));

        registerBlock(registry, new BlockSimpleFlowerBase("yellow_daffodil", BYGTab.tab, 0.0f));

        registerBlock(registry, createGeneratedSapling("yellow_spruce_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.YELLOW_SPRUCE, 0.5),
                BlockGeneratedSaplingBase.tree(BygTrees.YELLOW_SPRUCE_TALL, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockSimpleFlowerBase("yellow_tulip", BYGTab.tab, 0.0f));
        registerBlock(registry, new BlockWoodDoorBase("zelkova_door_bottom", 1, 2.0f, 8.0f, () -> ModItems.zelkova_door));

        registerBlock(registry, new BlockFenceBase("zelkova_fence", 0, 1.0f, 10.0f, false));

        registerBlock(registry, new BlockFenceGateBase("zelkova_fence_gate", 1, 1.0f, 10.0f));

        registerBlock(registry, new BlockStairsBase("zelkova_stairs", Material.WOOD, SoundType.WOOD, "axe", 0, 1.0f, 10.0f, true));

        registerBlock(registry, new BlockWoodBase("zelkova_wood", 1, 2.0f, 10.0f, 255, true));

        registerBlock(registry, new BlockWoodWallBase("zelkova_wood_wall", 0, 2.0f, 10.0f, 0, false, true));

        registerBlock(registry, new BlockBookshelfBase("zelkova_bookshelf", 0, 1.0f, 8.0f, 255, false));

        registerBlock(registry, new BlockShearableLeavesBase("zelkova_leaves", 0, () -> ModBlocks.zelkova_leaves, () -> new ItemStack(ModBlocks.zelkova_sapling, 1), 0.03, 1));

        registerBlock(registry, new BlockDirectionalLogBase("zelkova_log", 0, false));

        registerBlock(registry, new BlockPlanksBase("zelkova_planks",  0, 1.0f, 10.0f, 255, false));

        registerBlock(registry, createGeneratedSapling("zelkova_sapling", new BlockGeneratedSaplingBase.TreePlacement[]{
                BlockGeneratedSaplingBase.tree(BygTrees.ZELKOVA, 1.0)}, 0.4, BlockGeneratedSaplingBase.alwaysGrow()));

        registerBlock(registry, new BlockWoodSlabBase("zelkova_slab", 0));
    }

    private static Block createBeaconBaseBlock(String name, Material material, SoundType soundType, String harvestTool, int harvestLevel,
                                               float hardness, float resistance, float lightLevel, int lightOpacity, boolean silkHarvest) {
        return new BlockBasicBase(name, material, soundType, harvestTool, harvestLevel, hardness, resistance, lightLevel, lightOpacity, silkHarvest) {
            @Override
            public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
                return true;
            }
        };
    }

    private static Block createPlantableFallingBlock(String name, Material material, SoundType soundType, String harvestTool, int harvestLevel,
                                                     float hardness, float resistance, float lightLevel, int lightOpacity, boolean silkHarvest) {
        return new BlockFallingBase(name, material, soundType, harvestTool, harvestLevel, hardness, resistance, lightLevel, lightOpacity) {
            @Override
            public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
                return true;
            }

            @Override
            public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
                return silkHarvest;
            }
        };
    }

    private static Block createDroppingBasicBlock(String name, Material material, SoundType soundType, String harvestTool, int harvestLevel,
                                                  float hardness, float resistance, float lightLevel, int lightOpacity, boolean silkHarvest,
                                                  Supplier<ItemStack> dropSupplier) {
        return new BlockBasicBase(name, material, soundType, harvestTool, harvestLevel, hardness, resistance, lightLevel, lightOpacity, silkHarvest) {
            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(dropSupplier.get());
            }
        };
    }

    private static Block createOreBlock(String name, int harvestLevel, float hardness, float resistance, @Nullable Supplier<ItemStack> dropSupplier) {
        return new BlockBasicBase(name, Material.ROCK, SoundType.STONE, "pickaxe", harvestLevel, hardness, resistance, 0.0f, 255, false) {
            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                if (dropSupplier == null) {
                    super.getDrops(drops, world, pos, state, fortune);
                    return;
                }
                drops.add(dropSupplier.get());
            }
        };
    }

    private static Block createTransitionFlowerBlock(String name, @Nullable String particleName, @Nullable CreativeTabs tab, double tickChance,
                                                     Supplier<Block> replacementSupplier) {
        return new BlockSimpleFlowerBase(name, tab) {
            @Override
            public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
                if (Math.random() < tickChance) {
                    world.setBlockState(pos, replacementSupplier.get().getDefaultState(), 3);
                }
            }
        };
    }

    private static Block createHarvestFlowerBlock(String name, @Nullable String particleName, @Nullable CreativeTabs tab,
                                                  Supplier<Block> pickBlockSupplier, Supplier<ItemStack> dropSupplier) {
        return new BlockSimpleFlowerBase(name, tab) {
            @Override
            public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
                return new ItemStack(pickBlockSupplier.get(), 1);
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(dropSupplier.get());
            }
        };
    }

    private static Block createPoisonFlowerBlock(String name) {
        return new BlockFlower() {
            {
                this.setSoundType(SoundType.PLANT);
                this.setCreativeTab(BYGTab.tab);
                this.setHardness(0.01f);
                this.setResistance(2.0f);
                this.setLightLevel(0.0f);
                this.setTranslationKey(name);
                this.setRegistryName(name);
            }

            @Override
            public EnumFlowerColor getBlockType() {
                return EnumFlowerColor.YELLOW;
            }

            @SideOnly(Side.CLIENT)
            @Override
            public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
                for (EnumFlowerType flowerType : EnumFlowerType.getTypes(this.getBlockType())) {
                    list.add(new ItemStack(this, 1, flowerType.getMeta()));
                }
            }

            @Override
            public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
                super.onEntityCollision(world, pos, state, entity);
                if (Config.doesPoisonIvyApplyPoison() && entity instanceof EntityLivingBase) {
                    ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.POISON,
                            Config.getPoisonIvyPoisonDuration(),
                            Config.getPoisonIvyPoisonAmplifier(),
                            true,
                            false));
                }
            }

            @Override
            public boolean isFlammable(IBlockAccess blockAccess, BlockPos pos, EnumFacing face) {
                return true;
            }
        };
    }

    private static Block createLightBreakingFlowerBlock(String name, @Nullable CreativeTabs tab) {
        return new BlockFlower() {
            {
                this.setSoundType(SoundType.PLANT);
                this.setCreativeTab(tab);
                this.setHardness(0.01f);
                this.setResistance(2.0f);
                this.setLightLevel(0.0f);
                this.setTranslationKey(name);
                this.setRegistryName(name);
            }

            @Override
            public EnumFlowerColor getBlockType() {
                return EnumFlowerColor.YELLOW;
            }

            @SideOnly(Side.CLIENT)
            @Override
            public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
                for (EnumFlowerType flowerType : EnumFlowerType.getTypes(this.getBlockType())) {
                    list.add(new ItemStack(this, 1, flowerType.getMeta()));
                }
            }

            @Override
            public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
                if (world.getLightFromNeighbors(pos) >= 13) {
                    this.dropBlockAsItem(world, pos, state, 1);
                    world.setBlockToAir(pos);
                }
            }

            @Override
            public boolean isFlammable(IBlockAccess blockAccess, BlockPos pos, EnumFacing face) {
                return true;
            }
        };
    }

    private static Block createAltitudeSwapShearableLeavesBlock(String name, Supplier<Block> highAltitudeReplacementSupplier,
                                                                Supplier<Block> shearedDropSupplier, Supplier<ItemStack> naturalDropSupplier,
                                                                double naturalDropChance) {
        return new BlockShearableLeavesBase(name, 0, shearedDropSupplier, naturalDropSupplier, naturalDropChance, 1) {
            @Override
            public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
                super.onBlockAdded(world, pos, state);
                world.scheduleUpdate(pos, this, this.tickRate(world));
            }

            @Override
            public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
                super.updateTick(world, pos, state, random);
                // The leaf may have decayed away during the super call; don't resurrect it.
                if (world.getBlockState(pos).getBlock() != this) {
                    return;
                }
                if (pos.getY() >= 150) {
                    world.setBlockState(pos, highAltitudeReplacementSupplier.get().getDefaultState(), 3);
                    return;
                }
                world.scheduleUpdate(pos, this, this.tickRate(world));
            }
        };
    }

    private static Block createAltitudeSwapFruitLeavesBlock(String name, Supplier<Block> highAltitudeReplacementSupplier,
                                                            Supplier<ItemStack> dropSupplier) {
        return new BlockFruitLeavesBase(name, 0, dropSupplier) {
            @Override
            public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
                super.onBlockAdded(world, pos, state);
                world.scheduleUpdate(pos, this, this.tickRate(world));
            }

            @Override
            public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
                super.updateTick(world, pos, state, random);
                // The leaf may have decayed away during the super call; don't resurrect it.
                if (world.getBlockState(pos).getBlock() != this) {
                    return;
                }
                if (pos.getY() >= 150) {
                    world.setBlockState(pos, highAltitudeReplacementSupplier.get().getDefaultState(), 3);
                    return;
                }
                world.scheduleUpdate(pos, this, this.tickRate(world));
            }
        };
    }

    private static Block createCutoutDropBlock(String name, Material material, SoundType soundType, String harvestTool, int harvestLevel,
                                               float hardness, float resistance, AxisAlignedBB boundingBox, Supplier<ItemStack> dropSupplier) {
        return new BlockBasicBase(name, material, soundType, harvestTool, harvestLevel, hardness, resistance, 0.0f, 0, false) {
            @SideOnly(Side.CLIENT)
            @Override
            public BlockRenderLayer getRenderLayer() {
                return BlockRenderLayer.CUTOUT_MIPPED;
            }

            @Override
            public boolean isFullCube(IBlockState state) {
                return false;
            }

            @Override
            public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
                return boundingBox;
            }

            @Override
            public boolean isOpaqueCube(IBlockState state) {
                return false;
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(dropSupplier.get());
            }
        };
    }

    private static void spawnBlockDrop(World world, BlockPos pos, ItemStack stack) {
        if (world.isRemote || stack.isEmpty()) {
            return;
        }
        EntityItem entityToSpawn = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        entityToSpawn.setPickupDelay(10);
        world.spawnEntity(entityToSpawn);
    }

    private static boolean tryBonemealTransition(EntityPlayer entity, World world, BlockPos pos, double successChance,
                                                 Supplier<Block> nextStageSupplier, boolean playSound) {
        if (!isHoldingBonemeal(entity)) {
            return false;
        }
        if (Math.random() < successChance) {
            spawnHappyParticles(world, pos);
            consumeBonemeal(entity);
            world.setBlockState(pos, nextStageSupplier.get().getDefaultState(), 3);
            if (playSound) {
                playGrowSound(world, pos);
            }
        } else {
            consumeBonemeal(entity);
        }
        return true;
    }

    private static boolean isHoldingBonemeal(EntityPlayer entity) {
        return entity != null && entity.inventory.hasItemStack(new ItemStack(Items.DYE, 1, 15));
    }

    private static void consumeBonemeal(EntityPlayer entity) {
        if (entity != null) {
            entity.inventory.clearMatchingItems(new ItemStack(Items.DYE, 1, 15).getItem(), 15, 1, null);
        }
    }

    private static void spawnHappyParticles(World world, BlockPos pos) {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX(), pos.getY(), pos.getZ(),
                    5, 3.0, 3.0, 3.0, 1.0, new int[0]);
        }
    }

    private static void playGrowSound(World world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvent.REGISTRY.getObject(new ResourceLocation("block.chorus_flower.grow")), SoundCategory.NEUTRAL, 2.0f, 1.0f);
    }

    private static void spawnFruitDrops(World world, BlockPos pos, int count) {
        for (int i = 0; i < count; ++i) {
            spawnBlockDrop(world, pos, new ItemStack(ModItems.baobab_fruit, 1));
        }
    }

    private static Block createFlatDirectionalBlock(String name, BlockRenderLayer renderLayer, boolean flammable, boolean requiresSupportBelow) {
        return new Block(Material.PLANTS) {
            {
                this.setRegistryName(name);
                this.setTranslationKey(name);
                this.setSoundType(SoundType.PLANT);
                this.setHarvestLevel("pickaxe", 0);
                this.setHardness(0.01f);
                this.setResistance(1.0f);
                this.setLightLevel(0.0f);
                this.setLightOpacity(0);
                this.setCreativeTab(BYGTab.tab);
                this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
            }

            @SideOnly(Side.CLIENT)
            @Override
            public BlockRenderLayer getRenderLayer() {
                return renderLayer;
            }

            @Nullable
            @Override
            public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
                return NULL_AABB;
            }

            @Override
            public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
                return true;
            }

            @Override
            public boolean isFullCube(IBlockState state) {
                return false;
            }

            @Override
            public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
                switch (state.getValue(BlockHorizontal.FACING)) {
                    default:
                        return new AxisAlignedBB(1.0, 0.0, 1.0, 0.0, 0.1, 0.0);
                    case NORTH:
                        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1, 1.0);
                    case WEST:
                        return new AxisAlignedBB(0.0, 0.0, 1.0, 1.0, 0.1, 0.0);
                    case EAST:
                        return new AxisAlignedBB(1.0, 0.0, 0.0, 0.0, 0.1, 1.0);
                }
            }

            @Override
            protected BlockStateContainer createBlockState() {
                return new BlockStateContainer(this, new IProperty[]{BlockHorizontal.FACING});
            }

            @Override
            public IBlockState getStateFromMeta(int meta) {
                return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.byIndex(meta));
            }

            @Override
            public int getMetaFromState(IBlockState state) {
                return state.getValue(BlockHorizontal.FACING).getIndex();
            }

            @Override
            public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                                    int meta, EntityLivingBase placer) {
                return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
            }

            @Override
            public boolean isOpaqueCube(IBlockState state) {
                return false;
            }

            @Override
            public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
                return "clover".equals(name) ? BlockFaceShape.UNDEFINED : super.getBlockFaceShape(world, state, pos, face);
            }

            @Override
            public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
                return !"clover".equals(name) && super.isSideSolid(state, world, pos, side);
            }

            @Override
            public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
                return super.canPlaceBlockAt(worldIn, pos) && (!requiresSupportBelow || hasSupportBelow(worldIn, pos));
            }

            @Override
            public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
                if (requiresSupportBelow) {
                    checkAndDropIfUnsupported(worldIn, pos, state);
                }
            }

            @Override
            public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
                if (requiresSupportBelow) {
                    checkAndDropIfUnsupported(worldIn, pos, state);
                }
            }

            @Override
            public boolean isFlammable(IBlockAccess blockAccess, BlockPos pos, EnumFacing face) {
                return flammable;
            }

            private boolean hasSupportBelow(World worldIn, BlockPos pos) {
                BlockPos belowPos = pos.down();
                return worldIn.getBlockState(belowPos).isSideSolid(worldIn, belowPos, EnumFacing.UP);
            }

            private void checkAndDropIfUnsupported(World worldIn, BlockPos pos, IBlockState state) {
                if (worldIn.isRemote || hasSupportBelow(worldIn, pos)) {
                    return;
                }
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        };
    }

    private static Block createDirectionalPillarBlock(String name) {
        return new Block(Material.ROCK) {
            {
                this.setRegistryName(name);
                this.setTranslationKey(name);
                this.setSoundType(SoundType.STONE);
                this.setHarvestLevel("pickaxe", 1);
                this.setHardness(1.5f);
                this.setResistance(30.0f);
                this.setLightLevel(0.0f);
                this.setLightOpacity(255);
                this.setCreativeTab(BYGTab.tab);
                this.setDefaultState(this.blockState.getBaseState().withProperty(BlockDirectional.FACING, EnumFacing.SOUTH));
            }

            @Override
            protected BlockStateContainer createBlockState() {
                return new BlockStateContainer(this, new IProperty[]{BlockDirectional.FACING});
            }

            @Override
            public IBlockState getStateFromMeta(int meta) {
                return this.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.byIndex(meta));
            }

            @Override
            public int getMetaFromState(IBlockState state) {
                return state.getValue(BlockDirectional.FACING).getIndex();
            }

            @Override
            public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                                    int meta, EntityLivingBase placer) {
                EnumFacing axisFacing = facing == EnumFacing.WEST || facing == EnumFacing.EAST ? EnumFacing.UP
                        : (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? EnumFacing.EAST : EnumFacing.SOUTH);
                return this.getDefaultState().withProperty(BlockDirectional.FACING, axisFacing);
            }
        };
    }

    private static Block createTranslucentDropBlock(String name, Material material, float lightLevel, boolean flammable,
                                                    @Nullable Supplier<ItemStack> dropSupplier) {
        return new Block(material) {
            {
                this.setRegistryName(name);
                this.setTranslationKey(name);
                this.setSoundType(SoundType.SLIME);
                this.setHarvestLevel("axe", 0);
                this.setHardness(1.0f);
                this.setResistance(8.0f);
                this.setLightLevel(lightLevel);
                this.setLightOpacity(0);
                this.setCreativeTab(BYGTab.tab);
            }

            @SideOnly(Side.CLIENT)
            @Override
            public BlockRenderLayer getRenderLayer() {
                return BlockRenderLayer.TRANSLUCENT;
            }

            @Override
            public boolean isOpaqueCube(IBlockState state) {
                return false;
            }

            @Override
            public boolean isFlammable(IBlockAccess blockAccess, BlockPos pos, EnumFacing face) {
                return flammable;
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                if (dropSupplier == null) {
                    super.getDrops(drops, world, pos, state, fortune);
                    return;
                }
                drops.add(dropSupplier.get());
            }
        };
    }

    private static Block createCrystalBlock(String name, Supplier<Item> dropItem, int dropCount, boolean bonusDropOnBreak) {
        return new Block(Material.REDSTONE_LIGHT) {
            {
                this.setRegistryName(name);
                this.setTranslationKey(name);
                this.setSoundType(SoundType.GLASS);
                this.setHarvestLevel("pickaxe", 0);
                this.setHardness(1.5f);
                this.setResistance(8.0f);
                this.setLightLevel(1.0f);
                this.setLightOpacity(1);
                this.setCreativeTab(BYGTab.tab);
            }

            @SideOnly(Side.CLIENT)
            @Override
            public BlockRenderLayer getRenderLayer() {
                return BlockRenderLayer.TRANSLUCENT;
            }

            @Override
            public boolean isOpaqueCube(IBlockState state) {
                return false;
            }

            @Override
            public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
                // All crystal variants are made by this factory, so they share this anonymous class.
                // Do not render the internal face where two crystal blocks touch.
                if (world.getBlockState(pos.offset(side)).getBlock().getClass() == this.getClass()) {
                    return false;
                }
                return super.shouldSideBeRendered(state, world, pos, side);
            }

            @Override
            public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer entity, boolean willHarvest) {
                boolean removed = super.removedByPlayer(state, world, pos, entity, willHarvest);
                if (bonusDropOnBreak && Math.random() < 0.5 && !world.isRemote) {
                    EntityItem entityToSpawn = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(dropItem.get(), 1));
                    entityToSpawn.setPickupDelay(10);
                    world.spawnEntity(entityToSpawn);
                }
                return removed;
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(new ItemStack(dropItem.get(), dropCount));
            }
        };
    }

    private static Block createFacingMelonBlock(String name, float lightLevel) {
        return new Block(Material.WOOD) {
            {
                this.setRegistryName(name);
                this.setTranslationKey(name);
                this.setSoundType(SoundType.WOOD);
                this.setHardness(1.0f);
                this.setResistance(10.0f);
                this.setLightLevel(lightLevel);
                this.setLightOpacity(255);
                this.setCreativeTab(BYGTab.tab);
                this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
            }

            @Override
            protected BlockStateContainer createBlockState() {
                return new BlockStateContainer(this, new IProperty[]{BlockHorizontal.FACING});
            }

            @Override
            public IBlockState getStateFromMeta(int meta) {
                return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.byIndex(meta));
            }

            @Override
            public int getMetaFromState(IBlockState state) {
                return state.getValue(BlockHorizontal.FACING).getIndex();
            }

            @Override
            public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                                    int meta, EntityLivingBase placer) {
                return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
            }

            @Override
            public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
                return MapColor.GREEN;
            }

            @Override
            public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
                return false;
            }
        };
    }

    private static Block createDirectionalGlowshroomBlock(String name, float lightLevel, Supplier<Item> dropItem, int dropCount,
                                                          @Nullable Supplier<Block> activatedReplacement) {
        return new Block(Material.PLANTS) {
            {
                this.setRegistryName(name);
                this.setTranslationKey(name);
                this.setSoundType(SoundType.SLIME);
                this.setHarvestLevel("axe", 0);
                this.setHardness(0.0f);
                this.setResistance(5.0f);
                this.setLightLevel(lightLevel);
                this.setLightOpacity(0);
                this.setCreativeTab(BYGTab.tab);
                this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
            }

            @SideOnly(Side.CLIENT)
            @Override
            public BlockRenderLayer getRenderLayer() {
                return BlockRenderLayer.CUTOUT_MIPPED;
            }

            @Nullable
            @Override
            public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
                return NULL_AABB;
            }

            @Override
            public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
                return true;
            }

            @Override
            public boolean isFullCube(IBlockState state) {
                return false;
            }

            @Override
            public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
                switch (state.getValue(BlockHorizontal.FACING)) {
                    default:
                        return new AxisAlignedBB(0.8, 0.0, 0.8, 0.2, 0.8, 0.2);
                    case NORTH:
                        return new AxisAlignedBB(0.2, 0.0, 0.2, 0.8, 0.8, 0.8);
                    case WEST:
                        return new AxisAlignedBB(0.2, 0.0, 0.8, 0.8, 0.8, 0.2);
                    case EAST:
                        return new AxisAlignedBB(0.8, 0.0, 0.2, 0.2, 0.8, 0.8);
                }
            }

            @Override
            protected BlockStateContainer createBlockState() {
                return new BlockStateContainer(this, new IProperty[]{BlockHorizontal.FACING});
            }

            @Override
            public IBlockState getStateFromMeta(int meta) {
                return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.byIndex(meta));
            }

            @Override
            public int getMetaFromState(IBlockState state) {
                return state.getValue(BlockHorizontal.FACING).getIndex();
            }

            @Override
            public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                                    int meta, EntityLivingBase placer) {
                return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
            }

            @Override
            public boolean isOpaqueCube(IBlockState state) {
                return false;
            }

            @Override
            public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
                return false;
            }

            @Override
            public Item getItemDropped(IBlockState state, Random rand, int fortune) {
                return dropItem.get();
            }

            @Override
            public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
                return new ItemStack(dropItem.get());
            }

            @Override
            public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
                drops.add(new ItemStack(dropItem.get(), dropCount));
            }

            @Override
            public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand, EnumFacing side,
                                            float hitX, float hitY, float hitZ) {
                if (activatedReplacement == null) {
                    return super.onBlockActivated(world, pos, state, entity, hand, side, hitX, hitY, hitZ);
                }
                Item heldItem = entity.getHeldItemMainhand().getItem();
                Item growthItem = dropItem.get();
                if (heldItem != growthItem) {
                    return super.onBlockActivated(world, pos, state, entity, hand, side, hitX, hitY, hitZ);
                }
                if (!world.isRemote) {
                    world.playSound(null, pos, SoundEvent.REGISTRY.getObject(new ResourceLocation("block.slime.place")), SoundCategory.NEUTRAL, 1.0f, 1.0f);
                    entity.inventory.clearMatchingItems(growthItem, -1, 1, null);
                    world.setBlockToAir(pos);
                    world.setBlockState(pos, activatedReplacement.get().getDefaultState(), 3);
                }
                return true;
            }
        };
    }

    private static Block createStackingPlantBlock(String name, @Nullable CreativeTabs tab, float lightLevel, EnumPlantType plantType, int maxHeight,
                                                  boolean flammable, boolean replaceable, boolean caveLightBreak, boolean damagesEntities,
                                                  Supplier<ItemStack> dropSupplier) {
        return new BlockStackingPlantBase(name, tab, lightLevel, plantType, maxHeight, flammable, replaceable, caveLightBreak, damagesEntities,
                dropSupplier, false);
    }

    private static Block createDeadGrassBlock(String name) {
        return new BlockStackingPlantBase(name, BYGTab.tab, 0.0f, EnumPlantType.Desert, 1, true, true, false, false, () -> ItemStack.EMPTY, true);
    }

    private static void registerBlock(IForgeRegistry<Block> registry, Block block) {
        if (block.getRegistryName() != null && !Config.isContentRegistered(block.getRegistryName().getPath())) {
            return;
        }
        registry.register(block);
    }
}
