package blade.addon.utils.config.components;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CombinedTickTimer extends HUDComponent {

    private final CombineableTickTimer[] timers;

    public CombinedTickTimer(CombineableTickTimer... timers) {
        super("Combined tick timer");
        this.timers = timers;
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
        return Floor7.combineTickTimers;
    }

    @Override
    public boolean shouldRender() {
        for (CombineableTickTimer timer: timers) {
            if (timer.shouldRender()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<HUDCategory> categories() {
        List<HUDCategory> categories = new ArrayList<>();

        for (CombineableTickTimer timer: timers) {
            if (timer.enabled()) {
                categories.addAll(timer.categories());
            }
        }

        return categories;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        for (CombineableTickTimer timer: timers) {
            if (timer.shouldRender()) {
                timer.draw(guiGraphicsExtractor, getScaledX(), getScaledY());
                return;
            }
        }
    }

    @Override
    public void renderEditTemplate(@NonNull GuiGraphicsExtractor graphics) {
        RenderUtils.drawCenteredText(graphics, Minecraft.getInstance().font, Component.literal(Constants.DECIMAL_FORMAT.format(0.0)), getScaledX(), getScaledY(), 30, 0xffffffff);

    }
}
