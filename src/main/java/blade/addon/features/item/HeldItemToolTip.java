package blade.addon.features.item;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class HeldItemToolTip {

    private static Component tooltip = Component.literal("Some toolTip");
    private static int color = 0xffffffff;
    private static int heldItemFade = 0;

    public static void setText(Component newToolTip) {
        tooltip = newToolTip;
    }

    public static void setColor(int newColor) {
        color = ARGB.color(newColor, -1);
    }

    public static void setFade(int fade) {
        heldItemFade = fade;
    }

    public static boolean display() {
        return ExtraOptions.moveToolTip && heldItemFade > 0;
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        int currentColor = heldItemFade > 0? color: 0xffffffff;
        RenderUtils.drawCenteredText(graphics, component, tooltip, currentColor);
    }

}
