package blade.addon.mixin;

import blade.addon.features.item.ProtectItem;
import blade.addon.features.other.SearchBar;
import blade.addon.utils.rendering.DrawEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow
    @Final
    protected T menu;
    @Unique
    private static final int INVALID_SLOT_ID = -999;

    protected HandledScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at=@At("TAIL"))
    private static void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        SearchBar.render(context, mouseX, mouseY, deltaTicks);
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;III)V"))
    public void drawBackground(GuiGraphics context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getItem();
        DrawEvents.INVENTORY_SLOT_BEFORE.invoke(event -> event.draw(context, stack, x, y));
    }

    @Inject(method = "renderSlot", at = @At(value = "TAIL"))
    public void drawStarCount(GuiGraphics context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        int x = slot.x;
        int y = slot.y;
        ItemStack stack = slot.getItem();
        DrawEvents.INVENTORY_SLOT_AFTER.invoke(event -> event.draw(context, stack, x, y));
    }

    @Inject(method = "keyPressed", at=@At("HEAD"), cancellable = true)
    private void keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (SearchBar.keyPressed(input)) cir.setReturnValue(false);
    }

    @Inject(method = "mouseClicked", at=@At("HEAD"))
    private void onMouseClick(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        SearchBar.onMouseClick(click);
    }

    @Inject(method ="slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;onMouseClickAction(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickType;)V"), cancellable = true)
    public void protectItem(Slot slot, int slotId, int button, ClickType actionType, CallbackInfo ci){

        ItemStack held = menu.getCarried();

        if (slotId == INVALID_SLOT_ID && ProtectItem.protect(held)) {
            ci.cancel();
            return;
        }

        if (actionType == ClickType.THROW && slot != null) {
            if (ProtectItem.protect(slot.getItem())) {
                ci.cancel();
                return;
            }
        }

        AbstractContainerScreen screen = (AbstractContainerScreen) (Object) this;
        if (slot != null) {
            if (ProtectItem.blockGUI(screen, slot.getItem())) {
                ci.cancel();
            }
        }
    }
}
