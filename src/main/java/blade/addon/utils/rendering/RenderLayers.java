package blade.addon.utils.rendering;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class RenderLayers {

    public static final RenderType FILLED_DEBUG =
            RenderType.create("filled-debug", RenderSetup.builder(RenderPipelines.FILLED_DEBUG).createRenderSetup());

    public static final RenderType FILLED =
            RenderType.create("filled", RenderSetup.builder(RenderPipelines.FILLED).createRenderSetup());

    public static final RenderType LINE =
            RenderType.create("line", RenderSetup.builder(RenderPipelines.LINE).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());


    public static final RenderType FILLED_NO_DEPTH =
            RenderType.create("filled-no-depth", RenderSetup.builder(RenderPipelines.FILLED_NO_DEPTH).createRenderSetup());

    public static final RenderType LINE_NO_DEPTH =
            RenderType.create("line-no-depth", RenderSetup.builder(RenderPipelines.LINE_NO_DEPTH).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());


}
