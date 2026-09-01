package blade.addon.utils.config.values;

import blade.addon.features.dungeon.f7.dragons.DragonSpawn;
import config.practical.data.SoundData;
import config.practical.manager.ConfigValue;
import net.minecraft.sounds.SoundEvents;

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
    public static boolean replaceWithProgressBar = false;

    @ConfigValue
    public static boolean useValleyBar = true;

    @ConfigValue
    public static boolean combineTickTimers = false;

    @ConfigValue
    public static boolean dragSpawnTimers = false;

    @ConfigValue
    public static boolean sendSoundOnDragSpawn = false;

    @ConfigValue
    public static DragonSpawn.Team healerTeam = DragonSpawn.Team.ARCHER_TEAM;

    @ConfigValue
    public static boolean displayLocationNotification = false;

    @ConfigValue
    public static int notificationDuration = 15;

    @ConfigValue
    public static SoundData atLocationSound = new SoundData(SoundEvents.NOTE_BLOCK_PLING.value(), 1, 1);

    @ConfigValue
    public static int notificationRepetitions = 3;

    @ConfigValue
    public static boolean predevForAll = true;

    @ConfigValue
    public static boolean showAllRelicTimes = true;

    @ConfigValue
    public static boolean notifyPre4Completion = false;

    @ConfigValue
    public static boolean notifyStormCrush = false;

    @ConfigValue
    public static boolean timePillarExplosion = false;

    @ConfigValue
    public static boolean notifiyMelody = false;

    @ConfigValue
    public static boolean leapNotifications = false;

    @ConfigValue
    public static boolean disableTitlesAtPre4 = false;

    @ConfigValue
    public static boolean showDistanceAtYellowOnly = false;

    @ConfigValue
    public static boolean dontNotifiyForYourself = true;

    @ConfigValue
    public static boolean hideTerminalTitles = false;

    @ConfigValue
    public static boolean terminalTimeStamps = false;

    @ConfigValue
    public static boolean crystalPlaceReminder = false;

    @ConfigValue
    public static boolean showSectionProgress = false;

    @ConfigValue
    public static boolean notifySSCompletion = false;

    @ConfigValue
    public static boolean disableTitlesAtSS = false;

    @ConfigValue
    public static boolean instantlyDisplayCrystalReminder = false;

    @ConfigValue
    public static boolean dragonHealth = false;

    @ConfigValue
    public static boolean maxorStunDuration = false;

    @ConfigValue
    public static boolean makeGoldorTickUp = true;

    @ConfigValue
    public static boolean capitalizeHealthNumbers = true;

    @ConfigValue
    public static boolean sectionCompletionNotification = false;

    @ConfigValue
    public static SoundData sectionChangeSound = new SoundData(SoundEvents.NOTE_BLOCK_PLING.value(), 0, 1);

    @ConfigValue
    public static boolean enableMaxorTickTimer = false;

    @ConfigValue
    public static boolean showCurrentSection = false;

    @ConfigValue
    public static boolean sectionColorProgress = false;

    @ConfigValue
    public static boolean sectionPrevObjective = false;

    @ConfigValue
    public static boolean dragonTracer = false;

    @ConfigValue
    public static boolean assumeCore = true;

    @ConfigValue
    public static boolean assumeSplitEE2 = false;

    @ConfigValue
    public static boolean showLBTimer = false;

    @ConfigValue
    public static int lbTickOffset = 10;

    @ConfigValue
    public static boolean displayDistanceOnAllClasses = false;
}
