package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class PillarExplode {

    private static final int TOTAL_TICKS = 20;
    private static int tick = 0;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Floor7.notifyStormCrush && !Floor7.timePillarExplosion) return false;
            if (!Location.inDungeon() || !Phase.inP2()) return false;

            String string = text.getString();
            if (string == null) return false;

            if (string.equals("[BOSS] Storm: Oof") || string.equals("[BOSS] Storm: Ouch, that hurt!")) {
                tick = TOTAL_TICKS;
                Scheduler.scheduleSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1, 1);
            }

            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            return false;
        });
    }

    public static boolean displayTimer() {
        return Floor7.timePillarExplosion && tick > 0;
    }

    public static void renderTimer(HUDComponent component, DrawContext context) {
        int color = tick < 6 ? Constants.GREEN_COLOR : Constants.RED_COLOR;
        RenderUtils.drawTimer(component, context, tick, color);
    }

    public static boolean display() {
        return Floor7.notifyStormCrush && tick > 0;
    }

    public static void render(HUDComponent component, DrawContext context) {
        RenderUtils.drawCenteredText(context, component, Text.literal("§6||| §bStorm crushed! §6|||"));
    }
}
