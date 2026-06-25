package blade.addon.features.dungeon;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarpCooldown {

    private static final int WARP_COOLDOWN_MS = 30 * 1000;
    private static final Pattern PATTERN = Pattern.compile("^-*\\n\\[[^]]+] (\\w+) entered (?:MM )?\\w+ Catacombs, Floor (\\w+)!\\n-*$");

    private static long endTime = 0;
    private static boolean displayCooldown = false;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(message -> {
            if (!Dungeons.enableWarpCooldown) return false;

            if ((endTime - System.currentTimeMillis()) > 0) return false;

            Matcher matcher = PATTERN.matcher(message.getString());
            if (matcher.find()) {
                endTime = System.currentTimeMillis() + WARP_COOLDOWN_MS;
                displayCooldown = true;
            }
            return false;
        });
    }

    public static boolean display()  {
        if (!Dungeons.enableWarpCooldown || !displayCooldown) return false;

        if ((endTime - System.currentTimeMillis()) > 0) {
            return true;
        } else {
            displayCooldown = false;
            return false;
        }
    }

    public static void render(HUDComponent component, GuiGraphicsExtractor graphics) {
        double timeRemaining = Math.max((endTime - System.currentTimeMillis()) / 1000.0, 0);
        RenderUtils.drawPrefixedTimer(component, graphics, "Warp Cooldown", timeRemaining);
    }
}
