package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.BygTreePlacement;

import java.util.Random;

public final class AspenTreeGenerator extends WorldGenAbstractTree {
    private static final EnumFacing[] SIDES = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
    private static final int[] CROWN_RADII = {1, 2, 2, 2, 1, 1};

    public AspenTreeGenerator() {
        super(false);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        boolean large = random.nextInt(4) == 0;
        int height = large ? 19 + random.nextInt(7) : 14 + random.nextInt(6);
        if (position.getY() < 1 || position.getY() + height + 2 >= world.getHeight()
                || !world.isAreaLoaded(position.add(-4, -1, -4), position.add(4, height + 2, 4))) {
            return false;
        }
        if (!isSoil(world, position.down())
                || !BygTreePlacement.hasRoom(world, position.getX(), position.getZ(),
                position.getX() - 3, position.getX() + 3, position.getZ() - 3, position.getZ() + 3)) {
            return false;
        }
        for (int y = 0; y <= height + 1; y++) {
            if (!canReplace(world, position.up(y))) {
                return false;
            }
        }

        IBlockState log = ModBlocks.aspen_log.getDefaultState();
        IBlockState leaves = ModBlocks.aspen_leaves.getDefaultState();
        // Leaves first so logs always win where the two overlap.
        placeCrown(world, random, position.up(height), leaves);
        placeTufts(world, random, position, height, leaves);
        for (int y = 0; y <= height; y++) {
            this.setBlockAndNotifyAdequately(world, position.up(y), log.withProperty(BlockDirectional.FACING, EnumFacing.SOUTH));
        }
        placeBranches(world, random, position, height, log, leaves);
        if (large) {
            placeRoots(world, random, position, log);
        }
        return true;
    }

    private void placeCrown(World world, Random random, BlockPos top, IBlockState leaves) {
        for (int layer = 0; layer < CROWN_RADII.length; layer++) {
            int radius = CROWN_RADII[layer];
            int y = -CROWN_RADII.length + 1 + layer;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int distance = dx * dx + dz * dz;
                    if (distance > radius * radius + (radius > 1 ? 1 : 0)) {
                        continue;
                    }
                    // Keep the core solid, thin the rim so the crown stays airy.
                    if (Math.abs(dx) + Math.abs(dz) > 1 && random.nextInt(4) == 0) {
                        continue;
                    }
                    setLeaf(world, top.add(dx, y, dz), leaves);
                }
            }
        }
        setLeaf(world, top.up(), leaves);
    }

    private void placeTufts(World world, Random random, BlockPos base, int height, IBlockState leaves) {
        for (int y = 4; y < height - CROWN_RADII.length; y += 1 + random.nextInt(3)) {
            EnumFacing side = SIDES[random.nextInt(SIDES.length)];
            setLeaf(world, base.up(y).offset(side), leaves);
        }
    }

    private void placeBranches(World world, Random random, BlockPos base, int height, IBlockState log, IBlockState leaves) {
        int count = 1 + height / 6;
        for (int i = 0; i < count; i++) {
            int y = 4 + random.nextInt(Math.max(1, height - CROWN_RADII.length - 4));
            EnumFacing side = SIDES[random.nextInt(SIDES.length)];
            BlockPos branch = base.up(y).offset(side);
            if (!canReplace(world, branch)) {
                continue;
            }
            // Log axis: X = facing UP, Z = facing EAST (see BlockDirectionalLogBase).
            EnumFacing axis = side.getAxis() == EnumFacing.Axis.X ? EnumFacing.UP : EnumFacing.EAST;
            this.setBlockAndNotifyAdequately(world, branch, log.withProperty(BlockDirectional.FACING, axis));
            setLeaf(world, branch.offset(side), leaves);
            setLeaf(world, branch.up(), leaves);
            setLeaf(world, branch.offset(side.rotateY()), leaves);
            setLeaf(world, branch.offset(side.rotateYCCW()), leaves);
        }
    }

    private void placeRoots(World world, Random random, BlockPos base, IBlockState log) {
        for (EnumFacing side : SIDES) {
            int armHeight = 3 + random.nextInt(2);
            BlockPos arm = base.offset(side);
            if (!isSoil(world, arm.down())) {
                continue;
            }
            for (int y = 0; y < armHeight; y++) {
                if (canReplace(world, arm.up(y))) {
                    this.setBlockAndNotifyAdequately(world, arm.up(y), log.withProperty(BlockDirectional.FACING, EnumFacing.SOUTH));
                }
            }
        }
    }

    private void setLeaf(World world, BlockPos pos, IBlockState leaves) {
        if (canReplace(world, pos)) {
            this.setBlockAndNotifyAdequately(world, pos, leaves);
        }
    }

    private static boolean isSoil(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT;
    }

    private static boolean canReplace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getMaterial() == Material.AIR
                || state.getBlock().isLeaves(state, world, pos)
                || state.getBlock().isReplaceable(world, pos)
                || state.getBlock() == ModBlocks.aspen_sapling;
    }
}
