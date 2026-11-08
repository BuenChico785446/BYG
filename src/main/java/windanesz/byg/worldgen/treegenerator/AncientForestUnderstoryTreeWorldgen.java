package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import windanesz.byg.Config;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.AncientForestFloorWorldgen;
import windanesz.byg.worldgen.BygTreePlacement;
import windanesz.byg.worldgen.BygWorldGenerator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public final class AncientForestUnderstoryTreeWorldgen {
    private AncientForestUnderstoryTreeWorldgen() {
    }

    public static void generate(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || !BygWorldGenerator.matchesBiome(world, chunkX, chunkZ, "byg:byg_ancient_forest")) return;
        int attempts = Config.scaleClusterPlantAttempts(16);
        for (int i = 0; i < attempts; i++) {
            int x = chunkX + random.nextInt(16);
            int z = chunkZ + random.nextInt(16);
            if (!BygWorldGenerator.matchesBiome(world, x, z, "byg:byg_ancient_forest")) continue;
            BlockPos ground = AncientForestFloorWorldgen.findGround(world, x, z);
            if (ground == null || !hasRootRoom(world, ground) || !BygTreePlacement.allowTrees(world, random, ground)) continue;
            placeTree(world, random, ground);
        }
    }

    private static boolean hasRootRoom(World world, BlockPos ground) {
        int supported = 0;
        for (int[] offset : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            BlockPos nearby = AncientForestFloorWorldgen.findGround(world,
                    ground.getX() + offset[0], ground.getZ() + offset[1]);
            if (nearby != null && Math.abs(nearby.getY() - ground.getY()) <= 1) supported++;
        }
        if (supported < 3) return false;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx * dx + dz * dz > 4) continue;
                for (int dy = 1; dy <= 4; dy++) {
                    if (world.getBlockState(ground.add(dx, dy, dz)).getMaterial() == Material.WOOD) return false;
                }
            }
        }
        return true;
    }

    private static boolean placeTree(World world, Random random, BlockPos ground) {
        boolean birch = random.nextInt(5) == 0;
        int height = 7 + random.nextInt(5);
        int leanX = random.nextInt(3) - 1;
        int leanZ = random.nextInt(3) - 1;
        int baseX = ground.getX();
        int baseY = ground.getY();
        int baseZ = ground.getZ();
        IBlockState log = birch
                ? Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH)
                : Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        IBlockState leaves = birch
                ? Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
                        .withProperty(BlockLeaves.DECAYABLE, false)
                : ModBlocks.great_oak_leaves.getDefaultState();
        Map<BlockPos, IBlockState> shape = new LinkedHashMap<>();
        int crownX = baseX + leanX;
        int crownZ = baseZ + leanZ;
        addCrown(shape, random, crownX, baseY + height - 2, crownZ, height >= 10 ? 4 : 3, leaves);
        for (int branch = 0; branch < 2; branch++) {
            int dx = branch == 0 ? 1 : -1;
            int dz = random.nextBoolean() ? 1 : -1;
            int branchY = baseY + height - 3 + branch;
            int branchX = crownX + dx * 2;
            int branchZ = crownZ + dz;
            addCrown(shape, random, branchX, branchY + 1, branchZ, 3, leaves);
            shape.put(new BlockPos(crownX + dx, branchY, crownZ),
                    log.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X));
            shape.put(new BlockPos(branchX, branchY, crownZ),
                    log.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X));
            shape.put(new BlockPos(branchX, branchY, branchZ),
                    log.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z));
        }
        for (int y = 1; y <= height; y++) {
            int x = baseX + (y > height / 2 ? leanX : 0);
            int z = baseZ + (y > height / 2 + 1 ? leanZ : 0);
            if (y == height / 2 + 1 && leanX != 0) {
                shape.put(new BlockPos(baseX, baseY + y, baseZ), log);
            }
            if (y == height / 2 + 2 && leanZ != 0) {
                shape.put(new BlockPos(baseX + leanX, baseY + y, baseZ), log);
            }
            shape.put(new BlockPos(x, baseY + y, z), log);
        }
        BlockPos top = new BlockPos(crownX, baseY + height, crownZ);
        shape.put(top.up(), leaves);
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            shape.put(top.offset(facing), leaves);
        }

        for (Map.Entry<BlockPos, IBlockState> block : shape.entrySet()) {
            BlockPos pos = block.getKey();
            if (!BygWorldGenerator.matchesBiome(world, pos.getX(), pos.getZ(), "byg:byg_ancient_forest")) return false;
            IBlockState existing = world.getBlockState(pos);
            if (block.getValue() == leaves) continue;
            if (existing.getBlock().isLeaves(existing, world, pos)) continue;
            if (!world.isAirBlock(pos) && !existing.getBlock().isReplaceable(world, pos)) return false;
        }
        for (Map.Entry<BlockPos, IBlockState> block : shape.entrySet()) {
            BlockPos pos = block.getKey();
            IBlockState existing = world.getBlockState(pos);
            if (block.getValue() != leaves || world.isAirBlock(pos)
                    || existing.getBlock().isReplaceable(world, pos)) {
                world.setBlockState(pos, block.getValue(), 2);
            }
        }
        return true;
    }

    private static void addCrown(Map<BlockPos, IBlockState> shape, Random random,
                                 int cx, int cy, int cz, int radius, IBlockState leaves) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double distance = (double) (dx * dx + dz * dz) / (radius * radius)
                            + (double) (dy * dy) / 5;
                    if (distance < 1.25 - random.nextDouble() * 0.28) {
                        shape.put(new BlockPos(cx + dx, cy + dy, cz + dz), leaves);
                    }
                }
            }
        }
    }
}
