package blade.addon.mixin;

import blade.addon.utils.events.Events;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Team;
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

    @Inject(method = "onEntityTrackerUpdate", at = @At("TAIL"))
    private void onTracking(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci, @Local Entity entity) {
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

    @Inject(method = "onTeam", at = @At(value = "TAIL"))
    private void onTeam(TeamS2CPacket packet, CallbackInfo ci, @Local Team team) {
        if (team == null) return;
        String teamStr = team.getPrefix().getString() + team.getSuffix().getString();

        if (!Events.ON_TEAM.hasListeners()) return;
        Events.ON_TEAM.invoke(scoreBoardEvent -> scoreBoardEvent.onTeam(teamStr));
    }

}