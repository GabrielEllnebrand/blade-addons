package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Inject(method = "doItemUse", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getCount()I"), cancellable = true)
    private void testInteraction(CallbackInfo ci, @Local ItemStack itemStack) {
        BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
        if (blockHitResult == null) return;
        if (Events.ON_BLOCK_INTERACTION.invoke(blockInteractionEvent -> blockInteractionEvent.iteract(blockHitResult, itemStack))) {
            ci.cancel();
        }
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private void onHit(CallbackInfoReturnable<Boolean> cir) {
        DropAnimation.clearData();
    }

    @Inject(method = "setWorld", at=@At(value = "TAIL"))
    private void onWorld(ClientWorld world, CallbackInfo ci) {
        Location.swapWorld();
    }
}
