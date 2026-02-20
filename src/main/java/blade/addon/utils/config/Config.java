package blade.addon.utils.config;

import blade.addon.features.dungeon.f7.BossWaypoints;
import blade.addon.features.dungeon.f7.dragons.DragSpawnTimer;
import blade.addon.features.dungeon.f7.invincibility.InvincibilityTimer;
import blade.addon.features.dungeon.f7.location.LocationNotifier;
import blade.addon.features.filter.FilterList;
import blade.addon.features.highlight.MobHighlight;
import blade.addon.features.item.ProtectItem;
import blade.addon.features.other.DianaNotifier;
import blade.addon.features.notifications.NotificationList;
import blade.addon.utils.config.components.Components;
import blade.addon.utils.config.values.Buttons;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import blade.addon.utils.dungeon.Split;
import config.practical.ConfigurableScreen;
import config.practical.category.ConfigCategory;
import config.practical.manager.ConfigManager;
import config.practical.widgets.ConfigBool;
import config.practical.widgets.ConfigButton;
import config.practical.widgets.ConfigSection;
import config.practical.widgets.ConfigString;
import config.practical.widgets.ConfigTextArea;
import config.practical.widgets.color.ConfigColor;
import config.practical.widgets.options.ConfigOptions;
import config.practical.widgets.sliders.ConfigDouble;
import config.practical.widgets.sliders.ConfigInt;
import config.practical.widgets.sound.ConfigSound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class Config {

    private static final Text TITLE = Text.literal("Blade Addons");
    public static final ConfigManager manager = new ConfigManager(FolderUtility.OLD_PATH + FolderUtility.ADDONS_NAME,
            List.of(Phase.class, Section.class, Split.class, MobHighlight.class, ExtraOptions.class, DianaNotifier.class, Components.class, Dungeons.class, Floor7.class, Buttons.class, Visual.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);
        screen.addCategory(general());
        screen.addCategory(dungeons());
        screen.addCategory(floor7());
        screen.addCategory(splits());
        screen.addCategory(highlight());
        screen.addCategory(extra());
        screen.addCategory(visual());
        screen.addCategory(buttons());
        return screen;
    }

    private static ConfigCategory general() {
        ConfigCategory general = new ConfigCategory("General");
        general.add(new ConfigString(Text.literal("Message prefix"), () -> ExtraOptions.textPrefix, str -> ExtraOptions.textPrefix = str));

        ConfigSection notifications = new ConfigSection(Text.literal("Chat Notifications"));
        notifications.add(new ConfigButton(Text.literal("Edit Notifications"), () -> MinecraftClient.getInstance().setScreen(new NotificationList())));
        notifications.add(new ConfigBool(Text.literal("Ignore color codes"), () -> ExtraOptions.ignoreColorCodesNotification, bool -> ExtraOptions.ignoreColorCodesNotification = bool));
        general.add(notifications);


        ConfigSection chatFilter = new ConfigSection(Text.literal("Chat filter"));
        chatFilter.add(new ConfigButton(Text.literal("Edit"), () -> MinecraftClient.getInstance().setScreen(new FilterList())));
        chatFilter.add(new ConfigBool(Text.literal("Ignore color codes"), () -> ExtraOptions.ignoreColorCodesFilter, bool -> ExtraOptions.ignoreColorCodesFilter = bool));
        general.add(chatFilter);
        return general;
    }

    private static ConfigCategory dungeons() {
        ConfigCategory dungeons = new ConfigCategory("Dungeons");
        ConfigSection start = new ConfigSection(Text.literal("Start of run"));
        start.add(new ConfigBool(Text.literal("Warp cooldown"), () -> Dungeons.enableWarpCooldown, bool -> Dungeons.enableWarpCooldown = bool));
        start.add(new ConfigBool(Text.literal("Dupe class warning"), () -> Dungeons.detectDuplicateClass, bool -> Dungeons.detectDuplicateClass = bool));
        start.add(new ConfigBool(Text.literal("Ignore dupe mage"), () -> Dungeons.ignoreDupeMage, bool -> Dungeons.ignoreDupeMage = bool));
        start.add(new ConfigBool(Text.literal("Player count warning"), () -> Dungeons.detectPlayerCount, bool -> Dungeons.detectPlayerCount = bool));
        dungeons.add(start);

        ConfigSection invincibility = new ConfigSection(Text.literal("Invincibility Timer"));
        invincibility.add(new ConfigBool(Text.literal("Enable invincibility display"), () -> Dungeons.displayInvincibilityTimer, bool -> Dungeons.displayInvincibilityTimer = bool));
        invincibility.add(new ConfigOptions<>(Text.literal("Display when"), InvincibilityTimer.DisplayWhen.values(), () -> Dungeons.displayWhen, when -> Dungeons.displayWhen = when));
        invincibility.add(new ConfigBool(Text.literal("Use sprites"), () -> Dungeons.useSprites, bool -> Dungeons.useSprites = bool));
        invincibility.add(new ConfigBool(Text.literal("Show title on proc"), () -> Dungeons.showProcTitle, bool -> Dungeons.showProcTitle = bool));
        invincibility.add(new ConfigBool(Text.literal("Invincibility duration"), () -> Dungeons.InvincibilityDuration, bool -> Dungeons.InvincibilityDuration = bool));
        invincibility.add(new ConfigBool(Text.literal("Duration changing color"), () -> Dungeons.useStatusColorForInvincibility, bool -> Dungeons.useStatusColorForInvincibility = bool));
        invincibility.add(new ConfigBool(Text.literal("Remove \"Mask\" in the text"), () -> Dungeons.removeMaskPart, bool -> Dungeons.removeMaskPart = bool));
        dungeons.add(invincibility);

        ConfigSection classColors = new ConfigSection(Text.literal("Class Colors"));
        classColors.add(new ConfigBool(Text.literal("Use class colors"), () -> Dungeons.useClassColors, bool -> Dungeons.useClassColors = bool));
        classColors.add(new ConfigColor(Text.literal("Archer color"), () -> Dungeons.archerColor, color -> Dungeons.archerColor = color, "archer-color", false));
        classColors.add(new ConfigColor(Text.literal("Berserk color"), () -> Dungeons.berserkColor, color -> Dungeons.berserkColor = color, "berserk-color", false));
        classColors.add(new ConfigColor(Text.literal("Healer color"), () -> Dungeons.healerColor, color -> Dungeons.healerColor = color, "healer-color", false));
        classColors.add(new ConfigColor(Text.literal("Tank color"), () -> Dungeons.tankColor, color -> Dungeons.tankColor = color, "tank-color", false));
        classColors.add(new ConfigColor(Text.literal("Mage color"), () -> Dungeons.mageColor, color -> Dungeons.mageColor = color, "mage-color", false));

        dungeons.add(classColors);

        ConfigSection hidePlayers = new ConfigSection(Text.literal("Hide Players"));
        hidePlayers.add(new ConfigBool(Text.literal("Hide after leap"), () -> Dungeons.hideAfterLeap, bool -> Dungeons.hideAfterLeap = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide only in boss"), () -> Dungeons.hideOnlyInBoss, bool -> Dungeons.hideOnlyInBoss = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide at SS"), () -> Dungeons.hideAtSS, bool -> Dungeons.hideAtSS = bool));
        hidePlayers.add(new ConfigBool(Text.literal("SS only hides before terms"), () -> Dungeons.hideBeforeTermsOnly, bool -> Dungeons.hideBeforeTermsOnly = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide players in range"), () -> Dungeons.hidePlayersInRange, bool -> Dungeons.hidePlayersInRange = bool));
        hidePlayers.add(new ConfigDouble(Text.literal("Hiding range"), () -> Dungeons.hidePlayerRange, num -> Dungeons.hidePlayerRange = num, 1, 0, 7));
        hidePlayers.add(new ConfigBool(Text.literal("Hide teammate highlight"), () -> Dungeons.dontHighlightHiddenTeammates, bool -> Dungeons.dontHighlightHiddenTeammates = bool));


        dungeons.add(hidePlayers);

        ConfigSection chests = new ConfigSection(Text.literal("Croesus"));
        chests.add(new ConfigBool(Text.literal("Display current chest count"), () -> Dungeons.displayChestCount, bool -> Dungeons.displayChestCount = bool));
        chests.add(new ConfigBool(Text.literal("Only display after run is over"), () -> Dungeons.onlyAfterRunOver, bool -> Dungeons.onlyAfterRunOver = bool));
        chests.add(new ConfigBool(Text.literal("Send chest count warning"), () -> Dungeons.sendChestWarning, bool -> Dungeons.sendChestWarning = bool));
        chests.add(new ConfigInt(Text.literal("Warning at chest"), () -> Dungeons.chestWarningCount, num -> Dungeons.chestWarningCount = num, 1, 1, 60));

        dungeons.add(chests);
        dungeons.add(new ConfigBool(Text.literal("Leap message"), () -> Dungeons.enableLeapMessages, bool -> Dungeons.enableLeapMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Auto requeue"), () -> Dungeons.enableAutoRequeue, bool -> Dungeons.enableAutoRequeue = bool));

        ConfigSection expo = new ConfigSection(Text.literal("Explosive shot"));
        expo.add(new ConfigBool(Text.literal("calculate explosive shot"), () -> Dungeons.calculateCriticalHit, bool -> Dungeons.calculateCriticalHit = bool));
        expo.add(new ConfigBool(Text.literal("Only in boss"), () -> Dungeons.onlyInBoss, bool -> Dungeons.onlyInBoss = bool));
        dungeons.add(expo);

        dungeons.add(new ConfigBool(Text.literal("Notification on key spawn"), () -> Dungeons.enableKeyNotifier, bool -> Dungeons.enableKeyNotifier = bool));
        dungeons.add(new ConfigBool(Text.literal("Display key noti for all classes"), () -> Dungeons.displayKeyForAllClasses, bool -> Dungeons.displayKeyForAllClasses = bool));
        dungeons.add(new ConfigBool(Text.literal("Secret spawn timer"), () -> Dungeons.enableSecretSpawnTimer, bool -> Dungeons.enableSecretSpawnTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Item highlight"), () -> Dungeons.highlightItems, bool -> Dungeons.highlightItems = bool));
        dungeons.add(new ConfigBool(Text.literal("Combine screen notifications"), () -> Dungeons.combineScreenNotifications, bool -> Dungeons.combineScreenNotifications = bool));
        dungeons.add(new ConfigBool(Text.literal("Don't protect held item in run"), () -> Dungeons.dontProtectHeldItem, bool -> Dungeons.dontProtectHeldItem = bool));
        dungeons.add(new ConfigBool(Text.literal("Hide blaze nametags"), () -> Dungeons.hideBlazeNameTag, bool -> Dungeons.hideBlazeNameTag = bool));
        dungeons.add(new ConfigBool(Text.literal("Disable drop animation"), () -> Dungeons.disableDropAnimation, bool -> Dungeons.disableDropAnimation = bool));
        dungeons.add(new ConfigBool(Text.literal("Mask cooldown highlight"), () -> Dungeons.maskHighlight, bool -> Dungeons.maskHighlight = bool));
        dungeons.add(new ConfigBool(Text.literal("Highlight teammates"), () -> Dungeons.highlightTeammates, bool -> Dungeons.highlightTeammates = bool));
        dungeons.add(new ConfigBool(Text.literal("Render class names"), () -> Dungeons.renderClassName, bool -> Dungeons.renderClassName = bool));
        dungeons.add(new ConfigBool(Text.literal("Time until quiz question"), () -> Dungeons.quizTimer, bool -> Dungeons.quizTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("append quiz progress"), () -> Dungeons.quizProgress, bool -> Dungeons.quizProgress = bool));
        dungeons.add(new ConfigBool(Text.literal("Draw boss health numbers"), () -> Dungeons.bossHealthNumbers, bool -> Dungeons.bossHealthNumbers = bool));
        dungeons.add(new ConfigBool(Text.literal("Blood notifier on first 4 spawned"), () -> Dungeons.alertBloodSpawns, bool -> Dungeons.alertBloodSpawns = bool));
        dungeons.add(new ConfigBool(Text.literal("Blood notifier on all classes"), () -> Dungeons.alertBloodForAllClasses, bool -> Dungeons.alertBloodForAllClasses = bool));
        return dungeons;
    }

    private static ConfigCategory floor7() {
        ConfigCategory floor7 = new ConfigCategory("Floor 7");
        floor7.add(new ConfigBool(Text.literal("Combine tick timers"), () -> Floor7.combineTickTimers, bool -> Floor7.combineTickTimers = bool));
        floor7.add(new ConfigBool(Text.literal("Enable player leap count"), () -> Floor7.leapNotifications, bool -> Floor7.leapNotifications = bool));
        floor7.add(new ConfigBool(Text.literal("Capitalize health numbers"), () -> Floor7.capitalizeHealthNumbers, bool -> Floor7.capitalizeHealthNumbers = bool));

        ConfigSection waypoints = new ConfigSection(Text.literal("Waypoints"));
        waypoints.add(new ConfigBool(Text.literal("Enable boss waypoints"), () -> Floor7.enableBossWaypoints, bool -> Floor7.enableBossWaypoints = bool));
        waypoints.add(new ConfigBool(Text.literal("Enable placing waypoints"), BossWaypoints::getPlace, BossWaypoints::setPlace));
        waypoints.add(new ConfigBool(Text.literal("temporarily ignore boss"), BossWaypoints::getIgnoreBoss, BossWaypoints::setIgnoreBoss));
        waypoints.add(new ConfigColor(Text.literal("Next waypoint color"), () -> Floor7.nextWaypointColor, color -> Floor7.nextWaypointColor = color, "next-waypoint-color", true));
        waypoints.add(new ConfigBool(Text.literal("Next waypoint no depth check"), () -> Floor7.nextWaypointThroughWall, bool -> Floor7.nextWaypointThroughWall = bool));
        floor7.add(waypoints);

        ConfigSection maxor = new ConfigSection(Text.literal("Maxor"));
        maxor.add(new ConfigBool(Text.literal("Maxor tick timer"), () -> Floor7.enableMaxorTickTimer, bool -> Floor7.enableMaxorTickTimer = bool));
        maxor.add(new ConfigBool(Text.literal("Crystal Spawn Time"), () -> Floor7.enableCrystalSpawnTime, bool -> Floor7.enableCrystalSpawnTime = bool));
        maxor.add(new ConfigBool(Text.literal("Crystal place reminder"), () -> Floor7.crystalPlaceReminder, bool -> Floor7.crystalPlaceReminder = bool));
        maxor.add(new ConfigBool(Text.literal("Display reminder instantly"), () -> Floor7.instantlyDisplayCrystalReminder, bool -> Floor7.instantlyDisplayCrystalReminder = bool));
        maxor.add(new ConfigBool(Text.literal("Maxor stun timer"), () -> Floor7.maxorStunDuration, bool -> Floor7.maxorStunDuration = bool));
        floor7.add(maxor);

        ConfigSection storm = new ConfigSection(Text.literal("Storm"));
        storm.add(new ConfigBool(Text.literal("Storm tick timer"), () -> Floor7.enableStormTickTimer, bool -> Floor7.enableStormTickTimer = bool));
        storm.add(new ConfigColor(Text.literal("Timer color"), () -> Floor7.stormTickTimerColor, color -> Floor7.stormTickTimerColor = color, "storm-tick-timer-color", false));
        storm.add(new ConfigBool(Text.literal("Tick down from 5"), () -> Floor7.tickDownStormTickTimer, bool -> Floor7.tickDownStormTickTimer = bool));
        storm.add(new ConfigBool(Text.literal("First Death time"), () -> Floor7.enableStormDeathTime, bool -> Floor7.enableStormDeathTime = bool));
        storm.add(new ConfigBool(Text.literal("Distance to ledge"), () -> Floor7.displayDistanceToLedge, bool -> Floor7.displayDistanceToLedge = bool));
        storm.add(new ConfigBool(Text.literal("Only display distance at yellow"), () -> Floor7.showDistanceAtYellowOnly, bool -> Floor7.showDistanceAtYellowOnly = bool));
        storm.add(new ConfigBool(Text.literal("Warn if spirit mask is used"), () -> Floor7.notifyUsedSpiritMask, bool -> Floor7.notifyUsedSpiritMask = bool));
        storm.add(new ConfigBool(Text.literal("Pillar explode timer"), () -> Floor7.timePillarExplosion, bool -> Floor7.timePillarExplosion = bool));
        storm.add(new ConfigBool(Text.literal("Storm crushed notification"), () -> Floor7.notifyStormCrush, bool -> Floor7.notifyStormCrush = bool));
        floor7.add(storm);

        ConfigSection goldor = new ConfigSection(Text.literal("Goldor"));
        goldor.add(new ConfigBool(Text.literal("Goldor tick timer"), () -> Floor7.enableGoldorTickTimer, bool -> Floor7.enableGoldorTickTimer = bool));
        goldor.add(new ConfigBool(Text.literal("Term start time"), () -> Floor7.enableTermStartTimer, bool -> Floor7.enableTermStartTimer = bool));
        goldor.add(new ConfigBool(Text.literal("in 3s increments"), () -> Floor7.inDeathTicks, bool -> Floor7.inDeathTicks = bool));
        goldor.add(new ConfigBool(Text.literal("Tick up (instead of down from 3s)"), () -> Floor7.makeGoldorTickUp, bool -> Floor7.makeGoldorTickUp = bool));
        goldor.add(new ConfigBool(Text.literal("Positional messages"), () -> Floor7.enablePositionalMessages, bool -> Floor7.enablePositionalMessages = bool));
        goldor.add(new ConfigBool(Text.literal("Terminal splits"), () -> Section.enableTerminalSplits, bool -> Section.enableTerminalSplits = bool));
        goldor.add(new ConfigOptions<>(Text.literal("Display when"), Section.DisplayTerminalSplitsWhen.values(), () -> Section.displayTerminalSplitsWhen, when -> Section.displayTerminalSplitsWhen = when));
        goldor.add(new ConfigBool(Text.literal("Pre4 completion notification"), () -> Floor7.notifyPre4Completion, bool -> Floor7.notifyPre4Completion = bool));
        goldor.add(new ConfigBool(Text.literal("SS completion notification"), () -> Floor7.notifySSCompletion, bool -> Floor7.notifySSCompletion = bool));
        goldor.add(new ConfigBool(Text.literal("Melody warning notification"), () -> Floor7.notifiyMelody, bool -> Floor7.notifiyMelody = bool));
        goldor.add(new ConfigBool(Text.literal("Send terminal time stamps"), () -> Floor7.terminalTimeStamps, bool -> Floor7.terminalTimeStamps = bool));
        goldor.add(new ConfigBool(Text.literal("Section completion notification"), () -> Floor7.sectionCompletionNotification, bool -> Floor7.sectionCompletionNotification = bool));
        goldor.add(new ConfigSound(Text.literal("Section complete sound"), Floor7.sectionChangeSound, 2, 2, true));
        goldor.add(new ConfigBool(Text.literal("Current section display"), () -> Floor7.showCurrentSection, bool -> Floor7.showCurrentSection = bool));
        floor7.add(goldor);

        ConfigSection sectionProgress = new ConfigSection(Text.literal("Section progress"));
        sectionProgress.add(new ConfigBool(Text.literal("Display section progress"), () -> Floor7.showSectionProgress, bool -> Floor7.showSectionProgress = bool));
        sectionProgress.add(new ConfigBool(Text.literal("Change color with progress"), () -> Floor7.sectionColorProgress, bool -> Floor7.sectionColorProgress = bool));
        sectionProgress.add(new ConfigBool(Text.literal("Include prev objective"), () -> Floor7.sectionPrevObjective, bool -> Floor7.sectionPrevObjective = bool));
        floor7.add(sectionProgress);


        ConfigSection titles = new ConfigSection(Text.literal("Terminal titles"));
        titles.add(new ConfigBool(Text.literal("Disable titles on pre4"), () -> Floor7.disableTitlesAtPre4, bool -> Floor7.disableTitlesAtPre4 = bool));
        titles.add(new ConfigBool(Text.literal("Disable titles on ss"), () -> Floor7.disableTitlesAtSS, bool -> Floor7.disableTitlesAtSS = bool));
        titles.add(new ConfigBool(Text.literal("Hide most titles in terminals"), () -> Floor7.hideTerminalTitles, bool -> Floor7.hideTerminalTitles = bool));
        floor7.add(titles);

        ConfigSection locationNotifier = new ConfigSection(Text.literal("At location notifier"));
        locationNotifier.add(new ConfigBool(Text.literal("Display Location messages on screen"), () -> Floor7.displayLocationNotification, bool -> Floor7.displayLocationNotification = bool));
        locationNotifier.add(new ConfigBool(Text.literal("Hide your own notifications"), () -> Floor7.dontNotifiyForYourself, bool -> Floor7.dontNotifiyForYourself = bool));
        locationNotifier.add(new ConfigInt(Text.literal("Display duration (in client ticks)"), () -> Floor7.notificationDuration, num -> Floor7.notificationDuration = num, 1, 1, 20));
        locationNotifier.add(new ConfigSound(Text.literal("Notification sound"), Floor7.atLocationSound, 2, 2, false));
        locationNotifier.add(new ConfigInt(Text.literal("Sound repetitions"), () -> Floor7.notificationRepetitions, num -> Floor7.notificationRepetitions = num, 1, 0, 20));
        locationNotifier.add(new ConfigButton(Text.literal("Test notification"), () -> LocationNotifier.startNotification("Someone", " At <location>!!")));

        floor7.add(locationNotifier);

        ConfigSection relic = new ConfigSection(Text.literal("Relics"));
        relic.add(new ConfigBool(Text.literal("Relic start timer"), () -> Floor7.enableRelicStartTimer, bool -> Floor7.enableRelicStartTimer = bool));
        relic.add(new ConfigBool(Text.literal("Replace with progress bar"), () -> Floor7.replaceWithProgressBar, bool -> Floor7.replaceWithProgressBar = bool));
        relic.add(new ConfigBool(Text.literal("Use valleys progress bar"), () -> Floor7.useValleyBar, bool -> Floor7.useValleyBar = bool));
        relic.add(new ConfigInt(Text.literal("Relic start timer ticks"), () -> Floor7.relicSpawnTicks, num -> Floor7.relicSpawnTicks = num, 1, 30, 50));
        relic.add(new ConfigBool(Text.literal("Enable relic placed time"), () -> Floor7.enableRelicPlaceTime, bool -> Floor7.enableRelicPlaceTime = bool));
        relic.add(new ConfigBool(Text.literal("Block incorrect relic place (right click only)"), () -> Floor7.blockIncorrectRelicPlace, bool -> Floor7.blockIncorrectRelicPlace = bool));
        relic.add(new ConfigBool(Text.literal("Highlight picked up relic"), () -> Floor7.renderRelicHighlight, bool -> Floor7.renderRelicHighlight = bool));
        relic.add(new ConfigBool(Text.literal("Send all relic times"), () -> Floor7.showAllRelicTimes, bool -> Floor7.showAllRelicTimes = bool));
        floor7.add(relic);

        ConfigSection dragon = new ConfigSection(Text.literal("Dragons"));
        dragon.add(new ConfigBool(Text.literal("Enable Dragon spawn timers"), () -> Floor7.dragSpawnTimers, bool -> Floor7.dragSpawnTimers = bool));
        dragon.add(new ConfigBool(Text.literal("Send sound on dragon spawn"), () -> Floor7.sendSoundOnDragSpawn, bool -> Floor7.sendSoundOnDragSpawn = bool));
        dragon.add(new ConfigOptions<>(Text.literal("Healer prio"), DragSpawnTimer.Team.values(), () -> Floor7.healerTeam, team -> Floor7.healerTeam = team));
        dragon.add(new ConfigBool(Text.literal("Render dragon health"), () -> Floor7.dragonHealth, bool -> Floor7.dragonHealth = bool));
        floor7.add(dragon);

        return floor7;
    }

    private static ConfigCategory splits() {
        ConfigCategory splits = new ConfigCategory("Splits");
        splits.add(new ConfigBool(Text.literal("Enable Splits"), () -> Phase.enableSplits, bool -> Phase.enableSplits = bool));
        splits.add(new ConfigBool(Text.literal("Include total time"), () -> Phase.includeTotalTime, bool -> Phase.includeTotalTime = bool));
        splits.add(new ConfigBool(Text.literal("Send split in chat when over"), () -> Phase.sendSplitInChat, bool -> Phase.sendSplitInChat = bool));
        splits.add(new ConfigOptions<>(Text.literal("Tick timer type"), Split.TimerType.values(), () -> Split.timerType, type -> Split.timerType = type));
        splits.add(new ConfigBool(Text.literal("Only show activated splits"), () -> Phase.onlyShowActivatedSplits, bool -> Phase.onlyShowActivatedSplits = bool));


        splits.add(new ConfigColor(Text.literal("Real time color (Inactive)"), () -> Split.realTimeColorInactive, color -> Split.realTimeColorInactive = color, "real-time-inactive", false));
        splits.add(new ConfigColor(Text.literal("Real time color (Ongoing)"), () -> Split.realTimeColorOngoing, color -> Split.realTimeColorOngoing = color, "real-time-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Real time color (Complete)"), () -> Split.realTimeColorComplete, color -> Split.realTimeColorComplete = color, "real-time-complete", false));

        splits.add(new ConfigColor(Text.literal("Server time color (Inactive)"), () -> Split.serverTimeColorInactive, color -> Split.serverTimeColorInactive = color, "server-time-inactive", false));
        splits.add(new ConfigColor(Text.literal("Server time color (Ongoing)"), () -> Split.serverTimeColorOngoing, color -> Split.serverTimeColorOngoing = color, "server-time-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Server time color (Complete)"), () -> Split.serverTimeColorComplete, color -> Split.serverTimeColorComplete = color, "server-time-complete", false));

        splits.add(new ConfigColor(Text.literal("Parentheses color (Inactive)"), () -> Split.parenthesesColorInactive, color -> Split.parenthesesColorInactive = color, "parentheses-inactive", false));
        splits.add(new ConfigColor(Text.literal("Parentheses color (Ongoing)"), () -> Split.parenthesesColorOngoing, color -> Split.parenthesesColorOngoing = color, "parentheses-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Parentheses color (Complete)"), () -> Split.parenthesesColorComplete, color -> Split.parenthesesColorComplete = color, "parentheses-complete", false));
        return splits;
    }

    private static ConfigCategory highlight() {
        ConfigCategory highlight = new ConfigCategory("Mob Highlight");
        highlight.add(new ConfigBool(Text.literal("Enable mob highlight"), () -> MobHighlight.mobHighlight, bool -> MobHighlight.mobHighlight = bool));
        highlight.add(new ConfigBool(Text.literal("Don't highlight invisible mobs"), () -> MobHighlight.dontShowInvisibleMobs, bool -> MobHighlight.dontShowInvisibleMobs = bool));
        highlight.add(new ConfigDouble(Text.literal("Extra Wither Width"), () -> MobHighlight.witherExtraWidth, num -> MobHighlight.witherExtraWidth = num, 0.1, 0, 1.5));
        highlight.add(new ConfigInt(Text.literal("Outline Width"), () -> MobHighlight.outlineWidth, num -> MobHighlight.outlineWidth = num, 1, 1, 10));
        highlight.add(new ConfigOptions<>(Text.literal("Highlight mode"), MobHighlight.HighlightType.values(), () -> MobHighlight.currentHighlight, type -> MobHighlight.currentHighlight = type));
        highlight.add(new ConfigBool(Text.literal("Hide none starred nametags"), () -> MobHighlight.hideNoneStaredNameTags, bool -> MobHighlight.hideNoneStaredNameTags = bool));

        highlight.add(new ConfigColor(Text.literal("Star mob filled color"), () -> MobHighlight.starFilledColor, color -> MobHighlight.starFilledColor = color, "star-filled", true));
        highlight.add(new ConfigColor(Text.literal("Star mob outline color"), () -> MobHighlight.starOutlineColor, color -> MobHighlight.starOutlineColor = color, "star-outline", true));
        highlight.add(new ConfigColor(Text.literal("Tank mob filled color"), () -> MobHighlight.tankFilledColor, color -> MobHighlight.tankFilledColor = color, "tank-filled", true));
        highlight.add(new ConfigColor(Text.literal("Tank mob outline color"), () -> MobHighlight.tankOutlineColor, color -> MobHighlight.tankOutlineColor = color, "tank-outline", true));
        highlight.add(new ConfigColor(Text.literal("Mini boss filled color"), () -> MobHighlight.miniFilledColor, color -> MobHighlight.miniFilledColor = color, "mini-filled", true));
        highlight.add(new ConfigColor(Text.literal("Mini boss outline color"), () -> MobHighlight.miniOutlineColor, color -> MobHighlight.miniOutlineColor = color, "mini-outline", true));
        highlight.add(new ConfigColor(Text.literal("Fel filled color"), () -> MobHighlight.felFilledColor, color -> MobHighlight.felFilledColor = color, "fel-filled", true));
        highlight.add(new ConfigColor(Text.literal("Fel outline color"), () -> MobHighlight.felOutlineColor, color -> MobHighlight.felOutlineColor = color, "fel-outline", true));
        highlight.add(new ConfigColor(Text.literal("Shadow Assassin filled color"), () -> MobHighlight.assassinFilledColor, color -> MobHighlight.assassinFilledColor = color, "assassin-filled", true));
        highlight.add(new ConfigColor(Text.literal("Shadow Assassin outline color"), () -> MobHighlight.assassinOutlineColor, color -> MobHighlight.assassinOutlineColor = color, "assassin-outline", true));
        highlight.add(new ConfigColor(Text.literal("Bat filled color"), () -> MobHighlight.batFilledColor, color -> MobHighlight.batFilledColor = color, "bat-filled", true));
        highlight.add(new ConfigColor(Text.literal("Bat outline color"), () -> MobHighlight.batOutlineColor, color -> MobHighlight.batOutlineColor = color, "bat-outline", true));
        highlight.add(new ConfigColor(Text.literal("Wither filled color"), () -> MobHighlight.witherFilledColor, color -> MobHighlight.witherFilledColor = color, "wither-filled", true));
        highlight.add(new ConfigColor(Text.literal("Wither outline color"), () -> MobHighlight.witherOutlineColor, color -> MobHighlight.witherOutlineColor = color, "wither-outline", true));

        ConfigSection mimic = new ConfigSection(Text.literal("Mimic"));
        mimic.add(new ConfigBool(Text.literal("Highlight mimic chests"), () -> MobHighlight.highlightMimicChests, bool -> MobHighlight.highlightMimicChests = bool));
        mimic.add(new ConfigColor(Text.literal("Mimic filled color"), () -> MobHighlight.mimicFilledColor, color -> MobHighlight.mimicFilledColor = color, "mimic-filled", true));
        mimic.add(new ConfigColor(Text.literal("Mimic outline color"), () -> MobHighlight.mimicOutlineColor, color -> MobHighlight.mimicOutlineColor = color, "mimic-outline", true));
        highlight.add(mimic);

        ConfigSection sheep = new ConfigSection(Text.literal("Sheep"));
        sheep.add(new ConfigBool(Text.literal("Highlight sheep in dungeon"), () -> MobHighlight.highlightSheep, bool -> MobHighlight.highlightSheep = bool));
        sheep.add(new ConfigBool(Text.literal("Hide sheep in dungeons"), () -> MobHighlight.hideSheep, bool -> MobHighlight.hideSheep = bool));
        sheep.add(new ConfigColor(Text.literal("Sheep filled color"), () -> MobHighlight.sheepFilledColor, color -> MobHighlight.sheepFilledColor = color, "sheep-filled", true));
        sheep.add(new ConfigColor(Text.literal("Sheep outline color"), () -> MobHighlight.sheepOutlineColor, color -> MobHighlight.sheepOutlineColor = color, "sheep-outline", true));
        highlight.add(sheep);
        return highlight;
    }

    private static ConfigCategory extra() {

        ConfigCategory extra = new ConfigCategory("Extra options");
        extra.add(new ConfigBool(Text.literal("Show pbs in chat"), () -> ExtraOptions.showPbs, bool -> ExtraOptions.showPbs = bool));
        extra.add(new ConfigBool(Text.literal("Disable scroll wheel in hotbar"), () -> ExtraOptions.disableScrollHotbar, bool -> ExtraOptions.disableScrollHotbar = bool));
        extra.add(new ConfigBool(Text.literal("Display kicked time"), () -> ExtraOptions.enableKickedTimer, bool -> ExtraOptions.enableKickedTimer = bool));
        extra.add(new ConfigBool(Text.literal("Disable recipe book"), () -> ExtraOptions.disableRecipeBook, bool -> ExtraOptions.disableRecipeBook = bool));
        extra.add(new ConfigBool(Text.literal("Enable moveable held item tooltip"), () -> ExtraOptions.moveToolTip, bool -> ExtraOptions.moveToolTip = bool));
        extra.add(new ConfigColor(Text.literal("Prefix text color"), () -> ExtraOptions.timerPrefixColor, color -> ExtraOptions.timerPrefixColor = color, "timer-prefix", false));
        extra.add(new ConfigBool(Text.literal("Display selected arrow"), () -> ExtraOptions.displayCurrentArrow, bool -> ExtraOptions.displayCurrentArrow = bool));
        extra.add(new ConfigBool(Text.literal("Title on arrow swap"), () -> ExtraOptions.arrowSwapNotification, bool -> ExtraOptions.arrowSwapNotification = bool));
        extra.add(new ConfigBool(Text.literal("Toggleable searchbar (ctrl + f)"), () -> ExtraOptions.toggleableSearchBar, bool -> ExtraOptions.toggleableSearchBar = bool));
        extra.add(new ConfigBool(Text.literal("Disable protect item (resets on launch)"), () -> ProtectItem.stopProtectItem, bool -> ProtectItem.stopProtectItem = bool));
        //extra.add(new ConfigBool(Text.literal("Kuudra stun waypoint"), () -> ExtraOptions.stunWaypoint, bool -> ExtraOptions.stunWaypoint = bool));


        ConfigSection sound = new ConfigSection(Text.literal("Sound options"));
        sound.add(new ConfigBool(Text.literal("Disable \"on cooldown\" sound"), () -> ExtraOptions.disableAbilityCooldownSound, bool -> ExtraOptions.disableAbilityCooldownSound = bool));
        sound.add(new ConfigBool(Text.literal("Disable bonzo sound"), () -> ExtraOptions.disableBonzoSound, bool -> ExtraOptions.disableBonzoSound = bool));
        sound.add(new ConfigBool(Text.literal("Old bonzo sound"), () -> ExtraOptions.oldBonzoSound, bool -> ExtraOptions.oldBonzoSound = bool));
        sound.add(new ConfigBool(Text.literal("Old tuba sound"), () -> ExtraOptions.oldTubaSound, bool -> ExtraOptions.oldTubaSound = bool));
        extra.add(sound);

        ConfigSection rag = new ConfigSection(Text.literal("Ragnarock"));
        rag.add(new ConfigBool(Text.literal("Display rag axe duration"), () -> ExtraOptions.enableRagaxeDisplay, bool -> ExtraOptions.enableRagaxeDisplay = bool));
        rag.add(new ConfigBool(Text.literal("Use 1.8.9 rag sound"), () -> ExtraOptions.useOldRagSound, bool -> ExtraOptions.useOldRagSound = bool));
        rag.add(new ConfigBool(Text.literal("Use custom rag sound"), () -> ExtraOptions.useCustomRagSound, bool -> ExtraOptions.useCustomRagSound = bool));
        rag.add(new ConfigSound(Text.literal("Custom Rag sound"), ExtraOptions.ragSound));
        extra.add(rag);

        ConfigSection copyChat = new ConfigSection(Text.literal("Copy chat"));
        copyChat.add(new ConfigBool(Text.literal("Copy with right click"), () -> ExtraOptions.copyChat, bool -> ExtraOptions.copyChat = bool));
        copyChat.add(new ConfigBool(Text.literal("remove color codes"), () -> ExtraOptions.removeColorCodes, bool -> ExtraOptions.removeColorCodes = bool));
        copyChat.add(new ConfigBool(Text.literal("replace format char with &"), () -> ExtraOptions.replaceColorChars, bool -> ExtraOptions.replaceColorChars = bool));
        copyChat.add(new ConfigBool(Text.literal("copy line only"), () -> ExtraOptions.copyLineOnly, bool -> ExtraOptions.copyLineOnly = bool));
        copyChat.add(new ConfigBool(Text.literal("Send feedback msg in chat on copied msg"), () -> ExtraOptions.copyChatFeedback, bool -> ExtraOptions.copyChatFeedback = bool));
        extra.add(copyChat);

        ConfigSection pets = new ConfigSection(Text.literal("Pets"));
        pets.add(new ConfigBool(Text.literal("Highlight selected pet in menu"), () -> ExtraOptions.highlightSelectedPet, bool -> ExtraOptions.highlightSelectedPet = bool));
        pets.add(new ConfigColor(Text.literal("Pet highlight color"), () -> ExtraOptions.petHighlightColor, color -> ExtraOptions.petHighlightColor = color, "pet-highlight-color", false));
        pets.add(new ConfigBool(Text.literal("Draw selected pet"), () -> ExtraOptions.drawPetHUD, bool -> ExtraOptions.drawPetHUD = bool));
        pets.add(new ConfigBool(Text.literal("Draw the pets sprite"), () -> ExtraOptions.includePetSprite, bool -> ExtraOptions.includePetSprite = bool));
        pets.add(new ConfigBool(Text.literal("Draw pet level"), () -> ExtraOptions.displayPetLevel, bool -> ExtraOptions.displayPetLevel = bool));
        pets.add(new ConfigBool(Text.literal("Send sound on petswap"), () -> ExtraOptions.sendOnPetSound, bool -> ExtraOptions.sendOnPetSound = bool));
        pets.add(new ConfigSound(Text.literal("Pet swap sound"), ExtraOptions.petSound));
        pets.add(new ConfigBool(Text.literal("Pet swap notification"), () -> ExtraOptions.sendPetSwapNotification, bool -> ExtraOptions.sendPetSwapNotification = bool));
        extra.add(pets);

        ConfigSection diana = new ConfigSection(Text.literal("Diana notifications"));
        diana.add(new ConfigBool(Text.literal("Send Sound"), () -> DianaNotifier.sendSound, bool -> DianaNotifier.sendSound = bool));
        diana.add(new ConfigBool(Text.literal("Send Waypoint"), () -> DianaNotifier.sendWaypoint, bool -> DianaNotifier.sendWaypoint = bool));
        diana.add(new ConfigBool(Text.literal("Check Harpy"), () -> DianaNotifier.checkHarpy, bool -> DianaNotifier.checkHarpy = bool));
        diana.add(new ConfigBool(Text.literal("Check Bull"), () -> DianaNotifier.checkBull, bool -> DianaNotifier.checkBull = bool));
        diana.add(new ConfigBool(Text.literal("Check Nymph"), () -> DianaNotifier.checkNymph, bool -> DianaNotifier.checkNymph = bool));
        extra.add(diana);

        ConfigSection ss = new ConfigSection(Text.literal("Simon says practise"));

        ss.add(new ConfigTextArea(" Set the start button button with /ba ss <x> <y> <z> \n or use bigss coordinates \n " +
                (ExtraOptions.startButton != null? ExtraOptions.startButton.toShortString(): "cant find coordinates")));
        ss.add(new ConfigBool(Text.literal("Practise outside of is"), () -> ExtraOptions.practiceSSAnywhere, bool -> ExtraOptions.practiceSSAnywhere = bool));
        ss.add(new ConfigBool(Text.literal("Skip automatically"), () -> ExtraOptions.autoSkip, bool -> ExtraOptions.autoSkip = bool));
        ss.add(new ConfigBool(Text.literal("Realistic delay"), () -> ExtraOptions.realisticDelay, bool -> ExtraOptions.realisticDelay = bool));
        ss.add(new ConfigBool(Text.literal("Include lucky button"), () -> ExtraOptions.includeLuckyButton, bool -> ExtraOptions.includeLuckyButton = bool));
        ss.add(new ConfigBool(Text.literal("Block unlucky button click"), () -> ExtraOptions.blockUnluckyButtonClick, bool -> ExtraOptions.blockUnluckyButtonClick = bool));
        ss.add(new ConfigDouble(Text.literal("Lucky button rng (0.1)"), () -> ExtraOptions.luckyButtonRng, num -> ExtraOptions.luckyButtonRng = num, 0.01, 0, 1));
        ss.add(new ConfigColor(Text.literal("Lucky button color"), () -> ExtraOptions.luckyButtonColor, color -> ExtraOptions.luckyButtonColor = color, "lucky-button-color", true));
        ss.add(new ConfigSound(Text.literal("Button sound"), ExtraOptions.ssSound));

        extra.add(ss);
        return extra;
    }

    private static ConfigCategory visual() {
        ConfigCategory visual = new ConfigCategory("Visual changes");
        visual.add(new ConfigBool(Text.literal("Disable fire in f5"), () -> Visual.hideFireInf5, bool -> Visual.hideFireInf5 = bool));
        visual.add(new ConfigBool(Text.literal("Hide all entity on fire"), () -> Visual.hideEntityFire, bool -> Visual.hideEntityFire = bool));
        visual.add(new ConfigBool(Text.literal("Hide arrows stuck to entities"), () -> Visual.hideStuckArrows, bool -> Visual.hideStuckArrows = bool));
        visual.add(new ConfigBool(Text.literal("Hide dead entities"), () -> Visual.hideDeadEntities, bool -> Visual.hideDeadEntities = bool));
        visual.add(new ConfigBool(Text.literal("Item rarity background"), () -> Visual.itemRarityBackground, bool -> Visual.itemRarityBackground = bool));
        visual.add(new ConfigBool(Text.literal("Hide potion effects overlay"), () -> Visual.hideStatusOverLay, bool -> Visual.hideStatusOverLay = bool));
        visual.add(new ConfigBool(Text.literal("Disable glowing"), () -> Visual.disableGlowing, bool -> Visual.disableGlowing = bool));
        visual.add(new ConfigBool(Text.literal("Draw item starCount"), () -> Visual.drawStarCount, bool -> Visual.drawStarCount = bool));
        visual.add(new ConfigBool(Text.literal("Highlight protected items"), () -> Visual.highlightProtectedItem, bool -> Visual.highlightProtectedItem = bool));
        visual.add(new ConfigBool(Text.literal("Compact hoppity messages"), () -> Visual.compactHoppityMsgs, bool -> Visual.compactHoppityMsgs = bool));
        visual.add(new ConfigBool(Text.literal("Hide cooldown"), () -> Visual.hideCooldown, bool -> Visual.hideCooldown = bool));
        visual.add(new ConfigBool(Text.literal("Old player head size"), () -> Visual.oldPlayerHead, bool -> Visual.oldPlayerHead = bool));
        visual.add(new ConfigBool(Text.literal("Fix wither essence"), () -> Visual.fixWitherEssence, bool -> Visual.fixWitherEssence = bool));
        visual.add(new ConfigBool(Text.literal("1.8.9 like fishing bobber"), () -> Visual.oldFishingRod, bool -> Visual.oldFishingRod = bool));
        visual.add(new ConfigBool(Text.literal("Stop shovel swing when flattening dirt"), () -> Visual.stopShovelFlattening, bool -> Visual.stopShovelFlattening = bool));
        visual.add(new ConfigBool(Text.literal("Stop swinging when throwing a pearl"), () -> Visual.stopPearlSwing, bool -> Visual.stopPearlSwing = bool));
        return visual;
    }

    private static ConfigCategory buttons() {
        ConfigCategory inventory = new ConfigCategory("Inventory buttons");
        inventory.add(new ConfigTextArea("To add a command just input it with out the /, like \"ba ep\" "));
        inventory.add(new ConfigString(Text.literal("Button 1"), () -> Buttons.command1, str -> Buttons.command1 = str));
        inventory.add(new ConfigString(Text.literal("Button 2"), () -> Buttons.command2, str -> Buttons.command2 = str));
        inventory.add(new ConfigString(Text.literal("Button 3"), () -> Buttons.command3, str -> Buttons.command3 = str));
        inventory.add(new ConfigString(Text.literal("Button 4"), () -> Buttons.command4, str -> Buttons.command4 = str));
        inventory.add(new ConfigString(Text.literal("Button 5"), () -> Buttons.command5, str -> Buttons.command5 = str));
        inventory.add(new ConfigString(Text.literal("Button 6"), () -> Buttons.command6, str -> Buttons.command6 = str));
        inventory.add(new ConfigString(Text.literal("Button 7"), () -> Buttons.command7, str -> Buttons.command7 = str));
        return inventory;
    }

}
