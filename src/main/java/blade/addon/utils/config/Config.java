package blade.addon.utils.config;

import blade.addon.features.dungeon.CrystalSpawn;
import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.DupeClassChecker;
import blade.addon.features.dungeon.ExplosiveShot;
import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.HidePlayersAfterLeap;
import blade.addon.features.dungeon.InvincibilityTimer;
import blade.addon.features.dungeon.LeapMessage;
import blade.addon.features.dungeon.PositionMessages;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class Config {

    private static final Text TITLE = Text.literal("Blade Addons");
    public static final ConfigManager manager = new ConfigManager("./config/" + Constants.NAMESPACE + ".json",
            List.of(StormTickTimer.class, GoldorTickTimer.class, Phase.class, LeapMessage.class, PositionMessages.class, TermStartTimer.class, Split.class, DeathTickTimer.class, ExplosiveShot.class,
                    InvincibilityTimer.class, WarpCooldown.class, DupeClassChecker.class, HidePlayersAfterLeap.class, CrystalSpawn.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);

        ConfigCategory dungeons = new ConfigCategory("Dungeons");

        ConfigSection storm = new ConfigSection(Text.literal("Storm"));
        storm.add(new ConfigBool(Text.literal("Tick timer"), () -> StormTickTimer.enableStormTickTimer, bool -> StormTickTimer.enableStormTickTimer = bool));
        storm.add(new ConfigBool(Text.literal("First Death time"), () -> StormTickTimer.enableStormDeathTime, bool -> StormTickTimer.enableStormDeathTime = bool));
        dungeons.add(storm);

        dungeons.add(new ConfigBool(Text.literal("Leap message"), () -> LeapMessage.enableLeapMessages, bool -> LeapMessage.enableLeapMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Positional messages"), () -> PositionMessages.enablePositionalMessages, bool ->  PositionMessages.enablePositionalMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Auto requeue"), () -> Phase.autoReque, bool ->  Phase.autoReque = bool));
        dungeons.add(new ConfigBool(Text.literal("Term start time"), () -> TermStartTimer.enableTermStartTimer, bool ->  TermStartTimer.enableTermStartTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Death tick timer"), () -> DeathTickTimer.enableDeathTickTimer, bool ->  DeathTickTimer.enableDeathTickTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("calculate explosive shot"), () -> ExplosiveShot.calculateCriticalHit, bool ->  ExplosiveShot.calculateCriticalHit = bool));
        dungeons.add(new ConfigBool(Text.literal("Warp cooldown"), () -> WarpCooldown.enableWarpCooldown, bool -> WarpCooldown.enableWarpCooldown = bool));
        dungeons.add(new ConfigBool(Text.literal("Dupe class warning"), () -> DupeClassChecker.detectDuplicateClass, bool -> DupeClassChecker.detectDuplicateClass = bool));
        dungeons.add(new ConfigBool(Text.literal("Crystal Spawn Time"), () -> CrystalSpawn.enableCrystalSpawnTime, bool -> CrystalSpawn.enableCrystalSpawnTime = bool));

        ConfigSection invincibility = new ConfigSection(Text.literal("Invincibility stuff"));
        invincibility.add(new ConfigBool(Text.literal("Enable invincibility display"),() -> InvincibilityTimer.displayInvincibilityTimer, bool -> InvincibilityTimer.displayInvincibilityTimer = bool));
        invincibility.add(new ConfigOptions<>(Text.literal("Display when"), InvincibilityTimer.DisplayWhen.values(), () -> InvincibilityTimer.displayWhen, when -> InvincibilityTimer.displayWhen = when));
        dungeons.add(invincibility);

        ConfigSection hideAfterLeap = new ConfigSection(Text.literal("Hide players after leap"));
        hideAfterLeap.add(new ConfigBool(Text.literal("Enable setting"), () -> HidePlayersAfterLeap.hideAfterLeap, bool -> HidePlayersAfterLeap.hideAfterLeap = bool));
        hideAfterLeap.add(new ConfigBool(Text.literal("Hide only in boss"), () -> HidePlayersAfterLeap.hideOnlyInBoss, bool -> HidePlayersAfterLeap.hideOnlyInBoss = bool));
        dungeons.add(hideAfterLeap);

        ConfigSection goldor = new ConfigSection(Text.literal("Goldor tick timer"));
        goldor.add(new ConfigBool(Text.literal("Enable tick timer"), () -> GoldorTickTimer.enableGoldorTickTimer, bool -> GoldorTickTimer.enableGoldorTickTimer = bool));
        goldor.add(new ConfigBool(Text.literal("death ticks intervals"), () -> GoldorTickTimer.inDeathTicks, bool -> GoldorTickTimer.inDeathTicks = bool));
        dungeons.add(goldor);



        screen.addCategory(dungeons);

        ConfigCategory splits = new ConfigCategory("Splits");
        splits.add(new ConfigBool(Text.literal("Splits"), () -> Phase.enableSplits, bool -> Phase.enableSplits = bool));
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

        return screen;
    }

}
