package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeathMessage {

    private static final Pattern[] patterns = new Pattern[]{
        Pattern.compile("^ ☠ (.*) was killed by *."),
        Pattern.compile("^ ☠ (.*) died to a *."),
        Pattern.compile("^ ☠ (.*) fell to their death *."),
    };

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {

            if (!Location.inDungeon() || !Dungeons.sendDeathMessage) return false;

            String msg = text.getString();
            for(Pattern pattern: patterns) {
                Matcher matcher = pattern.matcher(msg);
                if (matcher.find()) {
                    String name = matcher.group(1);

                    ClientPacketListener networkHandler = Minecraft.getInstance().getConnection();
                    if (networkHandler == null) return false;
                    networkHandler.sendCommand("pc " + createMessage(name));
                    return false;
                }
            }
            return false;
        });

    }

    private static String createMessage(String name) {
        return Dungeons.deathMessage.replace("<player>", name);
    }

}
