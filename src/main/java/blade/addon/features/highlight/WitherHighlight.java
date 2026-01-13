package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.dungeon.Phase;
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
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ConcurrentLinkedQueue;

public class WitherHighlight {

    private static final float WITHER_BORN_HEALTH = 300f;

    private static final ConcurrentLinkedQueue<WitherEntity> withers = new ConcurrentLinkedQueue<>();


    public static void init() {
        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon() || !MobHighlight.mobHighlight) return false;

            if (Phase.inBoss()) {
                if (entity instanceof WitherEntity wither) {
                    if (wither.getHealth() != WITHER_BORN_HEALTH) {
                        withers.add(wither);
                    }
                }
            }

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(location -> {
            withers.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> withers.removeIf(wither -> wither.isRemoved() || wither.isDead()));

        WorldRenderEvents.AFTER_ENTITIES.register(WitherHighlight::renderFilled);
        WorldRenderEvents.AFTER_ENTITIES.register(WitherHighlight::renderOutline);
    }

    private static void renderFilled(WorldRenderContext context) {
        if (!MobHighlight.mobHighlight) return;
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

        float[] rgba = RenderUtils.toFloats(MobHighlight.witherFilledColor);
        withers.forEach(entity -> {

            Vec3d pos = Misc.getPos(entity, tickProgress);

            EntityDimensions dimension = entity.getDimensions(entity.getPose());
            Box box = dimension.getBoxAt(pos).expand(MobHighlight.witherExtraWidth, 0, MobHighlight.witherExtraWidth);

            VertexRendering.drawFilledBox(matrices, filledConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
        });

        matrices.pop();

    }

    private static void renderOutline(WorldRenderContext context) {
        if (!MobHighlight.mobHighlight) return;
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
        float[] rgba = RenderUtils.toFloats(MobHighlight.witherOutlineColor);

        withers.forEach(entity -> {

            Vec3d pos = Misc.getPos(entity, tickProgress);

            EntityDimensions dimension = entity.getDimensions(entity.getPose());
            Box box = dimension.getBoxAt(pos).expand(MobHighlight.witherExtraWidth, 0, MobHighlight.witherExtraWidth);

            VertexRendering.drawBox(entry, outlineConsumer, box, rgba[0], rgba[1], rgba[2], rgba[3]);
        });

        matrices.pop();

    }

}
