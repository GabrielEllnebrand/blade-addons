package blade.addon.mixin;

import blade.addon.features.item.ItemRarityHighlight;
import blade.addon.features.item.SelectedPetHighlight;
import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"))
    public void drawBackground(DrawContext context, Slot slot, CallbackInfo ci) {

        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getStack();

        if (ExtraOptions.highlightSelectedPet) {
            HandledScreen screen = (HandledScreen) (Object) this;
            if (screen instanceof GenericContainerScreen containerScreen) {
                Text title = containerScreen.getTitle();
                if (title == null) return;
                if (title.getString().equals("Pets")) {
                    SelectedPetHighlight.draw(context, stack, x, y);
                }

            }
        }

        if (ExtraOptions.itemRarityBackground) {
            ItemRarityHighlight.draw(context, stack, x, y);
        }
    }
}
