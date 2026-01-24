package blade.addon.utils.rendering;

import blade.addon.features.highlight.MobHighlight;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class RenderingEvents {

    public static RenderHandler FILLED_BLOCK = new RenderHandler();
    public static RenderHandler NO_DEPTH_FILLED = new RenderHandler();
    public static RenderHandler FILLED_ENTITY = new RenderHandler();
    public static RenderHandler OUTLINE_ENTITY = new RenderHandler();
    public static RenderHandler NO_DEPTH_OUTLINE_ENTITY = new RenderHandler();

    public static void init() {
        WorldRenderEvents.BEFORE_ENTITIES.register(RenderingEvents::filled);
        WorldRenderEvents.BEFORE_ENTITIES.register(RenderingEvents::filledNoDepth);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityFilled);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityOutline);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderingEvents::entityOutlineNoDepth);
    }

    private static void filled(WorldRenderContext context) {
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER);

        FILLED_BLOCK.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

    private static void filledNoDepth(WorldRenderContext context) {
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_LAYER_NO_DEPTH);

        NO_DEPTH_FILLED.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

    private static void entityFilled(WorldRenderContext context) {
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);

        FILLED_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }


    private static void entityOutline(WorldRenderContext context) {
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(MobHighlight.outlineWidth, true));

        OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

    private static void entityOutlineNoDepth(WorldRenderContext context) {
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(RenderLayers.getOutline(MobHighlight.outlineWidth, false));

        NO_DEPTH_OUTLINE_ENTITY.invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.pop();
    }

}
