package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;
import config.practical.manager.ConfigValue;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Split {

    public enum TimerType {
        TICK_TIME("Tick time"), DIFFRENCE("difference");

        private final String label;

        TimerType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static final int SPLIT_LENGTH = 165;

    public static final int GREEN = 5635925;
    public static final int GRAY = 11184810;
    public static final int DARK_GRAY = 5592405;

    @ConfigValue
    public static int realTimeColorInactive = GREEN;
    @ConfigValue
    public static int realTimeColorOngoing = GREEN;
    @ConfigValue
    public static int realTimeColorComplete = GREEN;

    @ConfigValue
    public static int serverTimeColorInactive = GRAY;
    @ConfigValue
    public static int serverTimeColorOngoing = GRAY;
    @ConfigValue
    public static int serverTimeColorComplete = GRAY;

    @ConfigValue
    public static int parenthesesColorInactive = DARK_GRAY;
    @ConfigValue
    public static int parenthesesColorOngoing = DARK_GRAY;
    @ConfigValue
    public static int parenthesesColorComplete = DARK_GRAY;

    @ConfigValue
    public static TimerType timerType = TimerType.TICK_TIME;

    private final String name, startString, endString;
    private final int color;
    private int tick;
    private long startTime, endTime;
    private boolean started, ended;

    public Split(String name, String startString, String endString, int color) {
        this.name = name;
        this.startString = startString;
        this.endString = endString;
        this.color = color;
        this.tick = 0;
        this.ended = false;
    }

    public void parseMessage(String string) {
        if (!started) {
            if (startString.equals(string)) {
                start();
            }
        } else if (!ended) {
            if (endString.equals(string)) {
                end();
            }
        }
    }

    public void tick() {
        if (started && !ended) {
            this.tick++;
        }
    }

    public void reset() {
        tick = 0;
        ended = false;
        started = false;
    }

    public void end() {
        if (ended) return;
        endTime = System.currentTimeMillis();
        started = false;
        ended = true;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        started = true;
    }

    public boolean started() {
        return started;
    }

    public boolean ended() {
        return ended;
    }

    public double getTickTime() {
        return tick * Constants.TICK_DURATION;
    }

    public double getRealTime() {
        double realTime;
        if (ended) {
            realTime = (endTime - startTime) / 1000.0;
        } else if (started) {
            realTime = (System.currentTimeMillis() - startTime) / 1000.0;
        } else {
            realTime = 0;
        }
        return realTime;
    }

    public MutableText createNameText() {
        return Text.literal(name + " ").withColor(color);
    }

    public double getTimeDiffrence() {
        return getRealTime() - getTickTime();
    }

    public MutableText createTimeText() {
        int realTimeColor, serverTimeColor, parenthesesColor;

        if (!started) {
            realTimeColor = realTimeColorInactive;
            serverTimeColor = serverTimeColorInactive;
            parenthesesColor = parenthesesColorInactive;
        } else if (!ended) {
            realTimeColor = realTimeColorOngoing;
            serverTimeColor = serverTimeColorOngoing;
            parenthesesColor = parenthesesColorOngoing;
        } else {
            realTimeColor = realTimeColorComplete;
            serverTimeColor = serverTimeColorComplete;
            parenthesesColor = parenthesesColorComplete;
        }

        double tickTime = getTickTime();
        double realTime = getRealTime();

        String serverTime;
        if (timerType == TimerType.DIFFRENCE) {
            double diff = realTime - tickTime;
            if (diff > 0) {
                serverTime = "+" + Constants.DECIMAL_FORMAT.format(diff) + "s ";
            } else {
                serverTime = Constants.DECIMAL_FORMAT.format(diff) + "s ";
            }
        } else {
            //default to tick timer
            serverTime = Constants.DECIMAL_FORMAT.format(tickTime) + "s";
        }

        String realTimeString = (realTime >= 60? (int)(realTime / 60) + "m ": "") + Constants.DECIMAL_FORMAT.format(realTime % 60) + "s";
        return Text.literal(realTimeString).withColor(realTimeColor)
                .append(Text.literal(" (").withColor(parenthesesColor)
                        .append(Text.literal(serverTime).withColor(serverTimeColor))
                        .append(Text.literal(")").withColor(parenthesesColor)
                        ));
    }

    public void drawSplit(DrawContext context, TextRenderer textRenderer, int x, int y) {
        Text nameText = createNameText();
        Text timerText = createTimeText();

        int timerWidth = textRenderer.getWidth(timerText);
        context.drawText(textRenderer, nameText, x, y, 0xffffffff, true);
        context.drawText(textRenderer, timerText, x + SPLIT_LENGTH - timerWidth, y, 0xffffffff, true);
    }
}
