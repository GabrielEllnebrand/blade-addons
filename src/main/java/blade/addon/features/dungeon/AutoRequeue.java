package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

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
                    Misc.addChatMessage(Text.literal("§aDowntime removed"));
                }
            } else {
                if (message.contains("!dt")) {
                    playerHashMap.put(username, message);
                    Misc.addChatMessage(Text.literal("§aDowntime added"));
                }
            }
            return false;
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() && !Dungeons.enableAutoRequeue) return;
            String string = message.getString();
            Matcher matcher = LEFT_PATTERN.matcher(string);
            if (matcher.find()) {
                someoneLeft = true;
            }
        });

        Events.ON_RUN_END.register(() -> {
            if (!Dungeons.enableAutoRequeue || someoneLeft) return false;

            if (playerHashMap.isEmpty()) {
                Misc.executeCommand("instancerequeue");
            } else {
                Misc.addChatMessage(Text.literal("Downtime reasons:"));
                playerHashMap.forEach((name, string) -> Misc.addChatMessage((Text.literal(name + "> " + string))));


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
