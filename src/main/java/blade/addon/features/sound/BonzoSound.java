package blade.addon.features.sound;

import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class BonzoSound {

    private static final SoundEvent OLD_BONZO_SOUND = SoundEvent.of(Identifier.of(Constants.NAMESPACE, "ghast-moan"));


    public static void init() {
        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (volume != 1.0 || soundEvent != SoundEvents.ENTITY_GHAST_WARN) return false;

            if (ExtraOptions.oldBonzoSound) {
                Misc.sendSound(OLD_BONZO_SOUND, volume, pitch);
                return true;
            }

            return ExtraOptions.disableBonzoSound;
        });
    }

}
