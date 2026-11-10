package windanesz.byg.worldgen;

import com.google.common.base.Predicate;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import windanesz.byg.Config;
import windanesz.byg.biome.BiomeCypressSwamplands;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.treegenerator.AncientForestUnderstoryTreeWorldgen;
import windanesz.byg.worldgen.treegenerator.AncientForestWorldTreeWorldgen;
import windanesz.byg.worldgen.treegenerator.PaloTree;

import java.util.*;
import java.util.function.Supplier;

public final class BygWorldGenerator implements IWorldGenerator {
    public static final BygWorldGenerator INSTANCE = new BygWorldGenerator();
    private static final String[] STONE_PEBBLE_BIOMES = {"byg:byg_stone_brushlands", "byg:byg_bluff_mountains", "byg:byg_pine_mountains",
            "byg:byg_snowy_pine_mountains", "byg:byg_dover_mountains", "byg:byg_grassland_plateau", "byg:byg_chaparral_lowlands", "byg:byg_alps"};
    private static final String[] STONE_SPIKE_BIOMES = {"byg:byg_stone_brushlands", "byg:byg_bluff_mountains", "byg:byg_pine_mountains",
            "byg:byg_dover_mountains", "byg:byg_grassland_plateau", "byg:byg_chaparral_lowlands"};
    private static final EnumFacing[] HORIZONTAL_FACINGS = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};

    private BygWorldGenerator() {
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator cg, IChunkProvider cp) {
        int blockX = chunkX * 16 + 8;
        int blockZ = chunkZ * 16 + 8;
        int dimID = world.provider.getDimension();
        capCypressSwamplandsWater(world, chunkX, chunkZ, dimID);
        QuagmireLandGenerator.generate(random, blockX, blockZ, world, dimID);
        MarshlandLandGenerator.generate(random, blockX, blockZ, world, dimID);
        TemplateWorldgenRegistry.generateAll(random, blockX, blockZ, world, dimID);
        ZelkovaWorldgen.generateAll(random, blockX, blockZ, world, dimID);
        PalmWorldgen.generate(random, blockX, blockZ, world, dimID);
        FrozenOakWorldgen.generate(random, blockX, blockZ, world, dimID);
        AncientForestWorldTreeWorldgen.generate(random, blockX, blockZ, world, dimID);
        AncientForestStoneWorldgen.generate(random, blockX, blockZ, world, dimID);
        AncientForestThornsWorldgen.generate(random, blockX, blockZ, world, dimID);
        AncientForestFloorWorldgen.generateGroundPatches(random, blockX, blockZ, world, dimID);
        AncientForestMushroomWorldgen.generate(random, blockX, blockZ, world, dimID);
        AncientForestUnderstoryTreeWorldgen.generate(random, blockX, blockZ, world, dimID);
        AncientForestCanopyWebWorldgen.generate(random, blockX, blockZ, world, dimID);
        FlowerWorldgenRegistry.generateAll(random, blockX, blockZ, world, dimID, cg, cp);
        AncientForestFloorWorldgen.generate(random, blockX, blockZ, world, dimID);
        generateEnchantedForestGlowshrooms(random, blockX, blockZ, world, dimID);
        generateEnchantedForestDoubleGrass(random, blockX, blockZ, world, dimID);
        generateBirchShelfFungi(random, blockX, blockZ, world, dimID);
        generateFallenBirchLogs(random, blockX, blockZ, world, dimID);
        generateDeadLeafPiles(random, blockX, blockZ, world, dimID);
        if (Config.isWorldgenFeatureEnabled("stone_pebbles")) {
            generateGroundBlocks(random, blockX, blockZ, world, dimID, ModBlocks.stone_pebbles, STONE_PEBBLE_BIOMES, 8, 1, true);
        }
        if (Config.isWorldgenFeatureEnabled("stone_spike")) {
            generateGroundBlocks(random, blockX, blockZ, world, dimID, ModBlocks.stone_spike, STONE_SPIKE_BIOMES, 1, 2, false);
        }
        GreatLakesIslandWorldgen.generate(random, blockX, blockZ, world, dimID);
        generateFloatingMangroveLogs(random, blockX, blockZ, world, dimID);
        generateFloatingMarshlandLogs(random, blockX, blockZ, world, dimID);
        if (Config.isWorldgenFeatureEnabled("dead_grass")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.dead_grass, 3, 1,
                    "byg:byg_outback", "byg:byg_dunes");
        }
        generateCrystalCanyonGrass(random, blockX, blockZ, world, dimID);
        if (Config.isWorldgenFeatureEnabled("algae")) {
            generateAlgaePatches(random, blockX, blockZ, world, dimID);
        }
        if (Config.generateEnchantedVillage() && Config.isWoodSetEnabled("structures")) {
            EnchantedVillage.generateWorld(random, blockX, blockZ, world, dimID, cg, cp);
        }
        if (Config.isWorldgenFeatureEnabled("glowcane_blue")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.glowcane_blue, 2, 5, "byg:byg_glowshroom_bayou");
        }
        if (Config.isWorldgenFeatureEnabled("glowcane_pink")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.glowcane_pink, 2, 5, "byg:byg_glowshroom_bayou");
        }
        if (Config.isWorldgenFeatureEnabled("glowcane_purple")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.glowcane_purple, 2, 5, "byg:byg_glowshroom_bayou");
        }
        if (Config.isWorldgenFeatureEnabled("glowcane_red")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.glowcane_red, 2, 5, "byg:byg_glowshroom_bayou");
        }
        if (Config.isWorldgenFeatureEnabled("golden_spined_cactus")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.golden_spined_cactus, 1, 1,
                    "byg:byg_outback", "byg:byg_lush_desert", "desert");
        }

        if (Config.isWorldgenFeatureEnabled("kasai_ore")) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, -1, () -> ModBlocks.kasai_ore, Config.scaleOreAttempts("kasai", 5), 106, 6, 6,
                    blockAt -> blockAt.getBlock() == Blocks.NETHERRACK, new String[0]);
        }
        if (Config.isWorldgenFeatureEnabled("latharium_ore")) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.latharium_ore, Config.scaleOreAttempts("latharium", 4), 1, 34, 4,
                    blockAt -> blockAt.getBlock() == Blocks.STONE, "byg:byg_enchanted_forest");
        }
        if (Config.isWorldgenFeatureEnabled("light_blue_sand")) {
            generateSandDeposit(random, blockX, blockZ, world, dimID, () -> ModBlocks.light_blue_sand, 44, 20, "byg:byg_tropical_islands");
        }
        if (Config.isWorldgenFeatureEnabled("minicactus")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.mini_cactus, 1, 1,
                    "byg:byg_outback", "byg:byg_lush_desert", "desert");
        }
        if (Config.isWorldgenFeatureEnabled("mud_block")) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.mud_block, 45, 10, 100, 45,
                    blockAt -> blockAt.getBlock() == Blocks.GRAVEL, true, "byg:byg_great_lakes");
        }
        if (Config.generatePaloTrees() && Config.isWoodSetEnabled("palo_verde")) {
            PaloTree.generateWorld(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("peat_dirt")) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.peat_dirt, 32, 60, 20, 32,
                    blockAt -> blockAt.getBlock() == Blocks.GRASS, true, "byg:byg_weeping_witch_forest");
        }
        if (Config.isWorldgenFeatureEnabled("peat_grass")) {
            SurfaceBlockWorldgen.generatePeatgrass(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("pendorite_ore")) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.pendorite_ore, Config.scaleOreAttempts("pendorite", 4), 5, 20, 4,
                    blockAt -> blockAt.getBlock() == Blocks.STONE,
                    "byg:byg_ancient_forest", "byg:byg_skyris_highlands", "byg:byg_pine_mountains", "extreme_hills");
        }
        if (Config.isWorldgenFeatureEnabled("pink_sand")) {
            generateSandDeposit(random, blockX, blockZ, world, dimID, () -> ModBlocks.pink_sand, 40, 27, "byg:byg_tropical_islands");
        }
        if (Config.isWorldgenFeatureEnabled("prickly_pear")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.prickly_pear, 1, 1,
                    "byg:byg_outback", "byg:byg_lush_desert", "desert");
        }
        if (Config.isWorldgenFeatureEnabled("purple_sand")) {
            generateSandDeposit(random, blockX, blockZ, world, dimID, () -> ModBlocks.purple_sand, 40, 26, "byg:byg_tropical_islands");
        }
        if (Config.isWorldgenFeatureEnabled("rocky_grass")) {
            SurfaceBlockWorldgen.generateRockyGrass(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("rocky_grass_alps")) {
            SurfaceBlockWorldgen.generateRockyGrassAlps(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("rocky_stone")) {
            SurfaceBlockWorldgen.generateRockystone(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("rockystone2")) {
            SurfaceBlockWorldgen.generateRockystoneInBluffs(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("sandy_grass")) {
            SurfaceBlockWorldgen.generateSandygrass(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("scoria")
                && matchesConfiguredBiome(world, blockX, blockZ, Config.getScoriaBiomes())) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.scoria, 30 * Config.scoriaAttemptMultiplier(), Config.scoriaMinY(), Config.scoriaYRange(), 30,
                    blockAt -> blockAt.getBlock() == Blocks.STONE, new String[0]);
        }
        if (Config.isWorldgenFeatureEnabled("sepinite")) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.sepinite, 32 * Config.sepiniteAttemptMultiplier(), Config.sepiniteMinY(), Config.sepiniteYRange(), 32,
                    blockAt -> blockAt.getBlock() == Blocks.STONE, "byg:byg_dover_mountains");
        }
        if (Config.isWorldgenFeatureEnabled("short_dead_grass")) {
            generateReedCluster(random, blockX, blockZ, world, dimID, () -> ModBlocks.short_dead_grass, 3, 1,
                    "byg:byg_outback", "byg:byg_dunes");
        }
        if (Config.isWorldgenFeatureEnabled("soapstone")
                && matchesConfiguredBiome(world, blockX, blockZ, Config.getSoapstoneBiomes())) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.soapstone, 30, 16, 5, 30,
                    blockAt -> blockAt.getBlock() == Blocks.STONE, new String[0]);
        }
        if (Config.isWorldgenFeatureEnabled("sodalite")) {
            SurfaceBlockWorldgen.generateSodalite(random, blockX, blockZ, world, dimID);
        }
        if (Config.isWorldgenFeatureEnabled("tamrelite_ore")) {
            generateMinableDeposit(random, blockX, blockZ, world, dimID, 0, () -> ModBlocks.tamrelite_ore, Config.scaleOreAttempts("tamrelite", 4), 5, 15, 4,
                    blockAt -> blockAt.getBlock() == Blocks.STONE, new String[0]);
        }

    }

    private static void generateReedCluster(Random random, int chunkX, int chunkZ, World world, int dimID, Supplier<Block> blockSupplier,
                                            int attempts, int maxHeight, String... biomeIds) {
        if (!Config.isFeatureDimension(dimID) || !matchesBiome(world, chunkX, chunkZ, biomeIds)) {
            return;
        }
        Block block = blockSupplier.get();
        if (!(block instanceof BlockReed) && !(block instanceof BlockBush)) {
            return;
        }
        int configuredAttempts = Config.scaleClusterPlantAttempts(attempts);
        WorldGenReed generator = new WorldGenReed() {
            @Override
            public boolean generate(World world, Random random, BlockPos pos) {
                for (int attempt = 0; attempt < 20; ++attempt) {
                    BlockPos placePos = pos.add(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
                    if (!world.isAirBlock(placePos)) {
                        continue;
                    }
                    int height = 1 + random.nextInt(random.nextInt(maxHeight) + 1);
                    height = Math.min(maxHeight, height);
                    for (int level = 0; level < height; ++level) {
                        if (block instanceof BlockReed ? !((BlockReed) block).canBlockStay(world, placePos) : !block.canPlaceBlockAt(world, placePos)) {
                            continue;
                        }
                        world.setBlockState(placePos.up(level), block.getDefaultState(), 2);
                    }
                }
                return true;
            }
        };
        for (int i = 0; i < configuredAttempts; ++i) {
            int x = centeredChunkCoordinate(random, chunkX);
            int y = random.nextInt(128);
            int z = centeredChunkCoordinate(random, chunkZ);
            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }

    /**
     * Scatters small patches of dead grass and short dead grass over the
     * cracked sand of the Crystal Canyons. The generic cluster generator picks
     * a random height, so it almost never finds the ground; this one starts
     * from the surface. Only blocks that can sustain the plant are used, so
     * mesa tops (white sand) stay bare.
     */
    private static void generateCrystalCanyonGrass(Random random, int chunkX, int chunkZ, World world, int dimID) {
        boolean tall = Config.isWorldgenFeatureEnabled("dead_grass");
        boolean shortGrass = Config.isWorldgenFeatureEnabled("short_dead_grass");
        if (!Config.isFeatureDimension(dimID) || (!tall && !shortGrass) || !matchesBiome(world, chunkX, chunkZ, "byg:byg_crystal_canyons")) {
            return;
        }
        int patches = Config.scaleClusterPlantAttempts(2);
        for (int patch = 0; patch < patches; patch++) {
            if (random.nextInt(3) == 0) {
                continue;
            }
            int centreX = centeredChunkCoordinate(random, chunkX);
            int centreZ = centeredChunkCoordinate(random, chunkZ);
            Block block;
            if (tall && shortGrass) {
                block = random.nextInt(3) == 0 ? ModBlocks.dead_grass : ModBlocks.short_dead_grass;
            } else {
                block = tall ? ModBlocks.dead_grass : ModBlocks.short_dead_grass;
            }
            int tries = 3 + random.nextInt(4);
            for (int i = 0; i < tries; i++) {
                int x = centreX + random.nextInt(5) - random.nextInt(5);
                int z = centreZ + random.nextInt(5) - random.nextInt(5);
                BlockPos pos = world.getHeight(new BlockPos(x, 0, z));
                if (world.isAirBlock(pos) && block.canPlaceBlockAt(world, pos)) {
                    world.setBlockState(pos, block.getDefaultState(), 2);
                }
            }
        }
    }

    private static void generateFloatingMangroveLogs(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || !matchesBiome(world, chunkX, chunkZ, "byg:byg_mangrove_marshes") || random.nextInt(3) != 0) {
            return;
        }

        boolean alongX = random.nextBoolean();
        int length = 3 + random.nextInt(3);
        int startX = alongX ? chunkX + random.nextInt(16 - length) : chunkX + random.nextInt(16);
        int startZ = alongX ? chunkZ + random.nextInt(16) : chunkZ + random.nextInt(16 - length);
        BlockPos surfacePos = findFloatingLogSurface(world, startX, startZ);
        if (surfacePos == null) {
            return;
        }

        int stepX = alongX ? 1 : 0;
        int stepZ = alongX ? 0 : 1;
        for (int offset = 0; offset < length; ++offset) {
            BlockPos placePos = surfacePos.add(stepX * offset, 0, stepZ * offset);
            if (!canPlaceFloatingLogAt(world, placePos)) {
                return;
            }
        }

        IBlockState logState = ModBlocks.mangrove_log.getDefaultState()
                .withProperty(BlockDirectional.FACING, alongX ? EnumFacing.UP : EnumFacing.EAST);
        for (int offset = 0; offset < length; ++offset) {
            world.setBlockState(surfacePos.add(stepX * offset, 0, stepZ * offset), logState, 2);
        }
    }

    private static void generateFallenBirchLogs(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || !matchesBiome(world, chunkX, chunkZ, "byg:byg_seasonal_birch_forest")) {
            return;
        }
        int attempts = Math.max(1, Config.scaleClusterPlantAttempts(2));
        for (int i = 0; i < attempts; ++i) {
            tryGenerateFallenBirchLog(random, chunkX, chunkZ, world);
        }
    }

    private static void tryGenerateFallenBirchLog(Random random, int chunkX, int chunkZ, World world) {
        boolean alongX = random.nextBoolean();
        int length = 2 + random.nextInt(3);
        int startX = alongX ? chunkX + random.nextInt(16 - length) : chunkX + random.nextInt(16);
        int startZ = alongX ? chunkZ + random.nextInt(16) : chunkZ + random.nextInt(16 - length);
        int stepX = alongX ? 1 : 0;
        int stepZ = alongX ? 0 : 1;

        BlockPos[] logPositions = new BlockPos[length];
        int targetY = -1;
        for (int offset = 0; offset < length; ++offset) {
            int x = startX + stepX * offset;
            int z = startZ + stepZ * offset;
            BlockPos placePos = findGroundPlacementThroughLeaves(world, x, z);
            if (placePos == null) {
                return;
            }
            BlockPos belowPos = placePos.down();
            IBlockState belowState = world.getBlockState(belowPos);
            if (targetY == -1) {
                targetY = placePos.getY();
            } else if (Math.abs(placePos.getY() - targetY) > 2) {
                return;
            }
            if (!world.isAirBlock(placePos)
                    || belowState.getMaterial().isLiquid()
                    || belowState.getMaterial() == Material.AIR) {
                return;
            }
            logPositions[offset] = placePos;
        }

        IBlockState logState = Blocks.LOG.getDefaultState()
                .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH)
                .withProperty(BlockLog.LOG_AXIS, alongX ? BlockLog.EnumAxis.X : BlockLog.EnumAxis.Z);
        for (BlockPos logPos : logPositions) {
            world.setBlockState(logPos, logState, 2);
            if (random.nextInt(4) != 0) {
                tryPlaceShelfFungiOnSupport(world, logPos, random);
            }
        }
    }

    private static void generateBirchShelfFungi(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || !matchesBiome(world, chunkX, chunkZ, "byg:byg_seasonal_birch_forest")) {
            return;
        }
        int attempts = Config.scaleClusterPlantAttempts(8);
        for (int i = 0; i < attempts; ++i) {
            if (random.nextInt(2) != 0) {
                continue;
            }
            int baseX = centeredChunkCoordinate(random, chunkX);
            int baseZ = centeredChunkCoordinate(random, chunkZ);
            int surfaceY = world.getHeight(new BlockPos(baseX, 0, baseZ)).getY();
            int topY = Math.min(world.getActualHeight() - 1, surfaceY + 12);
            int bottomY = Math.max(1, surfaceY - 16);
            boolean placed = false;
            for (int y = topY; y >= bottomY; --y) {
                for (int dx = -2; dx <= 2; ++dx) {
                    for (int dz = -2; dz <= 2; ++dz) {
                        BlockPos supportPos = new BlockPos(baseX + dx, y, baseZ + dz);
                        if (!isBirchLog(world.getBlockState(supportPos))) {
                            continue;
                        }
                        if (tryPlaceShelfFungiOnSupport(world, supportPos, random)) {
                            placed = true;
                            break;
                        }
                    }
                    if (placed) {
                        break;
                    }
                }
                if (placed) {
                    break;
                }
            }
        }
    }

    private static boolean tryPlaceShelfFungiOnSupport(World world, BlockPos supportPos, Random random) {
        if (ModBlocks.shelf_fungi == null) {
            return false;
        }
        int startIndex = random.nextInt(HORIZONTAL_FACINGS.length);
        for (int i = 0; i < HORIZONTAL_FACINGS.length; ++i) {
            EnumFacing outward = HORIZONTAL_FACINGS[(startIndex + i) % HORIZONTAL_FACINGS.length];
            BlockPos fungiPos = supportPos.offset(outward);
            IBlockState currentState = world.getBlockState(fungiPos);
            if (!world.isAirBlock(fungiPos) && !currentState.getBlock().isReplaceable(world, fungiPos)) {
                continue;
            }
            IBlockState fungiState = ModBlocks.shelf_fungi.getDefaultState()
                    .withProperty(BlockHorizontal.FACING, outward.getOpposite());
            if (!fungiState.getBlock().canPlaceBlockAt(world, fungiPos)) {
                continue;
            }
            world.setBlockState(fungiPos, fungiState, 2);
            return true;
        }
        return false;
    }

    private static boolean isBirchLog(IBlockState state) {
        return state.getBlock() == Blocks.LOG && state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.BIRCH;
    }

    private static BlockPos findGroundPlacementThroughLeaves(World world, int x, int z) {
        BlockPos cursor = world.getHeight(new BlockPos(x, 0, z)).down();
        int leafDepth = 0;
        while (cursor.getY() > 1) {
            IBlockState state = world.getBlockState(cursor);
            if (!state.getBlock().isLeaves(state, world, cursor)) {
                break;
            }
            leafDepth++;
            if (leafDepth > 8) {
                return null;
            }
            cursor = cursor.down();
        }

        IBlockState groundState = world.getBlockState(cursor);
        if (groundState.getMaterial().isLiquid() || groundState.getMaterial() == Material.AIR) {
            return null;
        }

        BlockPos placePos = cursor.up();
        IBlockState placeState = world.getBlockState(placePos);
        return (world.isAirBlock(placePos) || placeState.getBlock().isReplaceable(world, placePos)) ? placePos : null;
    }

    private static void generateEnchantedForestGlowshrooms(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || !Config.isWoodSetEnabled("glowshroom")
                || !matchesBiome(world, chunkX, chunkZ, "byg:byg_enchanted_forest")) {
            return;
        }
        Block[] mushrooms = {ModBlocks.small_blue_glowshroom, ModBlocks.small_green_glowshroom,
                ModBlocks.small_purple_glowshroom};
        int attempts = Config.scaleClusterPlantAttempts(1);
        for (int i = 0; i < attempts; i++) {
            int x = centeredChunkCoordinate(random, chunkX);
            int z = centeredChunkCoordinate(random, chunkZ);
            if (!matchesBiome(world, x, z, "byg:byg_enchanted_forest")) {
                continue;
            }
            BlockPos placePos = findGroundPlacementThroughLeaves(world, x, z);
            if (placePos == null) {
                continue;
            }
            Block ground = world.getBlockState(placePos.down()).getBlock();
            if (ground != Blocks.GRASS && ground != Blocks.DIRT) {
                continue;
            }
            IBlockState mushroom = mushrooms[random.nextInt(mushrooms.length)].getDefaultState()
                    .withProperty(BlockHorizontal.FACING, HORIZONTAL_FACINGS[random.nextInt(HORIZONTAL_FACINGS.length)]);
            world.setBlockState(placePos, mushroom, 2);
        }
    }

    private static void generateEnchantedForestDoubleGrass(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || random.nextInt(4) != 0
                || !matchesBiome(world, chunkX, chunkZ, "byg:byg_enchanted_forest")) {
            return;
        }
        int x = centeredChunkCoordinate(random, chunkX);
        int z = centeredChunkCoordinate(random, chunkZ);
        if (!matchesBiome(world, x, z, "byg:byg_enchanted_forest")) {
            return;
        }
        BlockPos lowerPos = findGroundPlacementThroughLeaves(world, x, z);
        if (lowerPos == null || !world.isAirBlock(lowerPos) || !world.isAirBlock(lowerPos.up())
                || world.getBlockState(lowerPos.down()).getBlock() != Blocks.GRASS) {
            return;
        }
        world.setBlockState(lowerPos, Blocks.DOUBLE_PLANT.getDefaultState()
                .withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.GRASS)
                .withProperty(BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.LOWER), 2);
        world.setBlockState(lowerPos.up(), Blocks.DOUBLE_PLANT.getDefaultState()
                .withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.GRASS)
                .withProperty(BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    }

    private static void generateGroundBlocks(Random random, int chunkX, int chunkZ, World world, int dimID, Block block,
                                             String[] biomeIds, int attempts, int oneIn, boolean randomFacing) {
        if (block == null || block == Blocks.AIR || !Config.isFeatureDimension(dimID) || !matchesBiome(world, chunkX, chunkZ, biomeIds)) {
            return;
        }
        if (oneIn > 1 && random.nextInt(oneIn) != 0) {
            return;
        }
        int count = Config.scaleClusterPlantAttempts(attempts);
        for (int i = 0; i < count; ++i) {
            BlockPos placePos = findGroundPlacementThroughLeaves(world, centeredChunkCoordinate(random, chunkX), centeredChunkCoordinate(random, chunkZ));
            if (placePos == null) {
                continue;
            }
            IBlockState state = block.getDefaultState();
            if (randomFacing) {
                state = state.withProperty(BlockHorizontal.FACING, HORIZONTAL_FACINGS[random.nextInt(HORIZONTAL_FACINGS.length)]);
            }
            world.setBlockState(placePos, state, 2);
        }
    }

    private static void generateDeadLeafPiles(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (ModBlocks.leaf_pile_dead == null) {
            return;
        }
        if (!Config.isFeatureDimension(dimID)) {
            return;
        }
        boolean isAspenForest = matchesBiome(world, chunkX, chunkZ, "byg:byg_aspen_forest");
        if (!isAspenForest && !matchesBiome(world, chunkX, chunkZ,
                "byg:byg_seasonal_forest",
                "byg:byg_bog",
                "byg:byg_seasonal_deciduous",
                "byg:byg_seasonal_taiga",
                "byg:byg_seasonal_birch_forest")) {
            return;
        }

        int attempts = Config.scaleClusterPlantAttempts(isAspenForest ? 3 : 10);
        for (int i = 0; i < attempts; ++i) {
            int baseX = centeredChunkCoordinate(random, chunkX);
            int baseZ = centeredChunkCoordinate(random, chunkZ);
            for (int spread = 0; spread < 2; ++spread) {
                int x = baseX + random.nextInt(5) - 2;
                int z = baseZ + random.nextInt(5) - 2;
                BlockPos placePos = findGroundPlacementThroughLeaves(world, x, z);
                if (placePos == null) {
                    continue;
                }
                IBlockState leafState = ModBlocks.leaf_pile_dead.getDefaultState()
                        .withProperty(BlockHorizontal.FACING, HORIZONTAL_FACINGS[random.nextInt(HORIZONTAL_FACINGS.length)]);
                if (leafState.getBlock().canPlaceBlockAt(world, placePos)) {
                    world.setBlockState(placePos, leafState, 2);
                }
            }
        }
    }

    private static void generateAlgaePatches(Random random, int chunkX, int chunkZ, World world, int dimID) {
        boolean isMarshlands = matchesBiome(world, chunkX, chunkZ, "byg:byg_marshlands");
        boolean isCypressSwamplands = matchesBiome(world, chunkX, chunkZ, "byg:byg_cypress_swamplands");
        if (!Config.isFeatureDimension(dimID) || (!matchesSwampBiome(world, chunkX, chunkZ)
                && !matchesBiome(world, chunkX, chunkZ, "byg:byg_quagmire") && !isMarshlands)) {
            return;
        }

        int configuredAttempts = Config.scaleClusterPlantAttempts(isCypressSwamplands ? 10 : isMarshlands ? 6 : 2);
        for (int i = 0; i < configuredAttempts; ++i) {
            BlockPos surfacePos = findShelteredAlgaeSurface(random, world, chunkX, chunkZ);
            if (surfacePos == null) {
                continue;
            }

            int minPatchSize;
            int targetPatchSize;
            int sizeRoll = random.nextInt(100);
            if (sizeRoll < 55) {
                minPatchSize = 3;
                targetPatchSize = 3 + random.nextInt(3);
            } else if (sizeRoll < 90) {
                minPatchSize = 6;
                targetPatchSize = 6 + random.nextInt(7);
            } else {
                minPatchSize = 12;
                targetPatchSize = 12 + random.nextInt(39);
            }

            Set<BlockPos> patch = buildAlgaePatch(random, world, surfacePos.up(), targetPatchSize, chunkX, chunkZ);
            if (patch.size() < minPatchSize) {
                continue;
            }
            for (BlockPos patchPos : patch) {
                world.setBlockState(patchPos, ModBlocks.algae.getDefaultState(), 2);
            }
        }
    }

    private static void capCypressSwamplandsWater(World world, int chunkX, int chunkZ, int dimID) {
        if (!Config.isFeatureDimension(dimID)) {
            return;
        }

        int seaLevel = world.getSeaLevel();
        for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
            for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
                BlockPos surfaceWater = new BlockPos(x, seaLevel - 1, z);
                if (!(world.getBiome(surfaceWater) instanceof BiomeCypressSwamplands)
                        || world.getBlockState(surfaceWater).getMaterial() != Material.WATER) {
                    continue;
                }

                BlockPos floor = surfaceWater;
                while (floor.getY() > 0 && world.getBlockState(floor).getMaterial() == Material.WATER) {
                    floor = floor.down();
                }
                if (world.getBlockState(floor).getMaterial() == Material.AIR) {
                    continue;
                }

                int mudFloorY = Math.max(floor.getY(), seaLevel - 3);
                for (int y = floor.getY() + 1; y < mudFloorY; y++) {
                    world.setBlockState(new BlockPos(x, y, z), Blocks.DIRT.getDefaultState(), 2);
                }
                world.setBlockState(new BlockPos(x, mudFloorY, z), ModBlocks.mud_block.getDefaultState(), 2);
            }
        }
    }

    private static void generateFloatingMarshlandLogs(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || !matchesBiome(world, chunkX, chunkZ, "byg:byg_marshlands") || random.nextInt(4) != 0) {
            return;
        }

        BlockPos bestStart = null;
        boolean bestAlongX = false;
        int bestLength = 0;
        int bestScore = Integer.MIN_VALUE;
        for (int attempt = 0; attempt < 12; attempt++) {
            boolean alongX = random.nextBoolean();
            int length = 2 + random.nextInt(3);
            int startX = alongX ? chunkX + random.nextInt(16 - length) : chunkX + random.nextInt(16);
            int startZ = alongX ? chunkZ + random.nextInt(16) : chunkZ + random.nextInt(16 - length);
            BlockPos surfacePos = findFloatingLogSurface(world, startX, startZ);
            if (surfacePos == null) {
                continue;
            }

            int score = floatingLogShelterScore(world, surfacePos, alongX, length);
            if (score > bestScore) {
                bestStart = surfacePos;
                bestAlongX = alongX;
                bestLength = length;
                bestScore = score;
            }
        }

        // A positive score keeps logs beside banks and in quiet dead ends instead of blocking open boat channels.
        if (bestStart == null || bestScore <= 0) {
            return;
        }

        boolean cypress = Config.isCypressContentEnabled();
        boolean willow = Config.isWoodSetEnabled("willow");
        if (!cypress && !willow) {
            return;
        }
        Block logBlock = willow && !(cypress && random.nextInt(5) == 0) ? ModBlocks.willow_log : ModBlocks.cypress_log;
        IBlockState logState = logBlock.getDefaultState()
                .withProperty(BlockDirectional.FACING, bestAlongX ? EnumFacing.UP : EnumFacing.EAST);
        int stepX = bestAlongX ? 1 : 0;
        int stepZ = bestAlongX ? 0 : 1;
        for (int offset = 0; offset < bestLength; offset++) {
            world.setBlockState(bestStart.add(stepX * offset, 0, stepZ * offset), logState, 2);
        }
    }

    private static int floatingLogShelterScore(World world, BlockPos start, boolean alongX, int length) {
        int stepX = alongX ? 1 : 0;
        int stepZ = alongX ? 0 : 1;
        int score = 0;
        for (int offset = 0; offset < length; offset++) {
            BlockPos logPos = start.add(stepX * offset, 0, stepZ * offset);
            if (!canPlaceFloatingLogAt(world, logPos)) {
                return Integer.MIN_VALUE;
            }
            for (EnumFacing facing : HORIZONTAL_FACINGS) {
                BlockPos neighborPos = logPos.offset(facing);
                IBlockState neighbor = world.getBlockState(neighborPos);
                if (neighbor.getMaterial() != Material.WATER && !neighbor.getBlock().isAir(neighbor, world, neighborPos)) {
                    score += 2;
                }
            }
        }

        // Favor logs whose ends point into a bank or enclosed pool, as naturally stranded driftwood would.
        BlockPos before = start.add(-stepX, 0, -stepZ);
        BlockPos after = start.add(stepX * length, 0, stepZ * length);
        if (!canPlaceFloatingLogAt(world, before)) {
            score += 3;
        }
        if (!canPlaceFloatingLogAt(world, after)) {
            score += 3;
        }
        return score;
    }

    private static BlockPos findShelteredAlgaeSurface(Random random, World world, int chunkX, int chunkZ) {
        BlockPos bestPos = null;
        int bestScore = Integer.MIN_VALUE;
        for (int sample = 0; sample < 10; sample++) {
            int x = chunkX + random.nextInt(16);
            int z = chunkZ + random.nextInt(16);
            BlockPos surfacePos = findFloatingLogSurface(world, x, z);
            if (surfacePos == null) {
                continue;
            }
            int score = algaeShelterScore(world, surfacePos);
            if (score > bestScore) {
                bestScore = score;
                bestPos = surfacePos;
            }
        }
        return bestScore > 0 || (bestPos != null && random.nextInt(8) == 0) ? bestPos : null;
    }

    private static int algaeShelterScore(World world, BlockPos waterPos) {
        int score = 0;
        for (EnumFacing facing : HORIZONTAL_FACINGS) {
            IBlockState neighbor = world.getBlockState(waterPos.offset(facing));
            if (neighbor.getMaterial() != Material.WATER && !neighbor.getBlock().isAir(neighbor, world, waterPos.offset(facing))) {
                score += 6;
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockPos nearbyPos = waterPos.add(dx, 0, dz);
                IBlockState nearby = world.getBlockState(nearbyPos);
                if (nearby.getMaterial() != Material.WATER && !nearby.getBlock().isAir(nearby, world, nearbyPos)) {
                    score++;
                }
            }
        }
        return score;
    }

    private static BlockPos findFloatingLogSurface(World world, int x, int z) {
        for (int y = world.getActualHeight() - 2; y > 0; --y) {
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = world.getBlockState(pos);
            if (state.getMaterial() == Material.WATER && world.isAirBlock(pos.up())) {
                return pos;
            }
        }
        return null;
    }

    private static Set<BlockPos> buildAlgaePatch(Random random, World world, BlockPos startPos, int targetPatchSize, int chunkX, int chunkZ) {
        Set<BlockPos> patch = new LinkedHashSet<>();
        if (!canPlaceAlgaeAt(world, startPos)) {
            return patch;
        }

        List<BlockPos> frontier = new ArrayList<>();
        patch.add(startPos);
        frontier.add(startPos);
        int expansionAttempts = targetPatchSize * 8;
        while (patch.size() < targetPatchSize && !frontier.isEmpty() && expansionAttempts-- > 0) {
            BlockPos basePos = frontier.get(random.nextInt(frontier.size()));
            BlockPos candidatePos = basePos.offset(HORIZONTAL_FACINGS[random.nextInt(HORIZONTAL_FACINGS.length)]);
            if (candidatePos.getX() < chunkX || candidatePos.getX() >= chunkX + 16
                    || candidatePos.getZ() < chunkZ || candidatePos.getZ() >= chunkZ + 16) {
                continue;
            }
            if (patch.contains(candidatePos)) {
                if (!hasExpandableAlgaeNeighbor(world, patch, basePos, chunkX, chunkZ)) {
                    frontier.remove(basePos);
                }
                continue;
            }
            if (!canPlaceAlgaeAt(world, candidatePos)) {
                if (!hasExpandableAlgaeNeighbor(world, patch, basePos, chunkX, chunkZ)) {
                    frontier.remove(basePos);
                }
                continue;
            }
            if (!isNearAlgaeShore(world, candidatePos.down(), 2) && random.nextInt(8) != 0) {
                continue;
            }
            patch.add(candidatePos);
            frontier.add(candidatePos);
            if (!hasExpandableAlgaeNeighbor(world, patch, basePos, chunkX, chunkZ)) {
                frontier.remove(basePos);
            }
        }
        return patch;
    }

    private static boolean isNearAlgaeShore(World world, BlockPos waterPos, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockPos nearbyPos = waterPos.add(dx, 0, dz);
                IBlockState nearby = world.getBlockState(nearbyPos);
                if (nearby.getMaterial() != Material.WATER && !nearby.getBlock().isAir(nearby, world, nearbyPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasExpandableAlgaeNeighbor(World world, Set<BlockPos> patch, BlockPos pos, int chunkX, int chunkZ) {
        for (EnumFacing facing : HORIZONTAL_FACINGS) {
            BlockPos neighborPos = pos.offset(facing);
            if (neighborPos.getX() < chunkX || neighborPos.getX() >= chunkX + 16
                    || neighborPos.getZ() < chunkZ || neighborPos.getZ() >= chunkZ + 16) {
                continue;
            }
            if (!patch.contains(neighborPos) && canPlaceAlgaeAt(world, neighborPos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceAlgaeAt(World world, BlockPos pos) {
        return world.isAirBlock(pos) && isStillWater(world.getBlockState(pos.down()));
    }

    private static boolean canPlaceFloatingLogAt(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getMaterial() == Material.WATER && world.isAirBlock(pos.up());
    }

    private static boolean isStillWater(IBlockState state) {
        if (state.getMaterial() != Material.WATER) {
            return false;
        }
        return !state.getPropertyKeys().contains(BlockLiquid.LEVEL) || state.getValue(BlockLiquid.LEVEL) == 0;
    }

    private static void generateSandDeposit(Random random, int chunkX, int chunkZ, World world, int dimID, Supplier<Block> blockSupplier,
                                            int minY, int yRange, String... biomeIds) {
        if (!Config.isFeatureDimension(dimID) || !matchesBiome(world, chunkX, chunkZ, biomeIds)) {
            return;
        }
        Block block = blockSupplier.get();
        int configuredAttempts = Config.scaleSandAttempts(32);
        Predicate<IBlockState> sandMatcher = net.minecraft.block.state.pattern.BlockMatcher.forBlock(Blocks.SAND);
        FastMinable generator = new FastMinable(block.getDefaultState(), 32, sandMatcher).onlyInBiomes(biomeIds);
        for (int i = 0; i < configuredAttempts; ++i) {
            // WorldGenMinable adds +8 internally. Choose a conservative seed so
            // the complete vein remains in Forge's loaded 2x2 decoration window.
            int x = safeMinableCoordinate(random, chunkX, 32);
            int y = random.nextInt(yRange) + minY;
            int z = safeMinableCoordinate(random, chunkZ, 32);
            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }

    private static void generateMinableDeposit(Random random, int chunkX, int chunkZ, World world, int dimID, int requiredDimension,
                                               Supplier<Block> blockSupplier, double attempts, int minY, int yRange, int veinSize,
                                               Predicate<IBlockState> targetPredicate, String... biomeIds) {
        generateMinableDeposit(random, chunkX, chunkZ, world, dimID, requiredDimension, blockSupplier, attempts, minY, yRange, veinSize,
                targetPredicate, false, biomeIds);
    }

    private static void generateMinableDeposit(Random random, int chunkX, int chunkZ, World world, int dimID, int requiredDimension,
                                               Supplier<Block> blockSupplier, double attempts, int minY, int yRange, int veinSize,
                                               Predicate<IBlockState> targetPredicate, boolean confineToBiomes, String... biomeIds) {
        if (requiredDimension == 0 ? !Config.isFeatureDimension(dimID) : dimID != requiredDimension) {
            return;
        }
        if (biomeIds.length > 0 && !matchesBiome(world, chunkX, chunkZ, biomeIds)) {
            return;
        }
        Block block = blockSupplier.get();
        int configuredAttempts = Config.scaleDepositAttempts(attempts);
        int configuredVeinSize = Config.scaleDepositVeinSize(veinSize);
        FastMinable generator = new FastMinable(block.getDefaultState(), configuredVeinSize, targetPredicate);
        if (confineToBiomes) {
            generator.onlyInBiomes(biomeIds);
        }
        for (int i = 0; i < configuredAttempts; ++i) {
            // WorldGenMinable adds +8 internally. Choose a conservative seed so
            // the complete vein remains in Forge's loaded 2x2 decoration window.
            int x = safeMinableCoordinate(random, chunkX, configuredVeinSize);
            int y = random.nextInt(yRange) + minY;
            int z = safeMinableCoordinate(random, chunkZ, configuredVeinSize);
            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }

    private static int centeredChunkCoordinate(Random random, int chunkStart) {
        return chunkStart + random.nextInt(16);
    }

    public static int safeMinableCoordinate(Random random, int chunkCenter, int veinSize) {
        // Compensate for WorldGenMinable's internal +8 and leave room for its
        // horizontal vein radius inside the loaded 2x2 decoration window.
        int margin = Math.min(8, (veinSize + 7) / 8);
        int span = 16 - margin * 2;
        return span > 0 ? chunkCenter - 8 + margin + random.nextInt(span) : chunkCenter;
    }

    public static boolean matchesBiome(World world, int chunkX, int chunkZ, String... biomeIds) {
        ResourceLocation biomeId = Biome.REGISTRY.getNameForObject(world.getBiome(new BlockPos(chunkX, 128, chunkZ)));
        if (biomeId == null) {
            return false;
        }
        for (String id : biomeIds) {
            if (biomeIdMatches(biomeId, id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether a hard-coded biome entry names the given biome. Like {@link ResourceLocation}, an entry without a
     * namespace is a vanilla biome, so "plains" means "minecraft:plains"; BYG and other mods need the full ID.
     */
    public static boolean biomeIdMatches(ResourceLocation biomeId, String entry) {
        return biomeId.equals(new ResourceLocation(entry));
    }

    /**
     * Matches a user-configured biome list: an empty list allows every biome, otherwise an entry matches either the
     * full registry ID ("byg:byg_alps", "minecraft:desert") or just the registry name ("byg_alps", "desert").
     */
    private static boolean matchesConfiguredBiome(World world, int chunkX, int chunkZ, String[] biomeIds) {
        if (biomeIds.length == 0) {
            return true;
        }
        ResourceLocation biomeId = Biome.REGISTRY.getNameForObject(world.getBiome(new BlockPos(chunkX, 128, chunkZ)));
        if (biomeId == null) {
            return false;
        }
        String full = biomeId.toString();
        String name = biomeId.getPath();
        for (String id : biomeIds) {
            String trimmed = id.trim();
            if (trimmed.equals(full) || trimmed.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesSwampBiome(World world, int chunkX, int chunkZ) {
        return BiomeDictionary.hasType(world.getBiome(new BlockPos(chunkX, 128, chunkZ)), BiomeDictionary.Type.SWAMP);
    }
}
