package blade.addon.features.other;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;

public class BlockPetMenu {

    private static boolean inPetMenu = false;

    public static void init() {
        Events.ON_SCREEN.register(screen -> {
            if (screen == null) {
                inPetMenu = false;
                return false;
            }

            inPetMenu = screen.getTitle().getString().contains("Pets");
            return false;
        });

        Events.ON_SLOT_CLICKED.register((slot,slotId, button, containerInput) -> {
            if (inPetMenu && button == 1 && Dungeons.blockRemovingPet && Location.inDungeon()) {
                return slotId >= 9 && slotId <= 44;
            }
            return false;
        });
    }

}
