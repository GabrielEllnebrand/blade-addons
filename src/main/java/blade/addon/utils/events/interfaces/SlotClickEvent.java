package blade.addon.utils.events.interfaces;

import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public interface SlotClickEvent {
    boolean onSlot(Slot slot, int button, ClickType clickType);
}
