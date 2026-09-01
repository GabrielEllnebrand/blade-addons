package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public class BloodNotification extends CombineableNotification {

    private boolean detected = false;
    private long prevTime = 0;

    public BloodNotification() {
        super("blood notification");
    }

    public void init() {

        Events.ON_GAME_MESSAGE.register(message -> {
            if (detected || !Location.inDungeon()) return false;


            if (message.getString().equals("[BOSS] The Watcher: Let's see how you can handle this.")) {
                detected = true;
                prevTime = System.currentTimeMillis();

                if (correctClass()) {
                    Misc.sendSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 2, 0);
                }
            }

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            resetDetection();
            return false;
        });
    }

    @Override
    public boolean shouldRender() {
        return Dungeons.alertBloodSpawns && System.currentTimeMillis() - prevTime < 2000 && correctClass();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of();
    }

    private void resetDetection() {
        detected = false;
    }

    private boolean correctClass() {
        return Dungeons.alertBloodForAllClasses || DungeonClass.isClass(DungeonClass.MAGE);
    }

    @Override
    public boolean enabled() {
        return Dungeons.alertBloodSpawns;
    }

    @Override
    public Component getText() {
        return Component.literal("First four mobs spawned!");
    }
}
