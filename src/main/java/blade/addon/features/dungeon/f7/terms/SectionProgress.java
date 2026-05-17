package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.TextUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SectionProgress {

    private static int completed = 0;
    private static int sectionTotal = 7;
    private static String prevObjective = "";

    private static String completedFormat = "";
    private static String objectiveFormat = "";

    public static void init() {

        updateObjectiveFormat();
        updateProgressFormat();

        Events.ON_GAME_MESSAGE.register((message) -> {
            if (!Phase.inTerminals() || !Floor7.sectionPrevObjective) return false;

            String string = message.getString();
            if (string.equals("The gate has been destroyed!")) {
                prevObjective = "Gate Destroyed";
                updateObjectiveFormat();
            }

            else if (string.equals("The gate will open in 5 seconds!")) {
                prevObjective = "Break Gate";
                updateObjectiveFormat();
            }

            return false;
        });

        Events.ON_TERMINAL.register((formattedName, action, objective, current, total) -> {
            completed = current;
            sectionTotal = total;
            prevObjective = TextUtil.capitaliseFirst(objective);
            updateObjectiveFormat();
            updateProgressFormat();
            return false;
        });

        Events.ON_SECTION_CHANGE.register(() -> {
            if (completed == sectionTotal) completed = 0;
            sectionTotal = getTotal();
            prevObjective = "";
            updateObjectiveFormat();
            updateProgressFormat();
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            completed = 0;
            sectionTotal = 7;
            return false;
        });

    }

    private static void updateObjectiveFormat() {
        objectiveFormat = switch (prevObjective) {
            case "Lever", "Gate Destroyed" -> "§c";
            case "Device" -> "§d";
            case "Terminal" -> "§b";
            case "Break Gate" -> "§5§l";
            default -> "";
        };
    }

    private static void updateProgressFormat() {
        if (completed >= sectionTotal) completedFormat = "§6§l";
        else if (sectionTotal - completed == 1 || (completed == 7 && sectionTotal == 8)) completedFormat = "§a";
        else if (completed >= 3) completedFormat = "§e";
        else completedFormat = "§c";
    }

    private static int getTotal() {
        return Section.getSection() != 2 ? 7 : 8;
    }

    private static Component getProgressText() {
        if (Floor7.sectionColorProgress) {
            return Component.literal("§f(" + completedFormat + completed + "§f/§a" + sectionTotal + "§f)");
        } else {
            return Component.literal("§a(§c" + completed + "§a/" + sectionTotal + ")");
        }
    }

    public static boolean display() {
        return Floor7.showSectionProgress && Phase.inTerminals();
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        if (Floor7.sectionPrevObjective) {
            RenderUtils.drawCenteredText(graphics, component, Component.literal(objectiveFormat + prevObjective + " ").append(getProgressText()));
        } else {
            RenderUtils.drawCenteredText(graphics, component, getProgressText());
        }

    }

}
