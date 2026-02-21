package blade.addon.utils.rendering;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class RenderUtils {

    private static final float TEXT_SCALE = 0.025f;

    public static float[] toFloats(int argb) {
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float a = ((argb >> 24) & 0xFF) / 255f;
        return new float[]{r, g, b, a};
    }

    public static int getStatusColor(int minGreen, int minOrange, int value) {
        return value >= minGreen ? Constants.GREEN : value >= minOrange ? Constants.GOLD : Constants.RED;
    }

    public static void drawText(DrawContext context, HUDComponent component, Text text, int color) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer == null) return;
        context.drawText(textRenderer, text, component.getScaledX(), component.getScaledY(), color, true);
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int maxWidth, int color) {
        if (textRenderer == null) return;
        int centered = (maxWidth - textRenderer.getWidth(text)) / 2;
        context.drawText(textRenderer, text, x + centered, y, color, true);

    }

    public static void drawCenteredText(DrawContext context, HUDComponent component, Text text) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer == null) return;
        drawCenteredText(context, textRenderer, text, component.getScaledX(), component.getScaledY(), component.getWidth(), 0xffffffff);
    }

    public static void drawCenteredText(DrawContext context, HUDComponent component, Text text, int color) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer == null) return;
        drawCenteredText(context, textRenderer, text, component.getScaledX(), component.getScaledY(), component.getWidth(), color);
    }

    public static void drawTimer(HUDComponent component, DrawContext context, int tick, int color) {
        double num = tick * Constants.TICK_DURATION;
        drawTimer(component, context, num, color);
    }

    public static void drawTimer(HUDComponent component, DrawContext context, double num, int color) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(num)), x, y, component.getWidth(), color);
    }

    public static void drawPrefixedTimer(HUDComponent component, DrawContext context, String prefix, int num) {
        drawPrefixedText(component, context, prefix, Constants.DECIMAL_FORMAT.format(num * Constants.TICK_DURATION) + "s");
    }

    public static void drawPrefixedTimer(HUDComponent component, DrawContext context, String prefix, double num) {
        drawPrefixedText(component, context, prefix, Constants.DECIMAL_FORMAT.format(num) + "s");
    }

    public static void drawPrefixedText(HUDComponent component, DrawContext context, String prefix, String text) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer == null) return;

        Text drawnText = Text.literal(prefix + ": ").withColor(ExtraOptions.timerPrefixColor).append(Text.literal(text).withColor(0xffffffff));
        context.drawText(textRenderer, drawnText, component.getScaledX(), component.getScaledY(), 0xffffffff, true);
    }

    public static void renderFilled(MatrixStack matrixStack, VertexConsumer consumer, Box box, float[] rgba) {
        if (rgba[3] == 0) return;
        VertexRendering.drawFilledBox(matrixStack, consumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static void renderOutline(MatrixStack matrixStack, VertexConsumer consumer, Box box, float[] rgba) {
        if (rgba[3] == 0) return;
        VertexRendering.drawBox(matrixStack.peek(), consumer, box, rgba[0], rgba[1], rgba[2], rgba[3]);
    }


    public static void renderText(WorldRenderContext context, MatrixStack matrices, Text text, double x, double y, double z, float scale) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(context.worldState().cameraRenderState.orientation);
        matrices.scale(TEXT_SCALE * scale, -TEXT_SCALE * scale, TEXT_SCALE * scale);

        float halfWidth = textRenderer.getWidth(text.getString()) / 2f;

        context.commandQueue().submitText(matrices, -halfWidth, 0, text.asOrderedText(), true, TextRenderer.TextLayerType.SEE_THROUGH, 15728880, 0xffffffff, 0, 0);
        matrices.pop();
    }

    public static void renderText(WorldRenderContext context, MatrixStack matrices, Text text, Vec3d pos, float scale) {
        renderText(context, matrices, text, pos.x, pos.y, pos.z, scale);
    }

    public static void renderLineTo(WorldRenderContext context, MatrixStack matrices, VertexConsumer consumer, double x, double y, double z, int color) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        Vec3d playerPos = EntityUtil.getLerpedPos(player);
        double eyeHeight = player.getStandingEyeHeight();
        Vector3f lookat = new Vector3f(0, 0, -1f).rotate(context.worldState().cameraRenderState.orientation);

        Vector3f startPos = playerPos.toVector3f().add(0f, (float)eyeHeight, 0f).add(lookat);
        Vec3d endPos = new Vec3d(x, y, z).subtract(playerPos).subtract(lookat.x, lookat.y + eyeHeight, lookat.z);
        VertexRendering.drawVector(matrices, consumer, startPos, endPos, color);
    }

    public static void renderLineTo(WorldRenderContext context, MatrixStack matrices, VertexConsumer consumer, Vec3d pos, int color) {
        renderLineTo(context, matrices, consumer, pos.x, pos.y, pos.z, color);
    }

        public static String formatNumber(float num) {
        if (Floor7.capitalizeHealthNumbers) {
            if (num >= 1e9) return String.format("%.1fB", num / 1e9);
            if (num >= 1e6) return String.format("%.1fM", num / 1e6);
            if (num >= 1e3) return String.format("%.1fK", num / 1e3);
            return num + "";
        } else {
            if (num >= 1e9) return String.format("%.1fb", num / 1e9);
            if (num >= 1e6) return String.format("%.1fm", num / 1e6);
            if (num >= 1e3) return String.format("%.1fk", num / 1e3);
            return num + "";
        }
    }
}
