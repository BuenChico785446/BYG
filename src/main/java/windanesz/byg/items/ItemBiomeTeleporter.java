package windanesz.byg.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import windanesz.byg.BiomesYouGo;
import windanesz.byg.client.BYGBiomeTab;
import windanesz.byg.command.BiomeTeleportation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Creative-only testing aid: right-click to teleport to the nearest instance of the item's biome.
 */
public class ItemBiomeTeleporter extends Item {
    private static final int COOLDOWN_TICKS = 20;

    private static final String TAG_BIOME = "biome";

    public ItemBiomeTeleporter() {
        setRegistryName(BiomesYouGo.MODID, "biome_teleporter");
        setTranslationKey("biome_teleporter");
        setCreativeTab(BYGBiomeTab.tab);
        setMaxStackSize(1);
    }

    public static ItemStack createStack(Item item, ResourceLocation biomeId) {
        ItemStack stack = new ItemStack(item);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(TAG_BIOME, biomeId.toString());
        return stack;
    }

    @Nullable
    public static ResourceLocation getBiomeId(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(TAG_BIOME, 8) ? new ResourceLocation(tag.getString(TAG_BIOME)) : null;
    }

    @Nullable
    public static Biome getBiome(ItemStack stack) {
        ResourceLocation id = getBiomeId(stack);
        return id == null ? null : Biome.REGISTRY.getObject(id);
    }

    /** Populated when the creative tab is opened, so biomes registered by any mod are included. */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        List<ResourceLocation> ids = new ArrayList<>(Biome.REGISTRY.getKeys());
        ids.sort(Comparator.comparing((ResourceLocation id) -> !BiomesYouGo.MODID.equals(id.getNamespace()))
                .thenComparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath));
        for (ResourceLocation id : ids) {
            items.add(createStack(this, id));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Biome biome = getBiome(stack);
        ResourceLocation id = getBiomeId(stack);
        ITextComponent name = biome != null ? new TextComponentString(biome.getBiomeName())
                : id != null ? new TextComponentString(id.toString())
                : new TextComponentTranslation("item.byg.biome_teleporter.no_biome");
        return new TextComponentTranslation("item.byg.biome_teleporter.named", name).getUnformattedText();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.byg.biome_teleporter"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote || !(player instanceof EntityPlayerMP)) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (!player.capabilities.isCreativeMode) {
            player.sendMessage(msg(TextFormatting.RED, "message.byg.biome_teleporter.creative_only"));
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        Biome biome = getBiome(stack);
        if (biome == null) {
            player.sendMessage(msg(TextFormatting.RED, "message.byg.biome_teleporter.invalid_biome", String.valueOf(getBiomeId(stack))));
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        boolean teleported = BiomeTeleportation.teleport((EntityPlayerMP) player, biome, player);
        return new ActionResult<>(teleported ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
    }

    private static ITextComponent msg(TextFormatting color, String key, Object... args) {
        return new TextComponentTranslation(key, args).setStyle(new Style().setColor(color));
    }
}
