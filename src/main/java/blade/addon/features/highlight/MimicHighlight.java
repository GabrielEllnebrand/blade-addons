package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MimicHighlight {

    private static final AABB box = new AABB(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375);

    private static final ConcurrentLinkedQueue<TrappedChestBlockEntity> mimics = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_LOCATION_CHANGE.register(location -> {
            mimics.clear();
            return false;
        });

       Events.ON_BLOCK_ENTITY.register(blockEntity -> {
          if (!Location.inDungeon()) return false;
          if (blockEntity instanceof TrappedChestBlockEntity mimic && !mimics.contains(mimic)) {
              mimics.add(mimic);
          }
           return false;
       });

       ClientTickEvents.END_CLIENT_TICK.register(client -> mimics.removeIf(BlockEntity::isRemoved));

        RenderingEvents.FILLED.register(MimicHighlight::renderFilled);
        RenderingEvents.LINE.register(MimicHighlight::renderOutline);
    }


    private static void renderFilled(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.highlightMimicChests || !MobHighlight.renderFilled()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicFilledColor);
        mimics.forEach(mimic -> RenderUtils.renderFilledBox(matrixStack, consumer, box.move(mimic.getBlockPos()), rgba));
    }

    private static void renderOutline(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.highlightMimicChests || !MobHighlight.renderOutline()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicOutlineColor);
        mimics.forEach(mimic -> RenderUtils.renderOutlinedBox(matrixStack, consumer,box.move(mimic.getBlockPos()), rgba));
    }
}
