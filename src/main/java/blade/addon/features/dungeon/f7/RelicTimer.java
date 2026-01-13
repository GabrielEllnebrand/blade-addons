package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.times.PersonalBests;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelicTimer {

    public enum Relic {
        GREEN(new Box(49, 7, 44, 50, 8, 45), 0xff55ff55, "§cRed"),
        RED(new Box(51, 7, 42, 52, 8, 43), 0xffff5555, "§aGreen"),
        PURPLE(new Box(54, 7, 41, 55, 8, 42), 0xffff55ff, "§5Purple"),
        ORANGE(new Box(57, 7, 42, 58, 8, 43), 0xffffaa00, "§6Orange"),
        BLUE(new Box(59, 7, 44, 60, 8, 45), 0xff55ffff, "§bBlue");

        final Box box;
        final int color;
        final String name;
        long placedTime = 0;

        Relic(Box box, int color, String name) {
            this.box = box;
            this.color = color;
            this.name = name;
        }
    }


    private static final int GREEN_COLOR = 0xff00ff00;
    private static final int RED_COLOR = 0xffff0000;

    private static final Pattern PATTERN = Pattern.compile("^(.+) picked the Corrupted (Red|Purple|Orange|Green|Blue) Relic!$");

    private static long phaseStartTime;
    private static Relic pickedupRelic = null;

    private static int tick = Floor7.relicSpawnTicks;
    private static boolean sentRelicTimes = false;

    private static boolean ignoreChecks = false;
    //only here to test the relic progress bar
    private static boolean forceGUI = false;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (!forceGUI) {
                if (!Location.inDungeon() || !Phase.inP5()) return false;
            }
            tick = Math.max(tick - 1, -1);

            if (tick == -1) {
                forceGUI = false;
            }

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
               reset();
            }
            return false;
        });

        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.inP5()) {
                phaseStartTime = System.currentTimeMillis();
                tick = Floor7.relicSpawnTicks;
            }
            return false;
        });

        Events.ON_GAME_MESSAGE.register(message -> {
            if (!Location.inDungeon() || !Phase.inP5()) return false;
            String str = message.getString().replaceAll("§.", "");
            Matcher matcher = PATTERN.matcher(str);
            if (matcher.find()) {
                String name = matcher.group(1);
                String relicString = matcher.group(2);

                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player == null) return false;
                if (name.equals(player.getName().getString())) {
                    pickedupRelic = Relic.valueOf(relicString.toUpperCase());
                    Debug.sendDebugMessage(Text.literal("Picked up relic " + relicString));
                }
            }

            return false;
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!Location.inDungeon() || !Phase.inP5() || !Floor7.showAllRelicTimes ||sentRelicTimes) return;

            Iterable<Entity> entities = world.getEntities();

            for (Entity entity : entities) {
                if (entity instanceof ArmorStandEntity armorStand) {
                    if (!EntityUtil.isWearing(armorStand, EquipmentSlot.HEAD, "Relic")) continue;

                    double ax = armorStand.getX();
                    double az = armorStand.getZ();

                    for (Relic relic : Relic.values()) {
                        if (relic.placedTime != 0 && Misc.getDistance(relic.box.maxX, ax, relic.box.maxZ, az) < 1) {
                            relic.placedTime = System.currentTimeMillis();
                            break;
                        }
                    }
                }
            }

            if (allRelicsPlaced()) {
                for (Relic relic : Relic.values()) {
                    long time = relic.placedTime - phaseStartTime;
                    Misc.addChatMessage(Text.literal(relic.name + " &aRelic placed in &e" + Constants.DECIMAL_FORMAT.format(time) + "s&a."));
                    sentRelicTimes = true;
                }
            }
        });


        Events.ON_BLOCK_INTERACTION.register((result, itemStack) -> {
            if (!ignoreChecks) {
                if ((!Location.inDungeon() || !Phase.inP5())) return false;
            }

            if (pickedupRelic == null) return false;

            BlockPos pos = result.getBlockPos();

            if (!ItemUtil.itemHasName(itemStack, "Relic")) {
                if (Floor7.blockIncorrectRelicPlace) {
                    Debug.sendDebugMessage(Text.literal("Item: " + itemStack.getName()));
                    Misc.addChatMessage(Text.literal("Blocked a weird click"));
                    return true;
                } else {
                    return false;
                }
            }

            if (pos.getX() == pickedupRelic.box.minX && (pos.getY() == pickedupRelic.box.minY || pos.getY() == pickedupRelic.box.minY - 1) && pos.getZ() == pickedupRelic.box.minZ) {
                if (Floor7.enableRelicPlaceTime) {
                    MutableText text = Text.literal("The ").formatted(Formatting.GREEN)
                            .append(Text.literal(pickedupRelic.name().toLowerCase()).withColor(pickedupRelic.color))
                            .append(" relic was placed in ").formatted(Formatting.GREEN);

                    switch (pickedupRelic) {
                        case RED -> PersonalBests.redRelicTime.testNewTime(text, phaseStartTime);
                        case ORANGE -> PersonalBests.orangeRelicTime.testNewTime(text, phaseStartTime);
                        case BLUE -> PersonalBests.blueRelicTime.testNewTime(text, phaseStartTime);
                        case GREEN -> PersonalBests.greenRelicTime.testNewTime(text, phaseStartTime);
                        case PURPLE -> PersonalBests.purpleRelicTime.testNewTime(text, phaseStartTime);
                    }

                }
                pickedupRelic = null;
            } else {
                if (Floor7.blockIncorrectRelicPlace) {
                    Misc.addChatMessage(Text.literal("incorrect click!"));
                    return true;
                }
            }
            return false;
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (pickedupRelic == null || !Floor7.renderRelicHighlight) return;

            Vec3d camera = context.worldState().cameraRenderState.pos;
            MatrixStack matrices = context.matrices();
            if (matrices == null) return;
            matrices.push();
            matrices.translate(-camera.x, -camera.y, -camera.z);

            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;
            VertexConsumer buffer = consumers.getBuffer(RenderLayers.FILLED_LAYER);


            Box box = pickedupRelic.box;
            float[] color = RenderUtils.toFloats(pickedupRelic.color);
            VertexRendering.drawFilledBox(matrices, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color[0], color[1], color[2], color[3]);


            matrices.pop();
        });

    }

    private static void reset() {
        tick = Floor7.relicSpawnTicks;
        pickedupRelic = null;
        for (Relic relic : Relic.values()) {
            relic.placedTime = 0;
        }
        sentRelicTimes = false;
    }

    private static boolean allRelicsPlaced() {
        for (Relic relic : Relic.values()) {
            if (relic.placedTime == 0) return false;
        }
        return true;
    }

    public static void testRelicGUI() {
        forceGUI = true;
        tick = Floor7.relicSpawnTicks;
    }

    public static boolean testSetRelic(String name) {
        try {
            pickedupRelic = RelicTimer.Relic.valueOf(name);
            Misc.addChatMessage(Text.literal("Relic is now: " + name));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void testIgnoreChecks() {
        ignoreChecks = !ignoreChecks;
        Misc.addChatMessage(Text.literal("Ignore relic checks: ").append(Misc.getStatusText(ignoreChecks)));

    }

    public static boolean display() {
        return Floor7.enableRelicStartTimer && Location.inDungeon() && Phase.inP5() && tick > -1 && !Floor7.replaceWithProgressBar;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int color = tick > 7 ? GREEN_COLOR : RED_COLOR;
        RenderUtils.drawTimer(component, context, tick, color);
    }

    public static boolean displayProgressBar() {
        if (forceGUI) return true;
        return Floor7.enableRelicStartTimer && Location.inDungeon() && Phase.inP5() && tick > -1 && Floor7.replaceWithProgressBar;
    }

    public static void renderProgressBar(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        //from valleyAddons
        StringBuilder message = new StringBuilder("§8[");

        if (Floor7.useValleyBar) {
            for (int i = 0; i < Floor7.relicSpawnTicks; ++i) {
                if (i < tick) {
                    if (tick > 2) message.append("§a|");
                    else message.append("§c|");
                } else message.append("§7|");
            }
            message.append("§8]");
        } else {
            int diff = Floor7.relicSpawnTicks - tick;
            for (int i = 0; i < Floor7.relicSpawnTicks; i++) {
                if (i < diff) {
                    if (tick < 2) message.append("§a|");
                    else message.append("§c|");
                } else message.append("§7|");
            }
            message.append("§8]");
        }

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal(message.toString()), x, y, component.getWidth(), 0xffffffff);

    }
}
