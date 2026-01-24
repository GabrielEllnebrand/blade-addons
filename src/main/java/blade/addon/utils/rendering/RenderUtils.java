package blade.addon.utils.rendering;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.ExtraOptions;
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

public class RenderUtils {

    private static final float TEXT_SCALE = 0.025f;

    public static float[] toFloats(int argb) {
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float a = ((argb >> 24) & 0xFF) / 255f;
        return new float[]{r, g, b, a};
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

    public static void drawPrefixedTimer(HUDComponent component, DrawContext context, double num, String text) {
        Text drawnText = Text.literal(text + ": ").withColor(ExtraOptions.timerPrefixColor).append(Text.literal(Constants.DECIMAL_FORMAT.format(num) + "s").withColor(0xffffffff));
        RenderUtils.drawCenteredText(context, component, drawnText);
    }

    public static void renderFilled(MatrixStack matrixStack, VertexConsumer consumer, Box box, float[] rgba) {
        VertexRendering.drawFilledBox(matrixStack, consumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static void renderOutline(MatrixStack matrixStack, VertexConsumer consumer, Box box, float[] rgba) {
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
}
