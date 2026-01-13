package blade.addon.mixin;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.data.EntityUtil;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "renderLabelIfPresent", at=@At("HEAD"), cancellable = true)
    private void hideNameTag(S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        Text text = state.displayName;
        if (!Dungeons.hideBlazeNameTag || text == null) return;
        String string = text.getString();
        if (string.contains("Blaze")) ci.cancel();

    }

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    public void hideFire(T entity, S state, float tickProgress, CallbackInfo ci) {

        if (Visual.hideEntityFire) {
            state.onFire = false;
            return;
        }

        if (entity instanceof PlayerEntity player && Visual.hideFireInf5) {
            if (EntityUtil.isClientPlayer(player)) {
                state.onFire = false;
            }
        }
    }
}
