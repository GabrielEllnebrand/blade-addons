package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;

public class GoldorTickTimer {

    private static final int GREEN_COLOR = 0xff00ff00;
    private static final int ORANGE_COLOR = 0xffDAA06D;
    private static final int RED_COLOR = 0xffff0000;

    @ConfigValue
    public static boolean enableGoldorTickTimer = false;

    @ConfigValue
    public static boolean inDeathTicks = true;
    private static int tick = 0;

    public static void tick(MinecraftClient client) {
        if (Location.inDungeon() && Phase.inP3()) tick++;
    }

    @ConfigValue
    public static HUDComponent goldorTickTimer = new HUDComponent(0, 0, 30, 10, 1,
            () -> enableGoldorTickTimer && Location.inDungeon() && Phase.inP3(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                int color = 0xffffffff;
                double num = tick * Constants.TICK_DURATION;

                if (inDeathTicks) {
                    num = num % 3;
                    if (num < 1) {
                        color = GREEN_COLOR;
                    } else if (num < 2) {
                        color = ORANGE_COLOR;
                    } else {
                        color = RED_COLOR;
                    }
                }

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, Constants.DECIMAL_FORMAT.format(num), x, y, color, true);




            })
    );
}
