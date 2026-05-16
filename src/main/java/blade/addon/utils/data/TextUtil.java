package blade.addon.utils.data;

import blade.addon.utils.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

public class TextUtil {

    static class StyleTracker {
        boolean isBold = false;
        boolean isItalic = false;
        boolean isUnderlined = false;
        boolean isStrikeThrough = false;
        boolean isObfuscated = false;
        int currentColor;

        public StyleTracker() {}

        @Override
        public String toString() {
            return "StyleTracker{" +
                    "isBold=" + isBold +
                    ", isItalic=" + isItalic +
                    ", isUnderlined=" + isUnderlined +
                    ", isStrikeThrough=" + isStrikeThrough +
                    ", isObfuscated=" + isObfuscated +
                    ", currentColor=" + currentColor +
                    '}';
        }
    }

    public static String orderedTextToString(FormattedCharSequence text) {
        StringBuilder builder = new StringBuilder();
        acceptOrderedText(builder, text);
        return builder.toString();
    }

    public static void acceptOrderedText(StringBuilder builder, FormattedCharSequence orderedText) {
        StyleTracker tracker = new StyleTracker();
        acceptOrderedText(builder, tracker, orderedText);
    }

    private static void acceptOrderedText(StringBuilder builder, StyleTracker tracker, FormattedCharSequence orderedText) {
        orderedText.accept((index, style, codePoint) -> {
            acceptStyle(builder, tracker, style);
            builder.appendCodePoint(codePoint);
            return true;
        });
    }

    /**
     * Grabs the color codes that are missing to
     * make it more convenient to use it in for example
     * chat notifications
     * @param builder StringBuilder
     * @param tracker StyleTracker, keeps track of previous style
     * @param style Style, the style of the current char
     */
    private static void acceptStyle(StringBuilder builder, StyleTracker tracker, Style style) {
        if (style == null) return;

        TextColor color = style.getColor();
        if (color != null && color.getValue() != tracker.currentColor) {
            builder.append('§');
            builder.append(getFormatChar(color.getValue()));
            tracker.currentColor = color.getValue();
        }

        if (style.isObfuscated() &&  !tracker.isObfuscated) {
            builder.append("§k");
            tracker.isObfuscated = true;
        }  else if (!style.isObfuscated() && tracker.isObfuscated) {
            tracker.isObfuscated = false;
        }

        if (style.isBold() && !tracker.isBold) {
            builder.append("§l");
            tracker.isBold = true;
        } else if (!style.isBold() && tracker.isBold) {
            tracker.isBold = false;
        }

        if (style.isStrikethrough() && !tracker.isStrikeThrough) {
            builder.append("§m");
            tracker.isStrikeThrough = true;
        }  else if (!style.isStrikethrough() && tracker.isStrikeThrough) {
            tracker.isStrikeThrough = false;
        }

        if (style.isUnderlined() && !tracker.isUnderlined) {
            builder.append("§n");
            tracker.isUnderlined = true;
        }  else if (!style.isUnderlined() && tracker.isUnderlined) {
            tracker.isUnderlined = false;
        }

        if (style.isItalic() && !tracker.isItalic) {
            builder.append("§o");
            tracker.isItalic = true;
        } else if (!style.isItalic() && tracker.isItalic) {
            tracker.isItalic = false;
        }
    }

    private static char getFormatChar(int color) {
        for (ChatFormatting format: ChatFormatting.values()) {
            Integer colorValue = format.getColor();
            if (colorValue == null) continue;

            if (colorValue == color) {
                return format.getChar();
            }
        }

        return '0';
    }

    public static String formatTicks(int tick) {
       return Constants.DECIMAL_FORMAT.format(tick * Constants.TICK_DURATION);
    }

    public static String capitaliseFirst(String message) {
        String strippedMessage = message.strip();
        if (strippedMessage.length() < 2) return message;
        return strippedMessage.substring(0, 1).toUpperCase() + strippedMessage.substring(1).toLowerCase();
    }
}
