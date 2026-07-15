package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeathMessage {

    private static final Pattern PATTERN = Pattern.compile("^ ☠ (.+) and became a ghost\\.$");

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {

            if (!Location.inDungeon() || !Dungeons.sendDeathMessage) return false;

            String msg = text.getString();

            Matcher matcher = PATTERN.matcher(msg);
            if (!matcher.find()) return false;

            String message = matcher.group(1);
            String name = DungeonClass.getNameFromMessage(message);
            if (name == null) return false;

            ClientPacketListener networkHandler = Minecraft.getInstance().getConnection();
            if (networkHandler == null) return false;
            networkHandler.sendCommand("pc " + createMessage(name));
            return false;
        });

    }

    private static String createMessage(String name) {
        return Dungeons.deathMessage.replace("<player>", name);
    }

}
