package blade.addon.mixin;

import blade.addon.features.dungeon.DeathTickTimer;
import blade.addon.utils.Debug;
import blade.addon.utils.Misc;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.detection.PlayerDataHolder;
import blade.addon.utils.events.Events;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

        if (Events.ON_ENTITY_TRACKED.hasListeners()) {
            Events.ON_ENTITY_TRACKED.listeners.forEach(entityTrackEvent -> entityTrackEvent.onEntity(entity, world));
        }
    }

    @Inject(method = "handlePlayerListAction", at = @At(value = "TAIL"))
    private void onPlayerList(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry, CallbackInfo ci) {
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

    @Inject(method = "onPlaySound", at = @At(value = "HEAD"), cancellable = true)
    private void onSound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        float volume = packet.getVolume();
        float pitch = packet.getPitch();
        SoundEvent event = packet.getSound().value();

        if (pitch == 0.0 && volume == 8.0 && event == SoundEvents.ENTITY_ENDERMAN_TELEPORT && ExtraOptions.disableAbilityCooldownSound) {
            ci.cancel();
        }

        if (Debug.sendSound) {
            Scheduler.scheduleTask(() -> Misc.addChatMessage(Text.literal("Sound: " + event.id())), 1);
        }

        if (Events.ON_SOUND.test(soundEvent -> soundEvent.onSound(event, volume, pitch))) {
            ci.cancel();
        }
    }

    @Inject(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;playSpawnSound(Lnet/minecraft/entity/Entity;)V"))
    public void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, @Local Entity entity) {

        if (entity instanceof PlayerEntity player) {
            PlayerDataHolder dataHolder = (PlayerDataHolder) (Object) player;
            boolean isRealPlayer = isARealPlayer(player);
            dataHolder.blade_addons$setIsRealPlayer(isRealPlayer);
        }

        if (Events.ON_ENTITY_SPAWNED.hasListeners()) {
            Events.ON_ENTITY_SPAWNED.listeners.forEach(entityTrackEvent -> entityTrackEvent.onEntity(entity, world));
        }

    }

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void onPlayerLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        DeathTickTimer.onTeleport(packet.change().position());
    }

    @Unique
    private static boolean isARealPlayer(Entity entity) {
        if (entity instanceof PlayerEntity player) {

            if (Misc.isClientPlayer(player)) return true;

            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return false;

            PlayerListEntry entry = networkHandler.getPlayerListEntry(player.getUuid());

            //this is a hack which will fail if someone has a really old bugged ign that includes a space
            if (entry != null) {
                String name = entry.getProfile().name();
                return !name.isEmpty() && !name.contains(" ");
            }
        }
        return false;
    }

}