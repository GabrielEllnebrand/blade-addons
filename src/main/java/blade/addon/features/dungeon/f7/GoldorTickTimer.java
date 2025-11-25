package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class GoldorTickTimer {

    private static final int WIDTH = 30;

    @ConfigValue
    public static boolean enableGoldorTickTimer = false;

    @ConfigValue
    public static boolean inDeathTicks = true;
    private static int tick = 0;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Location.inDungeon() && Phase.inTerminals()) tick++;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                tick = 0;
            }
        });
    }

    @ConfigValue
    public static HUDComponent goldorTickTimer = new HUDComponent(0, 0, WIDTH, 10, 1, "Goldor Tick Timer",
            () -> enableGoldorTickTimer && Location.inDungeon() && Phase.inTerminals(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                int color;
                double num = tick * Constants.TICK_DURATION;

                double mod = num % 3;
                if (mod < 1) {
                    color = Constants.GREEN_COLOR;
                } else if (mod < 2) {
                    color = Constants.ORANGE_COLOR;
                } else {
                    color = Constants.RED_COLOR;
                }

                if (inDeathTicks) num = mod;

                RenderUtils.drawCenteredText(drawContext, MinecraftClient.getInstance().textRenderer, Text.literal(Constants.DECIMAL_FORMAT.format(num)), x, y, WIDTH, color);

            }), () -> enableGoldorTickTimer
    );
}
