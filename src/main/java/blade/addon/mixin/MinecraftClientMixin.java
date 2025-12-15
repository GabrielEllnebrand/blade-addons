package blade.addon.mixin;

import blade.addon.utils.events.Events;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Inject(method = "doItemUse", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getCount()I"), cancellable = true)
    private void testInteraction(CallbackInfo ci, @Local ItemStack itemStack) {
        BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;

        if (blockHitResult == null) return;
        if (Events.ON_BLOCK_INTERACTION.test(blockInteractionEvent -> blockInteractionEvent.iteract(blockHitResult, itemStack))) {
            ci.cancel();
        }


    }
}
