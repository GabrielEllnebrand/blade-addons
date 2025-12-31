package blade.addon.mixin;

import blade.addon.utils.events.Events;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "setStack", at = @At("HEAD"))
    private void setStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (Events.ON_SLOT_CHANGE.hasListeners()) {
            Events.ON_SLOT_CHANGE.listeners.forEach(slotChangeEvent -> slotChangeEvent.onSlotChange(slot, stack));
        }
    }
}
