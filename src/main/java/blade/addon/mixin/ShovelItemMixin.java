package blade.addon.mixin;

import blade.addon.features.other.SwingAnimation;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.data.EntityUtil;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "useOnBlock", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (Visual.stopShovelFlattening && EntityUtil.isClientPlayer(context.getPlayer())) {
            SwingAnimation.ignoreNext(2);
        }
    }
}
