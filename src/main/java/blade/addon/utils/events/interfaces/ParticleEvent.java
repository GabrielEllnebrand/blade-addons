package blade.addon.utils.events.interfaces;

import net.minecraft.particle.ParticleEffect;

public interface ParticleEvent {

    void onParticle(double x, double y, double z, ParticleEffect effect);
}
