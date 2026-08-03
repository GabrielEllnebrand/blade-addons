package blade.addon.utils.rendering;

import blade.addon.utils.Constants;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.Identifier;

public class RenderPipelines {
    public static final RenderPipeline FILLED_DEBUG = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "filled-debug-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build());

    public static final RenderPipeline FILLED_DEBUG_NO_DEPTH = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "filled-debug-no-depth-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build());



    public static final RenderPipeline FILLED = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "filled-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build());

    public static final RenderPipeline LINE = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "line-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build());


    public static final RenderPipeline FILLED_NO_DEPTH = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "filled-no-depth-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build());


    public static final RenderPipeline LINE_NO_DEPTH = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(blade.addon.utils.Constants.NAMESPACE, "line-no-depth-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build());

    public static final RenderPipeline TEXT = net.minecraft.client.renderer.RenderPipelines.register(RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(blade.addon.utils.Constants.NAMESPACE, "text-pipe"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build());
}
