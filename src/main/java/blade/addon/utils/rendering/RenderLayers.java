package blade.addon.utils.rendering;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import java.util.OptionalDouble;

public class RenderLayers {
    public static final RenderLayer.MultiPhase FILLED_LAYER =
            RenderLayer.of("filled", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, RenderPipelines.FILLED_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false));

    public static final RenderLayer.MultiPhase FILLED_ENTITY_LAYER =
            RenderLayer.of("filled-entity", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, RenderPipelines.FILLED_ENTITY_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false));

    public static final RenderLayer.MultiPhase OUTLINE_ENTITY_LAYER =
            RenderLayer.of("filled-entity", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, RenderPipelines.OUTLINE_ENTITY_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(10)))
                    .build(false));
}
