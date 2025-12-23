package blade.addon.utils.events;

import blade.addon.utils.Debug;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomEvents {

    private static final Pattern PATTERN = Pattern.compile("^§9Party §8>");

    private static final int PARTY_MSG_OFFSET = 11;

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String string = message.getString();
            Matcher matcher = PATTERN.matcher(string);
            if (!matcher.find()) return;

            int index = string.indexOf(":");
            if (index < PARTY_MSG_OFFSET) {
                Debug.LOGGER.error("{} had bad index", string);
                return;
            }

            String tempUsername = string.substring(PARTY_MSG_OFFSET, index).replaceAll("§.", "");
            if (index + 2 >= string.length()) return;
            String sentMessage = string.substring(index + 2);

            index = tempUsername.indexOf("]") + 2;
            if (index > -1 && index < tempUsername.length()) {
                tempUsername = tempUsername.substring(index);
            }

            String username = tempUsername;
            if (Events.ON_PARTY_MESSAGE.hasListeners()) {
                Events.ON_PARTY_MESSAGE.invoke(partyMessageEvent -> partyMessageEvent.sentMessage(username, sentMessage));
            }

        });
    }
}
