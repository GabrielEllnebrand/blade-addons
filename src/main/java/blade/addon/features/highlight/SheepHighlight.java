package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SheepHighlight {

    private static final ConcurrentLinkedQueue<SheepEntity> sheepList = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof SheepEntity sheep) {
                sheepList.add(sheep);
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(location -> {
            sheepList.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> sheepList.removeIf(SheepEntity::isRemoved));

        WorldRenderEvents.AFTER_ENTITIES.register(SheepHighlight::renderFilled);
        WorldRenderEvents.AFTER_ENTITIES.register(SheepHighlight::renderOutline);
    }

    private static void renderFilled(WorldRenderContext context) {
        if (!MobHighlight.highlightSheep) return;
        if (!MobHighlight.renderFilled()) return;
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer filledConsumer = consumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);

        double tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);

        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepFilledColor);
        sheepList.forEach(entity -> {

            Vec3d pos = Misc.getPos(entity, tickProgress);

            EntityDimensions dimension = entity.getDimensions(entity.getPose());
            Box box = dimension.getBoxAt(pos);

            VertexRendering.drawFilledBox(matrices, filledConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
        });

        matrices.pop();

    }

    private static void renderOutline(WorldRenderContext context) {
        if (!MobHighlight.highlightSheep) return;
        if (!MobHighlight.renderOutline()) return;
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer outlineConsumer = consumers.getBuffer(RenderLayers.getOutline(MobHighlight.outlineWidth));

        double tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);

        MatrixStack.Entry entry = matrices.peek();
        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepOutlineColor);

        sheepList.forEach(entity -> {

            Vec3d pos = Misc.getPos(entity, tickProgress);

            EntityDimensions dimension = entity.getDimensions(entity.getPose());
            Box box = dimension.getBoxAt(pos);

            VertexRendering.drawBox(entry, outlineConsumer, box, rgba[0], rgba[1], rgba[2], rgba[3]);
        });

        matrices.pop();

    }

}
