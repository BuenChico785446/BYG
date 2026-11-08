package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.BygTreePlacement;

import java.util.Random;

public final class RedwoodTreeGenerator extends WorldGenAbstractTree {
    private static final int[] HEIGHTS = {18, 20, 22, 19, 15, 33, 29, 36};
    private static final int[] RADII = {5, 5, 6, 5, 4, 7, 6, 8};
    private static final int[][] DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public RedwoodTreeGenerator() {
        super(false);
    }

    /** Grow the same variants used by the biome from a complete 3x3 sapling group. */
    public boolean growFromSaplings(World world, Random random, BlockPos center) {
        BlockPos[] saplings = new BlockPos[9];
        IBlockState[] states = new IBlockState[9];
        int index = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos sapling = center.add(x, 0, z);
                IBlockState state = world.getBlockState(sapling);
                if (state.getBlock() != ModBlocks.redwood_sapling) {
                    return false;
                }
                saplings[index] = sapling;
                states[index++] = state;
            }
        }
        for (BlockPos sapling : saplings) {
            world.setBlockState(sapling, Blocks.AIR.getDefaultState(), 2);
        }
        if (generate(world, random, center, false)) {
            return true;
        }
        for (int i = 0; i < saplings.length; i++) {
            world.setBlockState(saplings[i], states[i], 2);
        }
        return false;
    }

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        return generate(world, random, position, true);
    }

    private boolean generate(World world, Random random, BlockPos position, boolean keepApart) {
        // The giant form is deliberately rarer than the five ordinary forms.
        int choice = random.nextInt(16);
        int variant = choice < 10 ? choice % 5 : choice == 10 ? 5 : choice < 13 ? 6 : 7;
        int height = HEIGHTS[variant] + random.nextInt(5) - 2;
        int radius = RADII[variant];
        if (position.getY() < 1 || position.getY() + height + 1 >= world.getHeight()
                || !world.isAreaLoaded(position.add(-radius, 0, -radius),
                position.add(radius, height + 1, radius))
                || !canGrowOn(world, position.down())
                || (keepApart && !BygTreePlacement.hasRoom(world, position.getX(), position.getZ(),
                position.getX() - radius, position.getX() + radius, position.getZ() - radius, position.getZ() + radius))) {
            return false;
        }
        for (int y = 0; y < height; y++) {
            if (!isReplaceable(world, position.up(y))) {
                return false;
            }
        }
        if ((variant == 6 || variant == 7) && !canPlaceReferenceTrunk(world, position, height)) {
            return false;
        }

        IBlockState log = ModBlocks.redwood_log.getDefaultState();
        IBlockState leaves = ModBlocks.redwood_leaves.getDefaultState();
        placeTrunk(world, random, position, height, variant, log);
        switch (variant) {
            case 0:
            case 3:
                // Redwood 1 and 4 have low, uneven limbs and open upper crowns.
                placeLowLimbs(world, random, position, variant, log, leaves);
                placeCrown(world, random, position.up(height - 7), 2, 2, leaves);
                placeCrown(world, random, position.up(height - 3), 2, 2, leaves);
                break;
            case 1:
                // Redwood 2 has four widely spaced whorls.
                placeWhorls(world, random, position, height, 4, 3, log, leaves);
                break;
            case 2:
                // Redwood 3 has a thick stem and five broad bough tiers.
                placeWhorls(world, random, position, height, 5, 4, log, leaves);
                break;
            case 4:
                // Redwood 5 is shorter and has three narrow tiers.
                placeWhorls(world, random, position, height, 3, 2, log, leaves);
                break;
            case 5:
                // Redwood 6 is the giant with repeated, tapering limb tiers.
                placeWhorls(world, random, position, height, 7, 5, log, leaves);
                break;
            case 6:
                // Broad, separated foliage shelves above a long clear trunk.
                placeReferenceTrunk(world, position, height, log);
                placeReferenceTier(world, random, position.up(height - 15), 3, log, leaves);
                placeReferenceTier(world, random, position.up(height - 10), 5, log, leaves);
                placeReferenceTier(world, random, position.up(height - 5), 4, log, leaves);
                placeReferenceTier(world, random, position.up(height - 3), 2, log, leaves);
                break;
            case 7:
                // Tall conical crown with horizontal boughs and a bare lower trunk.
                placeReferenceTrunk(world, position, height, log);
                placeConicalBoughs(world, random, position, height, log, leaves);
                break;
            default:
                break;
        }
        if (variant != 6 && variant != 7) {
            placeCrown(world, random, position.up(height - 2), 1, 2, leaves);
        }
        placeCap(world, position.up(height - 1), leaves);
        return true;
    }

    private void placeCap(World world, BlockPos top, IBlockState leaves) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                placeLeaf(world, top.add(x, 0, z), leaves);
                if (x == 0 || z == 0) {
                    placeLeaf(world, top.add(x, 1, z), leaves);
                }
            }
        }
        placeLeaf(world, top.up(2), leaves);
    }

    private void placeLeaf(World world, BlockPos pos, IBlockState leaves) {
        if (isReplaceable(world, pos)) {
            setBlockAndNotifyAdequately(world, pos, leaves);
        }
    }

    private boolean canPlaceReferenceTrunk(World world, BlockPos base, int height) {
        int[][] corners = {{1, 0}, {0, 1}, {1, 1}};
        for (int[] corner : corners) {
            BlockPos column = base.add(corner[0], 0, corner[1]);
            if (supportDepth(world, column) == 0) {
                return false;
            }
            for (int y = 0; y < height - 1; y++) {
                if (!isReplaceable(world, column.up(y))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void placeReferenceTrunk(World world, BlockPos base, int height, IBlockState log) {
        // A two-block-wide core gives this tree the heavy trunk in the reference.
        int[][] corners = {{1, 0}, {0, 1}, {1, 1}};
        for (int[] corner : corners) {
            BlockPos column = base.add(corner[0], 0, corner[1]);
            for (int y = 1 - supportDepth(world, column); y < height - 1; y++) {
                placeLog(world, column.up(y), log);
            }
        }
    }

    private void placeReferenceTier(World world, Random random, BlockPos center, int radius,
                                    IBlockState log, IBlockState leaves) {
        for (int[] direction : DIRECTIONS) {
            int reach = Math.max(1, radius - 1 - random.nextInt(2));
            for (int distance = 1; distance <= reach; distance++) {
                placeLog(world, center.add(direction[0] * distance, 0,
                        direction[1] * distance), limbLog(log, direction));
            }
        }
        // A shallow rounded shelf leaves a visible gap before the next tier.
        placeCrown(world, random, center.up(), radius, 2, leaves);
    }

    private void placeConicalBoughs(World world, Random random, BlockPos base, int height,
                                    IBlockState log, IBlockState leaves) {
        int crownBase = height / 2 - 2;
        int crownHeight = height - crownBase;
        for (int y = crownBase; y < height; y += 3) {
            int reach = Math.max(1, 6 - (y - crownBase) * 5 / crownHeight);
            int offset = random.nextInt(2);
            for (int i = 0; i < DIRECTIONS.length; i++) {
                if ((i + offset) % 4 == 0 && random.nextBoolean()) {
                    continue;
                }
                int[] direction = DIRECTIONS[i];
                int branchReach = Math.max(1, reach - random.nextInt(2));
                for (int distance = 1; distance <= branchReach; distance++) {
                    placeLog(world, base.add(direction[0] * distance, y + distance / 4,
                            direction[1] * distance), limbLog(log, direction));
                }
                BlockPos tip = base.add(direction[0] * branchReach,
                        y + branchReach / 4, direction[1] * branchReach);
                placeCrown(world, random, tip, branchReach >= 4 ? 2 : 1, 1, leaves);
            }
            placeCrown(world, random, base.up(y + 1), Math.max(1, reach - 2), 1, leaves);
        }
        placeCrown(world, random, base.up(height - 1), 1, 2, leaves);
    }

    private void placeTrunk(World world, Random random, BlockPos base, int height,
                            int variant, IBlockState log) {
        for (int y = 0; y < height; y++) {
            placeLog(world, base.up(y), log);
        }
        int omittedSide = random.nextBoolean() ? random.nextInt(4) : -1;
        for (int i = 0; i < 4; i++) {
            if (i == omittedSide) {
                continue;
            }
            BlockPos side = base.add(DIRECTIONS[i][0], 0, DIRECTIONS[i][1]);
            int supportDepth = supportDepth(world, side);
            int supportHeight = (variant == 5 ? 3 : 2) + random.nextInt(2);
            if (supportDepth == 0 || !clearSupportColumn(world, side, supportHeight)) {
                continue;
            }
            for (int y = 1 - supportDepth; y < supportHeight; y++) {
                placeLog(world, side.up(y), log);
            }
        }
    }

    private int supportDepth(World world, BlockPos side) {
        for (int depth = 1; depth <= 3; depth++) {
            BlockPos below = side.down(depth);
            if (!world.isBlockLoaded(below)) {
                return 0;
            }
            if (canGrowOn(world, below)) {
                return depth;
            }
            if (!isReplaceable(world, below)) {
                return 0;
            }
        }
        return 0;
    }

    private boolean clearSupportColumn(World world, BlockPos side, int height) {
        for (int y = 0; y < height; y++) {
            if (!isReplaceable(world, side.up(y))) {
                return false;
            }
        }
        return true;
    }

    private void placeLowLimbs(World world, Random random, BlockPos base, int variant,
                               IBlockState log, IBlockState leaves) {
        int offset = random.nextInt(4);
        for (int i = 0; i < 3; i++) {
            int[] direction = DIRECTIONS[(i + offset) % 4];
            int y = 5 + i * 2 + (variant == 3 ? 1 : 0);
            int reach = 2 + random.nextInt(2);
            for (int distance = 1; distance <= reach; distance++) {
                placeLog(world, base.add(direction[0] * distance, y + distance / 2,
                        direction[1] * distance), limbLog(log, direction));
            }
            placeCrown(world, random, base.add(direction[0] * reach, y + 2,
                    direction[1] * reach), 1, 1, leaves);
        }
    }

    private void placeWhorls(World world, Random random, BlockPos base, int height,
                             int tiers, int widestReach, IBlockState log, IBlockState leaves) {
        int bottom = height / 2 - 2;
        int spacing = Math.max(3, (height - bottom - 3) / tiers);
        for (int tier = 0; tier < tiers; tier++) {
            int y = bottom + tier * spacing;
            int reach = Math.max(2, widestReach - tier / (tiers == 7 ? 3 : 2));
            int rotation = random.nextInt(2);
            for (int i = rotation; i < DIRECTIONS.length; i += 2) {
                if (random.nextInt(5) == 0) {
                    continue;
                }
                int[] direction = DIRECTIONS[i];
                int branchReach = Math.max(2, reach - random.nextInt(2));
                for (int distance = 1; distance <= branchReach; distance++) {
                    placeLog(world, base.add(direction[0] * distance,
                            y + distance / 3, direction[1] * distance), limbLog(log, direction));
                }
                placeCrown(world, random, base.add(direction[0] * branchReach,
                        y + branchReach / 3, direction[1] * branchReach),
                        1 + (tier < tiers - 2 && random.nextInt(3) == 0 ? 1 : 0), 1, leaves);
            }
            placeCrown(world, random, base.up(y + 1), 1, 1, leaves);
        }
    }

    private void placeCrown(World world, Random random, BlockPos center, int radius, int depth,
                            IBlockState leaves) {
        for (int y = -depth; y <= depth; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = (double) (x * x + z * z) / (radius * radius + 0.5)
                            + (double) (y * y) / (depth * depth + 0.5);
                    if (distance <= 1.0 && (distance < 0.55 || random.nextInt(4) != 0)) {
                        BlockPos pos = center.add(x, y, z);
                        if (isReplaceable(world, pos)) {
                            setBlockAndNotifyAdequately(world, pos, leaves);
                        }
                    }
                }
            }
        }
    }

    private static IBlockState limbLog(IBlockState log, int[] direction) {
        return log.withProperty(BlockDirectional.FACING, direction[0] != 0 ? EnumFacing.UP : EnumFacing.EAST);
    }

    private void placeLog(World world, BlockPos pos, IBlockState log) {
        if (isReplaceable(world, pos)) {
            setBlockAndNotifyAdequately(world, pos, log);
        }
    }

    private boolean canGrowOn(World world, BlockPos pos) {
        IBlockState ground = world.getBlockState(pos);
        return ground.getBlock() == Blocks.GRASS || ground.getBlock() == Blocks.DIRT
                || ground.getBlock() == Blocks.SAND;
    }

    @Override
    public boolean isReplaceable(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos)
                || state.getBlock().isReplaceable(world, pos)
                || state.getBlock().isLeaves(state, world, pos);
    }
}
