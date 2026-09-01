package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class DragonSpawnTimer extends HUDComponent {

    public DragonSpawnTimer() {
        super("Dragon spawn timer");
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
        return Floor7.dragSpawnTimers;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.dragSpawnTimers && DragonSpawn.currentDragon != Dragon.NONE;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P5);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawTimer(this, guiGraphicsExtractor, DragonSpawn.getTick(), DragonSpawn.currentDragon.color);
    }
}
