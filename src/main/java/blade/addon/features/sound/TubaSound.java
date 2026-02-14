package blade.addon.features.sound;

import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.events.Events;
import net.minecraft.entity.passive.WolfSoundVariants;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class TubaSound {

    private static final SoundEvent SOUND = SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.Type.CLASSIC).deathSound().value();
    private static final SoundEvent TUBA_SOUND = SoundEvent.of(Identifier.of(Constants.NAMESPACE, "wolf-howl"));


    public static void init() {
        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (volume != 0.5 || soundEvent != SOUND) return false;
            if (!ItemUtil.isHolding("Tuba")) return false;

            if (ExtraOptions.oldTubaSound) {
                Misc.sendSound(TUBA_SOUND, volume, pitch);
                return true;
            }
            return false;
        });
    }

}
