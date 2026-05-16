package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.Phase;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;

public class TitleHider {

    private static final Pattern TERMINALS_DONE_PATTERN = Pattern.compile("^(\\w+) (activated|completed) a (terminal|device|lever)! \\((\\d)/(\\d)\\)$");

    public static boolean shouldHideTitle(Component title) {
        if (!(Floor7.hideTerminalTitles && Phase.inP3() && Location.inDungeon())) return false;
        String titleString = title.getString().replaceAll("§.", "");

        Matcher matcher = TERMINALS_DONE_PATTERN.matcher(titleString);
        if (matcher.find()) {
            String name = matcher.group(1);
            return !EntityUtil.isClientPlayer(name);
        } else {
            return false;
        }

    }

}
