package windanesz.byg.worldgen;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenStonePillarWaterfall extends WorldGenerator {

    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private static final IBlockState WATER = Blocks.WATER.getDefaultState();
    private static final EnumFacing[] HORIZONTAL_DIRECTIONS = {
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.EAST
    };

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        BlockPos topSurface = findSurface(world, position);
        if (topSurface == null || topSurface.getY() < 70) {
            return false;
        }

        WaterfallPath path = findBestPath(world, topSurface);
        if (path == null || path.dropHeight < 6) {
            return false;
        }

        FastBlockWriter out = new FastBlockWriter(world);
        carveOutlet(out, topSurface, path);
        carveFallColumn(out, topSurface, path);
        buildPond(world, out, path);
        placeWaterFeed(world, topSurface, path);
        return true;
    }

    private static WaterfallPath findBestPath(World world, BlockPos topSurface) {
        WaterfallPath bestPath = null;
        for (EnumFacing direction : HORIZONTAL_DIRECTIONS) {
            int edgeDistance = findEdgeDistance(world, topSurface, direction);
            if (edgeDistance < 2) {
                continue;
            }

            BlockPos fallPos = topSurface.offset(direction, edgeDistance + 1);
            BlockPos groundSurface = findSurface(world, fallPos);
            if (groundSurface == null) {
                continue;
            }

            int dropHeight = topSurface.getY() - groundSurface.getY();
            if (bestPath == null || dropHeight > bestPath.dropHeight) {
                bestPath = new WaterfallPath(direction, edgeDistance, fallPos, groundSurface, dropHeight);
            }
        }
        return bestPath;
    }

    private static int findEdgeDistance(World world, BlockPos topSurface, EnumFacing direction) {
        for (int distance = 1; distance <= 8; distance++) {
            BlockPos samplePos = topSurface.offset(direction, distance);
            if (!isSolid(world, samplePos)) {
                return distance - 1;
            }
        }
        return -1;
    }

    private static void carveOutlet(FastBlockWriter out, BlockPos topSurface, WaterfallPath path) {
        for (int distance = 1; distance <= path.edgeDistance + 1; distance++) {
            BlockPos outletPos = topSurface.offset(path.direction, distance);
            clearBlock(out, outletPos);
            clearBlock(out, outletPos.up());
        }
    }

    private static void carveFallColumn(FastBlockWriter out, BlockPos topSurface, WaterfallPath path) {
        for (int y = topSurface.getY(); y > path.groundSurface.getY(); y--) {
            clearBlock(out, new BlockPos(path.fallPos.getX(), y, path.fallPos.getZ()));
        }
    }

    private static void buildPond(World world, FastBlockWriter out, WaterfallPath path) {
        int radius = 1;
        int pondY = path.groundSurface.getY();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > 2) {
                    continue;
                }

                BlockPos pondPos = new BlockPos(path.fallPos.getX() + dx, pondY, path.fallPos.getZ() + dz);
                clearBlock(out, pondPos.up());
                placeWater(world, pondPos);
            }
        }
    }

    private static void placeWaterFeed(World world, BlockPos topSurface, WaterfallPath path) {
        int channelStart = Math.max(0, path.edgeDistance - 2);
        for (int distance = channelStart; distance <= path.edgeDistance; distance++) {
            placeWater(world, topSurface.offset(path.direction, distance));
        }

        BlockPos fallStart = new BlockPos(path.fallPos.getX(), topSurface.getY(), path.fallPos.getZ());
        placeWater(world, fallStart);
    }

    private static void clearBlock(FastBlockWriter out, BlockPos pos) {
        if (out.get(pos.getX(), pos.getY(), pos.getZ()).getBlock() != Blocks.BEDROCK) {
            out.set(pos, AIR);
        }
    }

    private static void placeWater(World world, BlockPos pos) {
        world.setBlockState(pos, WATER, 3);
        world.scheduleUpdate(pos, Blocks.WATER, Blocks.WATER.tickRate(world));
        world.notifyNeighborsOfStateChange(pos, Blocks.WATER, false);
    }

    private static boolean isSolid(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        return !material.isReplaceable() && !material.isLiquid() && !state.getBlock().isLeaves(state, world, pos);
    }

    private static BlockPos findSurface(World world, BlockPos origin) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(origin.getX(), world.getHeight(origin).getY(), origin.getZ());
        while (cursor.getY() > 1) {
            IBlockState state = world.getBlockState(cursor);
            Material material = state.getMaterial();
            if (!material.isReplaceable() && !material.isLiquid() && !state.getBlock().isLeaves(state, world, cursor)) {
                return cursor.toImmutable();
            }
            cursor.move(EnumFacing.DOWN);
        }
        return null;
    }

    private static final class WaterfallPath {
        private final EnumFacing direction;
        private final int edgeDistance;
        private final BlockPos fallPos;
        private final BlockPos groundSurface;
        private final int dropHeight;

        private WaterfallPath(EnumFacing direction, int edgeDistance, BlockPos fallPos, BlockPos groundSurface, int dropHeight) {
            this.direction = direction;
            this.edgeDistance = edgeDistance;
            this.fallPos = fallPos;
            this.groundSurface = groundSurface;
            this.dropHeight = dropHeight;
        }
    }
}

