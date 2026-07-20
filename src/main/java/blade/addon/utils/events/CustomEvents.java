package blade.addon.utils.events;

import blade.addon.utils.Location;
import blade.addon.utils.debug.Debug;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomEvents {

    private static final Pattern PARTY_PATTERN = Pattern.compile("^§9Party §8>");
    private static final Pattern LEAP_PATTERN = Pattern.compile("^You have teleported to (.*)!$");

    private static final int PARTY_MSG_OFFSET = 11;

    public static void init() {
        //party event
        Events.ON_GAME_MESSAGE.register(message -> {
            String string = message.getString();

            Matcher matcher = PARTY_PATTERN.matcher(string);
            if (!matcher.find()) return false;

            int index = string.indexOf(":");
            if (index < PARTY_MSG_OFFSET) {
                Debug.LOGGER.error("{} had bad index", string);
                return false;
            }

            String tempUsername = string.substring(PARTY_MSG_OFFSET, index).replaceAll("§.", "");
            if (index + 2 >= string.length()) return false;
            String sentMessage = string.substring(index + 2).strip();

            index = tempUsername.indexOf("]") + 2;
            if (index > -1 && index < tempUsername.length()) {
                tempUsername = tempUsername.substring(index);
            }

            String username = tempUsername;
            Events.ON_PARTY_MESSAGE.invoke(partyMessageEvent -> partyMessageEvent.sentMessage(username, sentMessage));

            return false;
        });

        //leap event
        Events.ON_GAME_MESSAGE.register(message -> {
            if (Location.inDungeon()) {
                String string = message.getString();

                Matcher matcher = LEAP_PATTERN.matcher(string);
                if (!matcher.find()) return false;

                Events.ON_LEAP.invoke(leapEvent -> leapEvent.onLeap(matcher.group(1)));
            }

            return false;
        });
    }
}
