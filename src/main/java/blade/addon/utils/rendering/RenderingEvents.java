package blade.addon.utils.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class RenderingEvents {

    public static RenderHandler FILLED_DEBUG = new RenderHandler(RenderLayers.FILLED_DEBUG);
    public static RenderHandler FILLED_DEBUG_NO_DEPTH = new RenderHandler(RenderLayers.FILLED_DEBUG_NO_DEPTH);
    public static RenderHandler FILLED = new RenderHandler(RenderLayers.FILLED);
    public static RenderHandler LINE = new RenderHandler(RenderLayers.LINE);
    public static RenderHandler FILLED_NO_DEPTH = new RenderHandler(RenderLayers.FILLED_NO_DEPTH);
    public static RenderHandler LINE_NO_DEPTH = new RenderHandler(RenderLayers.LINE_NO_DEPTH);


    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(FILLED_DEBUG::init);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(FILLED_DEBUG_NO_DEPTH::init);
        WorldRenderEvents.AFTER_ENTITIES.register(FILLED::init);
        WorldRenderEvents.AFTER_ENTITIES.register(LINE::init);
        WorldRenderEvents.AFTER_ENTITIES.register(FILLED_NO_DEPTH::init);
        WorldRenderEvents.AFTER_ENTITIES.register(LINE_NO_DEPTH::init);
    }
}
