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
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class Commands {

    private static final String[] COMMAND_ALIASES = {"ba", "blade", "bladeaddons"};

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(Commands::registerCommands);
    }

    public static void registerCommands(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
                                        CommandRegistryAccess registryAccess) {


        //prob not a good solution but I don't use that many commands currently
        for (String alias : COMMAND_ALIASES) {
            dispatcher.register(
                    ClientCommandManager.literal(alias)
                            .then(ClientCommandManager.literal("ep").executes(context -> FillHelper.fillItem(FillHelper.ENDER_PEARL, 16, 16, false)))

                            .then(ClientCommandManager.literal("sb").executes(context -> FillHelper.fillItem(FillHelper.SUPERBOOM_TNT, 64, 64, false)))

                            .then(ClientCommandManager.literal("ij").executes(context -> FillHelper.fillItem(FillHelper.INFLATABLE_JERRY, 64, 64, false)))

                            .then(ClientCommandManager.literal("refill")
                                    .executes(
                                            context -> {
                                                FillHelper.fillItem(FillHelper.ENDER_PEARL, 16, 16, true);
                                                Scheduler.scheduleTask(() -> FillHelper.fillItem(FillHelper.SUPERBOOM_TNT, 64, 64, true), 40);
                                                Scheduler.scheduleTask(() -> FillHelper.fillItem(FillHelper.INFLATABLE_JERRY, 64, 64, true), 80);
                                                return Constants.SUCCESS;
                                            }

                                    ))

                            .then(ClientCommandManager.literal("leaporder")
                                    .then(ClientCommandManager.argument("backupMage", StringArgumentType.string())
                                            .then(ClientCommandManager.argument("odinOrder", BoolArgumentType.bool())
                                                    .executes(context -> {
                                                        String backupMage = StringArgumentType.getString(context, "backupMage");
                                                        boolean odinOrder = BoolArgumentType.getBool(context, "odinOrder");
                                                        LeapOrder.leapOrder(backupMage, odinOrder);
                                                        return Constants.SUCCESS;
                                                    })
                                            )
                                    ))

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

                            .then(ClientCommandManager.literal("protectItem").executes(context -> {
                                ProtectItem.protectSelected();
                                return Constants.SUCCESS;
                            }))

                            .then(ClientCommandManager.literal("resetPbs").executes(context -> {
                                PersonalBests.reset();
                                return Constants.SUCCESS;
                            }))

                            .then(ClientCommandManager.literal("ss")
                                    .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
                                            .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
                                                    .then(ClientCommandManager.argument("z", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                int x = IntegerArgumentType.getInteger(context, "x");
                                                                int y = IntegerArgumentType.getInteger(context, "y");
                                                                int z = IntegerArgumentType.getInteger(context, "z");
                                                                ExtraOptions.startButton = new BlockPos(x, y, z);
                                                                Misc.addChatMessage(Text.literal("Set the start position to " + x + " " + y + " " + z));
                                                                Config.manager.save();
                                                                return Constants.SUCCESS;
                                                            }))
                                            )
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
