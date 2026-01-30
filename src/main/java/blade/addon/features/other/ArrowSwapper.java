package blade.addon.features.other;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrowSwapper {

    private static final int TOTAL_TICK = 20;

    private static final Pattern ARROW_PATTERN = Pattern.compile("^You set your selected arrow type to (.+)!$");
    private static final Text NOT_SCANNED_TEXT = Text.literal("Cant find arrow").formatted(Formatting.RED);

    private static Text displayedText = NOT_SCANNED_TEXT;
    private static int tick = 0;

    public static void init() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            tick = Math.max(0, tick - 1);
        });

        Events.ON_GAME_MESSAGE.register(text -> {
            String string = text.getString();

            Matcher matcher = ARROW_PATTERN.matcher(string);
            if (!matcher.find()) return false;

            List<Text> siblings = text.getSiblings();
            if (siblings.size() < 2) return false;
            setDisplayedText(siblings.get(1));

            return false;
        });

        Events.ON_SLOT_CHANGE.register((slot, item) -> {
            if (displayedText != NOT_SCANNED_TEXT) return false;
            if (!ItemUtil.itemHasName(item, "Arrow Swapper")) return false;

            if (item ==null) return false;
            LoreComponent lore = item.get(DataComponentTypes.LORE);
            if (lore == null) return false;

            List<Text> lines = lore.lines();
            if (lines.isEmpty()) return false;

            Pattern p = Pattern.compile("^Selected: (.+)$");

            for (Text line : lines) {
                Matcher matcher = p.matcher(line.getString());
                if (matcher.find()) {
                    List<Text> foundLine = line.getSiblings();
                    if (foundLine.size() < 2) return false;
                    setDisplayedText(foundLine.get(1));
                    break;
                }
            }
            return false;
        });
    }

    private static void setDisplayedText(Text text) {
        if (displayedText != NOT_SCANNED_TEXT) {
            tick = TOTAL_TICK;
        }
        displayedText = text;
    }

    public static boolean display() {
        return ExtraOptions.displayCurrentArrow;
    }

    public static void render(HUDComponent component, DrawContext context) {
        RenderUtils.drawText(context, component, displayedText, 0xffffffff);
    }

    public static boolean displayNotification() {
        return ExtraOptions.arrowSwapNotification && tick > 0;
    }

    public static void renderNotification(HUDComponent component, DrawContext context) {
        RenderUtils.drawCenteredText(context, component, displayedText, 0xffffffff);
    }
}
