package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class StormTickTimer {

    private static final String START_DIALOGUE = "[BOSS] Storm: Pathetic Maxor, just like expected.";
    private static final String END_DIALOGUE = "[BOSS] Storm: I should have known that I stood no chance.";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00.00");
    private static final double TICK_DURATION = 0.05;

    @ConfigValue
    public static boolean enableStormTickTimer = false;

    private static boolean inP2 = false;
    private static int tick = 0;

    public static void tick(MinecraftClient client) {
        if (Location.inDungeon() && inP2) tick++;
    }

    public static void parseString(Text message) {
        if (message.getString().contains(START_DIALOGUE)) inP2 = true;
        if (message.getString().contains(END_DIALOGUE)) inP2 = false;
    }

    @ConfigValue
    public static HUDComponent stormTickTimer = new HUDComponent(0, 0, 30, 10, 1,
            () -> enableStormTickTimer && Location.inDungeon() && inP2,
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double num = tick * TICK_DURATION;

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, DECIMAL_FORMAT.format(num).replace(".", ":"), x, y, 0xffffffff, true);
            })
    );
}
