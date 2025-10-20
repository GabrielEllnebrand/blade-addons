package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeapMessage {

    private static final Pattern SEARCH_PATTERN = Pattern.compile("^You have teleported to .*!$");

    @ConfigValue
    public static boolean enableLeapMessages = false;

    public static void parseMessage(Text message) {
        if (!Location.inDungeon()) return;
        String string = message.getString();

        Matcher matcher = SEARCH_PATTERN.matcher(string);
        if (matcher.find()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            player.networkHandler.sendChatMessage(string);
        }
    }

}
