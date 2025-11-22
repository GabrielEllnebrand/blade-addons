package blade.addon.utils.rendering;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import java.util.OptionalDouble;

import static blade.addon.utils.rendering.RenderPipelines.THROUGH_WALL_FILLED_PIPELINE;

public class RenderLayers {
    public static final RenderLayer.MultiPhase FILLED_LAYER =
            RenderLayer.of("filled", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, RenderPipelines.FILLED_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false));

    public static final RenderLayer.MultiPhase THROUGH_WALL_FILLED_LAYER =
            RenderLayer.of("through_wall_filled", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, THROUGH_WALL_FILLED_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false));

    public static final RenderLayer.MultiPhase FILLED_ENTITY_LAYER =
            RenderLayer.of("filled-entity", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, RenderPipelines.FILLED_ENTITY_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false));

    public static final RenderLayer.MultiPhase OUTLINE_ENTITY_LAYER =
            RenderLayer.of("outline-entity", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, RenderPipelines.OUTLINE_ENTITY_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(4)))
                    .build(false));
}
