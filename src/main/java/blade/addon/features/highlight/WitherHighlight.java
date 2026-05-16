package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.phys.AABB;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WitherHighlight {

    private static final float WITHER_BORN_HEALTH = 300f;

    private static final ConcurrentLinkedQueue<WitherBoss> withers = new ConcurrentLinkedQueue<>();


    public static void init() {
        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon() || !MobHighlight.mobHighlight) return false;

            if (Phase.inBoss()) {
                if (entity instanceof WitherBoss wither) {
                    if (wither.getHealth() != WITHER_BORN_HEALTH && !withers.contains(wither)) {
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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {withers.removeIf(wither -> wither.isRemoved() || wither.isDeadOrDying());});

        RenderingEvents.FILLED_ENTITY.register(WitherHighlight::renderFilled);
        RenderingEvents.OUTLINE_ENTITY.register(WitherHighlight::renderOutline);
    }

    private static AABB getBox(WitherBoss wither) {
        return EntityUtil.getBox(wither).inflate(MobHighlight.witherExtraWidth, 0, MobHighlight.witherExtraWidth);
    }

    private static void renderFilled(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.mobHighlight || !MobHighlight.renderFilled() || MobHighlight.dontRenderHighlight) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.witherFilledColor);
        withers.forEach(entity -> RenderUtils.renderFilled(getBox(entity), rgba));
    }

    private static void renderOutline(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.mobHighlight || !MobHighlight.renderOutline() || MobHighlight.dontRenderHighlight) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.witherOutlineColor);
        withers.forEach(entity -> RenderUtils.renderOutline(getBox(entity), rgba));
    }

}
