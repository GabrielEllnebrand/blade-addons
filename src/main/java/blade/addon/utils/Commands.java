package blade.addon.utils;

import blade.addon.features.dungeon.LeapOrder;
import blade.addon.features.dungeon.f7.BossWaypoints;
import blade.addon.features.item.ProtectItem;
import blade.addon.utils.config.Config;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.dungeon.FillHelper;
import blade.addon.utils.times.PersonalBests;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class Commands {

    private static final String[] COMMAND_ALIASES = {"ba", "blade", "bladeaddons"};

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(Commands::registerCommands);
    }

    public static void registerCommands(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
                                        CommandBuildContext registryAccess) {


        //prob not a good solution but I don't use that many commands currently
        for (String alias : COMMAND_ALIASES) {
            dispatcher.register(
                    ClientCommands.literal(alias)
                            .then(ClientCommands.literal("ep").executes(context -> FillHelper.fillItem(FillHelper.ENDER_PEARL, 16, 16, false)))

                            .then(ClientCommands.literal("sb").executes(context -> FillHelper.fillItem(FillHelper.SUPERBOOM_TNT, 64, 64, false)))

                            .then(ClientCommands.literal("ij").executes(context -> FillHelper.fillItem(FillHelper.INFLATABLE_JERRY, 64, 64, false)))

                            .then(ClientCommands.literal("refill")
                                    .executes(
                                            context -> {
                                                FillHelper.fillItem(FillHelper.ENDER_PEARL, 16, 16, true);
                                                Scheduler.scheduleTask(() -> FillHelper.fillItem(FillHelper.SUPERBOOM_TNT, 64, 64, true), 40);
                                                Scheduler.scheduleTask(() -> FillHelper.fillItem(FillHelper.INFLATABLE_JERRY, 64, 64, true), 80);
                                                return Constants.SUCCESS;
                                            }

                                    ))

                            .then(ClientCommands.literal("leaporder")
                                    .then(ClientCommands.argument("backupMage", StringArgumentType.string())
                                            .then(ClientCommands.argument("odinOrder", BoolArgumentType.bool())
                                                    .executes(context -> {
                                                        String backupMage = StringArgumentType.getString(context, "backupMage");
                                                        boolean odinOrder = BoolArgumentType.getBool(context, "odinOrder");
                                                        LeapOrder.leapOrder(backupMage, odinOrder);
                                                        return Constants.SUCCESS;
                                                    })
                                            )
                                    ))

                            .then(ClientCommands.literal("waypoint")

                                    .then(ClientCommands.literal("add")
                                            .then(ClientCommands.argument("x", DoubleArgumentType.doubleArg())
                                                    .then(ClientCommands.argument("y", DoubleArgumentType.doubleArg())
                                                            .then(ClientCommands.argument("z", DoubleArgumentType.doubleArg())
                                                                    .executes(context -> {
                                                                        double x = DoubleArgumentType.getDouble(context, "x");
                                                                        double y = DoubleArgumentType.getDouble(context, "y");
                                                                        double z = DoubleArgumentType.getDouble(context, "z");

                                                                        BossWaypoints.attemptAddWaypoint(x, y, z, 1, 1, 1);
                                                                        return Constants.SUCCESS;
                                                                    })

                                                                    .then(ClientCommands.argument("dx", DoubleArgumentType.doubleArg())
                                                                            .then(ClientCommands.argument("dy", DoubleArgumentType.doubleArg())
                                                                                    .then(ClientCommands.argument("dz", DoubleArgumentType.doubleArg())
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
                                                LocalPlayer player = Minecraft.getInstance().player;
                                                if (player == null) return 0;

                                                BossWaypoints.attemptAddWaypoint(player.getBlockX(), player.getBlockY(), player.getBlockZ(), 1, 1, 1);
                                                return Constants.SUCCESS;
                                            })

                                    )
                                    .then(ClientCommands.literal("remove")

                                            .then(ClientCommands.argument("radius", DoubleArgumentType.doubleArg())
                                                    .executes(context -> {
                                                        double r = DoubleArgumentType.getDouble(context, "radius");
                                                        LocalPlayer player = Minecraft.getInstance().player;
                                                        if (player == null) return Constants.FAIL;
                                                        BossWaypoints.removeWaypoints(player.getX(), player.getY(), player.getZ(), r);
                                                        return Constants.SUCCESS;
                                                    }))

                                            .then(ClientCommands.argument("x", DoubleArgumentType.doubleArg())
                                                    .then(ClientCommands.argument("y", DoubleArgumentType.doubleArg())
                                                            .then(ClientCommands.argument("z", DoubleArgumentType.doubleArg())
                                                                    .then(ClientCommands.argument("radius", DoubleArgumentType.doubleArg())
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

                                    .then(ClientCommands.literal("editMode").executes(context -> {
                                        BossWaypoints.togglePlace();
                                        return Constants.SUCCESS;
                                    }))

                                    .then(ClientCommands.literal("ignoreBoss").executes(context -> {
                                        BossWaypoints.toggleIgnoreBoss();
                                        return Constants.SUCCESS;
                                    }))

                            )

                            .then(ClientCommands.literal("protectItem").executes(context -> {
                                ProtectItem.protectSelected();
                                return Constants.SUCCESS;
                            }))

                            .then(ClientCommands.literal("resetPbs").executes(context -> {
                                PersonalBests.reset();
                                return Constants.SUCCESS;
                            }))

                            .then(ClientCommands.literal("ss")
                                    .then(ClientCommands.argument("x", IntegerArgumentType.integer())
                                            .then(ClientCommands.argument("y", IntegerArgumentType.integer())
                                                    .then(ClientCommands.argument("z", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int x = IntegerArgumentType.getInteger(context, "x");
                                                                int y = IntegerArgumentType.getInteger(context, "y");
                                                                int z = IntegerArgumentType.getInteger(context, "z");
                                                                ExtraOptions.startButton = new BlockPos(x, y, z);
                                                                Misc.addChatMessage(Component.literal("Set the start position to " + x + " " + y + " " + z));
                                                                Config.manager.save();
                                                                return Constants.SUCCESS;
                                                            }))
                                            )
                                    )
                            )

                            .executes(commandContext -> Scheduler.scheduleScreen(Config.createScreen(null)))
            );

            dispatcher.register(ClientCommands.literal("dh").executes(context -> {
                Misc.executeCommand("warp dungeon_hub");
                return Constants.SUCCESS;
            }));

            for (int i = 1; i <= 7; i++) {
                int finalI = i;
                dispatcher.register(ClientCommands.literal("f" + i).executes(context -> {
                    Misc.executeCommand("joindungeon catacombs " + finalI);
                    return Constants.SUCCESS;
                }));
                dispatcher.register(ClientCommands.literal("m" + i).executes(context -> {
                    Misc.executeCommand("joindungeon master_catacombs " + finalI);
                    return Constants.SUCCESS;
                }));
            }
        }
    }
}
