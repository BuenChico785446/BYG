package windanesz.byg.worldgen;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.ChunkGeneratorSettings;

import java.util.Random;

public final class DeadSeaDecorator extends BiomeDecorator {
    private static final Predicate<IBlockState> STONE = BlockMatcher.forBlock(Blocks.STONE);

    @Override
    protected void genDecorations(Biome biome, World world, Random random) {
        ChunkGeneratorSettings settings = this.chunkProviderSettings;
        this.dirtGen = vein(Blocks.DIRT.getDefaultState(), settings.dirtSize);
        this.gravelOreGen = vein(Blocks.GRAVEL.getDefaultState(), settings.gravelSize);
        this.graniteGen = vein(stone(BlockStone.EnumType.GRANITE), settings.graniteSize);
        this.dioriteGen = vein(stone(BlockStone.EnumType.DIORITE), settings.dioriteSize);
        this.andesiteGen = vein(stone(BlockStone.EnumType.ANDESITE), settings.andesiteSize);
        this.coalGen = vein(Blocks.COAL_ORE.getDefaultState(), settings.coalSize);
        this.ironGen = vein(Blocks.IRON_ORE.getDefaultState(), settings.ironSize);
        this.goldGen = vein(Blocks.GOLD_ORE.getDefaultState(), settings.goldSize);
        this.redstoneGen = vein(Blocks.REDSTONE_ORE.getDefaultState(), settings.redstoneSize);
        this.diamondGen = vein(Blocks.DIAMOND_ORE.getDefaultState(), settings.diamondSize);
        this.lapisGen = vein(Blocks.LAPIS_ORE.getDefaultState(), settings.lapisSize);
        super.genDecorations(biome, world, random);
    }

    private static FastMinable vein(IBlockState state, int size) {
        return new FastMinable(state, size, STONE);
    }

    private static IBlockState stone(BlockStone.EnumType variant) {
        return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, variant);
    }
}
