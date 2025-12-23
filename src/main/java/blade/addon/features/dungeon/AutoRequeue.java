package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoRequeue {

    private static final Pattern DT_PATTERN = Pattern.compile("!dt");
    private static final Pattern UNDT_PATTERN = Pattern.compile("!undt");
    private static final Pattern LEFT_PATTERN = Pattern.compile("has left the party.$");

    private static final HashMap<String, String> playerHashMap = new HashMap<>();


    private static boolean someoneLeft = false;

    public static void init() {
        Events.ON_PARTY_MESSAGE.register((username, message) -> {
            if (playerHashMap.containsKey(username)) {
                Matcher matcher = UNDT_PATTERN.matcher(message);
                if (matcher.find()) {
                    playerHashMap.remove(username);
                    Misc.addChatMessage(Text.literal("Downtime removed").formatted(Formatting.GREEN));
                }
            } else {
                Matcher matcher = DT_PATTERN.matcher(message);
                if (matcher.find()) {
                    playerHashMap.put(username, message);
                    Misc.addChatMessage(Text.literal("Downtime added").formatted(Formatting.GREEN));
                }
            }
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
            if (!Dungeons.enableAutoRequeue || someoneLeft) return;

            if (playerHashMap.isEmpty()) {
                Misc.executeCommand("instancerequeue");
            } else {
                Misc.addChatMessage(Text.literal("Downtime reasons:"));
                ChatHud chathud = MinecraftClient.getInstance().inGameHud.getChatHud();
                playerHashMap.forEach((name, string) -> chathud.addMessage(Text.literal(name + "> " + string)));


            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            playerHashMap.clear();
            someoneLeft = false;
        });
    }
}
