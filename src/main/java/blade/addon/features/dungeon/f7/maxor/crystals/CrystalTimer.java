package blade.addon.features.dungeon.f7.maxor.crystals;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableTickTimer;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDCategory;

import java.util.List;

public class CrystalTimer extends CombineableTickTimer {

    public CrystalTimer() {
        super("Crystal spawn timer");
    }

    @Override
    public boolean enabled() {
        return Floor7.enableCrystalSpawnTime;
    }

    @Override
    public int getColor() {
        return Constants.LIGHT_PURPLE;
    }

    @Override
    public double getTime() {
        return CrystalSpawn.getTick() * Constants.TICK_DURATION;
    }

    @Override
    public boolean shouldRender() {
        return getTime() > 0 && Location.inDungeon() && Phase.inP1() && Floor7.enableCrystalSpawnTime;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P1);
    }
}
