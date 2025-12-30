package blade.addon.features.other;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompactHoppity {

    private static final Pattern NAME_PATTERN = Pattern.compile("^§D§LHOPPITY'S HUNT §7You found (.+) §.\\(.+\\)!$");
    private static final Pattern INFO_PATTERN = Pattern.compile("^(§.§.)(DUPLICATE|NEW) RABBIT! (.+)");
    private static String formattedName = "";

    public static void init() {

        Events.ON_CANCELABLE_GAME_MESSAGE.register(text -> {
            if (!ExtraOptions.compactHoppityMsgs) return false;

            String string = text.getString();
            if (string.equals("HOPPITY'S HUNT You found a Hitman Egg!")) {
                return true;
            }

            Matcher matcher = NAME_PATTERN.matcher(string);
            if (matcher.find()) {
                formattedName = matcher.group(1);
                return true;
            }

            matcher = INFO_PATTERN.matcher(string);
            if (matcher.find()) {
                String styleInfo = matcher.group(1);
                String statusInfo = matcher.group(2);
                String chocolateAmount = matcher.group(3);
                Misc.addChatMessage(Text.literal(styleInfo + statusInfo + " " + formattedName + " " + chocolateAmount));
                formattedName = "";
                return true;
            }

            return false;

        });

    }

}
