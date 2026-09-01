package blade.addon.features.dungeon.f7.invincibility;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class InvincibilityDuration extends HUDComponent {

    private static final int MAX_DURATION = 3 * 20;
    private int ticks = 0;

    public InvincibilityDuration() {
        super("Invincibility duration");
    }

    public void init() {
        Events.ON_SERVER_TICK.register(() -> {
            ticks = Math.max(0, ticks - 1);
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
        return Dungeons.InvincibilityDuration;
    }

    @Override
    public boolean shouldRender() {
        return ticks > 0 && Dungeons.InvincibilityDuration;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int color = 0xffffffff;
        if (Dungeons.useStatusColorForInvincibility) {
            color = RenderUtils.getStatusColor(40, 20, ticks);
        }
        RenderUtils.drawTimer(this, guiGraphicsExtractor, ticks, color);
    }

    public void proc() {
        ticks = MAX_DURATION;
        Misc.sendSound(Dungeons.invincibilitySound);
    }

}
