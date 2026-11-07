package windanesz.byg;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import windanesz.byg.client.gui.GuiHandlerBYG;
import windanesz.byg.command.CommandByg;
import windanesz.byg.event.TerrainGenEventHandler;
import windanesz.byg.init.BygInitialization;
import windanesz.byg.proxy.IProxyBYG;
import windanesz.byg.registry.OreDictionaryEntries;
import windanesz.byg.worldgen.BygWorldGenerator;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class BiomesYouGo {
    public static final String MODID = Tags.MOD_ID;
    public static final String VERSION = Tags.VERSION;
    public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    @SidedProxy(clientSide = "windanesz.byg.client.ClientProxy", serverSide = "windanesz.byg.ServerProxy")
    public static IProxyBYG proxy;
    @Mod.Instance(value = Tags.MOD_ID)
    public static BiomesYouGo instance;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(BygWorldGenerator.INSTANCE, 5);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerBYG());
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEventHandler());
        BygInitialization.preInit(event);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        BygInitialization.init(event);
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        OreDictionaryEntries.init();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandByg());
        proxy.serverLoad(event);
    }
}
