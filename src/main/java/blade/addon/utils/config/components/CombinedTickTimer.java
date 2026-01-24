package blade.addon.utils.config.components;

import blade.addon.features.dungeon.f7.CrystalSpawn;
import blade.addon.features.dungeon.f7.RelicTimer;
import blade.addon.features.dungeon.f7.StormTickTimer;
import blade.addon.features.dungeon.f7.terms.GoldorTickTimer;
import blade.addon.features.dungeon.f7.terms.TermStartTimer;
import blade.addon.utils.Constants;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class CombinedTickTimer {

    public static boolean display() {
        if (!Floor7.combineTickTimers) return false;

        if (CrystalSpawn.display()) {
            return true;
        } else if (StormTickTimer.display()) {
            return true;
        } else if (TermStartTimer.display()) {
            return true;
        } else if (GoldorTickTimer.display()) {
            return true;
        } else if (RelicTimer.display()) {
            return true;
        }
        return false;
    }

    public static void render(HUDComponent component, DrawContext context) {
        if (CrystalSpawn.display()) {
            CrystalSpawn.render(component, context);
        } else if (StormTickTimer.display()) {
            StormTickTimer.render(component, context);
        } else if (TermStartTimer.display()) {
          TermStartTimer.render(component, context);
        } else if (GoldorTickTimer.display()) {
            GoldorTickTimer.render(component, context);
        } else if (RelicTimer.display()) {
            RelicTimer.render(component, context);
        } else {
            RenderUtils.drawCenteredText(context, component, Text.literal(Constants.DECIMAL_FORMAT.format(0.0)));
        }

    }
}
