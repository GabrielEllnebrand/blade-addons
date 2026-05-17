package blade.addon.features.dungeon.f7.maxor;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.TextUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;

public class MaxorStun {

    private static final int TOTAL_TICKS = 240;

    private static int tick = 0;
    private static boolean stunned = false;

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon() || !Phase.inP1())
                return false;
            String string = text.getString();

            if (string.equals("[BOSS] Maxor: THAT BEAM! IT HURTS! IT HURTS!!") || string.equals("[BOSS] Maxor: YOU TRICKED ME!")) {
                stunned = true;
            } else if (string.equals("⚠ Maxor is enraged! ⚠")) {
                tick = TOTAL_TICKS;
                stunned = false;
            }

            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            if (stunned) {
                tick = Math.max(0, tick - 1);
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            stunned = false;
            tick = TOTAL_TICKS;
            return false;
        });
    }

    public static boolean display() {
        return tick > 0 && Location.inDungeon() && Phase.inP1() && Floor7.maxorStunDuration && stunned;
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        RenderUtils.drawPrefixedText(component, graphics, "Stunned", TextUtil.formatTicks(tick));
    }


}
