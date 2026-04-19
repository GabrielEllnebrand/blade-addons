package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SheepHighlight {

    private static final ConcurrentLinkedQueue<SheepEntity> sheeps = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_ENTITY_SPAWNED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof SheepEntity sheep && !sheeps.contains(sheep)) {
                sheeps.add(sheep);
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(location -> {
            sheeps.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> sheeps.removeIf(sheep -> sheep.isRemoved() || sheep.isDead()));

        RenderingEvents.FILLED_ENTITY.register(SheepHighlight::renderFilled);
        RenderingEvents.OUTLINE_ENTITY.register(SheepHighlight::renderOutline);
    }

    private static void renderFilled(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.highlightSheep || !MobHighlight.renderFilled()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepFilledColor);
        sheeps.forEach(entity -> RenderUtils.renderFilled(EntityUtil.getBox(entity), rgba));
    }

    private static void renderOutline(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.highlightSheep || !MobHighlight.renderOutline()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepFilledColor);
        sheeps.forEach(entity -> RenderUtils.renderOutline(EntityUtil.getBox(entity), rgba));
    }

}
