package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarpCooldown {

    private static final int WARP_COOLDOWN_MS = 30 * 1000;
    private static final Pattern PATTERN = Pattern.compile("^-*\\n\\[[^]]+] (\\w+) entered (?:MM )?\\w+ Catacombs, Floor (\\w+)!\\n-*$");

    private static long endTime = 0;
    private static boolean displayCooldown = false;

    @ConfigValue
    public static boolean enableWarpCooldown = false;

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!enableWarpCooldown) return;

            Matcher matcher = PATTERN.matcher(message.getString());
            if (matcher.find()) {
                endTime = System.currentTimeMillis() + WARP_COOLDOWN_MS;
                displayCooldown = true;
            }
        });
    }

    @ConfigValue
    public static HUDComponent warpCoolDown = new HUDComponent(0, 0, 110, 10, 0.75f, "Warp cooldown",
            () -> {
                if (!enableWarpCooldown || !displayCooldown) return false;

                if ((endTime - System.currentTimeMillis()) > 0) {
                    return true;
                } else {
                    displayCooldown = false;
                    return false;
                }
            },
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double timeRemaining = Math.max((endTime - System.currentTimeMillis()) / 1000.0, 0);

                Text text = Text.literal("Warp Cooldown: ").formatted(Formatting.DARK_PURPLE).append(Text.literal(Constants.DECIMAL_FORMAT.format(timeRemaining)).formatted(Formatting.WHITE));

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xffffffff, true);
            })
    );
}
