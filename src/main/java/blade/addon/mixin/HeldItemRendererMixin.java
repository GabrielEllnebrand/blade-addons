package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import blade.addon.features.other.SwingAnimation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow
    private ItemStack mainHand;

    @Shadow
    private float equipProgressMainHand;

    @Inject(method = "updateHeldItems", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    private void test(CallbackInfo ci, @Local ClientPlayerEntity clientPlayer, @Local ItemStack itemStack) {
        if (!DropAnimation.shouldProceed()) return;
        if (itemStack.getItem() == Items.AIR) return;
        ItemStack prevDropped = DropAnimation.getPrevDroppedItem();
        int slot = DropAnimation.getSelectedSlot();
        if (prevDropped == null) return;

        //check if item has changed
        if (prevDropped.getItem() != itemStack.getItem()) {
            DropAnimation.clearData();
        }

        //check if slot has changed
        if (slot != clientPlayer.getInventory().getSelectedSlot()) {
            DropAnimation.clearData();
        }
    }

    @Inject(method = "updateHeldItems", at = @At("TAIL"))
    private void changeHeld(CallbackInfo ci) {
        if (!DropAnimation.shouldProceed()) return;
        ItemStack stack = DropAnimation.getPrevDroppedItem();
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;
        mainHand = stack;
        this.equipProgressMainHand = 1;
    }

    @Inject(method = "applyEquipOffset", at = @At("HEAD"), cancellable = true)
    private void applyEquipOffset(MatrixStack matrices, Arm arm, float swingProgress, CallbackInfo ci) {
        if (!SwingAnimation.shouldIgnore()) return;
        int side = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float) side * 0.56F, -0.52F, -0.72F);
        ci.cancel();
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void swingArm(float swingProgress, MatrixStack matrixStack, int i, Arm arm, CallbackInfo ci) {
        if (!SwingAnimation.shouldIgnore()) return;
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt(1) * (float) Math.PI);
        float g = 0.2F * MathHelper.sin(MathHelper.sqrt(1) * (float) (Math.PI * 2));
        float h = -0.2F * MathHelper.sin(1 * (float) Math.PI);
        matrixStack.translate(i * f, g, h);
        ci.cancel();
    }
}
