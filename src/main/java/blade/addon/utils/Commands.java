package blade.addon.utils;

import blade.addon.utils.dungeon.FillHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import org.jetbrains.annotations.NotNull;

public class Commands {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(Commands::registerCommands);
    }

    public static void registerCommands(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
                                        CommandRegistryAccess registryAccess) {
        dispatcher.register(
                ClientCommandManager.literal("ba")

                        .then(ClientCommandManager.literal("ep").executes(context -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.ENDER_PEARL, 16, 16)))

                        .then(ClientCommandManager.literal("sb").executes(context -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.SUPERBOOM_TNT, 64, 64)))

                        .then(ClientCommandManager.literal("ij").executes(context -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.INFLATABLE_JERRY, 64, 64)))

        );
    }
}
