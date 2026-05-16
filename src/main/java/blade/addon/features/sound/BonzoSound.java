package blade.addon.features.sound;

import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class BonzoSound {

    private static final SoundEvent OLD_BONZO_SOUND = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "ghast-moan"));


    public static void init() {
        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (volume != 1.0 || soundEvent != SoundEvents.GHAST_WARN) return false;

            if (ExtraOptions.oldBonzoSound) {
                Misc.sendSound(OLD_BONZO_SOUND, volume, pitch);
                return true;
            }

            return ExtraOptions.disableBonzoSound;
        });
    }

}
