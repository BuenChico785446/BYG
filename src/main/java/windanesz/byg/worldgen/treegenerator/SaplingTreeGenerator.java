package windanesz.byg.worldgen.treegenerator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import windanesz.byg.Config;
import windanesz.byg.blocks.BlockSappyMapleLog;
import windanesz.byg.registry.ModBlocks;
import windanesz.byg.worldgen.BygTreePlacement;

import java.util.Random;
import java.util.function.Supplier;

public final class SaplingTreeGenerator extends WorldGenAbstractTree implements TreeGrowthGenerator {

    private static final int SAPPY_LOG_CHANCE_PERCENT = 35;
    public enum TreeStyle {
        ROUND,
        TALL_ROUND,
        MAHOGANY,
        TROPICAL_MOUNTAIN,
        EBONY,
        HOLLY,
        WIZARD_HAT,
        TALL_WIZARD_HAT,
        ENCHANTED_TIERED,
        ENCHANTED_STEPPED,
        CONIFER,
        TALL_CONIFER,
        DROOPING,
        FIR,
        BAOBAB,
        BAOBAB_YOUNG,
        PALM,
        CHERRY_BLOSSOM,
        CHERRY_UMBRELLA,
        CHERRY_SPREADING,
        CHERRY_GRAND,
        CHERRY_ANCIENT,
        WILLOW,
        REDWOOD,
        MANGROVE,
        ZELKOVA,
        GREAT_OAK,
        SKYRIS,
        MAPLE,
        JACARANDA,
        JACARANDA_TALL,
        BLUE_SPRUCE,
        CIKA
    }

    private enum FirLayerShape {
        NONE,
        THIN,
        WIDE,
        SPIRE,
        BROAD,
        MEDIUM,
        SMALL_PLUS,
        CENTER
    }

    private final Supplier<IBlockState> logStateSupplier;
    private final Supplier<IBlockState> leavesStateSupplier;
    private final Supplier<IBlockState> accentLeavesStateSupplier;
    private final TreeStyle style;
    private final int minHeight;
    private final int extraHeight;

    public SaplingTreeGenerator(Supplier<IBlockState> logStateSupplier, Supplier<IBlockState> leavesStateSupplier,
                                TreeStyle style, int minHeight, int extraHeight) {
        this(logStateSupplier, leavesStateSupplier, null, style, minHeight, extraHeight);
    }

    public SaplingTreeGenerator(Supplier<IBlockState> logStateSupplier, Supplier<IBlockState> leavesStateSupplier,
                                Supplier<IBlockState> accentLeavesStateSupplier, TreeStyle style, int minHeight, int extraHeight) {
        super(false);
        this.logStateSupplier = logStateSupplier;
        this.leavesStateSupplier = leavesStateSupplier;
        this.accentLeavesStateSupplier = accentLeavesStateSupplier;
        this.style = style;
        this.minHeight = minHeight;
        this.extraHeight = extraHeight;
    }

