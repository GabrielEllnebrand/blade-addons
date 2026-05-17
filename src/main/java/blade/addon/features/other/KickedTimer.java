package blade.addon.features.other;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;

public class KickedTimer {

    private static boolean isKicked = false;
    private static long kickedTime = 0;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(text -> {
            if (!ExtraOptions.enableKickedTimer || isKicked) return false;
            String string = text.getString();
            if (string == null) return false;

            if (string.equals("You were kicked while joining that server!")) {
                kickedTime = System.currentTimeMillis();
                isKicked = true;
            }

            return false;
        });
    }

    public static boolean display() {
        return isKicked;
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        long diff = System.currentTimeMillis() - kickedTime;
        if (diff > 60 * 1000) isKicked = false;
        double drawnTime = Math.min(diff / 1000.0, 60.0);
        RenderUtils.drawPrefixedTimer(component, graphics, "Time kicked", drawnTime);
    }

}
