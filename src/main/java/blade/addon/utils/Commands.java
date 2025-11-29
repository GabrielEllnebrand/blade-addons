package blade.addon.utils;

import blade.addon.features.dungeon.f7.BossWaypoints;
import blade.addon.utils.config.Config;
import blade.addon.utils.dungeon.FillHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import org.jetbrains.annotations.NotNull;

public class Commands {

    private static final String[] COMMAND_ALIASES = {"ba", "blade", "bladeaddons"};

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(Commands::registerCommands);
    }

    public static void registerCommands(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
                                        CommandRegistryAccess registryAccess) {

        //prob not a good solution but I don't use that many commands currently
        for (String alias : COMMAND_ALIASES) {
            dispatcher.register(
                    ClientCommandManager.literal(alias)
                            .then(ClientCommandManager.literal("ep").executes(context -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.ENDER_PEARL, 16, 16)))

                            .then(ClientCommandManager.literal("sb").executes(context -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.SUPERBOOM_TNT, 64, 64)))

                            .then(ClientCommandManager.literal("ij").executes(context -> FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.INFLATABLE_JERRY, 64, 64)))

                            .then(ClientCommandManager.literal("waypoint")

                                    .then(ClientCommandManager.literal("add")
                                            .then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
                                                    .then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
                                                            .then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
                                                                    .executes(context -> {
                                                                        double x = DoubleArgumentType.getDouble(context, "x");
                                                                        double y = DoubleArgumentType.getDouble(context, "y");
                                                                        double z = DoubleArgumentType.getDouble(context, "z");

                                                                        BossWaypoints.attemptAddWaypoint(x, y, z, 1, 1, 1);
                                                                        return Constants.SUCCESS;
                                                                    })

                                                                    .then(ClientCommandManager.argument("dx", DoubleArgumentType.doubleArg())
                                                                            .then(ClientCommandManager.argument("dy", DoubleArgumentType.doubleArg())
                                                                                    .then(ClientCommandManager.argument("dz", DoubleArgumentType.doubleArg())
                                                                                            .executes(context -> {
                                                                                                double x = DoubleArgumentType.getDouble(context, "x");
                                                                                                double y = DoubleArgumentType.getDouble(context, "y");
                                                                                                double z = DoubleArgumentType.getDouble(context, "z");
                                                                                                double dx = DoubleArgumentType.getDouble(context, "dx");
                                                                                                double dy = DoubleArgumentType.getDouble(context, "dy");
                                                                                                double dz = DoubleArgumentType.getDouble(context, "dz");
                                                                                                BossWaypoints.attemptAddWaypoint(x, y, z, dx, dy, dz);
                                                                                                return Constants.SUCCESS;
                                                                                            })


                                                                                    )
                                                                            )
                                                                    )

                                                            )
                                                    )
                                            )
                                            .executes(context -> {
                                                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                                                if (player == null) return 0;

                                                BossWaypoints.attemptAddWaypoint(player.getBlockX(), player.getBlockY(), player.getBlockZ(), 1, 1, 1);
                                                return Constants.SUCCESS;
                                            })

                                    )
                                    .then(ClientCommandManager.literal("remove")

                                            .then(ClientCommandManager.argument("radius", DoubleArgumentType.doubleArg())
                                                    .executes(context -> {
                                                        double r = DoubleArgumentType.getDouble(context, "radius");
                                                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                                                        if (player == null) return Constants.FAIL;
                                                        BossWaypoints.removeWaypoints(player.getX(), player.getY(), player.getZ(), r);
                                                        return Constants.SUCCESS;
                                                    }))

                                            .then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
                                                    .then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
                                                            .then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
                                                                    .then(ClientCommandManager.argument("radius", DoubleArgumentType.doubleArg())
                                                                            .executes(context -> {
                                                                                double x = DoubleArgumentType.getDouble(context, "x");
                                                                                double y = DoubleArgumentType.getDouble(context, "y");
                                                                                double z = DoubleArgumentType.getDouble(context, "z");
                                                                                double radius = DoubleArgumentType.getDouble(context, "radius");

                                                                                BossWaypoints.removeWaypoints(x, y, z, radius);
                                                                                return Constants.SUCCESS;
                                                                            }))


                                                                    .executes(context -> {
                                                                        double x = DoubleArgumentType.getDouble(context, "x");
                                                                        double y = DoubleArgumentType.getDouble(context, "y");
                                                                        double z = DoubleArgumentType.getDouble(context, "z");

                                                                        BossWaypoints.removeWaypoint(x, y, z);
                                                                        return Constants.SUCCESS;
                                                                    })
                                                            )
                                                    )
                                            )

                                    )

                                    .then(ClientCommandManager.literal("editMode").executes(context -> {
                                        BossWaypoints.togglePlace();
                                        return Constants.SUCCESS;
                                    }))

                                    .then(ClientCommandManager.literal("ignoreBoss").executes(context -> {
                                        BossWaypoints.toggleIgnoreBoss();
                                        return Constants.SUCCESS;
                                    }))

                            )

                            .then(ClientCommandManager.literal("debug")
                                    .then(ClientCommandManager.argument("option", StringArgumentType.greedyString()).executes(context -> {
                                                String option = StringArgumentType.getString(context, "option");
                                                return Debug.parseOption(option);
                                            })
                                    )
                            )

                            .executes(commandContext -> Scheduler.scheduleScreen(Config.createScreen(null)))
            );

            dispatcher.register(ClientCommandManager.literal("dh").executes(context -> {
                Misc.executeCommand("warp dungeon_hub");
                return Constants.SUCCESS;
            }));
        }
    }

}
