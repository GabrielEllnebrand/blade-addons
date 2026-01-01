package blade.addon.utils.events.interfaces;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

public interface PlayerListEvent {
    boolean onNewPlayerEntry(PlayerListS2CPacket.Entry receivedEntry);
}
