package blade.addon.utils.config;

import blade.addon.features.DianaNotifier;
import blade.addon.features.dungeon.MobHighlight;
import blade.addon.features.dungeon.f7.BossWaypoints;
import blade.addon.features.dungeon.f7.InvincibilityTimer;
import blade.addon.utils.Constants;
import blade.addon.utils.config.components.Components;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import blade.addon.utils.dungeon.Split;
import config.practical.ConfigurableScreen;
import config.practical.category.ConfigCategory;
import config.practical.manager.ConfigManager;
import config.practical.screenwidgets.ConfigSection;
import config.practical.widgets.ConfigBool;
import config.practical.widgets.color.ConfigColor;
import config.practical.widgets.options.ConfigOptions;
import config.practical.widgets.sliders.ConfigDouble;
import config.practical.widgets.sliders.ConfigInt;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class Config {

    private static final Text TITLE = Text.literal("Blade Addons");
    public static final ConfigManager manager = new ConfigManager("./config/" + Constants.NAMESPACE + ".json",
            List.of(Phase.class, Section.class, Split.class, MobHighlight.class, ExtraOptions.class, DianaNotifier.class, Components.class, Dungeons.class, Floor7.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);

        ConfigCategory dungeons = new ConfigCategory("Dungeons");

        ConfigSection start = new ConfigSection(Text.literal("Start of run"));
        start.add(new ConfigBool(Text.literal("Warp cooldown"), () -> Dungeons.enableWarpCooldown, bool -> Dungeons.enableWarpCooldown = bool));
        start.add(new ConfigBool(Text.literal("Dupe class warning"), () -> Dungeons.detectDuplicateClass, bool -> Dungeons.detectDuplicateClass = bool));
        dungeons.add(start);

        ConfigSection invincibility = new ConfigSection(Text.literal("Invincibility Timer"));
        invincibility.add(new ConfigBool(Text.literal("Enable invincibility display"), () -> Dungeons.displayInvincibilityTimer, bool -> Dungeons.displayInvincibilityTimer = bool));
        invincibility.add(new ConfigOptions<>(Text.literal("Display when"), InvincibilityTimer.DisplayWhen.values(), () -> Dungeons.displayWhen, when -> Dungeons.displayWhen = when));
        invincibility.add(new ConfigBool(Text.literal("Use sprites"), () -> Dungeons.useSprites, bool -> Dungeons.useSprites = bool));
        invincibility.add(new ConfigBool(Text.literal("Show title on proc"), () -> Dungeons.showProcTitle, bool -> Dungeons.showProcTitle = bool));

        dungeons.add(invincibility);

        ConfigSection hidePlayers = new ConfigSection(Text.literal("Hide Players"));
        hidePlayers.add(new ConfigBool(Text.literal("Hide on leap"), () -> Dungeons.hideAfterLeap, bool -> Dungeons.hideAfterLeap = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide only in boss"), () -> Dungeons.hideOnlyInBoss, bool -> Dungeons.hideOnlyInBoss = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide at SS"), () -> Dungeons.hideAtSS, bool -> Dungeons.hideAtSS = bool));
        hidePlayers.add(new ConfigBool(Text.literal("SS only hides before terms"), () -> Dungeons.hideBeforeTermsOnly, bool -> Dungeons.hideBeforeTermsOnly = bool));
        dungeons.add(hidePlayers);

        ConfigSection chests = new ConfigSection(Text.literal("Croesus"));
        chests.add(new ConfigBool(Text.literal("Display current chest count"), () -> Dungeons.displayChestCount, bool -> Dungeons.displayChestCount = bool));
        chests.add(new ConfigBool(Text.literal("Only display after run is over"), () -> Dungeons.onlyAfterRunOver, bool -> Dungeons.onlyAfterRunOver = bool));
        chests.add(new ConfigBool(Text.literal("Send chest count warning"), () -> Dungeons.sendChestWarning, bool -> Dungeons.sendChestWarning = bool));
        chests.add(new ConfigInt(Text.literal("Warning at chest"), () -> Dungeons.chestWarningCount, num -> Dungeons.chestWarningCount = num, 1, 1, 60));

        dungeons.add(chests);
        dungeons.add(new ConfigBool(Text.literal("Leap message"), () -> Dungeons.enableLeapMessages, bool -> Dungeons.enableLeapMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Auto requeue"), () -> Dungeons.enableAutoRequeue, bool -> Dungeons.enableAutoRequeue = bool));
        dungeons.add(new ConfigBool(Text.literal("Death tick timer (not accurate currently)"), () -> Dungeons.enableDeathTickTimer, bool -> Dungeons.enableDeathTickTimer = bool));

        ConfigSection expo = new ConfigSection(Text.literal("Explosive shot"));
        expo.add(new ConfigBool(Text.literal("calculate explosive shot"), () -> Dungeons.calculateCriticalHit, bool -> Dungeons.calculateCriticalHit = bool));
        expo.add(new ConfigBool(Text.literal("Only in boss"), () -> Dungeons.onlyInBoss, bool -> Dungeons.onlyInBoss = bool));
        dungeons.add(expo);

        dungeons.add(new ConfigBool(Text.literal("Notification on key spawn"), () -> Dungeons.enableKeyNotifier, bool -> Dungeons.enableKeyNotifier = bool));
        dungeons.add(new ConfigBool(Text.literal("Secret spawn timer"), () -> Dungeons.enableSecretSpawnTimer, bool -> Dungeons.enableSecretSpawnTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Item highlight"), () -> Dungeons.highlightItems, bool -> Dungeons.highlightItems = bool));
        screen.addCategory(dungeons);

        ConfigCategory floor7 = new ConfigCategory("Floor 7");
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
        storm.add(new ConfigBool(Text.literal("Tick down from 5"), () -> Floor7.tickDownStormTickTimer, bool -> Floor7.tickDownStormTickTimer = bool));
        storm.add(new ConfigBool(Text.literal("First Death time"), () -> Floor7.enableStormDeathTime, bool -> Floor7.enableStormDeathTime = bool));
        storm.add(new ConfigBool(Text.literal("Distance to ledge"), () -> Floor7.displayDistanceToLedge, bool -> Floor7.displayDistanceToLedge = bool));
        storm.add(new ConfigBool(Text.literal("Warn if spirit mask is used"), () -> Floor7.notifyUsedSpiritMask, bool -> Floor7.notifyUsedSpiritMask = bool));
        floor7.add(storm);

        ConfigSection goldor = new ConfigSection(Text.literal("Goldor"));
        goldor.add(new ConfigBool(Text.literal("Goldor tick timer"), () -> Floor7.enableGoldorTickTimer, bool -> Floor7.enableGoldorTickTimer = bool));
        goldor.add(new ConfigBool(Text.literal("Term start time"), () -> Floor7.enableTermStartTimer, bool -> Floor7.enableTermStartTimer = bool));
        goldor.add(new ConfigBool(Text.literal("death ticks intervals"), () -> Floor7.inDeathTicks, bool -> Floor7.inDeathTicks = bool));
        goldor.add(new ConfigBool(Text.literal("Positional messages"), () -> Floor7.enablePositionalMessages, bool -> Floor7.enablePositionalMessages = bool));
        goldor.add(new ConfigBool(Text.literal("Terminal splits"), () -> Section.enableTerminalSplits, bool -> Section.enableTerminalSplits = bool));
        goldor.add(new ConfigOptions<>(Text.literal("Display when"), Section.DisplayTerminalSplitsWhen.values(), () -> Section.displayTerminalSplitsWhen, when -> Section.displayTerminalSplitsWhen = when));


        floor7.add(goldor);

        ConfigSection relic = new ConfigSection(Text.literal("Relics"));
        relic.add(new ConfigBool(Text.literal("Relic start timer"), () -> Floor7.enableRelicStartTimer, bool -> Floor7.enableRelicStartTimer = bool));
        relic.add(new ConfigInt(Text.literal("Relic start timer ticks"), () -> Floor7.relicSpawnTicks, num -> Floor7.relicSpawnTicks = num, 1, 30, 50));
        relic.add(new ConfigBool(Text.literal("Enable relic placed time"), () -> Floor7.enableRelicPlaceTime, bool -> Floor7.enableRelicPlaceTime = bool));
        relic.add(new ConfigBool(Text.literal("Highlight picked up relic"), () -> Floor7.renderRelicHighlight, bool -> Floor7.renderRelicHighlight = bool));
        floor7.add(relic);

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
        highlight.add(new ConfigBool(Text.literal("Dont highlight invisible mobs"), () -> MobHighlight.dontShowInvisibleMobs, bool -> MobHighlight.dontShowInvisibleMobs = bool));
        highlight.add(new ConfigDouble(Text.literal("Extra Wither Width"), () -> MobHighlight.witherExtraWidth, num -> MobHighlight.witherExtraWidth = num, 0.1, 0, 1.5));
        highlight.add(new ConfigOptions<>(Text.literal("Highlight mode"), MobHighlight.HighlightType.values(), () -> MobHighlight.currentHighlight, type -> MobHighlight.currentHighlight = type));
        highlight.add(new ConfigBool(Text.literal("Highlight mimic chests"), () -> MobHighlight.highlightMimicChests, bool -> MobHighlight.highlightMimicChests = bool));
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
        highlight.add(new ConfigColor(Text.literal("Wither color"), () -> MobHighlight.witherFilledColor, color -> MobHighlight.witherFilledColor = color, "wither-filled", true));
        highlight.add(new ConfigColor(Text.literal("Wither outline"), () -> MobHighlight.witherOutlineColor, color -> MobHighlight.witherOutlineColor = color, "wither-outline", true));
        highlight.add(new ConfigColor(Text.literal("Mimic color"), () -> MobHighlight.mimicFilledColor, color -> MobHighlight.mimicFilledColor = color, "mimic-filled", true));
        highlight.add(new ConfigColor(Text.literal("Mimic outline"), () -> MobHighlight.mimicOutlineColor, color -> MobHighlight.mimicOutlineColor = color, "mimic-outline", true));
        screen.addCategory(highlight);

        ConfigCategory extra = new ConfigCategory("Extra options");
        extra.add(new ConfigBool(Text.literal("Disable fire in f5"), () -> ExtraOptions.hideFireInf5, bool -> ExtraOptions.hideFireInf5 = bool));
        extra.add(new ConfigBool(Text.literal("Hide arrows stuck to entities"), () -> ExtraOptions.hideStuckArrows, bool -> ExtraOptions.hideStuckArrows = bool));
        extra.add(new ConfigBool(Text.literal("Disable ability on cooldown sound"), () -> ExtraOptions.disableAbilityCooldownSound, bool -> ExtraOptions.disableAbilityCooldownSound = bool));
        extra.add(new ConfigBool(Text.literal("Hide dead entities"), () -> ExtraOptions.hideDeadEntities, bool -> ExtraOptions.hideDeadEntities = bool));
        extra.add(new ConfigBool(Text.literal("Item rarity background"), () -> ExtraOptions.itemRarityBackground, bool -> ExtraOptions.itemRarityBackground = bool));

        ConfigSection diana = new ConfigSection(Text.literal("Diana notifications"));
        diana.add(new ConfigBool(Text.literal("Send Sound"), () -> DianaNotifier.sendSound, bool -> DianaNotifier.sendSound = bool));
        diana.add(new ConfigBool(Text.literal("Send Waypoint"), () -> DianaNotifier.sendWaypoint, bool -> DianaNotifier.sendWaypoint = bool));
        diana.add(new ConfigBool(Text.literal("Check Harpy"), () -> DianaNotifier.checkHarpy, bool -> DianaNotifier.checkHarpy = bool));
        diana.add(new ConfigBool(Text.literal("Check Bull"), () -> DianaNotifier.checkBull, bool -> DianaNotifier.checkBull = bool));
        diana.add(new ConfigBool(Text.literal("Check Nymph"), () -> DianaNotifier.checkNymph, bool -> DianaNotifier.checkNymph = bool));

        extra.add(diana);

        screen.addCategory(extra);


        return screen;
    }

}
