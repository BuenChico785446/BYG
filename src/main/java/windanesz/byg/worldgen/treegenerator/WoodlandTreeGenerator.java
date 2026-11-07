package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import windanesz.byg.blocks.BlockGeneratedSaplingBase;
import windanesz.byg.worldgen.BygTreePlacement;

import java.util.Random;

public abstract class WoodlandTreeGenerator extends WorldGenAbstractTree implements TreeGrowthGenerator {
    /** Deepest a root arm may reach below the trunk base before it is dropped instead of extended. */
    private static final int MAX_ROOT_DEPTH = 3;
    private static final EnumFacing[] SIDES = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
    private static final ThreadLocal<Boolean> GROWING = new ThreadLocal<>();

    protected WoodlandTreeGenerator() {
        super(false);
    }

    @Override
    public boolean growTree(World world, Random random, BlockPos position) {
        GROWING.set(Boolean.TRUE);
        try {
            return this.generate(world, random, position);
        } finally {
            GROWING.remove();
        }
    }

    /** Log state for a trunk running along the given axis (vertical = SOUTH, X = UP, Z = EAST). */
    protected static IBlockState log(IBlockState log, EnumFacing.Axis axis) {
        EnumFacing facing = axis == EnumFacing.Axis.Y ? EnumFacing.SOUTH
                : axis == EnumFacing.Axis.X ? EnumFacing.UP : EnumFacing.EAST;
        return log.withProperty(BlockDirectional.FACING, facing);
    }

    protected static EnumFacing randomSide(Random random) {
        return SIDES[random.nextInt(SIDES.length)];
    }

    protected static boolean isSoil(World world, BlockPos pos) {
        Material material = world.getBlockState(pos).getMaterial();
        return material == Material.GRASS || material == Material.GROUND;
    }

    protected static boolean canReplace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getMaterial() == Material.AIR
                || state.getBlock().isLeaves(state, world, pos)
                || state.getBlock().isReplaceable(world, pos)
                || state.getBlock() instanceof BlockGeneratedSaplingBase;
    }

    /** Whether the trunk may stand on the block at {@code pos}; defaults to ordinary soil. */
    protected boolean canGrowOn(World world, BlockPos pos) {
        return isSoil(world, pos);
    }

    /** True when the trunk can stand at {@code origin} and the whole column up to {@code top} is free. */
    protected boolean canStandAt(World world, BlockPos origin, int top, int radius, int crownTop) {
        if (origin.getY() < 1 || origin.getY() + crownTop >= world.getHeight()
                || !world.isAreaLoaded(origin.add(-radius, -MAX_ROOT_DEPTH, -radius), origin.add(radius, crownTop, radius))
                || !this.canGrowOn(world, origin.down())
                || (GROWING.get() == null && !BygTreePlacement.hasRoom(world, origin.getX(), origin.getZ(),
                origin.getX() - radius, origin.getX() + radius, origin.getZ() - radius, origin.getZ() + radius))) {
            return false;
        }
        for (int y = 0; y <= top; y++) {
            if (!canReplace(world, origin.up(y))) {
                return false;
            }
        }
        return true;
    }

    protected boolean placeAnchoredLog(World world, BlockPos pos, IBlockState log, int height) {
        int depth = 0;
        BlockPos support = pos.down();
        while (canReplace(world, support) && !world.getBlockState(support).getMaterial().isLiquid()) {
            if (++depth > MAX_ROOT_DEPTH) {
                return false;
            }
            support = support.down();
        }
        if (!world.getBlockState(support).getMaterial().isSolid()) {
            return false;
        }
        for (int y = 0; y < height; y++) {
            if (!canReplace(world, pos.up(y))) {
                return y > 0;
            }
            this.setBlockAndNotifyAdequately(world, pos.up(y), log);
        }
        for (int y = 1; y <= depth; y++) {
            this.setBlockAndNotifyAdequately(world, pos.down(y), log);
        }
        return true;
    }

    protected void placeLog(World world, BlockPos pos, IBlockState log) {
        if (canReplace(world, pos)) {
            this.setBlockAndNotifyAdequately(world, pos, log);
        }
    }

    protected void placeLeaf(World world, BlockPos pos, IBlockState leaves) {
        if (canReplace(world, pos) && !world.getBlockState(pos).getMaterial().isLiquid()) {
            this.setBlockAndNotifyAdequately(world, pos, leaves);
        }
    }

    protected static void fillLayer(boolean[][][] cells, int centre, int layer, double radius, double density, Random random) {
        int reach = (int) Math.ceil(radius);
        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > radius + 0.3) {
                    continue;
                }
                double chance = density * (1.2 - 0.55 * distance / Math.max(1.0, radius));
                if (random.nextDouble() < chance) {
                    cells[centre + dx][layer][centre + dz] = true;
                }
            }
        }
    }

    protected static void pruneIsolated(boolean[][][] cells, boolean[][][] anchors) {
        int sx = cells.length;
        int sy = cells[0].length;
        int sz = cells[0][0].length;
        boolean[][][] keep = new boolean[sx][sy][sz];
        for (int x = 0; x < sx; x++) {
            for (int y = 0; y < sy; y++) {
                for (int z = 0; z < sz; z++) {
                    if (cells[x][y][z] && (touches(cells, x, y, z) || touches(anchors, x, y, z) || anchors[x][y][z])) {
                        keep[x][y][z] = true;
                    }
                }
            }
        }
        for (int x = 0; x < sx; x++) {
            for (int y = 0; y < sy; y++) {
                System.arraycopy(keep[x][y], 0, cells[x][y], 0, sz);
            }
        }
    }

    private static boolean touches(boolean[][][] cells, int x, int y, int z) {
        return get(cells, x + 1, y, z) || get(cells, x - 1, y, z) || get(cells, x, y + 1, z)
                || get(cells, x, y - 1, z) || get(cells, x, y, z + 1) || get(cells, x, y, z - 1);
    }

    private static boolean get(boolean[][][] cells, int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < cells.length && y < cells[0].length && z < cells[0][0].length && cells[x][y][z];
    }
}
