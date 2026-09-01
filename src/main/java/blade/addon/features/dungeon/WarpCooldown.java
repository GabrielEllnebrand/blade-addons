package blade.addon.features.dungeon;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarpCooldown extends HUDComponent {

    private static final int INSTANCE_COOLDOWN_MS = 30 * 1000;
    private static final int WARP_COOLDOWN_MS = 4 * 1000;

    private static final Pattern INSTANCE_PATTERN = Pattern.compile("^-*\\n\\[[^]]+] (\\w+) entered (?:MM )?\\w+ Catacombs, Floor (\\w+)!\\n-*$");
    private static final Pattern WARP_PATTERN = Pattern.compile("^Sending to server .+");

    private static long endTime = 0;
    private static boolean displayCooldown = false;

    public WarpCooldown() {
        super("Warp cooldown");
    }

    public void init() {
        Events.ON_GAME_MESSAGE.register(message -> {
            if (!Dungeons.enableWarpCooldown) return false;

            if ((endTime - System.currentTimeMillis()) > 0) return false;

            Matcher matcher = INSTANCE_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                setCooldown(INSTANCE_COOLDOWN_MS);
                return false;
            }

            matcher = WARP_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                setCooldown(WARP_COOLDOWN_MS);
            }


            return false;
        });
    }

    private void setCooldown(int duration) {
        displayCooldown = true;
        long newEndTime = System.currentTimeMillis() + duration;
        if (newEndTime < endTime) return;
        endTime = newEndTime;

    }

    @Override
    public int getWidth() {
        return 110;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Dungeons.enableWarpCooldown;
    }

    @Override
    public boolean shouldRender() {
        if (!Dungeons.enableWarpCooldown || !displayCooldown) return false;

        if ((endTime - System.currentTimeMillis()) > 0) {
            return true;
        } else {
            displayCooldown = false;
            return false;
        }
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        double timeRemaining = Math.max((endTime - System.currentTimeMillis()) / 1000.0, 0);
        RenderUtils.drawPrefixedTimer(this, guiGraphicsExtractor, "Warp Cooldown", timeRemaining);
    }
}
