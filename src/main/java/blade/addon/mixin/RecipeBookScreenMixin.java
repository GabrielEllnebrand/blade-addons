package blade.addon.mixin;

import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookScreen.class)
public class RecipeBookScreenMixin {
    @Inject(method = "addRecipeBook", at=@At("HEAD"), cancellable = true)
    private void addRecipeBook(CallbackInfo ci) {
        if (ExtraOptions.disableRecipeBook) {
            ci.cancel();
        }
    }
}
