package blade.addon.mixin;

import blade.addon.utils.events.Events;
import blade.addon.utils.events.interfaces.ServerTickEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ServerTickMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof CommonPingS2CPacket common && Events.ON_SERVER_TICK.hasListeners()) {
            //admins send these packets too for inventory changes
            //thankfully they all have the param 0 for some reason
            if (common.getParameter() == 0) return;
            Events.ON_SERVER_TICK.invoke(ServerTickEvent::onServerTick);
        }


    }

    @Inject(method = "sendImmediately", at = @At("HEAD"))
    private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (packet instanceof CommonPingS2CPacket common && Events.ON_SERVER_TICK.hasListeners()) {
            if (common.getParameter() == 0) return;
            Events.ON_SERVER_TICK.invoke(ServerTickEvent::onServerTick);
        }


    }
}
