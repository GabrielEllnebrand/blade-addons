package blade.addon.mixin;

import blade.addon.features.ExtraOptions;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StuckArrowsFeatureRenderer.class)
public class ArrowMixin {

    @Inject(method = "getObjectCount", at=@At("TAIL"), cancellable = true)
    public void shouldRender(CallbackInfoReturnable<Integer> cir) {
        if (ExtraOptions.hideStuckArrows) {
            cir.setReturnValue(0);
        }

    }
}
