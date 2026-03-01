package blade.addon.utils.config.values;

import blade.addon.utils.Constants;
import config.practical.data.SoundData;
import config.practical.manager.ConfigValue;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class ExtraOptions {
    @ConfigValue
    public static boolean disableAbilityCooldownSound = true;

    @ConfigValue
    public static String textPrefix = "";

    @ConfigValue
    public static boolean highlightSelectedPet = false;

    @ConfigValue
    public static boolean drawPetHUD = false;

    @ConfigValue
    public static boolean includePetSprite = true;

    @ConfigValue
    public static boolean showPbs = true;

    @ConfigValue
    public static boolean disableScrollHotbar = false;

    @ConfigValue
    public static boolean enableKickedTimer = true;

    @ConfigValue
    public static boolean enableRagaxeDisplay = false;

    @ConfigValue
    public static boolean autoSkip = true;

    @ConfigValue
    public static boolean realisticDelay = false;

    @ConfigValue
    public static boolean includeLuckyButton = false;

    @ConfigValue
    public static double luckyButtonRng = 0.1;

    @ConfigValue
    public static int luckyButtonColor = 0xff4a4f4b;

    @ConfigValue
    public static boolean practiceSSAnywhere = false;

    @ConfigValue
    public static boolean blockUnluckyButtonClick = false;

    @ConfigValue
    public static BlockPos startButton = new BlockPos(2, 2, 2);

    @ConfigValue
    public static boolean disableRecipeBook = false;

    @ConfigValue
    public static boolean useCustomRagSound = false;

    @ConfigValue
    public static SoundData ragSound = new SoundData(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1, 1);

    @ConfigValue
    public static boolean sendOnPetSound = false;

    @ConfigValue
    public static  SoundData petSound = new SoundData(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1, 1);

    @ConfigValue
    public static boolean copyChat = false;

    @ConfigValue
    public static boolean removeColorCodes = false;

    @ConfigValue
    public static boolean replaceColorChars = false;

    @ConfigValue
    public static boolean copyLineOnly = false;

    @ConfigValue
    public static boolean disableBonzoSound = false;

    @ConfigValue
    public static boolean useOldRagSound = false;

    @ConfigValue
    public static SoundData ssSound = new SoundData(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 0, 1);

    @ConfigValue
    public static boolean moveToolTip = false;

    @ConfigValue
    public static boolean sendPetSwapNotification = false;

    @ConfigValue
    public static int timerPrefixColor = Constants.DARK_PURPLE;

    @ConfigValue
    public static boolean oldBonzoSound = false;

    @ConfigValue
    public static boolean displayCurrentArrow = false;

    @ConfigValue
    public static boolean arrowSwapNotification = false;

    @ConfigValue
    public static boolean stunWaypoint = false;

    @ConfigValue
    public static int petHighlightColor = 0xffff0000;

    @ConfigValue
    public static boolean displayPetLevel = false;

    @ConfigValue
    public static boolean toggleableSearchBar = true;

    @ConfigValue
    public static boolean copyChatFeedback = false;

    @ConfigValue
    public static boolean oldTubaSound = false;

    @ConfigValue
    public static boolean ignoreColorCodesFilter = false;

    @ConfigValue
    public static boolean ignoreColorCodesNotification = false;

    @ConfigValue
    public static boolean enableReaperDisplay = false;

    @ConfigValue
    public static boolean disableAllFilters = false;
}
