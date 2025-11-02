package blade.addon.mixin;

import blade.addon.features.dungeon.HidePlayersAfterLeap;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class RenderMixin {

    @Inject(method = "shouldRender", at = @At("TAIL"), cancellable = true)
    private <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getType() == EntityType.PLAYER) {
            cir.setReturnValue(!HidePlayersAfterLeap.shouldHidePlayers());
        }
    }
}
