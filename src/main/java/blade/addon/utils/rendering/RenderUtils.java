package blade.addon.utils.rendering;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class RenderUtils {
    public static float[] toFloats(int argb) {
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float a = ((argb >> 24) & 0xFF) / 255f;
        return new float[]{r, g, b, a};
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int maxWidth, int color) {
        int centered = (maxWidth - textRenderer.getWidth(text)) / 2;

        context.drawText(textRenderer, text, x + centered, y, color, true);

    }
    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int maxWidth) {
        drawCenteredText(context, textRenderer, text, x, y, maxWidth, 0xffffffff);
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, String string, int x, int y, int maxWidth) {
        drawCenteredText(context, textRenderer, Text.literal(string), x, y, maxWidth, 0xffffffff);
    }
}
