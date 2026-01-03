package blade.addon.utils.config;

import blade.addon.features.dungeon.f7.BossWaypoints;
import blade.addon.features.dungeon.f7.DragSpawnTimer;
import blade.addon.features.dungeon.f7.invincibility.InvincibilityTimer;
import blade.addon.features.dungeon.f7.location.LocationNotifier;
import blade.addon.features.highlight.MobHighlight;
import blade.addon.features.other.DianaNotifier;
import blade.addon.utils.config.components.Components;
import blade.addon.utils.config.values.Buttons;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Floor7;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class Config {

    private static final Text TITLE = Text.literal("Blade Addons");
    public static final ConfigManager manager = new ConfigManager(FolderUtility.OLD_PATH + FolderUtility.ADDONS_NAME,
            List.of(Phase.class, Section.class, Split.class, MobHighlight.class, ExtraOptions.class, DianaNotifier.class, Components.class, Dungeons.class, Floor7.class, Buttons.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);

        ConfigCategory dungeons = new ConfigCategory("Dungeons");
        ConfigSection start = new ConfigSection(Text.literal("Start of run"));
        start.add(new ConfigBool(Text.literal("Warp cooldown"), () -> Dungeons.enableWarpCooldown, bool -> Dungeons.enableWarpCooldown = bool));
        start.add(new ConfigBool(Text.literal("Dupe class warning"), () -> Dungeons.detectDuplicateClass, bool -> Dungeons.detectDuplicateClass = bool));
        start.add(new ConfigBool(Text.literal("Ignore dupe mage"), () -> Dungeons.ignoreDupeMage, bool -> Dungeons.ignoreDupeMage = bool));
        start.add(new ConfigBool(Text.literal("Player count warning"), () -> Dungeons.detectPlayerCount, bool -> Dungeons.detectPlayerCount = bool));
        start.add(new ConfigInt(Text.literal("Players needed"), () -> Dungeons.playersNeeded, count -> Dungeons.playersNeeded = count, 1, 1, 5));
        dungeons.add(start);

        ConfigSection invincibility = new ConfigSection(Text.literal("Invincibility Timer"));
        invincibility.add(new ConfigBool(Text.literal("Enable invincibility display"), () -> Dungeons.displayInvincibilityTimer, bool -> Dungeons.displayInvincibilityTimer = bool));
        invincibility.add(new ConfigOptions<>(Text.literal("Display when"), InvincibilityTimer.DisplayWhen.values(), () -> Dungeons.displayWhen, when -> Dungeons.displayWhen = when));
        invincibility.add(new ConfigBool(Text.literal("Use sprites"), () -> Dungeons.useSprites, bool -> Dungeons.useSprites = bool));
        invincibility.add(new ConfigBool(Text.literal("Show title on proc"), () -> Dungeons.showProcTitle, bool -> Dungeons.showProcTitle = bool));

        dungeons.add(invincibility);

        ConfigSection hidePlayers = new ConfigSection(Text.literal("Hide Players"));
        hidePlayers.add(new ConfigBool(Text.literal("Hide after leap"), () -> Dungeons.hideAfterLeap, bool -> Dungeons.hideAfterLeap = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide only in boss"), () -> Dungeons.hideOnlyInBoss, bool -> Dungeons.hideOnlyInBoss = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide at SS"), () -> Dungeons.hideAtSS, bool -> Dungeons.hideAtSS = bool));
        hidePlayers.add(new ConfigBool(Text.literal("SS only hides before terms"), () -> Dungeons.hideBeforeTermsOnly, bool -> Dungeons.hideBeforeTermsOnly = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide players in range"), () -> Dungeons.hidePlayersInRange, bool -> Dungeons.hidePlayersInRange = bool));
        hidePlayers.add(new ConfigDouble(Text.literal("Hiding range"), () -> Dungeons.hidePlayerRange, num -> Dungeons.hidePlayerRange = num, 1, 0, 7));

        dungeons.add(hidePlayers);

        ConfigSection chests = new ConfigSection(Text.literal("Croesus"));
        chests.add(new ConfigBool(Text.literal("Display current chest count"), () -> Dungeons.displayChestCount, bool -> Dungeons.displayChestCount = bool));
        chests.add(new ConfigBool(Text.literal("Only display after run is over"), () -> Dungeons.onlyAfterRunOver, bool -> Dungeons.onlyAfterRunOver = bool));
        chests.add(new ConfigBool(Text.literal("Send chest count warning"), () -> Dungeons.sendChestWarning, bool -> Dungeons.sendChestWarning = bool));
        chests.add(new ConfigInt(Text.literal("Warning at chest"), () -> Dungeons.chestWarningCount, num -> Dungeons.chestWarningCount = num, 1, 1, 60));
        chests.add(new ConfigColor(Text.literal("Widget color"), () -> Dungeons.chestCountColor, color -> Dungeons.chestCountColor = color, "chest-color", false));

        dungeons.add(chests);
        dungeons.add(new ConfigBool(Text.literal("Leap message"), () -> Dungeons.enableLeapMessages, bool -> Dungeons.enableLeapMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Auto requeue"), () -> Dungeons.enableAutoRequeue, bool -> Dungeons.enableAutoRequeue = bool));

        ConfigSection expo = new ConfigSection(Text.literal("Explosive shot"));
        expo.add(new ConfigBool(Text.literal("calculate explosive shot"), () -> Dungeons.calculateCriticalHit, bool -> Dungeons.calculateCriticalHit = bool));
        expo.add(new ConfigBool(Text.literal("Only in boss"), () -> Dungeons.onlyInBoss, bool -> Dungeons.onlyInBoss = bool));
        dungeons.add(expo);

        dungeons.add(new ConfigBool(Text.literal("Notification on key spawn"), () -> Dungeons.enableKeyNotifier, bool -> Dungeons.enableKeyNotifier = bool));
        dungeons.add(new ConfigBool(Text.literal("Secret spawn timer"), () -> Dungeons.enableSecretSpawnTimer, bool -> Dungeons.enableSecretSpawnTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Item highlight"), () -> Dungeons.highlightItems, bool -> Dungeons.highlightItems = bool));
        dungeons.add(new ConfigBool(Text.literal("Combine screen notifications"), () -> Dungeons.combineScreenNotifications, bool -> Dungeons.combineScreenNotifications = bool));
        dungeons.add(new ConfigBool(Text.literal("Don't protect held item in run"), () -> Dungeons.dontProtectHeldItem, bool -> Dungeons.dontProtectHeldItem = bool));
        dungeons.add(new ConfigBool(Text.literal("Hide blaze nametags"), () -> Dungeons.hideBlazeNameTag, bool -> Dungeons.hideBlazeNameTag = bool));
        dungeons.add(new ConfigBool(Text.literal("Disable drop animation"), () -> Dungeons.disableDropAnimation, bool -> Dungeons.disableDropAnimation = bool));
        dungeons.add(new ConfigBool(Text.literal("Mask cooldown highlight"), () -> Dungeons.maskHighlight, bool -> Dungeons.maskHighlight = bool));

        screen.addCategory(dungeons);

        ConfigCategory floor7 = new ConfigCategory("Floor 7");
        floor7.add(new ConfigBool(Text.literal("Combine tick timers"), () -> Floor7.combineTickTimers, bool -> Floor7.combineTickTimers = bool));
        floor7.add(new ConfigBool(Text.literal("Enable player leap count"), () -> Floor7.leapNotifications, bool -> Floor7.leapNotifications = bool));

        ConfigSection waypoints = new ConfigSection(Text.literal("Waypoints"));
        waypoints.add(new ConfigBool(Text.literal("Enable boss waypoints"), () -> Floor7.enableBossWaypoints, bool -> Floor7.enableBossWaypoints = bool));
        waypoints.add(new ConfigBool(Text.literal("Enable placing waypoints"), BossWaypoints::getPlace, BossWaypoints::setPlace));
        waypoints.add(new ConfigColor(Text.literal("Next waypoint color"), () -> Floor7.nextWaypointColor, color -> Floor7.nextWaypointColor = color, "next-waypoint-color", true));
        waypoints.add(new ConfigBool(Text.literal("Next waypoint no depth check"), () -> Floor7.nextWaypointThroughWall, bool -> Floor7.nextWaypointThroughWall = bool));
        floor7.add(waypoints);

        ConfigSection maxor = new ConfigSection(Text.literal("Maxor"));
        maxor.add(new ConfigBool(Text.literal("Crystal Spawn Time"), () -> Floor7.enableCrystalSpawnTime, bool -> Floor7.enableCrystalSpawnTime = bool));
        floor7.add(maxor);

        ConfigSection storm = new ConfigSection(Text.literal("Storm"));
        storm.add(new ConfigBool(Text.literal("Storm Tick timer"), () -> Floor7.enableStormTickTimer, bool -> Floor7.enableStormTickTimer = bool));
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
        goldor.add(new ConfigBool(Text.literal("Make goldor timer tick down"), () -> Floor7.inDeathTicks, bool -> Floor7.inDeathTicks = bool));
        goldor.add(new ConfigBool(Text.literal("Positional messages"), () -> Floor7.enablePositionalMessages, bool -> Floor7.enablePositionalMessages = bool));
        goldor.add(new ConfigBool(Text.literal("Terminal splits"), () -> Section.enableTerminalSplits, bool -> Section.enableTerminalSplits = bool));
        goldor.add(new ConfigOptions<>(Text.literal("Display when"), Section.DisplayTerminalSplitsWhen.values(), () -> Section.displayTerminalSplitsWhen, when -> Section.displayTerminalSplitsWhen = when));
        goldor.add(new ConfigBool(Text.literal("Pre4 completion notification"), () -> Floor7.notifyPre4Completion, bool -> Floor7.notifyPre4Completion = bool));
        goldor.add(new ConfigBool(Text.literal("Disable titles on pre4"), () -> Floor7.disableTitlesAtPre4, bool -> Floor7.disableTitlesAtPre4 = bool));
        goldor.add(new ConfigBool(Text.literal("Melody warning notification"), () -> Floor7.notifiyMelody, bool -> Floor7.notifiyMelody = bool));
        goldor.add(new ConfigBool(Text.literal("Hide most titles in terminals"), () -> Floor7.hideTerminalTitles, bool -> Floor7.hideTerminalTitles = bool));
        goldor.add(new ConfigBool(Text.literal("Send terminal time stamps"), () -> Floor7.terminalTimeStamps, bool -> Floor7.terminalTimeStamps = bool));

        floor7.add(goldor);

        ConfigSection locationNotifier = new ConfigSection(Text.literal("At location notifier"));
        locationNotifier.add(new ConfigBool(Text.literal("Display Location messages on screen"), () -> Floor7.displayLocationNotification, bool -> Floor7.displayLocationNotification = bool));
        locationNotifier.add(new ConfigBool(Text.literal("Hide your own notifications"), () -> Floor7.dontNotifiyForYourself, bool -> Floor7.dontNotifiyForYourself = bool));
        locationNotifier.add(new ConfigInt(Text.literal("Display duration (in client ticks)"), () -> Floor7.notificationDuration, num -> Floor7.notificationDuration = num, 1, 1, 20));
        locationNotifier.add(new ConfigSound(Text.literal("Notification sound"), Floor7.atLocationSound, 2, 2, false));
        locationNotifier.add(new ConfigInt(Text.literal("Sound repetitions"), () -> Floor7.notificationRepetitions, num -> Floor7.notificationRepetitions = num, 1, 0, 20));
        locationNotifier.add(new ConfigButton(Text.literal("Test notification"), () -> LocationNotifier.startNotification("Someone At <location>!!")));

        floor7.add(locationNotifier);

        ConfigSection phase5 = new ConfigSection(Text.literal("Relics and Dragons"));
        phase5.add(new ConfigBool(Text.literal("Relic start timer"), () -> Floor7.enableRelicStartTimer, bool -> Floor7.enableRelicStartTimer = bool));
        phase5.add(new ConfigBool(Text.literal("Replace with progress bar"), () -> Floor7.replaceWithProgressBar, bool -> Floor7.replaceWithProgressBar = bool));
        phase5.add(new ConfigBool(Text.literal("Use valleys progress bar"), () -> Floor7.useValleyBar, bool -> Floor7.useValleyBar = bool));
        phase5.add(new ConfigInt(Text.literal("Relic start timer ticks"), () -> Floor7.relicSpawnTicks, num -> Floor7.relicSpawnTicks = num, 1, 30, 50));
        phase5.add(new ConfigBool(Text.literal("Enable relic placed time"), () -> Floor7.enableRelicPlaceTime, bool -> Floor7.enableRelicPlaceTime = bool));
        phase5.add(new ConfigBool(Text.literal("Block incorrect relic place"), () -> Floor7.blockIncorrectRelicPlace, bool -> Floor7.blockIncorrectRelicPlace = bool));
        phase5.add(new ConfigBool(Text.literal("Highlight picked up relic"), () -> Floor7.renderRelicHighlight, bool -> Floor7.renderRelicHighlight = bool));
        phase5.add(new ConfigBool(Text.literal("Send all relic times"), () -> Floor7.showAllRelicTimes, bool -> Floor7.showAllRelicTimes = bool));
        phase5.add(new ConfigBool(Text.literal("Enable Dragon spawn timers"), () -> Floor7.dragSpawnTimers, bool -> Floor7.dragSpawnTimers = bool));
        phase5.add(new ConfigBool(Text.literal("Send sound on dragon spawn"), () -> Floor7.sendSoundOnDragSpawn, bool -> Floor7.sendSoundOnDragSpawn = bool));
        phase5.add(new ConfigOptions<>(Text.literal("Healer prio"), DragSpawnTimer.Team.values(), () -> Floor7.healerTeam, team -> Floor7.healerTeam = team));

        floor7.add(phase5);

        screen.addCategory(floor7);

        ConfigCategory splits = new ConfigCategory("Splits");
        splits.add(new ConfigBool(Text.literal("Enable Splits"), () -> Phase.enableSplits, bool -> Phase.enableSplits = bool));
        splits.add(new ConfigBool(Text.literal("Include total time"), () -> Phase.includeTotalTime, bool -> Phase.includeTotalTime = bool));
        splits.add(new ConfigBool(Text.literal("Send split in chat when over"), () -> Phase.sendSplitInChat, bool -> Phase.sendSplitInChat = bool));
        splits.add(new ConfigOptions<>(Text.literal("Tick timer type"), Split.TimerType.values(), () -> Split.timerType, type -> Split.timerType = type));

        splits.add(new ConfigColor(Text.literal("Real time color (Inactive)"), () -> Split.realTimeColorInactive, color -> Split.realTimeColorInactive = color, "real-time-inactive", false));
        splits.add(new ConfigColor(Text.literal("Real time color (Ongoing)"), () -> Split.realTimeColorOngoing, color -> Split.realTimeColorOngoing = color, "real-time-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Real time color (Complete)"), () -> Split.realTimeColorComplete, color -> Split.realTimeColorComplete = color, "real-time-complete", false));

        splits.add(new ConfigColor(Text.literal("Server time color (Inactive)"), () -> Split.serverTimeColorInactive, color -> Split.serverTimeColorInactive = color, "server-time-inactive", false));
        splits.add(new ConfigColor(Text.literal("Server time color (Ongoing)"), () -> Split.serverTimeColorOngoing, color -> Split.serverTimeColorOngoing = color, "server-time-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Server time color (Complete)"), () -> Split.serverTimeColorComplete, color -> Split.serverTimeColorComplete = color, "server-time-complete", false));

        splits.add(new ConfigColor(Text.literal("Parentheses color (Inactive)"), () -> Split.parenthesesColorInactive, color -> Split.parenthesesColorInactive = color, "parentheses-inactive", false));
        splits.add(new ConfigColor(Text.literal("Parentheses color (Ongoing)"), () -> Split.parenthesesColorOngoing, color -> Split.parenthesesColorOngoing = color, "parentheses-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Parentheses color (Complete)"), () -> Split.parenthesesColorComplete, color -> Split.parenthesesColorComplete = color, "parentheses-complete", false));
        screen.addCategory(splits);

        ConfigCategory highlight = new ConfigCategory("Mob Highlight");
        highlight.add(new ConfigBool(Text.literal("Enable mob highlight"), () -> MobHighlight.mobHighlight, bool -> MobHighlight.mobHighlight = bool));
        highlight.add(new ConfigBool(Text.literal("Don't highlight invisible mobs"), () -> MobHighlight.dontShowInvisibleMobs, bool -> MobHighlight.dontShowInvisibleMobs = bool));
        highlight.add(new ConfigDouble(Text.literal("Extra Wither Width"), () -> MobHighlight.witherExtraWidth, num -> MobHighlight.witherExtraWidth = num, 0.1, 0, 1.5));
        highlight.add(new ConfigInt(Text.literal("Outline Width"), () -> MobHighlight.outlineWidth, num -> MobHighlight.outlineWidth = num, 1, 1, 10));
        highlight.add(new ConfigOptions<>(Text.literal("Highlight mode"), MobHighlight.HighlightType.values(), () -> MobHighlight.currentHighlight, type -> MobHighlight.currentHighlight = type));
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

        screen.addCategory(highlight);

        ConfigCategory extra = new ConfigCategory("Extra options");
        extra.add(new ConfigString(Text.literal("Message prefix"), () -> ExtraOptions.textPrefix, str -> ExtraOptions.textPrefix = str));
        extra.add(new ConfigBool(Text.literal("Disable fire in f5"), () -> ExtraOptions.hideFireInf5, bool -> ExtraOptions.hideFireInf5 = bool));
        extra.add(new ConfigBool(Text.literal("Hide arrows stuck to entities"), () -> ExtraOptions.hideStuckArrows, bool -> ExtraOptions.hideStuckArrows = bool));
        extra.add(new ConfigBool(Text.literal("Disable ability on cooldown sound"), () -> ExtraOptions.disableAbilityCooldownSound, bool -> ExtraOptions.disableAbilityCooldownSound = bool));
        extra.add(new ConfigBool(Text.literal("Hide dead entities"), () -> ExtraOptions.hideDeadEntities, bool -> ExtraOptions.hideDeadEntities = bool));
        extra.add(new ConfigBool(Text.literal("Item rarity background"), () -> ExtraOptions.itemRarityBackground, bool -> ExtraOptions.itemRarityBackground = bool));
        extra.add(new ConfigBool(Text.literal("Hide potion effects overlay"), () -> ExtraOptions.hideStatusOverLay, bool -> ExtraOptions.hideStatusOverLay = bool));
        extra.add(new ConfigBool(Text.literal("Disable glowing"), () -> ExtraOptions.disableGlowing, bool -> ExtraOptions.disableGlowing = bool));
        extra.add(new ConfigBool(Text.literal("Draw item starCount"), () -> ExtraOptions.drawStarCount, bool -> ExtraOptions.drawStarCount = bool));
        extra.add(new ConfigBool(Text.literal("Highlight protected items"), () -> ExtraOptions.highlightProtectedItem, bool -> ExtraOptions.highlightProtectedItem = bool));
        extra.add(new ConfigBool(Text.literal("Show pbs in chat"), () -> ExtraOptions.showPbs, bool -> ExtraOptions.showPbs = bool));
        extra.add(new ConfigBool(Text.literal("Disable scroll wheel in hotbar"), () -> ExtraOptions.disableScrollHotbar, bool -> ExtraOptions.disableScrollHotbar = bool));
        extra.add(new ConfigBool(Text.literal("Display kicked time"), () -> ExtraOptions.enableKickedTimer, bool -> ExtraOptions.enableKickedTimer = bool));
        extra.add(new ConfigBool(Text.literal("Display rag axe duration"), () -> ExtraOptions.enableRagaxeDisplay, bool -> ExtraOptions.enableRagaxeDisplay = bool));
        extra.add(new ConfigBool(Text.literal("Use custom rag sound"), () -> ExtraOptions.useCustomRagSound, bool -> ExtraOptions.useCustomRagSound = bool));
        extra.add(new ConfigSound(Text.literal("Custom Rag sound"), ExtraOptions.ragSound));
        extra.add(new ConfigBool(Text.literal("Compact hoppity messages"), () -> ExtraOptions.compactHoppityMsgs, bool -> ExtraOptions.compactHoppityMsgs = bool));
        extra.add(new ConfigBool(Text.literal("Disable recipe book"), () -> ExtraOptions.disableRecipeBook, bool -> ExtraOptions.disableRecipeBook = bool));

        ConfigSection pets = new ConfigSection(Text.literal("Pets"));
        pets.add(new ConfigBool(Text.literal("Highlight selected pet"), () -> ExtraOptions.highlightSelectedPet, bool -> ExtraOptions.highlightSelectedPet = bool));
        pets.add(new ConfigBool(Text.literal("Draw selected pet"), () -> ExtraOptions.drawPetHUD, bool -> ExtraOptions.drawPetHUD = bool));
        pets.add(new ConfigBool(Text.literal("Draw the pets sprite"), () -> ExtraOptions.includePetSprite, bool -> ExtraOptions.includePetSprite = bool));
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

        extra.add(ss);
        screen.addCategory(extra);

        ConfigCategory inventory = new ConfigCategory("Inventory buttons");
        inventory.add(new ConfigString(Text.literal("Button 1"), () -> Buttons.command1, str -> Buttons.command1 = str));
        inventory.add(new ConfigString(Text.literal("Button 2"), () -> Buttons.command2, str -> Buttons.command2 = str));
        inventory.add(new ConfigString(Text.literal("Button 3"), () -> Buttons.command3, str -> Buttons.command3 = str));
        inventory.add(new ConfigString(Text.literal("Button 4"), () -> Buttons.command4, str -> Buttons.command4 = str));
        inventory.add(new ConfigString(Text.literal("Button 5"), () -> Buttons.command5, str -> Buttons.command5 = str));
        inventory.add(new ConfigString(Text.literal("Button 6"), () -> Buttons.command6, str -> Buttons.command6 = str));
        inventory.add(new ConfigString(Text.literal("Button 7"), () -> Buttons.command7, str -> Buttons.command7 = str));

        screen.addCategory(inventory);

        return screen;
    }

}
