package blade.addon.utils.config.components;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public abstract class CombineableNotification extends HUDComponent {

    public CombineableNotification(String info) {
        super(info);
    }

    @Override
    public int getWidth() {
        return 130;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return enabled() && !Dungeons.combineScreenNotifications;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        draw(guiGraphicsExtractor, getScaledX(), getScaledY());
    }

    public abstract boolean enabled();

    public abstract Component getText();

    public void draw(GuiGraphicsExtractor graphicsExtractor, int x, int y) {
        Font textRenderer = Minecraft.getInstance().font;
        if (textRenderer == null) return;
        RenderUtils.drawCenteredText(graphicsExtractor, textRenderer, getText(), x, y, getWidth(), 0xffffffff);
    }
}
