package blade.addon.features.other;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;

public class ReaperDisplay {

    private static final int TOTAL_TICKS = 20 * 6;

    private static int tick = 0;

    public static void init() {
        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (!ExtraOptions.enableReaperDisplay) return false;
            if (soundEvent != SoundEvents.ZOMBIE_VILLAGER_CURE) return false;
            if (volume != 0.5 && pitch != 1.0) return false;

            tick = TOTAL_TICKS;
            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            return false;
        });
    }

    public static boolean display() {
        return tick > 0;
    }

    public static void render(HUDComponent component, GuiGraphics context) {
        RenderUtils.drawTimer(component, context, tick, Constants.RED);
    }
}
