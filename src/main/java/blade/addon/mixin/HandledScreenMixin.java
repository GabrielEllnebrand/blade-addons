package blade.addon.mixin;

import blade.addon.features.item.ItemRarityDrawer;
import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"))
    public void drawBackground(DrawContext context, Slot slot, CallbackInfo ci) {
        if (!ExtraOptions.itemRarityBackground) return;
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getStack();
        ItemRarityDrawer.draw(context, stack, x, y);
    }
}
