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

public class GoldorTickTimer extends CombineableTickTimer {

    private int tick = 0;

    public GoldorTickTimer() {
        super("Goldor tick timer");
    }

    public void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inTerminals()) tick++;
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            if (Location.inDungeon()) {
                tick = 0;
            }
            return false;
        });
    }

    @Override
    public boolean shouldRender() {
        return Floor7.enableGoldorTickTimer && Location.inDungeon() && Phase.inTerminals();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P3);
    }

    @Override
    public boolean enabled() {
        return Floor7.enableGoldorTickTimer;
    }

    @Override
    public int getColor() {
        double mod = getTime() % 3;
        return  (mod < 1 ? Constants.GREEN : mod < 2 ? Constants.GOLD : Constants.RED);
    }

    @Override
    public double getTime() {
        double num = tick * Constants.TICK_DURATION;
        double mod = num % 3;
        if (Floor7.inDeathTicks && !Floor7.makeGoldorTickUp) mod = 3.0 - mod;
        if (Floor7.inDeathTicks) num = mod;
        return num;
    }
}
