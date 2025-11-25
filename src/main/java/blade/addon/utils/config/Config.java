package blade.addon.utils.config;

import blade.addon.features.dungeon.AutoRequeue;
import blade.addon.features.dungeon.ChestCounter;
import blade.addon.features.dungeon.ItemHighlight;
import blade.addon.features.dungeon.f7.CrystalSpawn;
import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.DupeClassChecker;
import blade.addon.features.dungeon.ExplosiveShot;
import blade.addon.features.dungeon.f7.DistanceToLedge;
import blade.addon.features.dungeon.f7.GoldorTickTimer;
import blade.addon.features.dungeon.HidePlayers;
import blade.addon.features.dungeon.f7.InvincibilityTimer;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.LeapMessage;
import blade.addon.features.dungeon.MobHighlight;
import blade.addon.features.dungeon.f7.PositionMessages;
import blade.addon.features.dungeon.f7.RelicTimer;
import blade.addon.features.dungeon.SecretSpawnTimer;
import blade.addon.features.ExtraOptions;
import blade.addon.features.dungeon.f7.StormTickTimer;
import blade.addon.features.dungeon.f7.TermStartTimer;
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
                    InvincibilityTimer.class, WarpCooldown.class, DupeClassChecker.class, HidePlayers.class, CrystalSpawn.class,
                    KeyNotifier.class, RelicTimer.class, AutoRequeue.class, MobHighlight.class, ChestCounter.class, SecretSpawnTimer.class, DistanceToLedge.class, ItemHighlight.class, ExtraOptions.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);

        ConfigCategory dungeons = new ConfigCategory("Dungeons");

        ConfigSection start = new ConfigSection(Text.literal("Start of run"));
        start.add(new ConfigBool(Text.literal("Warp cooldown"), () -> WarpCooldown.enableWarpCooldown, bool -> WarpCooldown.enableWarpCooldown = bool));
        start.add(new ConfigBool(Text.literal("Dupe class warning"), () -> DupeClassChecker.detectDuplicateClass, bool -> DupeClassChecker.detectDuplicateClass = bool));
        dungeons.add(start);

        ConfigSection invincibility = new ConfigSection(Text.literal("Invincibility Timer"));
        invincibility.add(new ConfigBool(Text.literal("Enable invincibility display"), () -> InvincibilityTimer.displayInvincibilityTimer, bool -> InvincibilityTimer.displayInvincibilityTimer = bool));
        invincibility.add(new ConfigOptions<>(Text.literal("Display when"), InvincibilityTimer.DisplayWhen.values(), () -> InvincibilityTimer.displayWhen, when -> InvincibilityTimer.displayWhen = when));
        invincibility.add(new ConfigBool(Text.literal("Use sprites"), () -> InvincibilityTimer.useSprites, bool -> InvincibilityTimer.useSprites = bool));

        dungeons.add(invincibility);

        ConfigSection hidePlayers = new ConfigSection(Text.literal("Hide Players"));
        hidePlayers.add(new ConfigBool(Text.literal("Hide on leap"), () -> HidePlayers.hideAfterLeap, bool -> HidePlayers.hideAfterLeap = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide only in boss"), () -> HidePlayers.hideOnlyInBoss, bool -> HidePlayers.hideOnlyInBoss = bool));
        hidePlayers.add(new ConfigBool(Text.literal("Hide at SS"), () -> HidePlayers.hideAtSS, bool -> HidePlayers.hideAtSS = bool));
        hidePlayers.add(new ConfigBool(Text.literal("SS only hides before terms"), () -> HidePlayers.hideBeforeTermsOnly, bool -> HidePlayers.hideBeforeTermsOnly = bool));
        dungeons.add(hidePlayers);

        ConfigSection chests = new ConfigSection(Text.literal("Croesus"));
        chests.add(new ConfigBool(Text.literal("Display current chest count"), () -> ChestCounter.displayChestCount, bool -> ChestCounter.displayChestCount = bool));
        chests.add(new ConfigBool(Text.literal("Send chest count warning"), () -> ChestCounter.sendChestWarning, bool -> ChestCounter.sendChestWarning = bool));
        chests.add(new ConfigInt(Text.literal("Warning at chest"), () -> ChestCounter.chestWarningCount, num -> ChestCounter.chestWarningCount = num, 1, 1, 60));

        dungeons.add(chests);

        dungeons.add(new ConfigBool(Text.literal("Leap message"), () -> LeapMessage.enableLeapMessages, bool -> LeapMessage.enableLeapMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Auto requeue"), () -> AutoRequeue.enableAutoRequeue, bool -> AutoRequeue.enableAutoRequeue = bool));
        dungeons.add(new ConfigBool(Text.literal("Death tick timer (not accurate currently)"), () -> DeathTickTimer.enableDeathTickTimer, bool -> DeathTickTimer.enableDeathTickTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("calculate explosive shot"), () -> ExplosiveShot.calculateCriticalHit, bool -> ExplosiveShot.calculateCriticalHit = bool));
        dungeons.add(new ConfigBool(Text.literal("Notification on key spawn"), () -> KeyNotifier.enableKeyNotifier, bool -> KeyNotifier.enableKeyNotifier = bool));
        dungeons.add(new ConfigBool(Text.literal("Secret spawn timer"), () -> SecretSpawnTimer.enableSecretSpawnTimer, bool -> SecretSpawnTimer.enableSecretSpawnTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Item highlight"), () -> ItemHighlight.highlightItems, bool -> ItemHighlight.highlightItems = bool));
        screen.addCategory(dungeons);

        ConfigCategory floor7 = new ConfigCategory("Floor 7");

        ConfigSection maxor = new ConfigSection(Text.literal("Maxor"));
        maxor.add(new ConfigBool(Text.literal("Crystal Spawn Time"), () -> CrystalSpawn.enableCrystalSpawnTime, bool -> CrystalSpawn.enableCrystalSpawnTime = bool));
        floor7.add(maxor);

        ConfigSection storm = new ConfigSection(Text.literal("Storm"));
        storm.add(new ConfigBool(Text.literal("Storm Tick timer"), () -> StormTickTimer.enableStormTickTimer, bool -> StormTickTimer.enableStormTickTimer = bool));
        storm.add(new ConfigBool(Text.literal("First Death time"), () -> StormTickTimer.enableStormDeathTime, bool -> StormTickTimer.enableStormDeathTime = bool));
        storm.add(new ConfigBool(Text.literal("Distance to ledge"), () -> DistanceToLedge.displayDistanceToLedge, bool -> DistanceToLedge.displayDistanceToLedge = bool));
        floor7.add(storm);

        ConfigSection goldor = new ConfigSection(Text.literal("Goldor"));
        goldor.add(new ConfigBool(Text.literal("Goldor tick timer"), () -> GoldorTickTimer.enableGoldorTickTimer, bool -> GoldorTickTimer.enableGoldorTickTimer = bool));
        goldor.add(new ConfigBool(Text.literal("Term start time"), () -> TermStartTimer.enableTermStartTimer, bool -> TermStartTimer.enableTermStartTimer = bool));
        goldor.add(new ConfigBool(Text.literal("death ticks intervals"), () -> GoldorTickTimer.inDeathTicks, bool -> GoldorTickTimer.inDeathTicks = bool));
        goldor.add(new ConfigBool(Text.literal("Positional messages"), () -> PositionMessages.enablePositionalMessages, bool -> PositionMessages.enablePositionalMessages = bool));

        floor7.add(goldor);

        ConfigSection relic = new ConfigSection(Text.literal("Relics"));
        relic.add(new ConfigBool(Text.literal("Relic start timer"), () -> RelicTimer.enableRelicStartTimer, bool -> RelicTimer.enableRelicStartTimer = bool));
        relic.add(new ConfigInt(Text.literal("Relic start timer ticks"), () -> RelicTimer.relicSpawnTicks, num -> RelicTimer.relicSpawnTicks = num, 1, 30, 50));
        relic.add(new ConfigBool(Text.literal("Enable relic placed time"), () -> RelicTimer.enableRelicPlaceTime, bool -> RelicTimer.enableRelicPlaceTime = bool));
        relic.add(new ConfigBool(Text.literal("Highlight picked up relic"), () -> RelicTimer.renderRelicHighlight, bool -> RelicTimer.renderRelicHighlight = bool));
        floor7.add(relic);

        screen.addCategory(floor7);

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

        screen.addCategory(extra);


        return screen;
    }

}
