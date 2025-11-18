package blade.addon.utils.config;

import blade.addon.features.dungeon.AutoRequeue;
import blade.addon.features.dungeon.ChestCounter;
import blade.addon.features.dungeon.CrystalSpawn;
import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.DupeClassChecker;
import blade.addon.features.dungeon.ExplosiveShot;
import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.HidePlayersAfterLeap;
import blade.addon.features.dungeon.InvincibilityTimer;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.LeapMessage;
import blade.addon.features.dungeon.MobHighlight;
import blade.addon.features.dungeon.PositionMessages;
import blade.addon.features.dungeon.RelicTimer;
import blade.addon.features.dungeon.StormTickTimer;
import blade.addon.features.dungeon.TermStartTimer;
import blade.addon.features.dungeon.WarpCooldown;
import blade.addon.utils.Constants;
import blade.addon.utils.dungeon.Phase;
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
            List.of(StormTickTimer.class, GoldorTickTimer.class, Phase.class, LeapMessage.class, PositionMessages.class, TermStartTimer.class, Split.class, DeathTickTimer.class, ExplosiveShot.class,
                    InvincibilityTimer.class, WarpCooldown.class, DupeClassChecker.class, HidePlayersAfterLeap.class, CrystalSpawn.class, KeyNotifier.class, RelicTimer.class, AutoRequeue.class, MobHighlight.class, ChestCounter.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);

        ConfigCategory dungeons = new ConfigCategory("Dungeons");

        ConfigSection start = new ConfigSection(Text.literal("Start of run"));
        start.add(new ConfigBool(Text.literal("Warp cooldown"), () -> WarpCooldown.enableWarpCooldown, bool -> WarpCooldown.enableWarpCooldown = bool));
        start.add(new ConfigBool(Text.literal("Dupe class warning"), () -> DupeClassChecker.detectDuplicateClass, bool -> DupeClassChecker.detectDuplicateClass = bool));
        dungeons.add(start);

        ConfigSection invincibility = new ConfigSection(Text.literal("Invincibility stuff"));
        invincibility.add(new ConfigBool(Text.literal("Enable invincibility display"),() -> InvincibilityTimer.displayInvincibilityTimer, bool -> InvincibilityTimer.displayInvincibilityTimer = bool));
        invincibility.add(new ConfigOptions<>(Text.literal("Display when"), InvincibilityTimer.DisplayWhen.values(), () -> InvincibilityTimer.displayWhen, when -> InvincibilityTimer.displayWhen = when));
        dungeons.add(invincibility);

        ConfigSection hideAfterLeap = new ConfigSection(Text.literal("Hide players after leap"));
        hideAfterLeap.add(new ConfigBool(Text.literal("Enable setting"), () -> HidePlayersAfterLeap.hideAfterLeap, bool -> HidePlayersAfterLeap.hideAfterLeap = bool));
        hideAfterLeap.add(new ConfigBool(Text.literal("Hide only in boss"), () -> HidePlayersAfterLeap.hideOnlyInBoss, bool -> HidePlayersAfterLeap.hideOnlyInBoss = bool));
        dungeons.add(hideAfterLeap);

        ConfigSection maxor = new ConfigSection(Text.literal("Maxor"));
        maxor.add(new ConfigBool(Text.literal("Crystal Spawn Time"), () -> CrystalSpawn.enableCrystalSpawnTime, bool -> CrystalSpawn.enableCrystalSpawnTime = bool));
        dungeons.add(maxor);

        ConfigSection storm = new ConfigSection(Text.literal("Storm"));
        storm.add(new ConfigBool(Text.literal("Tick timer"), () -> StormTickTimer.enableStormTickTimer, bool -> StormTickTimer.enableStormTickTimer = bool));
        storm.add(new ConfigBool(Text.literal("First Death time"), () -> StormTickTimer.enableStormDeathTime, bool -> StormTickTimer.enableStormDeathTime = bool));
        dungeons.add(storm);

        ConfigSection terms = new ConfigSection(Text.literal("Terminals"));
        terms.add(new ConfigBool(Text.literal("Term start time"), () -> TermStartTimer.enableTermStartTimer, bool ->  TermStartTimer.enableTermStartTimer = bool));
        dungeons.add(terms);

        ConfigSection goldor = new ConfigSection(Text.literal("Goldor"));
        goldor.add(new ConfigBool(Text.literal("Enable tick timer"), () -> GoldorTickTimer.enableGoldorTickTimer, bool -> GoldorTickTimer.enableGoldorTickTimer = bool));
        goldor.add(new ConfigBool(Text.literal("death ticks intervals"), () -> GoldorTickTimer.inDeathTicks, bool -> GoldorTickTimer.inDeathTicks = bool));
        dungeons.add(goldor);

        ConfigSection relic = new ConfigSection(Text.literal("Relics"));
        relic.add(new ConfigBool(Text.literal("Relic start timer"), () -> RelicTimer.enableRelicStartTimer, bool -> RelicTimer.enableRelicStartTimer = bool));
        relic.add(new ConfigInt(Text.literal("Relic start timer ticks"), () -> RelicTimer.relicSpawnTicks, num -> RelicTimer.relicSpawnTicks = num, 1, 30, 50));
        relic.add(new ConfigBool(Text.literal("Enable relic placed time"), () -> RelicTimer.enableRelicPlaceTime, bool -> RelicTimer.enableRelicPlaceTime = bool));
        relic.add(new ConfigBool(Text.literal("Highlight picked up relic"), () -> RelicTimer.renderRelicHighlight, bool -> RelicTimer.renderRelicHighlight = bool));
        dungeons.add(relic);

        ConfigSection chests = new ConfigSection(Text.literal("Croesus"));
        chests.add(new ConfigBool(Text.literal("Display current chest count"), () -> ChestCounter.displayChestCount, bool -> ChestCounter.displayChestCount = bool));
        chests.add(new ConfigBool(Text.literal("Send chest count warning"), () -> ChestCounter.sendChestWarning, bool -> ChestCounter.sendChestWarning = bool));
        chests.add(new ConfigInt(Text.literal("Warning at chest"), () -> ChestCounter.chestWarningCount, num -> ChestCounter.chestWarningCount = num, 1, 1, 60));

        dungeons.add(chests);

        dungeons.add(new ConfigBool(Text.literal("Leap message"), () -> LeapMessage.enableLeapMessages, bool -> LeapMessage.enableLeapMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Positional messages"), () -> PositionMessages.enablePositionalMessages, bool ->  PositionMessages.enablePositionalMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Auto requeue"), () -> AutoRequeue.enableAutoRequeue, bool ->  AutoRequeue.enableAutoRequeue = bool));
        dungeons.add(new ConfigBool(Text.literal("Death tick timer"), () -> DeathTickTimer.enableDeathTickTimer, bool ->  DeathTickTimer.enableDeathTickTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("calculate explosive shot"), () -> ExplosiveShot.calculateCriticalHit, bool ->  ExplosiveShot.calculateCriticalHit = bool));
        dungeons.add(new ConfigBool(Text.literal("Notification on key spawn"), () -> KeyNotifier.enableKeyNotifier, bool ->  KeyNotifier.enableKeyNotifier = bool));
        screen.addCategory(dungeons);

        ConfigCategory splits = new ConfigCategory("Splits");
        splits.add(new ConfigBool(Text.literal("Enable Splits"), () -> Phase.enableSplits, bool -> Phase.enableSplits = bool));
        splits.add(new ConfigBool(Text.literal("Include total time"), () -> Phase.includeTotalTime, bool -> Phase.includeTotalTime = bool));
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
        screen.addCategory(highlight);


        return screen;
    }

}
