package blade.addon.utils.events.interfaces;

import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;

public interface ParticleEvent {

    boolean onParticle(ClientboundLevelParticlesPacket packet);
}
