package blade.addon.mixin;

import blade.addon.utils.config.values.Visual;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StuckArrowsFeatureRenderer.class)
public class StuckArrowsFeatureRendererMixin {

    @Inject(method = "getObjectCount", at=@At("TAIL"), cancellable = true)
    public void shouldRender(CallbackInfoReturnable<Integer> cir) {
        if (Visual.hideStuckArrows) {
            cir.setReturnValue(0);
        }

    }
}
