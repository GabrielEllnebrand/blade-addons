package blade.addon.mixin;

import blade.addon.features.item.ItemRarityHighlight;
import blade.addon.features.item.ProtectItem;
import blade.addon.features.item.StarCountHighlight;
import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"))
    public void drawBackground(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (ExtraOptions.itemRarityBackground) {
            ItemRarityHighlight.draw(context, stack, x, y);
        }

        if (ExtraOptions.highlightProtectedItem) {
            ProtectItem.draw(context, stack, x, y);
        }

    }

    @Inject(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V", shift = At.Shift.AFTER))
    public void drawStar(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (ExtraOptions.drawStarCount) {
            StarCountHighlight.draw(context, stack, x, y);
        }
    }

     @Inject(method = "renderStatusEffectOverlay", at=@At("HEAD"), cancellable = true)
    public void renderStatusEffectsOverLay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ExtraOptions.hideStatusOverLay) {
            ci.cancel();
        }
     }
}
