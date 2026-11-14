package windanesz.byg.registry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmeltingRecipes {
    private static final Map<String, Integer> FUEL_VALUES = new HashMap<>();

    public static void init() {
        if (ModItems.maple_sap != null) {
            GameRegistry.addSmelting(new ItemStack(ModItems.maple_sap, 1), new ItemStack(ModItems.maple_syrup, 1), 0.35f);
        }
        if (windanesz.byg.registry.ModBlocks.black_sand != null) {
            GameRegistry.addSmelting(new ItemStack(windanesz.byg.registry.ModBlocks.black_sand, 1), new ItemStack((Block) Blocks.STAINED_GLASS, 1, 15), 1.0f);
        }
        if (ModItems.cooked_carrot != null) {
            GameRegistry.addSmelting(new ItemStack(Items.CARROT, 1), new ItemStack(ModItems.cooked_carrot, 1), 1.0f);
        }
        if (ModItems.cattail_rhizome != null) {
            GameRegistry.addSmelting(new ItemStack(ModItems.cattail_rhizome, 1), new ItemStack(ModItems.cooked_cattail_rhizome, 1), 0.35f);
        }
        if (ModItems.cooked_pufferfish != null) {
            GameRegistry.addSmelting(new ItemStack(Items.FISH, 1, 3), new ItemStack(ModItems.cooked_pufferfish, 1), 1.0f);
        }
        if (ModItems.cooked_pumpkin_seeds != null) {
            GameRegistry.addSmelting(new ItemStack(Items.PUMPKIN_SEEDS, 1), new ItemStack(ModItems.cooked_pumpkin_seeds, 1), 1.0f);
        }
        if (ModItems.cooked_spider_eye != null) {
            GameRegistry.addSmelting(new ItemStack(Items.SPIDER_EYE, 1), new ItemStack(ModItems.cooked_spider_eye, 1), 1.0f);
        }
        if (ModItems.cooked_tropical_fish != null) {
            GameRegistry.addSmelting(new ItemStack(Items.FISH, 1, 2), new ItemStack(ModItems.cooked_tropical_fish, 1), 1.0f);
        }
        if (ModItems.kiwi_raw != null) {
            GameRegistry.addSmelting(new ItemStack(ModItems.kiwi_raw, 1), new ItemStack(ModItems.kiwi_cooked, 1), 0.35f);
        }
        if (ModItems.rudo_beans != null) {
            GameRegistry.addSmelting(new ItemStack(ModItems.rudo_beans, 1), new ItemStack(ModItems.rudo_beans_roasted, 1), 1.0f);
        }
        if (windanesz.byg.registry.ModBlocks.white_sand != null) {
            GameRegistry.addSmelting(new ItemStack(windanesz.byg.registry.ModBlocks.white_sand, 1), new ItemStack((Block) Blocks.STAINED_GLASS, 1, 0), 1.0f);
        }
        for (Block cactus : new Block[]{ModBlocks.sonoran_cactus, ModBlocks.sonoran_cactus_flowering}) {
            if (cactus != null) {
                GameRegistry.addSmelting(new ItemStack(cactus, 1), new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()), 0.2f);
            }
        }
        registerCharcoalRecipes();
        registerFuelValues();
        GameRegistry.registerFuelHandler((IFuelHandler) SmeltingRecipes::getFuelBurnTime);
    }

    private static void registerCharcoalRecipes() {
        Set<String> registeredRecipes = new HashSet<>();
        for (ItemStack stack : OreDictionary.getOres("logWood")) {
            if (stack.isEmpty()) {
                continue;
            }
            Item item = stack.getItem();
            ResourceLocation registryName = item.getRegistryName();
            if (registryName == null || !"byg".equals(registryName.getNamespace())) {
                continue;
            }
            String path = registryName.getPath();
            if (!path.contains("log") && !path.contains("wood")) {
                continue;
            }
            String key = registryName + ":" + stack.getMetadata();
            if (!registeredRecipes.add(key)) {
                continue;
            }
            GameRegistry.addSmelting(stack.copy(), new ItemStack(Items.COAL, 1, 1), 0.15f);
        }
    }

    private static void registerFuelValues() {
        FUEL_VALUES.clear();
        registerOreDictionaryFuel("logWood", 300, "log", "wood");
        registerOreDictionaryFuel("plankWood", 300);
        registerOreDictionaryFuel("slabWood", 150);
        registerOreDictionaryFuel("stairWood", 300);
        registerOreDictionaryFuel("fenceWood", 300);
        registerOreDictionaryFuel("fenceGateWood", 300);
        registerOreDictionaryFuel("bookshelfWood", 300);
        registerOreDictionaryFuel("treeSapling", 100);
        registerDoorFuelValues();
        registerBlockFuel(ModBlocks.leaf_pile_dead, 100);
        registerBlockFuel(ModBlocks.dead_grass, 100);
        registerBlockFuel(ModBlocks.short_dead_grass, 100);
    }

    private static void registerBlockFuel(Block block, int burnTime) {
        if (block != null) {
            FUEL_VALUES.put(createStackKey(new ItemStack(block, 1, 0)), burnTime);
        }
    }

    private static void registerOreDictionaryFuel(String oreName, int burnTime, String... requiredPathParts) {
        for (ItemStack stack : OreDictionary.getOres(oreName)) {
            if (stack.isEmpty()) {
                continue;
            }
            Item item = stack.getItem();
            ResourceLocation registryName = item.getRegistryName();
            if (registryName == null || !"byg".equals(registryName.getNamespace())) {
                continue;
            }
            String path = registryName.getPath();
            if (requiredPathParts.length > 0) {
                boolean matches = false;
                for (String part : requiredPathParts) {
                    if (path.contains(part)) {
                        matches = true;
                        break;
                    }
                }
                if (!matches) {
                    continue;
                }
            }
            FUEL_VALUES.put(createStackKey(stack), burnTime);
        }
    }

    private static void registerDoorFuelValues() {
        for (Item item : Item.REGISTRY) {
            if (item == null || item.getRegistryName() == null || !"byg".equals(item.getRegistryName().getNamespace())) {
                continue;
            }
            String path = item.getRegistryName().getPath();
            if (path.endsWith("_door") || path.endsWith("_door") || path.endsWith("dooritem")) {
                FUEL_VALUES.put(createStackKey(new ItemStack(item, 1, 0)), 200);
            }
        }
    }

    private static int getFuelBurnTime(ItemStack fuel) {
        return FUEL_VALUES.getOrDefault(createStackKey(fuel), 0);
    }

    private static String createStackKey(ItemStack stack) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        return registryName + ":" + stack.getMetadata();
    }
}

