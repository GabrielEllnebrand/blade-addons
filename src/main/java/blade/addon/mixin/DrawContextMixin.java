package blade.addon.mixin;

import blade.addon.utils.config.values.Visual;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Final
    @Shadow
    private Matrix3x2fStack matrices;


    @ModifyVariable(method = "drawCooldownProgress", at=@At("STORE"), ordinal = 0)
    private float noCooldown(float f) {
        return Visual.hideCooldown? 0: f;
    }

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V", at=@At("HEAD"))
    private void scaleUp(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (Visual.oldPlayerHead && stack.getItem() == Items.PLAYER_HEAD) {
            float scale = 0.875f;
            int offset = (16 - (int)(scale * 16)) / 2;

            matrices.pushMatrix();
            matrices.translate(x * (1 - scale) + offset, y * (1 - scale) + offset);
            matrices.scale(scale);
        }
    }

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V", at=@At("TAIL"))
    private void scaleDown(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (Visual.oldPlayerHead && stack.getItem() == Items.PLAYER_HEAD) {
            matrices.popMatrix();
        }
    }

}
