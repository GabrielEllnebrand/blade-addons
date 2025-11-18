package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestCounter {

    private static final Pattern PATTERN = Pattern.compile("^ Unclaimed chests: (\\d+)$");

    @ConfigValue
    public static boolean displayChestCount = false;

    @ConfigValue
    public static boolean sendChestWarning = false;

    @ConfigValue
    public static int chestWarningCount = 55;

    private static int chestDisplayCount = 0;
    private static int countedChests = 0;
    private static boolean hasUpdatedData = false;
    private static boolean readPlayerEntryEvents = false;

    public static void init() {
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.in(Location.DUNGEON_HUB)) {
                readPlayerEntryEvents = true;
                hasUpdatedData = false;
            }
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (!readPlayerEntryEvents || hasUpdatedData) return;
            if (receivedEntry == null) return;
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
            if (sendChestWarning && chestDisplayCount + countedChests >= chestWarningCount) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2, 1);
                }
                MinecraftClient.getInstance().inGameHud.setTitle(Text.literal("Chest count reached").withColor(0xffff0000));
            }
        });
    }

    @ConfigValue
    public static HUDComponent chestCounter = new HUDComponent(0, 0, 100, 10, 1, "Chest count",
            () -> displayChestCount && Location.inDungeon(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                if (hasUpdatedData) {
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("Chests: " + Math.min(chestDisplayCount + countedChests, 60)), x, y, 0xffffffff, true);
                } else if(readPlayerEntryEvents) {
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("Updating"), x, y, 0xffffd700, true);
                } else {
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("go to the dungeon hub"), x, y, 0xffff0000, true);

                }
            })
    );


}
