package windanesz.byg.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import windanesz.byg.client.BYGTab;

public class BlockCrate
        extends Block
        implements ITileEntityProvider {


    public BlockCrate() {
        super(Material.WOOD);
        this.setRegistryName("crate");
        this.setTranslationKey("crate");
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", 0);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setLightLevel(0.0f);
        this.setLightOpacity(255);
        this.setCreativeTab(BYGTab.tab);
    }

    public static void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityCrate.class, "byg:tileentitycrate");
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCrate();
    }

    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
        super.eventReceived(state, worldIn, pos, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof TileEntityCrate) {
            // Unopened loot crates still drop their loot when broken.
            if (!world.isRemote) ((TileEntityCrate) tileentity).fillWithLoot(null);
            InventoryHelper.dropInventoryItems(world, pos, ((TileEntityCrate) tileentity));
        }
        world.removeTileEntity(pos);
        super.breakBlock(world, pos, state);
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityCrate) {
            return Container.calcRedstoneFromInventory(((TileEntityCrate) tileentity));
        }
        return 0;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityCrate) {
                player.displayGUIChest((IInventory) tileentity);
            }
        }
        return true;
    }

    public static class TileEntityCrate
            extends TileEntityLockableLoot {
        private NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);

        public int getSizeInventory() {
            return 27;
        }

        public boolean isEmpty() {
            for (ItemStack itemstack : this.stacks) {
                if (itemstack.isEmpty()) continue;
                return false;
            }
            return true;
        }

        public ItemStack getStackInSlot(int slot) {
            return (ItemStack) this.stacks.get(slot);
        }

        public String getName() {
            return this.hasCustomName() ? this.customName : "container.crate";
        }

        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            this.stacks = NonNullList.withSize((int) this.getSizeInventory(), ItemStack.EMPTY);
            if (!this.checkLootAndRead(compound)) {
                ItemStackHelper.loadAllItems((NBTTagCompound) compound, this.stacks);
            }
            if (compound.hasKey("CustomName", 8)) {
                this.customName = compound.getString("CustomName");
            }
        }

        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);
            if (!this.checkLootAndWrite(compound)) {
                ItemStackHelper.saveAllItems((NBTTagCompound) compound, this.stacks);
            }
            if (this.hasCustomName()) {
                compound.setString("CustomName", this.customName);
            }
            return compound;
        }

        public int getInventoryStackLimit() {
            return 64;
        }

        public String getGuiID() {
            return "byg:crate";
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
            this.fillWithLoot(playerIn);
            return new ContainerChest(playerInventory, this, playerIn);
        }

        protected NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }

}


