package blade.addon.utils.config.components;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public abstract class CombineableTickTimer extends HUDComponent {


    public CombineableTickTimer(String info) {
        super(info);
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
        return enabled() && !Floor7.combineTickTimers;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        draw(guiGraphicsExtractor, getScaledX(), getScaledY());
    }

    public abstract boolean enabled();

    public abstract int getColor();

    public abstract double getTime();

    public void draw(GuiGraphicsExtractor graphicsExtractor, int x, int y) {
        RenderUtils.drawCenteredText(graphicsExtractor, Minecraft.getInstance().font, Component.literal(Constants.DECIMAL_FORMAT.format(getTime())), x, y, 30, getColor());
    }
}
