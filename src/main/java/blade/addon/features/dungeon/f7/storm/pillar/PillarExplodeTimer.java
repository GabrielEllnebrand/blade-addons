package blade.addon.features.dungeon.f7.storm.pillar;

import blade.addon.utils.Constants;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PillarExplodeTimer extends HUDComponent {

    public PillarExplodeTimer() {
        super("Pillar explode timer");
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
        return Floor7.timePillarExplosion;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.timePillarExplosion && PillarExplode.tick > 0;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P2);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int color = PillarExplode.tick < 6 ? Constants.GREEN : Constants.RED;
        RenderUtils.drawTimer(this, guiGraphicsExtractor, PillarExplode.tick, color);
    }
}
