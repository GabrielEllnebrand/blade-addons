package blade.addon.features.item;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class HeldItemToolTip extends HUDComponent {

    private Component tooltip = Component.literal("Some toolTip");
    private int color = 0xffffffff;
    private int heldItemFade = 0;

    public HeldItemToolTip() {
        super("Tool tip");
    }

    public void setText(Component newToolTip) {
        tooltip = newToolTip;
    }

    public void setColor(int newColor) {
        color = ARGB.color(newColor, -1);
    }

    public void setFade(int fade) {
        heldItemFade = fade;
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
        return ExtraOptions.moveToolTip;
    }

    @Override
    public boolean shouldRender() {
        return ExtraOptions.moveToolTip && heldItemFade > 0;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int currentColor = heldItemFade > 0? color: 0xffffffff;
        RenderUtils.drawCenteredText(guiGraphicsExtractor, this, tooltip, currentColor);
    }
}
