package blade.addon.features.dungeon.f7.storm;

import blade.addon.features.dungeon.f7.invincibility.InvincibilityTimer;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import net.minecraft.network.chat.Component;

public class StormTime {

    private static final int WARN_TICK = 20 * 20;
    private static int tick = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP2() && !Phase.stormDead()) tick++;

            if (tick == WARN_TICK && Floor7.notifyUsedSpiritMask && DungeonClass.isClass(DungeonClass.MAGE) && InvincibilityTimer.spiritMaskUsed()) {
                Misc.setTitle(Component.literal("Leap to arch"));
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            if (Location.inDungeon()) {
                tick = 0;
            }
            return false;
        });
    }

    public static int getTick() {
        return tick;
    }
}
