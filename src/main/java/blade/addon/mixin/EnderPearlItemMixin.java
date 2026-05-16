package blade.addon.mixin;

import blade.addon.features.other.SwingAnimation;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.data.EntityUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderpearlItem.class)
public class EnderPearlItemMixin {

    @Inject(method = "use", at=@At("HEAD"))
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (Visual.stopPearlSwing && EntityUtil.isClientPlayer(user)) {
            SwingAnimation.ignoreNext(2);
        }
    }
}
