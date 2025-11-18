package blade.addon.mixin;

import blade.addon.utils.events.Events;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class NetworkMixin {


    @Shadow
    private ClientWorld world;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowPacketSizeAndPingCharts()Z"))
    private boolean alwaysSendPing(boolean original) {
        return true;
    }

    @Inject(method = "onEntityTrackerUpdate", at = @At("HEAD"))
    private void onTracking(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci) {
        if (world == null) return;
        Entity entity = this.world.getEntityById(packet.id());
        if (entity == null) return;

        if (Events.ON_ENTITY_TRACKED.hasListeners()) {
            Events.ON_ENTITY_TRACKED.listeners.forEach(entityTrackEvent -> entityTrackEvent.onEntityTracked(entity, world));
        }
    }

    @Inject(method = "handlePlayerListAction", at = @At(value = "TAIL"))
    private void onPlaterList(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry, CallbackInfo ci) {
        if (Events.ON_PLAYER_ENTRY.hasListeners()) {
            Events.ON_PLAYER_ENTRY.listeners.forEach(playerListEvent -> playerListEvent.onNewPlayerEntry(receivedEntry));
        }
    }

}