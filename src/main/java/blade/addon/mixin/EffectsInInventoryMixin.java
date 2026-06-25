package blade.addon.mixin;

import blade.addon.utils.config.values.Visual;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {

    @Inject(method = "extractEffects", at=@At("HEAD"), cancellable = true)
    public void renderEffects(GuiGraphicsExtractor context, Collection<MobEffectInstance> effects, int x, int height, int mouseX, int mouseY, int width, CallbackInfo ci) {
        if (Visual.hideStatusOverLay) {
            ci.cancel();
        }
    }

    @Inject(method = "extractText", at=@At("HEAD"), cancellable = true)
    public void renderText(CallbackInfo ci) {
        if (Visual.hideStatusOverLay) {
            ci.cancel();
        }
    }
}
