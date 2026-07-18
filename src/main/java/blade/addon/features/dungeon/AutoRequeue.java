package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoRequeue {

    private static final Pattern LEFT_PATTERN = Pattern.compile("has left the party.$");

    private static final HashMap<String, String> playerHashMap = new HashMap<>();


    private static boolean someoneLeft = false;

    public static void init() {
        Events.ON_PARTY_MESSAGE.register((username, message) -> {
            if (playerHashMap.containsKey(username)) {
                if (message.contains("!undt")) {
                    playerHashMap.remove(username);
                    Misc.addChatMessage(Component.literal("§aDowntime removed"));
                }
            } else {
                if (message.contains("!dt")) {
                    playerHashMap.put(username, message);
                    Misc.addChatMessage(Component.literal("§aDowntime added"));
                }
            }
            return false;
        });

        Events.ON_GAME_MESSAGE.register(message -> {
            if (!Location.inDungeon() && !Dungeons.enableAutoRequeue) return false;
            String string = message.getString();
            Matcher matcher = LEFT_PATTERN.matcher(string);
            if (matcher.find()) {
                someoneLeft = true;
            }
            return false;
        });

        Events.ON_RUN_END.register(() -> {
            if (!Dungeons.enableAutoRequeue || someoneLeft) return false;

            if (playerHashMap.isEmpty()) {
                Misc.executeCommand("instancerequeue");
            } else {
                Misc.addChatMessage(Component.literal("Downtime reasons:"));
                playerHashMap.forEach((name, string) -> Misc.addChatMessage((Component.literal(name + "> " + string))));


            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            playerHashMap.clear();
            someoneLeft = false;
            return false;
        });
    }
}
