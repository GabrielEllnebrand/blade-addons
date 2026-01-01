package blade.addon.mixin;

import blade.addon.utils.events.Events;
import blade.addon.utils.events.interfaces.ServerTickEvent;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), order = 0, cancellable = true)
    private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof CommonPingS2CPacket common) {
            //admins send these packets too for inventory changes
            //thankfully they all have the param 0 for some reason
            if (common.getParameter() == 0) return;
            Events.ON_SERVER_TICK.invoke(ServerTickEvent::onServerTick);
        }

        if (packet instanceof GameMessageS2CPacket message) {
            if (Events.ON_GAME_MESSAGE.invoke(gameMessageEvent -> gameMessageEvent.onGameMessage(message.content()))) {
                ci.cancel();
                return;
            }
        }

        if (packet instanceof ParticleS2CPacket particle) {
            double x = particle.getX();
            double y = particle.getY();
            double z = particle.getZ();
            ParticleEffect effect = particle.getParameters();

            Events.ON_PARTICLE.invoke(particleEvent -> particleEvent.onParticle(x, y, z, effect));

        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"))
    private void sendImmediately(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
        if (packet instanceof CommonPingS2CPacket common) {
            if (common.getParameter() == 0) return;
            Events.ON_SERVER_TICK.invoke(ServerTickEvent::onServerTick);
        }


    }
}
