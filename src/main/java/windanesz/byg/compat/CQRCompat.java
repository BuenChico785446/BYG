package windanesz.byg.compat;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import windanesz.byg.BiomesYouGo;
import windanesz.byg.Config;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class CQRCompat {
    private static final Logger LOGGER = LogManager.getLogger(BiomesYouGo.MODID);
    private static final String MOD_ID = "cqrepoured";
    private static final String CQR_PACKAGE = "team.cqr.cqrepoured.world.structure.";
    private static final int REGION_MARGIN = 8;
    private static final int UNDERGROUND_CLEARANCE = 4;
    private static final int MAX_CACHED_CHUNKS = 16384;

    private static final Map<World, Map<Long, Boolean>> DUNGEON_CHUNKS = Collections.synchronizedMap(new WeakHashMap<World, Map<Long, Boolean>>());

    private static boolean initialised;
    private static boolean available;
    private static Method dungeonAt;
    private static Method regionManager;
    private static Method regions;
    private static Method regionStart;
    private static Method regionEnd;

    private CQRCompat() {
    }

    public static boolean isNearDungeon(World world, BlockPos pos) {
        if (!Config.avoidCqrDungeons() || world.isRemote || !isAvailable()) {
            return false;
        }
        try {
            return isInsideProtectedRegion(world, pos) || isNearPredictedDungeon(world, pos);
        } catch (ReflectiveOperationException | RuntimeException e) {
            disable(e);
            return false;
        }
    }

    private static synchronized boolean isAvailable() {
        if (!initialised) {
            initialised = true;
            available = Loader.isModLoaded(MOD_ID) && bind();
        }
        return available;
    }

    private static boolean bind() {
        try {
            dungeonAt = Class.forName(CQR_PACKAGE + "generation.WorldDungeonGenerator").getMethod("getDungeonAt", World.class, int.class, int.class);
            regionManager = Class.forName(CQR_PACKAGE + "protection.ProtectedRegionManager").getMethod("getInstance", World.class);
            regions = Class.forName(CQR_PACKAGE + "protection.IProtectedRegionManager").getMethod("getProtectedRegions");
            Class<?> region = Class.forName(CQR_PACKAGE + "protection.ProtectedRegion");
            regionStart = region.getMethod("getStartPos");
            regionEnd = region.getMethod("getEndPos");
            return true;
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Chocolate Quest Repoured is installed but its API could not be found, trees will not avoid its dungeons", e);
            return false;
        }
    }

    private static synchronized void disable(Exception cause) {
        if (available) {
            available = false;
            LOGGER.warn("Disabling Chocolate Quest Repoured tree avoidance after an unexpected error", cause);
        }
    }

    private static boolean isInsideProtectedRegion(World world, BlockPos pos) throws ReflectiveOperationException {
        Object manager = regionManager.invoke(null, world);
        if (manager == null) {
            return false;
        }
        for (Object region : (Iterable<?>) regions.invoke(manager)) {
            BlockPos start = (BlockPos) regionStart.invoke(region);
            BlockPos end = (BlockPos) regionEnd.invoke(region);
            if (pos.getX() >= start.getX() - REGION_MARGIN && pos.getX() <= end.getX() + REGION_MARGIN
                    && pos.getZ() >= start.getZ() - REGION_MARGIN && pos.getZ() <= end.getZ() + REGION_MARGIN
                    && pos.getY() <= end.getY() + UNDERGROUND_CLEARANCE) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNearPredictedDungeon(World world, BlockPos pos) throws ReflectiveOperationException {
        int radius = Config.cqrAvoidanceRadiusChunks();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius && hasDungeon(world, chunkX + dx, chunkZ + dz)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasDungeon(World world, int chunkX, int chunkZ) throws ReflectiveOperationException {
        Map<Long, Boolean> cache;
        synchronized (DUNGEON_CHUNKS) {
            cache = DUNGEON_CHUNKS.get(world);
            if (cache == null) {
                cache = Collections.synchronizedMap(new HashMap<Long, Boolean>());
                DUNGEON_CHUNKS.put(world, cache);
            }
        }
        long key = ChunkPos.asLong(chunkX, chunkZ);
        Boolean cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        boolean result = dungeonAt.invoke(null, world, chunkX, chunkZ) != null;
        if (cache.size() >= MAX_CACHED_CHUNKS) {
            cache.clear();
        }
        cache.put(key, result);
        return result;
    }
}
