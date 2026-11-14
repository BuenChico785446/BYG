package windanesz.byg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenStonePillarSavannaChasm extends WorldGenerator {

    private static final IBlockState AIR = Blocks.AIR.getDefaultState();

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        BlockPos surface = findSurface(world, position);
        if (surface == null || surface.getY() < 62) {
            return false;
        }

        int radiusX = 3 + rand.nextInt(3);
        int radiusZ = 4 + rand.nextInt(2);
        int depth = 2 + rand.nextInt(3);
        BlockPos min = new BlockPos(surface.getX() - radiusX - 2, surface.getY() - depth - 4, surface.getZ() - radiusZ - 2);
        BlockPos max = new BlockPos(surface.getX() + radiusX + 2, surface.getY() + 6, surface.getZ() + radiusZ + 2);
        if (!world.isAreaLoaded(min, max)) {
            return false;
        }

        FastBlockWriter out = new FastBlockWriter(world);
        boolean carved = false;
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                float nx = dx / (float) radiusX;
                float nz = dz / (float) radiusZ;
                float distSq = nx * nx + nz * nz;
                if (distSq > 1.0f) {
                    continue;
                }

                BlockPos localSurface = findSurface(world, surface.add(dx, 0, dz));
                if (localSurface == null || Math.abs(localSurface.getY() - surface.getY()) > 5) {
                    continue;
                }

                int carveDepth = Math.max(1, Math.round((1.0f - distSq) * depth) + (rand.nextBoolean() ? 1 : 0));
                for (int y = 0; y < carveDepth; y++) {
                    BlockPos carvePos = localSurface.up(1 - y);
                    clearColumnBlock(out, carvePos);
                    if (y == 0 && rand.nextInt(4) == 0) {
                        clearColumnBlock(out, carvePos.up());
                    }
                }
                carved = true;
            }
        }
        return carved;
    }

    private static void clearColumnBlock(FastBlockWriter out, BlockPos pos) {
        IBlockState state = out.world().getBlockState(pos);
        Material material = state.getMaterial();
        Block block = state.getBlock();
        if (material.isLiquid() || block == Blocks.BEDROCK) {
            return;
        }
        out.set(pos, AIR);
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
}

