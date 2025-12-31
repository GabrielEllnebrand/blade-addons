package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MimicHighlight {

    private static final Box box = new Box(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375);

    private static final ConcurrentLinkedQueue<TrappedChestBlockEntity> mimicList = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_LOCATION_CHANGE.register(location -> {
            mimicList.clear();
        });

       Events.ON_BLOCK_ENTITY.register(blockEntity -> {
          if (!Location.inDungeon()) return;
          if (blockEntity instanceof TrappedChestBlockEntity mimic) {
              mimicList.add(mimic);
          }
       });

       ClientTickEvents.END_CLIENT_TICK.register(client -> {mimicList.removeIf(BlockEntity::isRemoved);});

        WorldRenderEvents.AFTER_ENTITIES.register(MimicHighlight::renderFilled);
        WorldRenderEvents.AFTER_ENTITIES.register(MimicHighlight::renderOutline);
    }

    private static void renderFilled(WorldRenderContext context) {
        if (!MobHighlight.highlightMimicChests) return;
        if (!MobHighlight.renderFilled()) return;
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer filledConsumer = consumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);

        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicFilledColor);
        mimicList.forEach(blockEntity -> {
            Box renderBox = box.offset(blockEntity.getPos());
            VertexRendering.drawFilledBox(matrices, filledConsumer, renderBox.minX, renderBox.minY, renderBox.minZ, renderBox.maxX, renderBox.maxY, renderBox.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
        });

        matrices.pop();

    }

    private static void renderOutline(WorldRenderContext context) {
        if (!MobHighlight.highlightMimicChests) return;
        if (!MobHighlight.renderOutline()) return;
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer outlineConsumer = consumers.getBuffer(RenderLayers.getOutline(MobHighlight.outlineWidth));

        MatrixStack.Entry entry = matrices.peek();
        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicOutlineColor);
        mimicList.forEach(blockEntity -> {
            Box renderBox = box.offset(blockEntity.getPos());
            VertexRendering.drawBox(entry, outlineConsumer, renderBox, rgba[0], rgba[1], rgba[2], rgba[3]);
        });

        matrices.pop();

    }
}
