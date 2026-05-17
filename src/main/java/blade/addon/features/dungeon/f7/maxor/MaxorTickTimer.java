package blade.addon.features.dungeon.f7.maxor;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;

public class MaxorTickTimer {

    private static int tick = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP1()) tick++;
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            tick = 0;
            return false;
        });
    }

    public static boolean display() {
        return Floor7.enableMaxorTickTimer && Location.inDungeon() && Phase.inP1();
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        RenderUtils.drawTimer(component, graphics, tick, 0xffffffff);
    }

}
