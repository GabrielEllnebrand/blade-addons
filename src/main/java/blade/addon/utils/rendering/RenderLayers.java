package blade.addon.utils.rendering;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class RenderLayers {

    public static final RenderType FILLED_DEBUG =
            RenderType.create("filled-debug-layer", RenderSetup.builder(RenderPipelines.FILLED_DEBUG).createRenderSetup());

    public static final RenderType FILLED_DEBUG_NO_DEPTH =
            RenderType.create("filled-debug-no-depth-layer", RenderSetup.builder(RenderPipelines.FILLED_DEBUG).createRenderSetup());

    public static final RenderType FILLED =
            RenderType.create("filled-layer", RenderSetup.builder(RenderPipelines.FILLED).createRenderSetup());

    public static final RenderType LINE =
            RenderType.create("line-layer", RenderSetup.builder(RenderPipelines.LINE).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());


    public static final RenderType FILLED_NO_DEPTH =
            RenderType.create("filled-no-depth-layer", RenderSetup.builder(RenderPipelines.FILLED_NO_DEPTH).createRenderSetup());

    public static final RenderType LINE_NO_DEPTH =
            RenderType.create("line-no-depth-layer", RenderSetup.builder(RenderPipelines.LINE_NO_DEPTH).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());


}
