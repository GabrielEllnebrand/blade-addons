package blade.addon.features.other;

import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class RagDisplay extends HUDComponent {

    private static final SoundEvent SOUND = SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CLASSIC).adultSounds().deathSound().value();
    private static final SoundEvent OLD_RAG_SOUND = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "wolf-howl"));
    private static final int TOTAL_TICKS = 200;

    private int tick = 0;

    public RagDisplay() {
        super("Rag axe display");
    }

    public void init() {
        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (!ExtraOptions.enableRagaxeDisplay && !ExtraOptions.useCustomRagSound && !ExtraOptions.useOldRagSound)
                return false;
            if (soundEvent != SOUND) return false;
            if (volume != 1 && pitch != 1.4920635223388672) return false;
            if (!ItemUtil.isHolding("Ragnarock")) return false;

            if (ExtraOptions.enableRagaxeDisplay) {
                tick = TOTAL_TICKS;
            }

            if (ExtraOptions.useOldRagSound) {
                Misc.sendSound(OLD_RAG_SOUND, volume, pitch);
                return true;
            } else if (ExtraOptions.useCustomRagSound) {
                Misc.sendSound(ExtraOptions.ragSound);
                return true;
            }
            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            return false;
        });
    }

    @Override
    public int getWidth() {
        return 30;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return ExtraOptions.enableRagaxeDisplay;
    }

    @Override
    public boolean shouldRender() {
        return tick > 0;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawTimer(this, guiGraphicsExtractor, tick, Constants.YELLOW);
    }

}
