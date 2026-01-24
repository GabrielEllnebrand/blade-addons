package blade.addon.features.item;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class HeldItemToolTip {

    private static Text tooltip = Text.literal("Some toolTip");
    private static int color = 0xffffffff;
    private static int heldItemFade = 0;

    public static void setText(Text newToolTip) {
        tooltip = newToolTip;
    }

    public static void setColor(int newColor) {
        color = ColorHelper.withAlpha(newColor, -1);
    }

    public static void setFade(int fade) {
        heldItemFade = fade;
    }

    public static boolean display() {
        return ExtraOptions.moveToolTip && heldItemFade > 0;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int currentColor = heldItemFade > 0? color: 0xffffffff;
        RenderUtils.drawCenteredText(context, component, tooltip, currentColor);
    }

}
