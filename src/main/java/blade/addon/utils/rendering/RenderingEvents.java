package blade.addon.utils.rendering;

import blade.addon.features.highlight.MobHighlight;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.phys.Vec3;

public class RenderingEvents {

    public static RenderHandler FILLED_BLOCK = new RenderHandler();
    public static RenderHandler NO_DEPTH_FILLED = new RenderHandler();
    public static RenderHandler FILLED_ENTITY = new RenderHandler();
    public static RenderHandler OUTLINE_ENTITY = new RenderHandler();
    public static RenderHandler NO_DEPTH_OUTLINE_ENTITY = new RenderHandler();
    public static RenderHandler LINE = new RenderHandler();


    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(RenderingEvents::filled);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(RenderingEvents::filledNoDepth);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityFilled);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityOutline);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityOutlineNoDepth);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(RenderingEvents::debugLine);

    }

    private static void filled(WorldRenderContext context) {
        LevelRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER);

        FILLED_BLOCK.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void filledNoDepth(WorldRenderContext context) {
        LevelRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER_NO_DEPTH);

        NO_DEPTH_FILLED.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void entityFilled(WorldRenderContext context) {
        LevelRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);

        FILLED_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }


    private static void entityOutline(WorldRenderContext context) {
        LevelRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(MobHighlight.outlineWidth, true));

        OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void entityOutlineNoDepth(WorldRenderContext context) {
        LevelRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(MobHighlight.outlineWidth, false));

        NO_DEPTH_OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

    private static void debugLine(WorldRenderContext context) {
        LevelRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3 camera = context.worldState().cameraRenderState.pos;
        PoseStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(4, true));

        LINE.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

}
