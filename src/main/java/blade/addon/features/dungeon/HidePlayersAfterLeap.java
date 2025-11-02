package blade.addon.features.dungeon;

import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.manager.ConfigValue;

public class HidePlayersAfterLeap {

    private static final int HIDE_DURATION = 3 * 1000;

    private static long startTime;
    private static boolean justLeapt = false;

    @ConfigValue
    public static boolean hideAfterLeap = false;

    @ConfigValue
    public static boolean hideOnlyInBoss = true;

    public static void init() {
        Events.ON_LEAP.register(message -> {
            justLeapt = true;
            startTime = System.currentTimeMillis();
        });
    }

    public static boolean shouldHidePlayers() {
        if (!hideAfterLeap || !justLeapt) return false;

        if (hideOnlyInBoss && !Phase.inBoss()) return false;

        if (System.currentTimeMillis() - startTime >= HIDE_DURATION) {
            justLeapt = false;
            return false;
        }

        return true;
    }


}
