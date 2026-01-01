package blade.addon.utils.events.interfaces;

import net.minecraft.item.ItemStack;

public interface SlotChangeEvent {
    boolean onSlotChange(int slot, ItemStack item);
}
