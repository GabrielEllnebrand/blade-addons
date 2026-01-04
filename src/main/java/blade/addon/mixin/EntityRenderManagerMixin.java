package blade.addon.mixin;

import blade.addon.features.dungeon.HidePlayers;
import blade.addon.features.dungeon.ItemHighlight;
import blade.addon.features.highlight.MobHighlight;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderManager.class)
public class EntityRenderManagerMixin {

    @Inject(method = "shouldRender", at = @At("TAIL"), cancellable = true)
    private <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity player) {
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer != null) {
                if (player.getId() == clientPlayer.getId()) return;
            }
            if (HidePlayers.shouldHidePlayers(player)) {
                cir.setReturnValue(false);
            }
        }

        if (!entity.isAlive() && ExtraOptions.hideDeadEntities) {
            cir.setReturnValue(false);
            return;
        }

        if (entity instanceof ItemEntity item && Dungeons.highlightItems) {
            if (ItemHighlight.hideItem(item)) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (entity instanceof SheepEntity) {
            if (Location.inDungeon() && MobHighlight.hideSheep) cir.setReturnValue(false);
        }
    }
}
