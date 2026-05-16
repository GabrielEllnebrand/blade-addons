package blade.addon.utils.debug;

import blade.addon.features.dungeon.f7.RelicTimer;
import blade.addon.features.dungeon.f7.location.LocationNotifier;
import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import blade.addon.utils.events.Events;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Debug {

    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.NAMESPACE);
    private static boolean sendDebug = false;
    public static boolean sendSound = false;
    public static boolean termInfo = false;
    public static boolean renderPositions = false;
    public static boolean sendNotiDebug = false;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(Debug::registerCommands);
    }

    private static void registerCommands(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
                                         CommandBuildContext registryAccess) {

        dispatcher.register(ClientCommandManager.literal("badev")
                .then(ClientCommandManager.literal("runInfo").executes(context -> {
                    sendRunInfo();
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("debug").executes(context -> {
                    sendDebug = !sendDebug;
                    Misc.addChatMessage(Component.literal("Send debug: ").append(Misc.getStatusText(sendDebug)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("sound").executes(context -> {
                    sendSound = !sendSound;
                    Misc.addChatMessage(Component.literal("Send Sound: ").append(Misc.getStatusText(sendSound)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("termInfo").executes(context -> {
                    termInfo = !termInfo;
                    Misc.addChatMessage(Component.literal("Terminal info: ").append(Misc.getStatusText(termInfo)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("sendNotification").then(ClientCommandManager.argument("message", StringArgumentType.string()).executes(context -> {
                    String message = StringArgumentType.getString(context, "message");
                    LocationNotifier.startNotification("Someone", message);
                    return Constants.SUCCESS;
                })))

                .then(ClientCommandManager.literal("drawPositionBoxes").executes(context -> {
                    renderPositions = !renderPositions;
                    Misc.addChatMessage(Component.literal("Render positons: ").append(Misc.getStatusText(renderPositions)));
                    return Constants.SUCCESS;
                }))

                .then(ClientCommandManager.literal("testString").then(ClientCommandManager.argument("message", StringArgumentType.string()).executes(context -> {
                    String message = StringArgumentType.getString(context, "message");
                    Events.ON_GAME_MESSAGE.invoke(gameMessageEvent -> gameMessageEvent.onGameMessage(Component.literal(message)));
                    return Constants.SUCCESS;
                })))

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

                .then(ClientCommandManager.literal("location")
                        .then(ClientCommandManager.literal("current")
                                .executes(context -> {
                                    Misc.addChatMessage(Component.literal(Location.getCurrentLocation().toString()));
                                    return Constants.SUCCESS;
                                        }))

                        .then(ClientCommandManager.literal("set")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name").toUpperCase();
                                            Location location =  Location.getLocation(name);
                                            Location.changeLocation(location);
                                            Misc.addChatMessage(Component.literal("Swapped to location: " + location.name()));
                                            return Constants.SUCCESS;
                                        })
                                )
                        )
                )

                .then(ClientCommandManager.literal("chatNoti")
                        .then(ClientCommandManager.literal("sendDebug")
                                .executes(context -> {
                                    sendNotiDebug = !sendNotiDebug;
                                    Misc.addChatMessage(Component.literal("Send notification debug: ").append(Misc.getStatusText(sendNotiDebug)));
                                    return Constants.SUCCESS;
                                })

                        )
                )

                .then(ClientCommandManager.literal("classes")
                        .executes(context -> {
                            DungeonClass.printClasses();
                            return Constants.SUCCESS;
                        })
                )
                .then(ClientCommandManager.literal("currentClass")
                        .executes(context -> {
                            Misc.addChatMessage(Component.literal("Current class: " + DungeonClass.currentClass));
                            return Constants.SUCCESS;
                        })
                )
        );
    }


    public static void sendDebugMessage(Component text) {
        if (sendDebug) {
            Misc.addChatMessage(text);
        }
    }

    public static void sendRunInfo() {
        Misc.addChatMessage(Component.literal("Phase: " + Phase.getPhase()));
        Misc.addChatMessage(Component.literal("Section: " + Section.getSection()));
        Misc.addChatMessage(Component.literal("Gateblown: " + Section.isGateBlownUp()));
        Misc.addChatMessage(Component.literal("In floor 7: " + Phase.isInFloor7()));
    }

}
