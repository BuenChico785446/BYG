package windanesz.byg.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public final class ModSounds {
    public static final SoundEvent KIWI_IDLE = create("entity.kiwi.idle");
    public static final SoundEvent KIWI_HURT = create("entity.kiwi.hurt");

    private ModSounds() {
    }

    private static SoundEvent create(String name) {
        ResourceLocation id = new ResourceLocation("byg", name);
        return new SoundEvent(id).setRegistryName(id);
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(KIWI_IDLE, KIWI_HURT);
    }
}
