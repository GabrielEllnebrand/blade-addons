package blade.addon.features.dungeon.f7.maxor;

import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MaxorTickTimer extends HUDComponent {

    private int tick = 0;

    public MaxorTickTimer() {
        super("Maxor tick timer");
    }

    public void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inP1()) tick++;
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            tick = 0;
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
        return Floor7.enableMaxorTickTimer;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.enableMaxorTickTimer && Location.inDungeon() && Phase.inP1();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P1);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawTimer(this, guiGraphicsExtractor, tick, 0xffffffff);
    }
}
