package blade.addon.features.dungeon.f7.storm;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableTickTimer;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDCategory;

import java.util.List;

public class StormTickTimer extends CombineableTickTimer {

    private static final int COUNTDOWN_DURATION = 5 * 20;
    private static final int CRUSH_TICK = 31 * 20;

    public StormTickTimer() {
        super("Storm tick timer");
    }

    @Override
    public boolean enabled() {
        return Floor7.enableStormTickTimer;
    }

    @Override
    public int getColor() {
        return Floor7.stormTickTimerColor;
    }

    @Override
    public double getTime() {
        double num = StormTime.getTick() * Constants.TICK_DURATION;
        if (Floor7.tickDownStormTickTimer) {
            num = CRUSH_TICK * Constants.TICK_DURATION - num;
        }
        return num;
    }

    @Override
    public boolean shouldRender() {
        if (Floor7.tickDownStormTickTimer) {
            double diff = CRUSH_TICK - StormTime.getTick();
            if (diff > COUNTDOWN_DURATION || diff < 0) return false;
        }

        return Floor7.enableStormTickTimer && Location.inDungeon() && Phase.inP2() && !Phase.stormDead();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P2);
    }
}
