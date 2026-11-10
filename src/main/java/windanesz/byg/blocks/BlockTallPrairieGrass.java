package windanesz.byg.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import windanesz.byg.registry.ModBlocks;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockTallPrairieGrass extends BlockBush implements IPlantable, IShearable {
    public static final PropertyEnum<Half> HALF = PropertyEnum.create("half", Half.class);

    public BlockTallPrairieGrass() {
        super(Material.VINE);
        this.setRegistryName("prairie_grass_tall");
        this.setTranslationKey("prairie_grass_tall");
        this.setSoundType(SoundType.PLANT);
        this.setHardness(0.0f);
        this.setResistance(0.0f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, Half.LOWER));
        this.setCreativeTab(null);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(HALF, (meta & 1) == 0 ? Half.LOWER : Half.UPPER);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HALF) == Half.UPPER ? 1 : 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
    }

    public void placeAt(World worldIn, BlockPos lowerPos, int flags) {
        worldIn.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, Half.LOWER), flags);
        worldIn.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, Half.UPPER), flags);
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getBlock() != this) {
            return super.canBlockStay(worldIn, pos, state);
        }

        if (state.getValue(HALF) == Half.UPPER) {
            return worldIn.getBlockState(pos.down()).getBlock() == this;
        }

        return worldIn.getBlockState(pos.up()).getBlock() == this && super.canBlockStay(worldIn, pos, state);
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (this.canBlockStay(worldIn, pos, state)) {
            return;
        }

        boolean upperHalf = state.getValue(HALF) == Half.UPPER;
        BlockPos upperPos = upperHalf ? pos : pos.up();
        BlockPos lowerPos = upperHalf ? pos.down() : pos;
        Block upperBlock = upperHalf ? this : worldIn.getBlockState(upperPos).getBlock();
        Block lowerBlock = upperHalf ? worldIn.getBlockState(lowerPos).getBlock() : this;

        if (!upperHalf) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
        }

        if (upperBlock == this) {
            worldIn.setBlockState(upperPos, Blocks.AIR.getDefaultState(), 2);
        }

        if (lowerBlock == this) {
            worldIn.setBlockState(lowerPos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, Half.UPPER), 2);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (state.getValue(HALF) == Half.UPPER) {
            if (worldIn.getBlockState(pos.down()).getBlock() == this) {
                if (!player.capabilities.isCreativeMode) {
                    worldIn.destroyBlock(pos.down(), true);
                } else {
                    worldIn.setBlockToAir(pos.down());
                }
            }
        } else {
            if (worldIn.getBlockState(pos.up()).getBlock() == this) {
                worldIn.setBlockToAir(pos.up());
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (state.getValue(HALF) == Half.LOWER) {
            PlantDrops.addGrassSeed(drops, world, fortune);
        }
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(ModBlocks.prairie_grass, 2));
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND;
    }

    public enum Half implements IStringSerializable {
        UPPER,
        LOWER;

        @Override
        public String getName() {
            return this == UPPER ? "upper" : "lower";
        }
    }
}
