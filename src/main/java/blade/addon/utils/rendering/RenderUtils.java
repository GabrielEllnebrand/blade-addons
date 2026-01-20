package blade.addon.utils.rendering;

import blade.addon.utils.Constants;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

public class RenderUtils {
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
    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int maxWidth) {
        drawCenteredText(context, textRenderer, text, x, y, maxWidth, 0xffffffff);
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, String string, int x, int y, int maxWidth, int color) {
        drawCenteredText(context, textRenderer, Text.literal(string), x, y, maxWidth, color);
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, String string, int x, int y, int maxWidth) {
        drawCenteredText(context, textRenderer, Text.literal(string), x, y, maxWidth, 0xffffffff);
    }

    public static void drawCenteredText(DrawContext context, HUDComponent component, Text text) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer == null) return;
        drawCenteredText(context, textRenderer, text, component.getScaledX(), component.getScaledY(), component.getWidth());
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

    public static void renderFilled(MatrixStack matrixStack, VertexConsumer consumer, Box box, float[] rgba) {
        VertexRendering.drawFilledBox(matrixStack, consumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static void renderOutline(MatrixStack matrixStack, VertexConsumer consumer, Box box, float[] rgba) {
        VertexRendering.drawBox(matrixStack.peek(), consumer, box, rgba[0], rgba[1], rgba[2], rgba[3]);
    }
}
