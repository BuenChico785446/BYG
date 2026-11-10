package windanesz.byg.worldgen;

import net.minecraft.block.BlockFlower;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import windanesz.byg.registry.ModBlocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public final class FlowerWorldgenRegistry {
    private static final Config[] CONFIGS = new Config[]{
            config(() -> (BlockFlower) ModBlocks.allium_bush, 200, "byg:byg_allium_fields"),
            config(() -> (BlockFlower) ModBlocks.alpine_bellflower, 2, "byg:byg_alps"),
            config(() -> (BlockFlower) ModBlocks.amaranth, 40, "byg:byg_amaranth_fields"),
            config(() -> (BlockFlower) ModBlocks.angelica, 2, "byg:byg_jacaranda_forest", "byg:byg_orchard", "byg:byg_stellata_pasture", "byg:byg_prairie", "byg:byg_grassland_plateau", "plains"),
            config(() -> (BlockFlower) ModBlocks.azalea, 1, "byg:byg_woodlands", "byg:byg_boreal_forest", "byg:byg_maple_taiga", "byg:byg_deciduous_forest", "forest"),
            config(() -> (BlockFlower) ModBlocks.begonia, 2, "byg:byg_fungal_jungle", "byg:byg_redwood_tropics", "byg:byg_tropical_mountains", "byg:byg_tropical_rainforest", "jungle"),
            config(() -> (BlockFlower) ModBlocks.bistort, 1, "byg:byg_fungal_jungle", "byg:byg_redwood_tropics", "byg:byg_tropical_rainforest", "byg:byg_tropical_mountains", "jungle"),
            config(() -> (BlockFlower) ModBlocks.blueberry_bush, 2, "byg:byg_blue_taiga", "byg:byg_boreal_forest", "byg:byg_coniferous_forest", "byg:byg_evergreen_taiga", "byg:byg_snowy_coniferous_forest", "byg:byg_snowy_evergreen_taiga"),
            config(() -> (BlockFlower) ModBlocks.bluesage, 2, "byg:byg_woodlands", "byg:byg_fungal_jungle", "byg:byg_redwood_tropics", "byg:byg_tropical_rainforest", "byg:byg_ebony_woods", "byg:byg_deciduous_forest"),
            config(() -> (BlockFlower) ModBlocks.california_poppy, 3, "byg:byg_prairie"),
            config(() -> (BlockFlower) ModBlocks.crocus, 2, "byg:byg_pine_mountains", "byg:byg_boreal_forest", "byg:byg_skyris_highlands", "byg:byg_bog", "byg:byg_weeping_witch_forest", "byg:byg_stone_brushlands", "byg:byg_bluff_mountains"),
            config(() -> (BlockFlower) ModBlocks.cyan_amaranth, 40, "byg:byg_amaranth_fields"),
            config(() -> (BlockFlower) ModBlocks.cyan_rose, 2, "byg:byg_frosty_forest", "byg:byg_snowy_pine_mountains", "byg:byg_snowy_coniferous_forest", "byg:byg_snowy_evergreen_taiga"),
            config(() -> (BlockFlower) ModBlocks.cyan_tulip, 40, "byg:byg_flowering_plains"),
            config(() -> (BlockFlower) ModBlocks.daffodil, 2, "byg:byg_cherry_grove"),
            config(() -> (BlockFlower) ModBlocks.delphinium, 2, "byg:byg_orchard", "byg:byg_prairie", "plains"),
            config(() -> (BlockFlower) ModBlocks.fairy_slipper, 2, "byg:byg_skyris_highlands", "byg:byg_enchanted_forest"),
            config(() -> (BlockFlower) ModBlocks.fairy_slipper, 1, "byg:byg_ancient_forest"),
            config(() -> (BlockFlower) ModBlocks.firecracker, 2, "byg:byg_lush_desert"),
            config(() -> (BlockFlower) ModBlocks.foxglove, 5, "byg:byg_skyris_highlands"),
            config(() -> (BlockFlower) ModBlocks.green_tulip, 40, "byg:byg_flowering_plains"),
            config(() -> (BlockFlower) ModBlocks.guzmania, 2, "byg:byg_fungal_jungle", "byg:byg_redwood_tropics", "byg:byg_tropical_rainforest", "byg:byg_tropical_mountains", "jungle"),
            config(() -> (BlockFlower) ModBlocks.horseweed, 2, "byg:byg_prairie"),
            config(() -> (BlockFlower) ModBlocks.incan_lily, 2, "byg:byg_fungal_jungle", "byg:byg_tropical_rainforest", "jungle", "jungle_hills", "jungle_edge", "byg:byg_tropical_islands"),
            config(() -> (BlockFlower) ModBlocks.iris, 2, "byg:byg_zelkova_forest", "byg:byg_seasonal_taiga", "byg:byg_coniferous_forest", "byg:byg_stone_brushlands", "byg:byg_bluff_mountains"),
            config(() -> (BlockFlower) ModBlocks.iris, 1, "byg:byg_marshlands"),
            config(() -> (BlockFlower) ModBlocks.japanese_orchid, 1, "byg:byg_cherry_grove"),
            config(() -> (BlockFlower) ModBlocks.kovan, 2, "byg:byg_zelkova_forest"),
            config(() -> (BlockFlower) ModBlocks.lazarus_bell_flower, 2, "byg:byg_fungal_jungle", "byg:byg_tropical_rainforest", "byg:byg_redwood_tropics", "byg:byg_tropical_mountains", "jungle"),
            config(() -> (BlockFlower) ModBlocks.lollipop_flower, 2, "byg:byg_stellata_pasture", "byg:byg_grassland_plateau", "byg:byg_meadow", "byg:byg_deciduous_forest", "byg:byg_aspen_forest"),
            config(() -> (BlockFlower) ModBlocks.magenta_amaranth, 40, "byg:byg_amaranth_fields"),
            config(() -> (BlockFlower) ModBlocks.magenta_celosia, 2, "byg:byg_cika_forest"),
            config(() -> (BlockFlower) ModBlocks.magenta_tulip, 40, "byg:byg_flowering_plains"),
            config(() -> (BlockFlower) ModBlocks.orange_amaranth, 40, "byg:byg_amaranth_fields"),
            config(() -> (BlockFlower) ModBlocks.orange_celosia, 2, "byg:byg_cika_forest"),
            config(() -> (BlockFlower) ModBlocks.orange_daisy, 2, "byg:byg_grassland_plateau", "plains", "byg:byg_aspen_forest"),
            config(() -> (BlockFlower) ModBlocks.osiria_rose, 1, "byg:byg_great_lakes"),
            config(() -> (BlockFlower) ModBlocks.peach_leather_flower, 1, "byg:byg_bayou", "byg:byg_mangrove_marshes", "swampland", "byg:byg_glowshroom_bayou", "byg:byg_cypress_swamplands", "byg:byg_marshlands"),
            config(() -> (BlockFlower) ModBlocks.pink_allium, 2, "byg:byg_jacaranda_forest", "byg:byg_stellata_pasture", "byg:byg_orchard", "byg:byg_skyris_highlands", "byg:byg_allium_fields", "byg:byg_cherry_grove"),
            config(() -> (BlockFlower) ModBlocks.pink_anemone, 2, "byg:byg_skyris_highlands", "byg:byg_seasonal_birch_forest"),
            config(() -> (BlockFlower) ModBlocks.pink_daffodil, 2, "byg:byg_cherry_grove", "byg:byg_enchanted_forest", "byg:byg_skyris_highlands"),
            config(() -> (BlockFlower) ModBlocks.pink_orchid, 2, "swampland", "byg:byg_bayou", "byg:byg_glowshroom_bayou", "byg:byg_mangrove_marshes"),
            config(() -> (BlockFlower) ModBlocks.pink_orchid, 1, "byg:byg_marshlands"),
            config(() -> (BlockFlower) ModBlocks.poison_ivy, 1, "byg:byg_tropical_rainforest"),
            config(() -> (BlockFlower) ModBlocks.prairie_grass, 222, "byg:byg_prairie"),
            config(() -> (BlockFlower) ModBlocks.protea_flower, 1, "byg:byg_fungal_jungle", "byg:byg_tropical_rainforest", "byg:byg_tropical_mountains", "jungle"),
            config(() -> (BlockFlower) ModBlocks.purple_amaranth, 40, "byg:byg_amaranth_fields"),
            config(() -> (BlockFlower) ModBlocks.purple_celosia, 2, "byg:byg_weeping_witch_forest"),
            config(() -> (BlockFlower) ModBlocks.purple_orchid, 2, "swampland", "byg:byg_bayou", "byg:byg_glowshroom_bayou", "byg:byg_mangrove_marshes"),
            config(() -> (BlockFlower) ModBlocks.purple_orchid, 1, "byg:byg_marshlands"),
            config(() -> (BlockFlower) ModBlocks.purple_sage, 2, "byg:byg_great_oak_lowlands", "byg:byg_boreal_forest", "byg:byg_orchard", "byg:byg_pine_mountains", "byg:byg_grassland_plateau", "byg:byg_seasonal_deciduous", "byg:byg_chaparral_lowlands", "byg:byg_deciduous_forest", "byg:byg_snowy_pine_mountains", "byg:byg_seasonal_birch_forest", "byg:byg_great_lakes", "byg:byg_aspen_forest", "byg:byg_pine_lowlands"),
            config(() -> (BlockFlower) ModBlocks.purple_tulip, 40, "byg:byg_flowering_plains"),
            config(() -> (BlockFlower) ModBlocks.red_celosia, 1, "byg:byg_cika_forest"),
            config(() -> (BlockFlower) ModBlocks.red_corn_flower, 2, "byg:byg_grassland_plateau", "plains", "byg:byg_prairie"),
            config(() -> (BlockFlower) ModBlocks.red_orchid, 3, "swampland", "byg:byg_bayou", "byg:byg_glowshroom_bayou", "byg:byg_mangrove_marshes"),
            config(() -> (BlockFlower) ModBlocks.richea, 1, "byg:byg_fungal_jungle", "byg:byg_tropical_rainforest", "byg:byg_tropical_mountains", "jungle"),
            config(() -> (BlockFlower) ModBlocks.rose, 2, "byg:byg_orchard", "byg:byg_red_oak_forest", "plains", "forest", "byg:byg_woodlands", "byg:byg_redwood_tropics", "byg:byg_grassland_plateau"),
            config(() -> (BlockFlower) ModBlocks.sacred_datura, 2, "byg:byg_stellata_pasture"),
            config(() -> (BlockFlower) ModBlocks.salal_bush, 2, "byg:byg_woodlands", "byg:byg_red_oak_forest", "byg:byg_deciduous_forest", "forest"),
            config(() -> (BlockFlower) ModBlocks.salal_bush_ripe, 1, "byg:byg_great_lakes"),
            config(() -> (BlockFlower) ModBlocks.silver_vase_flower, 1, "byg:byg_fungal_jungle", "byg:byg_tropical_rainforest", "byg:byg_tropical_mountains"),
            config(() -> (BlockFlower) ModBlocks.snowdrops, 2, "byg:byg_weeping_witch_forest", "byg:byg_snowy_evergreen_taiga", "byg:byg_frosty_forest", "byg:byg_alps"),
            config(() -> (BlockFlower) ModBlocks.torch_ginger, 2, "byg:byg_fungal_jungle", "byg:byg_tropical_rainforest", "byg:byg_tropical_mountains", "jungle"),
            config(() -> (BlockFlower) ModBlocks.violet_leather_flower, 1, "byg:byg_mangrove_marshes", "byg:byg_bayou", "swampland", "byg:byg_glowshroom_bayou", "byg:byg_cypress_swamplands", "byg:byg_marshlands"),
            config(() -> (BlockFlower) ModBlocks.white_anemone, 2, "byg:byg_cherry_grove", "byg:byg_red_oak_forest", "byg:byg_orchard", "byg:byg_pine_mountains", "byg:byg_pine_lowlands", "byg:byg_bluff_mountains"),
            config(() -> (BlockFlower) ModBlocks.white_celosia, 1, "byg:byg_frosty_forest"),
            config(() -> (BlockFlower) ModBlocks.white_sage, 2, "byg:byg_chaparral_lowlands", "plains", "byg:byg_boreal_forest", "byg:byg_ebony_woods", "byg:byg_weeping_witch_forest", "byg:byg_pine_lowlands"),
            config(() -> (BlockFlower) ModBlocks.wild_rudo, 1, "byg:byg_tropical_rainforest", "byg:byg_fungal_jungle"),
            config(() -> (BlockFlower) ModBlocks.wild_strawberry, 1, "byg:byg_woodlands", "byg:byg_weeping_witch_forest", "forest"),
            config(() -> (BlockFlower) ModBlocks.winter_cyclamen, 2, "byg:byg_snowy_evergreen_taiga", "byg:byg_weeping_witch_forest", "byg:byg_frosty_forest", "byg:byg_alps"),
            config(() -> (BlockFlower) ModBlocks.winter_rose, 2, "byg:byg_northern_forest"),
            config(() -> (BlockFlower) ModBlocks.winter_scilla, 2, "byg:byg_snowy_evergreen_taiga", "byg:byg_snowy_pine_mountains", "byg:byg_snowy_coniferous_forest", "byg:byg_frosty_forest"),
            config(() -> (BlockFlower) ModBlocks.winter_succulent, 2, "byg:byg_northern_forest", "byg:byg_snowy_deciduous_forest", "byg:byg_bluff_mountains", "byg:byg_seasonal_taiga", "byg:byg_weeping_witch_forest"),
            config(() -> (BlockFlower) ModBlocks.yellow_celosia, 1, "byg:byg_cika_forest"),
            config(() -> (BlockFlower) ModBlocks.yellow_daffodil, 2, "byg:byg_stellata_pasture", "byg:byg_orchard", "byg:byg_lush_desert"),
            config(() -> (BlockFlower) ModBlocks.yellow_tulip, 40, "byg:byg_flowering_plains"),
            config(() -> (BlockFlower) ModBlocks.pink_allium_bush, 120, "byg:byg_allium_fields")
    };

    private FlowerWorldgenRegistry() {
    }

    public static void generateAll(Random random, int chunkX, int chunkZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
        if (!windanesz.byg.Config.isFeatureDimension(dimID)) {
            return;
        }
        ResourceLocation biomeId = Biome.REGISTRY.getNameForObject(world.getBiome(new BlockPos(chunkX, 128, chunkZ)));
        if (biomeId == null) {
            return;
        }
        for (Config config : CONFIGS) {
            if (!config.matches(biomeId)) {
                continue;
            }
            config.generate(random, chunkX, chunkZ, world);
        }
    }

    private static Config config(Supplier<BlockFlower> block, int attempts, String... biomeIds) {
        return new Config(block, attempts, new HashSet<>(Arrays.asList(biomeIds)));
    }

    private static final class Config {
        private final Supplier<BlockFlower> block;
        private final int attempts;
        private final Set<String> biomeIds;

        private Config(Supplier<BlockFlower> block, int attempts, Set<String> biomeIds) {
            this.block = block;
            this.attempts = attempts;
            this.biomeIds = biomeIds;
        }

        private boolean matches(ResourceLocation biomeId) {
            for (String entry : this.biomeIds) {
                if (BygWorldGenerator.biomeIdMatches(biomeId, entry)) {
                    return true;
                }
            }
            return false;
        }

        private void generate(Random random, int chunkX, int chunkZ, World world) {
            BlockFlower flower = this.block.get();
            if (flower == null) {
                return;
            }
            int configuredAttempts = windanesz.byg.Config.scaleFlowerAttempts(this.attempts);

            for (int i = 0; i < configuredAttempts; ++i) {
                // chunkX/Z are the +8 decoration-window origin. The surrounding
                // 2x2 chunks are loaded while the source chunk is populated.
                int centerX = chunkX + random.nextInt(16);
                int centerY = random.nextInt(128);
                int centerZ = chunkZ + random.nextInt(16);

                // Replicate WorldGenFlowers spread (64 tries, up to seven
                // blocks from the center) within that loaded window.
                for (int j = 0; j < 64; ++j) {
                    int x = centerX + random.nextInt(8) - random.nextInt(8);
                    int y = centerY + random.nextInt(4) - random.nextInt(4);
                    int z = centerZ + random.nextInt(8) - random.nextInt(8);

                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.isAirBlock(pos) && flower.canPlaceBlockAt(world, pos)) {
                        world.setBlockState(pos, flower.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}

