package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;
import config.practical.manager.ConfigValue;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final String name;
    private final Pattern startPattern, endPattern;
    private final int color;
    private int tick;
    private long startTime, endTime;
    private boolean started, ended;

    public Split(String name, String start, String end, int color) {
        this.name = name;
        //json removes regex so gotta add them manually after
        String startRegex = "^".concat(start.replaceAll("\\.", "\\\\.").replaceAll("\\?", "\\\\?").replaceAll("\\[", "\\\\[").concat("$"));
        String endRegex = "^".concat(end.replaceAll("\\.", "\\\\.").replaceAll("\\?", "\\\\?").replaceAll("\\[", "\\\\[").concat("$"));
        this.startPattern = Pattern.compile(startRegex);
        this.endPattern = Pattern.compile(endRegex);
        this.color = color;
        this.tick = 0;
        this.ended = false;
    }

    public void parseMessage(String string) {
        if (!started) {
            Matcher matcher = startPattern.matcher(string);
            if (matcher.matches()) {
                start();
            }
        } else if (!ended) {
            Matcher matcher = endPattern.matcher(string);
            if (matcher.matches()) {
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

    private Text createText(double realTime, double tickTime) {
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

    public void drawSplit(DrawContext context, TextRenderer textRenderer, long currentTime, int x, int y) {
        double tickTime = tick * Constants.TICK_DURATION;

        double realTime;
        if (ended) {
            realTime = (endTime - startTime) / 1000.0;
        } else if (started) {
            realTime = (currentTime - startTime) / 1000.0;
        } else {
            realTime = 0;
        }

        Text nameText = Text.literal(name + " ").withColor(color);
        Text timerText = createText(realTime, tickTime);

        int timerWidth = textRenderer.getWidth(timerText);
        context.drawText(textRenderer, nameText, x, y, 0xffffffff, true);
        context.drawText(textRenderer, timerText, x + SPLIT_LENGTH - timerWidth, y, 0xffffffff, true);
    }
}
