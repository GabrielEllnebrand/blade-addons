package blade.addon.features.other;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class KickedTimer extends HUDComponent {

    private static boolean isKicked = false;
    private static long kickedTime = 0;

    public KickedTimer() {
        super("Kicked timer");
    }

    public void init() {
        Events.ON_GAME_MESSAGE.register(text -> {
            if (!ExtraOptions.enableKickedTimer || isKicked) return false;
            String string = text.getString();
            if (string == null) return false;

            if (string.equals("You were kicked while joining that server!") || string.equals("You are no longer allowed to access this instance!")) {
                kickedTime = System.currentTimeMillis();
                isKicked = true;
            }

            return false;
        });
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return ExtraOptions.enableKickedTimer;
    }

    @Override
    public boolean shouldRender() {
        return isKicked;
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        long diff = System.currentTimeMillis() - kickedTime;
        if (diff > 60 * 1000) isKicked = false;
        double drawnTime = Math.min(diff / 1000.0, 60.0);
        RenderUtils.drawPrefixedTimer(this, guiGraphicsExtractor, "Time kicked", drawnTime);
    }

}
