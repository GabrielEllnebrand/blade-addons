package blade.addon.features.other.loadout;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadoutData {

    private static final Pattern LOADOUT_PATTERN = Pattern.compile("^You equipped (.+)!$");

    private static final long DURATION = 1000;

    private static String current = "Unknown";
    private static long prevTime = 0;

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {

            if (!ExtraOptions.enableLoadoutDisplay) return false;

            String string = text.getString();
            Matcher matcher = LOADOUT_PATTERN.matcher(string);
            if (!matcher.find()) return false;

            current = matcher.group(1);
            prevTime = System.currentTimeMillis();
            Misc.sendSound(ExtraOptions.loadoutSound);

            return false;
        });

    }

    public static String getCurrentLoadout() {
        return current;
    }

    public static boolean shouldDisplayNotification() {
        return System.currentTimeMillis() - prevTime < DURATION;
    }

}
