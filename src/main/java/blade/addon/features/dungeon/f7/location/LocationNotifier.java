package blade.addon.features.dungeon.f7.location;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationNotifier extends HUDComponent {

    private static final Pattern PATTERN = Pattern.compile("^(At |Inside )");

    private Component notification = Component.literal("§6Username is At Location!");
    private int ticks = 0;

    public LocationNotifier() {
        super("At location display");
    }

    public void init() {
        Events.ON_PARTY_MESSAGE.register((username, message) -> {
            if (!Floor7.displayLocationNotification || !Location.inDungeon()) return false;
            Matcher matcher = PATTERN.matcher(format(message));
            if (!matcher.find()) return false;
            String action = matcher.group(1);
            String location = message.substring(message.indexOf(action) + action.length()).replaceAll("§.", "");
            if (PositionMessages.hasBeenSent(location)) return false;
            if (Floor7.dontNotifiyForYourself && EntityUtil.isClientPlayer(username)) return false;
            startNotification(username, " is " + action + location + "!");

            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(_ -> ticks = Math.max(ticks - 1, 0));
    }

    @Override
    public int getWidth() {
        return 200;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public boolean editable() {
        return Floor7.displayLocationNotification;
    }

    @Override
    public boolean shouldRender() {
         return ticks > 0 && Floor7.displayLocationNotification;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P3);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawCenteredText(guiGraphicsExtractor, this, notification);
    }

    private String format(String message) {
        String strippedMessage = message.strip();
        if (strippedMessage.length() < 2) return message;
        return strippedMessage.substring(0, 1).toUpperCase() + strippedMessage.substring(1).toLowerCase();
    }

    public void startNotification(String username, String message) {
        int color = Constants.GOLD;
        if (Dungeons.useClassColors) {
            color = DungeonClass.getColor(username);
        }

        notification = Component.literal(username).withColor(color).append(Component.literal("§e" + message).setStyle(Style.EMPTY));
        ticks = Floor7.notificationDuration;

        for (int i = 0; i < Floor7.notificationRepetitions; i++) {
            Scheduler.scheduleSound(Floor7.atLocationSound, i + 1);
        }
    }
}
