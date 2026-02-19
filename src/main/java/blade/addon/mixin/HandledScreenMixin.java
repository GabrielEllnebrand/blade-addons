package blade.addon.mixin;

import blade.addon.features.item.ProtectItem;
import blade.addon.features.other.SearchBar;
import blade.addon.utils.rendering.DrawEvents;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Shadow
    @Final
    protected T handler;
    @Unique
    private static final int INVALID_SLOT_ID = -999;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at=@At("TAIL"))
    private static void render(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        SearchBar.render(context, mouseX, mouseY, deltaTicks);
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"))
    public void drawBackground(DrawContext context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getStack();
        DrawEvents.INVENTORY_SLOT_BEFORE.invoke(event -> event.draw(context, stack, x, y));
    }

    @Inject(method = "drawSlot", at = @At(value = "TAIL"))
    public void drawStarCount(DrawContext context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getStack();
        DrawEvents.INVENTORY_SLOT_AFTER.invoke(event -> event.draw(context, stack, x, y));
    }

    @Inject(method = "keyPressed", at=@At("HEAD"), cancellable = true)
    private void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (SearchBar.keyPressed(input)) cir.setReturnValue(false);
    }

    @Inject(method = "mouseClicked", at=@At("HEAD"))
    private void onMouseClick(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        SearchBar.onMouseClick(click);
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
