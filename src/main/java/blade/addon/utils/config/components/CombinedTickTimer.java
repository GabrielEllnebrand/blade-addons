package blade.addon.utils.config.components;

import blade.addon.features.dungeon.f7.RelicTimer;
import blade.addon.features.dungeon.f7.maxor.CrystalSpawn;
import blade.addon.features.dungeon.f7.storm.StormTickTimer;
import blade.addon.features.dungeon.f7.terms.GoldorTickTimer;
import blade.addon.features.dungeon.f7.terms.TermStartTimer;
import blade.addon.utils.Constants;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

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

    public static void render(HUDComponent component, GuiGraphicsExtractor graphics) {
        if (CrystalSpawn.display()) {
            CrystalSpawn.render(component, graphics);
        } else if (StormTickTimer.display()) {
            StormTickTimer.render(component, graphics);
        } else if (TermStartTimer.display()) {
          TermStartTimer.render(component, graphics);
        } else if (GoldorTickTimer.display()) {
            GoldorTickTimer.render(component, graphics);
        } else if (RelicTimer.display()) {
            RelicTimer.render(component, graphics);
        } else {
            RenderUtils.drawCenteredText(graphics, component, Component.literal(Constants.DECIMAL_FORMAT.format(0.0)));
        }

    }
}
