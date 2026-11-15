package windanesz.byg.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import windanesz.byg.Config;
import windanesz.byg.registry.ModItems;
import windanesz.byg.registry.ModSounds;

import java.util.ArrayList;
import java.util.List;

public class EntityKiwiBird extends EntityAnimal {

    private static final DataParameter<Boolean> IS_SLEEPING =
            EntityDataManager.createKey(EntityKiwiBird.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_FORAGING =
            EntityDataManager.createKey(EntityKiwiBird.class, DataSerializers.BOOLEAN);

    private static final ResourceLocation AWAKE_TEXTURE = new ResourceLocation("byg:textures/entity/kiwi.png");
    private static final ResourceLocation SLEEPING_TEXTURE = new ResourceLocation("byg:textures/entity/kiwi_sleeping.png");

    private BlockPos burrowPos = null;
    private int timeUntilNextEgg;

    public EntityKiwiBird(World world) {
        super(world);
        this.setSize(0.4f, 0.7f);
        this.experienceValue = 5;
        this.isImmuneToFire = false;
        this.setNoAI(false);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.6D));
        if (ModItems.salal_berry != null) {
            this.tasks.addTask(2, new EntityAITempt(this, 1.1D, ModItems.salal_berry, false));
        }
        if (ModItems.worm != null) {
            this.tasks.addTask(2, new EntityAITempt(this, 1.1D, ModItems.worm, false));
        }
        this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(4, new EntityAIKiwiSleep(this));
        this.tasks.addTask(5, new EntityAIKiwiDigBurrow(this));
        this.tasks.addTask(6, new EntityAIKiwiForage(this));
        this.tasks.addTask(7, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(8, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.timeUntilNextEgg = nextEggDelay();
    }

    private int nextEggDelay() {
        int interval = Config.getKiwiEggLayInterval();
        return interval + this.rand.nextInt(interval);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.isEntityAlive() && !this.isChild() && ModItems.kiwi_egg != null && --this.timeUntilNextEgg <= 0) {
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(ModItems.kiwi_egg, 1);
            this.timeUntilNextEgg = nextEggDelay();
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(IS_SLEEPING, false);
        this.dataManager.register(IS_FORAGING, false);
    }

    public boolean isSleeping() { return this.dataManager.get(IS_SLEEPING); }
    public boolean isForaging() { return this.dataManager.get(IS_FORAGING); }
    void setSleeping(boolean v) { this.dataManager.set(IS_SLEEPING, v); }
    void setForaging(boolean v) { this.dataManager.set(IS_FORAGING, v); }

    public BlockPos getBurrowPos() { return burrowPos; }
    public void setBurrowPos(BlockPos pos) { this.burrowPos = pos; }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("EggLayTime")) {
            this.timeUntilNextEgg = compound.getInteger("EggLayTime");
        }
        if (compound.hasKey("BurrowX")) {
            this.burrowPos = new BlockPos(
                compound.getInteger("BurrowX"),
                compound.getInteger("BurrowY"),
                compound.getInteger("BurrowZ")
            );
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("EggLayTime", this.timeUntilNextEgg);
        if (this.burrowPos != null) {
            compound.setInteger("BurrowX", this.burrowPos.getX());
            compound.setInteger("BurrowY", this.burrowPos.getY());
            compound.setInteger("BurrowZ", this.burrowPos.getZ());
        }
    }

