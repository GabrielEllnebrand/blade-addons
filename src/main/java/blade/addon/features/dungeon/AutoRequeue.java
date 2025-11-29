package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.events.Events;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoRequeue {

    private static final Pattern PATTERN = Pattern.compile("^§9Party §8>");
    private static final Pattern DT_PATTERN = Pattern.compile("!dt");
    private static final Pattern UNDT_PATTERN = Pattern.compile("!undt");
    private static final Pattern LEFT_PATTERN = Pattern.compile("has left the party.$");

    private static final int MESSAGE_OFFSET = 2;
    private static final int NO_RANK_OFFSET = 12;


    private static final HashMap<String, Text> playerHashMap = new HashMap<>();

    @ConfigValue
    public static boolean enableAutoRequeue = false;

    private static boolean someoneLeft = false;

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() && !enableAutoRequeue) return;
            String string = message.getString();
            Matcher matcher = LEFT_PATTERN.matcher(string);
            if (matcher.find()) {
                someoneLeft = true;
                return;
            }

            matcher = PATTERN.matcher(string);

            if (!matcher.find()) return;

            int startOfMessage = string.indexOf(":");
            int startOfUsername = string.indexOf("]");

            if (startOfMessage == -1 || startOfMessage + MESSAGE_OFFSET >= string.length()) return;

            if (startOfUsername == -1) {
                startOfUsername = NO_RANK_OFFSET;
            }

            String realMessage = string.substring(startOfMessage + MESSAGE_OFFSET);
            String username = string.substring(startOfUsername + MESSAGE_OFFSET, startOfMessage - MESSAGE_OFFSET);

            if (playerHashMap.containsKey(username)) {
                matcher = UNDT_PATTERN.matcher(realMessage);
                if (matcher.find()) {
                    playerHashMap.remove(username);
                }
            } else {
                matcher = DT_PATTERN.matcher(realMessage);
                if (matcher.find()) {
                    playerHashMap.put(username, message);
                }
            }

        });

        Events.ON_RUN_END.register(() -> {
            if (!enableAutoRequeue || someoneLeft) return;

            if (playerHashMap.isEmpty()) {
                Misc.executeCommand("instancerequeue");
            } else {
                Misc.addChatMessage(Text.literal("Downtime reasons:"));
                ChatHud chathud = MinecraftClient.getInstance().inGameHud.getChatHud();
                playerHashMap.forEach((name, text) -> chathud.addMessage(text));


            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            playerHashMap.clear();
            someoneLeft = false;
        });
    }
}
