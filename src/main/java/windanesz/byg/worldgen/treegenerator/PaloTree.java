package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.Template;
import windanesz.byg.Config;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.BygTreePlacement;
import windanesz.byg.worldgen.TemplateWorldgenHelper;

import java.util.Random;

public final class PaloTree {
    private static final ResourceLocation TEMPLATE = new ResourceLocation("byg", "palo_verde3");
    private static final ResourceLocation RED_DESERT = new ResourceLocation("byg", "byg_red_desert");
    private static final ResourceLocation LUSH_DESERT = new ResourceLocation("byg", "byg_lush_desert");
    private static final ResourceLocation OUTBACK = new ResourceLocation("byg", "byg_outback");
    private static final IBlockState RED_SAND = Blocks.SAND.getStateFromMeta(1);
    private static final Rotation[] ROTATIONS = {
            Rotation.NONE, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90
    };
    private static final Mirror[] MIRRORS = {Mirror.NONE, Mirror.LEFT_RIGHT, Mirror.FRONT_BACK};

    private PaloTree() {
    }

    public static void generateWorld(Random random, int chunkCenterX, int chunkCenterZ, World world, int dimID) {
        if (!Config.isFeatureDimension(dimID) || world.isRemote || random.nextInt(100) >= 5) {
            return;
        }

        int x = chunkCenterX + random.nextInt(16);
        int z = chunkCenterZ + random.nextInt(16);
        int topY = world.getHeight(new BlockPos(x, 0, z)).getY() - 1;
        BlockPos.MutableBlockPos surface = new BlockPos.MutableBlockPos(x, topY, z);
        while (surface.getY() > 0) {
            IBlockState state = world.getBlockState(surface);
            if (!state.getBlock().isAir(state, world, surface) && !state.getBlock().isReplaceable(world, surface)) {
                break;
            }
            surface.setPos(x, surface.getY() - 1, z);
        }
        if (surface.getY() <= 0) {
            return;
        }

        IBlockState ground = world.getBlockState(surface);
        if (!ground.equals(RED_SAND) && ground.getBlock() != ModBlocks.sandy_grass) {
            return;
        }
        ResourceLocation biomeId = Biome.REGISTRY.getNameForObject(world.getBiome(surface));
        if (!RED_DESERT.equals(biomeId) && !LUSH_DESERT.equals(biomeId) && !OUTBACK.equals(biomeId)) {
            return;
        }

        Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(), TEMPLATE);
        if (template == null) {
            return;
        }
        Rotation rotation = ROTATIONS[random.nextInt(ROTATIONS.length)];
        Mirror mirror = MIRRORS[random.nextInt(MIRRORS.length)];
        int[] bounds = TemplateWorldgenHelper.getFootprintBounds(template.getSize().getX(), template.getSize().getZ(), rotation, mirror);
        BlockPos origin = new BlockPos(x - (bounds[0] + bounds[1]) / 2, surface.getY() - 1,
                z - (bounds[2] + bounds[3]) / 2);
        if (origin.getY() < 0 || origin.getY() + template.getSize().getY() > world.getActualHeight()
                || !TemplateWorldgenHelper.fitsCurrentChunk(template, origin, rotation, mirror,
                chunkCenterX - 8, chunkCenterZ - 8)
                || !BygTreePlacement.hasRoom(world, x, z, origin.getX() + bounds[0], origin.getX() + bounds[1],
                origin.getZ() + bounds[2], origin.getZ() + bounds[3])
                || !BygTreePlacement.allowTrees(world, random, surface)) {
            return;
        }
        TemplateWorldgenHelper.placeTemplateWithSettings(world, template, origin,
                TemplateWorldgenHelper.chunkPlacementSettings(rotation, mirror, chunkCenterX, chunkCenterZ));
    }
}
