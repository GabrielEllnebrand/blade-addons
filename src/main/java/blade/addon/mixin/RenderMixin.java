package blade.addon.mixin;

import blade.addon.features.ExtraOptions;
import blade.addon.features.dungeon.HidePlayers;
import blade.addon.features.dungeon.ItemHighlight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class RenderMixin {

    @Inject(method = "shouldRender", at = @At("TAIL"), cancellable = true)
    private <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity player) {
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer != null) {
                if (player.getId() == clientPlayer.getId()) return;
            }
            cir.setReturnValue(!HidePlayers.shouldHidePlayers(player));

        }

        if (!entity.isAlive() && ExtraOptions.hideDeadEntities) {
            cir.setReturnValue(false);
        }

        if (entity instanceof ItemEntity item && ItemHighlight.highlightItems) {
            if (ItemHighlight.hideItem(item)) {
                cir.setReturnValue(false);
            }
        }
    }
}
