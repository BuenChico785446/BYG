package windanesz.byg.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.Random;

public final class PlantDrops {
    private static final Random FALLBACK_RANDOM = new Random();

    private PlantDrops() {
    }

    public static void addGrassSeed(NonNullList<ItemStack> drops, IBlockAccess world, int fortune) {
        Random random = world instanceof World ? ((World) world).rand : FALLBACK_RANDOM;
        if (random.nextInt(8) != 0) {
            return;
        }
        ItemStack seed = ForgeHooks.getGrassSeed(random, fortune);
        if (!seed.isEmpty()) {
            drops.add(seed);
        }
    }
}