    // Find a suitable burrow location: flat 3x3 area with diggable blocks
    public BlockPos findBurrowLocation() {
        BlockPos start = this.getPosition();
        for (int attempt = 0; attempt < 20; attempt++) {
            int xOff = this.rand.nextInt(16) - 8;
            int zOff = this.rand.nextInt(16) - 8;
            BlockPos candidate = start.add(xOff, 0, zOff);
            
            // Find ground level
            while (candidate.getY() > 0 && this.world.isAirBlock(candidate)) {
                candidate = candidate.down();
            }
            candidate = candidate.up(); // Stand on top of ground
            
            if (isValidBurrowSite(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isValidBurrowSite(BlockPos pos) {
        // Check if 3x3 area is flat and made of diggable material
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = pos.add(x, -1, z);
                IBlockState state = this.world.getBlockState(checkPos);
                Block block = state.getBlock();
                
                // Must be solid ground (dirt, grass, etc.)
                if (!state.getMaterial().isSolid() || state.getMaterial() == Material.ROCK) {
                    return false;
                }
                
                // Check that position above ground is air or replaceable
                if (!this.world.isAirBlock(checkPos.up()) && !this.world.getBlockState(checkPos.up()).getBlock().isReplaceable(this.world, checkPos.up())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void init(FMLInitializationEvent event) {
        int spawnWeight = Config.getKiwiSpawnWeight();
        if (spawnWeight == 0 || !Config.isWoodSetEnabled("kiwi_bird")) {
            return;
        }
        List<ResourceLocation> biomeIds = new ArrayList<>();
        for (String configuredBiome : Config.getKiwiSpawnBiomes()) {
            if (configuredBiome == null || configuredBiome.trim().isEmpty()) {
                continue;
            }
            String id = configuredBiome.trim();
            if (id.indexOf(':') < 0) {
                id = "byg:" + id;
            }
            biomeIds.add(new ResourceLocation(id));
        }
        if (biomeIds.isEmpty()) {
            return;
        }
        EntitySpawnHelper.addSpawnIfBiomesPresent(EntityKiwiBird.class, spawnWeight, 3, 4, EnumCreatureType.CREATURE,
                biomeIds.toArray(new ResourceLocation[0]));
    }

    @SideOnly(Side.CLIENT)
    public static void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityKiwiBird.class, renderManager -> new RenderLiving(renderManager, new ModelKiwi(), 0.5f) {
            @Override
            protected ResourceLocation getEntityTexture(Entity entity) {
                return entity instanceof EntityKiwiBird && ((EntityKiwiBird) entity).isSleeping() ? SLEEPING_TEXTURE : AWAKE_TEXTURE;
            }

            @Override
            protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
                if (entitylivingbaseIn.isChild()) {
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    this.shadowSize = 0.25F;
                } else {
                    this.shadowSize = 0.5F;
                }
            }
        });
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() == ModItems.salal_berry || stack.getItem() == ModItems.worm);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new EntityKiwiBird(this.world);
    }

    @Override
    protected Item getDropItem() {
        return Items.FEATHER;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        int meat = Config.getKiwiMeatDropCount();
        if (meat > 0) {
            meat += this.rand.nextInt(lootingModifier + 1);
            Item drop = this.isBurning() ? ModItems.kiwi_cooked : ModItems.kiwi_raw;
            if (drop != null) {
                this.dropItem(drop, meat);
            }
        }
    }

    @Override
    public net.minecraft.util.SoundEvent getAmbientSound() {
        // Kiwis are nocturnal - silent during day
        long time = this.world.getWorldTime() % 24000;
        boolean isNight = time >= 13000 && time < 23000;
        if (!isNight) return null;
        
        return ModSounds.KIWI_IDLE;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ModSounds.KIWI_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return ModSounds.KIWI_HURT;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.0D);
    }

    // Sleep AI: active during day, pathfinds to burrow or sleeps in place
    static class EntityAIKiwiSleep extends EntityAIBase {
        private final EntityKiwiBird kiwi;
        private int sleepTimer;

        EntityAIKiwiSleep(EntityKiwiBird kiwi) {
            this.kiwi = kiwi;
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (kiwi.isInWater()) return false;
            long time = kiwi.world.getWorldTime() % 24000;
            boolean isDay = time >= 0 && time < 13000;
            return isDay;
        }

        @Override
        public void startExecuting() {
            this.sleepTimer = 0;
            kiwi.getNavigator().clearPath();
            
            // If has burrow, pathfind to it
            BlockPos burrow = kiwi.getBurrowPos();
            if (burrow != null && kiwi.world.isBlockLoaded(burrow)) {
                // Check burrow still exists
                if (isBurrowValid(burrow)) {
                    kiwi.getNavigator().tryMoveToXYZ(burrow.getX() + 0.5, burrow.getY(), burrow.getZ() + 0.5, 1.0);
                } else {
                    kiwi.setBurrowPos(null); // Burrow destroyed
                }
            }
            
            kiwi.setSleeping(true);
        }

        @Override
        public boolean shouldContinueExecuting() {
            long time = kiwi.world.getWorldTime() % 24000;
            boolean isDay = time >= 0 && time < 13000;
            return isDay && !kiwi.isInWater();
        }

        @Override
        public void updateTask() {
            this.sleepTimer++;
            kiwi.getNavigator().clearPath();
        }

        @Override
        public void resetTask() {
            kiwi.setSleeping(false);
        }

        private boolean isBurrowValid(BlockPos burrow) {
            // Check if the burrow blocks are still air (not filled in)
            return kiwi.world.isAirBlock(burrow.down()) && kiwi.world.isAirBlock(burrow.down().east());
        }
    }

    // Dig Burrow AI: runs occasionally, finds location and digs stair pattern
    static class EntityAIKiwiDigBurrow extends EntityAIBase {
        private final EntityKiwiBird kiwi;
        private BlockPos targetPos;
        private int digProgress;

        EntityAIKiwiDigBurrow(EntityKiwiBird kiwi) {
            this.kiwi = kiwi;
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (kiwi.isChild()) return false;
            // Only dig at night, randomly, if no burrow exists
            if (kiwi.isInWater()) return false;
            if (kiwi.getBurrowPos() != null) return false;
            
            long time = kiwi.world.getWorldTime() % 24000;
            boolean isNight = time >= 13000 && time < 23000;
            if (!isNight) return false;
            
            // Random trigger: ~1/600 chance per tick when conditions met
            if (kiwi.getRNG().nextInt(600) != 0) return false;
            
            // Find a burrow location
            this.targetPos = kiwi.findBurrowLocation();
            return this.targetPos != null;
        }

        @Override
        public void startExecuting() {
            this.digProgress = 0;
            kiwi.getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.0);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.digProgress < 60 && this.targetPos != null;
        }

        @Override
        public void updateTask() {
            this.digProgress++;
            
            // Move toward burrow site
            if (kiwi.getDistanceSq(targetPos) > 4.0) {
                kiwi.getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.0);
                return;
            }
            
            // Close enough - dig the burrow
            if (this.digProgress == 30 && !kiwi.world.isRemote) {
                digBurrow(targetPos);
                kiwi.setBurrowPos(targetPos);
            }
        }

