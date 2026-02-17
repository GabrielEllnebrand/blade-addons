package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class BloodNotifier {

    private static boolean detected = false;
    private static long prevTime = 0;

    public static void init() {

        Events.ON_GAME_MESSAGE.register(message -> {
            if (detected || !Location.inDungeon()) return false;


            if (message.getString().equals("[BOSS] The Watcher: Let's see how you can handle this.")) {
                detected = true;
                prevTime = System.currentTimeMillis();

                if (correctClass()) {
                    Misc.sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 0);
                }
            }

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            reset();
            return false;
        });
    }

    private static void reset() {
        detected = false;
    }

    private static boolean correctClass() {
        return Dungeons.alertBloodForAllClasses || DungeonClass.isClass(DungeonClass.MAGE);
    }

    public static boolean display() {
        return Dungeons.alertBloodSpawns && System.currentTimeMillis() - prevTime < 2000 && correctClass();
    }

    public static void render(HUDComponent component, DrawContext context) {
        RenderUtils.drawCenteredText(context, component, Text.literal("First four mobs spawned!"), Constants.RED);
    }

}
