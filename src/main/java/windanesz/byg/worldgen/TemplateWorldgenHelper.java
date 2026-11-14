package windanesz.byg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import windanesz.byg.BiomesYouGo;
import windanesz.byg.registry.ModBlocks;

import java.util.Random;
import java.util.regex.Pattern;

public final class TemplateWorldgenHelper {
    /** Template names that are trees, gated by {@link BygTreePlacement}. Bushes, logs and buildings are not. */
    private static final Pattern TREE_TEMPLATE = Pattern.compile("(af_tree|ancient_tree|baobab_?tree|bayoutree(glow)?|bluespruce"
            + "|bog_tree|borealtree|cikatree|cypresstree|dstree|dtree|evergreen_tree|greatoak_tree|ironwood|mahoganytree(_bird)?"
            + "|mead_tree|northern_tree|orangespruce|orch_tree|palo_verde|rainbow_tree|redoak_tree|redspruce|sc_tree|seasonbirch"
            + "|sf_(tree)?|spruce|tbluespruce|torangespruce|tspruce|tyellowspruce|yellowspruce)_?\\d*");

    private static final Logger LOGGER = LogManager.getLogger(BiomesYouGo.MODID);
    /**
     * Template placement notifies neighboring blocks after writing. Leave two
     * blocks around Forge's loaded decoration window so a boundary liquid can
     * update without its own observer lookup reaching an unloaded chunk.
     */
    private static final int CHUNK_BORDER_MARGIN = 2;
    private static final int PLACE_FLAGS = 2 | 16;

    private TemplateWorldgenHelper() {
    }

    public static Config config(int chancePerMillion, String templatePath, int spawnYOffset, HeightScan heightScan, String[] biomeIds, GroundMatcher... groundMatchers) {
        return new Config(chancePerMillion, templatePath, spawnYOffset, heightScan, biomeIds, groundMatchers);
    }

    public static GroundMatcher block(final IBlockState state) {
        return new GroundMatcher() {
            @Override
            public boolean matches(IBlockState blockAt) {
                return blockAt.getBlock() == state.getBlock();
            }
        };
    }

    /**
     * Matches every block state with the supplied material. This is useful for
     * terrain whose surface can be either a source or flowing liquid.
     */
    public static GroundMatcher material(final Material material) {
        return new GroundMatcher() {
            @Override
            public boolean matches(IBlockState blockAt) {
                return blockAt.getMaterial() == material;
            }
        };
    }

    public static GroundMatcher state(final IBlockState state) {
        return new GroundMatcher() {
            @Override
            public boolean matches(IBlockState blockAt) {
                return blockAt == state;
            }
        };
    }

    public static void generate(Random random, int chunkX, int chunkZ, World world, int dimID,
                                ResourceLocation biomeId, Config config) {
        if (!windanesz.byg.Config.isFeatureDimension(dimID) || world.isRemote || !windanesz.byg.Config.isTemplateEnabled(config.templatePath)) {
            return;
        }
        int configuredChance = windanesz.byg.Config.scaleTemplateChance(config.chancePerMillion);
        if (configuredChance <= 0 || random.nextInt(1000000) >= configuredChance) {
            return;
        }
        if (config.tree && !BygTreePlacement.allowTrees(world, random, new BlockPos(chunkX, 128, chunkZ))) {
            return;
        }

        boolean codeShape = CrystalCanyonWorldgen.BOTTOM_TEMPLATE.equals(config.templatePath);
        if (codeShape && !CrystalCanyonWorldgen.clusterAllows(world, random, chunkX, chunkZ)) {
            return;
        }
        Template template = null;
        try {
            if (!codeShape) template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(), new ResourceLocation("byg", config.templatePath));
        } catch (RuntimeException exception) {
            LOGGER.error("Unable to load or decode template {}.", config.templatePath, exception);
            return;
        }
        if (!codeShape && template == null) {
            return;
        }

        Rotation rotation = randomRotation(random);
        Mirror mirror = randomMirror(random);

        BlockPos size = codeShape
                ? new BlockPos(CrystalCanyonShapes.MESA_X, CrystalCanyonShapes.MESA_Y, CrystalCanyonShapes.WORK_Z)
                : template.getSize();
        int[] bounds = getFootprintBounds(size.getX(), size.getZ(), rotation, mirror);
        
