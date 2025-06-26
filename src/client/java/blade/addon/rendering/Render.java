package blade.addon.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import blade.addon.BugScan;
import blade.addon.BushScan;
import blade.addon.config.Config;
import blade.addon.utility.Constants;
import blade.addon.utility.Location;
import blade.addon.utility.Locations;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class Render {


    private static final RenderPipeline THROUGH_WALL_FILLED_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(Identifier.of(Constants.NAMESPACE, "through-walls-filled"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build());

    private static final RenderPipeline FILLED_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(Identifier.of(Constants.NAMESPACE, "filled"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .build());

    private static final RenderLayer.MultiPhase THROUGH_WALL_FILLED_LAYER =
            RenderLayer.of("through_wall_filled", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, THROUGH_WALL_FILLED_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false));

    private static final RenderLayer.MultiPhase FILLED_LAYER =
            RenderLayer.of("filled", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, FILLED_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false));


    public static void beforeDebugRendering(@NotNull WorldRenderContext context) {
        if(Location.in(Locations.GALATEA)) {
            bushRender(context);
            bugRender(context);
        }
    }

    public static RenderLayer getRenderLayer() {
        return Config.configInstance.seeThroughRendering ? THROUGH_WALL_FILLED_LAYER : FILLED_LAYER;
    }

    private static void bushRender(@NotNull WorldRenderContext context) {

        if (!Config.configInstance.enableBush) return;

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();

        MatrixStack matrixStack = context.matrixStack();
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer buffer = consumers.getBuffer(getRenderLayer());

        float[] rgba = new float[4];
        Config.configInstance.growingColor.getComponents(rgba);
        for (BlockPos pos : BushScan.getGrowing()) {
            VertexRendering.drawFilledBox(matrixStack, buffer, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, rgba[0], rgba[1], rgba[2], rgba[3]);
        }

        Config.configInstance.floweringColor.getComponents(rgba);
        for (BlockPos pos : BushScan.getFlowering()) {
            VertexRendering.drawFilledBox(matrixStack, buffer, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, rgba[0], rgba[1], rgba[2], rgba[3]);
        }

        matrixStack.pop();
    }

    private static void bugRender(@NotNull WorldRenderContext context) {

        if (!Config.configInstance.enableBug) return;

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();

        MatrixStack matrixStack = context.matrixStack();
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer buffer = consumers.getBuffer(getRenderLayer());

        float[] rgba = new float[4];
        Config.configInstance.bugColor.getComponents(rgba);
        for(Box box : BugScan.getPossibleBugs()){
            //VertexRendering.drawFilledBox(matrixStack, buffer, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 0.4, pos.getY() + 0.4, pos.getZ() + 0.4, rgba[0], rgba[1], rgba[2], rgba[3]);
            VertexRendering.drawFilledBox(matrixStack, buffer, box.minX, box.minY, box.minZ, box.minX + 0.4, box.minY + 0.4, box.minZ + 0.4, rgba[0], rgba[1], rgba[2], rgba[3]);
        }

        matrixStack.pop();
    }


}
