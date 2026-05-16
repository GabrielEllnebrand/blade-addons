package blade.addon.utils.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

public interface RenderingEvent {
    void render(WorldRenderContext context, PoseStack matrixStack, VertexConsumer consumer);

}
