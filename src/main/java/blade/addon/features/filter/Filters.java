package blade.addon.features.filter;

import blade.addon.utils.config.FolderUtility;
import config.practical.manager.ConfigManager;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.ArrayList;
import java.util.List;

public class Filters {

    public static final ConfigManager filterManager = new ConfigManager(FolderUtility.CONFIG_PATH + FolderUtility.FILTERS_NAME,
            List.of(Filters.class));

    @ConfigValue
    public static ArrayList<String> filters = new ArrayList<>();

    public static void init() {
        filterManager.load();
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (overlay) return true;
            String string = message.getString();
            for (String filter : filters) {
                if (string.matches(filter)) return false;
            }
            return true;
        });
    }

}
