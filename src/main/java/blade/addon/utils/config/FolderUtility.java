package blade.addon.utils.config;

import blade.addon.features.item.ProtectItem;
import blade.addon.utils.Constants;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.times.PersonalBests;

import java.io.File;

public class FolderUtility {

    public static final String OLD_PATH = "./config/";
    public static final String CONFIG_PATH = "config/blade-addons/";

    public static final String ADDONS_NAME = Constants.NAMESPACE + ".json";
    public static final String WAYPOINTS_NAME = Constants.NAMESPACE + "-waypoints.json";
    public static final String PBS_NAME = Constants.NAMESPACE + "-pbs.json";
    public static final String PROTECT_ITEMS_NAME = Constants.NAMESPACE + "-protected-items.json";
    public static final String NOTIFICATIONS_NAME = Constants.NAMESPACE + "-notifications.json";
    public static final String FILTERS_NAME = Constants.NAMESPACE + "-filters.json";

    public static void init() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Debug.LOGGER.error("Failed to create blade-addons directory");
            }
        }

        ProtectItem.itemManager.load();
        PersonalBests.pbManager.load();
    }

}
