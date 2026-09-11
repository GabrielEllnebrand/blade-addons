package blade.addon.mixin;

import blade.addon.utils.config.values.Visual;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    @Final
    @Shadow
    private Matrix3x2fStack pose;

    @ModifyVariable(method = "itemCooldown", at = @At("STORE"), ordinal = 0)
    private float noCooldown(float f) {
        return Visual.hideCooldown ? 0 : f;
    }

    @Inject(method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"))
    private void scaleUp(LivingEntity owner, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (Visual.oldPlayerHead && stack.getItem() == Items.PLAYER_HEAD) {
            float scale = 0.875f;
            int offset = (16 - (int) (scale * 16)) / 2;

            pose.pushMatrix();
            pose.translate(x * (1 - scale) + offset, y * (1 - scale) + offset);
            pose.scale(scale);
        }
    }

    @Inject(method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("TAIL"))
    private void scaleDown(LivingEntity owner, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (Visual.oldPlayerHead && stack.getItem() == Items.PLAYER_HEAD) {
            pose.popMatrix();
        }
    }

    @Inject(method = "tooltip", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;pushMatrix()Lorg/joml/Matrix3x2fStack;"))
    private void scaleUpTooltip_head(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner, @Nullable Identifier style, CallbackInfo ci) {

        float scaledX = xo - (float) xo * Visual.tooltipSize;
        float scaledY = yo - (float) yo * Visual.tooltipSize;

        pose.translate(scaledX, scaledY);
        pose.scale(Visual.tooltipSize);
    }

    @ModifyArgs(method = "tooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"))
    private void scaleUpTooltip(Args args) {

        int w = args.get(4);
        int h = args.get(5);

        float scaledW = (float) w * Visual.tooltipSize;
        float scaledH = (float) h * Visual.tooltipSize;

        args.set(4, (int) scaledW);
        args.set(5, (int) scaledH);
    }

}
