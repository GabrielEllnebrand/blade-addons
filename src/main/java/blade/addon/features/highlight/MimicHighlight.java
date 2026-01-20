package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MimicHighlight {

    private static final Box box = new Box(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375);

    private static final ConcurrentLinkedQueue<TrappedChestBlockEntity> mimics = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_LOCATION_CHANGE.register(location -> {
            mimics.clear();
            return false;
        });

       Events.ON_BLOCK_ENTITY.register(blockEntity -> {
          if (!Location.inDungeon()) return false;
          if (blockEntity instanceof TrappedChestBlockEntity mimic) {
              mimics.add(mimic);
          }
           return false;
       });

       ClientTickEvents.END_CLIENT_TICK.register(client -> mimics.removeIf(BlockEntity::isRemoved));

        RenderingEvents.FILLED_ENTITY.register(MimicHighlight::renderFilled);
        RenderingEvents.OUTLINE_ENTITY.register(MimicHighlight::renderOutline);
    }


    private static void renderFilled(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.mobHighlight || !MobHighlight.renderFilled()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicFilledColor);
        mimics.forEach(mimic -> RenderUtils.renderFilled(matrixStack, consumer, box.offset(mimic.getPos()), rgba));
    }

    private static void renderOutline(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.mobHighlight || !MobHighlight.renderOutline()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicOutlineColor);
        mimics.forEach(mimic -> RenderUtils.renderOutline(matrixStack, consumer, box.offset(mimic.getPos()), rgba));
    }
}
