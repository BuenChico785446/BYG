package windanesz.byg.biome;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.*;
import windanesz.byg.worldgen.treegenerator.BygTrees;

import java.util.Random;

public class BiomeStonePillarSavanna extends Biome {

    private static final WorldGenStonePillarMesa PILLAR_MESA_GENERATOR = new WorldGenStonePillarMesa();
    private static final WorldGenStonePillarSavannaChasm CHASM_GENERATOR = new WorldGenStonePillarSavannaChasm();
    private static final WorldGenStonePillarSavannaMound MOUND_GENERATOR = new WorldGenStonePillarSavannaMound();
    private static final WorldGenStonePillarWaterfall WATERFALL_GENERATOR = new WorldGenStonePillarWaterfall();
    private static final WorldGenTallGrass PILLAR_GRASS_GENERATOR = new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    private static final IBlockState TALL_GRASS = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);
    private static final WorldGenAbstractTree DRY_BROWN_OAK_TREE = BygTrees.DRY_BROWN_OAK;
    private static final WorldGenAbstractTree DRY_BROWN_TALL_OAK_TREE = BygTrees.DRY_BROWN_OAK_TALL;
    private static final WorldGenAbstractTree DRY_GREEN_OAK_TREE = BygTrees.DRY_GREEN_OAK;
    private static final WorldGenAbstractTree DRY_GREEN_TALL_OAK_TREE = BygTrees.DRY_GREEN_OAK_TALL;

    public BiomeStonePillarSavanna() {
        super(new Biome.BiomeProperties("Stone Pillar Savanna")
                .setRainDisabled()
                .setRainfall(0.1f)
                .setBaseHeight(0.35f)
                .setWaterColor(-14329397)
                .setHeightVariation(0.035f)
                .setTemperature(2.0f));
        this.setRegistryName("byg_stone_pillar_savanna");
        this.topBlock = Blocks.HARDENED_CLAY.getDefaultState();
        this.fillerBlock = Blocks.HARDENED_CLAY.getDefaultState();
        this.decorator.generateFalls = false;
        this.decorator.treesPerChunk = 0;
        this.decorator.flowersPerChunk = 0;
        this.decorator.grassPerChunk = 0;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.mushroomsPerChunk = 0;
        this.decorator.bigMushroomsPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 0;
        this.decorator.sandPatchesPerChunk = 0;
        this.decorator.gravelPatchesPerChunk = 0;
        this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityRabbit.class, 6, 1, 2));
        this.spawnableWaterCreatureList.clear();
    }

    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos) {
        return -4474795;
    }

    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
        return -4474795;
    }

    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature) {
        return -13395457;
    }

    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        return new WorldGenSavannaTree(false);
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        placeMicroterrain(worldIn, rand, pos);
        placeUncommonDryOak(worldIn, rand, pos);
        placeGroundGrassPatches(worldIn, rand, pos);
        placeGroundDeadBushes(worldIn, rand, pos);
        placeGroundCacti(worldIn, rand, pos);
        placePillars(worldIn, rand, pos);
        // super.decorate() is skipped on purpose: vanilla decorators spread past chunk borders and cascade worldgen.
    }

    private static void placePillars(World worldIn, Random rand, BlockPos pos) {
        int attempts = 5 + rand.nextInt(2);
        for (int i = 0; i < attempts; i++) {
            BlockPos pillarPos = pos.add(8 + rand.nextInt(16), 0, 8 + rand.nextInt(16));
            if (PILLAR_MESA_GENERATOR.generate(worldIn, rand, pillarPos)) {
                boolean hasWaterfall = rand.nextInt(4) == 0 && WATERFALL_GENERATOR.generate(worldIn, rand, pillarPos);
                if (!hasWaterfall) {
                    placePillarGrass(worldIn, rand, pillarPos);
                }
            }
        }
    }

    private static void placeGroundCacti(World worldIn, Random rand, BlockPos pos) {
        if (ModBlocks.sonoran_cactus == null) {
            return;
        }
        if (rand.nextInt(3) == 0) {
            int sonoranAttempts = 1 + rand.nextInt(3);
            for (int i = 0; i < sonoranAttempts; i++) {
                BlockPos cactusPos = worldIn.getHeight(pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
                if (cactusPos.getY() <= worldIn.getSeaLevel() + 15 && worldIn.isAirBlock(cactusPos)
                        && !worldIn.getBlockState(cactusPos.down()).getMaterial().isLiquid()) {
                    boolean flowering = rand.nextInt(3) == 0;
                    worldIn.setBlockState(cactusPos, (flowering ? ModBlocks.sonoran_cactus_flowering : ModBlocks.sonoran_cactus).getDefaultState(), 2);
                }
            }
        }
        if (rand.nextInt(2) == 0) {
            BlockPos miniPos = worldIn.getHeight(pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
            if (miniPos.getY() <= worldIn.getSeaLevel() + 15 && worldIn.isAirBlock(miniPos)
                    && !worldIn.getBlockState(miniPos.down()).getMaterial().isLiquid()) {
                worldIn.setBlockState(miniPos, ModBlocks.mini_cactus.getDefaultState(), 2);
            }
        }
    }

    private static void placeGroundDeadBushes(World worldIn, Random rand, BlockPos pos) {
        int attempts = 1 + rand.nextInt(3);
        for (int i = 0; i < attempts; i++) {
            BlockPos shrubPos = worldIn.getHeight(pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
            if (worldIn.isAirBlock(shrubPos) && Blocks.DEADBUSH.canPlaceBlockAt(worldIn, shrubPos)) {
                worldIn.setBlockState(shrubPos, Blocks.DEADBUSH.getDefaultState(), 2);
            }
        }
    }

    private static void placeGroundGrassPatches(World worldIn, Random rand, BlockPos pos) {
        int deadGrassPatches = rand.nextInt(3);
        for (int i = 0; i < deadGrassPatches; i++) {
            placeDeadGrassPatch(worldIn, rand, pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
        }

        int tallGrassPatches = rand.nextInt(3);
        for (int i = 0; i < tallGrassPatches; i++) {
            placeTallGrassPatch(worldIn, rand, pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8), pos);
        }
    }

    private static void placeDeadGrassPatch(World worldIn, Random rand, BlockPos centerPos) {
        if (ModBlocks.dead_grass == null) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            BlockPos grassPos = randomPatchSurface(worldIn, rand, centerPos);
            if (grassPos != null && worldIn.isAirBlock(grassPos) && ModBlocks.dead_grass.canPlaceBlockAt(worldIn, grassPos)) {
                worldIn.setBlockState(grassPos, ModBlocks.dead_grass.getDefaultState(), 2);
            }
        }
    }

    private static void placeTallGrassPatch(World worldIn, Random rand, BlockPos centerPos, BlockPos chunkPos) {
        for (int i = 0; i < 8; i++) {
            BlockPos surface = randomPatchSurface(worldIn, rand, centerPos);
            if (surface != null) {
                scatterTallGrass(worldIn, rand, surface, chunkPos);
            }
        }
    }

    private static BlockPos randomPatchSurface(World worldIn, Random rand, BlockPos centerPos) {
        BlockPos surface = worldIn.getHeight(centerPos.add(rand.nextInt(7) - 3, 0, rand.nextInt(7) - 3));
        return worldIn.getBiome(surface) instanceof BiomeStonePillarSavanna ? surface : null;
    }

    private static void scatterTallGrass(World worldIn, Random rand, BlockPos surface, BlockPos chunkPos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(surface);
        while (cursor.getY() > 0) {
            IBlockState state = worldIn.getBlockState(cursor);
            Block block = state.getBlock();
            if (!block.isAir(state, worldIn, cursor) && !block.isLeaves(state, worldIn, cursor)) {
                break;
            }
            cursor.move(EnumFacing.DOWN);
        }

        int originX = cursor.getX();
        int originY = cursor.getY();
        int originZ = cursor.getZ();
        for (int attempt = 0; attempt < 128; attempt++) {
            int x = originX + rand.nextInt(8) - rand.nextInt(8);
            int y = originY + rand.nextInt(4) - rand.nextInt(4);
            int z = originZ + rand.nextInt(8) - rand.nextInt(8);
            if (x < chunkPos.getX() || x > chunkPos.getX() + 31 || z < chunkPos.getZ() || z > chunkPos.getZ() + 31) {
                continue;
            }
            cursor.setPos(x, y, z);
            if (worldIn.isAirBlock(cursor) && Blocks.TALLGRASS.canBlockStay(worldIn, cursor, TALL_GRASS)) {
                worldIn.setBlockState(cursor, TALL_GRASS, 2);
            }
        }
    }

    private static void placeMicroterrain(World worldIn, Random rand, BlockPos pos) {
        int moundAttempts = 1 + (rand.nextInt(4) == 0 ? 1 : 0);
        for (int i = 0; i < moundAttempts; i++) {
            MOUND_GENERATOR.generate(worldIn, rand, pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
        }

        if (rand.nextInt(5) == 0) {
            CHASM_GENERATOR.generate(worldIn, rand, pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
        }
    }

    private static void placeUncommonDryOak(World worldIn, Random rand, BlockPos pos) {
        int count = rand.nextInt(3) == 0 ? 2 : 1;
        for (int i = 0; i < count; i++) {
            BlockPos treePos = worldIn.getHeight(pos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
            WorldGenAbstractTree tree = getRandomDryOakFeature(rand);
            tree.setDecorationDefaults();
            if (BygTreePlacement.allowTrees(worldIn, rand, treePos) && tree.generate(worldIn, rand, treePos)) {
                tree.generateSaplings(worldIn, rand, treePos);
            }
        }
    }

    private static WorldGenAbstractTree getRandomDryOakFeature(Random rand) {
        switch (rand.nextInt(4)) {
            case 0:
                return DRY_BROWN_TALL_OAK_TREE;
            case 1:
                return DRY_GREEN_TALL_OAK_TREE;
            case 2:
                return DRY_BROWN_OAK_TREE;
            default:
                return DRY_GREEN_OAK_TREE;
        }
    }

    private static void placePillarGrass(World worldIn, Random rand, BlockPos pillarPos) {
        int attempts = 1 + rand.nextInt(2);
        for (int i = 0; i < attempts; i++) {
            BlockPos grassPos = worldIn.getHeight(pillarPos.add(rand.nextInt(5) - 2, 0, rand.nextInt(5) - 2));
            if (grassPos.getY() >= worldIn.getSeaLevel() + 12) {
                PILLAR_GRASS_GENERATOR.generate(worldIn, rand, grassPos);
            }
        }
    }
}

