package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Split {

    public static final int SPLIT_LENGTH = 150;

    private final String name;
    private final Pattern pattern;
    private final int color;
    private int tick;
    private long startTime, endTime;
    private boolean started, ended;

    public Split(String name, String dialogue, int color) {
        this.name = name;
        //json removes regex so gotta add them manually after
        String regex = "^".concat(dialogue.replaceAll("\\.", "\\\\.").replaceAll("\\?", "\\\\?").replaceAll("\\[", "\\\\[").concat("$"));
        this.pattern = Pattern.compile(regex);
        this.color = color;
        this.tick = 0;
        this.ended = false;
    }

    public boolean matches(String string) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            startTime = System.currentTimeMillis();
            started = true;
            return true;
        }
        return false;
    }

    public void tick() {
        this.tick++;
    }

    public void reset() {
        tick = 0;
        ended = false;
        started = false;
    }

    public void end() {
        endTime = System.currentTimeMillis();
        started = false;
        ended = true;
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

        Text nameText = Text.literal(name + " ").setStyle(Style.EMPTY.withColor(color));
        Text timerText = Text.literal(Constants.DECIMAL_FORMAT.format(realTime) + "s ").setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                        .append(Text.literal("(").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY))
                                .append(Text.literal(Constants.DECIMAL_FORMAT.format(tickTime) + "s").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
                                        .append(Text.literal(")").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY))
                                        )));

        int timerWidth = textRenderer.getWidth(timerText);
        context.drawText(textRenderer, nameText, x, y, 0xffffffff, true);
        context.drawText(textRenderer, timerText, x + SPLIT_LENGTH - timerWidth, y, 0xffffffff, true);
    }
}
