package windanesz.byg.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenMinable;

/** Must consume the Random exactly like {@link WorldGenMinable} so existing seeds keep producing the same veins. */
public class FastMinable extends WorldGenMinable {

    private static final int WORLD_LIMIT = 30000000;
    private static final int PLACE_FLAGS = 2 | 16;

    private final IBlockState oreBlock;
    private final int numberOfBlocks;
    private final Predicate<IBlockState> predicate;
    private final boolean writesDirectly;
    private ResourceLocation[] allowedBiomes;
    private Biome[] resolvedBiomes;

    public FastMinable(IBlockState state, int blockCount, Predicate<IBlockState> predicate) {
        super(state, blockCount, predicate);
        this.oreBlock = state;
        this.numberOfBlocks = blockCount;
        this.predicate = predicate;
        this.writesDirectly = !state.getBlock().hasTileEntity(state);
    }

    public FastMinable onlyInBiomes(String... biomeIds) {
        if (biomeIds.length > 0) {
            this.allowedBiomes = new ResourceLocation[biomeIds.length];
            for (int i = 0; i < biomeIds.length; i++) {
                this.allowedBiomes[i] = new ResourceLocation(biomeIds[i]);
            }
            this.resolvedBiomes = null;
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
        int words = (sizeZ + 63) >> 6;
        long[] columns = new long[sizeX * sizeY * words];

        int span = this.numberOfBlocks / 8 + 6;
        double[] squaredX = new double[span];
        double[] squaredY = new double[span];
        double[] squaredZ = new double[span];

        for (int i = 0; i < this.numberOfBlocks; ++i) {
            float f1 = (float) i / (float) this.numberOfBlocks;
            double d6 = d0 + (d1 - d0) * (double) f1;
            double d7 = d4 + (d5 - d4) * (double) f1;
            double d8 = d2 + (d3 - d2) * (double) f1;
            double d9 = rand.nextDouble() * (double) this.numberOfBlocks / 16.0D;
            double d10 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double halfWidth = d10 / 2.0D;
            double halfHeight = d11 / 2.0D;
            int j = MathHelper.floor(d6 - halfWidth);
            int k = MathHelper.floor(d7 - halfHeight);
            int l = MathHelper.floor(d8 - halfWidth);
            int i1 = MathHelper.floor(d6 + halfWidth);
            int j1 = MathHelper.floor(d7 + halfHeight);
            int k1 = MathHelper.floor(d8 + halfWidth);

            if (i1 - j >= squaredX.length) {
                squaredX = new double[i1 - j + 1];
            }
            if (j1 - k >= squaredY.length) {
                squaredY = new double[j1 - k + 1];
            }
            if (k1 - l >= squaredZ.length) {
                squaredZ = new double[k1 - l + 1];
            }
            for (int x = j; x <= i1; ++x) {
                double d12 = ((double) x + 0.5D - d6) / halfWidth;
                squaredX[x - j] = d12 * d12;
            }
            for (int y = k; y <= j1; ++y) {
                double d13 = ((double) y + 0.5D - d7) / halfHeight;
                squaredY[y - k] = d13 * d13;
            }
            for (int z = l; z <= k1; ++z) {
                double d14 = ((double) z + 0.5D - d8) / halfWidth;
                squaredZ[z - l] = d14 * d14;
            }

            for (int x = j; x <= i1; ++x) {
                double sumX = squaredX[x - j];
                if (!(sumX < 1.0D)) {
                    continue;
                }
                for (int y = k; y <= j1; ++y) {
                    double sumXY = sumX + squaredY[y - k];
                    if (!(sumXY < 1.0D)) {
                        continue;
                    }
                    int from = l;
                    int to = k1;
                    while (from <= to && !(sumXY + squaredZ[from - l] < 1.0D)) {
                        from++;
                    }
                    while (to >= from && !(sumXY + squaredZ[to - l] < 1.0D)) {
                        to--;
                    }
                    if (from <= to) {
                        markRange(columns, ((x - originX) * sizeY + (y - originY)) * words, from - originZ, to - originZ);
                    }
                }
            }
        }

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int chunkX = Integer.MIN_VALUE;
        int chunkZ = Integer.MIN_VALUE;
        Chunk chunk = null;
        ExtendedBlockStorage[] storages = null;
        PlayerChunkMapEntry watchers = null;

        for (int xi = 0; xi < sizeX; xi++) {
            int x = originX + xi;
            if (x < -WORLD_LIMIT || x >= WORLD_LIMIT) {
                continue;
            }
            for (int yi = 0; yi < sizeY; yi++) {
                int y = originY + yi;
                if (y < 0 || y >= 256) {
                    continue;
                }
                int row = (xi * sizeY + yi) * words;
                for (int w = 0; w < words; w++) {
                    long bits = columns[row + w];
                    while (bits != 0L) {
                        int z = originZ + (w << 6) + Long.numberOfTrailingZeros(bits);
                        bits &= bits - 1L;
                        if (z < -WORLD_LIMIT || z >= WORLD_LIMIT) {
                            continue;
                        }
                        int cx = x >> 4;
                        int cz = z >> 4;
                        if (chunk == null || cx != chunkX || cz != chunkZ) {
                            chunk = world.getChunk(cx, cz);
                            chunkX = cx;
                            chunkZ = cz;
                            storages = chunk.getBlockStorageArray();
                            watchers = chunk.isPopulated() && world instanceof WorldServer
                                    ? ((WorldServer) world).getPlayerChunkMap().getEntry(cx, cz) : null;
                        }
                        ExtendedBlockStorage storage = storages[y >> 4];
                        IBlockState state = storage == Chunk.NULL_BLOCK_STORAGE
                                ? Blocks.AIR.getDefaultState() : storage.get(x & 15, y & 15, z & 15);
                        pos.setPos(x, y, z);
                        if (!state.getBlock().isReplaceableOreGen(state, world, pos, this.predicate)
                                || !isInAllowedBiome(world, pos)) {
                            continue;
                        }
                        if (this.writesDirectly && storage != Chunk.NULL_BLOCK_STORAGE && isPlainTerrain(state)
                                && state.getLightOpacity() == this.oreBlock.getLightOpacity()
                                && state.getLightValue() == this.oreBlock.getLightValue()) {
                            storage.set(x & 15, y & 15, z & 15, this.oreBlock);
                            chunk.markDirty();
                            if (watchers != null) {
                                watchers.blockChanged(x & 15, y, z & 15);
                            }
                        } else {
                            world.setBlockState(new BlockPos(x, y, z), this.oreBlock, PLACE_FLAGS);
                        }
                    }
                }
            }
        }

        return true;
    }

    private static boolean isPlainTerrain(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.GRAVEL
                || block == Blocks.SAND || block == Blocks.NETHERRACK || block == Blocks.SNOW;
    }

    private static void markRange(long[] columns, int rowStart, int from, int to) {
        int firstWord = from >> 6;
        int lastWord = to >> 6;
        for (int w = firstWord; w <= lastWord; w++) {
            int low = w == firstWord ? from & 63 : 0;
            int high = w == lastWord ? to & 63 : 63;
            columns[rowStart + w] |= (-1L >>> (63 - high)) & (-1L << low);
        }
    }

    private boolean isInAllowedBiome(World world, BlockPos pos) {
        if (this.allowedBiomes == null) {
            return true;
        }
        if (this.resolvedBiomes == null) {
            this.resolvedBiomes = resolveBiomes(this.allowedBiomes);
        }
        Biome biome = world.getBiome(pos);
        for (Biome allowed : this.resolvedBiomes) {
            if (allowed == biome) {
                return true;
            }
        }
        return false;
    }

    private static Biome[] resolveBiomes(ResourceLocation[] ids) {
        List<Biome> biomes = new ArrayList<>(ids.length);
        for (ResourceLocation id : ids) {
            Biome biome = Biome.REGISTRY.getObject(id);
            if (biome != null) {
                biomes.add(biome);
            }
        }
        return biomes.toArray(new Biome[0]);
    }
}
