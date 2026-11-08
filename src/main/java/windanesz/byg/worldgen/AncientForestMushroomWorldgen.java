package windanesz.byg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import windanesz.byg.Config;
import windanesz.byg.registry.ModBlocks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

final class AncientForestMushroomWorldgen {
    private AncientForestMushroomWorldgen() {
    }

    static void generate(Random random, int chunkX, int chunkZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || !BygWorldGenerator.matchesBiome(world, chunkX, chunkZ, "byg:byg_ancient_forest")) return;
        int attempts = Config.scaleClusterPlantAttempts(1);
        for (int i = 0; i < attempts; i++) {
            if (random.nextInt(2) != 0) continue;
            for (int site = 0; site < 8; site++) {
                int x = chunkX + 4 + random.nextInt(8);
                int z = chunkZ + 4 + random.nextInt(8);
                if (!BygWorldGenerator.matchesBiome(world, x, z, "byg:byg_ancient_forest")) continue;
                BlockPos ground = AncientForestFloorWorldgen.findGround(world, x, z);
                if (ground != null && hasGroundSupport(world, ground) && placeMushroom(random, world, ground)) break;
            }
        }
    }

    private static boolean hasGroundSupport(World world, BlockPos ground) {
        int supported = 0;
        for (int[] offset : new int[][]{{2, 0}, {-2, 0}, {0, 2}, {0, -2}}) {
            BlockPos around = AncientForestFloorWorldgen.findGround(world,
                    ground.getX() + offset[0], ground.getZ() + offset[1]);
            if (around != null && Math.abs(around.getY() - ground.getY()) <= 1) supported++;
        }
        return supported >= 2;
    }

    private static boolean placeMushroom(Random random, World world, BlockPos ground) {
        int kind = random.nextInt(Config.isWoodSetEnabled("glowshroom") ? 4 : 2);
        int height = 7 + random.nextInt(6);
        int radius = 2 + random.nextInt(3);
        IBlockState cap;
        IBlockState stem;
        if (kind == 0 || kind == 1) {
            Block block = kind == 0 ? Blocks.RED_MUSHROOM_BLOCK : Blocks.BROWN_MUSHROOM_BLOCK;
            cap = block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_OUTSIDE);
            stem = block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM);
        } else {
            cap = (kind == 2 ? ModBlocks.glowshroom_block_blue : ModBlocks.glowshroom_block_purple).getDefaultState();
            stem = ModBlocks.glowshroom_stem_yellow.getDefaultState();
        }

        Map<BlockPos, IBlockState> shape = new LinkedHashMap<>();
        int capY = ground.getY() + height;
        int layers = kind == 1 ? 2 : 4;
        for (int layer = 0; layer < layers; layer++) {
            int y = capY - 1 + layer;
            int layerRadius = kind == 1 ? radius - (layer == 0 ? 1 : 0)
                    : radius - (layer == 0 ? 1 : layer == layers - 1 ? 2 : 0);
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (dx * dx + dz * dz > layerRadius * layerRadius + random.nextInt(3)) continue;
                    shape.put(new BlockPos(ground.getX() + dx, y, ground.getZ() + dz), cap);
                }
            }
        }
        for (int y = ground.getY() + 1; y < capY; y++) {
            shape.put(new BlockPos(ground.getX(), y, ground.getZ()), stem);
        }

        for (BlockPos pos : shape.keySet()) {
            if (!BygWorldGenerator.matchesBiome(world, pos.getX(), pos.getZ(), "byg:byg_ancient_forest")) return false;
            IBlockState existing = world.getBlockState(pos);
            if (!world.isAirBlock(pos) && !existing.getBlock().isReplaceable(world, pos)
                    && !existing.getBlock().isLeaves(existing, world, pos)) return false;
        }
        for (Map.Entry<BlockPos, IBlockState> block : shape.entrySet()) {
            world.setBlockState(block.getKey(), block.getValue(), 2);
        }
        return true;
    }
}
