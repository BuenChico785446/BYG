package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.BygTreePlacement;

import java.util.Random;

public final class MangroveTreeGenerator extends WorldGenAbstractTree {
    private static final ResourceLocation MANGROVE_MARSHES = new ResourceLocation("byg", "byg_mangrove_marshes");
    private static final int MAX_WATER_DEPTH = 4;

    public MangroveTreeGenerator() {
        super(false);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        BlockPos soil = findSoil(world, position);
        if (soil == null) {
            return false;
        }
        if (!isMangroveSoil(world.getBlockState(soil))) {
            return false;
        }

        int waterDepth = position.getY() - soil.getY() - 1;
        BlockPos rootBase = soil.up();
        int rootRise = Math.max(3, waterDepth + 1) + random.nextInt(2);
        int trunkHeight = 4 + random.nextInt(5);
        int canopyRadiusX = 4 + random.nextInt(2);
        int canopyRadiusZ = 4 + random.nextInt(2);

        int leanX = 0;
        int leanZ = 0;
        switch (random.nextInt(4)) {
            case 0: leanX = 1; break;
            case 1: leanX = -1; break;
            case 2: leanZ = 1; break;
            default: leanZ = -1; break;
        }

        BlockPos junction = rootBase.up(rootRise);
        BlockPos crownCenter = junction.add(leanX, trunkHeight, leanZ);
        int horizontalRadius = Math.max(canopyRadiusX, canopyRadiusZ) + 1;
        if (crownCenter.getY() + 3 >= world.getHeight()) {
            return false;
        }
        if (!isInsideOriginBiome(world, position, crownCenter, horizontalRadius)) {
            return false;
        }
        if (!hasCanopyRoom(world, crownCenter, horizontalRadius)
                || !BygTreePlacement.hasRoom(world, position.getX(), position.getZ(),
                crownCenter.getX() - horizontalRadius, crownCenter.getX() + horizontalRadius,
                crownCenter.getZ() - horizontalRadius, crownCenter.getZ() + horizontalRadius)) {
            return false;
        }

        IBlockState log = ModBlocks.mangrove_log.getDefaultState();
        IBlockState leaves = ModBlocks.mangrove_leaves.getDefaultState();
        placeStiltRoots(world, random, rootBase, rootRise, log);
        placeLeaningTrunk(world, junction, trunkHeight, leanX, leanZ, log);
        placeCrownLimbs(world, random, crownCenter, log);
        placeCanopy(world, random, crownCenter, canopyRadiusX, canopyRadiusZ, leaves);
        return true;
    }

    private BlockPos findSoil(World world, BlockPos position) {
        BlockPos cursor = position.down();
        int waterDepth = 0;
        while (world.getBlockState(cursor).getMaterial() == Material.WATER) {
            if (++waterDepth > MAX_WATER_DEPTH) {
                return null;
            }
            cursor = cursor.down();
        }
        return cursor;
    }

    private boolean isMangroveSoil(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == ModBlocks.mud_block || block == ModBlocks.white_sand
                || material == Material.GROUND || material == Material.GRASS
                || material == Material.CLAY || material == Material.SAND;
    }

    private boolean isInsideOriginBiome(World world, BlockPos origin, BlockPos crownCenter, int radius) {
        return isMangroveMarshes(world, origin)
                && isMangroveMarshes(world, crownCenter.add(radius, 0, 0))
                && isMangroveMarshes(world, crownCenter.add(-radius, 0, 0))
                && isMangroveMarshes(world, crownCenter.add(0, 0, radius))
                && isMangroveMarshes(world, crownCenter.add(0, 0, -radius));
    }

    private boolean isMangroveMarshes(World world, BlockPos pos) {
        return MANGROVE_MARSHES.equals(world.getBiome(pos).getRegistryName());
    }

