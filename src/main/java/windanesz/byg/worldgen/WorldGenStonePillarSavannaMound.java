package windanesz.byg.worldgen;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenStonePillarSavannaMound extends WorldGenerator {

    private static final IBlockState HARDENED_CLAY = Blocks.HARDENED_CLAY.getDefaultState();

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        BlockPos surface = findSurface(world, position);
        if (surface == null || surface.getY() < 62) {
            return false;
        }

        int radiusX = 3 + rand.nextInt(2);
        int radiusZ = 3 + rand.nextInt(2);
        int height = 1 + rand.nextInt(2);
        BlockPos min = new BlockPos(surface.getX() - radiusX - 1, surface.getY() - 2, surface.getZ() - radiusZ - 1);
        BlockPos max = new BlockPos(surface.getX() + radiusX + 1, surface.getY() + height + 4, surface.getZ() + radiusZ + 1);
        if (!world.isAreaLoaded(min, max)) {
            return false;
        }

        FastBlockWriter out = new FastBlockWriter(world);
        boolean raised = false;
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                float nx = dx / (float) radiusX;
                float nz = dz / (float) radiusZ;
                float distSq = nx * nx + nz * nz;
                if (distSq > 1.0f) {
                    continue;
                }

                BlockPos localSurface = findSurface(world, surface.add(dx, 0, dz));
                if (localSurface == null || Math.abs(localSurface.getY() - surface.getY()) > 4) {
                    continue;
                }

                int moundHeight = Math.max(1, Math.round((1.0f - distSq) * height));
                for (int y = 1; y <= moundHeight; y++) {
                    out.set(localSurface.getX(), localSurface.getY() + y, localSurface.getZ(), HARDENED_CLAY);
                }
                raised = true;
            }
        }
        return raised;
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

