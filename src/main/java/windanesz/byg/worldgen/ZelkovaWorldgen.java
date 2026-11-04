package windanesz.byg.worldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import windanesz.byg.Config;
import windanesz.byg.registry.ModBlocks;

import java.util.Random;

final class ZelkovaWorldgen {
    private static final ResourceLocation FOREST = new ResourceLocation("byg", "byg_zelkova_forest");
    private static final int MARGIN = 2;

    // T = log, L = leaves, . = empty. Each row runs west to east; rows run north to south.
    private static final String[] FIVE = {
            "...../...../..T../...../.....", // A: bare trunk
            "...../..L../.LTL./..L../.....", // B: small cross
            "..L../...../L.T.L/...../..L..", // C: outer tips
            "..L../.LLL./LLTLL/.LLL./..L..", // D: broad tier
            "...../..L../.LLL./..L../.....", // E
            "...../..L../.LL../..L../.....", // F
            "...../...../.LL../...../.....", // G
            "...../...../..L../...../.....", // H
            "...../..L../..L../...../.....", // I
            "...../...../..LL./...../.....", // J
            "...../..L../..T../...../.....", // K
            "...../.LLL./.LTL./.LLL./.....", // L
            "..L../..L../LLTLL/..L../..L.."  // M
    };
    private static final String[] SEVEN = {
            "......./......./......./...T.../......./......./.......", // A
            "......./......./...L.../..LTL../...L.../......./.......", // B
            "......./...L.../......./.L.T.L./......./...L.../.......", // C
            "...L.../...L.../..LLL../LLLTLLL/..LLL../...L.../...L...", // D
            "......./...L.../...L.../.LLTLL./...L.../...L.../.......", // E
            "......./......./...L.../..LLL../...L.../......./.......", // F
            "......./......./......./..LL.../......./......./.......", // G
            "......./......./......./...L.../......./......./......."  // H
    };
    private static final String[] LAYERS = {
            "AAAAAABCDCBCDCBCDDCBCDCEFGHH",
            "AAAAABCDCBCCCBCDCBCCCEFGHH",
            "AAAAAAABCDCBCDCBCDCBCDCBIHH",
            "AAAABCDDCBCDDCBCDDCBGHH",
            "AAAABCDCBCDCBIHH",
            "AAAKBBCCLCCBIHH",
            "AAABCCMCCMCCBJHH",
            "AAAAAABCCCBCCCBGHH"
    };

    private ZelkovaWorldgen() {
    }

    static void generateAll(Random random, int chunkX, int chunkZ, World world, int dimension) {
        for (int variant = 0; variant < LAYERS.length; variant++) {
            generate(random, chunkX, chunkZ, world, dimension, variant);
        }
    }

    private static void generate(Random random, int chunkX, int chunkZ, World world, int dimension, int variant) {
        int chance = Config.scaleTemplateChance(990000);
        if (!Config.isFeatureDimension(dimension) || chance <= 0 || random.nextInt(1000000) >= chance) {
            return;
        }
        Biome biome = world.getBiome(new BlockPos(chunkX, 128, chunkZ));
        if (world.isRemote || !FOREST.equals(Biome.REGISTRY.getNameForObject(biome))
                || !BygTreePlacement.allowTrees(world, random, new BlockPos(chunkX, 128, chunkZ))) {
            return;
        }

        Rotation rotation = Rotation.values()[random.nextInt(4)];
        Mirror mirror = Mirror.values()[random.nextInt(3)];
        int width = variant == 1 ? 7 : 5;
        int[] bounds = TemplateWorldgenHelper.getFootprintBounds(width, width, rotation, mirror);
        int safeMinX = chunkX - 8 + MARGIN;
        int safeMinZ = chunkZ - 8 + MARGIN;
        int safeMaxX = chunkX - 8 + 31 - MARGIN;
        int safeMaxZ = chunkZ - 8 + 31 - MARGIN;
        int originX = randomSafeOrigin(random, chunkX, bounds[0], bounds[1], safeMinX, safeMaxX);
        int originZ = randomSafeOrigin(random, chunkZ, bounds[2], bounds[3], safeMinZ, safeMaxZ);
        if (originX == Integer.MIN_VALUE || originZ == Integer.MIN_VALUE) {
            return;
        }

        // Scan at the trunk, not the template corner. The corner may sit above a slope.
        int[] trunk = transform(width / 2, width / 2, rotation, mirror);
        int trunkX = originX + trunk[0];
        int trunkZ = originZ + trunk[1];
        int y = world.getActualHeight() - 1;
        while (y > 0 && (world.isAirBlock(new BlockPos(trunkX, y, trunkZ))
                || world.getBlockState(new BlockPos(trunkX, y, trunkZ)).getBlock()
                .isReplaceable(world, new BlockPos(trunkX, y, trunkZ)))) {
            y--;
        }
        if (world.getBlockState(new BlockPos(trunkX, y, trunkZ)).getBlock() != Blocks.GRASS
                || y < 1 || y + LAYERS[variant].length() >= world.getActualHeight()) {
            return;
        }
        if (!FOREST.equals(Biome.REGISTRY.getNameForObject(world.getBiome(new BlockPos(trunkX, 0, trunkZ))))
                || !BygTreePlacement.hasRoom(world, trunkX, trunkZ,
                originX + bounds[0], originX + bounds[1], originZ + bounds[2], originZ + bounds[3])) {
            return;
        }

        IBlockState log = ModBlocks.zelkova_log.getDefaultState();
        IBlockState leaves = ModBlocks.zelkova_leaves.getDefaultState();
        String[] palette = variant == 1 ? SEVEN : FIVE;
        for (int layer = 0; layer < LAYERS[variant].length(); layer++) {
            String[] rows = palette[LAYERS[variant].charAt(layer) - 'A'].split("/");
            for (int z = 0; z < width; z++) {
                for (int x = 0; x < width; x++) {
                    char block = rows[z].charAt(x);
                    if (block == '.') {
                        continue;
                    }
                    int[] offset = transform(x, z, rotation, mirror);
                    world.setBlockState(new BlockPos(originX + offset[0], y - 1 + layer, originZ + offset[1]),
                            block == 'T' ? log : leaves, 2);
                }
            }
        }
    }

    private static int randomSafeOrigin(Random random, int chunkCenter, int min, int max, int safeMin, int safeMax) {
        int centered = chunkCenter - ((min + max) / 2);
        int first = Math.max(centered, safeMin - min);
        int last = Math.min(centered + 15, safeMax - max);
        return first <= last ? first + random.nextInt(last - first + 1) : Integer.MIN_VALUE;
    }

    private static int[] transform(int x, int z, Rotation rotation, Mirror mirror) {
        if (mirror == Mirror.FRONT_BACK) {
            x = -x;
        } else if (mirror == Mirror.LEFT_RIGHT) {
            z = -z;
        }
        switch (rotation) {
            case CLOCKWISE_90:
                return new int[]{-z, x};
            case CLOCKWISE_180:
                return new int[]{-x, -z};
            case COUNTERCLOCKWISE_90:
                return new int[]{z, -x};
            default:
                return new int[]{x, z};
        }
    }
}
