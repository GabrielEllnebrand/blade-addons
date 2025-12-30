package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;

public class TitleHider {

    public static boolean shouldHideTitle() {
        return Floor7.hideTerminalTitles && Phase.inP3() && Location.inDungeon();
    }

}
