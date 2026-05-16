package blade.addon.utils.rendering;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

    public static void drawText(GuiGraphics context, HUDComponent component, Component text, int color) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        context.drawString(textRenderer, text, component.getScaledX(), component.getScaledY(), color, true);
    }

    public static void drawCenteredText(GuiGraphics context, Font textRenderer, Component text, int x, int y, int maxWidth, int color) {
        if (textRenderer == null) return;
        int centered = (maxWidth - textRenderer.width(text)) / 2;
        context.drawString(textRenderer, text, x + centered, y, color, true);

    }

    public static void drawCenteredText(GuiGraphics context, HUDComponent component, Component text) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        drawCenteredText(context, textRenderer, text, component.getScaledX(), component.getScaledY(), component.getWidth(), 0xffffffff);
    }

    public static void drawCenteredText(GuiGraphics context, HUDComponent component, Component text, int color) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        drawCenteredText(context, textRenderer, text, component.getScaledX(), component.getScaledY(), component.getWidth(), color);
    }

    public static void drawTimer(HUDComponent component, GuiGraphics context, int tick, int color) {
        double num = tick * Constants.TICK_DURATION;
        drawTimer(component, context, num, color);
    }

    public static void drawTimer(HUDComponent component, GuiGraphics context, double num, int color) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        RenderUtils.drawCenteredText(context, Minecraft.getInstance().font, Component.literal(Constants.DECIMAL_FORMAT.format(num)), x, y, component.getWidth(), color);
    }

    public static void drawPrefixedTimer(HUDComponent component, GuiGraphics context, String prefix, int num) {
        drawPrefixedText(component, context, prefix, Constants.DECIMAL_FORMAT.format(num * Constants.TICK_DURATION) + "s");
    }

    public static void drawPrefixedTimer(HUDComponent component, GuiGraphics context, String prefix, double num) {
        drawPrefixedText(component, context, prefix, Constants.DECIMAL_FORMAT.format(num) + "s");
    }

    public static void drawPrefixedText(HUDComponent component, GuiGraphics context, String prefix, String text) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;

        Component drawnText = Component.literal(prefix + ": ").withColor(ExtraOptions.timerPrefixColor).append(Component.literal(text).withColor(0xffffffff));
        context.drawString(textRenderer, drawnText, component.getScaledX(), component.getScaledY(), 0xffffffff, true);
    }

    public static void renderFilled(AABB box, float[] rgba) {
        if (rgba[3] == 0) return;
        Gizmos.cuboid(box, GizmoStyle.fill(ARGB.colorFromFloat(rgba[3], rgba[0], rgba[1], rgba[2]))); // Could be filledAndStroked
        //VertexRendering.drawFilledBox(matrixStack, consumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static void renderOutline(AABB box, float[] rgba) {
        if (rgba[3] == 0) return;
        Gizmos.cuboid(box, GizmoStyle.stroke(ARGB.colorFromFloat(rgba[3], rgba[0], rgba[1], rgba[2]))); // Could be filledAndStroked
        //VertexRendering.drawBox(matrixStack.peek(), consumer, box, rgba[0], rgba[1], rgba[2], rgba[3]);
    }


    public static void renderText(WorldRenderContext context, PoseStack matrices, Component text, double x, double y, double z, float scale) {
        Minecraft client = Minecraft.getInstance();
        Font textRenderer = client.font;
        LocalPlayer player = client.player;
        if (player == null) return;

        matrices.pushPose();
        matrices.translate(x, y, z);
        matrices.mulPose(context.worldState().cameraRenderState.orientation);
        matrices.scale(TEXT_SCALE * scale, -TEXT_SCALE * scale, TEXT_SCALE * scale);

        float halfWidth = textRenderer.width(text.getString()) / 2f;

        context.commandQueue().submitText(matrices, -halfWidth, 0, text.getVisualOrderText(), true, Font.DisplayMode.SEE_THROUGH, 15728880, 0xffffffff, 0, 0);
        matrices.popPose();
    }

    public static void renderText(WorldRenderContext context, PoseStack matrices, Component text, Vec3 pos, float scale) {
        renderText(context, matrices, text, pos.x, pos.y, pos.z, scale);
    }

    public static void renderLineTo(WorldRenderContext context, double x, double y, double z, int color) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Vec3 playerPos = EntityUtil.getLerpedPos(player);
        double eyeHeight = player.getEyeHeight();
        Vector3f lookAt = new Vector3f(0, 0, -1f).rotate(context.worldState().cameraRenderState.orientation);
        Vec3 startPos = playerPos.add(0, eyeHeight, 0).add(lookAt.x, lookAt.y, lookAt.z);
        Gizmos.line(startPos, new Vec3(x, y, z), color);
    }

    public static void renderLineTo(WorldRenderContext context, Vec3 pos, int color) {
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
}