    private boolean hasCanopyRoom(World world, BlockPos center, int radius) {
        for (int y = -2; y <= 3; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > radius * radius) {
                        continue;
                    }
                    BlockPos pos = center.add(x, y, z);
                    IBlockState state = world.getBlockState(pos);
                    if (state.getMaterial() == Material.WOOD && state.getBlock() != ModBlocks.mangrove_log) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeStiltRoots(World world, Random random, BlockPos base, int rootRise, IBlockState log) {
        int rootCount = 7 + random.nextInt(5);
        double phase = random.nextDouble() * Math.PI * 2.0D;
        for (int root = 0; root < rootCount; root++) {
            double angle = phase + Math.PI * 2.0D * root / rootCount + (random.nextDouble() - 0.5D) * 0.35D;
            int radius = 2 + random.nextInt(3);
            int footX = (int) Math.round(Math.cos(angle) * radius);
            int footZ = (int) Math.round(Math.sin(angle) * radius);

            for (int y = 0; y <= rootRise; y++) {
                double remaining = 1.0D - (double) y / (double) rootRise;
                int x = (int) Math.round(footX * remaining);
                int z = (int) Math.round(footZ * remaining);
                placeRootLog(world, base.add(x, y, z), log);

                // The larger NBT trees have broad, doubled roots at their feet.
                if (y < 2 && radius >= 3 && random.nextBoolean()) {
                    int sideX = Integer.signum(-footZ);
                    int sideZ = Integer.signum(footX);
                    placeRootLog(world, base.add(x + sideX, y, z + sideZ), log);
                }
            }
        }
    }

    private void placeLeaningTrunk(World world, BlockPos junction, int height, int leanX, int leanZ, IBlockState log) {
        for (int y = 0; y <= height; y++) {
            int x = y >= (height + 1) / 2 ? leanX : 0;
            int z = y >= (height + 1) / 2 ? leanZ : 0;
            placeRootLog(world, junction.add(x, y, z), log);
        }
    }

    private void placeCrownLimbs(World world, Random random, BlockPos center, IBlockState log) {
        int branchCount = 5 + random.nextInt(4);
        double phase = random.nextDouble() * Math.PI * 2.0D;
        for (int branch = 0; branch < branchCount; branch++) {
            double angle = phase + Math.PI * 2.0D * branch / branchCount + (random.nextDouble() - 0.5D) * 0.4D;
            int length = 2 + random.nextInt(4);
            IBlockState limb = log.withProperty(BlockDirectional.FACING,
                    Math.abs(Math.cos(angle)) >= Math.abs(Math.sin(angle)) ? EnumFacing.UP : EnumFacing.EAST);
            for (int step = 0; step <= length; step++) {
                int x = (int) Math.round(Math.cos(angle) * step);
                int z = (int) Math.round(Math.sin(angle) * step);
                int y = step == length && random.nextBoolean() ? 1 : 0;
                placeRootLog(world, center.add(x, y, z), limb);
            }
        }
    }

    private void placeCanopy(World world, Random random, BlockPos center, int radiusX, int radiusZ, IBlockState leaves) {
        placeCanopyLayer(world, random, center.down(), radiusX - 1, radiusZ - 1, 0.24D, leaves);
        placeCanopyLayer(world, random, center, radiusX, radiusZ, 0.14D, leaves);
        placeCanopyLayer(world, random, center.up(), radiusX - 1, radiusZ - 1, 0.25D, leaves);
        placeCanopyLayer(world, random, center.up(2), 2, 2, 0.35D, leaves);
    }

    private void placeCanopyLayer(World world, Random random, BlockPos center, int radiusX, int radiusZ,
                                  double edgeGapChance, IBlockState leaves) {
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int z = -radiusZ; z <= radiusZ; z++) {
                double distance = (double) (x * x) / (double) (radiusX * radiusX)
                        + (double) (z * z) / (double) (radiusZ * radiusZ);
                if (distance > 1.0D || distance > 0.68D && random.nextDouble() < edgeGapChance) {
                    continue;
                }
                placeLeaf(world, center.add(x, 0, z), leaves);
            }
        }
    }

    private void placeRootLog(World world, BlockPos pos, IBlockState log) {
        IBlockState current = world.getBlockState(pos);
        if (current.getBlock() == ModBlocks.mangrove_log || canReplace(current, world, pos, true)) {
            setBlockAndNotifyAdequately(world, pos, log);
        }
    }

    private void placeLeaf(World world, BlockPos pos, IBlockState leaves) {
        IBlockState current = world.getBlockState(pos);
        if (canReplace(current, world, pos, false)) {
            setBlockAndNotifyAdequately(world, pos, leaves);
        }
    }

    private boolean canReplace(IBlockState state, IBlockAccess world, BlockPos pos, boolean allowWater) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block.isAir(state, world, pos)
                || block.isLeaves(state, world, pos)
                || allowWater && material == Material.WATER
                || material == Material.PLANTS
                || material == Material.VINE
                || material == Material.SNOW
                || block.isReplaceable(world, pos)
                || block.canBeReplacedByLeaves(state, world, pos)
                || block instanceof BlockBush
                || block == Blocks.TALLGRASS
                || block == Blocks.DOUBLE_PLANT;
    }
}
