package blade.addon.utils.config;

import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.StormTickTimer;
import blade.addon.utils.Constants;
import blade.addon.utils.dungeon.Phase;
import config.practical.ConfigSection;
import config.practical.ConfigurableScreen;
import config.practical.category.ConfigCategory;
import config.practical.manager.ConfigManager;
import config.practical.widgets.ConfigBool;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class Config {

    private static final Text TITLE = Text.literal("Blade Addons");
    public static final ConfigManager manager = new ConfigManager("./config/" + Constants.NAMESPACE + ".json",
            List.of(StormTickTimer.class, GoldorTickTimer.class, Phase.class));

    public static Screen createScreen(Screen parent) {
        ConfigurableScreen screen = new ConfigurableScreen(TITLE, parent, manager);

        ConfigCategory dungeons = new ConfigCategory("Dungeons");
        dungeons.add(new ConfigBool(Text.literal("Storm tick timer"), () -> StormTickTimer.enableStormTickTimer, bool -> StormTickTimer.enableStormTickTimer = bool));
        dungeons.add(new ConfigBool(Text.literal("Splits"), () -> Phase.enableSplits, bool -> Phase.enableSplits = bool));

        ConfigSection goldor = new ConfigSection(Text.literal("Goldor tick timer"));
        goldor.add(new ConfigBool(Text.literal("Enable tick timer"), () -> GoldorTickTimer.enableGoldorTickTimer, bool -> GoldorTickTimer.enableGoldorTickTimer = bool));
        goldor.add(new ConfigBool(Text.literal("death ticks intervals"), () -> GoldorTickTimer.inDeathTicks, bool -> GoldorTickTimer.inDeathTicks = bool));
        dungeons.add(goldor);

        screen.addCategory(dungeons);


        return screen;
    }

}
