package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import blade.addon.utils.events.Events;
import blade.addon.utils.events.interfaces.WorldEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public HitResult hitResult;

    @Inject(method = "startUseItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
    private void testInteractionBlock(CallbackInfo ci, @Local ItemStack itemStack) {
        BlockHitResult blockHitResult = (BlockHitResult) this.hitResult;
        if (blockHitResult == null) return;
        if (Events.ON_BLOCK_INTERACTION.invoke(blockInteractionEvent -> blockInteractionEvent.interact(blockHitResult, itemStack))) {
            ci.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"))
    private void onHit(CallbackInfoReturnable<Boolean> cir) {
        DropAnimation.clearData();
    }

    @Inject(method = "updateLevelInEngines", at = @At(value = "TAIL"))
    private void onWorld(ClientLevel world, CallbackInfo ci) {
        Events.ON_WORLD_CHANGE.invoke(WorldEvent::onWorldSwap);
    }
}
