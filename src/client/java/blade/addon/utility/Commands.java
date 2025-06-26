package blade.addon.utility;

import blade.addon.BugScan;
import blade.addon.config.Config;
import com.mojang.brigadier.CommandDispatcher;
import blade.addon.FillHelper;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class Commands {

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(Commands::fillPearls);
        ClientCommandRegistrationCallback.EVENT.register(Commands::fillSuperBooms);
        ClientCommandRegistrationCallback.EVENT.register(Commands::openConfig);
    }


    private static void fillPearls(@NotNull CommandDispatcher<FabricClientCommandSource> commandDispatcher,
                                   CommandRegistryAccess commandRegistryAccess) {

        commandDispatcher.register(
                ClientCommandManager.literal("fillPearls")
                        .executes(commandContext -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.ENDER_PEARL, 16, 16))
        );
    }

    private static void fillSuperBooms(@NotNull CommandDispatcher<FabricClientCommandSource> commandDispatcher,
                                       CommandRegistryAccess commandRegistryAccess) {

        commandDispatcher.register(
                ClientCommandManager.literal("fillSuperboom")
                        .executes(commandContext -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.SUPERBOOM_TNT, 64, 64))
        );
    }


    private static void openConfig(@NotNull CommandDispatcher<FabricClientCommandSource> commandDispatcher,
                                       CommandRegistryAccess commandRegistryAccess) {

        commandDispatcher.register(
                ClientCommandManager.literal("bladeAddons")
                        .executes(commandContext -> {
                            MinecraftClient.getInstance().setScreen(Config.create(null));
                            return 1;
                        })
        );
    }
}
