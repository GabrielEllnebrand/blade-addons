package blade.addon.utils.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RenderHandler {

    private final RenderType renderType;

    public RenderHandler(RenderType renderType)  {
        this.renderType = renderType;
    }

    private final List<RenderingEvent> listeners = new ArrayList<>();

    public void register(RenderingEvent listener) {
        listeners.add(listener);
    }

    public void invoke(Consumer<RenderingEvent> action) {
        if (listeners.isEmpty()) return;
        for (RenderingEvent listener : listeners) {
            action.accept(listener);
        }
    }

    public void init(WorldRenderContext context) {
        LevelRenderState worldState = context.worldState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(renderType);

        invoke(renderingEvent -> renderingEvent.render(context, matrices, consumer));
        matrices.popPose();
    }

}
