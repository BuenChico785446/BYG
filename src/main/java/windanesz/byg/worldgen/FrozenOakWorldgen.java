package windanesz.byg.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import windanesz.byg.Config;
import windanesz.byg.worldgen.treegenerator.DeadFrozenOakTreeGenerator;

import java.util.Random;

final class FrozenOakWorldgen {
    private static final int CHANCE_PER_MILLION = 500000;

    private FrozenOakWorldgen() {
    }

    static void generate(Random random, int chunkX, int chunkZ, World world, int dimID) {
        int chance = Config.scaleTemplateChance(CHANCE_PER_MILLION);
        if (!Config.isFeatureDimension(dimID) || world.isRemote || !Config.isWoodSetEnabled("frozen_oak") || chance <= 0 || random.nextInt(1000000) >= chance) {
            return;
        }
        int x = chunkX + random.nextInt(16);
        int z = chunkZ + random.nextInt(16);
        if (!BygWorldGenerator.matchesBiome(world, x, z, "byg:byg_frosty_forest", "byg:byg_northern_forest")) {
            return;
        }
        BlockPos ground = AncientForestFloorWorldgen.findGround(world, x, z);
        if (ground != null && BygTreePlacement.hasRoom(world, x, z, x - 2, x + 2, z - 2, z + 2)
                && BygTreePlacement.allowTrees(world, random, ground)) {
            new DeadFrozenOakTreeGenerator().generate(world, random, ground.up());
        }
    }
}
