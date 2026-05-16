package blade.addon.utils.events.interfaces;

import net.minecraft.sounds.SoundEvent;

public interface PlaySoundEvent {
    boolean onSound(SoundEvent soundEvent, float volume, float pitch);
}
