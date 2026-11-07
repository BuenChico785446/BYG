package windanesz.byg.biome;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BiomeLushDesert
        extends Biome {

    public BiomeLushDesert() {
        super(new Biome.BiomeProperties("Lush Desert").setRainfall(0.5f).setBaseHeight(0.25f).setWaterColor(-14329397).setHeightVariation(0.1f).setTemperature(2.0f));
        this.setRegistryName("byg_lush_desert");
        this.topBlock = Blocks.HARDENED_CLAY.getDefaultState();
        this.fillerBlock = Blocks.HARDENED_CLAY.getDefaultState();
        this.decorator.generateFalls = true;
        this.decorator.treesPerChunk = 1;
        this.decorator.flowersPerChunk = 2;
        this.decorator.grassPerChunk = 30;
        this.decorator.deadBushPerChunk = 5;
        this.decorator.mushroomsPerChunk = 0;
        this.decorator.bigMushroomsPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 10;
        this.decorator.sandPatchesPerChunk = 0;
        this.decorator.gravelPatchesPerChunk = 0;
    }

    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos) {
        return -3615125;
    }

    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
        return -3615125;
    }

    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature) {
        return -13395457;
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        return new CustomTree();
    }

    static class CustomTree
            extends WorldGenAbstractTree {
        CustomTree() {
            super(false);
        }

        public boolean generate(World world, Random par2Random, BlockPos pos) {
            if (world.isRemote) {
                return false;
            }
            Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(), new ResourceLocation("byg", "ironwood1"));
            if (template == null) {
                return false;
            }
            Block ground = world.getBlockState(pos).getBlock();
            Block ground2 = world.getBlockState(pos.add(0, -1, 0)).getBlock();
            if (ground != Blocks.HARDENED_CLAY.getDefaultState().getBlock() && ground != Blocks.HARDENED_CLAY.getDefaultState().getBlock() && ground2 != Blocks.HARDENED_CLAY.getDefaultState().getBlock() && ground2 != Blocks.HARDENED_CLAY.getDefaultState().getBlock()) {
                return false;
            }
            Rotation rotation = Rotation.NONE;
            int rot = par2Random.nextInt(3);
            if (rot == 0) {
                rotation = Rotation.NONE;
            } else if (rot == 1) {
                rotation = Rotation.CLOCKWISE_90;
            } else if (rot == 2) {
                rotation = Rotation.CLOCKWISE_180;
            } else if (rot == 3) {
                rotation = Rotation.COUNTERCLOCKWISE_90;
            }
            Mirror mirror = Mirror.NONE;
            int mir = par2Random.nextInt(2);
            if (mir == 0) {
                mirror = Mirror.NONE;
            } else if (mir == 1) {
                mirror = Mirror.LEFT_RIGHT;
            } else if (mir == 2) {
                mirror = Mirror.FRONT_BACK;
            }
            int[] bounds = windanesz.byg.worldgen.TemplateWorldgenHelper.getFootprintBounds(template.getSize().getX(), template.getSize().getZ(), rotation, mirror);
            BlockPos placeTo = pos.add(-((bounds[0] + bounds[1]) / 2), 0, -((bounds[2] + bounds[3]) / 2));
            if (!windanesz.byg.worldgen.TemplateWorldgenHelper.fitsCurrentChunk(template, placeTo, rotation, mirror, pos)
                    || !windanesz.byg.worldgen.BygTreePlacement.hasRoom(world, pos.getX(), pos.getZ(),
                    placeTo.getX() + bounds[0], placeTo.getX() + bounds[1], placeTo.getZ() + bounds[2], placeTo.getZ() + bounds[3])) {
                return false;
            }
            windanesz.byg.worldgen.TemplateWorldgenHelper.placeTemplateWithSettings(world, template, placeTo, windanesz.byg.worldgen.TemplateWorldgenHelper.chunkPlacementSettings(par2Random, rotation, mirror, pos));
            return true;
        }

        protected boolean canGrowInto(Block blockType) {
            Material material = blockType.getDefaultState().getMaterial();
            return material == Material.AIR || blockType == Blocks.HARDENED_CLAY.getDefaultState().getBlock() || blockType == Blocks.HARDENED_CLAY.getDefaultState().getBlock();
        }

        protected void setDirtAt(World world, BlockPos pos) {
        }

        public boolean isReplaceable(World world, BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            return state.getBlock().isAir(state, (IBlockAccess) world, pos) || this.canGrowInto(state.getBlock()) || state.getBlock().isReplaceable((IBlockAccess) world, pos);
        }
    }

}

