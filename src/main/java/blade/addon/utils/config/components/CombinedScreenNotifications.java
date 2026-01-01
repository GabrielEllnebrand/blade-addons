package blade.addon.utils.config.components;

import blade.addon.features.dungeon.RunStartValidator;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.f7.terms.MelodyWarning;
import blade.addon.features.dungeon.f7.PillarExplode;
import blade.addon.features.dungeon.f7.terms.Pre4Notifier;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CombinedScreenNotifications {

    public static boolean display() {
        if (!Dungeons.combineScreenNotifications) return false;

        if (RunStartValidator.display()) {
            return true;
        } else if (KeyNotifier.display()) {
            return true;
        } else if (Pre4Notifier.display()) {
            return true;
        } else if (MelodyWarning.display()) {
            return true;
        } else if (PillarExplode.display()) {
            return true;
        }
        return false;
    }

    public static void render(HUDComponent component, DrawContext context) {
        if (RunStartValidator.display()) {
            RunStartValidator.render(component, context);
        } else if (KeyNotifier.display()) {
            KeyNotifier.render(component, context);
        } else if (Pre4Notifier.display()) {
            Pre4Notifier.render(component, context);
        } else if (MelodyWarning.display()) {
            MelodyWarning.render(component, context);
        }else if (PillarExplode.display()) {
            PillarExplode.render(component, context);
        }
        else {
            int x = component.getScaledX();
            int y = component.getScaledY();

            RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, "Some notification", x, y, component.getWidth());
        }

    }
}
