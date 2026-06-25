package blade.addon.features.other;

import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrowSwapper {

    private static final int TOTAL_TICK = 20;

    private static final Pattern ARROW_PATTERN = Pattern.compile("^You set your selected arrow type to (.+)!$");
    private static final Component NOT_SCANNED_TEXT = Component.literal("Cant find arrow").withStyle(ChatFormatting.RED);

    private static Component displayedText = NOT_SCANNED_TEXT;
    private static int tick = 0;

    public static void init() {
        ClientTickEvents.END_LEVEL_TICK.register(world -> {
            tick = Math.max(0, tick - 1);
        });

        Events.ON_GAME_MESSAGE.register(text -> {
            String string = text.getString();

            Matcher matcher = ARROW_PATTERN.matcher(string);
            if (!matcher.find()) return false;

            List<Component> siblings = text.getSiblings();
            if (siblings.size() < 2) return false;
            setDisplayedText(siblings.get(1));

            return false;
        });

        Events.ON_SLOT_CHANGE.register((slot, item) -> {
            if (displayedText != NOT_SCANNED_TEXT) return false;
            if (!ItemUtil.itemHasName(item, "Arrow Swapper")) return false;

            if (item ==null) return false;
            ItemLore lore = item.get(DataComponents.LORE);
            if (lore == null) return false;

            List<Component> lines = lore.lines();
            if (lines.isEmpty()) return false;

            Pattern p = Pattern.compile("^Selected: (.+)$");

            for (Component line : lines) {
                Matcher matcher = p.matcher(line.getString());
                if (matcher.find()) {
                    List<Component> foundLine = line.getSiblings();
                    if (foundLine.size() < 2) return false;
                    setDisplayedText(foundLine.get(1));
                    break;
                }
            }
            return false;
        });
    }

    private static void setDisplayedText(Component text) {
        if (displayedText != NOT_SCANNED_TEXT) {
            tick = TOTAL_TICK;
        }
        displayedText = text;
    }

    public static boolean display() {
        return ExtraOptions.displayCurrentArrow;
    }

    public static void render(HUDComponent component, GuiGraphicsExtractor graphics) {
        RenderUtils.drawText(graphics, component, displayedText, 0xffffffff);
    }

    public static boolean displayNotification() {
        return ExtraOptions.arrowSwapNotification && tick > 0;
    }

    public static void renderNotification(HUDComponent component, GuiGraphicsExtractor graphics) {
        RenderUtils.drawCenteredText(graphics, component, displayedText, 0xffffffff);
    }
}
