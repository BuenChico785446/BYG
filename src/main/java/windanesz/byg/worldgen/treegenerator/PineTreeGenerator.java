package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.BygTreePlacement;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public final class PineTreeGenerator extends WorldGenAbstractTree implements TreeGrowthGenerator {
    public enum Size { SMALL, LARGE, SAPLING }

    private static final Variant[] VARIANTS = {
            new Variant(7, 13, 7, 81, PineTreeShapes.SMALL_1),
            new Variant(7, 10, 7, 110, PineTreeShapes.SMALL_2),
            new Variant(5, 15, 5, 63, PineTreeShapes.SMALL_3),
            new Variant(9, 16, 9, 117, PineTreeShapes.SMALL_4),
            new Variant(12, 27, 12, 612, PineTreeShapes.LARGE_1),
            new Variant(10, 18, 10, 348, PineTreeShapes.LARGE_2),
            new Variant(14, 18, 14, 412, PineTreeShapes.LARGE_3)
    };

    private final Size size;

    public PineTreeGenerator(Size size) {
        super(false);
        this.size = size;
    }

    @Override
    public boolean growTree(World world, Random random, BlockPos position) {
        return this.generate(world, random, position);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        BlockPos trunkOrigin = position;
        boolean large = this.size == Size.LARGE;
        if (this.size == Size.SAPLING) {
            BlockPos square = findTwoByTwoSaplings(world, position);
            large = square != null;
            if (large) {
                trunkOrigin = square;
            }
        }
        int first = large ? 4 : 0;
        Variant variant = VARIANTS[first + random.nextInt(large ? 3 : 4)];
        int trunkOffset = large ? variant.width / 2 - 1 : variant.width / 2;
        BlockPos origin = trunkOrigin.add(-trunkOffset, 0, -trunkOffset);
        if (position.getY() < 1 || position.getY() + variant.height >= world.getHeight()
                || !world.isAreaLoaded(origin.down(), origin.add(variant.width - 1, variant.height, variant.depth - 1))
                || (this.size != Size.SAPLING && !BygTreePlacement.hasRoom(world, trunkOrigin.getX(), trunkOrigin.getZ(),
                origin.getX(), origin.getX() + variant.width - 1, origin.getZ(), origin.getZ() + variant.depth - 1))) {
            return false;
        }
        int trunkWidth = large ? 2 : 1;
        for (int x = 0; x < trunkWidth; x++) {
            for (int z = 0; z < trunkWidth; z++) {
                BlockPos base = trunkOrigin.add(x, 0, z);
                IBlockState ground = world.getBlockState(base.down());
                if (ground.getBlock() != Blocks.GRASS && ground.getBlock() != Blocks.DIRT) {
                    return false;
                }
            }
        }
        // Check every log before writing anything. Crowns may overlap other foliage.
        int rotation = random.nextInt(4);
        for (int i = 0; i < variant.blocks.length; i += 3) {
            if ((variant.blocks[i + 2] & 128) != 0) {
                BlockPos target = rotate(origin, variant, variant.blocks[i] & 255,
                        variant.blocks[i + 1] & 255, variant.blocks[i + 2] & 127, rotation);
                if (!canReplace(world, target)) {
                    return false;
                }
            }
        }
        IBlockState log = ModBlocks.pine_log.getDefaultState();
        IBlockState leaves = ModBlocks.pine_leaves.getDefaultState();
        for (int i = 0; i < variant.blocks.length; i += 3) {
            if ((variant.blocks[i + 2] & 128) != 0) {
                BlockPos target = rotate(origin, variant, variant.blocks[i] & 255,
                        variant.blocks[i + 1] & 255, variant.blocks[i + 2] & 127, rotation);
                this.setBlockAndNotifyAdequately(world, target, log);
            }
        }
        for (int i = 0; i < variant.blocks.length; i += 3) {
            if ((variant.blocks[i + 2] & 128) == 0) {
                BlockPos target = rotate(origin, variant, variant.blocks[i] & 255,
                        variant.blocks[i + 1] & 255, variant.blocks[i + 2] & 127, rotation);
                if (canReplace(world, target)) {
                    this.setBlockAndNotifyAdequately(world, target, leaves);
                }
            }
        }
        return true;
    }

    private static BlockPos findTwoByTwoSaplings(World world, BlockPos pos) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dz = -1; dz <= 0; dz++) {
                BlockPos corner = pos.add(dx, 0, dz);
                if (world.isAreaLoaded(corner, corner.east().south())
                        && world.getBlockState(corner).getBlock() == ModBlocks.pine_sapling
                        && world.getBlockState(corner.east()).getBlock() == ModBlocks.pine_sapling
                        && world.getBlockState(corner.south()).getBlock() == ModBlocks.pine_sapling
                        && world.getBlockState(corner.east().south()).getBlock() == ModBlocks.pine_sapling) {
                    return corner;
                }
            }
        }
        return null;
    }

    private static BlockPos rotate(BlockPos origin, Variant variant, int x, int y, int z, int turns) {
        switch (turns) {
            case 1: return origin.add(variant.depth - 1 - z, y, x);
            case 2: return origin.add(variant.width - 1 - x, y, variant.depth - 1 - z);
            case 3: return origin.add(z, y, variant.width - 1 - x);
            default: return origin.add(x, y, z);
        }
    }

    private static boolean canReplace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == ModBlocks.pine_sapling
                || state.getBlock().isAir(state, world, pos)
                || state.getBlock().isLeaves(state, world, pos)
                || state.getBlock().isReplaceable(world, pos);
    }

    private static final class Variant {
        private final int width;
        private final int height;
        private final int depth;
        private final byte[] blocks;

        private Variant(int width, int height, int depth, int count, String[] layers) {
            this.width = width;
            this.height = height;
            this.depth = depth;
            if (layers.length != height) {
                throw new IllegalStateException("Pine tree has " + layers.length + " layers; expected " + height);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream(count * 3);
            for (int y = 0; y < height; y++) {
                String[] rows = layers[y].split("\\|", -1);
                if (rows.length != depth) {
                    throw new IllegalStateException("Pine tree layer " + y + " has " + rows.length + " rows; expected " + depth);
                }
                for (int z = 0; z < depth; z++) {
                    if (rows[z].length() != width) {
                        throw new IllegalStateException("Pine tree layer " + y + " row " + z + " has width " + rows[z].length() + "; expected " + width);
                    }
                }
                // Keep the original y/x/z block order for world placement.
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < depth; z++) {
                        char block = rows[z].charAt(x);
                        if (block == '.') {
                            continue;
                        }
                        if (block != 'L' && block != 'F') {
                            throw new IllegalStateException("Pine tree has unknown block '" + block + "' at " + x + "," + y + "," + z);
                        }
                        output.write(x);
                        output.write(y);
                        output.write(z | (block == 'L' ? 128 : 0));
                    }
                }
            }
            this.blocks = output.toByteArray();
            if (this.blocks.length != count * 3) {
                throw new IllegalStateException("Pine tree has " + this.blocks.length / 3 + " blocks; expected " + count);
            }
        }
    }
}
