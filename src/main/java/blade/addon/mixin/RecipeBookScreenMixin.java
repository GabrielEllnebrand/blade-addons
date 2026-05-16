package blade.addon.mixin;

import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractRecipeBookScreen.class)
public class RecipeBookScreenMixin {
    @Inject(method = "initButton", at=@At("HEAD"), cancellable = true)
    private void addRecipeBook(CallbackInfo ci) {
        if (ExtraOptions.disableRecipeBook) {
            ci.cancel();
        }
    }
}
