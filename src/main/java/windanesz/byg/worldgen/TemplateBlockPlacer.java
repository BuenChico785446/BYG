package windanesz.byg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import windanesz.byg.BiomesYouGo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Places template blocks straight into chunks instead of through {@link World#setBlockState}; falls back to vanilla placement when it cannot. */
final class TemplateBlockPlacer {

    private static final Logger LOGGER = LogManager.getLogger(BiomesYouGo.MODID);
    private static final boolean VANILLA_PLACEMENT = Boolean.getBoolean("byg.vanillaTemplatePlacement");

    private static Field blocksField;
    private static Field entitiesField;
    private static boolean resolved;

    private TemplateBlockPlacer() {
    }

    static boolean place(World world, Template template, BlockPos origin, PlacementSettings settings, int flags) {
        if (VANILLA_PLACEMENT || world.isRemote || world.captureBlockSnapshots) {
            return false;
        }
        List<Template.BlockInfo> blocks = blocksOf(template);
        if (blocks == null || (!settings.getIgnoreEntities() && !hasNoEntities(template))) {
            return false;
        }
        BlockPos size = template.getSize();
        if (blocks.isEmpty() || size.getX() < 1 || size.getY() < 1 || size.getZ() < 1) {
            return true;
        }

        Block replaced = settings.getReplacedBlock();
        StructureBoundingBox box = settings.getBoundingBox();
        Mirror mirror = settings.getMirror();
        Rotation rotation = settings.getRotation();
        boolean ignoreStructureBlock = settings.getIgnoreStructureBlock();
        FastBlockWriter writer = new FastBlockWriter(world);
        Map<IBlockState, IBlockState> transformed = new IdentityHashMap<>();
        List<BlockPos> supportSensitive = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (Template.BlockInfo info : blocks) {
            BlockPos local = Template.transformedBlockPos(settings, info.pos);
            cursor.setPos(origin.getX() + local.getX(), origin.getY() + local.getY(), origin.getZ() + local.getZ());
            Block block = info.blockState.getBlock();
            if (block == replaced || (ignoreStructureBlock && block == Blocks.STRUCTURE_BLOCK)
                    || (box != null && !box.isVecInside(cursor))) {
                continue;
            }
            IBlockState state = transformed.get(info.blockState);
            if (state == null) {
                state = info.blockState.withMirror(mirror).withRotation(rotation);
                transformed.put(info.blockState, state);
            }
            if (info.tileentityData != null) {
                placeWithTileEntity(world, cursor.toImmutable(), state, info.tileentityData, mirror, rotation, flags);
            } else {
                writer.setDirect(cursor.getX(), cursor.getY(), cursor.getZ(), state);
            }
            if (reactsToNeighbours(world, cursor, state)) {
                supportSensitive.add(cursor.toImmutable());
            }
        }

        // Without the vanilla neighbour pass, unsupported plants and attachments would stay; only those blocks need a check.
        for (BlockPos pos : supportSensitive) {
            world.getBlockState(pos).neighborChanged(world, pos, Blocks.AIR, pos.down());
        }
        return true;
    }

    private static void placeWithTileEntity(World world, BlockPos pos, IBlockState state, NBTTagCompound data,
                                            Mirror mirror, Rotation rotation, int flags) {
        TileEntity existing = world.getTileEntity(pos);
        if (existing != null) {
            if (existing instanceof IInventory) {
                ((IInventory) existing).clear();
            }
            world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 4);
        }
        if (!world.setBlockState(pos, state, flags)) {
            return;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            data.setInteger("x", pos.getX());
            data.setInteger("y", pos.getY());
            data.setInteger("z", pos.getZ());
            tileEntity.readFromNBT(data);
            tileEntity.mirror(mirror);
            tileEntity.rotate(rotation);
            tileEntity.markDirty();
        }
    }

    private static boolean reactsToNeighbours(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        return !state.isOpaqueCube() && !state.getMaterial().isLiquid()
                && !block.isAir(state, world, pos) && !block.isLeaves(state, world, pos);
    }

    @SuppressWarnings("unchecked")
    private static List<Template.BlockInfo> blocksOf(Template template) {
        resolve();
        if (blocksField == null) {
            return null;
        }
        try {
            return (List<Template.BlockInfo>) blocksField.get(template);
        } catch (IllegalAccessException | RuntimeException exception) {
            LOGGER.warn("Cannot read template blocks, placing templates the vanilla way.", exception);
            blocksField = null;
            return null;
        }
    }

    private static boolean hasNoEntities(Template template) {
        if (entitiesField == null) {
            return false;
        }
        try {
            return ((List<?>) entitiesField.get(template)).isEmpty();
        } catch (IllegalAccessException | RuntimeException exception) {
            entitiesField = null;
            return false;
        }
    }

    private static synchronized void resolve() {
        if (resolved) {
            return;
        }
        resolved = true;
        for (Field field : Template.class.getDeclaredFields()) {
            if (field.getType() != List.class || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Type generic = field.getGenericType();
            if (!(generic instanceof ParameterizedType)) {
                continue;
            }
            field.setAccessible(true);
            if (((ParameterizedType) generic).getActualTypeArguments()[0] == Template.BlockInfo.class) {
                blocksField = field;
            } else {
                entitiesField = field;
            }
        }
        if (blocksField == null) {
            LOGGER.warn("Template block list not found, placing templates the vanilla way.");
        }
    }
}
