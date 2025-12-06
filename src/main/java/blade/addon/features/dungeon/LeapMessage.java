package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeapMessage {

    private static final int INDEX_REMOVAL = 9;
    private static final Pattern SEARCH_PATTERN = Pattern.compile("^You have teleported to .*!$");

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (Location.inDungeon()) {
                String string = message.getString();

                Matcher matcher = SEARCH_PATTERN.matcher(string);
                if (!matcher.find()) return;

                if (Events.ON_LEAP.hasListeners()) {
                    Events.ON_LEAP.listeners.forEach(leapEvent -> leapEvent.onLeap(message));
                }
            }
        });

        Events.ON_LEAP.register(message -> {
            if (!Dungeons.enableLeapMessages) return;

            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return;
            networkHandler.sendChatCommand("pc " + message.getString().substring(INDEX_REMOVAL));
        });
    }


}
