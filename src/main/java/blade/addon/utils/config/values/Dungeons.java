package blade.addon.utils.config.values;

import blade.addon.features.dungeon.f7.invincibility.InvincibilityTimer;
import config.practical.manager.ConfigValue;

public class Dungeons {

    @ConfigValue
    public static boolean highlightItems = false;

    @ConfigValue
    public static boolean enableWarpCooldown = false;

    @ConfigValue
    public static boolean displayInvincibilityTimer = false;

    @ConfigValue
    public static boolean showProcTitle = false;

    @ConfigValue
    public static InvincibilityTimer.DisplayWhen displayWhen = InvincibilityTimer.DisplayWhen.ALWAYS;

    @ConfigValue
    public static boolean useSprites = false;

    @ConfigValue
    public static boolean hideAfterLeap = false;

    @ConfigValue
    public static boolean hideOnlyInBoss = true;

    @ConfigValue
    public static boolean hideAtSS = false;

    @ConfigValue
    public static boolean hideBeforeTermsOnly = false;

    @ConfigValue
    public static boolean displayChestCount = false;

    @ConfigValue
    public static boolean onlyAfterRunOver = false;

    @ConfigValue
    public static boolean sendChestWarning = false;

    @ConfigValue
    public static int chestWarningCount = 55;

    @ConfigValue
    public static int chestCountColor = 0xffffffff;

    @ConfigValue
    public static boolean enableLeapMessages = false;

    @ConfigValue
    public static boolean enableAutoRequeue = false;

    @ConfigValue
    public static boolean calculateCriticalHit = false;

    @ConfigValue
    public static boolean onlyInBoss = false;

    @ConfigValue
    public static boolean enableKeyNotifier = false;

    @ConfigValue
    public static boolean enableSecretSpawnTimer = false;

    @ConfigValue
    public static boolean combineScreenNotifications = false;

    @ConfigValue
    public static boolean dontProtectHeldItem = true;

    @ConfigValue
    public static boolean hideBlazeNameTag = false;

    @ConfigValue
    public static boolean hidePlayersInRange = false;

    @ConfigValue
    public static double hidePlayerRange = 2;

    @ConfigValue
    public static boolean disableDropAnimation = false;

    @ConfigValue
    public static boolean maskHighlight = false;

    @ConfigValue
    public static boolean detectDuplicateClass = false;

    @ConfigValue
    public static boolean ignoreDupeMage = false;

    @ConfigValue
    public static boolean detectPlayerCount = false;

    @ConfigValue
    public static boolean useClassColors = false;

    @ConfigValue
    public static int archerColor = 0xffffaa00;

    @ConfigValue
    public static int berserkColor = 0xffaa0000;

    @ConfigValue
    public static int healerColor = 0xffff55ff;

    @ConfigValue
    public static int tankColor = 0xff00aa00;

    @ConfigValue
    public static int mageColor = 0xff55ffff;
}
