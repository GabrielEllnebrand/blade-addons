package blade.addon.mixin;

import blade.addon.features.item.ProtectItem;
import blade.addon.utils.config.values.Dungeons;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "dropSelectedItem", at=@At("HEAD"), cancellable = true)
    public void protectItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (Dungeons.dontProtectHeldItem && ProtectItem.isInADungeon()) return;
        if (ProtectItem.protect(this.getInventory().getSelectedStack())) {
            cir.setReturnValue(false);
        }
    }
}
