package blade.addon.features.dungeon.f7.terms;

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

public class CurrentSection extends HUDComponent {

    private int section = 1;

    public CurrentSection() {
        super("Current section");
    }

    public void init() {
        Events.ON_SECTION_CHANGE.register(() -> {
            section++;
            return false;
        });
        Events.ON_LOCATION_CHANGE.register(_ -> {
            section = 1;
            return false;
        });
    }

    @Override
    public int getWidth() {
        return 50;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Floor7.showCurrentSection;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.showCurrentSection && Phase.inTerminals();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P3);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawPrefixedText(this, guiGraphicsExtractor, "Section", " " + section);

    }
}
