package windanesz.byg.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import windanesz.byg.BiomesYouGo;
import windanesz.byg.Config;
import windanesz.byg.armour.ArmorMaterialKasai;
import windanesz.byg.registry.ModItems;
import windanesz.byg.registry.ModPotions;

import java.util.*;

@Mod.EventBusSubscriber(modid = BiomesYouGo.MODID)
public final class EventHandler {
    static final SoundEvent SHEAR_SOUND = SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.sheep.shear"));
    private static final Map<UUID, Integer> lastHookId = new HashMap<>();
    private static final Set<UUID> wormActive = new HashSet<>();
    /** Boolean in the entity's persistent data (saved as ForgeData), so the regeneration survives world reloads. */
    private static final String PET_REGEN_TAG = BiomesYouGo.MODID + ":golden_beetroot_regen";

    private EventHandler() {
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        if (!isWearingFullLathariumSet(player) || !player.isSneaking()) {
            return;
        }
        World world = player.world;
        if (!Config.isLathariumBootsLevitationEnabled()) {
            return;
        }
        player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION,
                Config.getLathariumBootsLevitationDuration(),
                Config.getLathariumBootsLevitationAmplifier(),
                false,
                false));
        player.fallDistance = 0.0F;
        int particleCount = Config.getLathariumBootsParticleCount();
        if (particleCount > 0 && Math.random() < Config.getLathariumBootsParticleChance() && world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.END_ROD, player.posX, player.posY, player.posZ, particleCount, 3.0, 3.0, 3.0, 1.0, new int[0]);
        }
    }

    @SubscribeEvent
    public static void onWormFishingTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || !Config.isWormFishingEnhancementEnabled()) {
            return;
        }
        EntityPlayer player = event.player;
        EntityFishHook hook = player.fishEntity;
        if (hook == null) {
            lastHookId.remove(player.getUniqueID());
            if (wormActive.remove(player.getUniqueID())) {
                player.inventory.addItemStackToInventory(new ItemStack(ModItems.worm));
            }
            return;
        }
        int hookId = hook.getEntityId();
        if (lastHookId.getOrDefault(player.getUniqueID(), -1) == hookId) {
            return;
        }
        lastHookId.put(player.getUniqueID(), hookId);
        if (player.inventory.clearMatchingItems(ModItems.worm, -1, 1, null) <= 0) {
            return;
        }
        wormActive.add(player.getUniqueID());
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        ItemStack rod = mainHand.getItem() instanceof ItemFishingRod ? mainHand
                : offHand.getItem() instanceof ItemFishingRod ? offHand : ItemStack.EMPTY;
        int lureLevel = rod.isEmpty() ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.LURE, rod);
        hook.setLureSpeed(lureLevel + Config.getWormLureLevel());
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        if (Config.isWormFishingEnhancementEnabled()) {
            wormActive.remove(event.getEntityPlayer().getUniqueID());
        }
    }

    @SubscribeEvent
    public static void onKasaiFireWardTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || player.world.isRemote || !player.isBurning()
                || !Config.isKasaiFireWardEnabled() || player.isPotionActive(MobEffects.FIRE_RESISTANCE)
                || ArmorMaterialKasai.countWornPieces(player) < 4
                || player.getCooldownTracker().hasCooldown(ArmorMaterialKasai.helmet)) {
            return;
        }
        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Config.getKasaiFireWardDuration(), 0, false, true));
        int cooldown = Config.getKasaiFireWardCooldown();
        if (cooldown > 0) {
            player.getCooldownTracker().setCooldown(ArmorMaterialKasai.helmet, cooldown);
            player.getCooldownTracker().setCooldown(ArmorMaterialKasai.body, cooldown);
            player.getCooldownTracker().setCooldown(ArmorMaterialKasai.legs, cooldown);
            player.getCooldownTracker().setCooldown(ArmorMaterialKasai.boots, cooldown);
        }
    }

    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if (event.getEntityLiving().isPotionActive(ModPotions.clarity)
                && effect.getPotion() != ModPotions.clarity) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        if (event.getPotionEffect().getPotion() != ModPotions.clarity) {
            return;
        }
        for (PotionEffect activeEffect : new ArrayList<>(event.getEntityLiving().getActivePotionEffects())) {
            if (activeEffect.getPotion() != ModPotions.clarity) {
                event.getEntityLiving().removePotionEffect(activeEffect.getPotion());
            }
        }
    }

    @SubscribeEvent
    public static void onFeedGoldenBeetroot(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        if (stack.getItem() != ModItems.golden_beetroot || !Config.isGoldenBeetrootPetRegenEnabled()
                || !(target instanceof EntityLivingBase) || !isTamed(target)) {
            return;
        }
        // Consume the interaction on both sides so the beetroot is never eaten by the player.
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (event.getWorld().isRemote || target.getEntityData().getBoolean(PET_REGEN_TAG)) {
            return;
        }
        target.getEntityData().setBoolean(PET_REGEN_TAG, true);
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            Advancement advancement = player.getServer().getAdvancementManager()
                    .getAdvancement(new ResourceLocation(BiomesYouGo.MODID, "token_of_love"));
            if (advancement != null) {
                player.getAdvancements().grantCriterion(advancement, "feed_tamed_animal");
            }
        }
        if (!event.getEntityPlayer().capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        WorldServer world = (WorldServer) event.getWorld();
        // Approximate the mouth: at eye height, slightly lower, pushed forward along the head's facing direction.
        double headYaw = Math.toRadians(((EntityLivingBase) target).rotationYawHead);
        double reach = target.width * 0.5D;
        double mouthX = target.posX - Math.sin(headYaw) * reach;
        double mouthY = target.posY + target.getEyeHeight() - 0.1D;
        double mouthZ = target.posZ + Math.cos(headYaw) * reach;
        world.spawnParticle(EnumParticleTypes.ITEM_CRACK, mouthX, mouthY, mouthZ, 12, 0.08, 0.08, 0.08, 0.05,
                Item.getIdFromItem(ModItems.golden_beetroot));
        world.spawnParticle(EnumParticleTypes.HEART, target.posX, target.posY + target.height, target.posZ,
                7, 0.4, 0.4, 0.4, 0.0, new int[0]);
    }

    @SubscribeEvent
    public static void onPetRegenTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || entity.isDead || entity.getHealth() >= entity.getMaxHealth()
                || !entity.getEntityData().getBoolean(PET_REGEN_TAG) || !Config.isGoldenBeetrootPetRegenEnabled()
                || entity.ticksExisted % Config.getGoldenBeetrootPetRegenInterval() != 0) {
            return;
        }
        entity.heal(Config.getGoldenBeetrootPetRegenAmount());
    }

    private static boolean isTamed(Entity entity) {
        return entity instanceof EntityTameable && ((EntityTameable) entity).isTamed()
                || entity instanceof AbstractHorse && ((AbstractHorse) entity).isTame();
    }

    private static boolean isWearingFullLathariumSet(EntityPlayer player) {
        if (ModItems.latharium_armour_helmet == null || ModItems.latharium_armour_body == null
                || ModItems.latharium_armour_legs == null || ModItems.latharium_armour_boots == null) {
            return false;
        }
        return isWearing(player, EntityEquipmentSlot.HEAD, ModItems.latharium_armour_helmet)
                && isWearing(player, EntityEquipmentSlot.CHEST, ModItems.latharium_armour_body)
                && isWearing(player, EntityEquipmentSlot.LEGS, ModItems.latharium_armour_legs)
                && isWearing(player, EntityEquipmentSlot.FEET, ModItems.latharium_armour_boots);
    }

    private static boolean isWearing(EntityPlayer player, EntityEquipmentSlot slot, net.minecraft.item.Item expectedItem) {
        ItemStack stack = player.getItemStackFromSlot(slot);
        return !stack.isEmpty() && stack.getItem() == expectedItem;
    }


    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (windanesz.byg.registry.ModBlocks.carved_melon == null || player == null || player.getHeldItemMainhand().getItem() != Items.SHEARS || world.getBlockState(pos).getBlock() != Blocks.MELON_BLOCK) {
            return;
        }
        world.playSound(null, pos, SHEAR_SOUND, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        world.setBlockState(pos, windanesz.byg.registry.ModBlocks.carved_melon.getDefaultState(), 3);
    }

    /** Right-clicking a BYG log or wood block with any axe strips it into its stripped variant, as in later versions. */
    @SubscribeEvent
    public static void onAxeStrip(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || !held.getItem().getToolClasses(held).contains("axe")) {
            return;
        }
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block.getRegistryName() == null || !BiomesYouGo.MODID.equals(block.getRegistryName().getNamespace())) {
            return;
        }
        String path = block.getRegistryName().getPath();
        if (path.startsWith("stripped_") || !(path.endsWith("_log") || path.endsWith("_wood"))) {
            return;
        }
        Block stripped = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BiomesYouGo.MODID, "stripped_" + path));
        if (stripped == null || stripped == Blocks.AIR) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (world.isRemote) {
            return;
        }
        IBlockState strippedState = stripped.getDefaultState();
        if (state.getPropertyKeys().contains(BlockDirectional.FACING) && strippedState.getPropertyKeys().contains(BlockDirectional.FACING)) {
            strippedState = strippedState.withProperty(BlockDirectional.FACING, state.getValue(BlockDirectional.FACING));
        }
        world.setBlockState(pos, strippedState, 11);
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        EntityPlayer player = event.getEntityPlayer();
        if (!player.capabilities.isCreativeMode) {
            held.damageItem(1, player);
        }
    }
}
