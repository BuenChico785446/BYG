package windanesz.byg.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import windanesz.byg.client.BYGTab;

public class BlockWoodBase extends Block {
    private final boolean silkHarvest;

    public BlockWoodBase(String name, int harvestLevel, float hardness, float resistance, int lightOpacity, boolean silkHarvest) {
        super(Material.WOOD);
        this.silkHarvest = silkHarvest;
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", harvestLevel);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setLightLevel(0.0f);
        this.setLightOpacity(lightOpacity);
        this.setCreativeTab(BYGTab.tab);
    }

    @Override
    public boolean isFlammable(IBlockAccess blockAccess, BlockPos pos, EnumFacing face) {
        return true;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return this.silkHarvest;
    }

    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }
}

