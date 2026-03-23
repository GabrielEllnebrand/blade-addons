package blade.addon.mixin;

import blade.addon.features.other.SwingAnimation;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.data.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderPearlItem.class)
public class EnderPearlItemMixin {

    @Inject(method = "use", at=@At("HEAD"))
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<Boolean> cir) {
        if (Visual.stopPearlSwing && EntityUtil.isClientPlayer(user)) {
            SwingAnimation.ignoreNext(2);
        }
    }
}
