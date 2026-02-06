package blade.addon.mixin;

import blade.addon.features.other.SearchBar;
import blade.addon.utils.config.values.ExtraOptions;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.CharInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onChar", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;charTyped(Lnet/minecraft/client/input/CharInput;)Z"))
    private void onChar(long window, CharInput input, CallbackInfo ci, @Local Screen screen) {
        if (ExtraOptions.toggleableSearchBar && screen instanceof HandledScreen<?>) {
            SearchBar.CharTyped(input);
        }
    }


}
