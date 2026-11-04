package windanesz.byg.worldgen;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

/**
 * Gate for every BYG tree placed outside the vanilla biome decorator. Fires Forge's TREE decoration event so
 * listeners that replace or cancel trees (e.g. Dynamic Trees) also control BYG's own tree placement.
 */
public final class BygTreePlacement {
    private static final int TRUNK_RADIUS = 1;
    private static final int SAMPLE_STEP = 2;
    private static final int MAX_BLOCKED_SHARE = 4;

    private BygTreePlacement() {
    }

    /**
     * @param pos the tree position, or the chunk decoration origin when checking once for a whole chunk
     * @return false if a listener denied tree generation here
     */
    public static boolean allowTrees(World world, Random random, BlockPos pos) {
        return TerrainGen.decorate(world, random, new ChunkPos(pos), pos, DecorateBiomeEvent.Decorate.EventType.TREE);
    }

    public static boolean hasRoom(World world, int trunkX, int trunkZ, int minX, int maxX, int minZ, int maxZ) {
        for (int x = trunkX - TRUNK_RADIUS; x <= trunkX + TRUNK_RADIUS; x++) {
            for (int z = trunkZ - TRUNK_RADIUS; z <= trunkZ + TRUNK_RADIUS; z++) {
                if (isTreeColumn(world, x, z)) {
                    return false;
                }
            }
        }
        int sampled = 0;
        int blocked = 0;
        for (int x = minX; x <= maxX; x += SAMPLE_STEP) {
            for (int z = minZ; z <= maxZ; z += SAMPLE_STEP) {
                sampled++;
                if (isTreeColumn(world, x, z)) {
                    blocked++;
                }
            }
        }
        return blocked * MAX_BLOCKED_SHARE <= sampled;
    }

    private static boolean isTreeColumn(World world, int x, int z) {
        BlockPos top = world.getHeight(new BlockPos(x, 0, z)).down();
        Material material = world.getBlockState(top).getMaterial();
        return material == Material.LEAVES || material == Material.WOOD;
    }
}
