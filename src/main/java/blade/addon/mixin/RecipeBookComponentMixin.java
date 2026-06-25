package blade.addon.mixin;

import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (ExtraOptions.disableRecipeBook) {
            ci.cancel();
        }
    }

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    public void render(CallbackInfoReturnable<Boolean> cir) {
        if (ExtraOptions.disableRecipeBook) {
            cir.setReturnValue(false);
        }

    }

    @Inject(method = "setVisible", at = @At("HEAD"), cancellable = true)
    public void render(boolean opened, CallbackInfo ci) {
        if (ExtraOptions.disableRecipeBook) {
            ci.cancel();
        }
    }
}
