package blade.addon.utils.config.values;

import blade.addon.features.other.InventoryButton;
import config.practical.manager.ConfigValue;

public class Buttons {

    public static void init() {

    }

    @ConfigValue
    public static String command1 = "";

    @ConfigValue
    public static String command2 = "";

    @ConfigValue
    public static String command3 = "";

    @ConfigValue
    public static String command4 = "";

    @ConfigValue
    public static String command5 = "";

    @ConfigValue
    public static String command6 = "";

    @ConfigValue
    public static String command7 = "";


    public static InventoryButton button1 = new InventoryButton(77, 5, () -> command1);

    public static InventoryButton button2 = new InventoryButton(77, 23, () -> command2);

    public static InventoryButton button3 = new InventoryButton(77, 41, () -> command3);

    public static InventoryButton button4 = new InventoryButton(133, 5, () -> command4);

    public static InventoryButton button5 = new InventoryButton(151, 5, () -> command5);

    public static InventoryButton button6 = new InventoryButton(133, 61, () -> command6);

    public static InventoryButton button7 = new InventoryButton(151, 61, () -> command7);



}
