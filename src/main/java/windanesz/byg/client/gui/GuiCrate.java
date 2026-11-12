package windanesz.byg.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import windanesz.byg.blocks.BlockCrate;

public class GuiCrate {
    public static int GUIID = 12;

    public static IInventory inherited;



    public static class GuiWindow
            extends GuiContainer {
        private static final ResourceLocation texture = new ResourceLocation("byg:textures/crategui.png");
        World world;
        int x;
        int y;
        int z;
        EntityPlayer entity;

        public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
            super((Container) new ContainerCrate(world, x, y, z, entity));
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.entity = entity;
            this.xSize = 176;
            this.ySize = 180;
        }

        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.renderHoveredToolTip(mouseX, mouseY);
        }

        protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.renderEngine.bindTexture(texture);
            int k = (this.width - this.xSize) / 2;
            int l = (this.height - this.ySize) / 2;
            this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
            this.zLevel = 100.0f;
        }

        protected void drawGuiContainerForegroundLayer(int par1, int par2) {
            this.fontRenderer.drawString(net.minecraft.client.resources.I18n.format("tile.crate.name"), 7, 8, -10066330);
        }
    }

    public static class ContainerCrate
            extends Container {
        World world;
        EntityPlayer entity;
        int x;
        int y;
        int z;

        public ContainerCrate(World world, int x, int y, int z, EntityPlayer player) {
            int si;
            this.world = world;
            this.entity = player;
            this.x = x;
            this.y = y;
            this.z = z;
            TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
            if (ent instanceof BlockCrate.TileEntityCrate && !world.isRemote) {
                // The custom GUI bypasses createContainer(), so roll any pending loot table here.
                ((BlockCrate.TileEntityCrate) ent).fillWithLoot(player);
            }
            inherited = ent instanceof BlockCrate.TileEntityCrate ? (IInventory) ent : new InventoryBasic("", true, 9);
            this.addSlotToContainer(new Slot(inherited, 0, 8, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 1, 8, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 2, 8, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 3, 26, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 4, 26, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 5, 26, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 6, 44, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 7, 44, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 8, 44, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 9, 62, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 10, 62, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 11, 62, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 12, 80, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 13, 80, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 14, 80, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 15, 98, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 16, 98, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 17, 98, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 18, 116, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 19, 116, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 20, 116, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 21, 134, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 22, 134, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 23, 134, 55) {
            });
            this.addSlotToContainer(new Slot(inherited, 24, 152, 19) {
            });
            this.addSlotToContainer(new Slot(inherited, 25, 152, 37) {
            });
            this.addSlotToContainer(new Slot(inherited, 26, 152, 55) {
            });
            for (si = 0; si < 3; ++si) {
                for (int sj = 0; sj < 9; ++sj) {
                    this.addSlotToContainer(new Slot(player.inventory, sj + (si + 1) * 9, 8 + sj * 18, 91 + si * 18));
                }
            }
            for (si = 0; si < 9; ++si) {
                this.addSlotToContainer(new Slot(player.inventory, si, 8 + si * 18, 149));
            }
        }

        public boolean canInteractWith(EntityPlayer player) {
            return true;
        }

        public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = (Slot) this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();
                if (index < 27) {
                    if (!this.mergeItemStack(itemstack1, 27, this.inventorySlots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                    slot.onSlotChange(itemstack1, itemstack);
                } else if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
                    if (index < 54 ? !this.mergeItemStack(itemstack1, 54, this.inventorySlots.size(), true) : !this.mergeItemStack(itemstack1, 27, 54, false)) {
                        return ItemStack.EMPTY;
                    }
                    return ItemStack.EMPTY;
                }
                if (itemstack1.getCount() == 0) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }
                if (itemstack1.getCount() == itemstack.getCount()) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(playerIn, itemstack1);
            }
            return itemstack;
        }


    }
}



