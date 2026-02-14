package blade.addon.mixin;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.events.Events;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {


    @Shadow
    private ClientWorld world;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowPacketSizeAndPingCharts()Z"))
    private boolean alwaysSendPing(boolean original) {
        return true;
    }

    @Inject(method = "onEntityTrackerUpdate", at = @At("TAIL"))
    private void onTracking(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci, @Local Entity entity) {
        if (entity == null) return;

        Events.ON_ENTITY_TRACKED.invoke(entityTrackEvent -> entityTrackEvent.onEntity(entity, world));
    }

    @Inject(method = "handlePlayerListAction", at = @At(value = "TAIL"))
    private void onPlayerList(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry, CallbackInfo ci) {
        Events.ON_PLAYER_ENTRY.invoke(playerListEvent -> playerListEvent.onNewPlayerEntry(receivedEntry));
    }

    @Inject(method = "onTeam", at = @At(value = "TAIL"))
    private void onTeam(TeamS2CPacket packet, CallbackInfo ci, @Local Team team) {
        if (team == null) return;
        String teamStr = (team.getPrefix().getString() + team.getSuffix().getString()).replaceAll("§.", "");
        Events.ON_TEAM.invoke(scoreBoardEvent -> scoreBoardEvent.onTeam(teamStr));
    }

    @Inject(method = "onPlaySound", at = @At(value = "HEAD"), cancellable = true)
    private void onSound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        float volume = packet.getVolume();
        float pitch = packet.getPitch();
        SoundEvent event = packet.getSound().value();

        if (ExtraOptions.disableAbilityCooldownSound && pitch == 0.0 && volume == 8.0 && event == SoundEvents.ENTITY_ENDERMAN_TELEPORT) {
            ci.cancel();
        }

        if (Debug.sendSound) {
            Misc.addChatMessage(Text.literal("Sound: " + event.id() + " Volume: " + volume + " Pitch: " + pitch));
        }

        if (Events.ON_SOUND.invoke(soundEvent -> soundEvent.onSound(event, volume, pitch))) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlaySoundFromEntity", at = @At(value = "HEAD"), cancellable = true)
    private void onLocationSound(PlaySoundFromEntityS2CPacket packet, CallbackInfo ci) {
        float volume = packet.getVolume();
        float pitch = packet.getPitch();
        SoundEvent event = packet.getSound().value();

        if (Debug.sendSound) {
            Misc.addChatMessage(Text.literal("Sound: " + event.id() + " Volume: " + volume + " Pitch: " + pitch + "entitySeed: " + packet.getEntityId()));
        }

        if (Events.ON_SOUND.invoke(soundEvent -> soundEvent.onSound(event, volume, pitch))) {
            ci.cancel();
        }
    }

    @Inject(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;playSpawnSound(Lnet/minecraft/entity/Entity;)V"))
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, @Local Entity entity) {
        Events.ON_ENTITY_SPAWNED.invoke(entityTrackEvent -> entityTrackEvent.onEntity(entity, world));
    }

    @Inject(method = "onParticle", at = @At("HEAD"))
    private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        Events.ON_PARTICLE.invoke(particleEvent -> particleEvent.onParticle(packet));
    }

    @WrapOperation(method = "onBundle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V"))
    private void apply(Packet<?> packet, PacketListener listener, Operation<Void> original) {
        if (packet instanceof GameMessageS2CPacket(Text content, boolean overlay) && !overlay) {
            if (Events.ON_GAME_MESSAGE.invoke(gameMessageEvent -> gameMessageEvent.onGameMessage(content))) {
                return;
            }
        }

        Events.ON_PACKET.invoke(packetEvent -> packetEvent.onPacket(packet));
        original.call(packet, listener);
    }
}