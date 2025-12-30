package blade.addon.mixin;

import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (ExtraOptions.disableRecipeBook) {
            ci.cancel();
        }
    }

    @Inject(method = "isOpen", at = @At("HEAD"), cancellable = true)
    public void render(CallbackInfoReturnable<Boolean> cir) {
        if (ExtraOptions.disableRecipeBook) {
            cir.setReturnValue(false);
        }

    }

    @Inject(method = "setOpen", at = @At("HEAD"), cancellable = true)
    public void render(boolean opened, CallbackInfo ci) {
        if (ExtraOptions.disableRecipeBook) {
            ci.cancel();
        }
    }
}
