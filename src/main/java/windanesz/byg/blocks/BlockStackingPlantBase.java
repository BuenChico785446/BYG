package windanesz.byg.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import windanesz.byg.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class BlockStackingPlantBase extends net.minecraft.block.BlockReed implements IShearable {
    private final String name;
    private final EnumPlantType plantType;
    private final int maxHeight;
    private final boolean flammable;
    private final boolean replaceable;
    private final boolean caveLightBreak;
    private final boolean damagesEntities;
    private final Supplier<ItemStack> dropSupplier;
    private final boolean shearable;

    public BlockStackingPlantBase(String name, @Nullable CreativeTabs tab, float lightLevel, EnumPlantType plantType, int maxHeight,
                                  boolean flammable, boolean replaceable, boolean caveLightBreak, boolean damagesEntities,
                                  Supplier<ItemStack> dropSupplier, boolean shearable) {
        this.name = name;
        this.plantType = plantType;
        this.maxHeight = maxHeight;
        this.flammable = flammable;
        this.replaceable = replaceable;
        this.caveLightBreak = caveLightBreak;
        this.damagesEntities = damagesEntities;
        this.dropSupplier = dropSupplier;
        this.shearable = shearable;
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(tab);
        this.setHardness(0.01f);
        this.setResistance(2.0f);
        this.setLightLevel(lightLevel);
        this.setTranslationKey(name);
        this.setRegistryName(name);
    }

    @Override
    public boolean isFlammable(IBlockAccess blockAccess, BlockPos pos, EnumFacing face) {
        return flammable;
    }

    @Override
    public boolean isReplaceable(IBlockAccess blockAccess, BlockPos pos) {
        return replaceable || super.isReplaceable(blockAccess, pos);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return plantType;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        return blockBelow.canSustainPlant(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP, this) || blockBelow == this;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if ("short_dead_grass".equals(name)) {
            // texture only fills the lower 9/16 of the block
            return new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.5625D, 0.9D);
        }
        return super.getBoundingBox(state, source, pos);
    }

    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess access, BlockPos pos, int pass) {
        return 0xFFFFFF;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (caveLightBreak && world.getLightFromNeighbors(pos) >= 13) {
            this.dropBlockAsItem(world, pos, state, 1);
            world.setBlockToAir(pos);
            return;
        }
        if ((world.getBlockState(pos.down()).getBlock() == this || this.checkForDrop(world, pos, state)) && world.isAirBlock(pos.up())) {
            int height = 1;
            while (world.getBlockState(pos.down(height)).getBlock() == this) {
                ++height;
            }
            if (height < maxHeight) {
                int age = state.getValue(AGE);
                if (age == 15) {
                    world.setBlockState(pos.up(), this.getDefaultState());
                    world.setBlockState(pos, state.withProperty(AGE, 0), 4);
                } else {
                    world.setBlockState(pos, state.withProperty(AGE, age + 1), 4);
                }
            }
        }
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollision(world, pos, state, entity);
        if (damagesEntities) {
            entity.attackEntityFrom(DamageSource.GENERIC, Config.getDamagingPlantDamage());
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(dropSupplier.get());
        if (shearable) {
            PlantDrops.addGrassSeed(drops, world, fortune);
        }
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
        return shearable;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }
}
