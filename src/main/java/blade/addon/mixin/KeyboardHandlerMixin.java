package blade.addon.mixin;

import blade.addon.features.other.SearchBar;
import blade.addon.utils.config.values.ExtraOptions;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "charTyped", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z"))
    private void onChar(long window, CharacterEvent input, CallbackInfo ci, @Local Screen screen) {
        if (ExtraOptions.toggleableSearchBar && screen instanceof AbstractContainerScreen<?>) {
            SearchBar.CharTyped(input);
        }
    }


}
