package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class QuizTimer extends HUDComponent {

    private static final int START_DURATION = 220;
    private static final int QUESTION_DURATION = 100;

    private int tick = 0;
    private int stage = 0;

    public QuizTimer() {
        super("Quiz timer");
    }

    public void init() {

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon()) return false;

            String string = text.getString();
            if (string.equals("[STATUE] Oruo the Omniscient: I am Oruo the Omniscient. I have lived many lives. I have learned all there is to know.")) {
                tick = START_DURATION;
                stage = 1;
            } else if (string.equals("[STATUE] Oruo the Omniscient: 2 questions left... Then you will have proven your worth to me!")) {
                tick = QUESTION_DURATION;
                stage = 2;
            } else if (string.equals("[STATUE] Oruo the Omniscient: One more question!")) {
                tick = QUESTION_DURATION;
                stage = 3;
            }

            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            tick = 0;
            stage = 0;
            return false;
        });
    }

    @Override
    public int getWidth() {
        return 80;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Dungeons.quizTimer;
    }

    @Override
    public boolean shouldRender() {
        return Dungeons.quizTimer && tick > 0;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.CLEAR);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;

        if (Dungeons.quizProgress) {
            Component drawnText = Component.literal("Quiz " + "(").withColor(ExtraOptions.timerPrefixColor)
                    .append(Component.literal(stage + "/3").withColor(0xffffffff)
                            .append(Component.literal("): ").withColor(ExtraOptions.timerPrefixColor))
                            .append(Component.literal(Constants.DECIMAL_FORMAT.format(tick * Constants.TICK_DURATION) + "s").withColor(0xffffffff)));
            guiGraphicsExtractor.text(textRenderer, drawnText, getScaledX(), getScaledY(), 0xffffffff, true);
        } else {
            RenderUtils.drawPrefixedTimer(this, guiGraphicsExtractor, "Quiz", tick);
        }
    }


}
