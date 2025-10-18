package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class GoldorTickTimer {

    private static final int GREEN_COLOR = 0xff00ff00;
    private static final int ORANGE_COLOR = 0xffDAA06D;
    private static final int RED_COLOR = 0xffff0000;

    private static final String START_DIALOGUE = "[BOSS] Goldor: Who dares trespass into my domain?";
    private static final String END_DIALOGUE = "The Core entrance is opening!";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00.00");
    private static final double TICK_DURATION = 0.05;

    @ConfigValue
    public static boolean enableGoldorTickTimer = false;

    @ConfigValue
    public static boolean inDeathTicks = true;

    private static boolean inP3 = false;
    private static int tick = 0;

    public static void tick(MinecraftClient client) {
        if (Location.inDungeon() && inP3) tick++;
    }

    public static void parseString(Text message) {
        if (message.getString().contains(START_DIALOGUE)) inP3 = true;
        if (message.getString().contains(END_DIALOGUE)) inP3 = false;
    }

    @ConfigValue
    public static HUDComponent goldorTickTimer = new HUDComponent(0, 0, 30, 10, 1,
            () -> enableGoldorTickTimer && Location.inDungeon() && inP3,
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                int color = 0xffffffff;
                double num = tick * TICK_DURATION;

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

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, DECIMAL_FORMAT.format(num).replace(".", ":"), x, y, color, true);




            })
    );
}
