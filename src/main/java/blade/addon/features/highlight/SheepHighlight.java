package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.world.entity.animal.sheep.Sheep;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SheepHighlight {

    private static final ConcurrentLinkedQueue<Sheep> sheeps = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_ENTITY_SPAWNED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof Sheep sheep && !sheeps.contains(sheep)) {
                sheeps.add(sheep);
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(location -> {
            sheeps.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> sheeps.removeIf(sheep -> sheep.isRemoved() || sheep.isDeadOrDying()));

        RenderingEvents.FILLED.register(SheepHighlight::renderFilled);
        RenderingEvents.LINE.register(SheepHighlight::renderOutline);
    }

    private static void renderFilled(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.highlightSheep || !MobHighlight.renderFilled()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepFilledColor);
        sheeps.forEach(entity -> RenderUtils.renderFilledBox(matrixStack, consumer, EntityUtil.getBox(entity), rgba));
    }

    private static void renderOutline(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.highlightSheep || !MobHighlight.renderOutline()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepFilledColor);
        sheeps.forEach(entity -> RenderUtils.renderOutlinedBox(matrixStack, consumer,EntityUtil.getBox(entity), rgba));
    }

}
