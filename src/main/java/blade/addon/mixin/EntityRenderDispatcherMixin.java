package blade.addon.mixin;

import blade.addon.features.dungeon.HidePlayers;
import blade.addon.features.dungeon.ItemHighlight;
import blade.addon.features.highlight.MobHighlight;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Visual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "shouldRender", at = @At("TAIL"), cancellable = true)
    private <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player) {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer != null) {
                if (player.getId() == clientPlayer.getId()) return;
            }
            if (HidePlayers.shouldHidePlayers(player)) {
                cir.setReturnValue(false);
            }
        }

        if (!entity.isAlive() && Visual.hideDeadEntities) {
            cir.setReturnValue(false);
            return;
        }

        if (entity instanceof ItemEntity item && Dungeons.highlightItems) {
            if (ItemHighlight.hideItem(item)) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (entity instanceof Sheep) {
            if (Location.inDungeon() && MobHighlight.hideSheep) cir.setReturnValue(false);
        }

        if (Visual.oldFishingRod && entity instanceof FishingHook bobber) {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null || bobber.getOwner() != clientPlayer) return;

            //Pretty much just hides the bobber if it's too close
            //its def not exact, but it's good enough for now
            Vec3 pos = bobber.getInterpolation().position();
            if (clientPlayer.position().distanceTo(pos) < 2 && bobber.tickCount < 6 && pos.y > clientPlayer.getY() + 1.2 && clientPlayer.getXRot() > -60) {
                cir.setReturnValue(false);
            }
        }

        if (entity instanceof ArmorStand armorStand && MobHighlight.hideNoneStaredNameTags) {
            if (MobHighlight.nonStaredTags.contains(armorStand)) {
                cir.setReturnValue(false);
            }
        }

    }
}
