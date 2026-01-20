package blade.addon.features.dungeon.f7.invincibility;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;

public class InvincibilityDuration {

    private static final int MAX_DURATION = 3 * 20;
    private static int ticks = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            ticks = Math.max(0, ticks - 1);
            return false;
        });
    }

    public static void proc() {
        ticks = MAX_DURATION;
    }

    public static boolean display() {
        return  ticks > 0 && Dungeons.InvincibilityDuration;
    }

    public static void render(HUDComponent component, DrawContext context) {
        RenderUtils.drawTimer(component, context, ticks, 0xffffffff);
    }
}
