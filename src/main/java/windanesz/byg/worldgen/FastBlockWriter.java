package windanesz.byg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/** Writes plain terrain blocks straight into the chunk; everything else goes through {@link World#setBlockState}. */
public final class FastBlockWriter {

    private static final int FALLBACK_FLAGS = 2 | 16;

    private final World world;
    private final WorldServer serverWorld;
    private Chunk chunk;
    private int chunkX = Integer.MIN_VALUE;
    private int chunkZ = Integer.MIN_VALUE;
    private PlayerChunkMapEntry watchers;

    public FastBlockWriter(World world) {
        this.world = world;
        this.serverWorld = world instanceof WorldServer ? (WorldServer) world : null;
    }

    public World world() {
        return world;
    }

    public void set(BlockPos pos, IBlockState state) {
        set(pos.getX(), pos.getY(), pos.getZ(), state);
    }

    public void set(int x, int y, int z, IBlockState state) {
        if (y < 0 || y > 255) {
            return;
        }
        Chunk target = chunkAt(x >> 4, z >> 4);
        IBlockState old = target.getBlockState(x & 15, y, z & 15);
        if (old == state) {
            return;
        }
        BlockPos pos = new BlockPos(x, y, z);
        if (!isPlain(old) || !isPlain(state)) {
            world.setBlockState(pos, state, FALLBACK_FLAGS);
            return;
        }
        if (target.setBlockState(pos, state) == null) {
            return;
        }
        if (old.getLightOpacity(world, pos) != state.getLightOpacity(world, pos)
                || old.getLightValue(world, pos) != state.getLightValue(world, pos)) {
            world.checkLight(pos);
        }
        if (watchers != null) {
            watchers.blockChanged(x & 15, y, z & 15);
        }
    }

    private Chunk chunkAt(int cx, int cz) {
        if (chunk == null || cx != chunkX || cz != chunkZ) {
            chunk = world.getChunk(cx, cz);
            chunkX = cx;
            chunkZ = cz;
            watchers = serverWorld != null && chunk.isPopulated() ? serverWorld.getPlayerChunkMap().getEntry(cx, cz) : null;
        }
        return chunk;
    }

    private static boolean isPlain(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.AIR
                || block == Blocks.HARDENED_CLAY
                || block == Blocks.STAINED_HARDENED_CLAY
                || block == Blocks.STONE
                || block == Blocks.DIRT
                || block == Blocks.GRASS
                || block == Blocks.SANDSTONE
                || block == Blocks.RED_SANDSTONE;
    }
}