        int sourceChunkMinX = chunkX - 8;
        int sourceChunkMinZ = chunkZ - 8;
        // Forge's +8 decoration offset gives generators a loaded 2x2-chunk
        // window. Templates may use that window but must not extend beyond it.
        int safeMinX = sourceChunkMinX + CHUNK_BORDER_MARGIN;
        int safeMinZ = sourceChunkMinZ + CHUNK_BORDER_MARGIN;
        int safeMaxX = sourceChunkMinX + 31 - CHUNK_BORDER_MARGIN;
        int safeMaxZ = sourceChunkMinZ + 31 - CHUNK_BORDER_MARGIN;

        // Pick from the overlap between the normal decoration area and the
        // origins whose complete template footprint is safe. Choosing first
        // and rejecting afterward made large tree templates disappear often;
        // biomes such as Great Oak Lowlands have no vanilla tree fallback.
        // Canyon templates are 34 blocks long, wider than the safe window, so
        // they can never fit whole. Centre them on the window and clip the
        // overhang instead of skipping them.
        boolean clipToWindow = config.templatePath.startsWith("canyon_");
        int x;
        int z;
        if (clipToWindow) {
            x = clippedOrigin(random, safeMinX, safeMaxX, bounds[0], bounds[1]);
            z = clippedOrigin(random, safeMinZ, safeMaxZ, bounds[2], bounds[3]);
        } else {
            x = randomSafeOrigin(random, chunkX, bounds[0], bounds[1], safeMinX, safeMaxX);
            z = randomSafeOrigin(random, chunkZ, bounds[2], bounds[3], safeMinZ, safeMaxZ);
        }
        int clipMinX = Math.max(x + bounds[0], safeMinX);
        int clipMaxX = Math.min(x + bounds[1], safeMaxX);
        int clipMinZ = Math.max(z + bounds[2], safeMinZ);
        int clipMaxZ = Math.min(z + bounds[3], safeMaxZ);
        if (x == Integer.MIN_VALUE || z == Integer.MIN_VALUE) {
            return;
        }
        
        if (!clipToWindow && (x + bounds[0] < safeMinX || x + bounds[1] > safeMaxX
                || z + bounds[2] < safeMinZ || z + bounds[3] > safeMaxZ)) {
            return;
        }
        
        // A clipped template's origin corner can lie outside the loaded window,
        // so sample the ground at the middle of the part that will be written.
        int scanX = clipToWindow ? (clipMinX + clipMaxX) / 2 : x;
        int scanZ = clipToWindow ? (clipMinZ + clipMaxZ) / 2 : z;
        int height = world.getActualHeight() - 1;
        BlockPos cursor = new BlockPos(scanX, height, scanZ);

        if (config.heightScan == HeightScan.AIR_ONLY) {
            while (height > 0 && world.isAirBlock(cursor)) {
                height--;
                cursor = new BlockPos(scanX, height, scanZ);
            }
        } else {
            while (height > 0 && (isReplaceableOrAir(world, cursor)
                    || (config.templatePath.startsWith("enchanted") && isEnchantedTreeBlock(world.getBlockState(cursor))))) {
                height--;
                cursor = new BlockPos(scanX, height, scanZ);
            }
        }

        int surfaceY = height - 1;
        BlockPos surfacePos = cursor;
        IBlockState blockAt = world.getBlockState(surfacePos);
        if (!matchesGround(blockAt, config.groundMatchers)) {
            return;
        }

        int centerX = x + (bounds[0] + bounds[1]) / 2;
        int centerZ = z + (bounds[2] + bounds[3]) / 2;
        if (!isConfiguredBiome(world, centerX, centerZ, config.biomeIds)
                || (config.tree && !BygTreePlacement.hasRoom(world, centerX, centerZ,
                x + bounds[0], x + bounds[1], z + bounds[2], z + bounds[3]))) {
            return;
        }

        if (isAncientTree(config.templatePath) && !hasAncientTreeClearing(world, centerX, centerZ, height)) {
            return;
        }

        if ("pine_house1".equals(config.templatePath)
                && !hasSupportedFootprint(world, x, z, height, bounds)) {
            return;
        }

