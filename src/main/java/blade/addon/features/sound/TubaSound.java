package blade.addon.features.sound;

import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.events.Events;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;

public class TubaSound {

    private static final SoundEvent SOUND = SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CLASSIC).deathSound().value();
    private static final SoundEvent TUBA_SOUND = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "wolf-howl"));


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
