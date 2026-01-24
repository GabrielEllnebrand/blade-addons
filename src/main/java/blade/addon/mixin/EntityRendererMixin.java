package blade.addon.mixin;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.DungeonClass;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    public void hideFire(T entity, S state, float tickProgress, CallbackInfo ci) {

        if (Dungeons.hideBlazeNameTag && state.displayName != null) {
            if (state.displayName.getString().contains("Blaze")) {
                state.displayName = null;
            }

        }
        if (entity instanceof OtherClientPlayerEntity player && Dungeons.renderClassName && Location.inDungeon()) {
            if (DungeonClass.isTeammate(player)) {
                state.displayName = null;
            }
        }

        if (Visual.hideEntityFire) {
            state.onFire = false;
        } else if (entity instanceof PlayerEntity player && Visual.hideFireInf5) {
            if (EntityUtil.isClientPlayer(player)) {
                state.onFire = false;
            }
        }
    }
}
