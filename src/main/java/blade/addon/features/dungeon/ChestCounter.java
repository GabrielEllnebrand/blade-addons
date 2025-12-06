package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
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
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (receivedEntry == null || !Location.in(Location.DUNGEON_HUB)) return;
            Text text = receivedEntry.displayName();
            if (text == null) return;

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
        });

        Events.ON_RUN_END.register(() -> {
            countedChests++;
            if (Dungeons.sendChestWarning && chestDisplayCount + countedChests >= Dungeons.chestWarningCount) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2, 1);
                }
                MinecraftClient.getInstance().inGameHud.setTitle(Text.literal("Chest count reached").withColor(0xffff0000));
            }
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
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("Chests: " + Math.min(chestDisplayCount + countedChests, 60)), x, y, 0xffffffff, true);
        } else {
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("go to the dungeon hub"), x, y, 0xffff0000, true);

        }
    }
}
