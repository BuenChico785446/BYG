package windanesz.byg.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandByg extends CommandBase {
    @Override
    public String getName() {
        return "byg";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.byg.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3 || !"tp".equalsIgnoreCase(args[0]) || !"biome".equalsIgnoreCase(args[1])) {
            throw new WrongUsageException(getUsage(sender));
        }
        ResourceLocation id = BiomeTeleportation.resolve(args[2]);
        Biome biome = id == null ? null : Biome.REGISTRY.getObject(id);
        if (biome == null) {
            throw new CommandException("commands.byg.tp.biome.unknown", args[2]);
        }
        EntityPlayerMP player = args.length > 3 ? getPlayer(server, sender, args[3]) : getCommandSenderAsPlayer(sender);
        BiomeTeleportation.teleport(player, biome, sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "tp");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "biome");
        }
        if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, Biome.REGISTRY.getKeys());
        }
        if (args.length == 4) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}
