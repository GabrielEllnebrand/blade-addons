package blade.addon.features.item;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DropAnimation {

    private static final int FIX_IN_TICKS = 20;

    private static int scheduledFix = 0;
    private static ItemStack dropped;
    private static int selectedSlot;
    private static boolean cleared = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (scheduledFix == 0) {
                clearData();
            } else if (dropped != null){
                scheduledFix--;
            }
        });
    }

    public static void clearData() {
        if (cleared) return;
        setDroppedData(null, -1);
        cleared = true;
    }

    public static void setDroppedData(ItemStack itemStack, int slot) {
        if (itemStack == null) {
            dropped = null;
            selectedSlot = -1;
            return;
        }
        if (itemStack.getItem() == Items.AIR) return;
        dropped = itemStack;
        selectedSlot = slot;
        scheduledFix = FIX_IN_TICKS;
        cleared = false;
    }

    public static ItemStack getPrevDroppedItem() {
        return dropped;
    }

    public static int getSelectedSlot() {
        return selectedSlot;
    }

    public static boolean shouldProceed() {
        return Dungeons.disableDropAnimation && Location.inDungeon();
    }
}
