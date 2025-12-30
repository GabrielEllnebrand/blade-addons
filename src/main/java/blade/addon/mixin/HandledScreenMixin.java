package blade.addon.mixin;

import blade.addon.features.dungeon.f7.invincibility.MaskHighlight;
import blade.addon.features.item.ItemRarityHighlight;
import blade.addon.features.item.ProtectItem;
import blade.addon.features.item.SelectedPetHighlight;
import blade.addon.features.item.StarCountHighlight;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin<T extends ScreenHandler> {

    @Shadow
    @Final
    protected T handler;
    @Unique
    private static final int INVALID_SLOT_ID = -999;

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

        if (ExtraOptions.highlightProtectedItem) {
            ProtectItem.draw(context, stack, x, y);
        }

        if (ExtraOptions.itemRarityBackground) {
            ItemRarityHighlight.draw(context, stack, x, y);
        }
    }

    @Inject(method = "drawSlot", at = @At(value = "TAIL"))
    public void drawStarCount(DrawContext context, Slot slot, CallbackInfo ci) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getStack();

        if (Dungeons.maskHighlight) {
            MaskHighlight.draw(context, stack, x, y);
        }

        if (ExtraOptions.drawStarCount) {
            StarCountHighlight.draw(context, stack, x, y);
        }
    }

    @Inject(method ="onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/screen/slot/SlotActionType;)V"), cancellable = true)
    public void protectItem(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci){

        ItemStack held = handler.getCursorStack();

        if (slotId == INVALID_SLOT_ID && ProtectItem.protect(held)) {
            ci.cancel();
            return;
        }

        if (actionType == SlotActionType.THROW && slot != null) {
            if (ProtectItem.protect(slot.getStack())) {
                ci.cancel();
                return;
            }
        }

        HandledScreen screen = (HandledScreen) (Object) this;
        if (slot != null) {
            if (ProtectItem.blockGUI(screen, slot.getStack())) {
                ci.cancel();
            }
        }
    }
}
