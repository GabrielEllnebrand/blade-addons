package blade.addon.features;

import config.practical.manager.ConfigValue;

public class ExtraOptions {

    @ConfigValue
    public static boolean hideFireInf5 = false;

    @ConfigValue
    public static boolean hideStuckArrows = false;

    @ConfigValue
    public static boolean disableAbilityCooldownSound = true;

    public static void init() {

    }

}
