package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class GoldorTickTimer {

    private static int tick = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inTerminals()) tick++;
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = 0;
            }
            return false;
        });
    }

    public static boolean display() {
        return Floor7.enableGoldorTickTimer && Location.inDungeon() && Phase.inTerminals();
    }

    public static void render(HUDComponent component, GuiGraphicsExtractor graphics) {
        double num = tick * Constants.TICK_DURATION;
        double mod = num % 3;
        if (Floor7.inDeathTicks && !Floor7.makeGoldorTickUp) mod = 3.0 - mod;
        if (Floor7.inDeathTicks) num = mod;

        int color = (mod < 1 ? Constants.GREEN : mod < 2 ? Constants.GOLD : Constants.RED);

        RenderUtils.drawTimer(component, graphics, num, color);
    }
}