    /**
     * Grows a tree from a 3x3 square of saplings centred on {@code center}. The saplings are cleared
     * before generation and restored if the tree cannot be placed.
     */
    public boolean growFromSaplings(World world, Random random, BlockPos center, Block sapling) {
        BlockPos[] positions = new BlockPos[9];
        IBlockState[] states = new IBlockState[9];
        int index = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = center.add(x, 0, z);
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() != sapling) {
                    return false;
                }
                positions[index] = pos;
                states[index++] = state;
            }
        }
        for (BlockPos pos : positions) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        }
        if (this.place(world, random, center)) {
            return true;
        }
        for (int i = 0; i < positions.length; i++) {
            world.setBlockState(positions[i], states[i], 2);
        }
        return false;
    }

    @Override
    public boolean growTree(World world, Random random, BlockPos position) {
        return this.place(world, random, position);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        int radius = this.getMaxLeafRadius();
        return BygTreePlacement.hasRoom(world, position.getX(), position.getZ(),
                position.getX() - radius, position.getX() + radius, position.getZ() - radius, position.getZ() + radius)
                && this.place(world, random, position);
    }

    private boolean place(World world, Random random, BlockPos position) {
        if (this.style == TreeStyle.EBONY) {
            return this.generateEbony(world, random, position);
        }
        if (this.style == TreeStyle.ZELKOVA) {
            return this.generateZelkova(world, random, position);
        }
        if (this.style == TreeStyle.GREAT_OAK) {
            return this.generateGreatOak(world, random, position);
        }
        if (this.style == TreeStyle.BAOBAB) {
            return this.generateBaobab(world, random, position);
        }
        if (this.style == TreeStyle.BAOBAB_YOUNG) {
            return this.generateYoungBaobab(world, random, position);
        }
        if (this.style == TreeStyle.SKYRIS) {
            return this.generateSkyris(world, random, position);
        }
        if (this.style == TreeStyle.MAPLE) {
            return this.generateMaple(world, random, position);
        }
        if (this.style == TreeStyle.BLUE_SPRUCE) {
            return this.generateBlueSpruce(world, random, position);
        }
        if (this.style == TreeStyle.CIKA) {
            return this.generateCika(world, random, position);
        }
        if (this.style == TreeStyle.CHERRY_UMBRELLA || this.style == TreeStyle.CHERRY_SPREADING
                || this.style == TreeStyle.CHERRY_GRAND || this.style == TreeStyle.CHERRY_ANCIENT) {
            return this.generateCherryBoughTree(world, random, position);
        }
        if (this.style == TreeStyle.PALM) {
            return this.generatePalm(world, random, position);
        }
        if (this.style == TreeStyle.TROPICAL_MOUNTAIN) {
            return this.generateTropicalMountain(world, random, position);
        }
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0);
        int maxRadius = this.getMaxLeafRadius();
        int extraTop = this.getExtraTopClearance();
        if (position.getY() < 1 || position.getY() + height + extraTop >= world.getHeight()) {
            return false;
        }
        if (this.style == TreeStyle.REDWOOD
                ? !this.hasRedwoodRoom(world, position, height, maxRadius, extraTop)
                : !this.hasRoom(world, position, height, maxRadius, extraTop)) {
            return false;
        }
        if (!this.canGrowOn(world, position.down())) {
            return false;
        }
        if (this.style != TreeStyle.HOLLY) {
            this.placeTrunk(world, position, height);
        }
        switch (this.style) {
            case CONIFER:
                this.placeConiferCanopy(world, random, position, height, 2, 4);
                break;
            case TALL_CONIFER:
                this.placeConiferCanopy(world, random, position, height, 3, 6);
                break;
            case TALL_ROUND:
                this.placeRoundCanopy(world, random, position.up(height - 2), 3, 4, false, 0);
                break;
            case MAHOGANY:
                this.placeMahoganyCanopy(world, random, position, height);
                break;
            case HOLLY:
                this.placeHollyCanopy(world, random, position, height);
                break;
            case WIZARD_HAT:
                this.placeWizardHatCanopy(world, random, position.up(height - 2), false);
                break;
            case TALL_WIZARD_HAT:
                this.placeWizardHatCanopy(world, random, position.up(height - 1), true);
                break;
            case ENCHANTED_TIERED:
                this.placeEnchantedTieredCanopy(world, random, position, height);
                break;
            case ENCHANTED_STEPPED:
                this.placeEnchantedSteppedCanopy(world, position, height);
                break;
            case DROOPING:
                this.placeRoundCanopy(world, random, position.up(height - 2), 3, 4, true, 2);
                break;
            case FIR:
                this.placeFirCanopy(world, random, position, height);
                break;
            case CHERRY_BLOSSOM:
                this.placeCherryCanopy(world, random, position.up(height - 2));
                break;
            case WILLOW:
                this.placeRoundCanopy(world, random, position.up(height - 3), 4, 5, true, 4);
                break;
            case REDWOOD:
                this.placeRedwoodBase(world, position);
                this.placeRedwoodCanopy(world, random, position, height);
                break;
            case MANGROVE:
                this.placeMangroveRoots(world, position);
                this.placeRoundCanopy(world, random, position.up(height - 2), 3, 4, true, 3);
                break;
            case JACARANDA:
                this.placeJacarandaCanopy(world, random, position, height, false);
                break;
            case JACARANDA_TALL:
                this.placeJacarandaCanopy(world, random, position, height, true);
                break;
            case ROUND:
            default:
                this.placeRoundCanopy(world, random, position.up(height - 2), 2, 3, false, 0);
                break;
        }
        return true;
    }

    private boolean generateCika(World world, Random random, BlockPos position) {
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0);
        int maxRadius = 4 + random.nextInt(2);
        int bareTrunk = Math.min(7 + random.nextInt(2), height * 2 / 5);
        if (position.getY() < 1 || position.getY() + height + 1 >= world.getHeight()
                || !world.isAreaLoaded(position.add(-maxRadius, 0, -maxRadius), position.add(maxRadius + 1, height + 1, maxRadius + 1))) {
            return false;
        }
        for (int x = 0; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
                if (!this.canGrowOn(world, position.add(x, -1, z))) {
                    return false;
                }
                for (int y = 0; y < height; y++) {
                    if (!this.isReplaceable(world, position.add(x, y, z))) {
                        return false;
                    }
                }
            }
        }
        IBlockState logState = this.logStateSupplier.get();
        IBlockState leavesState = this.leavesStateSupplier.get();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x <= 1; x++) {
                for (int z = 0; z <= 1; z++) {
                    this.placeLog(world, position.add(x, y, z), logState);
                }
            }
        }
        int crownLength = height - bareTrunk;
        // Tiers are built bottom-up: one or two bough layers, then a narrower notch layer. Thickness and
        // notch depth vary per tier, and each layer's radius varies by direction so the crown is uneven.
        int k = 0;
        while (k <= crownLength) {
            int y = position.getY() + bareTrunk + k;
            double t = (double) k / crownLength;
            int baseRadius = (int) Math.round(maxRadius * Math.pow(1.0 - t, 0.8));
            if (k >= crownLength - 3) {
                int tipRadius = k >= crownLength - 1 ? 0 : 1;
                this.placeCikaLayer(world, random, position, y, new int[]{tipRadius, tipRadius, tipRadius, tipRadius, tipRadius, tipRadius, tipRadius, tipRadius}, leavesState, false);
                if (k == crownLength) {
                    this.placeLeaf(world, new BlockPos(position.getX(), y + 1, position.getZ()), leavesState);
                }
                k++;
                continue;
            }
            int boughs = 1 + random.nextInt(2);
            for (int i = 0; i < boughs && k <= crownLength - 4; i++, k++) {
                int[] sectors = new int[8];
                for (int s = 0; s < 8; s++) {
                    int jitter = random.nextInt(4) == 0 ? -1 : random.nextInt(5) == 0 ? 1 : 0;
                    sectors[s] = Math.max(1, Math.min(maxRadius, baseRadius + jitter));
                }
                this.placeCikaLayer(world, random, position, position.getY() + bareTrunk + k, sectors, leavesState, true);
            }
            int gaps = random.nextInt(4) == 0 ? 2 : 1;
            for (int i = 0; i < gaps && k <= crownLength - 4; i++, k++) {
                int notch = 2 + random.nextInt(2);
                int[] sectors = new int[8];
                for (int s = 0; s < 8; s++) {
                    sectors[s] = Math.max(1, baseRadius - notch + (random.nextInt(3) == 0 ? 1 : 0));
                }
                this.placeCikaLayer(world, random, position, position.getY() + bareTrunk + k, sectors, leavesState, false);
            }
        }
        // Log stubs poking out of the lowest skirt.
        int stubY = position.getY() + bareTrunk;
        int[][] stubs = {{-maxRadius + 2, 0}, {maxRadius - 1, 1}};
        for (int[] stub : stubs) {
            BlockPos stubPos = new BlockPos(position.getX() + stub[0], stubY, position.getZ() + stub[1]);
            this.placeLog(world, stubPos, logState);
            this.placeLeaf(world, stubPos.up(), leavesState);
        }
        return true;
    }

    /**
     * Places one horizontal crown layer around the 2x2 trunk. {@code sectorRadii} holds the radius for each of
     * eight compass sectors; bough layers additionally droop a few outer leaves one block down.
     */
    private void placeCikaLayer(World world, Random random, BlockPos position, int y, int[] sectorRadii,
                                IBlockState leavesState, boolean bough) {
        int max = 0;
        for (int r : sectorRadii) {
            max = Math.max(max, r);
        }
        for (int x = -max - 1; x <= max + 2; x++) {
            for (int z = -max - 1; z <= max + 2; z++) {
                double dx = x - 0.5;
                double dz = z - 0.5;
                int sector = (int) ((Math.atan2(dz, dx) + Math.PI) / (2 * Math.PI) * 8) % 8;
                double reach = sectorRadii[sector] + 0.9;
                double dist2 = dx * dx + dz * dz;
                if (dist2 > reach * reach) {
                    continue;
                }
                boolean edge = dist2 > (reach - 1.0) * (reach - 1.0);
                if (edge && sectorRadii[sector] >= 3 && random.nextInt(6) == 0) {
                    continue;
                }
                BlockPos pos = new BlockPos(position.getX() + x, y, position.getZ() + z);
                this.placeLeaf(world, pos, leavesState);
                if (bough && edge && random.nextInt(3) == 0) {
                    this.placeLeaf(world, pos.down(), leavesState);
                }
            }
        }
    }

    private boolean generateBlueSpruce(World world, Random random, BlockPos position) {
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0);
        int maxRadius = height >= 15 ? 4 : 3;
        int top = height + 2;
        if (position.getY() < 1 || position.getY() + top + 1 >= world.getHeight()
                || !world.isAreaLoaded(position.add(-maxRadius, 0, -maxRadius), position.add(maxRadius, top + 1, maxRadius))
                || !this.canGrowOn(world, position.down())) {
            return false;
        }
        for (int y = 0; y <= top; y++) {
            if (!this.isReplaceable(world, position.up(y))) {
                return false;
            }
        }
        this.placeTrunk(world, position, height);
        IBlockState leavesState = this.leavesStateSupplier.get();
        int crownStart = 2 + random.nextInt(2);
        int length = top - crownStart;
        for (int k = 0; k <= length; k++) {
            // Radius widens steadily from the spire; alternating layers form
            // wide boughs with narrower gaps between them.
            double taper = k * (maxRadius + 0.6) / length;
            int radius = (int) Math.round(taper);
            if (k % 2 == 1) {
                radius = Math.max(0, radius - 1);
            }
            if (k < 2) {
                radius = 0;
            }
            int y = position.getY() + top - k;
            boolean lowest = k >= length - 1;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int dist2 = x * x + z * z;
                    if (dist2 > radius * radius + 1) {
                        continue;
                    }
                    if (radius > 1 && dist2 >= radius * radius - 1 && random.nextInt(lowest ? 2 : 4) == 0) {
                        continue;
                    }
                    this.placeLeaf(world, new BlockPos(position.getX() + x, y, position.getZ() + z), leavesState);
                }
            }
            // Boughs droop slightly at their tips.
            if (radius >= 2 && k % 2 == 0) {
                for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
                    if (random.nextInt(3) != 0) {
                        this.placeLeaf(world, new BlockPos(position.getX() + facing.getXOffset() * radius,
                                y - 1, position.getZ() + facing.getZOffset() * radius), leavesState);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Procedural palms modelled on the two authored structures: a slim, single-log palm that leans in
     * two steps and ends in a compact four-frond crown, and a thick 2x2 palm that leans diagonally in
     * three steps and ends in a broad domed crown with eight drooping fronds.
     */
    private boolean generatePalm(World world, Random random, BlockPos position) {
        boolean thick = random.nextInt(9) < 4;
        int span = thick ? 2 : 1;
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0)
                + (thick ? 1 : 0);
        int reach = thick ? 11 : 7;
        if (position.getY() < 1 || position.getY() + height + 4 >= world.getHeight()
                || !world.isAreaLoaded(position.add(-reach, 0, -reach), position.add(reach, height + 4, reach))) {
            return false;
        }
        for (int x = 0; x < span; x++) {
            for (int z = 0; z < span; z++) {
                if (!this.canGrowOn(world, position.add(x, -1, z))) {
                    return false;
                }
            }
        }

        int stepX;
        int stepZ;
        if (thick) {
            stepX = random.nextBoolean() ? 1 : -1;
            stepZ = random.nextBoolean() ? 1 : -1;
        } else {
            int direction = random.nextInt(4);
            stepX = direction == 0 ? 1 : direction == 1 ? -1 : 0;
            stepZ = direction == 2 ? 1 : direction == 3 ? -1 : 0;
        }
        int[] shiftAt = thick
                ? new int[]{height * 3 / 10, height * 11 / 20, height * 4 / 5}
                : new int[]{height * 2 / 5, height * 3 / 4};
        java.util.List<BlockPos> trunk = new java.util.ArrayList<>();
        int offsetX = 0;
        int offsetZ = 0;
        int shifts = 0;
        for (int y = 0; y < height; y++) {
            if (shifts < shiftAt.length && y >= shiftAt[shifts]) {
                offsetX += stepX;
                // The thick palm skips the middle step on Z so its lean is not perfectly diagonal.
                if (!thick || shifts != 1) {
                    offsetZ += stepZ;
                }
                shifts++;
            }
            for (int x = 0; x < span; x++) {
                for (int z = 0; z < span; z++) {
                    BlockPos pos = position.add(offsetX + x, y, offsetZ + z);
                    // Water counts as replaceable for leaves, but a palm must not stand or lean in it.
                    if (!this.isReplaceable(world, pos) || world.getBlockState(pos).getMaterial().isLiquid()) {
                        return false;
                    }
                    trunk.add(pos);
                }
            }
        }

        IBlockState log = this.logStateSupplier.get();
        for (BlockPos pos : trunk) {
            this.setBlockAndNotifyAdequately(world, pos, log);
        }
        this.placePalmFlare(world, random, position, span, log);
        this.placePalmCrown(world, random, position.add(offsetX, 0, offsetZ), height - 2, span);
        return true;
    }

    /** Roots flaring one block out around the base, up to two blocks tall. */
    private void placePalmFlare(World world, Random random, BlockPos base, int span, IBlockState log) {
        for (int x = -1; x <= span; x++) {
            for (int z = -1; z <= span; z++) {
                boolean insideX = x >= 0 && x < span;
                boolean insideZ = z >= 0 && z < span;
                // Only the cells sharing a side with the trunk footprint.
                if (insideX == insideZ || (span > 1 && random.nextInt(5) < 2)) {
                    continue;
                }
                BlockPos root = base.add(x, 0, z);
                if (!this.canGrowOn(world, root.down()) || !this.isReplaceable(world, root)) {
                    continue;
                }
                this.setBlockAndNotifyAdequately(world, root, log);
                BlockPos upper = root.up();
                if (this.isReplaceable(world, upper) && (span == 1 || random.nextBoolean())) {
                    this.setBlockAndNotifyAdequately(world, upper, log);
                }
            }
        }
    }

    /**
     * {@code crownBase} is the minimum corner of the trunk's top footprint at the tree's base Y;
     * {@code crownY} is the height of the fronds' lower layer.
     */
    private void placePalmCrown(World world, Random random, BlockPos crownBase, int crownY, int span) {
        IBlockState leaves = this.leavesStateSupplier.get();
        boolean thick = span > 1;
        BlockPos crown = crownBase.up(crownY);
        if (thick) {
            this.placePalmDisc(world, crown.up(1), span, 3, 5, leaves);
            this.placePalmDisc(world, crown.up(2), span, 3, 3, leaves);
            this.placePalmDisc(world, crown.up(3), span, 1, 1, leaves);
            // A skirt of leaves hugging the trunk just below the fronds.
            this.placePalmDisc(world, crown.down(), span, 1, 2, leaves);
        } else {
            this.placePalmDisc(world, crown.up(1), span, 1, 2, leaves);
            this.placePalmDisc(world, crown.up(2), span, 1, 1, leaves);
        }

        int[][] axes = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] axis : axes) {
            int length = thick ? 5 + random.nextInt(3) : 3 + random.nextInt(2);
            for (int lateral = 0; lateral < span; lateral++) {
                for (int r = 1; r <= length; r++) {
                    int x = axis[0] != 0 ? (axis[0] > 0 ? span - 1 : 0) + axis[0] * r : lateral;
                    int z = axis[1] != 0 ? (axis[1] > 0 ? span - 1 : 0) + axis[1] * r : lateral;
                    this.placePalmFrondBlock(world, crown.add(x, 0, z), r, length, thick ? 3 : 0, leaves);
                }
            }
        }
        int[][] diagonals = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] diagonal : diagonals) {
            int length = thick ? 3 + random.nextInt(2) : 2;
            for (int r = 1; r <= length; r++) {
                int x = (diagonal[0] > 0 ? span - 1 : 0) + diagonal[0] * r;
                int z = (diagonal[1] > 0 ? span - 1 : 0) + diagonal[1] * r;
                this.placePalmFrondBlock(world, crown.add(x, 0, z), r, length, thick ? 2 : 1, leaves);
            }
        }
    }

    /**
     * Fronds arch: the upper layer runs to one block short of the tip, and the lower layer holds the
     * drooping tip (plus, on thick palms, the underside near the trunk).
     */
    private void placePalmFrondBlock(World world, BlockPos pos, int r, int length, int underside, IBlockState leaves) {
        if (r <= length - 1) {
            this.placeLeaf(world, pos.up(), leaves);
        }
        if (r >= length - 1 || r <= underside) {
            this.placeLeaf(world, pos, leaves);
        }
    }

    /** A rounded disc of leaves around the trunk footprint, bounded by a square radius and a Manhattan sum. */
    private void placePalmDisc(World world, BlockPos origin, int span, int maxRadius, int maxSum, IBlockState leaves) {
        for (int x = -maxRadius; x <= span - 1 + maxRadius; x++) {
            for (int z = -maxRadius; z <= span - 1 + maxRadius; z++) {
                int edgeX = x < 0 ? -x : Math.max(0, x - (span - 1));
                int edgeZ = z < 0 ? -z : Math.max(0, z - (span - 1));
                if (edgeX + edgeZ <= maxSum) {
                    this.placeLeaf(world, origin.add(x, 0, z), leaves);
                }
            }
        }
    }

    private boolean generateTropicalMountain(World world, Random random, BlockPos position) {
        int height = this.minHeight + random.nextInt(this.extraHeight + 1);
        int radius = 5;
        if (position.getY() < 1 || position.getY() + height + 4 >= world.getHeight()
                || !world.isAreaLoaded(position.add(-radius, -1, -radius),
                        position.add(radius, height + 4, radius))) {
            return false;
        }
        Block soil = world.getBlockState(position.down()).getBlock();
        if (soil != windanesz.byg.registry.ModBlocks.overgrown_stone && soil != Blocks.STONE) {
            return false;
        }
        for (int y = 0; y < height; y++) {
            if (!this.isReplaceable(world, position.up(y))) {
                return false;
            }
        }
        this.placeTrunk(world, position, height);
        this.placeTropicalMountainCanopy(world, random, position, height);
        return true;
    }

    private void placeTropicalMountainCanopy(World world, Random random, BlockPos base, int height) {
        IBlockState log = this.logStateSupplier.get();
        IBlockState leaves = this.leavesStateSupplier.get();
        int shape = random.nextInt(3);
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int start = random.nextInt(directions.length);
        int branches = shape == 0 ? 3 : shape == 1 ? 4 : 5;
        for (int i = 0; i < branches; i++) {
            int[] direction = directions[(start + i * 3) % directions.length];
            int reach = direction[0] != 0 && direction[1] != 0 ? 2 : 2 + random.nextInt(2);
            int branchY = height - 5 + random.nextInt(3);
            BlockPos tip = base.add(direction[0] * reach, branchY + 2, direction[1] * reach);
            for (int step = 1; step <= reach; step++) {
                this.placeLog(world, base.add(direction[0] * step,
                        branchY + (step * 2) / reach, direction[1] * step), log);
            }
            this.placeLeafBlob(world, random, tip, shape == 0 ? 2 : 3, 1 + random.nextInt(2), leaves);
        }
        // Offset and stack rounded leaf masses so the crown has an uneven, rising outline.
        int crownX = random.nextInt(3) - 1;
        int crownZ = random.nextInt(3) - 1;
        BlockPos crown = base.add(crownX, height - 1, crownZ);
        this.placeLeafBlob(world, random, crown, shape == 2 ? 4 : 3, 2, leaves);
        this.placeLeafBlob(world, random, crown.add(-crownX, 2 + random.nextInt(2), -crownZ),
                shape == 0 ? 2 : 3, 1, leaves);
    }

    private boolean generateMaple(World world, Random random, BlockPos position) {
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0);
        if (position.getY() < 1 || position.getY() + height + 8 >= world.getHeight()
                || !this.canGrowOn(world, position.down())
                || !this.hasMapleRoom(world, position, height)) {
            return false;
        }

        IBlockState log = this.logStateSupplier.get();
        IBlockState leaves = this.leavesStateSupplier.get();
        // Four crown silhouettes: small airy, wide spreading, flat layered limbs and a dense round mass.
        int roll = random.nextInt(10);
        int variant = roll < 3 ? 0 : roll < 6 ? 1 : roll < 8 ? 2 : 3;
        int trunkHeight = variant == 2 ? height + 1 : height;
        this.placeTrunk(world, position, trunkHeight);
        this.placeSappyMapleLog(world, random, position, log);
        if (height >= 9 && random.nextInt(3) != 0) {
            int firstAxis = random.nextInt(2);
            int firstReach = 1 + random.nextInt(2);
            this.placeMapleBaseSupports(world, position, log, firstAxis, firstReach);
            if (random.nextBoolean()) {
                this.placeMapleBaseSupports(world, position, log, 1 - firstAxis, 3 - firstReach);
            }
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int offset = random.nextInt(directions.length);
        int limbs;
        switch (variant) {
            case 0:
                limbs = 4;
                break;
            case 1:
                limbs = 5 + random.nextInt(2);
                break;
            case 2:
                limbs = 4;
                break;
            default:
                limbs = 3;
                break;
        }
        for (int i = 0; i < limbs; i++) {
            // Layered limbs keep to the four cardinal directions so they read as a cross of branches.
            int[] direction = variant == 2 ? directions[i] : directions[(i * 2 + offset + i / 4) % directions.length];
            int reach;
            int branchY;
            int rise;
            switch (variant) {
                case 0:
                    reach = 2 + random.nextInt(2);
                    branchY = height - 2 - random.nextInt(2);
                    rise = 2;
                    break;
                case 1:
                    reach = 3 + random.nextInt(2);
                    branchY = height - 3 + random.nextInt(2);
                    rise = 1 + random.nextInt(3);
                    break;
                case 2:
                    reach = 3 + random.nextInt(2);
                    branchY = height - 1;
                    rise = 0;
                    break;
                default:
                    reach = 2;
                    branchY = height - 1 - random.nextInt(2);
                    rise = 2;
                    break;
            }
            for (int step = 1; step <= reach; step++) {
                BlockPos limb = position.add(direction[0] * step, branchY + (step * rise) / reach, direction[1] * step);
                this.placeLog(world, limb, log);
                if (variant == 2 && step >= 2) {
                    this.placeMapleLeaves(world, random, limb.up(), 3, 1, 0.6f, leaves);
                }
            }
            BlockPos tip = position.add(direction[0] * reach, branchY + rise, direction[1] * reach);
            switch (variant) {
                case 0:
                    this.placeMapleLeaves(world, random, tip.up(), 2, 2, 0.7f, leaves);
                    break;
                case 1:
                    this.placeMapleLeaves(world, random, tip.up(), 3, 2, 0.65f, leaves);
                    break;
                case 2:
                    this.placeMapleLeaves(world, random, tip.up(), 3, 2, 0.7f, leaves);
                    break;
                default:
                    this.placeMapleLeaves(world, random, tip.up(), 3, 2, 0.85f, leaves);
                    break;
            }
        }
        BlockPos top = position.up(trunkHeight);
        switch (variant) {
            case 0:
                this.placeMapleLeaves(world, random, top, 2, 2, 0.75f, leaves);
                break;
            case 1:
                this.placeMapleLeaves(world, random, top.up(), 3, 2, 0.7f, leaves);
                break;
            case 2:
                this.placeMapleLeaves(world, random, top.up(), 3, 2, 0.75f, leaves);
                this.placeMapleLeaves(world, random, top.up(3), 2, 1, 0.7f, leaves);
                break;
            default:
                this.placeMapleLeaves(world, random, top.up(), 4, 3, 0.9f, leaves);
                break;
        }
        return true;
    }

    /**
     * An airy leaf mass with random holes, ported from the old maple NBT trees. Every leaf stays within
     * four steps of the centre so it is not immediately decayed away from the limb it is centred on.
     */
    private void placeMapleLeaves(World world, Random random, BlockPos center, int radius, int verticalRadius,
                                  float density, IBlockState leaves) {
        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double shape = (x * x + z * z) / (double) (radius * radius + 1)
                            + (y * y) / (double) (verticalRadius * verticalRadius + 1);
                    if (shape > 1.0 || Math.abs(x) + Math.abs(y) + Math.abs(z) > radius + 1) {
                        continue;
                    }
                    if (random.nextFloat() > density) {
                        continue;
                    }
                    this.placeLeaf(world, center.add(x, y, z), leaves);
                }
            }
        }
    }

    private void placeSappyMapleLog(World world, Random random, BlockPos position, IBlockState log) {
        // A few maples weep sap near the base, on a side that is open so a tap can be hung there.
        if (log.getBlock() != ModBlocks.maple_log || random.nextInt(100) >= SAPPY_LOG_CHANCE_PERCENT) {
            return;
        }
        BlockPos logPos = position.up(1 + random.nextInt(3));
        int startSide = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            EnumFacing side = EnumFacing.byHorizontalIndex((startSide + i) % 4);
            if (world.isAirBlock(logPos.offset(side))) {
                this.setBlockAndNotifyAdequately(world, logPos,
                        ModBlocks.sappy_maple_log.getDefaultState().withProperty(BlockSappyMapleLog.SAP_SIDE, side));
                return;
            }
        }
    }

    private void placeMapleBaseSupports(World world, BlockPos position, IBlockState log, int axis, int reach) {
        for (int side : new int[]{-1, 1}) {
            for (int step = 1; step <= reach; step++) {
                BlockPos support = axis == 0 ? position.add(side * step, 0, 0)
                        : position.add(0, 0, side * step);
                if (!this.canGrowOn(world, support.down()) || !this.isReplaceable(world, support)) {
                    break;
                }
                this.placeLog(world, support, log);
            }
        }
    }

    private boolean hasMapleRoom(World world, BlockPos position, int height) {
        // Crowns may overlap, but a clear trunk and a loaded canopy footprint are required.
        if (!world.isAreaLoaded(position.add(-7, 0, -7), position.add(7, height + 8, 7))) {
            return false;
        }
        for (int y = 0; y < height; y++) {
            if (!this.isReplaceable(world, position.up(y))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Skyris trees: a thin trunk that drifts sideways a block or two, threaded through flat leaf
     * discs (a wide disc with a diamond cap) at irregular heights, with the odd green apple leaf.
     */
    private boolean generateSkyris(World world, Random random, BlockPos position) {
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0);
        int maxReach = 8;
        if (position.getY() < 1 || position.getY() + height + 3 >= world.getHeight()
                || !this.canGrowOnSquare(world, position.down(), 1)
                || !world.isAreaLoaded(position.add(-maxReach, 0, -maxReach),
                        position.add(maxReach, height + 3, maxReach))) {
            return false;
        }

        // Trunk column for every layer; drift is capped so the crown stays inside the loaded area.
        int[] columnX = new int[height];
        int[] columnZ = new int[height];
        boolean[] shiftLayer = new boolean[height];
        EnumFacing[] shiftDirection = new EnumFacing[height];
        int x = 0;
        int z = 0;
        int sinceShift = 0;
        for (int y = 0; y < height; y++) {
            sinceShift++;
            if (y >= 3 && y < height - 2 && sinceShift >= 3 && random.nextInt(3) == 0) {
                EnumFacing direction = EnumFacing.byHorizontalIndex(random.nextInt(4));
                int nextX = x + direction.getXOffset();
                int nextZ = z + direction.getZOffset();
                if (Math.abs(nextX) <= 3 && Math.abs(nextZ) <= 3) {
                    shiftLayer[y] = true;
                    shiftDirection[y] = direction;
                    x = nextX;
                    z = nextZ;
                    sinceShift = 0;
                }
            }
            columnX[y] = x;
            columnZ[y] = z;
        }
        for (int y = 0; y < height; y++) {
            if (!this.isReplaceable(world, position.add(columnX[y], y, columnZ[y]))) {
                return false;
            }
        }

        IBlockState logState = this.logStateSupplier.get();
        for (int y = 0; y < height; y++) {
            if (shiftLayer[y]) {
                EnumFacing direction = shiftDirection[y];
                IBlockState horizontal = this.withLogAxis(logState, direction.getAxis());
                this.placeLog(world, position.add(columnX[y] - direction.getXOffset(), y, columnZ[y] - direction.getZOffset()), horizontal);
                this.placeLog(world, position.add(columnX[y], y, columnZ[y]), horizontal);
            } else {
                this.placeLog(world, position.add(columnX[y], y, columnZ[y]), logState);
            }
        }

        // Discs from the crown down, spaced 4-5 layers apart; the last one is optionally a small bulb.
        int discY = height - 1;
        int discIndex = 0;
        while (discY >= 3) {
            int radius = discIndex == 0 ? 3 + random.nextInt(2) : (discY < 6 ? 2 + random.nextInt(2) : 3 + random.nextInt(2));
            BlockPos center = position.add(columnX[discY], discY, columnZ[discY]);
            this.placeSkyrisDisc(world, random, center, radius);
            discIndex++;
            discY -= 4 + random.nextInt(2);
            if (discIndex >= 3) {
                break;
            }
        }
        return true;
    }

    private IBlockState withLogAxis(IBlockState logState, EnumFacing.Axis axis) {
        if (!logState.getPropertyKeys().contains(BlockDirectional.FACING)) {
            return logState;
        }
        // BlockDirectionalLogBase: SOUTH = vertical, UP = X axis, EAST = Z axis.
        return logState.withProperty(BlockDirectional.FACING, axis == EnumFacing.Axis.X ? EnumFacing.UP : EnumFacing.EAST);
    }

    private void placeSkyrisDisc(World world, Random random, BlockPos center, int radius) {
        IBlockState leaves = this.leavesStateSupplier.get();
        IBlockState fruit = Config.isSkyrisContentEnabled() ? ModBlocks.skyris_leaves_green_apple.getDefaultState() : leaves;
        int maxDistance = radius <= 2 ? radius * radius + 1 : radius * radius + radius + 1;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= maxDistance) {
                    this.placeLeaf(world, center.add(dx, 0, dz), random.nextInt(25) == 0 ? fruit : leaves);
                }
            }
        }
        int capRadius = radius - 1;
        for (int dx = -capRadius; dx <= capRadius; dx++) {
            for (int dz = -capRadius; dz <= capRadius; dz++) {
                if (Math.abs(dx) + Math.abs(dz) <= capRadius) {
                    this.placeLeaf(world, center.add(dx, 1, dz), leaves);
                }
            }
        }
    }

    private boolean generateZelkova(World world, Random random, BlockPos position) {
        int trunkHeight = 7 + random.nextInt(3);
        int crownDepth = 6 + random.nextInt(3);
        int crownBase = trunkHeight - 1;
        int crownTop = crownBase + crownDepth;
        int crownRadius = 2 + random.nextInt(2);
        if (position.getY() < 1 || position.getY() + crownTop + 2 >= world.getHeight()) {
            return false;
        }
        if (!this.canGrowOn(world, position.down()) || !this.hasRoom(world, position, crownTop + 1, crownRadius + 2, 2)) {
            return false;
        }
        IBlockState logState = this.logStateSupplier.get();
        IBlockState leavesState = this.leavesStateSupplier.get();
        for (int y = 0; y <= crownTop; y++) {
            this.placeLog(world, position.up(y), logState);
        }
        int midLayer = crownDepth / 2;
        for (int layer = 0; layer <= crownDepth; layer++) {
            int radius = crownRadius;
            int distanceFromMid = Math.abs(layer - midLayer);
            if (distanceFromMid >= midLayer) {
                radius = 1;
            } else if (distanceFromMid >= Math.max(1, midLayer - 1)) {
                radius = Math.max(1, crownRadius - 1);
            }
            this.placeDiamondCanopyLayer(world, random, position.up(crownBase + layer), radius, leavesState, true);
            if (layer > 1 && layer < crownDepth - 1 && radius >= 2 && random.nextInt(3) == 0) {
                int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                int[] direction = directions[random.nextInt(directions.length)];
                this.placeZelkovaBranch(world, random, position.up(crownBase + layer), direction[0], direction[1],
                        1 + random.nextInt(2), logState, leavesState);
            }
        }
        this.placeLeafBlob(world, random, position.up(crownTop + 1), 1, 0, leavesState);
        return true;
    }

    private boolean generateGreatOak(World world, Random random, BlockPos position) {
        int trunkHeight = 8 + random.nextInt(5);
        int crownRadius = 5 + random.nextInt(4);
        int totalHeight = trunkHeight + 6;
        if (position.getY() < 1 || position.getY() + totalHeight + 2 >= world.getHeight()) {
            return false;
        }
        // Branches reach at most five blocks from the trunk, with a three-block leaf cluster.
        // Eight blocks also fits the normal biome decoration offset.
        if (!this.canGrowOnSquare(world, position.down(), 1) || !this.hasRoom(world, position, totalHeight + 1, 8, 2)) {
            return false;
        }
        IBlockState logState = this.logStateSupplier.get();
        IBlockState leavesState = this.leavesStateSupplier.get();
        this.placeGreatOakRootFlare(world, position, logState);
        this.placeSquareTrunk(world, position, trunkHeight, 1, logState);
        this.placeLog(world, position.up(trunkHeight), logState);
        this.placeLog(world, position.up(trunkHeight + 1), logState);
        this.placeGreatOakCrown(world, random, position.up(trunkHeight - 1), crownRadius, leavesState);
        int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        boolean[] used = new boolean[directions.length];
        int branchCount = 3 + random.nextInt(5);
        for (int i = 0; i < branchCount; i++) {
            int index = random.nextInt(directions.length);
            while (used[index]) {
                index = (index + 1) % directions.length;
            }
            used[index] = true;
            int[] direction = directions[index];
            int branchStart = trunkHeight - 3 + random.nextInt(3);
            this.placeGreatOakBranch(world, random, position.up(branchStart), direction[0], direction[1],
                    3 + random.nextInt(3), 1 + random.nextInt(2), logState, leavesState);
        }
        this.placeLeafBlob(world, random, position.up(totalHeight), 2 + random.nextInt(2), 1, leavesState);
        return true;
    }

    private boolean canGrowOnSquare(World world, BlockPos soilPos, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (!this.canGrowOn(world, soilPos.add(x, 0, z))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasRoom(World world, BlockPos position, int height, int radius, int extraTop) {
        // World#getBlockState can populate a neighboring chunk during decoration.
        // Reject trees whose clearance area is not already available.
        if (!world.isAreaLoaded(position.add(-radius, 0, -radius),
                position.add(radius, height + extraTop, radius))) {
            return false;
        }
        for (int y = 0; y <= height + extraTop; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = position.add(x, y, z);
                    if (checkPos.getY() < 0 || checkPos.getY() >= world.getHeight()) {
                        return false;
                    }
                    if (!this.isReplaceable(world, checkPos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean generateEbony(World world, Random random, BlockPos position) {
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0);
        int splitVariant = random.nextInt(3);
        if (splitVariant != 0) {
            height -= 2;
        }
        int canopyVariant = random.nextInt(5); // One compact, two broad, two sprawling crowns.
        int canopyRadius = canopyVariant == 0 ? 5 : canopyVariant <= 2 ? 6 : 7;
        if (position.getY() < 1 || position.getY() + height + 4 >= world.getHeight()
                || !this.canGrowOn(world, position.down())
                || !this.hasEbonyRoom(world, position, height, canopyRadius)) {
            return false;
        }

        IBlockState log = this.logStateSupplier.get();
        IBlockState leaves = this.leavesStateSupplier.get();
        this.placeTrunk(world, position, height);

        // Root pillars make a broken 3 or 5 block wide footprint while
        // the trunk above them stays one block wide.
        int baseRadius = 1 + random.nextInt(2);
        for (int x = -baseRadius; x <= baseRadius; x++) {
            for (int z = -baseRadius; z <= baseRadius; z++) {
                int distance = Math.max(Math.abs(x), Math.abs(z));
                if (distance == 0 || (distance > 1 && random.nextInt(3) != 0)
                        || (distance == 1 && random.nextInt(5) == 0)
                        || !this.canConnectEbonyBase(world, position, x, z)) {
                    continue;
                }
                // Connect every pillar to the central trunk before building it.
                for (int step = 1; step <= Math.abs(x); step++) {
                    this.placeLog(world, position.add(Integer.signum(x) * step, 0, 0), log);
                }
                for (int step = 1; step <= Math.abs(z); step++) {
                    this.placeLog(world, position.add(x, 0, Integer.signum(z) * step), log);
                }
                int pillarHeight = 1 + random.nextInt(Math.max(1, 4 - distance));
                for (int y = 0; y < pillarHeight; y++) {
                    this.placeLog(world, position.add(x, y, z), log);
                }
            }
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        // Forked trees have shorter trunks and separate crowns on sloping limbs.
        if (splitVariant != 0) {
            int firstDirection = random.nextInt(directions.length);
            int stemCount = splitVariant == 1 ? 2 : 3;
            int forkY = random.nextBoolean() ? 2 + random.nextInt(2) : height - 5;
            for (int stem = 0; stem < stemCount; stem++) {
                int directionIndex = (firstDirection + stem * (stemCount == 2 ? 1 : 3)) % directions.length;
                int[] direction = directions[directionIndex];
                int reach = forkY <= 3 ? 4 + random.nextInt(2) : 3 + random.nextInt(2);
                for (int step = 1; step <= reach; step++) {
                    int rise = (step * 2) / 3;
                    if (step > 1 && rise > ((step - 1) * 2) / 3) {
                        this.placeLog(world, position.add(direction[0] * (step - 1), forkY + rise,
                                direction[1] * (step - 1)), log);
                    }
                    this.placeLog(world, position.add(direction[0] * step, forkY + rise,
                            direction[1] * step), log);
                }
                BlockPos tip = position.add(direction[0] * reach, forkY + (reach * 2) / 3,
                        direction[1] * reach);
                this.placeLeafBlob(world, random, tip.up(), 2, 2, leaves);
            }
        }
        for (int[] direction : directions) {
            if (canopyVariant == 0 && random.nextInt(3) == 0) {
                continue;
            }
            int branchY = height - (canopyVariant == 0 ? 3 : 4) + random.nextInt(2);
            int reach = canopyVariant == 0 ? 2 + random.nextInt(2)
                    : canopyVariant <= 2 ? 3 : 4;
            for (int step = 1; step <= reach; step++) {
                int rise = step / 2;
                if (step > 1 && rise > (step - 1) / 2) {
                    this.placeLog(world, position.add(direction[0] * (step - 1), branchY + rise,
                            direction[1] * (step - 1)), log);
                }
                this.placeLog(world, position.add(direction[0] * step, branchY + rise,
                        direction[1] * step), log);
            }
            this.placeLeafBlob(world, random,
                    position.add(direction[0] * reach, branchY + reach / 2 + 1,
                            direction[1] * reach), canopyVariant == 0 ? 2 : 3, 2, leaves);
        }
        this.placeLeafBlob(world, random, position.up(height), 3, 2, leaves);
        if (canopyVariant > 0) {
            this.placeLeafBlob(world, random, position.up(height - 2), 3, 1, leaves);
            this.placeLeafBlob(world, random, position.up(height + 2), 2, 1, leaves);
            int[][] diagonals = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] direction : diagonals) {
                if (canopyVariant <= 2 && random.nextBoolean()) {
                    continue;
                }
                int branchY = height - 3 + random.nextInt(2);
                int reach = canopyVariant <= 2 ? 2 : 3;
                for (int step = 1; step <= reach; step++) {
                    this.placeLog(world, position.add(direction[0] * step, branchY,
                            direction[1] * (step - 1)), log);
                    this.placeLog(world, position.add(direction[0] * step, branchY,
                            direction[1] * step), log);
                }
                this.placeLeafBlob(world, random,
                        position.add(direction[0] * reach, branchY + 1,
                                direction[1] * reach), 2, 2, leaves);
            }
        }
        return true;
    }

    private boolean hasRedwoodRoom(World world, BlockPos position, int height, int radius, int extraTop) {
        if (!world.isAreaLoaded(position.add(-radius, 0, -radius),
                position.add(radius, height + extraTop, radius))) {
            return false;
        }
        for (int y = 0; y < height; y++) {
            if (!this.isReplaceable(world, position.up(y))) {
                return false;
            }
        }
        return true;
    }

    private boolean hasEbonyRoom(World world, BlockPos position, int height, int canopyRadius) {
        if (!world.isAreaLoaded(position.add(-canopyRadius, 0, -canopyRadius),
                position.add(canopyRadius, height + 4, canopyRadius))) {
            return false;
        }
        // Crowns can overlap in a dense forest; only the trunk must have clear space.
        for (int y = 0; y < height; y++) {
            if (!this.isReplaceable(world, position.up(y))) {
                return false;
            }
        }
        return true;
    }

    private boolean canConnectEbonyBase(World world, BlockPos position, int x, int z) {
        for (int step = 1; step <= Math.abs(x); step++) {
            if (!this.canGrowOn(world, position.add(Integer.signum(x) * step, -1, 0))) {
                return false;
            }
        }
        for (int step = 1; step <= Math.abs(z); step++) {
            if (!this.canGrowOn(world, position.add(x, -1, Integer.signum(z) * step))) {
                return false;
            }
        }
        return true;
    }

    private boolean canGrowOn(World world, BlockPos soilPos) {
        if (!world.isBlockLoaded(soilPos)) {
            return false;
        }
        IBlockState state = world.getBlockState(soilPos);
        if (this.style == TreeStyle.ENCHANTED_TIERED || this.style == TreeStyle.ENCHANTED_STEPPED) {
            return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT;
        }
        Material material = state.getMaterial();
        if (this.style == TreeStyle.SKYRIS && material == Material.WOOD) {
            // Skyris trunks poke through neighbouring crowns; never grow on top of another tree's log.
            return false;
        }
        if (this.style == TreeStyle.GREAT_OAK) {
            // Great oak branches overhang water and neighbouring ground. A tree rooted on one of those
            // branch logs would float, so only real soil counts.
            return material == Material.GROUND || material == Material.GRASS || material == Material.SAND
                    || material == Material.CLAY;
        }
        return material == Material.GROUND || material == Material.GRASS || material == Material.SAND
                || material == Material.CLAY || material == Material.WOOD;
    }

    private void placeTrunk(World world, BlockPos position, int height) {
        IBlockState logState = this.logStateSupplier.get();
        for (int y = 0; y < height; y++) {
            this.setBlockAndNotifyAdequately(world, position.up(y), logState);
        }
    }

    private void placeSquareTrunk(World world, BlockPos center, int height, int radius, IBlockState logState) {
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (y >= height - 2 && Math.abs(x) == radius && Math.abs(z) == radius) {
                        continue;
                    }
                    this.placeLog(world, center.add(x, y, z), logState);
                }
            }
        }
    }

    private void placeGreatOakRootFlare(World world, BlockPos center, IBlockState logState) {
        this.placeGreatOakRoot(world, center.north(), logState);
        this.placeGreatOakRoot(world, center.south(), logState);
        this.placeGreatOakRoot(world, center.east(), logState);
        this.placeGreatOakRoot(world, center.west(), logState);
        this.placeGreatOakRoot(world, center.north(2), logState);
        this.placeGreatOakRoot(world, center.south(2), logState);
        this.placeGreatOakRoot(world, center.east(2), logState);
        this.placeGreatOakRoot(world, center.west(2), logState);
        this.placeGreatOakRoot(world, center.north().east(), logState);
        this.placeGreatOakRoot(world, center.north().west(), logState);
        this.placeGreatOakRoot(world, center.south().east(), logState);
        this.placeGreatOakRoot(world, center.south().west(), logState);
    }

    /** Roots outside the 3x3 soil check must still sit on soil, otherwise they hang over water or cliffs. */
    private void placeGreatOakRoot(World world, BlockPos pos, IBlockState logState) {
        if (this.canGrowOn(world, pos.down())) {
            this.placeLog(world, pos, logState);
        }
    }

    private void placeZelkovaBranch(World world, Random random, BlockPos start, int dx, int dz, int length,
                                    IBlockState logState, IBlockState leavesState) {
        BlockPos branchTip = start;
        for (int step = 1; step <= length; step++) {
            branchTip = start.add(dx * step, step / 2, dz * step);
            this.placeLog(world, branchTip, logState);
        }
        this.placeLeafBlob(world, random, branchTip.up(), 1 + random.nextInt(2), 1, leavesState);
    }

    private void placeGreatOakCrown(World world, Random random, BlockPos crownBase, int radius, IBlockState leavesState) {
        BlockPos crownCenter = crownBase.up(3);
        this.placeLeafBlob(world, random, crownCenter.down(), Math.max(3, radius - 1), 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter, radius, 2, leavesState);
        this.placeLeafBlob(world, random, crownCenter.up(2), Math.max(2, radius - 2), 1, leavesState);
        int spread = Math.max(2, radius - 3);
        this.placeLeafBlob(world, random, crownCenter.north(3), spread, 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter.south(3), spread, 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter.east(3), spread, 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter.west(3), spread, 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter.north(2).east(2), spread - 1, 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter.north(2).west(2), spread - 1, 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter.south(2).east(2), spread - 1, 1, leavesState);
        this.placeLeafBlob(world, random, crownCenter.south(2).west(2), spread - 1, 1, leavesState);
    }

    private void placeGreatOakBranch(World world, Random random, BlockPos start, int dx, int dz, int length, int rise,
                                     IBlockState logState, IBlockState leavesState) {
        BlockPos branchTip = start;
        for (int step = 1; step <= length; step++) {
            int y = (step * rise) / Math.max(1, length);
            branchTip = start.add(dx * step, y, dz * step);
            this.placeLog(world, branchTip, logState);
            if (step < length && random.nextBoolean()) {
                this.placeLeafBlob(world, random, branchTip, 1, 0, leavesState);
            }
        }
        this.placeLeafBlob(world, random, branchTip.up(), 2 + random.nextInt(2), 1, leavesState);
    }

    private void placeDiamondCanopyLayer(World world, Random random, BlockPos center, int radius, IBlockState leavesState,
                                         boolean trimCorners) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int chebyshev = Math.max(Math.abs(x), Math.abs(z));
                int manhattan = Math.abs(x) + Math.abs(z);
                if (chebyshev > radius || manhattan > radius + Math.max(1, radius / 2)) {
                    continue;
                }
                if (trimCorners && Math.abs(x) == radius && Math.abs(z) == radius && random.nextBoolean()) {
                    continue;
                }
                this.placeLeaf(world, center.add(x, 0, z), leavesState);
            }
        }
    }

    private void placeLeafBlob(World world, Random random, BlockPos center, int radius, int verticalRadius, IBlockState leavesState) {
        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            int layerRadius = Math.max(1, radius - Math.max(0, Math.abs(y) - 1));
            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    int distance = x * x + z * z;
                    int maxDistance = layerRadius * layerRadius + random.nextInt(2);
                    if (distance > maxDistance) {
                        continue;
                    }
                    if (Math.abs(x) == layerRadius && Math.abs(z) == layerRadius && random.nextInt(3) == 0) {
                        continue;
                    }
                    this.placeLeaf(world, center.add(x, y, z), leavesState);
                }
            }
        }
    }

    private void placeRoundCanopy(World world, Random random, BlockPos center, int radius, int depth, boolean drooping, int hangingDepth) {
        IBlockState leavesState = this.leavesStateSupplier.get();
        for (int y = 0; y <= depth; y++) {
            int layerRadius = Math.max(1, radius - (y / 2));
            int layerY = center.getY() + (depth - y);
            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    int distance = Math.abs(x) + Math.abs(z);
                    if (distance > layerRadius + random.nextInt(2)) {
                        continue;
                    }
                    BlockPos leafPos = new BlockPos(center.getX() + x, layerY, center.getZ() + z);
                    this.placeLeaf(world, leafPos, leavesState);
                    if (drooping && y >= depth - 1 && (Math.abs(x) == layerRadius || Math.abs(z) == layerRadius)) {
                        this.placeHangingLeaves(world, random, leafPos.down(), leavesState, hangingDepth);
                    }
                }
            }
        }
    }

    /**
     * Broad umbrella crown: a short forked trunk carrying several wide leaf discs. The tall variant keeps the
     * same silhouette but rises more steeply and uses rounder, deeper discs instead of flattened ones.
     */
    private void placeJacarandaCanopy(World world, Random random, BlockPos position, int height, boolean tall) {
        IBlockState leavesState = this.leavesStateSupplier.get();
        IBlockState logState = this.logStateSupplier.get();
        BlockPos top = position.up(height - 1);
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int start = random.nextInt(directions.length);
        int branches = 4 + random.nextInt(2);
        for (int i = 0; i < branches; i++) {
            int[] direction = directions[(start + i * 2) % directions.length];
            int length = 2 + random.nextInt(2);
            BlockPos tip = top;
            for (int step = 1; step <= length; step++) {
                tip = top.add(direction[0] * step, tall ? step : step / 2, direction[1] * step);
                this.placeLog(world, tip, logState);
            }
            this.placeJacarandaDisc(world, random, tip.up(), 3, leavesState, tall);
        }
        this.placeLog(world, top.up(), logState);
        if (tall) {
            this.placeLog(world, top.up(2), logState);
        }
        this.placeJacarandaDisc(world, random, top.up(tall ? 3 : 2), 4, leavesState, tall);
    }

    private void placeJacarandaDisc(World world, Random random, BlockPos center, int radius, IBlockState leavesState, boolean tall) {
        int depth = tall ? 2 : 1;
        for (int y = -depth; y <= depth; y++) {
            int layerRadius = tall && Math.abs(y) == 1 ? radius : radius - Math.abs(y);
            if (layerRadius < 1) {
                continue;
            }
            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    if (x * x + z * z > layerRadius * layerRadius + 1) {
                        continue;
                    }
                    if (y != 0 && random.nextInt(3) == 0) {
                        continue;
                    }
                    this.placeLeaf(world, center.add(x, y, z), leavesState);
                }
            }
        }
    }

    private void placeHollyCanopy(World world, Random random, BlockPos position, int height) {
        IBlockState leavesState = this.leavesStateSupplier.get();
        boolean matureTree = height >= 16;
        int canopyStart = matureTree && random.nextBoolean() ? 5 + random.nextInt(2) : (matureTree ? 3 : 1);
        int baseRadius = matureTree ? 5 : 3;
        int canopyHeight = height + 2 - canopyStart;
        this.placeTrunk(world, position, canopyStart + 3 + random.nextInt(2));
        for (int layer = canopyStart; layer <= height + 1; layer++) {
            int crownLayer = layer - canopyStart;
            int radius = Math.max(1, baseRadius - (crownLayer * baseRadius / canopyHeight));
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int distance = x * x + z * z;
                    if (distance > radius * radius + random.nextInt(2)) {
                        continue;
                    }
                    IBlockState leafState = this.accentLeavesStateSupplier != null && random.nextInt(24) == 0
                            ? this.accentLeavesStateSupplier.get() : leavesState;
                    this.placeLeaf(world, position.add(x, layer, z), leafState);
                }
            }
        }
        if (random.nextInt(4) != 0) {
            this.placeLeaf(world, position.up(height + 2), leavesState);
        }
    }

    private void placeConiferCanopy(World world, Random random, BlockPos position, int height, int maxRadius, int depth) {
        IBlockState leavesState = this.leavesStateSupplier.get();
        BlockPos canopyTop = position.up(height);
        for (int layer = 0; layer <= depth; layer++) {
            int radius = Math.max(1, Math.min(maxRadius, 1 + (layer / 2)));
            int y = canopyTop.getY() - layer;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int edgeDistance = Math.abs(x) + Math.abs(z);
                    if (edgeDistance > radius + (layer > 1 ? 0 : 1)) {
                        continue;
                    }
                    if (random.nextInt(5) == 0 && edgeDistance == radius * 2) {
                        continue;
                    }
                    this.placeLeaf(world, new BlockPos(position.getX() + x, y, position.getZ() + z), leavesState);
                }
            }
        }
    }

    private void placeFirCanopy(World world, Random random, BlockPos position, int height) {
        IBlockState leavesState = this.leavesStateSupplier.get();
        boolean tall = height >= 18;
        int foliageStart = tall ? 8 : 6;
        int layerCount = tall ? 17 : 12;
        for (int i = 0; i < layerCount; i++) {
            FirLayerShape layer = this.getFirLayerShape(tall, i);
            if (layer == FirLayerShape.NONE) {
                continue;
            }
            BlockPos layerCenter = position.up(foliageStart + i);
            this.placeFirLayer(world, random, layerCenter, leavesState, layer, tall, i);
            if ((layer == FirLayerShape.WIDE || layer == FirLayerShape.BROAD || layer == FirLayerShape.MEDIUM)
                    && i > 0 && i < layerCount - 3) {
                this.placeFirSkirt(world, random, layerCenter, leavesState);
            }
        }
    }

    private FirLayerShape getFirLayerShape(boolean tall, int index) {
        switch (index) {
            case 0:
                return FirLayerShape.THIN;
            case 1:
                return FirLayerShape.WIDE;
            case 2:
                return tall ? FirLayerShape.SPIRE : FirLayerShape.THIN;
            case 3:
                return tall ? FirLayerShape.WIDE : FirLayerShape.BROAD;
            case 4:
                return FirLayerShape.THIN;
            case 5:
                return tall ? FirLayerShape.WIDE : FirLayerShape.MEDIUM;
            case 6:
                return tall ? FirLayerShape.SPIRE : FirLayerShape.THIN;
            case 7:
                return tall ? FirLayerShape.WIDE : FirLayerShape.MEDIUM;
            case 8:
                return tall ? FirLayerShape.SPIRE : FirLayerShape.NONE;
            case 9:
                return tall ? FirLayerShape.BROAD : FirLayerShape.SMALL_PLUS;
            case 10:
                return tall ? FirLayerShape.THIN : FirLayerShape.CENTER;
            case 11:
                return tall ? FirLayerShape.MEDIUM : FirLayerShape.CENTER;
            case 12:
                return FirLayerShape.SMALL_PLUS;
            case 13:
                return FirLayerShape.CENTER;
            case 14:
                return FirLayerShape.SMALL_PLUS;
            case 15:
            case 16:
                return FirLayerShape.CENTER;
            default:
                return FirLayerShape.NONE;
        }
    }

    private void placeFirLayer(World world, Random random, BlockPos center, IBlockState leavesState,
                               FirLayerShape shape, boolean tall, int index) {
        switch (shape) {
            case THIN:
                this.placeFirCross(world, center, leavesState, 1, false);
                break;
            case WIDE:
                this.placeFirDisc(world, random, center, leavesState, 3, 1, true, true,
                        tall && index > 2 && index < 8 && random.nextInt(4) == 0);
                break;
            case SPIRE:
                this.placeFirDisc(world, random, center, leavesState, 2, 0, true, false, false);
                break;
            case BROAD:
                this.placeFirDisc(world, random, center, leavesState, 3, 0, true, false, false);
                this.placeFirCross(world, center, leavesState, 3, false);
                break;
            case MEDIUM:
                this.placeFirDisc(world, random, center, leavesState, 2, 1, random.nextInt(5) == 0, false, false);
                break;
            case SMALL_PLUS:
                this.placeFirCross(world, center, leavesState, 1, true);
                break;
            case CENTER:
                this.placeLeaf(world, center, leavesState);
                break;
            case NONE:
            default:
                break;
        }
    }

    private void placeFirCross(World world, BlockPos center, IBlockState leavesState, int armLength, boolean includeCenter) {
        if (includeCenter) {
            this.placeLeaf(world, center, leavesState);
        }
        for (int step = 1; step <= armLength; step++) {
            this.placeLeaf(world, center.north(step), leavesState);
            this.placeLeaf(world, center.south(step), leavesState);
            this.placeLeaf(world, center.east(step), leavesState);
            this.placeLeaf(world, center.west(step), leavesState);
        }
    }

    private void placeFirDisc(World world, Random random, BlockPos center, IBlockState leavesState, int radius, int spread,
                              boolean hollowCenter, boolean notchCorners, boolean breakEdge) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int chebyshev = Math.max(Math.abs(x), Math.abs(z));
                int manhattan = Math.abs(x) + Math.abs(z);
                if (chebyshev > radius || manhattan > radius + spread) {
                    continue;
                }
                if (hollowCenter && x == 0 && z == 0) {
                    continue;
                }
                if (notchCorners && Math.abs(x) == radius && Math.abs(z) == radius) {
                    continue;
                }
                if (breakEdge && manhattan >= radius + spread && random.nextInt(3) == 0) {
                    continue;
                }
                this.placeLeaf(world, center.add(x, 0, z), leavesState);
            }
        }
    }

    private void placeFirSkirt(World world, Random random, BlockPos center, IBlockState leavesState) {
        if (random.nextBoolean()) {
            this.placeLeaf(world, center.north().down(), leavesState);
        }
        if (random.nextBoolean()) {
            this.placeLeaf(world, center.south().down(), leavesState);
        }
        if (random.nextBoolean()) {
            this.placeLeaf(world, center.east().down(), leavesState);
        }
        if (random.nextBoolean()) {
            this.placeLeaf(world, center.west().down(), leavesState);
        }
    }

    private boolean generateBaobab(World world, Random random, BlockPos position) {
        int height = Math.max(12, this.minHeight + 5) + random.nextInt(this.extraHeight + 3);
        if (position.getY() < 1 || position.getY() + height + 4 >= world.getHeight()
                || !this.canGrowOnSquare(world, position.down(), 1)
                || !this.hasRoom(world, position, height + 3, 9, 0)) {
            return false;
        }

        IBlockState log = this.logStateSupplier.get();
        IBlockState leaves = this.leavesStateSupplier.get();
        // A solid, slightly tapered bottle trunk, with a low buttressed base.
        for (int y = 0; y < height; y++) {
            int radius = y < height - 3 ? 1 : 0;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    this.placeLog(world, position.add(x, y, z), log);
                }
            }
        }
        for (int direction = 0; direction < 4; direction++) {
            int dx = direction == 0 ? 1 : direction == 1 ? -1 : 0;
            int dz = direction == 2 ? 1 : direction == 3 ? -1 : 0;
            BlockPos root = position.add(dx * 2, 0, dz * 2);
            if (this.canGrowOn(world, root.down())) {
                this.placeLog(world, root, log);
                this.placeLog(world, root.up(), log);
            }
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}};
        BlockPos[] tips = new BlockPos[directions.length];
        int[] crownRadii = new int[directions.length];
        int rotation = random.nextInt(4);
        for (int i = 0; i < directions.length; i++) {
            int dx = directions[i][0];
            int dz = directions[i][1];
            for (int turn = 0; turn < rotation; turn++) {
                int next = -dz;
                dz = dx;
                dx = next;
            }
            int length = 4 + random.nextInt(3);
            int startY = height - 4 + random.nextInt(3);
            BlockPos tip = position.add(dx * length, startY + 2, dz * length);
            for (int step = 1; step <= length; step++) {
                int previousRise = (step - 1) * 2 / length;
                int rise = step * 2 / length;
                if (dx != 0 && dz != 0) {
                    this.placeLog(world, position.add(dx * step, startY + previousRise, dz * (step - 1)), log);
                }
                this.placeLog(world, position.add(dx * step, startY + previousRise, dz * step), log);
                this.placeLog(world, position.add(dx * step, startY + rise, dz * step), log);
            }
            tips[i] = tip;
            crownRadii[i] = 2 + random.nextInt(2);
        }
        for (int i = 0; i < tips.length; i++) {
            this.placeBaobabLeafCluster(world, random, tips[i], crownRadii[i], leaves);
        }
        this.placeBaobabLeafCluster(world, random, position.up(height), 2, leaves);
        return true;
    }

    private void placeRedwoodCanopy(World world, Random random, BlockPos position, int height) {
        int shape = random.nextInt(3);
        if (shape == 0) {
            // A compact, high crown leaves most of the trunk exposed.
            this.placeConiferCanopy(world, random, position, height, 3, 6);
        } else if (shape == 1) {
            // A broad crown reaches farther down the trunk.
            this.placeConiferCanopy(world, random, position, height, 4, 11);
        } else {
            // Separated boughs give older redwoods a stepped silhouette.
            IBlockState leaves = this.leavesStateSupplier.get();
            for (int layer = 0; layer <= 9; layer++) {
                if (layer % 3 == 2) {
                    continue;
                }
                int radius = Math.min(4, 1 + layer / 3);
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.abs(x) + Math.abs(z) <= radius + 1) {
                            this.placeLeaf(world, position.add(x, height - layer, z), leaves);
                        }
                    }
                }
            }
        }
    }

    private boolean generateYoungBaobab(World world, Random random, BlockPos position) {
        int height = 9 + random.nextInt(3);
        if (position.getY() < 1 || position.getY() + height + 3 >= world.getHeight()
                || !this.canGrowOn(world, position.down())
                || !this.hasRoom(world, position, height + 2, 5, 0)) {
            return false;
        }
        IBlockState log = this.logStateSupplier.get();
        IBlockState leaves = this.leavesStateSupplier.get();
        BlockPos[] tips = new BlockPos[4];
        for (int y = 0; y < height; y++) {
            this.placeLog(world, position.up(y), log);
        }
        int rotation = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            int direction = (i + rotation) % 4;
            int dx = direction == 0 ? 1 : direction == 1 ? -1 : 0;
            int dz = direction == 2 ? 1 : direction == 3 ? -1 : 0;
            int branchY = height - 2 - (i % 2);
            int length = 2 + random.nextInt(2);
            for (int step = 1; step <= length; step++) {
                this.placeLog(world, position.add(dx * step, branchY + (step - 1) / 2, dz * step), log);
                this.placeLog(world, position.add(dx * step, branchY + step / 2, dz * step), log);
            }
            tips[i] = position.add(dx * length, branchY + length / 2, dz * length);
        }
        for (BlockPos tip : tips) {
            this.placeBaobabLeafCluster(world, random, tip, 2, leaves);
        }
        this.placeBaobabLeafCluster(world, random, position.up(height), 2, leaves);
        return true;
    }

    private void placeBaobabLeafCluster(World world, Random random, BlockPos center, int radius, IBlockState leaves) {
        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        int firstGap = random.nextInt(directions.length);
        int secondGap = (firstGap + 2 + random.nextInt(5)) % directions.length;
        int gapDistance = radius - 1;
        for (int y = -1; y <= 1; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int firstX = x - directions[firstGap][0] * gapDistance;
                    int firstZ = z - directions[firstGap][1] * gapDistance;
                    int secondX = x - directions[secondGap][0] * gapDistance;
                    int secondZ = z - directions[secondGap][1] * gapDistance;
                    int distance = x * x + z * z;
                    if (distance > radius * radius + (y == 0 ? 1 : -2)
                            || firstX * firstX + firstZ * firstZ <= 1
                            || (radius > 2 && secondX * secondX + secondZ * secondZ <= 1)
                            || (y < 0 && random.nextInt(3) == 0)
                            || (distance >= radius * radius - 1 && random.nextInt(4) == 0)) {
                        continue;
                    }
                    this.placeLeaf(world, center.add(x, y, z), leaves);
                }
            }
        }
    }

    private void placeCherryCanopy(World world, Random random, BlockPos center) {
        IBlockState leavesState = this.leavesStateSupplier.get();
        IBlockState logState = this.logStateSupplier.get();
        this.placeCherryLeafCluster(world, random, center.up(), leavesState, 2);
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] direction : directions) {
            int length = 2 + random.nextInt(2);
            int rise = random.nextInt(2);
            for (int step = 1; step <= length; step++) {
                this.placeLog(world, center.add(direction[0] * step,
                        step == length ? rise : 0, direction[1] * step), logState);
            }
            BlockPos tip = center.add(direction[0] * length, rise, direction[1] * length);
            this.placeCherryLeafCluster(world, random, tip, leavesState, 2);
        }
    }

    private void placeCherryLeafCluster(World world, Random random, BlockPos center,
                                        IBlockState leavesState, int radius) {
        for (int y = -1; y <= 2; y++) {
            int layerRadius = y == 2 ? 1 : radius;
            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    if (x * x + z * z > layerRadius * layerRadius + (y == 0 ? 1 : 0)
                            || (y == -1 && random.nextInt(4) == 0)) {
                        continue;
                    }
                    this.placeLeaf(world, center.add(x, y, z), leavesState);
                }
            }
        }
    }

    /**
     * Procedural replacement for the old cherry_tree1..7 templates. Every variant is a trunk carrying tiers of
     * log boughs, each ending in a leaf lobe, plus a few large lobes filling the middle of the crown. Grand and
     * ancient trees use a 2x2 trunk; spreading trees occasionally do too.
     */
    private boolean generateCherryBoughTree(World world, Random random, BlockPos position) {
        int height = this.minHeight + (this.extraHeight > 0 ? random.nextInt(this.extraHeight + 1) : 0);
        boolean thick = this.style == TreeStyle.CHERRY_GRAND || this.style == TreeStyle.CHERRY_ANCIENT
                || (this.style == TreeStyle.CHERRY_SPREADING && random.nextInt(3) == 0);
        int footprint = thick ? 1 : 0;
        // Bough tiers: {y, cardinal length, diagonal length, rise, lobe radius}. Central lobes: {y, radius}.
        int[][] tiers;
        int[][] centralLobes;
        int reach;
        switch (this.style) {
            case CHERRY_UMBRELLA:
                tiers = new int[][]{{height - 3, 3, 2, 1, 3}};
                centralLobes = new int[][]{{height - 1, 4}, {height + 1, 2}};
                reach = 8;
                break;
            case CHERRY_SPREADING:
                tiers = new int[][]{{height - 2, 0, 3, 3, 3}};
                centralLobes = new int[][]{{height + 1, 3}};
                reach = 8;
                break;
            case CHERRY_GRAND:
                tiers = new int[][]{{height - 8, 4, 3, 2, 3}, {height - 3, 0, 3, 1, 3}};
                centralLobes = new int[][]{{height - 5, 5}, {height, 4}, {height + 3, 2}};
                reach = 9;
                break;
            default:
                tiers = new int[][]{{height - 14, 5, 4, 2, 3}, {height - 9, 4, 3, 1, 3}, {height - 2, 0, 2, 1, 3}};
                centralLobes = new int[][]{{height - 12, 5}, {height - 7, 5}, {height, 4}, {height + 3, 2}};
                reach = 10;
                break;
        }
        int topClearance = 6;
        if (position.getY() < 1 || position.getY() + height + topClearance >= world.getHeight()
                || !world.isAreaLoaded(position.add(-reach, 0, -reach),
                position.add(reach + footprint, height + topClearance, reach + footprint))) {
            return false;
        }
        for (int x = 0; x <= footprint; x++) {
            for (int z = 0; z <= footprint; z++) {
                if (!this.canGrowOn(world, position.add(x, -1, z))) {
                    return false;
                }
                for (int y = 0; y < height; y++) {
                    if (!this.isReplaceable(world, position.add(x, y, z))) {
                        return false;
                    }
                }
            }
        }
        IBlockState logState = this.logStateSupplier.get();
        IBlockState leavesState = this.leavesStateSupplier.get();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x <= footprint; x++) {
                for (int z = 0; z <= footprint; z++) {
                    this.placeLog(world, position.add(x, y, z), logState);
                }
            }
        }
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        for (int[] tier : tiers) {
            for (int d = 0; d < directions.length; d++) {
                int length = d < 4 ? tier[1] : tier[2];
                if (length <= 0) {
                    continue;
                }
                length += random.nextInt(2);
                int[] dir = directions[d];
                BlockPos origin = position.add(dir[0] > 0 ? footprint : 0, tier[0], dir[1] > 0 ? footprint : 0);
                for (int step = 1; step <= length; step++) {
                    this.placeLog(world, origin.add(dir[0] * step, step * tier[3] / length, dir[1] * step), logState);
                }
                this.placeCherryLobe(world, random,
                        origin.add(dir[0] * length, tier[3] + 1, dir[1] * length), tier[4], leavesState);
            }
        }
        for (int[] lobe : centralLobes) {
            this.placeCherryLobe(world, random, position.add(footprint > 0 && random.nextBoolean() ? 1 : 0, lobe[0],
                    footprint > 0 && random.nextBoolean() ? 1 : 0), lobe[1], leavesState);
        }
        return true;
    }

    private void placeCherryLobe(World world, Random random, BlockPos center, int radius, IBlockState leavesState) {
        int vertical = Math.max(2, (int) Math.round(radius * 0.75));
        double horizontalReach = (radius + 0.5) * (radius + 0.5);
        double verticalReach = (vertical + 0.5) * (vertical + 0.5);
        for (int y = -vertical; y <= vertical; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double shell = (x * x + z * z) / horizontalReach + y * y / verticalReach;
                    if (shell > 1.0 || (shell > 0.7 && random.nextInt(4) == 0)) {
                        continue;
                    }
                    this.placeLeaf(world, center.add(x, y, z), leavesState);
                }
            }
        }
    }

    private void placeWizardHatCanopy(World world, Random random, BlockPos center, boolean tall) {
        IBlockState leavesState = this.leavesStateSupplier.get();
        int brimRadius = tall ? 4 : 3;
        int coneHeight = tall ? 6 : 4;
        int tipHeight = tall ? 3 : 2;
        int tipOffsetX = random.nextInt(3) - 1;
        int tipOffsetZ = random.nextInt(3) - 1;

        for (int y = -1; y <= 0; y++) {
            int radius = y == -1 ? brimRadius : Math.max(1, brimRadius - 1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int manhattan = Math.abs(x) + Math.abs(z);
                    if (manhattan > radius + 1) {
                        continue;
                    }
                    if (manhattan >= radius && random.nextInt(4) == 0) {
                        continue;
                    }
                    this.placeLeaf(world, center.add(x, y, z), leavesState);
                }
            }
        }

        for (int level = 0; level <= coneHeight; level++) {
            int radius = Math.max(1, (brimRadius - 1) - level / 2);
            int offsetX = level >= coneHeight - 1 ? tipOffsetX : 0;
            int offsetZ = level >= coneHeight - 1 ? tipOffsetZ : 0;
            BlockPos layerCenter = center.add(offsetX, level + 1, offsetZ);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int chebyshev = Math.max(Math.abs(x), Math.abs(z));
                    if (chebyshev > radius) {
                        continue;
                    }
                    if (chebyshev == radius && random.nextInt(5) == 0) {
                        continue;
                    }
                    this.placeLeaf(world, layerCenter.add(x, 0, z), leavesState);
                }
            }
        }

        BlockPos tipBase = center.add(tipOffsetX, coneHeight + 2, tipOffsetZ);
        for (int i = 0; i < tipHeight; i++) {
            this.placeLeaf(world, tipBase.up(i), leavesState);
        }
    }

    private void placeEnchantedTieredCanopy(World world, Random random, BlockPos base, int height) {
        IBlockState leaves = this.leavesStateSupplier.get();
        IBlockState log = this.logStateSupplier.get();
        for (int tier = 0; tier < 4; tier++) {
            int y = height - 11 + tier * 3;
            int radius = 4 - tier;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int distance = Math.max(Math.abs(x), Math.abs(z));
                    if (distance == 0 || Math.abs(x) + Math.abs(z) > radius + 1) {
                        continue;
                    }
                    int lift = distance >= radius - 1 ? 1 : 0;
                    if ((x == 0 || z == 0) && distance < radius) {
                        this.placeLog(world, base.add(x, y + lift, z), log);
                    }
                    this.placeLeaf(world, base.add(x, y + lift + 1, z), leaves);
                    if (distance < radius && random.nextInt(5) != 0) {
                        this.placeLeaf(world, base.add(x, y + lift, z), leaves);
                    }
                    if (distance < radius - 1 && random.nextInt(3) == 0) {
                        this.placeLeaf(world, base.add(x, y - 1, z), leaves);
                    }
                }
            }
            this.placeLeaf(world, base.up(y + 2), leaves);
            this.placeLeaf(world, base.up(y + 3), leaves);
        }
        for (int y = 0; y < 5; y++) {
            int radius = y == 0 ? 2 : y < 3 ? 1 : 0;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) + Math.abs(z) <= radius + 1) {
                        this.placeLeaf(world, base.add(x, height + y, z), leaves);
                    }
                }
            }
        }
    }

    private void placeEnchantedSteppedCanopy(World world, BlockPos base, int height) {
        IBlockState leaves = this.leavesStateSupplier.get();
        int[] radii = {2, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0};
        for (int layer = 0; layer < radii.length; layer++) {
            int radius = radii[layer];
            int y = height - 8 + layer;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (radius > 1 && Math.abs(x) == radius && Math.abs(z) == radius) {
                        continue;
                    }
                    this.placeLeaf(world, base.add(x, y, z), leaves);
                }
            }
        }
    }

    private void placeMahoganyCanopy(World world, Random random, BlockPos base, int height) {
        IBlockState leaves = this.leavesStateSupplier.get();
        IBlockState log = this.logStateSupplier.get();
        // A wide, shallow crown above smaller platforms carried by side branches.
        for (int layer = 0; layer < 3; layer++) {
            int radius = layer == 1 ? 5 : 4;
            int y = height - 3 + layer;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius && Math.abs(z) == radius && random.nextInt(3) != 0) {
                        continue;
                    }
                    this.placeLeaf(world, base.add(x, y, z), leaves);
                }
            }
        }
        for (int direction = 0; direction < 4; direction++) {
            int dx = direction == 0 ? 1 : direction == 1 ? -1 : 0;
            int dz = direction == 2 ? 1 : direction == 3 ? -1 : 0;
            int y = height - 5 - random.nextInt(2);
            for (int step = 1; step <= 3; step++) {
                this.placeLog(world, base.add(dx * step, y, dz * step), log);
            }
            BlockPos branch = base.add(dx * 3, y + 1, dz * 3);
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x) == 2 && Math.abs(z) == 2 && random.nextBoolean()) {
                        continue;
                    }
                    this.placeLeaf(world, branch.add(x, 0, z), leaves);
                    if (Math.abs(x) < 2 && Math.abs(z) < 2) {
                        this.placeLeaf(world, branch.add(x, 1, z), leaves);
                    }
                }
            }
        }
    }

    private void placeRedwoodBase(World world, BlockPos position) {
        IBlockState logState = this.logStateSupplier.get();
        this.placeLog(world, position.north(), logState);
        this.placeLog(world, position.south(), logState);
        this.placeLog(world, position.east(), logState);
        this.placeLog(world, position.west(), logState);
    }

    private void placeMangroveRoots(World world, BlockPos position) {
        IBlockState logState = this.logStateSupplier.get();
        this.placeLog(world, position.north(), logState);
        this.placeLog(world, position.south(), logState);
        this.placeLog(world, position.east(), logState);
        this.placeLog(world, position.west(), logState);
        this.placeLog(world, position.north().up(), logState);
        this.placeLog(world, position.south().up(), logState);
    }

    private void placeHangingLeaves(World world, Random random, BlockPos start, IBlockState leavesState, int maxDepth) {
        int depth = 1 + random.nextInt(maxDepth);
        for (int i = 0; i < depth; i++) {
            BlockPos pos = start.down(i);
            if (!this.isReplaceable(world, pos)) {
                return;
            }
            this.setBlockAndNotifyAdequately(world, pos, leavesState);
        }
    }

    private void placeLeaf(World world, BlockPos pos, IBlockState leavesState) {
        if (this.isReplaceable(world, pos)) {
            this.setBlockAndNotifyAdequately(world, pos, leavesState);
        }
    }

    private void placeLog(World world, BlockPos pos, IBlockState logState) {
        if (this.isReplaceable(world, pos)) {
            this.setBlockAndNotifyAdequately(world, pos, logState);
        }
    }

    private int getMaxLeafRadius() {
        switch (this.style) {
            case CHERRY_BLOSSOM:
                return 5;
            case JACARANDA:
            case JACARANDA_TALL:
                return 6;
            case MAHOGANY:
                return 5;
            case WILLOW:
            case REDWOOD:
            case MANGROVE:
            case ZELKOVA:
            case GREAT_OAK:
                return 4;
            case FIR:
                return 3;
            case TALL_WIZARD_HAT:
                return 4;
            case ENCHANTED_TIERED:
                return 4;
            case ENCHANTED_STEPPED:
                return 3;
            case WIZARD_HAT:
            case TALL_ROUND:
            case DROOPING:
                return 3;
            case HOLLY:
                return 5;
            case BAOBAB:
                return 4;
            case TALL_CONIFER:
                return 3;
            case ROUND:
            case CONIFER:
            default:
                return 2;
        }
    }

    private int getExtraTopClearance() {
        switch (this.style) {
            case WILLOW:
            case REDWOOD:
            case MANGROVE:
                return 5;
            case FIR:
                return 5;
            case TALL_WIZARD_HAT:
                return 5;
            case ENCHANTED_TIERED:
                return 5;
            case ENCHANTED_STEPPED:
                return 2;
            case WIZARD_HAT:
                return 4;
            case DROOPING:
            case CHERRY_BLOSSOM:
            case ZELKOVA:
            case GREAT_OAK:
            case HOLLY:
                return 4;
            default:
                return 2;
        }
    }

    @Override
    public boolean isReplaceable(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        return this.canReplace(state, world, pos);
    }

    private boolean canReplace(IBlockState state, IBlockAccess world, BlockPos pos) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block.isAir(state, world, pos)
                || block.isLeaves(state, world, pos)
                || material == Material.PLANTS
                || material == Material.VINE
                || material == Material.SNOW
                || block.isReplaceable(world, pos)
                || block.canBeReplacedByLeaves(state, world, pos)
                || block instanceof BlockBush
                || block == Blocks.TALLGRASS
                || block == Blocks.DOUBLE_PLANT;
    }
}
