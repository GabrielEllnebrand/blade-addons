package blade.addon.utils.events.interfaces;

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;

public interface ParticleEvent {

    boolean onParticle(ParticleS2CPacket packet);
}
