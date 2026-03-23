package blade.addon.mixin;

import blade.addon.features.other.SwingAnimation;
import blade.addon.utils.data.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("TAIL"))
    public void swingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        if (EntityUtil.isClientPlayer((Entity) (Object) this)) {
            SwingAnimation.consumeSwing();
        }
    }
}
