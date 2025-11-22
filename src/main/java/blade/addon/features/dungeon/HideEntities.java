package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.manager.ConfigValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HideEntities {

    private static final int HIDE_DURATION = 3 * 1000;
    private static final double DISTANCE = 3;

    private static final Vec3d SS_POSITION = new Vec3d(108, 119, 94);

    private static long startTime;
    private static boolean justLeapt = false;

    @ConfigValue
    public static boolean hideDeadEntities = false;

    @ConfigValue
    public static boolean hideAfterLeap = false;

    @ConfigValue
    public static boolean hideOnlyInBoss = true;

    @ConfigValue
    public static boolean hideAtSS = false;

    @ConfigValue
    public static boolean hideBeforeTermsOnly = false;

    public static void init() {
        Events.ON_LEAP.register(message -> {
            justLeapt = true;
            startTime = System.currentTimeMillis();
        });
    }

    public static boolean testHideAtLeap() {
        if (!hideAfterLeap || !justLeapt) return false;

        if (System.currentTimeMillis() - startTime >= HIDE_DURATION) {
            justLeapt = false;
            return false;
        }

        return true;
    }

    public static boolean testHideAtSS(PlayerEntity player) {
        if (!hideAtSS || !Phase.inBoss()) return false;
        if (hideBeforeTermsOnly && Phase.inP3()) return false;

        return SS_POSITION.distanceTo(player.getPos()) <= DISTANCE;
    }

    public static boolean shouldHidePlayers(PlayerEntity player) {
        if (!Location.inDungeon()) return false;

        if (testHideAtLeap()) {
            return true;
        }

        return testHideAtSS(player);
    }


}
