package windanesz.byg.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import windanesz.byg.BiomesYouGo;

import javax.annotation.Nullable;
import java.util.Locale;

public final class BiomeTeleportation {
    /** Cells are sampled at biome-generation resolution (4 blocks); a tile covers 64 x 64 cells. */
    private static final int TILE_CELLS = 64;
    private static final int TILE_BLOCKS = TILE_CELLS * 4;
    private static final int MAX_TILE_RADIUS = 48;

    private BiomeTeleportation() {
    }

    @Nullable
    public static ResourceLocation resolve(String input) {
        String name = input.toLowerCase(Locale.ROOT);
        String[] candidates = name.indexOf(':') >= 0 ? new String[] {name}
                : new String[] {name, BiomesYouGo.MODID + ":" + name, BiomesYouGo.MODID + ":byg_" + name};
        for (String candidate : candidates) {
            ResourceLocation id = new ResourceLocation(candidate);
            if (Biome.REGISTRY.containsKey(id)) {
                return id;
            }
        }
        return null;
    }

    public static boolean teleport(EntityPlayerMP player, Biome biome, ICommandSender feedback) {
        World world = player.world;
        feedback.sendMessage(msg(TextFormatting.GRAY, "message.byg.biome_teleporter.searching", biome.getBiomeName()));
        BlockPos target = findBiome(world, biome, player.getPosition());
        if (target == null) {
            feedback.sendMessage(msg(TextFormatting.RED, "message.byg.biome_teleporter.not_found",
                    biome.getBiomeName(), MAX_TILE_RADIUS * TILE_BLOCKS));
            return false;
        }

        // Load the surrounding chunks so decoration (trees etc.) exists before choosing the landing height.
        int chunkX = target.getX() >> 4;
        int chunkZ = target.getZ() >> 4;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.getChunk(chunkX + dx, chunkZ + dz);
            }
        }
        BlockPos ground = world.getTopSolidOrLiquidBlock(target);
        player.dismountRidingEntity();
        player.connection.setPlayerLocation(ground.getX() + 0.5, ground.getY() + 1, ground.getZ() + 0.5,
                player.rotationYaw, player.rotationPitch);
        player.fallDistance = 0;
        feedback.sendMessage(msg(TextFormatting.GREEN, "message.byg.biome_teleporter.teleported",
                biome.getBiomeName(), ground.getX(), ground.getZ()));
        return true;
    }

    private static ITextComponent msg(TextFormatting color, String key, Object... args) {
        return new TextComponentTranslation(key, args).setStyle(new Style().setColor(color));
    }

    /** Spirals outwards tile by tile and returns the matching cell closest to the tile centre, or null. */
    @Nullable
    private static BlockPos findBiome(World world, Biome wanted, BlockPos origin) {
        int originCellX = origin.getX() >> 2;
        int originCellZ = origin.getZ() >> 2;
        for (int radius = 0; radius <= MAX_TILE_RADIUS; radius++) {
            BlockPos best = null;
            long bestDistance = Long.MAX_VALUE;
            for (int tx = -radius; tx <= radius; tx++) {
                for (int tz = -radius; tz <= radius; tz++) {
                    if (Math.max(Math.abs(tx), Math.abs(tz)) != radius) {
                        continue;
                    }
                    int cellX = originCellX + tx * TILE_CELLS - TILE_CELLS / 2;
                    int cellZ = originCellZ + tz * TILE_CELLS - TILE_CELLS / 2;
                    Biome[] biomes = world.getBiomeProvider().getBiomesForGeneration(null, cellX, cellZ, TILE_CELLS, TILE_CELLS);
                    for (int i = 0; i < biomes.length; i++) {
                        if (biomes[i] != wanted) {
                            continue;
                        }
                        int x = (cellX + i % TILE_CELLS) * 4 + 2;
                        int z = (cellZ + i / TILE_CELLS) * 4 + 2;
                        long dx = x - origin.getX();
                        long dz = z - origin.getZ();
                        long distance = dx * dx + dz * dz;
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            best = new BlockPos(x, 0, z);
                        }
                    }
                }
            }
            if (best != null) {
                return best;
            }
        }
        return null;
    }
}
