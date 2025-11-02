package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeapMessage {

    private static final int INDEX_REMOVAL = 9;
    private static final Pattern SEARCH_PATTERN = Pattern.compile("^You have teleported to .*!$");

    @ConfigValue
    public static boolean enableLeapMessages = false;

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (Location.inDungeon()) {
                if (!enableLeapMessages) return;
                String string = message.getString();

                Matcher matcher = SEARCH_PATTERN.matcher(string);
                if (matcher.find()) {
                    ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
                    if (networkHandler == null) return;
                    networkHandler.sendChatCommand("pc " + string.substring(INDEX_REMOVAL));
                }
            }
        });
    }


}
