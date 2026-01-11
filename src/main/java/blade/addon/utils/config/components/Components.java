package blade.addon.utils.config.components;

import blade.addon.features.dungeon.ChestCounter;
import blade.addon.features.dungeon.RunStartValidator;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.SecretSpawnTimer;
import blade.addon.features.dungeon.WarpCooldown;
import blade.addon.features.dungeon.f7.CrystalSpawn;
import blade.addon.features.dungeon.f7.DistanceToLedge;
import blade.addon.features.dungeon.f7.DragSpawnTimer;
import blade.addon.features.dungeon.f7.PillarExplode;
import blade.addon.features.dungeon.f7.RelicTimer;
import blade.addon.features.dungeon.f7.StormTickTimer;
import blade.addon.features.dungeon.f7.invincibility.InvincibilityTimer;
import blade.addon.features.dungeon.f7.location.LocationNotifier;
import blade.addon.features.dungeon.f7.terms.GoldorTickTimer;
import blade.addon.features.dungeon.f7.terms.LeapNotification;
import blade.addon.features.dungeon.f7.terms.MelodyWarning;
import blade.addon.features.dungeon.f7.terms.Pre4Notifier;
import blade.addon.features.dungeon.f7.terms.TermStartTimer;
import blade.addon.features.other.KickedTimer;
import blade.addon.features.other.RagDisplay;
import blade.addon.features.other.SelectedPet;
import blade.addon.features.notifications.Notifications;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Floor7;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;

public class Components {

    private static final int TICK_TIMER_WIDTH = 30;
    private static final int NOTIFICATION_WIDTH = 130;

    public static void init() {
    }

    @ConfigValue
    public static HUDComponent invincibilityTimer = new HUDComponent(0, 0, 110, 28, 1, "Invincibility timer", InvincibilityTimer::display, InvincibilityTimer::render, () -> Dungeons.displayInvincibilityTimer);

    @ConfigValue
    public static HUDComponent chestCounter = new HUDComponent(0, 0, 110, 10, 1, "Chest count", ChestCounter::display, ChestCounter::render, () -> Dungeons.displayChestCount);

    @ConfigValue
    public static HUDComponent keyNotifierDisplay = new HUDComponent(0, 0, NOTIFICATION_WIDTH, 10, 1, "", KeyNotifier::display, KeyNotifier::render, () -> Dungeons.enableKeyNotifier && !Dungeons.combineScreenNotifications);

    @ConfigValue
    public static HUDComponent duplicateClassDisplay = new HUDComponent(0, 0, NOTIFICATION_WIDTH, 10, 1, "", RunStartValidator::display, RunStartValidator::render, () -> (Dungeons.detectDuplicateClass || Dungeons.detectPlayerCount) && !Dungeons.combineScreenNotifications);

    @ConfigValue
    public static HUDComponent secretSpawnTimer = new HUDComponent(0, 0, 20, 10, 1, "Secret spawn timer", SecretSpawnTimer::display, SecretSpawnTimer::render, () -> Dungeons.enableSecretSpawnTimer);

    @ConfigValue
    public static HUDComponent warpCoolDown = new HUDComponent(0, 0, 110, 10, 0.75f, "Warp cooldown", WarpCooldown::display, WarpCooldown::render, () -> Dungeons.enableWarpCooldown);

    @ConfigValue
    public static HUDComponent crystalSpawnTime = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Crystal Spawn Time", CrystalSpawn::display, CrystalSpawn::render, () -> Floor7.enableCrystalSpawnTime && !Floor7.combineTickTimers);

    @ConfigValue
    public static HUDComponent stormTickTimer = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Storm Tick Timer", StormTickTimer::display, StormTickTimer::render, () -> Floor7.enableStormTickTimer && !Floor7.combineTickTimers);

    @ConfigValue
    public static HUDComponent stormDeathTime = new HUDComponent(0, 0, 30, 10, 1, "Storm Death Time", StormTickTimer::displayDeathTime, StormTickTimer::renderDeathTime, () -> Floor7.enableStormDeathTime);

    @ConfigValue
    public static HUDComponent distanceToLedgeComponent = new HUDComponent(0, 0, 30, 10, 1, "Distance to ledge", DistanceToLedge::display, DistanceToLedge::render, () -> Floor7.displayDistanceToLedge);

