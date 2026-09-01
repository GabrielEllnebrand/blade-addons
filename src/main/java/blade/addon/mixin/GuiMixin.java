package blade.addon.mixin;

import blade.addon.features.dungeon.f7.terms.TitleHider;
import blade.addon.features.dungeon.f7.terms.device.DeviceNotifier;
import blade.addon.utils.config.components.Components;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.interfaces.GameHud;
import blade.addon.utils.rendering.DrawEvents;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

    @Inject(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"), order = 2000)
    public void drawBackground(GuiGraphicsExtractor context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        DrawEvents.HUD_SLOT_BEFORE.invoke(slotEvent -> slotEvent.draw(context, stack, x, y));
    }

    @Inject(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", shift = At.Shift.AFTER), order = 2000)
    public void drawStar(GuiGraphicsExtractor context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        DrawEvents.HUD_SLOT_AFTER.invoke(slotEvent -> slotEvent.draw(context, stack, x, y));
    }

    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true, order = 2000)
    public void renderStatusEffectsOverLay(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
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

    @Inject(method = "extractSelectedItemName", at=@At("HEAD"))
    private void getFadeDuration(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!ExtraOptions.moveToolTip) return;
        Components.toolTipDisplay.setFade(toolHighlightTimer);
    }

    @Inject(method = "extractSelectedItemName", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"), cancellable = true)
    private void renderHeldItemTooltip(GuiGraphicsExtractor graphics, CallbackInfo ci, @Local(name = "str") MutableComponent mutableText, @Local(name = "alpha") int color) {
        if (!ExtraOptions.moveToolTip) return;
        ci.cancel();
        Components.toolTipDisplay.setText(mutableText);
        Components.toolTipDisplay.setColor(color);
        Profiler.get().pop();
    }


    @Unique
    public void blade_addons$forceTitle(Component title, Component subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
    }
}
