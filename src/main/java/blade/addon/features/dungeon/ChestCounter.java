package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestCounter extends HUDComponent {

    private static final Pattern PATTERN = Pattern.compile("^ Unclaimed chests: (\\d+)$");

    private static int chestDisplayCount = 0;
    private static int countedChests = 0;
    private static boolean hasUpdatedData = false;

    public ChestCounter() {
        super("Chest count");
    }

    public void init() {
        Events.ON_LOCATION_CHANGE.register(_ -> {
            if (Location.in(Location.DUNGEON_HUB)) {
                hasUpdatedData = false;
            }
            return false;
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (receivedEntry == null || !Location.in(Location.DUNGEON_HUB)) return false;
            Component text = receivedEntry.displayName();
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
                Scheduler.scheduleSound(SoundEvents.NOTE_BLOCK_PLING.value(), 2, 1);
                Misc.setTitle(Component.literal("§cChest count reached"));
            }

            return false;
        });
    }

    @Override
    public int getWidth() {
        return 110;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Dungeons.displayChestCount;
    }

    @Override
    public boolean shouldRender() {
        if ((Dungeons.onlyAfterRunOver && !Phase.runOver())) return false;
        return Dungeons.displayChestCount && Location.inDungeon();
    }

    @Override
    public List<HUDCategory> categories() {
        return null;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        int x = getScaledX();
        int y = getScaledY();

        if (hasUpdatedData) {
            RenderUtils.drawPrefixedText(this, guiGraphicsExtractor, "Chests", Math.min(chestDisplayCount + countedChests, 60) + "");
        } else {
            guiGraphicsExtractor.text(Minecraft.getInstance().font, Component.literal("go to the dungeon hub"), x, y, Constants.RED, true);

        }
    }
}
