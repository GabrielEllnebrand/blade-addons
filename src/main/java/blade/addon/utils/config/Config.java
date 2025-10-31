package blade.addon.utils.config;

import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.LeapMessage;
import blade.addon.features.dungeon.PositionMessages;
import blade.addon.features.dungeon.StormTickTimer;
import blade.addon.features.dungeon.TermStartTimer;
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
            List.of(StormTickTimer.class, GoldorTickTimer.class, Phase.class, LeapMessage.class, PositionMessages.class, TermStartTimer.class, Split.class, DeathTickTimer.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);

        ConfigCategory dungeons = new ConfigCategory("Dungeons");
        dungeons.add(new ConfigBool(Text.literal("Storm tick timer"), () -> StormTickTimer.enableStormTickTimer, bool -> StormTickTimer.enableStormTickTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Leap message"), () -> LeapMessage.enableLeapMessages, bool -> LeapMessage.enableLeapMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Positional messages"), () -> PositionMessages.enablePositionalMessages, bool ->  PositionMessages.enablePositionalMessages = bool));
        dungeons.add(new ConfigBool(Text.literal("Auto reque"), () -> Phase.autoReque, bool ->  Phase.autoReque = bool));
        dungeons.add(new ConfigBool(Text.literal("Term start time"), () -> TermStartTimer.enableTermStartTimer, bool ->  TermStartTimer.enableTermStartTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Death tick timer"), () -> DeathTickTimer.enableDeathTickTimer, bool ->  DeathTickTimer.enableDeathTickTimer = bool));

        config.practical.screenwidgets.ConfigSection goldor = new ConfigSection(Text.literal("Goldor tick timer"));
        goldor.add(new ConfigBool(Text.literal("Enable tick timer"), () -> GoldorTickTimer.enableGoldorTickTimer, bool -> GoldorTickTimer.enableGoldorTickTimer = bool));
        goldor.add(new ConfigBool(Text.literal("death ticks intervals"), () -> GoldorTickTimer.inDeathTicks, bool -> GoldorTickTimer.inDeathTicks = bool));
        dungeons.add(goldor);

        screen.addCategory(dungeons);

        ConfigCategory splits = new ConfigCategory("Splits");
        splits.add(new ConfigBool(Text.literal("Splits"), () -> Phase.enableSplits, bool -> Phase.enableSplits = bool));
        splits.add(new ConfigColor(Text.literal("Real time color (Inactive)"), () -> Split.realTimeColorInactive, color -> Split.realTimeColorInactive = color, "real-time-inactive", false));
        splits.add(new ConfigColor(Text.literal("Real time color (Ongoing)"), () -> Split.realTimeColorOngoing, color -> Split.realTimeColorOngoing = color, "real-time-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Real time color (Complete)"), () -> Split.realTimeColorComplete, color -> Split.realTimeColorComplete = color, "real-time-complete", false));

        splits.add(new ConfigColor(Text.literal("Server time color (Inactive)"), () -> Split.serverTimeColorInactive, color -> Split.serverTimeColorInactive = color, "server-time-inactive", false));
        splits.add(new ConfigColor(Text.literal("Server time color (Ongoing)"), () -> Split.serverTimeColorOngoing, color -> Split.serverTimeColorOngoing = color, "server-time-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Server time color (Complete)"), () -> Split.serverTimeColorComplete, color -> Split.serverTimeColorComplete = color, "server-time-complete", false));

        splits.add(new ConfigColor(Text.literal("Parentheses color (Inactive)"), () -> Split.parenthesesColorInactive, color -> Split.parenthesesColorInactive = color, "parentheses-inactive", false));
        splits.add(new ConfigColor(Text.literal("Parentheses color (Ongoing)"), () -> Split.parenthesesColorOngoing, color -> Split.parenthesesColorOngoing = color, "parentheses-ongoing", false));
        splits.add(new ConfigColor(Text.literal("Parentheses color (Complete)"), () -> Split.parenthesesColorComplete, color -> Split.parenthesesColorComplete = color, "parentheses-complete", false));

        splits.add(new ConfigOptions<>(Text.literal("Tick timer type"), Split.TimerType.values(), () -> Split.timerType, type -> Split.timerType = type));

        screen.addCategory(splits);

        return screen;
    }

}
