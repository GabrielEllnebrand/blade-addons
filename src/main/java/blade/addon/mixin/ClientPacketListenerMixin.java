package blade.addon.mixin;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.events.Events;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {


    @Shadow
    private ClientLevel level;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showNetworkCharts()Z"))
    private boolean alwaysSendPing(boolean original) {
        return true;
    }

    @Inject(method = "handleSetEntityData", at = @At("TAIL"))
    private void onTracking(ClientboundSetEntityDataPacket packet, CallbackInfo ci, @Local Entity entity) {
        if (entity == null) return;

        Events.ON_ENTITY_TRACKED.invoke(entityTrackEvent -> entityTrackEvent.onEntity(entity, level));
    }

    @Inject(method = "applyPlayerInfoUpdate", at = @At(value = "TAIL"))
    private void onPlayerList(ClientboundPlayerInfoUpdatePacket.Action action, ClientboundPlayerInfoUpdatePacket.Entry receivedEntry, PlayerInfo currentEntry, CallbackInfo ci) {
        Events.ON_PLAYER_ENTRY.invoke(playerListEvent -> playerListEvent.onNewPlayerEntry(receivedEntry));
    }

    @Inject(method = "handleSetPlayerTeamPacket", at = @At(value = "TAIL"))
    private void onTeam(ClientboundSetPlayerTeamPacket packet, CallbackInfo ci, @Local PlayerTeam team) {
        if (team == null) return;
        String teamStr = (team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString()).replaceAll("§.", "");
        Events.ON_TEAM.invoke(scoreBoardEvent -> scoreBoardEvent.onTeam(teamStr));
    }

    @Inject(method = "handleSoundEvent", at = @At(value = "HEAD"), cancellable = true)
    private void onSound(ClientboundSoundPacket packet, CallbackInfo ci) {
        float volume = packet.getVolume();
        float pitch = packet.getPitch();
        SoundEvent event = packet.getSound().value();

        if (ExtraOptions.disableAbilityCooldownSound && pitch == 0.0 && volume == 8.0 && event == SoundEvents.ENDERMAN_TELEPORT) {
            ci.cancel();
        }

        if (Debug.sendSound) {
            Misc.addChatMessage(Component.literal("Sound: " + event.location() + " Volume: " + volume + " Pitch: " + pitch));
        }

        if (Events.ON_SOUND.invoke(soundEvent -> soundEvent.onSound(event, volume, pitch))) {
            ci.cancel();
        }
    }

    @Inject(method = "handleSoundEntityEvent", at = @At(value = "HEAD"), cancellable = true)
    private void onLocationSound(ClientboundSoundEntityPacket packet, CallbackInfo ci) {
        float volume = packet.getVolume();
        float pitch = packet.getPitch();
        SoundEvent event = packet.getSound().value();

        if (Debug.sendSound) {
            Misc.addChatMessage(Component.literal("Sound: " + event.location() + " Volume: " + volume + " Pitch: " + pitch + "entitySeed: " + packet.getId()));
        }

        if (Events.ON_SOUND.invoke(soundEvent -> soundEvent.onSound(event, volume, pitch))) {
            ci.cancel();
        }
    }

    @Inject(method = "handleAddEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;postAddEntitySoundInstance(Lnet/minecraft/world/entity/Entity;)V"))
    private void onEntitySpawn(ClientboundAddEntityPacket packet, CallbackInfo ci, @Local Entity entity) {
        Events.ON_ENTITY_SPAWNED.invoke(entityTrackEvent -> entityTrackEvent.onEntity(entity, level));
    }

    @Inject(method = "handleParticleEvent", at = @At("HEAD"))
    private void onParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
        Events.ON_PARTICLE.invoke(particleEvent -> particleEvent.onParticle(packet));
    }

    @WrapOperation(method = "handleBundlePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"))
    private void apply(Packet<?> packet, PacketListener listener, Operation<Void> original) {
        if (packet instanceof ClientboundSystemChatPacket(Component content, boolean overlay) && !overlay) {
            if (Events.ON_GAME_MESSAGE.invoke(gameMessageEvent -> gameMessageEvent.onGameMessage(content))) {
                return;
            }
        }

        Events.ON_PACKET.invoke(packetEvent -> packetEvent.onPacket(packet));
        original.call(packet, listener);
    }
}