        // The prairie farm house is a single flat slab of dirt/cobblestone on layer 0. On a slope the
        // downhill side hung in midair, so refuse steep ground here and fill the gap below after placing.
        boolean farmHouse = "farm_house1".equals(config.templatePath);
        if (farmHouse && !hasShallowGapBelowFootprint(world, x, z, height - 1, bounds)) {
            return;
        }

        // Bluff trees include stone in their saved bases. On the steep Alps,
        // placing one from a single surface sample can leave that base in midair.
        if (config.templatePath.startsWith("blufftree")
                && biomeId != null && "byg:byg_alps".equals(biomeId.toString())
                && !hasFullySupportedFootprint(world, x, z, height, bounds)) {
            return;
        }

        int spawnY = surfaceY + config.spawnYOffset;
        if (config.templatePath.startsWith("orch_tree")) {
            // The height scan above samples the template corner, but these
            // trunks sit near the template centre; on slopes that left the
            // tree floating. Anchor to the ground under the trunk instead.
            BlockPos trunkLocal = orchardTrunkLocalPos(config.templatePath);
            BlockPos trunk = Template.transformedBlockPos(
                    new PlacementSettings().setRotation(rotation).setMirror(mirror), trunkLocal).add(x, 0, z);
            int trunkGroundY = height + 8;
            BlockPos trunkCursor = new BlockPos(trunk.getX(), trunkGroundY, trunk.getZ());
            while (trunkGroundY > 0 && isReplaceableOrAir(world, trunkCursor)) {
                trunkGroundY--;
                trunkCursor = new BlockPos(trunk.getX(), trunkGroundY, trunk.getZ());
            }
            if (!matchesGround(world.getBlockState(trunkCursor), config.groundMatchers)) {
                return;
            }
            spawnY = trunkGroundY + config.spawnYOffset;
        }
        int[][] flowerBase = stemBase(config.templatePath);
        if (flowerBase != null) {
            // The height scan above samples the template corner, but the stems stand elsewhere in the
            // template; on slopes that left the plant hovering. Anchor to the ground under every base
            // stem instead, rest the plant on the lowest one and refuse steeper ground.
            int lowest = Integer.MAX_VALUE;
            int highest = Integer.MIN_VALUE;
            PlacementSettings baseSettings = new PlacementSettings().setRotation(rotation).setMirror(mirror);
            for (int[] base : flowerBase) {
                BlockPos stem = Template.transformedBlockPos(baseSettings, new BlockPos(base[0], 0, base[1])).add(x, 0, z);
                int groundY = height + 8;
                BlockPos stemCursor = new BlockPos(stem.getX(), groundY, stem.getZ());
                while (groundY > 0 && isReplaceableOrAir(world, stemCursor)) {
                    groundY--;
                    stemCursor = new BlockPos(stem.getX(), groundY, stem.getZ());
                }
                if (!matchesGround(world.getBlockState(stemCursor), config.groundMatchers)
                        || config.templatePath.startsWith("cact") && world.getBlockState(stemCursor.up()).getMaterial().isLiquid()) {
                    return;
                }
                lowest = Math.min(lowest, groundY);
                highest = Math.max(highest, groundY);
            }
            if (highest - lowest > 1) {
                return;
            }
            // Same convention as the scan above: the origin layer replaces the ground block it stands on.
            spawnY = lowest - 1 + config.spawnYOffset;
        }
        if (config.templatePath.startsWith("ds_rock")) {
            // These templates were positioned relative to the water surface.
            // The seabed varies by several blocks, leaving some rocks suspended.
            int floorY = waterFloorY(world, x, z);
            int[][] corners = {{bounds[0], bounds[2]}, {bounds[0], bounds[3]},
                    {bounds[1], bounds[2]}, {bounds[1], bounds[3]}};
            for (int[] corner : corners) {
                int cornerFloor = waterFloorY(world, x + corner[0], z + corner[1]);
                if (cornerFloor < 0) return;
                floorY = Math.min(floorY, cornerFloor);
            }
            if (floorY < 1) return;
            spawnY = floorY - 1;
        }
        BlockPos spawnTo = new BlockPos(x, spawnY, z);
        // Evergreen tree templates include air below their canopies. Do not let
        // those saved air blocks carve holes through the terrain beneath them.
        Block replacedBlock = config.templatePath.startsWith("evergreen_tree") ? Blocks.AIR : null;
        PlacementSettings placement = new PlacementSettings().setRotation(rotation).setMirror(mirror)
                .setReplacedBlock(replacedBlock).setIgnoreStructureBlock(false).setIgnoreEntities(false);
        if (clipToWindow) {
            placement.setBoundingBox(new StructureBoundingBox(clipMinX, 0, clipMinZ, clipMaxX, 255, clipMaxZ));
        }
        if (codeShape) {
            CrystalCanyonWorldgen.place(world, random, spawnTo, rotation, mirror, clipMinX, clipMaxX, clipMinZ, clipMaxZ);
        } else {
            placeTemplateWithSettings(world, template, spawnTo, placement);
            if (farmHouse) {
                fillFoundationBelow(world, spawnTo, bounds);
            }
            if (config.templatePath.startsWith("bayoutree")
                    && ("byg:byg_glowshroom_bayou".equals(String.valueOf(biomeId))
                    || "byg:byg_bayou".equals(String.valueOf(biomeId)))) {
                extendBayouTreeRoots(world, spawnTo, size, bounds);
            }
        }
    }

    private static void extendBayouTreeRoots(World world, BlockPos origin, BlockPos size, int[] bounds) {
        for (int x = origin.getX() + bounds[0]; x <= origin.getX() + bounds[1]; x++) {
            for (int z = origin.getZ() + bounds[2]; z <= origin.getZ() + bounds[3]; z++) {
                for (int y = origin.getY(); y < origin.getY() + size.getY(); y++) {
                    BlockPos base = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(base);
                    if (base.getY() > world.getSeaLevel() || state.getMaterial() != Material.WOOD
                            || world.getBlockState(base.down()).getMaterial() != Material.WATER) {
                        continue;
                    }
                    BlockPos root = base.down();
                    while (root.getY() > 0 && world.getBlockState(root).getMaterial() == Material.WATER) {
                        world.setBlockState(root, state, PLACE_FLAGS);
                        root = root.down();
                    }
                    break;
                }
            }
        }
    }

    private static boolean isConfiguredBiome(World world, int x, int z, ResourceLocation[] biomeIds) {
        ResourceLocation biomeId = Biome.REGISTRY.getNameForObject(world.getBiome(new BlockPos(x, 0, z)));
        for (ResourceLocation allowed : biomeIds) {
            if (allowed.equals(biomeId)) {
                return true;
            }
        }
        return false;
    }

    private static BlockPos orchardTrunkLocalPos(String path) {
        switch (path) {
            case "orch_tree2": return new BlockPos(6, 0, 6);
            case "orch_tree3": return new BlockPos(7, 0, 6);
            case "orch_tree4": return new BlockPos(6, 0, 5);
            default: return new BlockPos(5, 0, 5);
        }
    }

    /** Template-local x/z of every stem or trunk block in the bottom layer of the templates that must stand on the ground under each of those blocks. */
    private static int[][] stemBase(String path) {
        switch (path) {
            case "greatoak_tree1":
            case "greatoak_tree2":
            case "greatoak_tree3": return new int[][]{{5, 7}, {6, 6}, {6, 7}, {6, 8}, {7, 5}, {7, 6}, {7, 7}, {7, 8}, {7, 9}, {8, 6}, {8, 7}, {8, 8}, {9, 7}};
            case "greatoak_tree4": return new int[][]{{6, 8}, {7, 7}, {7, 8}, {7, 9}, {8, 6}, {8, 7}, {8, 8}, {8, 9}, {8, 10}, {9, 7}, {9, 8}, {9, 9}, {10, 8}};
            case "greatoak_tree5": return new int[][]{{4, 7}, {5, 6}, {5, 7}, {6, 5}, {6, 6}, {6, 7}, {6, 8}, {7, 7}};
            case "flower1": return new int[][]{{3, 4}, {4, 4}, {4, 5}};
            case "flower2": return new int[][]{{4, 3}, {5, 2}, {5, 3}, {6, 6}, {6, 7}, {7, 7}};
            case "redoak_tree1":
            case "redoak_tree2":
            case "redoak_tree3":
            case "redoak_tree4":
            case "redoak_tree5":
            case "redoak_tree6":
            case "flower3":
            case "flower6":
            case "flower7":
            case "cact1":
            case "cact2":
            case "cact3":
            case "cact4": return new int[][]{{2, 2}};
            case "flower4":
            case "flower5": return new int[][]{{2, 5}, {2, 6}, {3, 4}, {3, 5}, {4, 5}};
            case "flower8": return new int[][]{{5, 5}};
            case "redoak_tree7": return new int[][]{{6, 5}};
            case "mahoganytree1":
            case "mahoganytree2":
            case "mahoganytree3":
            case "mahoganytree4":
            case "mahoganytree5":
            case "mahoganytree8": return new int[][]{{4, 4}};
            case "mahoganytree6":
            case "mahoganytree_bird": return new int[][]{{5, 5}};
            case "mahoganytree7": return new int[][]{{6, 6}};
            case "sc_tree1": return new int[][]{{8, 5}, {9, 5}, {8, 6}, {9, 6}};
            case "sc_tree2": return new int[][]{{7, 4}};
            case "sc_tree3": return new int[][]{{6, 3}};
            case "sc_tree4": return new int[][]{{4, 6}, {3, 7}, {4, 7}, {5, 7}, {4, 8}};
            case "sc_tree5": return new int[][]{{5, 4}, {6, 4}, {5, 5}, {6, 5}};
            case "sc_tree6": return new int[][]{{4, 6}};
            case "sc_tree7": return new int[][]{{6, 7}};
            case "sc_tree8": return new int[][]{{6, 5}, {5, 6}};
            case "sc_tree9": return new int[][]{{4, 4}};
            case "flower9": return new int[][]{{4, 1}, {4, 2}, {5, 2}};
            case "bluespruce1":
            case "bluespruce2":
            case "borealtree1":
            case "borealtree2":
            case "borealtree4":
            case "borealtree5":
            case "borealtree6":
            case "borealtree7":
            case "dstree1":
            case "dstree2":
            case "dstree3":
            case "dstree4":
            case "dstree6":
            case "dtree1":
            case "dtree2":
            case "dtree3":
            case "dtree4":
            case "dtree6":
            case "evergreen_tree1":
            case "evergreen_tree2":
            case "mead_tree2":
            case "mead_tree3":
            case "mead_tree4":
            case "orangespruce1":
            case "orangespruce2":
            case "redspruce1":
            case "redspruce2":
            case "seasonbirch1":
            case "seasonbirch2":
            case "seasonbirch3":
            case "seasonbirch4":
            case "seasonbirch5":
            case "seasonbirch6":
            case "seasonbirch7":
            case "seasonbirch8":
            case "seasonbirch9":
            case "sf_10":
            case "sf_11":
            case "sf_13":
            case "sf_14":
            case "sf_15":
            case "sf_16":
            case "sf_18":
            case "sf_tree1":
            case "sf_tree2":
            case "sf_tree3":
            case "sf_tree4":
            case "sf_tree5":
            case "sf_tree6":
            case "sf_tree7":
            case "sf_tree8":
            case "spruce1":
            case "spruce2":
            case "yellowspruce1":
            case "yellowspruce2": return new int[][]{{2, 2}};
            case "bluespruce3":
            case "bluespruce4":
            case "bluespruce6":
            case "borealtree3":
            case "ironwood2":
            case "northern_tree1":
            case "northern_tree2":
            case "orangespruce3":
            case "orangespruce4":
            case "orangespruce6":
            case "redspruce3":
            case "redspruce4":
            case "redspruce6":
            case "sf_12":
            case "spruce3":
            case "spruce4":
            case "spruce6":
            case "yellowspruce3":
            case "yellowspruce4":
            case "yellowspruce6": return new int[][]{{3, 3}};
            case "bluespruce5":
            case "bog_tree1":
            case "bog_tree2":
            case "bog_tree4":
            case "bog_tree5":
            case "ironwood1":
            case "orangespruce5":
            case "redspruce5":
            case "spruce5":
            case "yellowspruce5": return new int[][]{{1, 1}};
            case "bog_tree3": return new int[][]{{2, 1}};
            case "dstree5":
            case "dtree5":
            case "ironwood3": return new int[][]{{3, 4}};
            case "mead_tree1": return new int[][]{{6, 6}};
            case "northern_tree3":
            case "tbluespruce1":
            case "tbluespruce2":
            case "torangespruce1":
            case "torangespruce2":
            case "tspruce1":
            case "tspruce2":
            case "tyellowspruce1":
            case "tyellowspruce2": return new int[][]{{4, 4}, {4, 5}, {5, 4}, {5, 5}};
            case "northern_tree4": return new int[][]{{7, 7}, {7, 8}, {8, 7}, {8, 8}};
            case "palo_verde3": return new int[][]{{3, 6}, {4, 5}};
            case "sf_17": return new int[][]{{3, 6}};
            case "sf_tree9": return new int[][]{{6, 4}};
            case "tbluespruce3":
            case "torangespruce3":
            case "tspruce3":
            case "tyellowspruce3": return new int[][]{{3, 3}, {3, 4}, {4, 3}, {4, 4}};
            default: return null;
        }
    }

    private static int waterFloorY(World world, int x, int z) {
        BlockPos pos = new BlockPos(x, world.getSeaLevel(), z);
        for (int i = 0; i < 3 && world.isAirBlock(pos); i++) pos = pos.down();
        if (world.getBlockState(pos).getMaterial() != Material.WATER) return -1;
        while (pos.getY() > 1 && world.getBlockState(pos.down()).getMaterial() == Material.WATER) {
            pos = pos.down();
        }
        return pos.getY();
    }

    private static boolean isAncientTree(String path) {
        return path.startsWith("ancient_tree") || "af_tree4".equals(path);
    }

    private static boolean hasAncientTreeClearing(World world, int trunkX, int trunkZ, int groundY) {
        // The old nine-point, same-height grass check rejected nearly every
        // uneven forest floor. Require only the trunk and most immediate roots
        // to have grass beneath them, allowing a one-block slope.
        int[][] offsets = {{0, 0}, {2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        int supported = 0;
        for (int[] offset : offsets) {
            int sampleX = trunkX + offset[0];
            int sampleZ = trunkZ + offset[1];
            for (int dy = -1; dy <= 1; dy++) {
                if (world.getBlockState(new BlockPos(sampleX, groundY + dy, sampleZ)).getBlock() == Blocks.GRASS) {
                    supported++;
                    break;
                }
            }
        }
        return supported >= 3 && world.getBlockState(new BlockPos(trunkX, groundY, trunkZ)).getBlock() == Blocks.GRASS;
    }


    public static void placeTemplateWithSettings(World world, Template template, BlockPos spawnTo, PlacementSettings settings) {
        template.addBlocksToWorld(world, spawnTo, settings, PLACE_FLAGS);
    }

    public static boolean fitsCurrentChunk(Template template, BlockPos origin, Rotation rotation, Mirror mirror, int sourceChunkMinX, int sourceChunkMinZ) {
        BlockPos size = template.getSize();
        int[] bounds = getFootprintBounds(size.getX(), size.getZ(), rotation, mirror);
        int safeMinX = sourceChunkMinX + CHUNK_BORDER_MARGIN;
        int safeMinZ = sourceChunkMinZ + CHUNK_BORDER_MARGIN;
        int safeMaxX = sourceChunkMinX + 31 - CHUNK_BORDER_MARGIN;
        int safeMaxZ = sourceChunkMinZ + 31 - CHUNK_BORDER_MARGIN;
        return origin.getX() + bounds[0] >= safeMinX
                && origin.getX() + bounds[1] <= safeMaxX
                && origin.getZ() + bounds[2] >= safeMinZ
                && origin.getZ() + bounds[3] <= safeMaxZ;
    }

    public static boolean fitsCurrentChunk(Template template, BlockPos origin, Rotation rotation, Mirror mirror, BlockPos sourcePos) {
        int sourceChunkMinX = ((sourcePos.getX() - 8) >> 4) << 4;
        int sourceChunkMinZ = ((sourcePos.getZ() - 8) >> 4) << 4;
        return fitsCurrentChunk(template, origin, rotation, mirror, sourceChunkMinX, sourceChunkMinZ);
    }

    public static PlacementSettings chunkPlacementSettings(Rotation rotation, Mirror mirror, int sourceChunkMinX, int sourceChunkMinZ) {
        return new PlacementSettings().setRotation(rotation).setMirror(mirror)
                .setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false);
    }

    public static PlacementSettings chunkPlacementSettings(Random random, Rotation rotation, Mirror mirror, BlockPos sourcePos) {
        int sourceChunkMinX = ((sourcePos.getX() - 8) >> 4) << 4;
        int sourceChunkMinZ = ((sourcePos.getZ() - 8) >> 4) << 4;
        return chunkPlacementSettings(rotation, mirror, sourceChunkMinX, sourceChunkMinZ).setRandom(random);
    }

    private static int sourceChunkMin(int blockCoordinate) {
        return (blockCoordinate >> 4) << 4;
    }

    public static int[] getFootprintBounds(int sizeX, int sizeZ, Rotation rotation, Mirror mirror) {
        int[][] corners = new int[][]{
                {0, 0},
                {Math.max(0, sizeX - 1), 0},
                {0, Math.max(0, sizeZ - 1)},
                {Math.max(0, sizeX - 1), Math.max(0, sizeZ - 1)}
        };
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (int[] corner : corners) {
            int x = corner[0];
            int z = corner[1];
            if (mirror == Mirror.FRONT_BACK) {
                x = -x;
            } else if (mirror == Mirror.LEFT_RIGHT) {
                z = -z;
            }
            int transformedX;
            int transformedZ;
            switch (rotation) {
                case CLOCKWISE_90:
                    transformedX = -z;
                    transformedZ = x;
                    break;
                case CLOCKWISE_180:
                    transformedX = -x;
                    transformedZ = -z;
                    break;
                case COUNTERCLOCKWISE_90:
                    transformedX = z;
                    transformedZ = -x;
                    break;
                case NONE:
                default:
                    transformedX = x;
                    transformedZ = z;
                    break;
            }
            minX = Math.min(minX, transformedX);
            maxX = Math.max(maxX, transformedX);
            minZ = Math.min(minZ, transformedZ);
            maxZ = Math.max(maxZ, transformedZ);
        }
        return new int[]{minX, maxX, minZ, maxZ};
    }

    private static boolean isReplaceableOrAir(World world, BlockPos pos) {
        return world.isAirBlock(pos) || world.getBlockState(pos).getBlock().isReplaceable((IBlockAccess) world, pos);
    }

    private static boolean hasSupportedFootprint(World world, int originX, int originZ, int groundY, int[] bounds) {
        int minX = originX + bounds[0];
        int maxX = originX + bounds[1];
        int minZ = originZ + bounds[2];
        int maxZ = originZ + bounds[3];
        int[] xs = {minX, (minX + maxX) / 2, maxX};
        int[] zs = {minZ, (minZ + maxZ) / 2, maxZ};
        for (int sampleX : xs) {
            for (int sampleZ : zs) {
                BlockPos belowBase = new BlockPos(sampleX, groundY - 1, sampleZ);
                if (isReplaceableOrAir(world, belowBase)
                        || !world.getBlockState(belowBase).getMaterial().isSolid()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static final int MAX_FOUNDATION_DEPTH = 5;

    /** True when every column of the footprint has ground within {@link #MAX_FOUNDATION_DEPTH} blocks below the template's bottom layer. */
    private static boolean hasShallowGapBelowFootprint(World world, int originX, int originZ, int bottomY, int[] bounds) {
        for (int x = originX + bounds[0]; x <= originX + bounds[1]; x++) {
            for (int z = originZ + bounds[2]; z <= originZ + bounds[3]; z++) {
                int depth = 0;
                BlockPos pos = new BlockPos(x, bottomY - 1, z);
                while (depth <= MAX_FOUNDATION_DEPTH && pos.getY() > 0 && isReplaceableOrAir(world, pos)) {
                    pos = pos.down();
                    depth++;
                }
                if (depth > MAX_FOUNDATION_DEPTH) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Pillars dirt down from every solid block on the template's bottom layer until it meets ground. */
    private static void fillFoundationBelow(World world, BlockPos origin, int[] bounds) {
        IBlockState fill = Blocks.DIRT.getDefaultState();
        for (int x = origin.getX() + bounds[0]; x <= origin.getX() + bounds[1]; x++) {
            for (int z = origin.getZ() + bounds[2]; z <= origin.getZ() + bounds[3]; z++) {
                if (!world.getBlockState(new BlockPos(x, origin.getY(), z)).getMaterial().isSolid()) {
                    continue;
                }
                BlockPos pos = new BlockPos(x, origin.getY() - 1, z);
                for (int depth = 0; depth <= MAX_FOUNDATION_DEPTH && pos.getY() > 0 && isReplaceableOrAir(world, pos); depth++) {
                    world.setBlockState(pos, fill, PLACE_FLAGS);
                    pos = pos.down();
                }
            }
        }
    }

    private static boolean hasFullySupportedFootprint(World world, int originX, int originZ, int groundY, int[] bounds) {
        for (int x = originX + bounds[0]; x <= originX + bounds[1]; x++) {
            for (int z = originZ + bounds[2]; z <= originZ + bounds[3]; z++) {
                BlockPos ground = new BlockPos(x, groundY, z);
                if (isReplaceableOrAir(world, ground)
                        || !world.getBlockState(ground).getMaterial().isSolid()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isEnchantedTreeBlock(IBlockState state) {
        Block block = state.getBlock();
        return block == ModBlocks.enchanted_log || block == ModBlocks.enchanted_leaves_blue
                || block == ModBlocks.enchanted_leaves_pink || block == ModBlocks.enchanted_leaves_purple;
    }

    private static int clippedOrigin(Random random, int safeMin, int safeMax, int footprintMin, int footprintMax) {
        int slack = (safeMax - safeMin) - (footprintMax - footprintMin);
        if (slack >= 0) {
            return safeMin - footprintMin + random.nextInt(slack + 1);
        }
        int centred = (safeMin + safeMax) / 2 - (footprintMin + footprintMax) / 2;
        return centred + random.nextInt(7) - 3;
    }

    private static int randomSafeOrigin(Random random, int chunkCenter, int footprintMin, int footprintMax,
                                        int safeMin, int safeMax) {
        int centeredOrigin = chunkCenter - ((footprintMin + footprintMax) / 2);
        int minOrigin = Math.max(centeredOrigin, safeMin - footprintMin);
        int maxOrigin = Math.min(centeredOrigin + 15, safeMax - footprintMax);
        return minOrigin <= maxOrigin ? minOrigin + random.nextInt(maxOrigin - minOrigin + 1) : Integer.MIN_VALUE;
    }

    private static boolean matchesGround(IBlockState blockAt, GroundMatcher[] groundMatchers) {
        for (GroundMatcher groundMatcher : groundMatchers) {
            if (groundMatcher.matches(blockAt)) {
                return true;
            }
        }
        return false;
    }

    private static Rotation randomRotation(Random random) {
        Rotation rotation = Rotation.NONE;
        int rotationIndex = random.nextInt(4);
        if (rotationIndex == 1) {
            rotation = Rotation.CLOCKWISE_90;
        } else if (rotationIndex == 2) {
            rotation = Rotation.CLOCKWISE_180;
        } else if (rotationIndex == 3) {
            rotation = Rotation.COUNTERCLOCKWISE_90;
        }
        return rotation;
    }

    private static Mirror randomMirror(Random random) {
        Mirror mirror = Mirror.NONE;
        int mirrorIndex = random.nextInt(3);
        if (mirrorIndex == 1) {
            mirror = Mirror.LEFT_RIGHT;
        } else if (mirrorIndex == 2) {
            mirror = Mirror.FRONT_BACK;
        }
        return mirror;
    }

    public enum HeightScan {
        REPLACEABLE,
        AIR_ONLY
    }

    public interface GroundMatcher {
        boolean matches(IBlockState blockAt);
    }

    public static final class Config {
        private final int chancePerMillion;
        private final String templatePath;
        private final int spawnYOffset;
        private final HeightScan heightScan;
        private final GroundMatcher[] groundMatchers;
        private final ResourceLocation[] biomeIds;
        private final boolean tree;

        private Config(int chancePerMillion, String templatePath, int spawnYOffset, HeightScan heightScan, String[] biomeIds, GroundMatcher[] groundMatchers) {
            this.chancePerMillion = chancePerMillion;
            this.templatePath = templatePath;
            this.spawnYOffset = spawnYOffset;
            this.heightScan = heightScan;
            this.groundMatchers = groundMatchers;
            this.tree = TREE_TEMPLATE.matcher(templatePath).matches();
            this.biomeIds = new ResourceLocation[biomeIds.length];
            for (int index = 0; index < biomeIds.length; index++) {
                this.biomeIds[index] = new ResourceLocation(biomeIds[index]);
            }
        }

        ResourceLocation[] biomeIds() {
            return biomeIds;
        }
    }
}
