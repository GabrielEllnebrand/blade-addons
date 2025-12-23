package blade.addon.utils.times;

import blade.addon.utils.config.FolderUtility;
import config.practical.manager.ConfigManager;
import config.practical.manager.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public class PersonalBests {

    private static final ArrayList<PersonalBest> pbList = new ArrayList<>();

    public static final ConfigManager pbManager = new ConfigManager(FolderUtility.CONFIG_PATH + FolderUtility.PBS_NAME,
            List.of(PersonalBests.class));

    @ConfigValue
    public static PersonalBest redRelicTime = new PersonalBest();

    @ConfigValue
    public static PersonalBest orangeRelicTime = new PersonalBest();

    @ConfigValue
    public static PersonalBest blueRelicTime = new PersonalBest();

    @ConfigValue
    public static PersonalBest greenRelicTime = new PersonalBest();

    @ConfigValue
    public static PersonalBest purpleRelicTime = new PersonalBest();

    @ConfigValue
    public static PersonalBest crystalTime = new PersonalBest();

    @ConfigValue
    public static PersonalBest predevTime = new PersonalBest();

    @ConfigValue
    public static PersonalBest practiseSSTime = new PersonalBest();
    @ConfigValue
    public static PersonalBest practiseSSRealisticTime = new PersonalBest();
    @ConfigValue
    public static PersonalBest practiseSSRealisticLuckyTime = new PersonalBest();
    @ConfigValue
    public static PersonalBest practiseSSLuckyTime = new PersonalBest();

    public static void save() {
        pbManager.save();
    }

    public static void reset() {
        for (PersonalBest personalBest: pbList) {
            personalBest.reset();
        }
    }

    public static void register(PersonalBest personalBest) {
        pbList.add(personalBest);
    }

}
