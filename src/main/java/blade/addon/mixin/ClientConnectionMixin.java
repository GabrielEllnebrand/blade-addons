package blade.addon.mixin;

import blade.addon.utils.events.Events;
import blade.addon.utils.events.interfaces.ServerTickEvent;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V"), order = 0, cancellable = true)
    private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof CommonPingS2CPacket common) {
            //admins send these packets too for inventory changes
            //thankfully they all have the param 0 for some reason
            if (common.getParameter() == 0) return;
            Events.ON_SERVER_TICK.invoke(ServerTickEvent::onServerTick);
        }

        if (packet instanceof GameMessageS2CPacket(Text content, boolean overlay) && !overlay) {
            if (Events.ON_GAME_MESSAGE.invoke(gameMessageEvent -> gameMessageEvent.onGameMessage(content))) {
               ci.cancel();
            }
        }

        Events.ON_PACKET.invoke(packetEvent -> packetEvent.onPacket(packet));
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"))
    private void sendImmediately(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
        // for some reason this just being here
        // fixes player tracking and I don't know why
        // will hopefully look into it later
    }
}
