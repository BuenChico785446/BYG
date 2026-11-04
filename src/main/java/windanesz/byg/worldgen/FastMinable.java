package windanesz.byg.worldgen;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenMinable;

/** Must consume the Random exactly like {@link WorldGenMinable} so existing seeds keep producing the same veins. */
public class FastMinable extends WorldGenMinable {

    private static final int WORLD_LIMIT = 30000000;
    private static final int PLACE_FLAGS = 2 | 16;

    private final IBlockState oreBlock;
    private final int numberOfBlocks;
    private final Predicate<IBlockState> predicate;
    private ResourceLocation[] allowedBiomes;

    public FastMinable(IBlockState state, int blockCount, Predicate<IBlockState> predicate) {
        super(state, blockCount, predicate);
        this.oreBlock = state;
        this.numberOfBlocks = blockCount;
        this.predicate = predicate;
    }

    public FastMinable onlyInBiomes(String... biomeIds) {
        if (biomeIds.length > 0) {
            this.allowedBiomes = new ResourceLocation[biomeIds.length];
            for (int i = 0; i < biomeIds.length; i++) {
                this.allowedBiomes[i] = new ResourceLocation(biomeIds[i]);
            }
        }
        return this;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        float f = rand.nextFloat() * (float) Math.PI;
        double d0 = (double) ((float) (position.getX() + 8) + MathHelper.sin(f) * (float) this.numberOfBlocks / 8.0F);
        double d1 = (double) ((float) (position.getX() + 8) - MathHelper.sin(f) * (float) this.numberOfBlocks / 8.0F);
        double d2 = (double) ((float) (position.getZ() + 8) + MathHelper.cos(f) * (float) this.numberOfBlocks / 8.0F);
        double d3 = (double) ((float) (position.getZ() + 8) - MathHelper.cos(f) * (float) this.numberOfBlocks / 8.0F);
        double d4 = (double) (position.getY() + rand.nextInt(3) - 2);
        double d5 = (double) (position.getY() + rand.nextInt(3) - 2);

        double reach = this.numberOfBlocks / 16.0D + 0.5D;
        int originX = MathHelper.floor(Math.min(d0, d1) - reach) - 1;
        int originY = MathHelper.floor(Math.min(d4, d5) - reach) - 1;
        int originZ = MathHelper.floor(Math.min(d2, d3) - reach) - 1;
        int sizeX = MathHelper.floor(Math.max(d0, d1) + reach) + 2 - originX;
        int sizeY = MathHelper.floor(Math.max(d4, d5) + reach) + 2 - originY;
        int sizeZ = MathHelper.floor(Math.max(d2, d3) + reach) + 2 - originZ;
        boolean[] visited = new boolean[sizeX * sizeY * sizeZ];

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int chunkX = Integer.MIN_VALUE;
        int chunkZ = Integer.MIN_VALUE;
        Chunk chunk = null;

        for (int i = 0; i < this.numberOfBlocks; ++i) {
            float f1 = (float) i / (float) this.numberOfBlocks;
            double d6 = d0 + (d1 - d0) * (double) f1;
            double d7 = d4 + (d5 - d4) * (double) f1;
            double d8 = d2 + (d3 - d2) * (double) f1;
            double d9 = rand.nextDouble() * (double) this.numberOfBlocks / 16.0D;
            double d10 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int x = j; x <= i1; ++x) {
                double d12 = ((double) x + 0.5D - d6) / (d10 / 2.0D);
                if (d12 * d12 < 1.0D) {
                    for (int y = k; y <= j1; ++y) {
                        double d13 = ((double) y + 0.5D - d7) / (d11 / 2.0D);
                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int z = l; z <= k1; ++z) {
                                double d14 = ((double) z + 0.5D - d8) / (d10 / 2.0D);
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    if (y < 0 || y >= 256 || x < -WORLD_LIMIT || x >= WORLD_LIMIT
                                            || z < -WORLD_LIMIT || z >= WORLD_LIMIT) {
                                        continue;
                                    }
                                    int index = ((x - originX) * sizeY + (y - originY)) * sizeZ + (z - originZ);
                                    if (visited[index]) {
                                        continue;
                                    }
                                    visited[index] = true;
                                    int cx = x >> 4;
                                    int cz = z >> 4;
                                    if (chunk == null || cx != chunkX || cz != chunkZ) {
                                        chunk = world.getChunk(cx, cz);
                                        chunkX = cx;
                                        chunkZ = cz;
                                    }
                                    IBlockState state = chunk.getBlockState(x, y, z);
                                    pos.setPos(x, y, z);
                                    if (state.getBlock().isReplaceableOreGen(state, world, pos, this.predicate)
                                            && isInAllowedBiome(world, pos)) {
                                        world.setBlockState(new BlockPos(x, y, z), this.oreBlock, PLACE_FLAGS);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean isInAllowedBiome(World world, BlockPos pos) {
        if (this.allowedBiomes == null) {
            return true;
        }
        ResourceLocation biomeId = Biome.REGISTRY.getNameForObject(world.getBiome(pos));
        for (ResourceLocation allowed : this.allowedBiomes) {
            if (allowed.equals(biomeId)) {
                return true;
            }
        }
        return false;
    }
}
