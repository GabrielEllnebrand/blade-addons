package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecretSpawnTimer {

    private static final Pattern PATTERN = Pattern.compile("^Time Elapsed: ");
    private static final int TICKS_PER_SECOND = 20;
    private static final int WIDTH = 20;

    @ConfigValue
    public static boolean enableSecretSpawnTimer = false;

    private static int tick = 0;

    public static void init() {

        Events.ON_TEAM.register(text -> {
            if (!Location.inDungeon() || !enableSecretSpawnTimer) return;

            Matcher matcher = PATTERN.matcher(text);
            if (matcher.find()) {
                tick = TICKS_PER_SECOND;
            }
        });

        Events.ON_SERVER_TICK.register(() -> tick = Math.max(tick - 1, 0));
    }

    @ConfigValue
    public static HUDComponent secretSpawnTimer = new HUDComponent(0, 0, WIDTH, 10, 1, "Secret spawn timer",
            () -> Location.inDungeon() && enableSecretSpawnTimer && !Phase.inBoss() && Phase.runStarted(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                int color;
                if (tick > 10) {
                    color = Constants.GREEN_COLOR;
                } else if (tick > 5) {
                    color = Constants.ORANGE_COLOR;
                } else {
                    color = Constants.RED_COLOR;
                }

                RenderUtils.drawCenteredText(drawContext, MinecraftClient.getInstance().textRenderer, Text.literal(tick + ""), x, y, WIDTH, color);

            }), () -> enableSecretSpawnTimer
    );
}
