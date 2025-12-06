package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelicTimer {

    enum Relic {
        GREEN(new Box(49, 7, 44, 50, 8, 45), 0xff55ff55),
        RED(new Box(51, 7, 42, 52, 8, 43), 0xffff5555),
        PURPLE(new Box(54, 7, 41, 55, 8, 42), 0xffff55ff),
        ORANGE(new Box(57, 7, 42, 58, 8, 43), 0xffffaa00),
        BLUE(new Box(59, 7, 44, 60, 8, 45), 0xff55ffff);

        final Box box;
        final int color;

        Relic(Box box, int color) {
            this.box = box;
            this.color = color;
        }
    }

    private static final int GREEN_COLOR = 0xff00ff00;
    private static final int RED_COLOR = 0xffff0000;

    private static final Pattern PATTERN = Pattern.compile("^(.+) picked the Corrupted (Red|Purple|Orange|Green|Blue) Relic!$");

    private static long pickupTime;
    private static Relic pickedupRelic = null;

    private static int tick = Floor7.relicSpawnTicks;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (!Location.inDungeon() || !Phase.inP5()) return;
            tick = Math.max(tick - 1, 0);
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = Floor7.relicSpawnTicks;
                pickedupRelic = null;
            }
        });

        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.inP5()) {
                pickupTime = System.currentTimeMillis();
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() || !Phase.inP5() || !Floor7.enableRelicPlaceTime) return;

            String str = message.getString().replaceAll("§.", "");
            Matcher matcher = PATTERN.matcher(str);
            if (matcher.find()) {
                String name = matcher.group(1);
                String relicString = matcher.group(2);

                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player == null) return;
                if (name.equals(player.getName().getString())) {
                    pickedupRelic = Relic.valueOf(relicString.toUpperCase());
                }
            }
        });

        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            if (world == null) return ActionResult.PASS;
            if (!Location.inDungeon() || !Phase.inP5() || pickedupRelic == null) return ActionResult.PASS;
            BlockState block = world.getBlockState(blockPos);
            onBlockClick(block, blockPos);
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world == null) return ActionResult.PASS;
            if (!Location.inDungeon() || !Phase.inP5() || pickedupRelic == null) return ActionResult.PASS;
            BlockState block = world.getBlockState(hitResult.getBlockPos());
            onBlockClick(block, hitResult.getBlockPos());
            return ActionResult.PASS;
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(worldRenderContext -> {
            if (pickedupRelic == null || !Floor7.renderRelicHighlight) return;

            Camera camera = worldRenderContext.camera();
            Vec3d cameraPos = camera.getPos();

            MatrixStack matrixStack = worldRenderContext.matrixStack();
            if (matrixStack == null) return;
            matrixStack.push();
            matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            VertexConsumerProvider consumers = worldRenderContext.consumers();
            if (consumers == null) return;
            VertexConsumer buffer = consumers.getBuffer(RenderLayers.FILLED_LAYER);


            Box box = pickedupRelic.box;
            float[] color = RenderUtils.toFloats(pickedupRelic.color);
            VertexRendering.drawFilledBox(matrixStack, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color[0], color[1], color[2], color[3]);


            matrixStack.pop();
        });

    }

    private static void onBlockClick(BlockState block, BlockPos pos) {
        if (block.getBlock() != Blocks.ANVIL && block.getBlock() != Blocks.CAULDRON) return;
        double distance = pickedupRelic.box.getCenter().distanceTo(pos.toCenterPos());
        if (distance <= 1) {
            if (Floor7.enableRelicPlaceTime) {
            double diff = (System.currentTimeMillis() - pickupTime) / 1000.0;
                Misc.addChatMessage(Text.literal("The ").formatted(Formatting.GREEN)
                        .append(Text.literal(pickedupRelic.name().toLowerCase()).withColor(pickedupRelic.color))
                        .append(" relic was placed in ").formatted(Formatting.GREEN)
                        .append(Text.literal(Constants.DECIMAL_FORMAT.format(diff) + "s.").formatted(Formatting.YELLOW)));
            }
            pickedupRelic = null;
        }


    }

    public static boolean display() {
        return  Floor7.enableRelicStartTimer && Location.inDungeon() && Phase.inP5() && tick > 0;
    }

    public static void render(HUDComponent component, DrawContext context)  {
        int x = component.getScaledX();
        int y = component.getScaledY();

        int color = tick > 7 ? GREEN_COLOR : RED_COLOR;

        double num = tick * Constants.TICK_DURATION;

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(num)), x, y, component.getWidth(), color);

    }
}
