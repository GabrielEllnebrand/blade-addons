package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;


public class StormLBTimer extends HUDComponent {

    private static final int BASE_LB_TICK = 20 * 34;
    private static final int DISPLAY_DURATION_TICK = 20 * 5;

    private int tick = 0;

    public StormLBTimer() {
        super("Storm lb timer");
    }

    public void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP2() && !Phase.stormDead()) tick++;
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
    public int getWidth() {
        return 30;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Floor7.showLBTimer;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.showLBTimer && Location.inDungeon() && Phase.inP2() && !Phase.stormDead() && validTime() && DungeonClass.isClass(DungeonClass.ARCHER);
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P2);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int deltaTick = getCurrentTickTime();
        double num = deltaTick * Constants.TICK_DURATION;
        RenderUtils.drawTimer(this, guiGraphicsExtractor, num, RenderUtils.getStatusColor(60, 30, deltaTick));
    }

    private int getCurrentTickTime() {
        return BASE_LB_TICK + Floor7.lbTickOffset - tick;
    }

    private boolean validTime() {
        int deltaTick = getCurrentTickTime();
        return deltaTick > 0 && deltaTick <= DISPLAY_DURATION_TICK;

    }
}
