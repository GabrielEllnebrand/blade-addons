package blade.addon.utils.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface RenderingEvent {
    void render(WorldRenderContext context, MatrixStack matrixStack, VertexConsumer consumer);

}
