package blade.addon.utils.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class RenderingEvents {

    public static RenderHandler FILLED_DEBUG = new RenderHandler(RenderLayers.FILLED_DEBUG);
    public static RenderHandler FILLED_DEBUG_NO_DEPTH = new RenderHandler(RenderLayers.FILLED_DEBUG_NO_DEPTH);
    public static RenderHandler FILLED = new RenderHandler(RenderLayers.FILLED);
    public static RenderHandler LINE = new RenderHandler(RenderLayers.LINE);
    public static RenderHandler FILLED_NO_DEPTH = new RenderHandler(RenderLayers.FILLED_NO_DEPTH);
    public static RenderHandler LINE_NO_DEPTH = new RenderHandler(RenderLayers.LINE_NO_DEPTH);


    public static void init() {
        LevelRenderEvents.COLLECT_SUBMITS.register(FILLED_DEBUG::init);
        LevelRenderEvents.COLLECT_SUBMITS.register(FILLED_DEBUG_NO_DEPTH::init);
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(FILLED::init);
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(LINE::init);
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(FILLED_NO_DEPTH::init);
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(LINE_NO_DEPTH::init);
    }
}
