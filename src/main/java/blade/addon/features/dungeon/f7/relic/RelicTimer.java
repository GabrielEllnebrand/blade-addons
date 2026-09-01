package blade.addon.features.dungeon.f7.relic;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableTickTimer;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDCategory;

import java.util.List;

public class RelicTimer extends CombineableTickTimer {

    private static final int GREEN_COLOR = 0xff00ff00;
    private static final int RED_COLOR = 0xffff0000;

    public RelicTimer() {
        super("Relic Spawn Timer");
    }

    @Override
    public boolean enabled() {
        return Floor7.enableRelicStartTimer && !Floor7.replaceWithProgressBar;
    }

    @Override
    public int getColor() {
        return RelicSpawn.getTick() > 7 ? GREEN_COLOR : RED_COLOR;
    }

    @Override
    public double getTime() {
        return RelicSpawn.getTick() * Constants.TICK_DURATION;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.enableRelicStartTimer && Location.inDungeon() && Phase.inP5() && RelicSpawn.getTick() > -1 && !Floor7.replaceWithProgressBar;

    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P5);
    }
}
