package blade.addon.utils.debug;

import blade.addon.features.dungeon.f7.RelicTimer;
import blade.addon.features.dungeon.f7.location.LocationNotifier;
import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Debug {

    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.NAMESPACE);
    private static boolean sendDebug = false;
    public static boolean sendSound = false;
    public static boolean termInfo = false;
    public static boolean renderPositions = false;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(Debug::registerCommands);
    }

    private static void registerCommands(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess) {

        dispatcher.register(ClientCommandManager.literal("badev")
                .then(ClientCommandManager.literal("runInfo").executes(context -> {
                    sendRunInfo();
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("debug").executes(context -> {
                    sendDebug = !sendDebug;
                    Misc.addChatMessage(Text.literal("Send debug: ").append(Misc.getStatusText(sendDebug)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("sound").executes(context -> {
                    sendSound = !sendSound;
                    Misc.addChatMessage(Text.literal("Send Sound: ").append(Misc.getStatusText(sendSound)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("termInfo").executes(context -> {
                    termInfo = !termInfo;
                    Misc.addChatMessage(Text.literal("Terminal info: ").append(Misc.getStatusText(termInfo)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("sendNotification").then(ClientCommandManager.argument("message", StringArgumentType.string()).executes(context -> {
                    String message = StringArgumentType.getString(context, "message");
                    LocationNotifier.startNotification("Someone", message);
                    return Constants.SUCCESS;
                })))

                .then(ClientCommandManager.literal("drawPositionBoxes").executes(context -> {
                    renderPositions = !renderPositions;
                    Misc.addChatMessage(Text.literal("Render positons: ").append(Misc.getStatusText(renderPositions)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("relic")
                        .then(ClientCommandManager.literal("set")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name").toUpperCase();
                                            if (RelicTimer.testSetRelic(name)) {
                                                return Constants.SUCCESS;
                                            }
                                            return Constants.FAIL;
                                        })
                                )
                        )

                        .then(ClientCommandManager.literal("testRelicGUI").executes(context -> {
                            RelicTimer.testRelicGUI();
                            return Constants.SUCCESS;
                        }))

                        .then(ClientCommandManager.literal("getRelic").executes(context -> {
                            RelicTimer.printRelic();
                            return Constants.SUCCESS;
                        }))

                )
        );
    }


    public static void sendDebugMessage(Text text) {
        if (sendDebug) {
            Misc.addChatMessage(text);
        }
    }

    public static void sendRunInfo() {
        Misc.addChatMessage(Text.literal("Phase: " + Phase.getPhase()));
        Misc.addChatMessage(Text.literal("Section: " + Section.getSection()));
        Misc.addChatMessage(Text.literal("Gateblown: " + Section.isGateBlownUp()));
        Misc.addChatMessage(Text.literal("In floor 7: " + Phase.isInFloor7()));
    }

}
