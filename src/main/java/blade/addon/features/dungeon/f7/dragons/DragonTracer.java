package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

public class DragonTracer {

    public static void init() {
        RenderingEvents.LINE.register(DragonTracer::render);
    }

    private static void render(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!Floor7.dragonTracer) return;

        Dragon dragon = DragSpawnTimer.currentDragon;
        if (dragon == null || dragon == Dragon.NONE || !DragSpawnTimer.hasDoneSplit) return;

        RenderUtils.renderLineTo(context, dragon.spawnPos, dragon.color);
    }
}
