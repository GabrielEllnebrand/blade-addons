package blade.addon.mixin;

import blade.addon.utils.config.values.Visual;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Final
    @Shadow
    private Matrix3x2fStack pose;

    @Shadow
    public abstract int guiHeight();

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

    @ModifyExpressionValue(method = "tooltip", at = @At(value = "INVOKE", target = "Lorg/joml/Vector2ic;x()I"))
    private int modifyTooltipX(int x) {
        if (Visual.tooltipSize == 1) return x;
        return 0;
    }

    @ModifyExpressionValue(method = "tooltip", at = @At(value = "INVOKE", target = "Lorg/joml/Vector2ic;y()I"))
    private int modifyTooltipY(int y) {
        if (Visual.tooltipSize == 1) return y;
        return 0;
    }

    @Inject(method = "tooltip", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;pushMatrix()Lorg/joml/Matrix3x2fStack;"))
    private void scaleUpTooltip_head(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner,
                                     @Nullable Identifier style, CallbackInfo ci, @Local(name = "h") int h) {
        if (Visual.tooltipSize == 1) return;
        float newX = xo + 6;
        float newY = yo - ((float) h / 2) * Visual.tooltipSize + 4;

        //remove bottom overflow hopefully
        if ((h + newY) * Visual.tooltipSize > this.guiHeight()) {
            newY = this.guiHeight() - h - 4;
        }

        //remove top and left overflow
        if (newX < 0) newX = 6;
        if (newY < 0) newY = 4;

        pose.translate(newX, newY);
        pose.scale(Visual.tooltipSize);
    }

}
