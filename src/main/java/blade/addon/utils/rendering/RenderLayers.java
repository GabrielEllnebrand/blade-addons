package blade.addon.utils.rendering;

import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;

import java.util.HashMap;
import java.util.OptionalDouble;

public class RenderLayers {

    private static final HashMap<Integer, RenderLayer> OUTLINE_WIDTHS = new HashMap<>();
    private static final HashMap<Integer, RenderLayer> OUTLINE_WIDTHS_NO_DEPTH = new HashMap<>();

    public static final RenderLayer FILLED_LAYER =
            RenderLayer.of("filled", RenderSetup.builder(RenderPipelines.FILLED_PIPELINE).layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).build());


    public static final RenderLayer FILLED_LAYER_NO_DEPTH =
            RenderLayer.of("through_wall_filled", RenderSetup.builder(RenderPipelines.FILLED_PIPELINE_NO_DEPTH).layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).build());

    public static final RenderLayer FILLED_ENTITY_LAYER =
            RenderLayer.of("filled-entity", RenderSetup.builder(RenderPipelines.FILLED_ENTITY_PIPELINE).layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).build());

    public static RenderLayer getOutline(int width, boolean depthCheck) {
        if (depthCheck) {
            if (OUTLINE_WIDTHS.containsKey(width)) {
                return OUTLINE_WIDTHS.get(width);
            }

            RenderLayer outline_layer =
                    RenderLayer.of("outline-entity-" + width, RenderSetup.builder(RenderPipelines.OUTLINE_ENTITY_PIPELINE).outlineMode(RenderSetup.OutlineMode.IS_OUTLINE).layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).build());

            OUTLINE_WIDTHS.put(width, outline_layer);
            return outline_layer;
        } else {
            if (OUTLINE_WIDTHS_NO_DEPTH.containsKey(width)) {
                return OUTLINE_WIDTHS_NO_DEPTH.get(width);
            }

            RenderLayer outline_layer =
                    RenderLayer.of("outline-entity-" + width, RenderSetup.builder(RenderPipelines.OUTLINE_ENTITY_PIPELINE).outlineMode(RenderSetup.OutlineMode.IS_OUTLINE).layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).build());


            OUTLINE_WIDTHS_NO_DEPTH.put(width, outline_layer);
            return outline_layer;
        }

    }


}
