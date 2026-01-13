package blade.addon.mixin;

import blade.addon.features.dungeon.f7.terms.Pre4Notifier;
import blade.addon.features.dungeon.f7.terms.TitleHider;
import blade.addon.features.item.ItemRarityHighlight;
import blade.addon.features.item.ProtectItem;
import blade.addon.features.item.StarCountHighlight;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.interfaces.GameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin implements GameHud {

    @Shadow
    private Text title;
    @Shadow
    private Text subtitle;
    @Shadow
    private int titleFadeInTicks;
    @Shadow
    private int titleStayTicks;
    @Shadow
    private int titleFadeOutTicks;
    @Shadow
    private int titleRemainTicks;

    @Inject(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"), order = 2000)
    public void drawBackground(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (Visual.itemRarityBackground) {
            ItemRarityHighlight.draw(context, stack, x, y);
        }

        if (Visual.highlightProtectedItem) {
            ProtectItem.draw(context, stack, x, y);
        }

    }

    @Inject(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V", shift = At.Shift.AFTER), order = 2000)
    public void drawStar(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (Visual.drawStarCount) {
            StarCountHighlight.draw(context, stack, x, y);
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true, order = 2000)
    public void renderStatusEffectsOverLay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (Visual.hideStatusOverLay) {
            ci.cancel();
        }
    }

    @Inject(method = "setTitle", at = @At("HEAD"), cancellable = true, order = 2000)
    public void renderStatusEffectsOverLay(Text title, CallbackInfo ci) {
        if (TitleHider.shouldHideTitle()) ci.cancel();
        else if (Pre4Notifier.disableTitles()) ci.cancel();
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"), cancellable = true, order = 2000)
    public void setSubtitle(Text subtitle, CallbackInfo ci) {
        if (TitleHider.shouldHideTitle()) ci.cancel();
        else if (Pre4Notifier.disableTitles()) ci.cancel();
    }


    @Unique
    public void blade_addons$forceTitle(Text title, Text subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
    }
}
