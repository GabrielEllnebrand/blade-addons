package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.TextUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SectionProgress extends HUDComponent {

    private int completed = 0;
    private int sectionTotal = 7;
    private String prevObjective = "";

    private String completedFormat = "";
    private String objectiveFormat = "";

    public SectionProgress() {
        super("Section progress");
    }

    public void init() {

        prevObjective = "";
        completedFormat = "";
        objectiveFormat = "";

        updateObjectiveFormat();
        updateProgressFormat();

        Events.ON_GAME_MESSAGE.register((message) -> {
            if (!Phase.inTerminals() || !Floor7.sectionPrevObjective) return false;

            String string = message.getString();
            if (string.equals("The gate has been destroyed!")) {
                prevObjective = "Gate Destroyed";
                updateObjectiveFormat();
            } else if (string.equals("The gate will open in 5 seconds!")) {
                prevObjective = "Break Gate";
                updateObjectiveFormat();
            }

            return false;
        });

        Events.ON_TERMINAL.register((_, _, objective, current, total) -> {
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

        Events.ON_LOCATION_CHANGE.register(_ -> {
            completed = 0;
            sectionTotal = 7;
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
        return Floor7.showSectionProgress;
    }

    @Override
    public boolean shouldRender() {
        return Floor7.showSectionProgress && Phase.inTerminals();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P3);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        if (Floor7.sectionPrevObjective) {
            RenderUtils.drawCenteredText(guiGraphicsExtractor, this, Component.literal(objectiveFormat + prevObjective + " ").append(getProgressText()));
        } else {
            RenderUtils.drawCenteredText(guiGraphicsExtractor, this, getProgressText());
        }
    }

    private void updateObjectiveFormat() {
        objectiveFormat = switch (prevObjective) {
            case "Lever", "Gate Destroyed" -> "§c";
            case "Device" -> "§d";
            case "Terminal" -> "§b";
            case "Break Gate" -> "§5§l";
            default -> "";
        };
    }

    private void updateProgressFormat() {
        if (completed >= sectionTotal) completedFormat = "§6§l";
        else if (sectionTotal - completed == 1 || (completed == 7 && sectionTotal == 8)) completedFormat = "§a";
        else if (completed >= 3) completedFormat = "§e";
        else completedFormat = "§c";
    }

    private int getTotal() {
        return Section.getSection() != 2 ? 7 : 8;
    }

    private Component getProgressText() {
        if (Floor7.sectionColorProgress) {
            return Component.literal("§f(" + completedFormat + completed + "§f/§a" + sectionTotal + "§f)");
        } else {
            return Component.literal("§a(§c" + completed + "§a/" + sectionTotal + ")");
        }
    }



}
