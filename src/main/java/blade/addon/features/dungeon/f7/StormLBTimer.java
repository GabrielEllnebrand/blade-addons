package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;

public class StormLBTimer {

    private static final int BASE_LB_TICK = 20 * 34;
    private static final int DISPLAY_DURATION_TICK = 20 * 5;

    private static int tick = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP2() && !Phase.stormDead()) tick++;
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = 0;
            }
            return false;
        });
    }

    private static int getCurrentTickTime() {
        return BASE_LB_TICK + Floor7.lbTickOffset - tick;
    }

    private static boolean validTime() {
        int deltaTick = getCurrentTickTime();
        return deltaTick > 0 && deltaTick <= DISPLAY_DURATION_TICK;

    }

    public static boolean display() {
        return Floor7.showLBTimer && Location.inDungeon() && Phase.inP2() && !Phase.stormDead() && validTime() && DungeonClass.isClass(DungeonClass.ARCHER);
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        int deltaTick = getCurrentTickTime();
        double num = deltaTick * Constants.TICK_DURATION;
        RenderUtils.drawTimer(component, graphics, num, RenderUtils.getStatusColor(60, 30, deltaTick));
    }

}
