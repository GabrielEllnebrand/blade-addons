package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CurrentSection {

    private static int section = 1;

    public static void init() {
        Events.ON_SECTION_CHANGE.register(() -> {
            section++;
            return false;
        });
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            section = 1;
            return false;
        });
    }

    public static boolean display() {
        return Floor7.showCurrentSection && Phase.inTerminals();
    }

    public static void render(HUDComponent component, GuiGraphicsExtractor graphics) {
        RenderUtils.drawPrefixedText(component, graphics, "Section", " " + section);
    }

}
