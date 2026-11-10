package windanesz.byg;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.regex.Pattern;

@net.minecraftforge.common.config.Config(modid = BiomesYouGo.MODID, name = "OhTheBiomesYoullGo")
public final class Config {
    private static final Map<String, Integer> BIOME_WEIGHTS = new HashMap<>();
    private static final Set<String> ENABLED_BIOMES = new HashSet<>();
    private static final Map<String, FoodValues> FOOD_VALUES = new HashMap<>();
    private static boolean baked;
    // Updated only when the config is baked; collision code reads this cached
    // primitive rather than consulting Forge's config data every tick.
    private static double mudMovementMultiplier = 0.925D;
    private static double quagmireSlimeChance = 0.004D;
    private static final Set<String> MUD_IMMUNE_ENTITIES = new HashSet<>();

    @net.minecraftforge.common.config.Config.Name("Biome Settings")
    @net.minecraftforge.common.config.Config.Comment({
            "Biome enable/disable and spawn weighting settings.",
            "Enabled biomes and weight overrides accept either 'registry_name' or 'modid:registry_name'."
    })
    public static BiomeSettings biomeSettings = new BiomeSettings();

    @net.minecraftforge.common.config.Config.Name("Food Settings")
    @net.minecraftforge.common.config.Config.Comment("Food level and saturation settings for BYG food items. Restart required after changes.")
    public static FoodSettings foodSettings = new FoodSettings();

    @net.minecraftforge.common.config.Config.Name("Item Settings")
    @net.minecraftforge.common.config.Config.Comment("Special BYG item behaviour settings.")
    public static ItemSettings itemSettings = new ItemSettings();

    @net.minecraftforge.common.config.Config.Name("Block Settings")
    @net.minecraftforge.common.config.Config.Comment("Harvest, hazard, and special block behaviour settings.")
    public static BlockSettings blockSettings = new BlockSettings();

    @net.minecraftforge.common.config.Config.Name("Content Settings")
    @net.minecraftforge.common.config.Config.Comment({
            "Enable or disable whole content sets: biome and wood sets, ore sets, and armour/tool equipment sets.",
            "Disabling a set also removes its blocks, items, recipes, generation and associated biome; disabling a biome alone (Biome Settings) leaves its content available.",
            "Change only before creating a world to avoid missing mappings. Restart required."
    })
    @net.minecraftforge.common.config.Config.RequiresMcRestart
    public static ContentSettings contentSettings = new ContentSettings();

    @net.minecraftforge.common.config.Config.Name("Equipment Settings")
    @net.minecraftforge.common.config.Config.Comment("Enable or disable BYG armour and weapon/tool sets. Equipment stat changes require a Minecraft restart.")
    @net.minecraftforge.common.config.Config.RequiresMcRestart
    public static EquipmentSettings equipmentSettings = new EquipmentSettings();

    @net.minecraftforge.common.config.Config.Name("Worldgen Settings")
    @net.minecraftforge.common.config.Config.Comment("World generation tuning for templates, flowers, ores, plants, and special features.")
    public static WorldgenSettings worldgenSettings = new WorldgenSettings();

    @net.minecraftforge.common.config.Config.Name("Creature Settings")
    @net.minecraftforge.common.config.Config.Comment("Behaviour tuning for BYG creatures.")
    public static CreatureSettings creatureSettings = new CreatureSettings();

    @net.minecraftforge.common.config.Config.Name("Client Settings")
    @net.minecraftforge.common.config.Config.Comment("Visual settings applied only on this client.")
    public static ClientSettings clientSettings = new ClientSettings();

    private Config() {
    }

    public static void init() {
        syncAndBake();
    }

    public static boolean isBiomeEnabled(String biomeName) {
        ensureBaked();
        String normalized = normalizeBiomeName(biomeName);
        return ENABLED_BIOMES.contains(normalized)
                && (!"byg_mangrove_marshes".equals(normalized) || contentSettings.mangroveContentEnabled)
                && (!"byg_baobab_savanna".equals(normalized) || contentSettings.baobabContentEnabled)
                && (!"byg_great_oak_lowlands".equals(normalized) || contentSettings.greatOakContentEnabled)
                && (!"byg_ebony_woods".equals(normalized) || contentSettings.ebonyContentEnabled)
                && (!"byg_cypress_swamplands".equals(normalized) || contentSettings.cypressContentEnabled)
                && (!"byg_cika_forest".equals(normalized) || contentSettings.cikaContentEnabled)
                && (!"byg_redwood_tropics".equals(normalized) || contentSettings.redwoodContentEnabled)
                && (!"byg_skyris_highlands".equals(normalized) || contentSettings.skyrisContentEnabled)
                && (!"byg_enchanted_forest".equals(normalized) || contentSettings.enchantedTreeContentEnabled)
                && (!"byg_cherry_grove".equals(normalized) || contentSettings.cherryContentEnabled)
                && (!"byg_jacaranda_forest".equals(normalized) || contentSettings.jacarandaContentEnabled)
                && (!"byg_maple_taiga".equals(normalized) || contentSettings.mapleContentEnabled)
                && (!"byg_zelkova_forest".equals(normalized) || contentSettings.zelkovaContentEnabled)
                && (!"byg_whispering_woods".equals(normalized)
                        || (contentSettings.rowanContentEnabled && contentSettings.hawthornContentEnabled))
                && (!"byg_weeping_witch_forest".equals(normalized) || contentSettings.witchHazelContentEnabled)
                && (!"byg_aspen_forest".equals(normalized) || contentSettings.aspenContentEnabled)
                && (!("byg_bayou".equals(normalized) || "byg_glowshroom_bayou".equals(normalized)) || contentSettings.willowContentEnabled)
                && (!"byg_glowshroom_bayou".equals(normalized) || contentSettings.glowcaneContentEnabled)
                && (!("byg_red_oak_forest".equals(normalized) || "byg_seasonal_forest".equals(normalized)
                        || "byg_seasonal_deciduous".equals(normalized) || "byg_stone_pillar_savanna".equals(normalized))
                        || contentSettings.oakVariantsContentEnabled)
                && (!"byg_orchard".equals(normalized) || contentSettings.orchardContentEnabled)
                && (!"byg_stellata_pasture".equals(normalized) || contentSettings.stellataContentEnabled)
                && (!"byg_seasonal_birch_forest".equals(normalized) || contentSettings.birchVariantsContentEnabled)
                && (!("byg_blue_taiga".equals(normalized) || "byg_giant_blue_spruce_taiga".equals(normalized)
                        || "byg_seasonal_taiga".equals(normalized) || "byg_giant_seasonal_spruce_taiga".equals(normalized))
                        || contentSettings.spruceVariantsContentEnabled)
                && (!"byg_dead_sea".equals(normalized) || contentSettings.blackSandContentEnabled)
                && (!("byg_mangrove_marshes".equals(normalized) || "byg_crystal_canyons".equals(normalized))
                        || contentSettings.coloredSandContentEnabled)
                && (!"byg_crystal_canyons".equals(normalized) || contentSettings.crystalContentEnabled)
                && (!("byg_pine_lowlands".equals(normalized) || "byg_pine_mountains".equals(normalized)
                        || "byg_snowy_pine_mountains".equals(normalized)) || contentSettings.pineContentEnabled)
                && (!("byg_coniferous_forest".equals(normalized) || "byg_snowy_coniferous_forest".equals(normalized))
                        || contentSettings.firContentEnabled);
    }

    /** Whether the wood set is enabled. */
    public static boolean isWoodSetEnabled(String set) {
        ensureBaked();
        switch (set) {
            case "cherry": return contentSettings.cherryContentEnabled;
            case "fir": return contentSettings.firContentEnabled;
            case "maple": return contentSettings.mapleContentEnabled;
            case "pine": return contentSettings.pineContentEnabled;
            case "zelkova": return contentSettings.zelkovaContentEnabled;
            case "mahogany": return contentSettings.mahoganyContentEnabled;
            case "jacaranda": return contentSettings.jacarandaContentEnabled;
            case "holly": return contentSettings.hollyContentEnabled;
            case "frozen_oak": return contentSettings.frozenOakContentEnabled;
            case "rowan": return contentSettings.rowanContentEnabled;
            case "hawthorn": return contentSettings.hawthornContentEnabled;
            case "witch_hazel": return contentSettings.witchHazelContentEnabled;
            case "aspen": return contentSettings.aspenContentEnabled;
            case "willow": return contentSettings.willowContentEnabled;
            case "ironwood": return contentSettings.ironwoodContentEnabled;
            case "rainbow_eucalyptus": return contentSettings.rainbowEucalyptusContentEnabled;
            case "glowcane": return contentSettings.glowcaneContentEnabled;
            case "oak_variants": return contentSettings.oakVariantsContentEnabled;
            case "palo_verde": return contentSettings.paloVerdeContentEnabled;
            case "orchard": return contentSettings.orchardContentEnabled;
            case "stellata": return contentSettings.stellataContentEnabled;
            case "birch_variants": return contentSettings.birchVariantsContentEnabled;
            case "spruce_variants": return contentSettings.spruceVariantsContentEnabled;
            case "sepinite": return contentSettings.sepiniteContentEnabled;
            case "soapstone": return contentSettings.soapstoneContentEnabled;
            case "sodalite": return contentSettings.sodaliteContentEnabled;
            case "scoria": return contentSettings.scoriaContentEnabled;
            case "glowshroom": return contentSettings.glowshroomContentEnabled;
            case "strawberry": return contentSettings.strawberryContentEnabled;
            case "blueberry": return contentSettings.blueberryContentEnabled;
            case "rudo": return contentSettings.rudoContentEnabled;
            case "cattail": return contentSettings.cattailContentEnabled;
            case "salal": return contentSettings.salalContentEnabled;
            case "nether_furnace": return contentSettings.netherFurnaceContentEnabled;
            case "flowers": return contentSettings.flowersContentEnabled;
            case "black_sand": return contentSettings.blackSandContentEnabled;
            case "cacti": return contentSettings.cactiContentEnabled;
            case "mushroom_decor": return contentSettings.mushroomDecorContentEnabled;
            case "ground_cover": return contentSettings.groundCoverContentEnabled;
            case "food": return contentSettings.foodContentEnabled;
            case "fungal_zombie": return contentSettings.fungalZombieContentEnabled;
            case "fungal_skeleton": return contentSettings.fungalSkeletonContentEnabled;
            case "kiwi_bird": return contentSettings.kiwiBirdContentEnabled;
            case "crystal_crawler": return contentSettings.crystalCrawlerContentEnabled;
            case "structures": return contentSettings.structuresContentEnabled;
            case "colored_sand": return contentSettings.coloredSandContentEnabled;
            case "crystal": return contentSettings.crystalContentEnabled;
            default: return true;
        }
    }

    /**
     * Whether a block or item with this registry path should exist. Blocks and items of a switchable wood set
     * (including stripped variants and the differently named cherry and maple saplings) are dropped when the set is off.
     */
    public static boolean isContentRegistered(String registryPath) {
        if (registryPath.startsWith("stripped_") && !isStrippedLogsEnabled()) {
            return false;
        }
        String set = woodSetOf(registryPath);
        return set == null || isWoodSetEnabled(set);
    }

