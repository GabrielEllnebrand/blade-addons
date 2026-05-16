package blade.addon.mixin;

import blade.addon.features.other.SwingAnimation;
import blade.addon.utils.data.EntityUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("TAIL"))
    public void swingHand(InteractionHand hand, boolean fromServerPlayer, CallbackInfo ci) {
        if (EntityUtil.isClientPlayer((Entity) (Object) this)) {
            SwingAnimation.consumeSwing();
        }
    }
}