        @Override
        public void resetTask() {
            this.targetPos = null;
            this.digProgress = 0;
        }

        private void digBurrow(BlockPos start) {
            // Pattern: DDX
            //          XDD
            // start is the top-left position
            // Layer 1 (surface level): dig start and start.east()
            kiwi.world.setBlockState(start.down(), Blocks.AIR.getDefaultState(), 3);
            kiwi.world.setBlockState(start.down().east(), Blocks.AIR.getDefaultState(), 3);
            
            // Layer 2 (one down): dig start.south() and start.south().east()
            kiwi.world.setBlockState(start.down(2).south(), Blocks.AIR.getDefaultState(), 3);
            kiwi.world.setBlockState(start.down(2).south().east(), Blocks.AIR.getDefaultState(), 3);
        }
    }

    // Forage AI: active at night, random head-bobbing with worm drops
    static class EntityAIKiwiForage extends EntityAIBase {
        private final EntityKiwiBird kiwi;
        private int duration;

        EntityAIKiwiForage(EntityKiwiBird kiwi) {
            this.kiwi = kiwi;
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (kiwi.isChild()) return false;
            if (kiwi.isInWater()) return false;
            long time = kiwi.world.getWorldTime() % 24000;
            boolean isNight = time >= 13000 && time < 23000;
            return isNight && kiwi.getRNG().nextInt(Config.getKiwiForageAttemptInterval()) == 0;
        }

        @Override
        public void startExecuting() {
            this.duration = 40 + kiwi.getRNG().nextInt(40);
            kiwi.getNavigator().clearPath();
            kiwi.setForaging(true);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.duration > 0;
        }

        @Override
        public void updateTask() {
            this.duration--;
            kiwi.getNavigator().clearPath();
            
            // Drop worm when foraging completes naturally
            if (this.duration == 0 && !kiwi.world.isRemote) {
                if (kiwi.getRNG().nextDouble() < Config.getKiwiWormFindChance()) {
                    kiwi.entityDropItem(new ItemStack(ModItems.worm, 1), 0.0f);
                }
            }
        }

        @Override
        public void resetTask() {
            kiwi.setForaging(false);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ModelKiwi extends ModelBase {
        private static final float BODY_PITCH = 1.5708F;

        public final ModelRenderer head;
        public final ModelRenderer bill;
        public final ModelRenderer body;
        public final ModelRenderer left_wing;
        public final ModelRenderer right_wing;
        public final ModelRenderer left_leg;
        public final ModelRenderer right_leg;

        // Separate, authored sleeping pose: the bird lies on the ground with its legs splayed out to the sides
        private final ModelRenderer sleep_head;
        private final ModelRenderer sleep_bill;
        private final ModelRenderer sleep_body;
        private final ModelRenderer sleep_left_wing;
        private final ModelRenderer sleep_right_wing;
        private final ModelRenderer sleep_left_leg;
        private final ModelRenderer sleep_right_leg;

        public ModelKiwi() {
            this.textureWidth = 64;
            this.textureHeight = 32;

            this.head = new ModelRenderer(this);
            this.head.setRotationPoint(0.0F, 15.0F, -4.0F);
            this.head.cubeList.add(new ModelBox(this.head, 0, 0, -2.0F, -5.0F, -4.0F, 4, 5, 4, 0.0F, false));
            this.head.cubeList.add(new ModelBox(this.head, 0, 9, -2.0F, -5.0F, -4.0F, 4, 2, 4, 0.2F, false));

            // Child of the head so the bill follows head turns, bobbing and sleeping poses
            this.bill = new ModelRenderer(this);
            this.bill.setRotationPoint(0.0F, 0.0F, -2.0F);
            this.bill.cubeList.add(new ModelBox(this.bill, 16, 0, -1.0F, -2.5F, -7.0F, 2, 2, 5, 0.0F, false));
            this.head.addChild(this.bill);

            this.body = new ModelRenderer(this);
            this.body.setRotationPoint(0.0F, 16.0F, 0.0F);
            this.body.rotateAngleX = BODY_PITCH;
            this.body.cubeList.add(new ModelBox(this.body, 0, 16, -4.0F, -4.0F, -3.0F, 8, 8, 8, 0.0F, false));
            this.body.cubeList.add(new ModelBox(this.body, 32, 23, -4.0F, -4.0F, -4.0F, 8, 8, 1, 0.0F, false));

            this.left_wing = new ModelRenderer(this);
            this.left_wing.setRotationPoint(5.0F, 13.0F, 0.0F);
            this.left_wing.cubeList.add(new ModelBox(this.left_wing, 50, 0, -1.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F, false));

            this.right_wing = new ModelRenderer(this);
            this.right_wing.setRotationPoint(-5.0F, 13.0F, 0.0F);
            this.right_wing.cubeList.add(new ModelBox(this.right_wing, 50, 0, 0.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F, true));

            this.left_leg = new ModelRenderer(this);
            this.left_leg.setRotationPoint(2.0F, 19.0F, 1.0F);
            this.left_leg.cubeList.add(new ModelBox(this.left_leg, 38, 0, -1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F, false));
            this.left_leg.cubeList.add(new ModelBox(this.left_leg, 38, 8, -1.0F, 0.0F, -2.0F, 3, 3, 3, 0.01F, true));

            this.right_leg = new ModelRenderer(this);
            this.right_leg.setRotationPoint(-3.0F, 19.0F, 1.0F);
            this.right_leg.cubeList.add(new ModelBox(this.right_leg, 38, 0, -1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F, false));
            this.right_leg.cubeList.add(new ModelBox(this.right_leg, 38, 8, -1.0F, 0.0F, -2.0F, 3, 3, 3, 0.001F, false));

            this.sleep_head = new ModelRenderer(this);
            this.sleep_head.setRotationPoint(0.0F, 24.0F, -4.0F);
            this.sleep_head.cubeList.add(new ModelBox(this.sleep_head, 0, 0, -2.0F, -5.0F, -4.0F, 4, 5, 4, 0.0F, false));
            this.sleep_head.cubeList.add(new ModelBox(this.sleep_head, 0, 9, -2.0F, -5.0F, -4.0F, 4, 3, 4, 0.2F, false));

            this.sleep_bill = new ModelRenderer(this);
            this.sleep_bill.setRotationPoint(0.0F, 0.0F, -2.0F);
            this.sleep_bill.cubeList.add(new ModelBox(this.sleep_bill, 16, 0, -1.0F, -2.5F, -7.0F, 2, 2, 5, 0.0F, false));
            this.sleep_head.addChild(this.sleep_bill);

            this.sleep_body = new ModelRenderer(this);
            this.sleep_body.setRotationPoint(0.0F, 21.0F, 0.0F);
            this.sleep_body.rotateAngleX = BODY_PITCH;
            this.sleep_body.cubeList.add(new ModelBox(this.sleep_body, 0, 16, -4.0F, -4.0F, -3.0F, 8, 8, 8, 0.0F, false));
            this.sleep_body.cubeList.add(new ModelBox(this.sleep_body, 32, 23, -4.0F, -4.0F, -4.0F, 8, 8, 1, 0.0F, false));

            this.sleep_left_wing = new ModelRenderer(this);
            this.sleep_left_wing.setRotationPoint(5.0F, 18.0F, 0.0F);
            this.sleep_left_wing.cubeList.add(new ModelBox(this.sleep_left_wing, 50, 0, -1.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F, false));

            this.sleep_right_wing = new ModelRenderer(this);
            this.sleep_right_wing.setRotationPoint(-5.0F, 18.0F, 0.0F);
            this.sleep_right_wing.cubeList.add(new ModelBox(this.sleep_right_wing, 50, 0, 0.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F, true));

            this.sleep_left_leg = new ModelRenderer(this);
            this.sleep_left_leg.setRotationPoint(5.0F, 19.0F, 1.0F);
            this.sleep_left_leg.cubeList.add(new ModelBox(this.sleep_left_leg, 38, 0, -1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F, false));
            this.sleep_left_leg.cubeList.add(new ModelBox(this.sleep_left_leg, 38, 8, -1.0F, 0.0F, -2.0F, 3, 3, 3, 0.01F, true));

            this.sleep_right_leg = new ModelRenderer(this);
            this.sleep_right_leg.setRotationPoint(-6.0F, 19.0F, 1.0F);
            this.sleep_right_leg.cubeList.add(new ModelBox(this.sleep_right_leg, 38, 0, -1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F, false));
            this.sleep_right_leg.cubeList.add(new ModelBox(this.sleep_right_leg, 38, 8, -1.0F, 0.0F, -2.0F, 3, 3, 3, 0.001F, false));
        }

        @Override
        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            super.render(entity, f, f1, f2, f3, f4, f5);
            this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
            boolean sleeping = entity instanceof EntityKiwiBird && ((EntityKiwiBird) entity).isSleeping();
            if (sleeping) {
                this.sleep_head.render(f5);
                this.sleep_body.render(f5);
                this.sleep_left_wing.render(f5);
                this.sleep_right_wing.render(f5);
                this.sleep_left_leg.render(f5);
                this.sleep_right_leg.render(f5);
                return;
            }
            this.head.render(f5);
            this.body.render(f5);
            this.left_wing.render(f5);
            this.right_wing.render(f5);
            this.left_leg.render(f5);
            this.right_leg.render(f5);
        }

        @Override
        public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

            // Reset to the rest pose so state changes (forage/walk) never leak into each other
            this.head.rotateAngleX = 0.0F;
            this.head.rotateAngleY = 0.0F;
            this.left_leg.rotateAngleX = 0.0F;
            this.right_leg.rotateAngleX = 0.0F;

            if (entity instanceof EntityKiwiBird) {
                EntityKiwiBird kiwi = (EntityKiwiBird) entity;
                if (kiwi.isSleeping()) {
                    // Slow breathing: the head rocks gently about its resting point
                    this.sleep_head.rotateAngleX = MathHelper.sin(ageInTicks * 0.05F) * 0.04F;
                    return;
                }
                if (kiwi.isForaging()) {
                    this.head.rotateAngleX = 0.8F + MathHelper.sin(ageInTicks * 0.35F) * 0.55F;
                    return;
                }
            }

            this.head.rotateAngleY = netHeadYaw * 0.017453292F;
            this.head.rotateAngleX = 0.2F + headPitch * 0.017453292F;

            float swing = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.left_leg.rotateAngleX = swing;
            this.right_leg.rotateAngleX = -swing;
        }
    }
}
