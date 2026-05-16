package blade.addon.utils.rendering;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.HashMap;

public class RenderLayers {

    private static final HashMap<Integer, RenderType> OUTLINE_WIDTHS = new HashMap<>();
    private static final HashMap<Integer, RenderType> OUTLINE_WIDTHS_NO_DEPTH = new HashMap<>();

    public static final RenderType FILLED_LAYER =
            RenderType.create("filled", RenderSetup.builder(RenderPipelines.FILLED_PIPELINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());


    public static final RenderType FILLED_LAYER_NO_DEPTH =
            RenderType.create("through_wall_filled", RenderSetup.builder(RenderPipelines.FILLED_PIPELINE_NO_DEPTH).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());

    public static final RenderType FILLED_ENTITY_LAYER =
            RenderType.create("filled-entity", RenderSetup.builder(RenderPipelines.FILLED_ENTITY_PIPELINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());

    public static RenderType getOutline(int width, boolean depthCheck) {
        if (depthCheck) {
            if (OUTLINE_WIDTHS.containsKey(width)) {
                return OUTLINE_WIDTHS.get(width);
            }

            RenderType outline_layer =
                    RenderType.create("outline-entity-" + width, RenderSetup.builder(RenderPipelines.OUTLINE_ENTITY_PIPELINE).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());

            OUTLINE_WIDTHS.put(width, outline_layer);
            return outline_layer;
        } else {
            if (OUTLINE_WIDTHS_NO_DEPTH.containsKey(width)) {
                return OUTLINE_WIDTHS_NO_DEPTH.get(width);
            }

            RenderType outline_layer =
                    RenderType.create("outline-entity-" + width, RenderSetup.builder(RenderPipelines.OUTLINE_ENTITY_PIPELINE).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());


            OUTLINE_WIDTHS_NO_DEPTH.put(width, outline_layer);
            return outline_layer;
        }

    }


}
