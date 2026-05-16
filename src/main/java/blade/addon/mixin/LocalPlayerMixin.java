package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import blade.addon.features.item.ProtectItem;
import blade.addon.utils.config.values.Dungeons;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "drop(Z)Z", at = @At("HEAD"), cancellable = true)
    public void protectItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (Dungeons.dontProtectHeldItem && ProtectItem.isInADungeon()) return;

        LocalPlayer self = (LocalPlayer) (Object) this;

        if (ProtectItem.protect(self.getInventory().getSelectedItem())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "drop(Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    public void getDropped(boolean entireStack, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
        if (!DropAnimation.shouldProceed()) return;

        LocalPlayer self = (LocalPlayer) (Object) this;
        int slot = self.getInventory().getSelectedSlot();
        DropAnimation.setDroppedData(itemStack, slot);
    }
}