    @ConfigValue
    public static HUDComponent goldorTickTimer = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Goldor Tick Timer", GoldorTickTimer::display, GoldorTickTimer::render, () -> Floor7.enableGoldorTickTimer && !Floor7.combineTickTimers);

    @ConfigValue
    public static HUDComponent termStartTimer = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Term Start Timer", TermStartTimer::display, TermStartTimer::render, () -> Floor7.enableTermStartTimer && !Floor7.combineTickTimers);

    @ConfigValue
    public static HUDComponent relicSpawnTimer = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Relic Spawn Timer", RelicTimer::display, RelicTimer::render, () -> Floor7.enableRelicStartTimer && !Floor7.combineTickTimers && !Floor7.replaceWithProgressBar);

    @ConfigValue
    public static HUDComponent relicProgressBar = new HUDComponent(0, 0, 110, 10, 1, "Relic progressbar", RelicTimer::displayProgressBar, RelicTimer::renderProgressBar, () -> Floor7.enableRelicStartTimer && Floor7.replaceWithProgressBar);

    @ConfigValue
    public static HUDComponent combinedTickTimer = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Combined Tick timer", CombinedTickTimer::display, CombinedTickTimer::render, () -> Floor7.combineTickTimers);

    @ConfigValue
    public static HUDComponent combinedNotifications = new HUDComponent(0, 0, NOTIFICATION_WIDTH, 10, 1, "Combined Notifications", CombinedScreenNotifications::display, CombinedScreenNotifications::render, () -> Dungeons.combineScreenNotifications);

    @ConfigValue
    public static HUDComponent dragSpawnTimer = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Dragon spawn timer", DragSpawnTimer::display, DragSpawnTimer::render, () -> Floor7.dragSpawnTimers);

    @ConfigValue
    public static HUDComponent petDisplay = new HUDComponent(0, 0, 100, 16, 1, "Selected pet display", SelectedPet::display, SelectedPet::render, () -> ExtraOptions.drawPetHUD);

    @ConfigValue
    public static HUDComponent atNotificationDisplay = new HUDComponent(0, 0, 200, 16, 1, "At location display", LocationNotifier::display, LocationNotifier::render, () -> Floor7.displayLocationNotification);

    @ConfigValue
    public static HUDComponent kickedTimer = new HUDComponent(0, 0, 100, 10, 1, "Kicked timer", KickedTimer::display, KickedTimer::render, () -> ExtraOptions.enableKickedTimer);

    @ConfigValue
    public static HUDComponent pre4Notification = new HUDComponent(0, 0, NOTIFICATION_WIDTH, 10, 1, "pre4 done notification", Pre4Notifier::display, Pre4Notifier::render, () -> Floor7.notifyPre4Completion && !Dungeons.combineScreenNotifications);

    @ConfigValue
    public static HUDComponent pillarExplodeTimer = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Pillar explode timer", PillarExplode::displayTimer, PillarExplode::renderTimer, () -> Floor7.timePillarExplosion);

    @ConfigValue
    public static HUDComponent stormCrushNotification = new HUDComponent(0, 0, NOTIFICATION_WIDTH, 10, 1, "Pillar explode timer", PillarExplode::display, PillarExplode::render, () -> Floor7.notifyStormCrush && !Dungeons.combineScreenNotifications);

    @ConfigValue
    public static HUDComponent melodyNotification = new HUDComponent(0, 0, NOTIFICATION_WIDTH, 10, 1, "melody warning notification", MelodyWarning::display, MelodyWarning::render, () -> Floor7.notifiyMelody && !Dungeons.combineScreenNotifications);

    @ConfigValue
    public static HUDComponent ragDisplay = new HUDComponent(0, 0, TICK_TIMER_WIDTH, 10, 1, "Rag axe display", RagDisplay::display, RagDisplay::render, () -> ExtraOptions.enableRagaxeDisplay);

    @ConfigValue
    public static HUDComponent leapedDisplay = new HUDComponent(0, 0, 110, 10, 1, "Leaped displayed", LeapNotification::display, LeapNotification::render, () -> Floor7.leapNotifications);

    @ConfigValue
    public static HUDComponent chatNotification = new HUDComponent(0, 0, NOTIFICATION_WIDTH, 10, 1, "Chat notification", Notifications::display, Notifications::render, () -> !Dungeons.combineScreenNotifications);
}
