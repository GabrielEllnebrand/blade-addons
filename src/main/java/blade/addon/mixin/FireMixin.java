package blade.addon.mixin;

import blade.addon.features.dungeon.f7.RenderOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class FireMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    public void test(T entity, S state, float tickProgress, CallbackInfo ci) {

        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer != null) {
            if (clientPlayer == entity && RenderOptions.hideFireInf5) {
                state.onFire = false;
            }
        }

    }
}
