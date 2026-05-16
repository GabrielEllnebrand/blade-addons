package blade.addon.features.dungeon.f7.invincibility;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;

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

    public static void render(HUDComponent component, GuiGraphics context) {
        int color = 0xffffffff;
        if (Dungeons.useStatusColorForInvincibility) {
            color = RenderUtils.getStatusColor(40, 20, ticks);
        }
        RenderUtils.drawTimer(component, context, ticks, color);
    }
}
