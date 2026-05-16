package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import blade.addon.features.other.SwingAnimation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private float mainHandHeight;

    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    private void test(CallbackInfo ci, @Local LocalPlayer clientPlayer, @Local ItemStack itemStack) {
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void changeHeld(CallbackInfo ci) {
        if (!DropAnimation.shouldProceed()) return;
        ItemStack stack = DropAnimation.getPrevDroppedItem();
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;
        mainHandItem = stack;
        this.mainHandHeight = 1;
    }

    @Inject(method = "applyItemArmTransform", at = @At("HEAD"), cancellable = true)
    private void applyEquipOffset(PoseStack matrices, HumanoidArm arm, float swingProgress, CallbackInfo ci) {
        if (!SwingAnimation.shouldIgnore()) return;
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        matrices.translate((float) side * 0.56F, -0.52F, -0.72F);
        ci.cancel();
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void swingArm(float swingProgress, PoseStack matrixStack, int i, HumanoidArm arm, CallbackInfo ci) {
        if (!SwingAnimation.shouldIgnore()) return;
        float f = -0.4F * Mth.sin(Mth.sqrt(1) * (float) Math.PI);
        float g = 0.2F * Mth.sin(Mth.sqrt(1) * (float) (Math.PI * 2));
        float h = -0.2F * Mth.sin(1 * (float) Math.PI);
        matrixStack.translate(i * f, g, h);
        ci.cancel();
    }
}
