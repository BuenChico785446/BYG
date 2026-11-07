package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.Block;
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

    public final class WeepingWitchTreeGenerator extends WorldGenAbstractTree {
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public WeepingWitchTreeGenerator() {
        super(false);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos origin) {
        int height = 15 + random.nextInt(4);
        if (origin.getY() < 1 || origin.getY() + height + 3 >= world.getActualHeight()
                || !isSoil(world.getBlockState(origin.down()).getBlock())
                || !BygTreePlacement.hasRoom(world, origin.getX(), origin.getZ(),
                origin.getX() - 5, origin.getX() + 5, origin.getZ() - 5, origin.getZ() + 5)) {
            return false;
        }
        for (int y = 0; y <= height; y++) {
            if (!canReplace(world, origin.up(y))) {
                return false;
            }
        }

        IBlockState log = ModBlocks.witch_hazel_log.getDefaultState();
        IBlockState leaves = ModBlocks.witch_hazel_leaves.getDefaultState();
        IBlockState flowers = ModBlocks.witch_hazel_leaves_blooming.getDefaultState();
        for (int y = 0; y < height; y++) {
            place(world, origin.up(y), log);
            if (y > 1 && y < height - 6) {
                place(world, origin.add(1, y, 0), log);
                place(world, origin.add(0, y, 1), log);
                place(world, origin.add(1, y, 1), log);
            }
        }
        for (int[] direction : DIRECTIONS) {
            int rootLength = 2 + random.nextInt(2);
            for (int step = 1; step <= rootLength; step++) {
                BlockPos rootTop = origin.add(direction[0] * step, Math.max(0, 3 - step), direction[1] * step);
                placeSupportedRoot(world, rootTop, origin.getY(), log);
            }
        }
        for (int branch = 0; branch < 7; branch++) {
            double angle = Math.PI * 2.0 * branch / 7.0 + random.nextDouble() * 0.35;
            int length = 3 + random.nextInt(3);
            int baseY = height - 5 + random.nextInt(3);
            IBlockState limbLog = log.withProperty(BlockDirectional.FACING,
                    Math.abs(Math.cos(angle)) >= Math.abs(Math.sin(angle)) ? EnumFacing.UP : EnumFacing.EAST);
            BlockPos tip = origin.up(baseY);
            for (int step = 1; step <= length; step++) {
                BlockPos limb = origin.add((int) Math.round(Math.cos(angle) * step),
                        baseY + step / 2, (int) Math.round(Math.sin(angle) * step));
                if (!canReplace(world, limb)) {
                    break;
                }
                place(world, limb, limbLog);
                tip = limb;
            }
            addLeaves(world, random, tip, leaves, flowers);
        }
        addLeaves(world, random, origin.up(height), leaves, flowers);
        return true;
    }

    private void addLeaves(World world, Random random, BlockPos center, IBlockState leaves, IBlockState flowers) {
        for (int y = -1; y <= 1; y++) {
            int radius = y == 0 ? 2 : 1;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) + Math.abs(z) <= radius + 1 && random.nextInt(6) != 0) {
                        place(world, center.add(x, y, z), random.nextInt(5) == 0 ? flowers : leaves);
                    }
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.max(Math.abs(x), Math.abs(z)) == 2 && random.nextInt(3) == 0) {
                    int drop = 1 + random.nextInt(3);
                    for (int y = 2; y <= drop + 1; y++) {
                        place(world, center.add(x, -y, z), random.nextInt(6) == 0 ? flowers : leaves);
                    }
                }
            }
        }
    }

    private void place(World world, BlockPos pos, IBlockState state) {
        if (canReplace(world, pos)) {
            setBlockAndNotifyAdequately(world, pos, state);
        }
    }

    private void placeSupportedRoot(World world, BlockPos top, int trunkBaseY, IBlockState log) {
        int soilY = Integer.MIN_VALUE;
        for (int y = top.getY() - 1; y >= trunkBaseY - 4; y--) {
            BlockPos cursor = new BlockPos(top.getX(), y, top.getZ());
            if (isSoil(world.getBlockState(cursor).getBlock())) {
                soilY = y;
                break;
            }
            if (!canReplace(world, cursor)) {
                return;
            }
        }
        if (soilY == Integer.MIN_VALUE) {
            return;
        }
        for (int y = soilY + 1; y <= top.getY(); y++) {
            if (!canReplace(world, new BlockPos(top.getX(), y, top.getZ()))) {
                return;
            }
        }
        for (int y = soilY + 1; y <= top.getY(); y++) {
            place(world, new BlockPos(top.getX(), y, top.getZ()), log);
        }
    }

    private static boolean isSoil(Block block) {
        return block == Blocks.GRASS || block == Blocks.DIRT
                || block == ModBlocks.peat_dirt || block == ModBlocks.peat_grass;
    }

    private static boolean canReplace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        return material == Material.AIR || material == Material.LEAVES
                || material == Material.PLANTS || material == Material.VINE
                || state.getBlock().isReplaceable(world, pos);
    }
}
