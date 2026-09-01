package blade.addon.features.dungeon;

import blade.addon.features.dungeon.f7.terms.device.DeviceNotifier;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class HidePlayers {

    private static final int HIDE_DURATION = 3 * 1000;

    private static long startTime;
    private static boolean justLeapt = false;


    public static void init() {
        Events.ON_LEAP.register(_ -> {
            justLeapt = true;
            startTime = System.currentTimeMillis();
            return false;
        });
    }

    public static boolean testHideAtLeap() {
        if (!Dungeons.hideAfterLeap || !justLeapt) return false;
        if (!Phase.inBoss() && Dungeons.hideOnlyInBoss) return false;

        if (System.currentTimeMillis() - startTime >= HIDE_DURATION) {
            justLeapt = false;
            return false;
        }

        return true;
    }

    public static boolean testHideAtSS(Player player) {
        if (!Dungeons.hideAtSS || !Phase.inBoss()) return false;
        if (Dungeons.hideBeforeTermsOnly && Phase.inP3()) return false;

        return DeviceNotifier.atSS(player);
    }

    public static boolean testHideInRange(Player player) {
        if (!Dungeons.hidePlayersInRange) return false;
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return false;

        double distance = player.position().distanceTo(clientPlayer.position());
        if (!Double.isFinite(distance) || !Double.isFinite(Dungeons.hidePlayerRange)) return false;
        return distance <= Dungeons.hidePlayerRange;

    }

    public static boolean shouldHidePlayers(Player player) {
        if (!Location.inDungeon() || !EntityUtil.isARealPlayer(player) || EntityUtil.isClientPlayer(player)) return false;

        if (testHideAtLeap()) {
            return true;
        }

        if (testHideAtSS(player)) {
            return true;
        }

        return testHideInRange(player);
    }


}
