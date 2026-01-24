package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SectionProgress {

    private static int completed = 0;
    private static int sectionTotal = 7;

    public static void init() {

        Events.ON_TERMINAL.register((formattedName, action, objective, current, total) -> {
            completed = current;
            sectionTotal = total;
            return false;
        });

        Events.ON_SECTION_CHANGE.register(() -> {
            if (completed == sectionTotal) completed = 0;
            sectionTotal = getTotal();
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            completed = 0;
            sectionTotal = 7;
            return false;
        });

    }

    private static int getTotal() {
        return Section.getSection() != 2? 7: 8;
    }

    public static boolean display() {
        return Floor7.showSectionProgress && Phase.inTerminals();
    }

    public static void render(HUDComponent component, DrawContext context) {
        RenderUtils.drawCenteredText(context, component, Text.literal("§a(§c" + completed + "§a/" + sectionTotal + ")"));
    }

}
