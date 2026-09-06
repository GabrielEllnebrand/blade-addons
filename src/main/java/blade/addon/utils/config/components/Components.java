package blade.addon.utils.config.components;

import blade.addon.features.dungeon.*;
import blade.addon.features.dungeon.f7.dragons.DragonSpawnTimer;
import blade.addon.features.dungeon.f7.invincibility.InvincibilityDisplay;
import blade.addon.features.dungeon.f7.maxor.crystals.CrystalNotification;
import blade.addon.features.dungeon.f7.relic.RelicProgressBar;
import blade.addon.features.dungeon.f7.StormLBTimer;
import blade.addon.features.dungeon.f7.invincibility.InvincibilityDuration;
import blade.addon.features.dungeon.f7.location.LocationNotifier;
import blade.addon.features.dungeon.f7.maxor.MaxorStun;
import blade.addon.features.dungeon.f7.maxor.MaxorTickTimer;
import blade.addon.features.dungeon.f7.maxor.crystals.CrystalTimer;
import blade.addon.features.dungeon.f7.relic.RelicTimer;
import blade.addon.features.dungeon.f7.storm.*;
import blade.addon.features.dungeon.f7.storm.pillar.PillarExplodeTimer;
import blade.addon.features.dungeon.f7.storm.pillar.PillarNotification;
import blade.addon.features.dungeon.f7.terms.*;
import blade.addon.features.dungeon.f7.terms.device.DeviceNotification;
import blade.addon.features.item.HeldItemToolTip;
import blade.addon.features.notifications.NotificationDisplay;
import blade.addon.features.other.*;
import blade.addon.features.other.arrow.ArrowDisplay;
import blade.addon.features.other.arrow.ArrowNotification;
import blade.addon.features.other.loadout.LoadoutDisplay;
import blade.addon.features.other.loadout.LoadoutNotification;
import blade.addon.features.other.pet.PetDisplay;
import blade.addon.features.other.pet.PetNotification;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;

public class Components {

    public static void init() {}

    @ConfigValue
    public static HUDComponent invincibilityTimer = new InvincibilityDisplay();
    @ConfigValue
    public static HUDComponent chestCounter = new ChestCounter();

    @ConfigValue
    public static HUDComponent secretSpawnTimer = new SecretSpawnTimer();

    @ConfigValue
    public static HUDComponent warpCoolDown = new WarpCooldown();

    @ConfigValue
    public static HUDComponent stormDeathTime = new StormDeathTime();

    @ConfigValue
    public static HUDComponent distanceToLedgeComponent = new DistanceToLedge();

    @ConfigValue
    public static CombineableTickTimer crystalSpawnTime = new CrystalTimer();

    @ConfigValue
    public static CombineableTickTimer stormTickTimer = new StormTickTimer();

    @ConfigValue
    public static CombineableTickTimer termStartTimer = new TermStartTimer();

    @ConfigValue
    public static CombineableTickTimer goldorTickTimer = new GoldorTickTimer();

    @ConfigValue
    public static CombineableTickTimer relicSpawnTimer = new RelicTimer();

    @ConfigValue
    public static HUDComponent combinedTickTimer = new CombinedTickTimer(crystalSpawnTime, stormTickTimer, termStartTimer, goldorTickTimer, relicSpawnTimer);

    @ConfigValue
    public static CombineableNotification duplicateClassDisplay = new RunStartValidator();

    @ConfigValue
    public static CombineableNotification keyNotifierDisplay = new KeyNotification();

    @ConfigValue
    public static CombineableNotification pre4Notification = new DeviceNotification();

    @ConfigValue
    public static CombineableNotification melodyNotification = new MelodyNotification();

    @ConfigValue
    public static CombineableNotification stormCrushNotification = new PillarNotification();

    @ConfigValue
    public static CombineableNotification crystalReminderNotification = new CrystalNotification();

    @ConfigValue
    public static CombineableNotification chatNotification = new NotificationDisplay();

    @ConfigValue
    public static CombineableNotification petTitleNotification = new PetNotification();

    @ConfigValue
    public static CombineableNotification arrowSwapDisplay = new ArrowNotification();

    @ConfigValue
    public static CombineableNotification sectionCompletionDisplay = new SectionCompleteNotification();

    @ConfigValue
    public static CombineableNotification bloodNotificationDisplay = new BloodNotification();

    @ConfigValue
    public static CombineableNotification loadoutNotification = new LoadoutNotification();

    @ConfigValue
    public static HUDComponent combinedNotifications = new CombinedNotification(
            duplicateClassDisplay, keyNotifierDisplay, pre4Notification,
            melodyNotification, stormCrushNotification, crystalReminderNotification,
            chatNotification, petTitleNotification, arrowSwapDisplay,
            sectionCompletionDisplay, bloodNotificationDisplay, loadoutNotification);

    @ConfigValue
    public static HUDComponent relicProgressBar = new RelicProgressBar();

    @ConfigValue
    public static HUDComponent dragSpawnTimer = new DragonSpawnTimer();

    @ConfigValue
    public static HUDComponent petDisplay = new PetDisplay();

    @ConfigValue
    public static LocationNotifier atNotificationDisplay = new LocationNotifier();

    @ConfigValue
    public static HUDComponent kickedTimer = new KickedTimer();

    @ConfigValue
    public static HUDComponent pillarExplodeTimer = new PillarExplodeTimer();

    @ConfigValue
    public static HUDComponent ragDisplay = new RagDisplay();

    @ConfigValue
    public static HUDComponent leapedDisplay = new LeapDisplay();

    @ConfigValue
    public static HeldItemToolTip toolTipDisplay = new HeldItemToolTip();

    @ConfigValue
    public static InvincibilityDuration invincibilityDurationDisplay = new InvincibilityDuration();

    @ConfigValue
    public static HUDComponent sectionProgressDisplay = new SectionProgress();

    @ConfigValue
    public static HUDComponent quizTimerDisplay = new QuizTimer();

    @ConfigValue
    public static HUDComponent maxorStunDisplay = new MaxorStun();

    @ConfigValue
    public static HUDComponent selectedArrowDisplay = new ArrowDisplay();

    @ConfigValue
    public static HUDComponent maxorTickTimer = new MaxorTickTimer();

    @ConfigValue
    public static HUDComponent currentSectionDisplay = new CurrentSection();

    @ConfigValue
    public static HUDComponent reaperDisplay = new ReaperDisplay();

    @ConfigValue
    public static HUDComponent stormLbTimer = new StormLBTimer();

    @ConfigValue
    public static HUDComponent LoadoutDisplay = new LoadoutDisplay();
}
