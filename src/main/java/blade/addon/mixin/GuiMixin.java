package blade.addon.mixin;

import blade.addon.features.dungeon.f7.terms.DeviceNotifier;
import blade.addon.features.dungeon.f7.terms.TitleHider;
import blade.addon.features.item.HeldItemToolTip;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.interfaces.GameHud;
import blade.addon.utils.rendering.DrawEvents;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin implements GameHud {

    @Shadow
    private Component title;
    @Shadow
    private Component subtitle;
    @Shadow
    private int titleFadeInTime;
    @Shadow
    private int titleStayTime;
    @Shadow
    private int titleFadeOutTime;
    @Shadow
    private int titleTime;

    @Shadow
    private int toolHighlightTimer;

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"), order = 2000)
    public void drawBackground(GuiGraphics context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        DrawEvents.HUD_SLOT_BEFORE.invoke(slotEvent -> slotEvent.draw(context, stack, x, y));
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", shift = At.Shift.AFTER), order = 2000)
    public void drawStar(GuiGraphics context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        DrawEvents.HUD_SLOT_AFTER.invoke(slotEvent -> slotEvent.draw(context, stack, x, y));
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true, order = 2000)
    public void renderStatusEffectsOverLay(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (Visual.hideStatusOverLay) {
            ci.cancel();
        }
    }

    @Inject(method = "setTitle", at = @At("HEAD"), cancellable = true, order = 2000)
    public void setTitle(Component title, CallbackInfo ci) {
        if (TitleHider.shouldHideTitle(title)) ci.cancel();
        else if (DeviceNotifier.disableTitles(title)) ci.cancel();
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"), cancellable = true, order = 2000)
    public void setSubtitle(Component subtitle, CallbackInfo ci) {
        if (TitleHider.shouldHideTitle(subtitle)) ci.cancel();
        else if (DeviceNotifier.disableTitles(subtitle)) ci.cancel();
    }

    @Inject(method = "renderSelectedItemName", at=@At("HEAD"))
    private void getFadeDuration(GuiGraphics context, CallbackInfo ci) {
        if (!ExtraOptions.moveToolTip) return;
        HeldItemToolTip.setFade(toolHighlightTimer);
    }

    @Inject(method = "renderSelectedItemName", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"), cancellable = true)
    private void renderHeldItemTooltip(GuiGraphics context, CallbackInfo ci, @Local MutableComponent mutableText, @Local(ordinal = 3) int color) {
        if (!ExtraOptions.moveToolTip) return;
        ci.cancel();
        HeldItemToolTip.setText(mutableText);
        HeldItemToolTip.setColor(color);
        Profiler.get().pop();
    }


    @Unique
    public void blade_addons$forceTitle(Component title, Component subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
    }
}