    /** Whether a structure template is generated: templates named after a switchable wood set follow that set's toggle. */
    public static boolean isTemplateEnabled(String templatePath) {
        if (templatePath.startsWith("canyon_crystal") && !isCrystalContentEnabled()) {
            return false;
        }
        for (String set : WOOD_SETS) {
            if (templatePath.startsWith(set) && !isWoodSetEnabled(set)) {
                return false;
            }
        }
        for (Map.Entry<String, String[]> entry : TEMPLATE_PREFIXES.entrySet()) {
            if (!isWoodSetEnabled(entry.getKey())) {
                for (String prefix : entry.getValue()) {
                    if (templatePath.startsWith(prefix)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Templates that use a switchable set's blocks without being named after it. */
    private static final Map<String, String[]> TEMPLATE_PREFIXES = new HashMap<>();
    static {
        TEMPLATE_PREFIXES.put("aspen", new String[]{"mahogany_sapling2"});
        TEMPLATE_PREFIXES.put("willow", new String[]{"bayoutree", "bayou_village", "bayou_witch_hut"});
        TEMPLATE_PREFIXES.put("ironwood", new String[]{"sapling_ironwood", "canyon_top1", "canyon_color_top1"});
        TEMPLATE_PREFIXES.put("rainbow_eucalyptus", new String[]{"rainbow_"});
        TEMPLATE_PREFIXES.put("glowcane", new String[]{"bayou_ground_glow", "bayoutreeglow", "enchanted_village"});
        TEMPLATE_PREFIXES.put("cacti", new String[]{"cact", "plant_mini_cactus"});
        TEMPLATE_PREFIXES.put("ground_cover", new String[]{"dead_grass", "deadbush", "deadleaf", "lilypad", "plant_clover", "leaf", "stone_spike"});
        TEMPLATE_PREFIXES.put("structures", new String[]{"bayou_village", "deciduous_village", "enchanted_village", "orchard_village", "quag_village", "red_village", "salem_village", "lake_village", "farm_house", "great_oak_house", "pine_house", "pine_campsite", "ancient_outpost", "bayou_witch_hut", "weepingwitchhut", "cypress_tower", "ds_guardiancove", "ds_maraudersport", "mob_snowman", "mob_wolfpack"});
        TEMPLATE_PREFIXES.put("palo_verde", new String[]{"palo_verde", "sapling_palo_verde"});
        TEMPLATE_PREFIXES.put("orchard", new String[]{"orch_tree", "sapling_orchard", "orchard_village"});
        TEMPLATE_PREFIXES.put("birch_variants", new String[]{"brown_birch_", "orange_birch_", "red_birch_", "sapling_birch_yellow",
                "seasonbirch", "dsbush", "borealtree4", "borealtree5", "borealtree6", "sf_tree3", "sf_tree7"});
        TEMPLATE_PREFIXES.put("spruce_variants", new String[]{"bluespruce", "orangespruce", "redspruce", "yellowspruce",
                "tbluespruce", "torangespruce", "tredspruce", "tyellowspruce"});
        TEMPLATE_PREFIXES.put("cattail", new String[]{"cattail"});
        TEMPLATE_PREFIXES.put("glowshroom", new String[]{"bayoutreeglow"});
        TEMPLATE_PREFIXES.put("oak_variants", new String[]{"dry_brown_oak_", "dry_green_oak_", "dstree", "deciduous_village_seasonal",
                "redoak_tree", "sapling_oak_", "sf_"});
    }

    /** Whether stripped log and stripped wood blocks are registered at all, regardless of wood set. */
    public static boolean isStrippedLogsEnabled() {
        ensureBaked();
        return contentSettings.strippedLogsEnabled;
    }

    public static boolean isCrystalContentEnabled() {
        ensureBaked();
        return contentSettings.crystalContentEnabled;
    }

    private static final String[] WOOD_SETS = {"cherry", "fir", "maple", "pine", "zelkova", "mahogany", "jacaranda", "holly", "frozen_oak", "rowan", "hawthorn", "witch_hazel", "aspen", "willow", "ironwood", "rainbow_eucalyptus"};

    /** The wood set a registry path belongs to, or null for content that is not part of a switchable set. */
    public static String woodSetOf(String registryPath) {
        String name = registryPath.startsWith("stripped_") ? registryPath.substring("stripped_".length()) : registryPath;
        // Sap, syrup and pancakes come from tapped maples, so they follow the maple set.
        if (name.equals("maple_sap") || name.equals("maple_syrup") || name.equals("maple_pancakes")) {
            return "maple";
        }
        if (name.startsWith("glowcane_") || name.equals("glowcelium")) {
            return "glowcane";
        }
        if (name.equals("eucalyptus_door")) {
            return "rainbow_eucalyptus";
        }
        if (name.startsWith("orange_oak_") || name.startsWith("red_oak_") || name.startsWith("dry_brown_oak_")
                || name.startsWith("dry_green_oak_") || name.startsWith("oak_leaves_")) {
            return "oak_variants";
        }
        if (name.endsWith("_crystals") || name.endsWith("_crystal_block")) {
            return "crystal";
        }
        if (name.equals("pink_cherry_sapling") || name.equals("white_cherry_sapling")) {
            return "cherry";
        }
        if (name.equals("red_maple_sapling") || name.equals("silver_maple_sapling")
                || name.equals("sappy_maple_log") || name.equals("maple_tap")) {
            return "maple";
        }
        for (String set : WOOD_SETS) {
            if (name.startsWith(set + "_")) {
                return set;
            }
        }
        for (Map.Entry<String, Pattern> entry : NAME_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(name).find()) {
                return entry.getKey();
            }
        }
        return FLOWER_NAMES.contains(name) ? "flowers" : null;
    }

    /** Content sets identified by registry-name pattern rather than by a shared wood-set prefix. */
    private static final Map<String, Pattern> NAME_PATTERNS = new LinkedHashMap<>();
    private static final Set<String> FLOWER_NAMES = new HashSet<>(Arrays.asList(
            "allium_bush", "alpine_bellflower", "amaranth", "angelica", "azalea", "begonia",
            "bistort", "black_rose", "bluesage", "california_poppy", "crocus", "cyan_amaranth",
            "cyan_rose", "cyan_tulip", "daffodil", "delphinium", "fairy_slipper", "firecracker",
            "foxglove", "green_tulip", "guzmania", "horseweed", "incan_lily", "iris",
            "japanese_orchid", "kovan", "lazarus_bell_flower", "lollipop_flower", "magenta_amaranth", "magenta_celosia",
            "magenta_tulip", "orange_amaranth", "orange_celosia", "orange_daisy", "osiria_rose", "peach_leather_flower",
            "pink_allium", "pink_allium_bush", "pink_anemone", "pink_daffodil", "pink_orchid", "protea_flower",
            "purple_amaranth", "purple_celosia", "purple_orchid", "purple_sage", "purple_tulip", "red_celosia",
            "red_corn_flower", "red_orchid", "richea", "rose", "sacred_datura", "silver_vase_flower",
            "snowdrops", "torch_ginger", "violet_leather_flower", "white_anemone", "white_celosia", "white_sage",
            "winter_cyclamen", "winter_rose", "winter_scilla", "winter_succulent", "yellow_celosia", "yellow_daffodil",
            "yellow_tulip", "blue_petal", "light_blue_petal", "purple_petal", "red_petal", "white_petal",
            "yellow_petal", "flowers"));
    static {
        NAME_PATTERNS.put("cacti", Pattern.compile("^(golden_spined_cactus|mini_cactus|prickly_pear|sonoran_cactus(_flowering)?)$"));
        NAME_PATTERNS.put("mushroom_decor", Pattern.compile("^(black_puff|shelf_fungi|weeping_milk_cap|wood_blewit)$"));
        NAME_PATTERNS.put("ground_cover", Pattern.compile("^(algae|blanket_weed|ivy|poison_ivy|tiny_lilypad|stone_pebbles|stone_spike|thorn_block|thorn_branches|clover|dead_grass|short_dead_grass|leafpile|leaf_pile_dead)$"));
        NAME_PATTERNS.put("food", Pattern.compile("^(carrot_soup|cooked_carrot|cooked_pufferfish|cooked_tropical_fish|cooked_spider_eye|cooked_pumpkin_seeds|tropical_fish_soup|spider_eye_soup|berry_juice|golden_beetroot|pumpkin_bread|pumpkin_mash|carved_melon|jackomelon)$"));
        NAME_PATTERNS.put("colored_sand", Pattern.compile("^(light_blue|pink|purple|white)_(sand|sandstone|chiseled_sandstone|smooth_sandstone)$"));
        NAME_PATTERNS.put("black_sand", Pattern.compile("^black_(sand|sandstone|chiseled_sandstone|smooth_sandstone)$"));
        NAME_PATTERNS.put("palo_verde", Pattern.compile("^palo_verde_"));
        NAME_PATTERNS.put("orchard", Pattern.compile("^orchard_"));
        NAME_PATTERNS.put("stellata", Pattern.compile("stellata"));
        NAME_PATTERNS.put("birch_variants", Pattern.compile("^(brown|orange|red|yellow)_birch_|^birch_leaves_"));
        NAME_PATTERNS.put("spruce_variants", Pattern.compile("^(blue|orange|red|yellow)_spruce_|^spruce_leaves_"));
        NAME_PATTERNS.put("sepinite", Pattern.compile("^sepinite"));
        NAME_PATTERNS.put("soapstone", Pattern.compile("soapstone"));
        NAME_PATTERNS.put("sodalite", Pattern.compile("sodalite"));
        NAME_PATTERNS.put("scoria", Pattern.compile("^scoria"));
        NAME_PATTERNS.put("glowshroom", Pattern.compile("glowshroom"));
        NAME_PATTERNS.put("strawberry", Pattern.compile("strawberry"));
        NAME_PATTERNS.put("blueberry", Pattern.compile("blueberry"));
        NAME_PATTERNS.put("rudo", Pattern.compile("(^|_)rudo"));
        NAME_PATTERNS.put("cattail", Pattern.compile("^cattail|^cooked_cattail"));
        NAME_PATTERNS.put("salal", Pattern.compile("salal"));
        NAME_PATTERNS.put("kiwi_bird", Pattern.compile("^kiwi_"));
        NAME_PATTERNS.put("nether_furnace", Pattern.compile("^nether_furnace"));
    }

    public static int getBiomeWeight(String biomeName, int defaultWeight) {
        ensureBaked();
        int configuredWeight = BIOME_WEIGHTS.containsKey(normalizeBiomeName(biomeName))
                ? BIOME_WEIGHTS.get(normalizeBiomeName(biomeName))
                : defaultWeight;
        configuredWeight = (int) Math.round(configuredWeight * biomeSettings.globalBiomeWeightMultiplier);
        return Math.max(0, configuredWeight);
    }

    public static boolean shouldAddBiomesToSpawnList() {
        ensureBaked();
        return biomeSettings.addBiomesToSpawnList;
    }

    public static boolean allowDaytimeWitchesInWeepingWitchForest() {
        ensureBaked();
        return creatureSettings.daytimeWitchesInWeepingWitchForest;
    }

    public static boolean isEquipmentSetEnabled(String setName) {
        ensureBaked();
        if ("kasai".equals(setName)) {
            return contentSettings.kasaiContentEnabled && contentSettings.kasaiEquipmentEnabled;
        }
        if ("latharium".equals(setName)) {
            return contentSettings.lathariumContentEnabled && contentSettings.lathariumEquipmentEnabled;
        }
        if ("pendorite".equals(setName)) {
            return contentSettings.pendoriteContentEnabled && contentSettings.pendoriteEquipmentEnabled;
        }
        if ("tamrelite".equals(setName)) {
            return contentSettings.tamreliteContentEnabled && contentSettings.tamreliteEquipmentEnabled;
        }
        return true;
    }

    public static FoodValues getFoodValues(String itemName, int defaultFoodLevel, float defaultSaturation) {
        ensureBaked();
        FoodValues values = FOOD_VALUES.get(itemName);
        return values != null ? values : new FoodValues(defaultFoodLevel, defaultSaturation);
    }

    public static int getFoodLevel(String itemName, int defaultFoodLevel) {
        return getFoodValues(itemName, defaultFoodLevel, 0.3f).foodLevel;
    }

    public static float getFoodSaturation(String itemName, float defaultSaturation) {
        return getFoodValues(itemName, 1, defaultSaturation).saturation;
    }

    public static boolean isBaobabfruitAlwaysEdible() {
        ensureBaked();
        return itemSettings.baobabfruitAlwaysEdible;
    }

    public static boolean doesBaobabfruitClearEffects() {
        ensureBaked();
        return itemSettings.baobabfruitClearPotionEffects;
    }

    public static boolean isBaobabfruitPlantingEnabled() {
        ensureBaked();
        return itemSettings.baobabfruitPlantingEnabled;
    }

    public static boolean doesBaobabfruitPlantingConsumeItem() {
        ensureBaked();
        return itemSettings.baobabfruitPlantingConsumesItem;
    }

    public static boolean isClarityPotionBrewingEnabled() {
        ensureBaked();
        return itemSettings.clarityPotionBrewingEnabled;
    }

    public static boolean isBlueberryPlantingEnabled() {
        ensureBaked();
        return itemSettings.blueberryPlantingEnabled;
    }

    public static boolean doesBlueberryPlantingConsumeItem() {
        ensureBaked();
        return itemSettings.blueberryPlantingConsumesItem;
    }

    public static float getBerryJuiceHealAmount() {
        ensureBaked();
        return (float) Math.max(0.0D, itemSettings.berryJuiceHealAmount);
    }

    public static int getBerryJuiceRegenerationDuration() {
        ensureBaked();
        return Math.max(0, itemSettings.berryJuiceRegenerationDuration);
    }

    public static float getMudBallDamage() {
        ensureBaked();
        return (float) Math.max(0.0D, itemSettings.mudBallDamage);
    }

    public static boolean isWormFishingEnhancementEnabled() {
        ensureBaked();
        return itemSettings.wormFishingEnhancementEnabled;
    }

    public static double getWormFishingJunkRerollChance() {
        ensureBaked();
        return Math.max(0.0D, Math.min(1.0D, itemSettings.wormFishingJunkRerollChance));
    }

    public static double getWormFishingQualityBoostChance() {
        ensureBaked();
        return Math.max(0.0D, Math.min(1.0D, itemSettings.wormFishingQualityBoostChance));
    }

    public static double getWormFishingExtraFishChance() {
        ensureBaked();
        return Math.max(0.0D, Math.min(1.0D, itemSettings.wormFishingExtraFishChance));
    }

    public static boolean areBiomeTeleporterItemsEnabled() {
        ensureBaked();
        return itemSettings.biomeTeleporterItemsEnabled;
    }

    public static int getGoldenBeetrootRegenDuration() {
        ensureBaked();
        return Math.max(0, itemSettings.goldenBeetrootRegenDuration);
    }

    public static boolean isGoldenBeetrootPetRegenEnabled() {
        ensureBaked();
        return itemSettings.goldenBeetrootPetRegenEnabled;
    }

    public static int getGoldenBeetrootPetRegenInterval() {
        ensureBaked();
        return Math.max(1, itemSettings.goldenBeetrootPetRegenInterval);
    }

    public static float getGoldenBeetrootPetRegenAmount() {
        ensureBaked();
        return (float) Math.max(0.0D, itemSettings.goldenBeetrootPetRegenAmount);
    }

    public static int getWormLureLevel() {
        ensureBaked();
        return Math.max(0, itemSettings.wormLureLevel);
    }

    public static boolean isLathariumBootsLevitationEnabled() {
        ensureBaked();
        return equipmentSettings.lathariumBootsLevitationEnabled;
    }

    public static int getLathariumBootsLevitationDuration() {
        ensureBaked();
        return Math.max(0, equipmentSettings.lathariumBootsLevitationDuration);
    }

    public static int getLathariumBootsLevitationAmplifier() {
        ensureBaked();
        return Math.max(0, equipmentSettings.lathariumBootsLevitationAmplifier);
    }

    public static double getLathariumBootsParticleChance() {
        ensureBaked();
        return Math.max(0.0D, equipmentSettings.lathariumBootsParticleChance);
    }

    public static int getLathariumBootsParticleCount() {
        ensureBaked();
        return Math.max(0, equipmentSettings.lathariumBootsParticleCount);
    }

    public static boolean isKasaiFireWardEnabled() {
        ensureBaked();
        return equipmentSettings.kasaiFireWardEnabled;
    }

    public static int getKasaiFireWardDuration() {
        ensureBaked();
        return Math.max(1, equipmentSettings.kasaiFireWardDuration);
    }

    public static int getKasaiFireWardCooldown() {
        ensureBaked();
        return Math.max(0, equipmentSettings.kasaiFireWardCooldown);
    }

    public static ArmorMaterialStats getArmorMaterialStats(String setName, int defaultDurabilityMultiplier,
                                                           int defaultBootsProtection, int defaultLeggingsProtection,
                                                           int defaultChestplateProtection, int defaultHelmetProtection,
                                                           int defaultEnchantability, float defaultToughness) {
        ensureBaked();
        ArmorMaterialSettings settings = getArmorMaterialSettings(setName);
        if (settings == null) {
            return new ArmorMaterialStats(defaultDurabilityMultiplier, defaultBootsProtection, defaultLeggingsProtection,
                    defaultChestplateProtection, defaultHelmetProtection, defaultEnchantability, defaultToughness);
        }
        return new ArmorMaterialStats(
                Math.max(0, settings.durabilityMultiplier),
                Math.max(0, settings.bootsProtection),
                Math.max(0, settings.leggingsProtection),
                Math.max(0, settings.chestplateProtection),
                Math.max(0, settings.helmetProtection),
                Math.max(0, settings.enchantability),
                (float) Math.max(0.0D, settings.toughness)
        );
    }

    public static int getBlueberryBushHarvestYield() {
        ensureBaked();
        return Math.max(0, blockSettings.blueberryBushBerryCount);
    }

    public static int getStrawberryBushHarvestYield() {
        ensureBaked();
        return Math.max(0, blockSettings.strawberryBushBerryCount);
    }

    public static int getBaobabFruitHarvestCount() {
        ensureBaked();
        return Math.max(0, blockSettings.baobabFruitHarvestCount);
    }

    public static int getBaobabFruitBreakDropCount() {
        ensureBaked();
        return Math.max(0, blockSettings.baobabFruitBreakDropCount);
    }

    public static double getPlantStageGrowthChance(String blockName, double defaultChance) {
        ensureBaked();
        if ("blueberry_bush".equals(blockName)) {
            return Math.max(0.0D, blockSettings.blueberryBushStageGrowthChance);
        }
        if ("strawberry_bush".equals(blockName)) {
            return Math.max(0.0D, blockSettings.strawberryBushStageGrowthChance);
        }
        if ("baobab_fruit_block".equals(blockName)) {
            return Math.max(0.0D, blockSettings.baobabFruitStageGrowthChance);
        }
        return Math.max(0.0D, defaultChance);
    }

    public static double getPlantStageBonemealChance(String blockName, double defaultChance) {
        ensureBaked();
        if ("blueberry_bush".equals(blockName)) {
            return Math.max(0.0D, blockSettings.blueberryBushBonemealGrowthChance);
        }
        if ("strawberry_bush".equals(blockName)) {
            return Math.max(0.0D, blockSettings.strawberryBushBonemealGrowthChance);
        }
        if ("baobab_fruit_block".equals(blockName)) {
            return Math.max(0.0D, blockSettings.baobabFruitBonemealGrowthChance);
        }
        return Math.max(0.0D, defaultChance);
    }

    public static int getBaobabFruitStageTickRate() {
        ensureBaked();
        return Math.max(1, blockSettings.baobabFruitStageTickRate);
    }

    public static int getMapleTapStageTickRate() {
        ensureBaked();
        return Math.max(1, blockSettings.mapleTapStageTickRate);
    }

    public static double getMapleTapLogDepletionChance() {
        ensureBaked();
        return Math.min(1.0D, Math.max(0.0D, blockSettings.mapleTapLogDepletionChance));
    }

    public static int getMapleSyrupSips() {
        ensureBaked();
        return Math.max(1, itemSettings.mapleSyrupSips);
    }

    public static int getMapleSyrupSpeedDuration() {
        ensureBaked();
        return Math.max(0, itemSettings.mapleSyrupSpeedDuration);
    }

    public static float getThornblockDamage() {
        ensureBaked();
        return (float) Math.max(0.0D, blockSettings.thornblockDamage);
    }

    public static float getThornBranchesDamage() {
        ensureBaked();
        return (float) Math.max(0.0D, blockSettings.thornBranchesDamage);
    }

    public static float getCactusDamage() {
        ensureBaked();
        return (float) Math.max(0.0D, blockSettings.cactusDamage);
    }

    public static float getDamagingPlantDamage() {
        ensureBaked();
        return (float) Math.max(0.0D, blockSettings.damagingPlantDamage);
    }

    public static double getMudMovementMultiplier() {
        return mudMovementMultiplier;
    }

    /** Whether the entity with this registry ID (e.g. "wilderlands:battlefield_ghost") ignores mud slowdown. */
    public static boolean isMudSlownessImmune(ResourceLocation entityId) {
        return entityId != null && MUD_IMMUNE_ENTITIES.contains(entityId.toString());
    }

    public static double getQuagmireSlimeChance() {
        return quagmireSlimeChance;
    }

    public static int getKiwiForageAttemptInterval() {
        ensureBaked();
        return Math.max(1, creatureSettings.kiwiForageAttemptInterval);
    }

    public static double getKiwiWormFindChance() {
        ensureBaked();
        return Math.max(0.0D, Math.min(1.0D, creatureSettings.kiwiWormFindChance));
    }

    public static int getKiwiEggLayInterval() {
        ensureBaked();
        return Math.max(1, creatureSettings.kiwiEggLayInterval);
    }

    public static int getKiwiMeatDropCount() {
        ensureBaked();
        return Math.max(0, creatureSettings.kiwiMeatDropCount);
    }

    public static boolean isOreContentEnabled(String oreName) {
        ensureBaked();
        switch (oreName) {
            case "kasai": return contentSettings.kasaiContentEnabled;
            case "latharium": return contentSettings.lathariumContentEnabled;
            case "pendorite": return contentSettings.pendoriteContentEnabled;
            case "tamrelite": return contentSettings.tamreliteContentEnabled;
            default: throw new IllegalArgumentException("Unknown ore content set: " + oreName);
        }
    }

    public static String[] getKiwiSpawnBiomes() {
        ensureBaked();
        return creatureSettings.kiwiSpawnBiomes.clone();
    }

    public static int getKiwiSpawnWeight() {
        ensureBaked();
        return Math.max(0, creatureSettings.kiwiSpawnWeight);
    }

    public static int getFungalZombieSpawnWeight() {
        ensureBaked();
        return Math.max(0, creatureSettings.fungalZombieSpawnWeight);
    }

    public static int getFungalSkeletonSpawnWeight() {
        ensureBaked();
        return Math.max(0, creatureSettings.fungalSkeletonSpawnWeight);
    }

    public static int getFungalZombiePoisonAmplifier() {
        ensureBaked();
        // the amplifier is stored as a signed byte when an effect is saved
        return Math.max(0, Math.min(127, creatureSettings.fungalZombiePoisonAmplifier));
    }

    public static int getFungalZombiePoisonDuration() {
        ensureBaked();
        return Math.max(0, creatureSettings.fungalZombiePoisonDuration);
    }

    public static boolean isMangroveContentEnabled() {
        ensureBaked();
        return contentSettings.mangroveContentEnabled;
    }

    public static boolean isBaobabContentEnabled() {
        ensureBaked();
        return contentSettings.baobabContentEnabled;
    }

    public static boolean isGreatOakContentEnabled() {
        ensureBaked();
        return contentSettings.greatOakContentEnabled;
    }

    public static boolean isEbonyContentEnabled() {
        ensureBaked();
        return contentSettings.ebonyContentEnabled;
    }

    public static boolean isCypressContentEnabled() {
        ensureBaked();
        return contentSettings.cypressContentEnabled;
    }

    public static boolean isCikaContentEnabled() {
        ensureBaked();
        return contentSettings.cikaContentEnabled;
    }

    public static boolean isRedwoodContentEnabled() {
        ensureBaked();
        return contentSettings.redwoodContentEnabled;
    }

    public static boolean isSkyrisContentEnabled() {
        ensureBaked();
        return contentSettings.skyrisContentEnabled;
    }

    public static boolean isPalmContentEnabled() {
        ensureBaked();
        return contentSettings.palmContentEnabled;
    }

    public static boolean isEnchantedTreeContentEnabled() {
        ensureBaked();
        return contentSettings.enchantedTreeContentEnabled;
    }

    public static double getSpringwaterBubbleChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.springwaterBubbleChance);
    }

    public static int getSpringwaterBubbleParticleCount() {
        ensureBaked();
        return Math.max(0, blockSettings.springwaterBubbleParticleCount);
    }

    public static boolean doesSpringwaterApplyRegeneration() {
        ensureBaked();
        return blockSettings.springwaterAppliesRegeneration;
    }

    public static boolean doesNetherFurnaceIgniteFromFireBelow() {
        ensureBaked();
        return blockSettings.netherFurnaceIgnitesFromFireBelow;
    }

    public static int getNetherFurnaceTickRate() {
        ensureBaked();
        return Math.max(1, blockSettings.netherFurnaceTickRate);
    }

    public static int getNetherFurnaceLitTickRate() {
        ensureBaked();
        return Math.max(1, blockSettings.netherFurnaceLitTickRate);
    }

    public static double getNetherFurnaceAmbientEffectChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.netherFurnaceAmbientEffectChance);
    }

