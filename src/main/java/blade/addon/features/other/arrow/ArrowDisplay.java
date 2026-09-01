package blade.addon.features.other.arrow;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ArrowDisplay extends HUDComponent {

    public ArrowDisplay() {
        super("Selected arrow display");
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return ExtraOptions.displayCurrentArrow;
    }

    @Override
    public boolean shouldRender() {
        return ExtraOptions.displayCurrentArrow;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawText(guiGraphicsExtractor, this, ArrowSwapper.displayedText, 0xffffffff);
    }
}
