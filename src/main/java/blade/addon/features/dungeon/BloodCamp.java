package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BloodCamp {

    private static final Pattern PATTERN = Pattern.compile("^\\[BOSS] The Watcher: Let's see how you can handle this\\.$");

    private static int ticks = 0;
    private static int countDownTicks = 0;
    private static boolean countDown = false;
    private static boolean countDownOver = false;


    //TODO: fix this

    public static void init() {
        return;
        /*Events.ON_SERVER_TICK.register(() -> {
            if (!Location.inDungeon()) return;
            if (!Phase.inClear() || !Phase.rushDone()) return;

            if (!countDown) {
                ticks++;
            } else if (!countDownOver) {
                countDownTicks--;
                if (countDownTicks <= 0) {
                    countDownOver = true;
                }
            }
        });
 */
    }

    public static void parseMessage(Text message) {
       /*
        if (!Location.inDungeon()) return;
        if (!Phase.inClear() || !Phase.rushDone()) return;
        if (countDown) return;

        Matcher matcher = PATTERN.matcher(message.getString());
        if (matcher.matches()) {
            countDown = true;
            countDownTicks = 50;
        }
 */
    }

    public static void reset() {
        ticks = 0;
        countDown = false;
        countDownOver = false;
    }
/*
    @ConfigValue
    public static HUDComponent bloodTickDown = new HUDComponent(0, 0, 30, 10, 1,
            () -> countDown && !countDownOver,
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                double num = countDownTicks * Constants.TICK_DURATION;

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, Constants.DECIMAL_FORMAT.format(num), x, y, 0xffffffff, true);
            })
    ); */
}
