package blade.addon.features.notifications;

import blade.addon.utils.Misc;
import blade.addon.utils.config.FolderUtility;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import config.practical.data.SoundData;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Notifications {

    private static final String filePath = FolderUtility.CONFIG_PATH + FolderUtility.NOTIFICATIONS_NAME;
    private static final String NOTIFICATIONS_NAME = "notifications";

    @ConfigValue
    public static CopyOnWriteArrayList<Notification> notifications;

    private static double tick = 0;
    private static String message;

    public static void init() {
        notifications = new CopyOnWriteArrayList<>();
        load();
        Events.ON_GAME_MESSAGE.register(text -> {
            String message = text.getString();

            if (ExtraOptions.ignoreColorCodesNotification) {
                message = message.replaceAll("§.", "");
            }

            if (Debug.sendNotiDebug) {
                Misc.addChatMessage(Component.literal(message.replaceAll("§", "&")));
            }

            for (Notification notification : notifications) {
                notification.testMessage(message);
            }

            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(event -> tick = Math.max(tick - 1, 0));
    }

    public static void addNotification(Notification notification) {
        if (notifications == null) return;
        notifications.add(notification);
    }

    public static void removeNotification(Notification notification) {
        if (notifications == null) return;
        notifications.remove(notification);
    }

    public static List<Notification> getNotifications() {
        return notifications;
    }

    public static boolean display() {
        return tick > 0;
    }

    public static void render(HUDComponent component, GuiGraphics graphics) {
        Component text = Component.literal(message != null ? message : "Some notification");
        RenderUtils.drawCenteredText(graphics, component, text);
    }

    public static void setNotification(Notification notification) {
        if (notifications == null) return;
        tick = notification.getTicks();
        message = notification.getNotificationString();
    }

    public static void save() {

        JsonObject obj = new JsonObject();
        Gson gson = new Gson();

        obj.add(NOTIFICATIONS_NAME, gson.toJsonTree(notifications));

        JsonElement tree = gson.toJsonTree(obj);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(tree.toString());
        } catch (IOException ignored) {

        }
    }

    public static void load() {
        String jsonContent;
        try {
            jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ignored) {
            return;
        }
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(jsonContent, JsonObject.class);

        JsonElement element;
        if (object.has("waypoints")) {
            //backwards compatibility because I forgot to change the name
            //before release...
            element = object.get("waypoints");
        } else {
            element = object.get(NOTIFICATIONS_NAME);
        }

        if (!element.isJsonArray()) return;
        JsonArray array = element.getAsJsonArray();

        for (JsonElement notificationElement : array) {
            JsonObject notification = notificationElement.getAsJsonObject();

            try {

                if (!notification.has("matchString")) continue;
                String matchString = notification.get("matchString").getAsString();

                if (!notification.has("notificationString")) continue;
                String notificationString = notification.get("notificationString").getAsString();

                if (!notification.has("command")) continue;
                String command = notification.get("command").getAsString();

                if (!notification.has("useRegex")) continue;
                boolean useRegex = notification.get("useRegex").getAsBoolean();

                if (!notification.has("sendCommand")) continue;
                boolean sendCommand = notification.get("sendCommand").getAsBoolean();

                if (!notification.has("enabled")) continue;
                boolean enabled = notification.get("enabled").getAsBoolean();

                if (!notification.has("ticks")) continue;
                int ticks = notification.get("ticks").getAsInt();

                if (!notification.has("sound")) continue;
                SoundData sound = loadSoundData(notification.get("sound"));
                if  (sound == null) continue;

                notifications.add(new Notification(matchString, notificationString, command, useRegex, sendCommand, enabled, ticks, sound));
            } catch (NumberFormatException e) {
                System.out.println("msg: " + e.getMessage());
            }
        }
    }

    public static SoundData loadSoundData(JsonElement jsonElement) {
        JsonObject soundData = jsonElement.getAsJsonObject();

        if (!soundData.has("sound")) return null;
        String sound = soundData.get("sound").getAsString();

        if (!soundData.has("volume")) return null;
        float volume = soundData.get("volume").getAsFloat();

        if (!soundData.has("pitch")) return null;
        float pitch = soundData.get("pitch").getAsFloat();

        return new SoundData(Identifier.parse(sound), volume, pitch);
    }
}
