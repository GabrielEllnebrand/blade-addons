package blade.addon.features.other.loadout;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class LoadoutDisplay extends HUDComponent {

    public LoadoutDisplay() {
        super("Loadout display");
    }

    @Override
    public int getWidth() {
        return 110;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return ExtraOptions.enableLoadoutDisplay;
    }

    @Override
    public boolean shouldRender() {
        return ExtraOptions.enableLoadoutDisplay;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        RenderUtils.drawPrefixedText(this, guiGraphicsExtractor, "Loadout", LoadoutData.getCurrentLoadout());
    }
}
