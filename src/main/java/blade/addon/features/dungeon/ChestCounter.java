package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestCounter {

    private static final Pattern PATTERN = Pattern.compile("^ Unclaimed chests: (\\d+)$");

    private static int chestDisplayCount = 0;
    private static int countedChests = 0;
    private static boolean hasUpdatedData = false;

    public static void init() {
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.in(Location.DUNGEON_HUB)) {
                hasUpdatedData = false;
            }
            return false;
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (receivedEntry == null || !Location.in(Location.DUNGEON_HUB)) return false;
            Text text = receivedEntry.displayName();
            if (text == null) return false;

            String string = text.getString();
            Matcher matcher = PATTERN.matcher(string);
            if (matcher.find()) {
                try {
                    chestDisplayCount = Integer.parseInt(matcher.group(1));
                    countedChests = 0;
                    hasUpdatedData = true;
                } catch (NumberFormatException ignored) {
                }
            }
            return false;
        });

        Events.ON_RUN_END.register(() -> {
            countedChests++;
            if (Dungeons.sendChestWarning && chestDisplayCount + countedChests >= Dungeons.chestWarningCount) {
                Scheduler.scheduleSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2, 1);
                Misc.setTitle(Text.literal("§cChest count reached"));
            }

            return false;
        });
    }

    public static boolean display() {
        if ((Dungeons.onlyAfterRunOver && !Phase.runOver())) return false;
        return Dungeons.displayChestCount && Location.inDungeon();
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        if (hasUpdatedData) {
            RenderUtils.drawPrefixedText(component, context, "Chests", Math.min(chestDisplayCount + countedChests, 60) + "");
        } else {
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("go to the dungeon hub"), x, y, Constants.RED_COLOR, true);

        }
    }
}
