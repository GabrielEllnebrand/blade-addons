package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleHider {

    private static final Pattern TERMINALS_DONE_PATTERN = Pattern.compile("^(\\w+) (activated|completed) a (terminal|device|lever)! \\((\\d)/(\\d)\\)$");

    public static void processSubtitle(SubtitleS2CPacket packet) {
        if (!shouldHideTitle()) return;
        if (Section.inSection(1) && Pre4Notifier.atDev()) return;
        String string = packet.text().getString().replaceAll("§.", "");


        Matcher matcher = TERMINALS_DONE_PATTERN.matcher(string);
        if (matcher.find()) {
            String name = matcher.group(1);
            if (Misc.isClientPlayer(name)) {
                Misc.forceTitle(Text.empty(), packet.text());
            }
            //end of section msg is handled by the Section class currently
        } else {
            Misc.forceTitle(Text.empty(), packet.text());
        }
    }

    public static boolean shouldHideTitle() {
        return Floor7.hideTerminalTitles && Phase.inP3() && Location.inDungeon();
    }

}
