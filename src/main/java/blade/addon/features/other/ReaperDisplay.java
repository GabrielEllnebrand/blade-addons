package blade.addon.features.other;

import blade.addon.utils.Constants;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ReaperDisplay extends HUDComponent{

    private static final int TOTAL_TICKS = 20 * 6;

    private int tick = 0;

    public ReaperDisplay() {
        super("reaper display");
    }

    public void init() {
        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (!ExtraOptions.enableReaperDisplay) return false;
            if (soundEvent != SoundEvents.ZOMBIE_VILLAGER_CURE) return false;
            if (volume != 0.5 && pitch != 1.0) return false;

            tick = TOTAL_TICKS;
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
        return ExtraOptions.enableReaperDisplay;
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
        RenderUtils.drawTimer(this, guiGraphicsExtractor, tick, Constants.RED);

    }
}
