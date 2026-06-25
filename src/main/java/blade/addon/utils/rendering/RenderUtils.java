package blade.addon.utils.rendering;

import blade.addon.features.highlight.MobHighlight;
import blade.addon.utils.Constants;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
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

    public static void drawText(GuiGraphicsExtractor graphics, HUDComponent component, Component text, int color) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        graphics.text(textRenderer, text, component.getScaledX(), component.getScaledY(), color, true);
    }

    public static void drawCenteredText(GuiGraphicsExtractor graphics, Font textRenderer, Component text, int x, int y, int maxWidth, int color) {
        if (textRenderer == null) return;
        int centered = (maxWidth - textRenderer.width(text)) / 2;
        graphics.text(textRenderer, text, x + centered, y, color, true);

    }

    public static void drawCenteredText(GuiGraphicsExtractor graphics, HUDComponent component, Component text) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        drawCenteredText(graphics, textRenderer, text, component.getScaledX(), component.getScaledY(), component.getWidth(), 0xffffffff);
    }

    public static void drawCenteredText(GuiGraphicsExtractor graphics, HUDComponent component, Component text, int color) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        drawCenteredText(graphics, textRenderer, text, component.getScaledX(), component.getScaledY(), component.getWidth(), color);
    }

    public static void drawTimer(HUDComponent component, GuiGraphicsExtractor graphics, int tick, int color) {
        double num = tick * Constants.TICK_DURATION;
        drawTimer(component, graphics, num, color);
    }

    public static void drawTimer(HUDComponent component, GuiGraphicsExtractor graphics, double num, int color) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        RenderUtils.drawCenteredText(graphics, Minecraft.getInstance().font, Component.literal(Constants.DECIMAL_FORMAT.format(num)), x, y, component.getWidth(), color);
    }

    public static void drawPrefixedTimer(HUDComponent component, GuiGraphicsExtractor graphics, String prefix, int num) {
        drawPrefixedText(component, graphics, prefix, Constants.DECIMAL_FORMAT.format(num * Constants.TICK_DURATION) + "s");
    }

    public static void drawPrefixedTimer(HUDComponent component, GuiGraphicsExtractor graphics, String prefix, double num) {
        drawPrefixedText(component, graphics, prefix, Constants.DECIMAL_FORMAT.format(num) + "s");
    }

    public static void drawPrefixedText(HUDComponent component, GuiGraphicsExtractor graphics, String prefix, String text) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;

        Component drawnText = Component.literal(prefix + ": ").withColor(ExtraOptions.timerPrefixColor).append(Component.literal(text).withColor(0xffffffff));
        graphics.text(textRenderer, drawnText, component.getScaledX(), component.getScaledY(), 0xffffffff, true);
    }

    public static void renderText(LevelRenderContext context, PoseStack matrices, Component text, double x, double y, double z, float scale) {
        Minecraft client = Minecraft.getInstance();
        Font textRenderer = client.font;
        LocalPlayer player = client.player;
        if (player == null) return;

        matrices.pushPose();
        matrices.translate(x, y, z);
        matrices.mulPose(context.levelState().cameraRenderState.orientation);
        matrices.scale(TEXT_SCALE * scale, -TEXT_SCALE * scale, TEXT_SCALE * scale);

        float halfWidth = textRenderer.width(text.getString()) / 2f;


        //context.commandQueue().submitText(matrices, -halfWidth, 0, text.getVisualOrderText(), true, Font.DisplayMode.SEE_THROUGH, 15728880, 0xffffffff, 0, 0);
        textRenderer.drawInBatch(text, -halfWidth, 0, 15728880, true, matrices.last().pose(), context.bufferSource(), Font.DisplayMode.SEE_THROUGH, 0, 0xffffffff);
        matrices.popPose();
    }

    public static void renderText(LevelRenderContext context, PoseStack matrices, Component text, Vec3 pos, float scale) {
        renderText(context, matrices, text, pos.x, pos.y, pos.z, scale);
    }

    public static void renderLineTo(LevelRenderContext context, double x, double y, double z, int color) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Vec3 playerPos = EntityUtil.getLerpedPos(player);
        double eyeHeight = player.getEyeHeight();
        Vector3f lookAt = new Vector3f(0, 0, -1f).rotate(context.levelState().cameraRenderState.orientation);
        Vec3 startPos = playerPos.add(0, eyeHeight, 0).add(lookAt.x, lookAt.y, lookAt.z);
        Gizmos.line(startPos, new Vec3(x, y, z), color);
    }

    public static void renderLineTo(LevelRenderContext context, Vec3 pos, int color) {
        renderLineTo(context, pos.x, pos.y, pos.z, color);
    }

    public static String formatNumber(float num) {
        if (Floor7.capitalizeHealthNumbers) {
            if (num >= 1e9) return String.format("%.1fB", num / 1e9);
            if (num >= 1e6) return String.format("%.1fM", num / 1e6);
            if (num >= 1e3) return String.format("%.1fK", num / 1e3);
        } else {
            if (num >= 1e9) return String.format("%.1fb", num / 1e9);
            if (num >= 1e6) return String.format("%.1fm", num / 1e6);
            if (num >= 1e3) return String.format("%.1fk", num / 1e3);
        }
        return num + "";
    }

    private static void quad(Matrix4f matrix,
                             VertexConsumer consumer,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             double x4, double y4, double z4,

                             float r, float g, float b, float a) {

        consumer.addVertex(matrix, (float) x1, (float) y1, (float) z1)
                .setColor(r, g, b, a)
                .setNormal(0, 1, 0);

        consumer.addVertex(matrix, (float) x2, (float) y2, (float) z2)
                .setColor(r, g, b, a)
                .setNormal(0, 1, 0);

        consumer.addVertex(matrix, (float) x3, (float) y3, (float) z3)
                .setColor(r, g, b, a)
                .setNormal(0, 1, 0);

        consumer.addVertex(matrix, (float) x4, (float) y4, (float) z4)
                .setColor(r, g, b, a)
                .setNormal(0, 1, 0);

    }

    private static void verticalLine(Matrix4f matrix,
                                     VertexConsumer consumer,
                                     double x1, double y1, double z1,
                                     double x2, double y2, double z2,
                                     float r, float g, float b, float a, float width) {

        consumer.addVertex(matrix, (float) x1, (float) y1, (float) z1)
                .setColor(r, g, b, a)
                .setNormal(1, 0, 0).setLineWidth(width);

        consumer.addVertex(matrix, (float) x2, (float) y2, (float) z2)
                .setColor(r, g, b, a)
                .setNormal(1, 0, 0).setLineWidth(width);

        consumer.addVertex(matrix, (float) x1, (float) y1, (float) z1)
                .setColor(r, g, b, a)
                .setNormal(0, 0, 1).setLineWidth(width);

        consumer.addVertex(matrix, (float) x2, (float) y2, (float) z2)
                .setColor(r, g, b, a)
                .setNormal(0, 0, 1).setLineWidth(width);
    }

    private static void horizontalLine(Matrix4f matrix,
                                       VertexConsumer consumer,
                                       double x1, double y1, double z1,
                                       double x2, double y2, double z2,
                                       float r, float g, float b, float a, float width) {

        consumer.addVertex(matrix, (float) x1, (float) y1, (float) z1)
                .setColor(r, g, b, a)
                .setNormal(0, 1, 0).setLineWidth(width);

        consumer.addVertex(matrix, (float) x2, (float) y2, (float) z2)
                .setColor(r, g, b, a)
                .setNormal(0, 1, 0).setLineWidth(width);
    }

    public static void renderOutlinedBox(PoseStack matrices,
                                       VertexConsumer consumer,
                                       AABB box,
                                       float[] rgba) {


        if (rgba[3] == 0) return;
        float r = rgba[0];
        float g = rgba[1];
        float b = rgba[2];
        float a = rgba[3];

        Matrix4f matrix = matrices.last().pose();

        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        horizontalLine(matrix, consumer, minX, minY, minZ, minX, maxY, minZ, r, g, b, a, MobHighlight.outlineWidth);
        horizontalLine(matrix, consumer, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a, MobHighlight.outlineWidth);
        horizontalLine(matrix, consumer, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a, MobHighlight.outlineWidth);
        horizontalLine(matrix, consumer, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a, MobHighlight.outlineWidth);

        verticalLine(matrix, consumer, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a, MobHighlight.outlineWidth);
        verticalLine(matrix, consumer, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a, MobHighlight.outlineWidth);
        verticalLine(matrix, consumer, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a, MobHighlight.outlineWidth);
        verticalLine(matrix, consumer, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a, MobHighlight.outlineWidth);

        verticalLine(matrix, consumer, minX, minY, minZ, maxX, minY, minZ, r, g, b, a, MobHighlight.outlineWidth);
        verticalLine(matrix, consumer, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a, MobHighlight.outlineWidth);
        verticalLine(matrix, consumer, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a, MobHighlight.outlineWidth);
        verticalLine(matrix, consumer, minX, minY, maxZ, minX, minY, minZ, r, g, b, a, MobHighlight.outlineWidth);


    }

    public static void renderFilledBox(PoseStack matrices,
                                     VertexConsumer consumer,
                                     AABB box,
                                     float[] rgba) {

        if (rgba[3] == 0) return;
        float r = rgba[0];
        float g = rgba[1];
        float b = rgba[2];
        float a = rgba[3];

        Matrix4f matrix = matrices.last().pose();

        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        quad(matrix, consumer, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, r, g, b, a);
        quad(matrix, consumer, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, r, g, b, a);

        quad(matrix, consumer, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, minX, minY, maxZ, r, g, b, a);
        quad(matrix, consumer, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ, r, g, b, a);

        quad(matrix, consumer, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
        quad(matrix, consumer, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

    }

    public static void renderFilledBlock(AABB box, int argb) {
        Gizmos.cuboid(box, GizmoStyle.fill(argb));
    }
}
