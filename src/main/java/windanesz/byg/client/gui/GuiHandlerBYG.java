package windanesz.byg.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandlerBYG implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GuiNetherFurnace.GUIID) {
            return new GuiNetherFurnace.ContainerNetherFurnaceLit(world, x, y, z, player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GuiNetherFurnace.GUIID) {
            return new GuiNetherFurnace.GuiWindow(world, x, y, z, player);
        }
        return null;
    }
}

