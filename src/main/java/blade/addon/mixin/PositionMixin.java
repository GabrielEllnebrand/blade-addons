package blade.addon.mixin;

import blade.addon.features.dungeon.DeathTickTimer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class PositionMixin {

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void channelRead0(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        DeathTickTimer.onTeleport(packet.change().position());
    }



}
