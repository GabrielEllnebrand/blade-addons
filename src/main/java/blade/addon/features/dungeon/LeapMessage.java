package blade.addon.features.dungeon;

import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class LeapMessage {

    private static final int INDEX_REMOVAL = 9;

    public static void init() {
        Events.ON_LEAP.register(message -> {
            if (!Dungeons.enableLeapMessages) return false;

            ClientPacketListener networkHandler = Minecraft.getInstance().getConnection();
            if (networkHandler == null) return false;
            networkHandler.sendCommand("pc " + message.getString().substring(INDEX_REMOVAL));
            return false;
        });
    }


}
