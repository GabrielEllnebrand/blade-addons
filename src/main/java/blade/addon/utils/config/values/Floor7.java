package blade.addon.utils.config.values;

import blade.addon.features.dungeon.f7.DragSpawnTimer;
import config.practical.manager.ConfigValue;

public class Floor7 {

    @ConfigValue
    public static boolean enableBossWaypoints = false;

    @ConfigValue
    public static int nextWaypointColor = 0xff00F7F7;

    @ConfigValue
    public static boolean nextWaypointThroughWall = false;

    @ConfigValue
    public static boolean enableCrystalSpawnTime = false;

    @ConfigValue
    public static boolean enableStormTickTimer = false;

    @ConfigValue
    public static int stormTickTimerColor = 0xffffffff;

    @ConfigValue
    public static boolean tickDownStormTickTimer = false;

    @ConfigValue
    public static boolean enableStormDeathTime = false;

    @ConfigValue
    public static boolean notifyUsedSpiritMask = false;

    @ConfigValue
    public static boolean displayDistanceToLedge = false;

    @ConfigValue
    public static boolean enableGoldorTickTimer = false;

    @ConfigValue
    public static boolean inDeathTicks = true;

    @ConfigValue
    public static boolean enableTermStartTimer = false;

    @ConfigValue
    public static boolean enablePositionalMessages = false;

    @ConfigValue
    public static boolean enableRelicStartTimer = false;

    @ConfigValue
    public static int relicSpawnTicks = 42;

    @ConfigValue
    public static boolean enableRelicPlaceTime = false;

    @ConfigValue
    public static boolean renderRelicHighlight = false;

    @ConfigValue
    public static boolean blockIncorrectRelicPlace = false;

    @ConfigValue
    public static boolean combineTickTimers = false;

    @ConfigValue
    public static boolean dragSpawnTimers = false;

    @ConfigValue
    public static boolean sendSoundOnDragSpawn = false;

    @ConfigValue
    public static DragSpawnTimer.Team healerTeam = DragSpawnTimer.Team.ARCHER_TEAM;
}
