package blade.addon.features.dungeon;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class LeapMessage {

    private static final int INDEX_REMOVAL = 9;

    public static void init() {
        Events.ON_LEAP.register(message -> {
            if (!Dungeons.enableLeapMessages) return false;

            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return false;
            networkHandler.sendChatCommand("pc " + message.getString().substring(INDEX_REMOVAL));
            return false;
        });
    }


}
