package blade.addon.utils.config.values;

import config.practical.data.SoundData;
import config.practical.manager.ConfigValue;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class ExtraOptions {
    @ConfigValue
    public static boolean hideFireInf5 = false;

    @ConfigValue
    public static boolean hideStuckArrows = false;

    @ConfigValue
    public static boolean disableAbilityCooldownSound = true;

    @ConfigValue
    public static boolean hideDeadEntities = false;

    @ConfigValue
    public static boolean itemRarityBackground = false;

    @ConfigValue
    public static boolean hideStatusOverLay = false;

    @ConfigValue
    public static boolean disableGlowing = false;

    @ConfigValue
    public static String textPrefix = "";

    @ConfigValue
    public static boolean highlightSelectedPet = false;

    @ConfigValue
    public static boolean drawPetHUD = false;

    @ConfigValue
    public static boolean includePetSprite = true;

    @ConfigValue
    public static boolean drawStarCount = false;

    @ConfigValue
    public static boolean highlightProtectedItem = false;

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
    public static boolean compactHoppityMsgs = false;

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
}
