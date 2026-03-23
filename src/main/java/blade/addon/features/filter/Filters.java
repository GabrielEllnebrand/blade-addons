package blade.addon.features.filter;

import blade.addon.utils.config.FolderUtility;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.debug.Debug;
import config.practical.manager.ConfigManager;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class Filters {

    public static final ConfigManager filterManager = new ConfigManager(FolderUtility.CONFIG_PATH + FolderUtility.FILTERS_NAME,
            List.of(Filters.class));

    @ConfigValue
    public static ArrayList<String> filters = new ArrayList<>();

    public static void init() {
        filterManager.load();
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (overlay || ExtraOptions.disableAllFilters) return true;
            String string = message.getString();

            if (ExtraOptions.ignoreColorCodesFilter) {
                string = string.replaceAll("§.", "");
            }

            for (String filter : filters) {
                try {
                    if (string.matches(filter)) return false;
                } catch (PatternSyntaxException e) {
                    Debug.sendDebugMessage(Text.literal("Invalid regex: " + filter));
                }
            }
            return true;
        });
    }

}
