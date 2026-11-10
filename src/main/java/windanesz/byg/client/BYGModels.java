package windanesz.byg.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import windanesz.byg.BiomesYouGo;
import windanesz.byg.blocks.BlockAlgae;
import windanesz.byg.blocks.BlockBygLeaves;
import windanesz.byg.blocks.BlockFenceGateBase;
import windanesz.byg.blocks.BlockSonoranCactus;
import windanesz.byg.blocks.BlockGeneratedSaplingBase;
import windanesz.byg.items.ItemBiomeTeleporter;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.registry.ModItems;

@Mod.EventBusSubscriber(modid = BiomesYouGo.MODID, value = Side.CLIENT)
public final class BYGModels {
    // check_decay and decayable track leaf decay state only; they never change which model is used, so the renderer
    // must ignore them or it would look for blockstate variants ("check_decay=true" etc.) that don't exist.
    private static final StateMap LEAF_DECAY_STATE_MAP = new StateMap.Builder().ignore(BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build();

    // BYG saplings inherit vanilla's tree "type" property, but it is never used (metadata is fixed to 0), so the
    // renderer must ignore it or it would look for blockstate variants for spruce, birch, etc.
    private static final StateMap SAPLING_TYPE_STATE_MAP = new StateMap.Builder().ignore(BlockSapling.TYPE).build();

    private static final StateMap FENCE_GATE_STATE_MAP = new StateMap.Builder().ignore(BlockFenceGate.POWERED).build();
    private static final StateMap CACTUS_STATE_MAP = new StateMap.Builder().ignore(BlockCactus.AGE).build();

    private BYGModels() {
    }

    @SubscribeEvent
    public static void register(ModelRegistryEvent event) {
        for (Item item : Item.REGISTRY) {
            ResourceLocation registryName = item.getRegistryName();
            if (registryName != null && BiomesYouGo.MODID.equals(registryName.getNamespace())) {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(registryName, "inventory"));
            }
        }
        for (Block block : Block.REGISTRY) {
            if (block instanceof BlockBygLeaves) {
                ModelLoader.setCustomStateMapper(block, LEAF_DECAY_STATE_MAP);
            } else if (block instanceof BlockGeneratedSaplingBase) {
                ModelLoader.setCustomStateMapper(block, SAPLING_TYPE_STATE_MAP);
            } else if (block instanceof BlockFenceGateBase) {
                ModelLoader.setCustomStateMapper(block, FENCE_GATE_STATE_MAP);
            } else if (block instanceof BlockSonoranCactus) {
                ModelLoader.setCustomStateMapper(block, CACTUS_STATE_MAP);
            }
        }
    }

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        if (ModBlocks.algae == null) {
            return;
        }
        event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            return ((BlockAlgae) ModBlocks.algae).colorMultiplier(world, pos, tintIndex);
        }, ModBlocks.algae);
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        if (ModBlocks.algae != null) {
            event.getItemColors().registerItemColorHandler((stack, tintIndex) -> 0x456F43,
                    Item.getItemFromBlock(ModBlocks.algae));
        }
        if (ModItems.biome_teleporter != null) {
            event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                net.minecraft.world.biome.Biome biome = ItemBiomeTeleporter.getBiome(stack);
                return biome == null ? 0xFFFFFF : biome.getFoliageColorAtPos(net.minecraft.util.math.BlockPos.ORIGIN);
            }, ModItems.biome_teleporter);
        }
        // layer0 is the potion liquid overlay, layer1 the glass bottle, which stays untinted.
        if (ModItems.berry_juice != null) {
            event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex > 0 ? 0xFFFFFF : 0xB3225B,
                    ModItems.berry_juice);
        }
        if (ModItems.maple_sap != null) {
            event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex > 0 ? 0xFFFFFF : 0xD6E8E6,
                    ModItems.maple_sap);
            event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex > 0 ? 0xFFFFFF : 0xB5651D,
                    ModItems.maple_syrup);
        }
    }
}