    public static int getNetherFurnaceAmbientParticleCount() {
        ensureBaked();
        return Math.max(0, blockSettings.netherFurnaceAmbientParticleCount);
    }

    public static boolean doesPoisonIvyApplyPoison() {
        ensureBaked();
        return blockSettings.poisonIvyAppliesPoison;
    }

    public static int getPoisonIvyPoisonDuration() {
        ensureBaked();
        return Math.max(0, blockSettings.poisonIvyPoisonDuration);
    }

    public static int getPoisonIvyPoisonAmplifier() {
        ensureBaked();
        return Math.max(0, blockSettings.poisonIvyPoisonAmplifier);
    }

    public static int getStructureSpawnBlockTickRate() {
        ensureBaked();
        return Math.max(1, blockSettings.structureSpawnBlockTickRate);
    }

    public static double getColoredCanyonMiddleTemplateChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.coloredCanyonMiddleTemplateChance);
    }

    public static double getColoredCanyonPrimaryTopTemplateChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.coloredCanyonPrimaryTopTemplateChance);
    }

    public static double getCrystalCanyonBlueTemplateChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.crystalCanyonBlueTemplateChance);
    }

    public static double getCrystalCanyonPurpleTemplateChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.crystalCanyonPurpleTemplateChance);
    }

    public static double getCrystalCanyonRedTemplateChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.crystalCanyonRedTemplateChance);
    }

    public static double getCrystalCanyonWhiteTemplateChance() {
        ensureBaked();
        return Math.max(0.0D, blockSettings.crystalCanyonWhiteTemplateChance);
    }

    public static boolean isFeatureDimension(int dimensionId) {
        ensureBaked();
        for (int featureDimension : worldgenSettings.featureDimensions) {
            if (featureDimension == dimensionId) {
                return true;
            }
        }
        return false;
    }

    public static int scaleTemplateChance(int chancePerMillion) {
        ensureBaked();
        return Math.max(0, (int) Math.round(chancePerMillion * worldgenSettings.templateChanceMultiplier));
    }

    public static int scaleFlowerAttempts(int attempts) {
        ensureBaked();
        return Math.max(0, (int) Math.round(attempts * worldgenSettings.flowerAttemptMultiplier));
    }

    public static int scaleClusterPlantAttempts(int attempts) {
        ensureBaked();
        return Math.max(0, (int) Math.round(attempts * worldgenSettings.clusterPlantAttemptMultiplier));
    }

    public static int scaleSandAttempts(int attempts) {
        ensureBaked();
        return Math.max(0, (int) Math.round(attempts * worldgenSettings.sandAttemptMultiplier));
    }

    public static int scaleDepositAttempts(double attempts) {
        ensureBaked();
        return Math.max(0, (int) Math.round(attempts * worldgenSettings.undergroundDepositAttemptMultiplier));
    }

    public static double scaleOreAttempts(String oreName, int attempts) {
        ensureBaked();
        double multiplier;
        switch (oreName) {
            case "kasai": multiplier = worldgenSettings.kasaiOreAttemptMultiplier; break;
            case "latharium": multiplier = worldgenSettings.lathariumOreAttemptMultiplier; break;
            case "pendorite": multiplier = worldgenSettings.pendoriteOreAttemptMultiplier; break;
            case "tamrelite": multiplier = worldgenSettings.tamreliteOreAttemptMultiplier; break;
            default: throw new IllegalArgumentException("Unknown ore: " + oreName);
        }
        return attempts * multiplier;
    }

    public static double scoriaAttemptMultiplier() {
        ensureBaked();
        return worldgenSettings.scoriaAttemptMultiplier;
    }

    public static int scoriaMinY() {
        ensureBaked();
        return worldgenSettings.scoriaMinY;
    }

    public static int scoriaYRange() {
        ensureBaked();
        return Math.max(1, worldgenSettings.scoriaMaxY - worldgenSettings.scoriaMinY + 1);
    }

    public static String[] getScoriaBiomes() {
        ensureBaked();
        return worldgenSettings.scoriaBiomes.clone();
    }

    public static String[] getSoapstoneBiomes() {
        ensureBaked();
        return worldgenSettings.soapstoneBiomes.clone();
    }

    public static double sepiniteAttemptMultiplier() {
        ensureBaked();
        return worldgenSettings.sepiniteAttemptMultiplier;
    }

    public static int sepiniteMinY() {
        ensureBaked();
        return worldgenSettings.sepiniteMinY;
    }

    public static int sepiniteYRange() {
        ensureBaked();
        return Math.max(1, worldgenSettings.sepiniteMaxY - worldgenSettings.sepiniteMinY + 1);
    }

    public static int scaleDepositVeinSize(int veinSize) {
        ensureBaked();
        return Math.max(1, (int) Math.round(veinSize * worldgenSettings.undergroundDepositVeinSizeMultiplier));
    }

    public static boolean generateEnchantedVillage() {
        ensureBaked();
        return worldgenSettings.generateEnchantedVillage;
    }


    public static boolean generatePaloTrees() {
        ensureBaked();
        return worldgenSettings.generatePaloTrees;
    }

    public static boolean avoidCqrDungeons() {
        ensureBaked();
        return worldgenSettings.avoidCqrDungeons;
    }

    public static int cqrAvoidanceRadiusChunks() {
        ensureBaked();
        return worldgenSettings.cqrAvoidanceRadiusChunks;
    }

    public static int leafDecayRange() {
        ensureBaked();
        return blockSettings.leafDecayRange;
    }

    public static double saplingBonemealChanceMultiplier() {
        ensureBaked();
        return blockSettings.saplingBonemealChanceMultiplier;
    }


    public static boolean generateRockySurfaceFeatures() {
        ensureBaked();
        return worldgenSettings.generateRockySurfaceFeatures;
    }

    public static boolean isWorldgenFeatureEnabled(String featureName) {
        ensureBaked();
        if ("black_sand".equals(featureName) && !isWoodSetEnabled("black_sand")) {
            return false;
        }
        if (("white_sand".equals(featureName) || "light_blue_sand".equals(featureName)
                || "pink_sand".equals(featureName) || "purple_sand".equals(featureName)) && !isWoodSetEnabled("colored_sand")) {
            return false;
        }
        if (("golden_spined_cactus".equals(featureName) || "minicactus".equals(featureName) || "prickly_pear".equals(featureName))
                && !isWoodSetEnabled("cacti")) {
            return false;
        }
        if (("algae".equals(featureName) || "dead_grass".equals(featureName) || "short_dead_grass".equals(featureName)
                || "stone_pebbles".equals(featureName) || "stone_spike".equals(featureName))
                && !isWoodSetEnabled("ground_cover")) {
            return false;
        }
        if (("sepinite".equals(featureName) || "soapstone".equals(featureName) || "sodalite".equals(featureName)
                || "scoria".equals(featureName)) && !isWoodSetEnabled(featureName)) {
            return false;
        }
        if ("dead_grass".equals(featureName)) {
            return worldgenSettings.generateDeadGrassClusters;
        }
        if ("short_dead_grass".equals(featureName)) {
            return worldgenSettings.generateShortDeadGrassClusters;
        }
        if ("algae".equals(featureName)) {
            return worldgenSettings.generateAlgaePatches;
        }
        if ("stone_pebbles".equals(featureName)) {
            return worldgenSettings.generateStonePebbles;
        }
        if ("stone_spike".equals(featureName)) {
            return worldgenSettings.generateStoneSpikes;
        }
        if ("glowcane_blue".equals(featureName)) {
            return worldgenSettings.generateGlowcaneBlue;
        }
        if ("glowcane_pink".equals(featureName)) {
            return worldgenSettings.generateGlowcanePink;
        }
        if ("glowcane_purple".equals(featureName)) {
            return worldgenSettings.generateGlowcanePurple;
        }
        if ("glowcane_red".equals(featureName)) {
            return worldgenSettings.generateGlowcaneRed;
        }
        if ("golden_spined_cactus".equals(featureName)) {
            return worldgenSettings.generateGoldenSpinedCactus;
        }
        if ("mini_cactus".equals(featureName)) {
            return worldgenSettings.generateMinicactus;
        }
        if ("prickly_pear".equals(featureName)) {
            return worldgenSettings.generatePricklyPear;
        }
        if ("kasai_ore".equals(featureName)) {
            return contentSettings.kasaiContentEnabled && worldgenSettings.generateKasaiOre;
        }
        if ("latharium_ore".equals(featureName)) {
            return contentSettings.lathariumContentEnabled && worldgenSettings.generateLathariumOre;
        }
        if ("mud_block".equals(featureName)) {
            return worldgenSettings.generateMudDeposits;
        }
        if ("peat_dirt".equals(featureName)) {
            return worldgenSettings.generatePeatDeposits;
        }
        if ("pendorite_ore".equals(featureName)) {
            return contentSettings.pendoriteContentEnabled && worldgenSettings.generatePendoriteOre;
        }
        if ("scoria".equals(featureName)) {
            return worldgenSettings.generateScoriaDeposits;
        }
        if ("sepinite".equals(featureName)) {
            return worldgenSettings.generateSepiniteDeposits;
        }
        if ("soapstone".equals(featureName)) {
            return worldgenSettings.generateSoapstoneDeposits;
        }
        if ("tamrelite_ore".equals(featureName)) {
            return contentSettings.tamreliteContentEnabled && worldgenSettings.generateTamreliteOre;
        }
        if ("light_blue_sand".equals(featureName)) {
            return worldgenSettings.generateLightBlueSand;
        }
        if ("pink_sand".equals(featureName)) {
            return worldgenSettings.generatePinkSand;
        }
        if ("purple_sand".equals(featureName)) {
            return worldgenSettings.generatePurpleSand;
        }
        if ("peat_grass".equals(featureName)) {
            return worldgenSettings.generateRockySurfaceFeatures && worldgenSettings.generatePeatgrass;
        }
        if ("rocky_grass".equals(featureName)) {
            return worldgenSettings.generateRockySurfaceFeatures && worldgenSettings.generateRockyGrass;
        }
        if ("rocky_grass_alps".equals(featureName)) {
            return worldgenSettings.generateRockySurfaceFeatures && worldgenSettings.generateRockyGrassAlps;
        }
        if ("rocky_stone".equals(featureName)) {
            return worldgenSettings.generateRockySurfaceFeatures && worldgenSettings.generateRockystone;
        }
        if ("rockystone2".equals(featureName)) {
            return worldgenSettings.generateRockySurfaceFeatures && worldgenSettings.generateRockystone2;
        }
        if ("sandy_grass".equals(featureName)) {
            return worldgenSettings.generateRockySurfaceFeatures && worldgenSettings.generateSandygrass;
        }
        if ("sodalite".equals(featureName)) {
            return worldgenSettings.generateRockySurfaceFeatures && worldgenSettings.generateSodalite;
        }
        return true;
    }

    private static void ensureBaked() {
        if (!baked) {
            syncAndBake();
        }
    }

    private static ArmorMaterialSettings getArmorMaterialSettings(String setName) {
        if ("kasai".equals(setName)) {
            return equipmentSettings.kasaiArmor;
        }
        if ("latharium".equals(setName)) {
            return equipmentSettings.lathariumArmor;
        }
        if ("pendorite".equals(setName)) {
            return equipmentSettings.pendoriteArmor;
        }
        if ("tamrelite".equals(setName)) {
            return equipmentSettings.tamreliteArmor;
        }
        return null;
    }

    private static void syncAndBake() {
        ConfigManager.sync(BiomesYouGo.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
        bake();
    }

    private static void bake() {
        baked = false;
        ENABLED_BIOMES.clear();
        BIOME_WEIGHTS.clear();
        FOOD_VALUES.clear();

        for (String biomeName : biomeSettings.enabledBiomes) {
            String normalized = normalizeBiomeName(biomeName);
            if (!normalized.isEmpty()) {
                ENABLED_BIOMES.add(normalized);
            }
        }

        for (String entry : biomeSettings.biomeWeightOverrides) {
            if (entry == null) {
                continue;
            }
            String[] split = entry.split("=", 2);
            if (split.length != 2) {
                continue;
            }
            String normalized = normalizeBiomeName(split[0]);
            if (normalized.isEmpty()) {
                continue;
            }
            try {
                BIOME_WEIGHTS.put(normalized, Integer.parseInt(split[1].trim()));
            } catch (NumberFormatException ignored) {
                // Ignore malformed entries so a single bad value does not break startup.
            }
        }

        addFoodValues();
        mudMovementMultiplier = Math.max(0.0D, Math.min(1.0D, blockSettings.mudMovementMultiplier));
        quagmireSlimeChance = Math.max(0.0D, Math.min(0.05D, blockSettings.quagmireSlimeChance));
        MUD_IMMUNE_ENTITIES.clear();
        for (String entityId : blockSettings.mudSlownessImmuneEntities) {
            if (entityId != null && !entityId.trim().isEmpty()) {
                MUD_IMMUNE_ENTITIES.add(entityId.trim().toLowerCase(Locale.ROOT));
            }
        }
        baked = true;
    }

    private static void addFoodValues() {
        FOOD_VALUES.put("baobab_fruit", new FoodValues(foodSettings.baobabfruitFoodLevel, (float) foodSettings.baobabfruitSaturation));
        FOOD_VALUES.put("berry_juice", new FoodValues(foodSettings.berryJuiceFoodLevel, (float) foodSettings.berryJuiceSaturation));
        FOOD_VALUES.put("blueberry", new FoodValues(foodSettings.blueberryFoodLevel, (float) foodSettings.blueberrySaturation));
        FOOD_VALUES.put("blueberry_pie", new FoodValues(foodSettings.blueberrypieFoodLevel, (float) foodSettings.blueberrypieSaturation));
        FOOD_VALUES.put("carrot_soup", new FoodValues(foodSettings.carrotsoupFoodLevel, (float) foodSettings.carrotsoupSaturation));
        FOOD_VALUES.put("cooked_carrot", new FoodValues(foodSettings.cookedcarrotFoodLevel, (float) foodSettings.cookedcarrotSaturation));
        FOOD_VALUES.put("cooked_pufferfish", new FoodValues(foodSettings.cookedpufferfishFoodLevel, (float) foodSettings.cookedpufferfishSaturation));
        FOOD_VALUES.put("cooked_pumpkin_seeds", new FoodValues(foodSettings.cookedpumpkinseedsFoodLevel, (float) foodSettings.cookedpumpkinseedsSaturation));
        FOOD_VALUES.put("cooked_spider_eye", new FoodValues(foodSettings.cookedspidereyeFoodLevel, (float) foodSettings.cookedspidereyeSaturation));
        FOOD_VALUES.put("cooked_tropical_fish", new FoodValues(foodSettings.cookedtropicalfishFoodLevel, (float) foodSettings.cookedtropicalfishSaturation));
        FOOD_VALUES.put("glowshroom_soup_blue", new FoodValues(foodSettings.glowshroomsoupblueFoodLevel, (float) foodSettings.glowshroomsoupblueSaturation));
        FOOD_VALUES.put("glowshroom_soup_purple", new FoodValues(foodSettings.glowshroomsouppurpleFoodLevel, (float) foodSettings.glowshroomsouppurpleSaturation));
        FOOD_VALUES.put("golden_beetroot", new FoodValues(foodSettings.goldenbeetrootFoodLevel, (float) foodSettings.goldenbeetrootSaturation));
        FOOD_VALUES.put("green_glowshroom_stew", new FoodValues(foodSettings.greenGlowshroomStewFoodLevel, (float) foodSettings.greenGlowshroomStewSaturation));
        FOOD_VALUES.put("green_apple", new FoodValues(foodSettings.greenappleFoodLevel, (float) foodSettings.greenappleSaturation));
        FOOD_VALUES.put("green_apple_pie", new FoodValues(foodSettings.greenapplepieFoodLevel, (float) foodSettings.greenapplepieSaturation));
        FOOD_VALUES.put("hawthorn_berries", new FoodValues(foodSettings.hawthornberriesFoodLevel, (float) foodSettings.hawthornberriesSaturation));
        FOOD_VALUES.put("holly_berries", new FoodValues(foodSettings.hollyberriesFoodLevel, (float) foodSettings.hollyberriesSaturation));
        FOOD_VALUES.put("kiwi_cooked", new FoodValues(foodSettings.kiwiCookedFoodLevel, (float) foodSettings.kiwiCookedSaturation));
        FOOD_VALUES.put("kiwi_raw", new FoodValues(foodSettings.kiwiRawFoodLevel, (float) foodSettings.kiwiRawSaturation));
        FOOD_VALUES.put("pumpkin_bread",new FoodValues(foodSettings.pumpkinbreadFoodLevel, (float) foodSettings.pumpkinbreadSaturation));
        FOOD_VALUES.put("pumpkin_mash", new FoodValues(foodSettings.pumpkinmashFoodLevel, (float) foodSettings.pumpkinmashSaturation));
        FOOD_VALUES.put("rowan_berries", new FoodValues(foodSettings.rowanberriesFoodLevel, (float) foodSettings.rowanberriesSaturation));
        FOOD_VALUES.put("rudo_beans", new FoodValues(foodSettings.rudobeansFoodLevel, (float) foodSettings.rudobeansSaturation));
        FOOD_VALUES.put("rudo_beans_roasted", new FoodValues(foodSettings.rudobeansroastedFoodLevel, (float) foodSettings.rudobeansroastedSaturation));
        FOOD_VALUES.put("salal_berry", new FoodValues(foodSettings.salalBerryFoodLevel, (float) foodSettings.salalBerrySaturation));
        FOOD_VALUES.put("silver_apple", new FoodValues(foodSettings.silverAppleFoodLevel, (float) foodSettings.silverAppleSaturation));
        FOOD_VALUES.put("spider_eye_soup", new FoodValues(foodSettings.spidereyesoupFoodLevel, (float) foodSettings.spidereyesoupSaturation));
        FOOD_VALUES.put("strawberry", new FoodValues(foodSettings.strawberryFoodLevel, (float) foodSettings.strawberrySaturation));
        FOOD_VALUES.put("strawberry_pie", new FoodValues(foodSettings.strawberrypieFoodLevel, (float) foodSettings.strawberrypieSaturation));
        FOOD_VALUES.put("tropical_fish_soup", new FoodValues(foodSettings.tropicalfishsoupFoodLevel, (float) foodSettings.tropicalfishsoupSaturation));
    }

    private static String normalizeBiomeName(String biomeName) {
        if (biomeName == null) {
            return "";
        }
        String normalized = biomeName.trim();
        if (normalized.startsWith(BiomesYouGo.MODID + ":")) {
            normalized = normalized.substring(BiomesYouGo.MODID.length() + 1);
        }
        return normalized;
    }

    @Mod.EventBusSubscriber(modid = BiomesYouGo.MODID)
    public static final class EventHandler {
        private EventHandler() {
        }

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (BiomesYouGo.MODID.equals(event.getModID())) {
                syncAndBake();
            }
        }
    }

    public static final class ClientSettings {
        @net.minecraftforge.common.config.Config.Name("Enable Biome Fog")
        @net.minecraftforge.common.config.Config.Comment("Enable or disable BYG fog if you find it annoying in the Bayou, Cypress Swamplands, Mangrove Marshes, Bog, Glowshroom Bayou, and Dead Sea. Vanilla fog is unaffected.")
        public boolean enableBiomeFog = true;
    }

    public static final class BiomeSettings {
        @net.minecraftforge.common.config.Config.Comment({
                "Allowlist of BYG biome registry IDs. Only biomes in this list are registered and added to world generation.",
                "Remove an entry to disable that biome. IDs may be written as 'registry_name' or 'byg:registry_name'.",
                "Restart required after changing this list."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public String[] enabledBiomes = {
                "byg_allium_fields", "byg_alps", "byg_amaranth_fields", "byg_ancient_forest", "byg_aspen_forest",
                "byg_baobab_savanna", "byg_bayou", "byg_blue_taiga", "byg_bluff_mountains", "byg_bog",
                "byg_boreal_forest", "byg_chaparral_lowlands", "byg_cherry_grove", "byg_cika_forest", "byg_colored_canyons",
                "byg_coniferous_forest", "byg_crystal_canyons", "byg_cypress_swamplands", "byg_dead_sea", "byg_deciduous_forest",
                "byg_dover_mountains", "byg_dunes", "byg_ebony_woods", "byg_enchanted_forest", "byg_evergreen_taiga",
                "byg_flowering_plains", "byg_frosty_forest", "byg_fungal_jungle", "byg_giant_blue_spruce_taiga",
                "byg_giant_seasonal_spruce_taiga", "byg_giant_snowy_spruce_taiga", "byg_glaciers", "byg_glowshroom_bayou",
                "byg_grassland_plateau", "byg_great_lakes", "byg_great_oak_lowlands", "byg_jacaranda_forest", "byg_lush_desert",
                "byg_mangrove_marshes", "byg_maple_taiga", "byg_marshlands", "byg_meadow", "byg_northern_forest",
                "byg_orchard", "byg_outback", "byg_outlands", "byg_pine_lowlands", "byg_pine_mountains", "byg_prairie", "byg_quagmire",
                "byg_red_desert", "byg_red_oak_forest", "byg_red_outlands", "byg_redwood_tropics", "byg_savanna_canopy",
                "byg_seasonal_birch_forest", "byg_seasonal_deciduous", "byg_seasonal_forest", "byg_seasonal_taiga", "byg_shrublands",
                "byg_skyris_highlands", "byg_snowy_coniferous_forest", "byg_snowy_deciduous_forest", "byg_snowy_evergreen_taiga",
                "byg_snowy_pine_mountains", "byg_sonoran_desert", "byg_stellata_pasture", "byg_stone_brushlands",
                "byg_stone_pillar_savanna", "byg_tropical_islands",
                "byg_tropical_mountains", "byg_tropical_rainforest", "byg_weeping_witch_forest", "byg_whispering_woods", "byg_woodlands",
                "byg_zelkova_forest"
        };

        @net.minecraftforge.common.config.Config.Comment({
                "Changes how often an enabled biome is chosen during world generation, without disabling the biome itself.",
                "Each entry is 'registry_name=weight'. Weights are relative within the biome's climate group: a weight of 8",
                "is chosen about twice as often as a weight of 4. For example, 'byg_alps=8' makes Alps more common among icy biomes.",
                "Use a weight of 0 to prevent a biome from being chosen by its climate group. IDs may optionally use the 'byg:' namespace.",
                "Restart required after changing this list."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public String[] biomeWeightOverrides = {};

        @net.minecraftforge.common.config.Config.Comment({
                "Multiplier applied to every default or overridden biome weight.",
                "1.0 leaves weights unchanged; 0.5 halves them; 0.0 prevents all BYG biomes from being selected. Restart required."
        })
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public double globalBiomeWeightMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment({
                "Whether enabled BYG biomes are added to Forge's spawn-biome list for creature spawning.",
                "Set to false to keep BYG biomes in world generation while excluding them from that spawn list. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean addBiomesToSpawnList = true;
    }

    public static final class FoodSettings {
        public int baobabfruitFoodLevel = 4;
        public double baobabfruitSaturation = 0.3D;
        public int berryJuiceFoodLevel = 2;
        public double berryJuiceSaturation = 0.6D;
        public int blueberryFoodLevel = 2;
        public double blueberrySaturation = 0.3D;
        public int blueberrypieFoodLevel = 8;
        public double blueberrypieSaturation = 0.3D;
        public int carrotsoupFoodLevel = 9;
        public double carrotsoupSaturation = 0.6D;
        public int cookedcarrotFoodLevel = 4;
        public double cookedcarrotSaturation = 0.3D;
        public int cookedpufferfishFoodLevel = 6;
        public double cookedpufferfishSaturation = 0.3D;
        public int cookedpumpkinseedsFoodLevel = 3;
        public double cookedpumpkinseedsSaturation = 0.3D;
        public int cookedspidereyeFoodLevel = 5;
        public double cookedspidereyeSaturation = 0.3D;
        public int cookedtropicalfishFoodLevel = 5;
        public double cookedtropicalfishSaturation = 0.3D;
        public int glowshroomsoupblueFoodLevel = 8;
        public double glowshroomsoupblueSaturation = 0.6D;
        public int glowshroomsouppurpleFoodLevel = 8;
        public double glowshroomsouppurpleSaturation = 0.6D;
        public int goldenbeetrootFoodLevel = 6;
        public double goldenbeetrootSaturation = 0.3D;
        public int greenGlowshroomStewFoodLevel = 8;
        public double greenGlowshroomStewSaturation = 0.6D;
        public int greenappleFoodLevel = 6;
        public double greenappleSaturation = 0.3D;
        public int greenapplepieFoodLevel = 8;
        public double greenapplepieSaturation = 0.3D;
        public int hawthornberriesFoodLevel = 2;
        public double hawthornberriesSaturation = 0.3D;
        public int hollyberriesFoodLevel = 2;
        public double hollyberriesSaturation = 0.3D;
        public int kiwiCookedFoodLevel = 6;
        public double kiwiCookedSaturation = 0.8D;
        public int kiwiRawFoodLevel = 2;
        public double kiwiRawSaturation = 0.3D;
        public int pumpkinbreadFoodLevel = 7;
        public double pumpkinbreadSaturation = 0.3D;
        public int pumpkinmashFoodLevel = 3;
        public double pumpkinmashSaturation = 0.6D;
        public int rowanberriesFoodLevel = 2;
        public double rowanberriesSaturation = 0.3D;
        public int rudobeansFoodLevel = 1;
        public double rudobeansSaturation = 0.3D;
        public int rudobeansroastedFoodLevel = 3;
        public double rudobeansroastedSaturation = 0.3D;
        public int salalBerryFoodLevel = 2;
        public double salalBerrySaturation = 0.3D;
        public int silverAppleFoodLevel = 6;
        public double silverAppleSaturation = 0.3D;
        public int spidereyesoupFoodLevel = 10;
        public double spidereyesoupSaturation = 0.6D;
        public int strawberryFoodLevel = 2;
        public double strawberrySaturation = 0.3D;
        public int strawberrypieFoodLevel = 8;
        public double strawberrypieSaturation = 0.3D;
        public int tropicalfishsoupFoodLevel = 10;
        public double tropicalfishsoupSaturation = 0.6D;
    }

    public static final class ItemSettings {
        public boolean baobabfruitAlwaysEdible = true;
        public boolean baobabfruitClearPotionEffects = true;
        public boolean baobabfruitPlantingEnabled = true;
        public boolean baobabfruitPlantingConsumesItem = true;
        @net.minecraftforge.common.config.Config.Comment("Enable brewing the Potion of Clarity with Baobab Powder. Requires a Minecraft restart.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean clarityPotionBrewingEnabled = true;
        public boolean blueberryPlantingEnabled = true;
        public boolean blueberryPlantingConsumesItem = true;
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D)
        public double mudBallDamage = 1.0D;
        @net.minecraftforge.common.config.Config.Comment("Health restored instantly when drinking Berry Juice, in half-hearts. Set to 0.0 to disable the instant heal.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D)
        public double berryJuiceHealAmount = 4.0D;
        @net.minecraftforge.common.config.Config.Comment("Duration in ticks of the Regeneration effect applied by Berry Juice. Set to 0 to disable the effect.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int berryJuiceRegenerationDuration = 100;
        @net.minecraftforge.common.config.Config.Comment("Duration in ticks of the Speed I effect applied by Maple Syrup. Set to 0 to disable the effect. 800 ticks is 40 seconds.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int mapleSyrupSpeedDuration = 800;
        @net.minecraftforge.common.config.Config.Comment("How many sips a bottle of Maple Syrup gives before it is empty. Requires a Minecraft restart.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 32)
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public int mapleSyrupSips = 3;
        @net.minecraftforge.common.config.Config.Comment("Register the creative-only Biome Teleporter item (one stack per vanilla or modded biome, used for testing). Requires a Minecraft restart.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean biomeTeleporterItemsEnabled = true;
        @net.minecraftforge.common.config.Config.Comment("Duration in ticks of the Regeneration I effect a player gets from eating Golden Beetroot. Set to 0 to disable the effect. 2400 ticks is 2 minutes.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int goldenBeetrootRegenDuration = 2400;
        @net.minecraftforge.common.config.Config.Comment("Allow feeding Golden Beetroot to a tamed creature (wolf, cat, parrot, horse...) to give it permanent passive health regeneration. Saved with the creature.")
        public boolean goldenBeetrootPetRegenEnabled = true;
        @net.minecraftforge.common.config.Config.Comment("Ticks between regeneration pulses for a creature fed Golden Beetroot. Regeneration I heals every 50 ticks, so the default of 100 is half that rate.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1)
        public int goldenBeetrootPetRegenInterval = 100;
        @net.minecraftforge.common.config.Config.Comment("Health restored per pulse, in half-hearts.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D)
        public double goldenBeetrootPetRegenAmount = 1.0D;
        public boolean wormFishingEnhancementEnabled = true;
        @net.minecraftforge.common.config.Config.Comment("Lure level bonus applied to the fishing hook when a worm is consumed on cast. Stacks with the rod's Lure enchantment.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int wormLureLevel = 2;
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double wormFishingJunkRerollChance = 0.25D;
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double wormFishingQualityBoostChance = 0.15D;
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double wormFishingExtraFishChance = 0.10D;
    }

    public static final class ContentSettings {
        @net.minecraftforge.common.config.Config.Comment("Disable Kasai ore, storage block, ingot, chain plating, equipment, recipes, and generation. Change only before creating a world to avoid missing mappings.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean kasaiContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment("Disable Latharium ore, storage block, gem, equipment, recipes, and generation. Change only before creating a world to avoid missing mappings.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean lathariumContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment("Disable Pendorite ore, storage block, gem, equipment, recipes, and generation. Change only before creating a world to avoid missing mappings.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean pendoriteContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment("Disable Tamrelite ore, storage block, gem, equipment, recipes, and generation. Change only before creating a world to avoid missing mappings.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean tamreliteContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment("Set to false to disable the Kasai armour, weapon and tool set only; Kasai ore and materials stay. Restart required.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean kasaiEquipmentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment("Set to false to disable the Latharium armour, weapon and tool set only; Latharium ore and materials stay. Restart required.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean lathariumEquipmentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment("Set to false to disable the Pendorite armour, weapon and tool set only; Pendorite ore and materials stay. Restart required.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean pendoriteEquipmentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment("Set to false to disable the Tamrelite armour, weapon and tool set only; Tamrelite ore and materials stay. Restart required.")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean tamreliteEquipmentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Redwood Tropics and all redwood blocks and recipes.",
                "Removing byg_redwood_tropics from Enabled Biomes disables only the biome; redwood content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean redwoodContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Skyris Highlands and all skyris blocks, fruit items, and recipes.",
                "Removing byg_skyris_highlands from Enabled Biomes disables only the biome; skyris content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean skyrisContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Cika Forest and all cika blocks, items, and recipes.",
                "Removing byg_cika_forest from Enabled Biomes disables only the biome; cika content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean cikaContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Enchanted Forest and its blue, pink, purple, and green enchanted tree blocks and recipes.",
                "The enchanted stick item remains registered for recipes that use it, but its crafting recipe is disabled with the trees.",
                "Removing byg_enchanted_forest from Enabled Biomes disables only the biome; enchanted tree content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean enchantedTreeContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Cypress Swamplands and all cypress blocks, items, and recipes.",
                "Removing byg_cypress_swamplands from Enabled Biomes disables only the biome; cypress content remains available when this is true.",
                "When disabled, floating logs in Marshlands use willow instead of cypress.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean cypressContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Ebony Woods and all ebony blocks, items, and recipes.",
                "Removing byg_ebony_woods from Enabled Biomes disables only the biome; ebony content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean ebonyContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Great Oak Lowlands and all great oak blocks, items, and recipes.",
                "Removing byg_great_oak_lowlands from Enabled Biomes disables only the biome; great oak content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean greatOakContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Baobab Savanna and all baobab blocks, items, crafting recipes (including 1 fruit to 1 powder), and Clarity brewing with baobab powder.",
                "Removing byg_baobab_savanna from Enabled Biomes disables only the biome; baobab content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean baobabContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Mangrove Marshes and all mangrove blocks, items, and recipes.",
                "Removing byg_mangrove_marshes from Enabled Biomes disables only the biome; mangrove content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean mangroveContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable palm trees: all palm blocks, the palm door, recipes, the palm sapling, and palm tree generation",
                "(Tropical Islands and beaches). Palm has no biome of its own, so no biome is affected.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean palmContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Cherry Grove and all cherry blocks, items, and recipes.",
                "Removing byg_cherry_grove from Enabled Biomes disables only the biome; cherry content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean cherryContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable fir blocks, items, and recipes, and the Coniferous Forest and Snowy Coniferous Forest biomes that grow them.",
                "Removing those biomes from Enabled Biomes disables only the biomes; fir content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean firContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Maple Forest and all maple blocks, the maple tap, sappy maple logs, maple sap, maple syrup, maple pancakes, and recipes.",
                "Maple sap, maple syrup, and maple pancakes stay registered, but sap can no longer be collected without a tap.",
                "Removing byg_maple_taiga from Enabled Biomes disables only the biome; maple content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean mapleContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable pine blocks, items, and recipes, the pine house and campsite, and the Pine Lowlands, Pine Mountains,",
                "and Snowy Pine Mountains biomes. Removing those biomes from Enabled Biomes disables only the biomes; pine content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean pineContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Zelkova Forest and all zelkova blocks, items, and recipes.",
                "Removing byg_zelkova_forest from Enabled Biomes disables only the biome; zelkova content remains available when this is true.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean zelkovaContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable mahogany blocks, items, recipes, and mahogany tree generation.",
                "Tropical Rainforest and Tropical Mountains keep their other trees. Mahogany has no biome of its own, so no biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean mahoganyContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Jacaranda Forest and all jacaranda blocks, items, and recipes.",
                "Amaranth Fields and Allium Fields lose their jacaranda trees. Removing byg_jacaranda_forest from Enabled Biomes disables only that biome.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean jacarandaContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable holly blocks, items (including holly berries), recipes, and holly tree generation.",
                "Evergreen Taiga and Snowy Evergreen Taiga lose their holly trees. Holly has no biome of its own, so no biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean hollyContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable frozen oak blocks, items, recipes, and frozen oak tree generation.",
                "Frosty Forest and Northern Forest lose their frozen oaks. Frozen oak has no biome of its own, so no biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean frozenOakContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the Crystal Canyons biome and all crystal blocks, crystal items, recipes, and crystal canyon generation.",
                "Crystal crawlers only spawn in the Crystal Canyons, so they no longer appear naturally.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean crystalContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable rowan blocks, items (including rowan berries), recipes, and rowan tree generation.",
                "Also disables Whispering Woods, which grows both rowan and hawthorn.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean rowanContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable hawthorn blocks, items (including hawthorn berries), recipes, and hawthorn tree generation.",
                "Also disables Whispering Woods, which grows both rowan and hawthorn.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean hawthornContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable witch hazel blocks, items, recipes, and witch hazel tree generation.",
                "Also disables Weeping Witch Forest, whose trees and witch hut depend on it.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean witchHazelContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable aspen blocks, items, recipes, and aspen tree generation.",
                "Also disables Aspen Forest.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean aspenContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable willow blocks, items, recipes, and willow tree generation.",
                "Also disables Bayou and Glowshroom Bayou, whose trees and structures are built from willow. Marshlands floating logs fall back to cypress.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean willowContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable ironwood blocks, items, recipes, and ironwood tree generation.",
                "Lush Desert and the canyons keep their other features but lose their ironwood trees.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean ironwoodContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable rainbow eucalyptus blocks, items (including the eucalyptus door), recipes, and tree generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean rainbowEucalyptusContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable glowcane, glowcelium, their items and recipes, and glowcane structures (including the enchanted village).",
                "Also disables Glowshroom Bayou.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean glowcaneContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the red, orange, dry brown and dry green oak variants: their saplings, leaves, recipes, and tree generation.",
                "Also disables Red Oak Forest, Seasonal Forest, Seasonal Deciduous and Stone Pillar Savanna. Great oak and frozen oak have their own toggles.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean oakVariantsContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable palo verde blocks, recipes, and palo verde tree generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean paloVerdeContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the light blue, pink, purple and white sands and their sandstone, chiseled and smooth variants, plus their recipes and sand deposits.",
                "Also disables Mangrove Marshes (white sand) and Crystal Canyons (white sandstone), whose terrain is made of them. Black sand has its own toggle.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean coloredSandContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the golden spined cactus, mini cactus, prickly pear and Sonoran cacti, their recipes, and their generation.",
                "No biome is disabled; Stone Pillar Savanna simply grows no cacti.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean cactiContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the black puff, shelf fungi, weeping milk cap and wood blewit mushrooms and their generation.",
                "Glowshrooms have their own toggle. No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean mushroomDecorContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable small ground-cover plants and debris: algae, blanket weed, ivy, poison ivy, tiny lilypads, stone pebbles and spikes, thorns, clover, dead grass and leaf piles, and their generation.",
                "Reeds, prairie grass and overgrown stone are not included because biomes are built from them. No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean groundCoverContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable BYG prepared foods (soups, cooked items, berry juice, golden beetroot, pumpkin foods, carved melon and jack-o-melon), their recipes, and their smelting.",
                "Foods that belong to a plant set, such as strawberry pie, follow that set instead. No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean foodContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Fungal Zombies: the mob is not registered, has no spawn egg, and never spawns.",
                "Existing mobs of this type disappear from loaded worlds. No blocks or biomes are affected.",
                "Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean fungalZombieContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Fungal Skeletons: the mob and its mushroom blob are not registered, it has no spawn egg, and never spawns.",
                "Existing mobs of this type disappear from loaded worlds. No blocks or biomes are affected.",
                "Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean fungalSkeletonContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Kiwi Birds: the mob is not registered, has no spawn egg, and never spawns.",
                "Existing mobs of this type disappear from loaded worlds. No blocks or biomes are affected.",
                "Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean kiwiBirdContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable Crystal Crawlers: the mob is not registered, has no spawn egg, and never spawns.",
                "Existing mobs of this type disappear from loaded worlds. No blocks or biomes are affected.",
                "Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean crystalCrawlerContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable BYG villages, huts, houses, camps, outposts, towers and mob structures (template-based structures only).",
                "Trees, plants and natural features are controlled by their own toggles. Structures built in code, such as the Dead Sea shipwrecks, are not affected. No biome is disabled.",
                "Restart is not required for this option, but it only affects newly generated chunks."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean structuresContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable black sand and the black sandstone, chiseled and smooth variants, plus their recipes and the black sand to glass smelting.",
                "Also disables Dead Sea, whose terrain is made of black sand. Leave this true to keep Dead Sea while disabling the other coloured sands.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean blackSandContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable orchard (apple tree) blocks, recipes, and orchard tree generation.",
                "Also disables Orchard.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean orchardContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable stellata tree blocks, recipes, and tree generation.",
                "Also disables Stellata Pasture.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean stellataContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the brown, orange, red and yellow birch variants: saplings, leaves, and tree generation.",
                "Also disables Seasonal Birch Forest.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean birchVariantsContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the blue, orange, red and yellow spruce variants: saplings, leaves, and tree generation.",
                "Also disables Blue Taiga, Giant Blue Spruce Taiga, Seasonal Taiga and Giant Seasonal Spruce Taiga.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean spruceVariantsContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable sepinite stone blocks, recipes, and sepinite deposit generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean sepiniteContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable soapstone blocks, recipes, and soapstone deposit generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean soapstoneContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable sodalite blocks, recipes, and sodalite deposit generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean sodaliteContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable scoria blocks, recipes, and scoria deposit generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean scoriaContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable glowshroom plants, glowshroom blocks, soups and stew, and their generation.",
                "Ancient Forest keeps its ordinary giant mushrooms; Fungal Zombies and Fungal Skeletons stop dropping glowshrooms.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean glowshroomContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable strawberries, strawberry bushes, wild strawberries, strawberry pie, and their generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean strawberryContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable blueberries, blueberry bushes, blueberry pie, and their generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean blueberryContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable rudo beans, rudo stalks, wild rudo, roasted beans, and their generation.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean rudoContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable cattails, cattail rhizomes, cooked rhizomes, and cattail generation in marshes.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean cattailContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable salal bushes and salal berries, and their generation.",
                "Kiwi birds stop being tempted by salal berries. No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean salalContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable the nether furnace blocks and recipes.",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean netherFurnaceContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable all BYG flowers and petal blocks and their flower generation (salal, strawberry, blueberry and rudo plants have their own toggles).",
                "No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean flowersContentEnabled = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Set to false to disable every stripped log and stripped wood block of all wood sets at once.",
                "A stripped block is also dropped when its own wood set is disabled; this toggle only adds to that. No biome is disabled.",
                "Change only before creating a world: removing blocks from an existing world causes missing mappings. Restart required."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean strippedLogsEnabled = true;
    }

    public static final class BlockSettings {
        public int blueberryBushBerryCount = 2;
        public int strawberryBushBerryCount = 2;
        public int baobabFruitHarvestCount = 1;
        public int baobabFruitBreakDropCount = 3;
        public double blueberryBushStageGrowthChance = 0.5D;
        public double blueberryBushBonemealGrowthChance = 0.4D;
        public double strawberryBushStageGrowthChance = 0.5D;
        public double strawberryBushBonemealGrowthChance = 0.4D;
        public double baobabFruitStageGrowthChance = 0.3D;
        public double baobabFruitBonemealGrowthChance = 0.4D;
        public int baobabFruitStageTickRate = 1800;
        @net.minecraftforge.common.config.Config.Comment("Ticks a Maple Tap takes to fill one of its 3 stages, so a full tap takes three times this. 200 ticks is 10 seconds per stage.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1)
        public int mapleTapStageTickRate = 200;
        @net.minecraftforge.common.config.Config.Comment("Chance (0 to 1) that a Sappy Maple Log turns back into a plain Maple Log each time sap is taken from its tap.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double mapleTapLogDepletionChance = 0.2D;
        public double thornblockDamage = 1.0D;
        public double thornBranchesDamage = 1.0D;
        @net.minecraftforge.common.config.Config.Comment("Damage dealt to entities touching a Sonoran Cactus.")
        public double cactusDamage = 1.0D;
        public double damagingPlantDamage = 1.0D;
        @net.minecraftforge.common.config.Config.Comment("Horizontal movement retained while walking through mud. 1.0 disables slowdown; 0.925 is a 7.5% slowdown.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double mudMovementMultiplier = 0.925D;
        @net.minecraftforge.common.config.Config.Comment({
                "Entity registry IDs ('modid:entity_name') that are not slowed by mud, such as flying or ethereal mobs.",
                "An empty list means every entity is slowed."
        })
        public String[] mudSlownessImmuneEntities = {"wilderlands:battlefield_ghost"};
        @net.minecraftforge.common.config.Config.Comment("Chance for an eligible quagmire land surface to be a slime pocket. Set to 0.0 to disable slime generation.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 0.05D)
        public double quagmireSlimeChance = 0.004D;
        @net.minecraftforge.common.config.Config.Comment({
                "How many blocks of connected leaves may lie between a leaf and the nearest log or wood block before the leaf decays once a tree is cut or damaged.",
                "Vanilla uses 4, which cuts the outer crown off many BYG trees. Applies to leaves the next time they check for decay."
        })
        @net.minecraftforge.common.config.Config.RangeInt(min = 4, max = 12)
        public int leafDecayRange = 7;

        @net.minecraftforge.common.config.Config.Comment({
                "Multiplier for the chance that one bone meal makes a BYG sapling grow. Every sapling has a base chance of 0.4.",
                "0 disables bone meal growth, 2.5 or more makes every bone meal grow the tree."
        })
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 10.0D)
        public double saplingBonemealChanceMultiplier = 1.0D;
        public double springwaterBubbleChance = 0.4D;
        public int springwaterBubbleParticleCount = 2;
        @net.minecraftforge.common.config.Config.Comment("Whether Springwater grants Regeneration I while an entity remains in it.")
        public boolean springwaterAppliesRegeneration = true;
        public boolean netherFurnaceIgnitesFromFireBelow = true;
        public int netherFurnaceTickRate = 20;
        public int netherFurnaceLitTickRate = 10;
        public double netherFurnaceAmbientEffectChance = 0.01D;
        public int netherFurnaceAmbientParticleCount = 5;
        public boolean poisonIvyAppliesPoison = true;
        public int poisonIvyPoisonDuration = 300;
        public int poisonIvyPoisonAmplifier = 1;
        public int structureSpawnBlockTickRate = 2;
        public double coloredCanyonMiddleTemplateChance = 0.2D;
        public double coloredCanyonPrimaryTopTemplateChance = 0.5D;
        public double crystalCanyonBlueTemplateChance = 0.25D;
        public double crystalCanyonPurpleTemplateChance = 0.25D;
        public double crystalCanyonRedTemplateChance = 0.25D;
        public double crystalCanyonWhiteTemplateChance = 0.25D;
    }

    public static final class CreatureSettings {
        @net.minecraftforge.common.config.Config.Comment({
                "Allow witches to spawn naturally on the surface of Weeping Witch Forest during the day.",
                "When disabled, vanilla witch spawning rules and weight apply. Restart required after changing this setting."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean daytimeWitchesInWeepingWitchForest = true;

        @net.minecraftforge.common.config.Config.Comment({
                "Biome registry IDs where kiwis spawn naturally. Use 'registry_name' for BYG biomes or 'modid:registry_name' for other mods.",
                "An empty list disables natural kiwi spawning. Restart required after changing this list."
        })
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public String[] kiwiSpawnBiomes = {"byg_woodlands", "byg_great_oak_lowlands", "byg_deciduous_forest"};

        @net.minecraftforge.common.config.Config.Comment({
                "Relative spawn weight for kiwis in the configured biomes. Higher values make them more common; 25 is the default.",
                "Set to 0 to disable natural kiwi spawning. Restart required."
        })
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public int kiwiSpawnWeight = 25;

        @net.minecraftforge.common.config.Config.Comment("Relative spawn weight for fungal zombies in Fungal Jungle. 20 is the default; 0 disables natural spawning. Restart required.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public int fungalZombieSpawnWeight = 20;

        @net.minecraftforge.common.config.Config.Comment("Relative spawn weight for fungal skeletons in Fungal Jungle. 20 is the default; 0 disables natural spawning. Restart required.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public int fungalSkeletonSpawnWeight = 20;

        @net.minecraftforge.common.config.Config.Comment("Amplifier of the Poison effect a fungal zombie's hit inflicts. 0 is Poison I, 1 is Poison II, and so on.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 127)
        public int fungalZombiePoisonAmplifier = 0;

        @net.minecraftforge.common.config.Config.Comment("Duration in ticks of the Poison effect a fungal zombie's hit inflicts. Set to 0 to disable the poison. 160 ticks is 8 seconds.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int fungalZombiePoisonDuration = 160;

        @net.minecraftforge.common.config.Config.Comment("Average interval in ticks between kiwi forage attempts at night. Lower values make kiwis dig for worms more often.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1)
        public int kiwiForageAttemptInterval = 120;

        @net.minecraftforge.common.config.Config.Comment("Chance for a kiwi to find a worm when a forage action completes.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double kiwiWormFindChance = 0.33D;

        @net.minecraftforge.common.config.Config.Comment({
                "Minimum ticks between eggs laid by an adult kiwi. A random extra delay of up to the same amount is added, so 12000 means one egg every 10 to 20 minutes.",
                "Set very high to make eggs effectively rare."
        })
        @net.minecraftforge.common.config.Config.RangeInt(min = 1)
        public int kiwiEggLayInterval = 12000;

        @net.minecraftforge.common.config.Config.Comment("Number of Raw Kiwi Meat a kiwi drops when killed. Each level of Looting adds up to one extra. Set to 0 to stop kiwis dropping meat.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int kiwiMeatDropCount = 1;
    }

    public static final class EquipmentSettings {
        @net.minecraftforge.common.config.Config.Name("Kasai Armour Material")
        @net.minecraftforge.common.config.Config.Comment("Kasai armour durability, protection, enchantability, and toughness.")
        public ArmorMaterialSettings kasaiArmor = new ArmorMaterialSettings(30, 4, 6, 6, 4, 11, 3.5D);

        @net.minecraftforge.common.config.Config.Name("Latharium Armour Material")
        @net.minecraftforge.common.config.Config.Comment("Latharium armour durability, protection, enchantability, and toughness.")
        public ArmorMaterialSettings lathariumArmor = new ArmorMaterialSettings(24, 3, 5, 5, 3, 16, 2.0D);

        @net.minecraftforge.common.config.Config.Name("Pendorite Armour Material")
        @net.minecraftforge.common.config.Config.Comment("Pendorite armour durability, protection, enchantability, and toughness.")
        public ArmorMaterialSettings pendoriteArmor = new ArmorMaterialSettings(25, 2, 2, 4, 2, 15, 1.0D);

        @net.minecraftforge.common.config.Config.Name("Tamrelite Armour Material")
        @net.minecraftforge.common.config.Config.Comment("Tamrelite armour durability, protection, enchantability, and toughness.")
        public ArmorMaterialSettings tamreliteArmor = new ArmorMaterialSettings(44, 4, 7, 8, 4, 10, 2.5D);

        public boolean lathariumBootsLevitationEnabled = true;
        public int lathariumBootsLevitationDuration = 40;
        public int lathariumBootsLevitationAmplifier = 2;
        public double lathariumBootsParticleChance = 0.1D;
        public int lathariumBootsParticleCount = 5;

        public boolean kasaiFireWardEnabled = true;
        public int kasaiFireWardDuration = 600;
        public int kasaiFireWardCooldown = 6000;
    }

    public static final class ArmorMaterialSettings {
        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int durabilityMultiplier;

        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int bootsProtection;

        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int leggingsProtection;

        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int chestplateProtection;

        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int helmetProtection;

        @net.minecraftforge.common.config.Config.RangeInt(min = 0)
        public int enchantability;

        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D)
        public double toughness;

        public ArmorMaterialSettings() {
        }

        public ArmorMaterialSettings(int durabilityMultiplier, int bootsProtection, int leggingsProtection,
                                     int chestplateProtection, int helmetProtection, int enchantability, double toughness) {
            this.durabilityMultiplier = durabilityMultiplier;
            this.bootsProtection = bootsProtection;
            this.leggingsProtection = leggingsProtection;
            this.chestplateProtection = chestplateProtection;
            this.helmetProtection = helmetProtection;
            this.enchantability = enchantability;
            this.toughness = toughness;
        }
    }

    public static final class ArmorMaterialStats {
        private final int durabilityMultiplier;
        private final int[] damageReductionAmounts;
        private final int enchantability;
        private final float toughness;

        private ArmorMaterialStats(int durabilityMultiplier, int bootsProtection, int leggingsProtection,
                                   int chestplateProtection, int helmetProtection, int enchantability, float toughness) {
            this.durabilityMultiplier = durabilityMultiplier;
            this.damageReductionAmounts = new int[]{bootsProtection, leggingsProtection, chestplateProtection, helmetProtection};
            this.enchantability = enchantability;
            this.toughness = toughness;
        }

        public int getDurabilityMultiplier() {
            return durabilityMultiplier;
        }

        public int[] getDamageReductionAmounts() {
            return damageReductionAmounts.clone();
        }

        public int getEnchantability() {
            return enchantability;
        }

        public float getToughness() {
            return toughness;
        }
    }

    public static final class WorldgenSettings {
        @net.minecraftforge.common.config.Config.Comment("Dimension IDs where BYG's surface and underground world features generate (structures, trees, plants, ground patches and overworld ores). Features still only appear in their matching biomes, so add a dimension here only if it uses BYG or vanilla overworld biomes. Kasai ore always generates in the Nether.")
        public int[] featureDimensions = {0};

        @net.minecraftforge.common.config.Config.Comment("Multiplies the per-chunk spawn chance of BYG template-based and related rare features in new chunks. 1.0 is the default; 0 skips these chance checks. Higher values cannot raise a chance above 100%.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double templateChanceMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Multiplies BYG flower patch placement attempts in matching biomes when generating new chunks. 1.0 is the default; 0 skips these attempts. Increase gradually: higher values add generation work but do not guarantee proportionally more flowers because placement needs valid spots.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double flowerAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Multiplies BYG clustered plant and ground-cover patch attempts when generating new chunks, including reeds, shrubs, and forest floor plants. 1.0 is the default; 0 skips most scaled attempts. Increase gradually: this affects many features and adds generation work, while valid placement spots still limit results.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double clusterPlantAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Multiplies attempts to replace vanilla sand with BYG coloured sand deposits in eligible biomes when generating new chunks. 1.0 is the default; 0 skips these attempts. More attempts add generation work and need suitable sand to replace.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double sandAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Multiplies attempts for BYG underground deposits in new chunks, including ores, mud, peat, scoria, sepinite, and soapstone. 1.0 is the default; 0 skips these attempts. Ore-specific attempt multipliers also apply.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double undergroundDepositAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Multiplies the size of each BYG underground deposit vein in new chunks, including ores, mud, peat, scoria, sepinite, and soapstone. 1.0 is the default; the minimum is 0.1. This changes vein size, not the number of attempts.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.1, max = 10.0)
        public double undergroundDepositVeinSizeMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Kasai ore generation attempts relative to default. Multiplies the global underground deposit attempt setting; 0 disables attempts in new chunks.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double kasaiOreAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Latharium ore generation attempts relative to default. Multiplies the global underground deposit attempt setting; 0 disables attempts in new chunks.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double lathariumOreAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Pendorite ore generation attempts relative to default. Multiplies the global underground deposit attempt setting; 0 disables attempts in new chunks.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double pendoriteOreAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Tamrelite ore generation attempts relative to default. Multiplies the global underground deposit attempt setting; 0 disables attempts in new chunks.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double tamreliteOreAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Scoria generation attempts relative to default. Multiplies the global underground deposit attempt setting; 0 disables attempts in new chunks.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double scoriaAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Lowest Y level where scoria deposits can start.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 255)
        public int scoriaMinY = 1;

        @net.minecraftforge.common.config.Config.Comment("Highest Y level where scoria deposits can start. Must be at least the minimum Y.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 255)
        public int scoriaMaxY = 14;

        @net.minecraftforge.common.config.Config.Comment({
                "Biome registry IDs where scoria deposits may generate. Use 'registry_name' for BYG biomes or 'modid:registry_name' for other mods.",
                "An empty list (the default) lets scoria generate in every biome. Applies to newly generated chunks."
        })
        public String[] scoriaBiomes = {};

        @net.minecraftforge.common.config.Config.Comment({
                "Biome registry IDs where soapstone deposits may generate. Use 'registry_name' for BYG biomes or 'modid:registry_name' for other mods.",
                "An empty list (the default) lets soapstone generate in every biome. Applies to newly generated chunks."
        })
        public String[] soapstoneBiomes = {};

        @net.minecraftforge.common.config.Config.Comment("Sepinite generation attempts relative to default. Multiplies the global underground deposit attempt setting; 0 disables attempts in new chunks.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0, max = 10.0)
        public double sepiniteAttemptMultiplier = 1.0D;

        @net.minecraftforge.common.config.Config.Comment("Lowest Y level where sepinite deposits can start.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 255)
        public int sepiniteMinY = 60;

        @net.minecraftforge.common.config.Config.Comment("Highest Y level where sepinite deposits can start. Must be at least the minimum Y.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 255)
        public int sepiniteMaxY = 119;

        @net.minecraftforge.common.config.Config.Comment("Keep BYG trees from generating around Chocolate Quest Repoured dungeons so the dungeons are not cut into the forest. Only has an effect when CQR is installed. Applies to newly generated chunks.")
        public boolean avoidCqrDungeons = true;

        @net.minecraftforge.common.config.Config.Comment("Radius in chunks around a CQR dungeon that stays free of BYG trees.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 16)
        public int cqrAvoidanceRadiusChunks = 5;

        public boolean generateEnchantedVillage = true;
        public boolean generatePaloTrees = true;
        public boolean generateRockySurfaceFeatures = true;
        public boolean generateDeadGrassClusters = true;
        public boolean generateShortDeadGrassClusters = true;
        public boolean generateAlgaePatches = true;
        public boolean generateStonePebbles = true;
        public boolean generateStoneSpikes = true;
        public boolean generateGlowcaneBlue = true;
        public boolean generateGlowcanePink = true;
        public boolean generateGlowcanePurple = true;
        public boolean generateGlowcaneRed = true;
        public boolean generateGoldenSpinedCactus = true;
        public boolean generateMinicactus = true;
        public boolean generatePricklyPear = true;
        public boolean generateKasaiOre = true;
        public boolean generateLathariumOre = true;
        public boolean generateMudDeposits = true;
        public boolean generatePeatDeposits = true;
        public boolean generatePendoriteOre = true;
        public boolean generateScoriaDeposits = true;
        public boolean generateSepiniteDeposits = true;
        public boolean generateSoapstoneDeposits = true;
        public boolean generateTamreliteOre = true;
        public boolean generateLightBlueSand = true;
        public boolean generatePinkSand = true;
        public boolean generatePurpleSand = true;
        public boolean generatePeatgrass = true;
        public boolean generateRockyGrass = true;
        public boolean generateRockyGrassAlps = true;
        public boolean generateRockystone = true;
        public boolean generateRockystone2 = true;
        public boolean generateSandygrass = true;
        public boolean generateSodalite = true;
    }

    public static final class FoodValues {
        public final int foodLevel;
        public final float saturation;

        private FoodValues(int foodLevel, float saturation) {
            this.foodLevel = foodLevel;
            this.saturation = saturation;
        }
    }
}

