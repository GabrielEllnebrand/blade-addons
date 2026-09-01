package blade.addon.features.dungeon.f7.storm.pillar;

import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import net.minecraft.sounds.SoundEvents;

public class PillarExplode {

    private static final int TOTAL_TICKS = 20;
    static int tick = 0;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Floor7.notifyStormCrush && !Floor7.timePillarExplosion) return false;
            if (!Location.inDungeon() || !Phase.inP2()) return false;

            String string = text.getString();
            if (string == null) return false;

            if (string.equals("[BOSS] Storm: Oof") || string.equals("[BOSS] Storm: Ouch, that hurt!")) {
                tick = TOTAL_TICKS;
                Scheduler.scheduleSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1, 1);
            }

            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            return false;
        });
    }
}
