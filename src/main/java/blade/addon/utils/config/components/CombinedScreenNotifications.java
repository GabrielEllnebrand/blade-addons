package blade.addon.utils.config.components;

import blade.addon.features.dungeon.BloodNotifier;
import blade.addon.features.dungeon.KeyNotifier;
import blade.addon.features.dungeon.RunStartValidator;
import blade.addon.features.dungeon.f7.maxor.CrystalSpawn;
import blade.addon.features.dungeon.f7.storm.PillarExplode;
import blade.addon.features.dungeon.f7.terms.DeviceNotifier;
import blade.addon.features.dungeon.f7.terms.MelodyWarning;
import blade.addon.features.dungeon.f7.terms.SectionCompletion;
import blade.addon.features.notifications.Notifications;
import blade.addon.features.other.ArrowSwapper;
import blade.addon.features.other.SelectedPet;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class CombinedScreenNotifications {

    public static boolean display() {
        if (!Dungeons.combineScreenNotifications) return false;

        if (RunStartValidator.display()) {
            return true;
        } else if (KeyNotifier.display()) {
            return true;
        } else if (DeviceNotifier.display()) {
            return true;
        } else if (MelodyWarning.display()) {
            return true;
        } else if (PillarExplode.display()) {
            return true;
        }else if (CrystalSpawn.displayNotification()) {
            return true;
        } else if (Notifications.display()) {
            return true;
        } else if (SelectedPet.displayNotification()) {
            return true;
        } else if (ArrowSwapper.displayNotification()) {
            return true;
        } else if (SectionCompletion.display()) {
            return true;
        } else if (BloodNotifier.display()) {
            return true;
        }
        return false;
    }

    public static void render(HUDComponent component, GuiGraphicsExtractor graphics) {
        if (RunStartValidator.display()) {
            RunStartValidator.render(component, graphics);
        } else if (KeyNotifier.display()) {
            KeyNotifier.render(component, graphics);
        } else if (DeviceNotifier.display()) {
            DeviceNotifier.render(component, graphics);
        } else if (MelodyWarning.display()) {
            MelodyWarning.render(component, graphics);
        } else if (PillarExplode.display()) {
            PillarExplode.render(component, graphics);
        } else if (CrystalSpawn.displayNotification()) {
            CrystalSpawn.renderNotification(component, graphics);
        }else if (Notifications.display()) {
            Notifications.render(component, graphics);
        } else if (SelectedPet.displayNotification()) {
            SelectedPet.renderNotification(component, graphics);
        }   else if (ArrowSwapper.displayNotification()) {
            ArrowSwapper.renderNotification(component, graphics);
        }   else if (SectionCompletion.display()) {
            SectionCompletion.render(component, graphics);
        }else if (BloodNotifier.display()) {
            BloodNotifier.render(component, graphics);
        }else {
            RenderUtils.drawCenteredText(graphics, component, Component.literal("Some notification"));
        }

    }
}
