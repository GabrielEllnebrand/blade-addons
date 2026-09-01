package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableTickTimer;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDCategory;

import java.util.List;

public class TermStartTimer extends CombineableTickTimer {

    private static final int TOTAL_TICKS = 100;

    private int tick = TOTAL_TICKS;

    public TermStartTimer() {
        super("Term Start Timer");
    }

    public void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP2() && Phase.stormDead()) tick--;
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
           tick = TOTAL_TICKS;
            return false;
        });
    }

    @Override
    public boolean shouldRender() {
        return Floor7.enableTermStartTimer && Location.inDungeon() && Phase.inP2() && Phase.stormDead();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P2, Categories.P3);
    }

    @Override
    public boolean enabled() {
        return Floor7.enableTermStartTimer;
    }

    @Override
    public int getColor() {
        return  Constants.YELLOW;
    }

    @Override
    public double getTime() {
        return tick * Constants.TICK_DURATION;
    }
}
