package blade.addon.utils.config.components;

import blade.addon.features.dungeon.DupeClassChecker;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CombinedScreenNotifications {

    public static boolean display() {
        if (!Dungeons.combineScreenNotifications) return false;

        if (DupeClassChecker.display()) {
            return true;
        } else if (KeyNotifier.display()) {
            return true;
        }
        return false;
    }

    public static void render(HUDComponent component, DrawContext context) {
        if (DupeClassChecker.display()) {
            DupeClassChecker.render(component, context);
        } else if (KeyNotifier.display()) {
            KeyNotifier.render(component, context);
        } else {
            int x = component.getScaledX();
            int y = component.getScaledY();

            RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, "Some notification", x, y, component.getWidth());
        }

    }
}
