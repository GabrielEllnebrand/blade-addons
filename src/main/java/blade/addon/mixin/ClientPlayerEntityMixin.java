package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import blade.addon.features.item.ProtectItem;
import blade.addon.utils.config.values.Dungeons;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    public void protectItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (Dungeons.dontProtectHeldItem && ProtectItem.isInADungeon()) return;

        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;

        if (ProtectItem.protect(self.getInventory().getSelectedStack())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "dropSelectedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    public void getDropped(boolean entireStack, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
        if (!DropAnimation.shouldProceed()) return;

        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        int slot = self.getInventory().getSelectedSlot();
        DropAnimation.setDroppedData(itemStack, slot);
    }
}
