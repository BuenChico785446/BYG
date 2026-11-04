package windanesz.byg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import windanesz.byg.Config;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.treegenerator.BygTrees;

import java.util.Random;

public final class PalmWorldgen {
    private static final ResourceLocation TROPICAL_ISLANDS = new ResourceLocation("byg", "byg_tropical_islands");
    private static final ResourceLocation BEACHES = new ResourceLocation("minecraft", "beaches");
    // Combined chance per million per chunk of the two removed templates (100000 + 80000).
    private static final int CHANCE_PER_MILLION = 180000;
    // The thick palm's crown, plus its lean, reaches this far from the trunk.
    private static final int MAX_REACH = 11;

    private PalmWorldgen() {
    }

    /** {@code chunkX}/{@code chunkZ} carry Forge's +8 decoration offset. */
    static void generate(Random random, int chunkX, int chunkZ, World world, int dimension) {
        if (!Config.isFeatureDimension(dimension) || world.isRemote || !Config.isPalmContentEnabled()) {
            return;
        }
        int chance = Config.scaleTemplateChance(CHANCE_PER_MILLION);
        if (chance <= 0 || random.nextInt(1000000) >= chance) {
            return;
        }

        // Keep the whole crown inside the loaded 2x2-chunk window, and inside this chunk's own area.
        int minX = Math.max(chunkX, chunkX - 8 + MAX_REACH);
        int maxX = Math.min(chunkX + 15, chunkX + 23 - MAX_REACH);
        int minZ = Math.max(chunkZ, chunkZ - 8 + MAX_REACH);
        int maxZ = Math.min(chunkZ + 15, chunkZ + 23 - MAX_REACH);
        int x = minX + random.nextInt(maxX - minX + 1);
        int z = minZ + random.nextInt(maxZ - minZ + 1);

        ResourceLocation biomeId = Biome.REGISTRY.getNameForObject(world.getBiome(new BlockPos(x, 128, z)));
        if (!TROPICAL_ISLANDS.equals(biomeId) && !BEACHES.equals(biomeId)) {
            return;
        }

        BlockPos ground = findSurface(world, x, z);
        if (ground == null || !isPalmSand(world.getBlockState(ground)) || !BygTreePlacement.hasRoom(world, x, z, x, x, z, z)
                || !BygTreePlacement.allowTrees(world, random, ground)) {
            return;
        }
        BygTrees.PALM.generate(world, random, ground.up());
    }

    private static BlockPos findSurface(World world, int x, int z) {
        int y = world.getActualHeight() - 1;
        BlockPos pos = new BlockPos(x, y, z);
        while (y > 0 && (world.isAirBlock(pos)
                || (world.getBlockState(pos).getBlock().isReplaceable(world, pos)
                && !world.getBlockState(pos).getMaterial().isLiquid()))) {
            y--;
            pos = new BlockPos(x, y, z);
        }
        return y < 1 ? null : pos;
    }

    private static boolean isPalmSand(IBlockState state) {
        Block block = state.getBlock();
        return block == ModBlocks.light_blue_sand || block == ModBlocks.pink_sand || block == ModBlocks.purple_sand
                || block == ModBlocks.white_sand || block == ModBlocks.black_sand
                || state == Blocks.SAND.getStateFromMeta(0);
    }
}
