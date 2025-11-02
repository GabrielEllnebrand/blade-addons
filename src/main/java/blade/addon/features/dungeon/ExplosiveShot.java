package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExplosiveShot {

    private static final Pattern PATTERN = Pattern.compile("^Your Explosive Shot hit (\\d+) enemies for ([\\d,?.]+) damage\\.$");
    private static final DecimalFormat FORMAT = new DecimalFormat("###,###,###,###");

    @ConfigValue
    public static boolean calculateCriticalHit = false;

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon()) return;
            if (!calculateCriticalHit) return;

            String string = message.getString();

            Matcher matcher = PATTERN.matcher(string);

            if (matcher.matches()) {
                String count = matcher.group(1);
                String amount = matcher.group(2);

                try {
                    int countNum = Integer.parseInt(count);
                    double amountNum = Double.parseDouble(amount.replace(",", ""));

                    double damagePerEntity = amountNum / countNum;

                    InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
                    gameHud.getChatHud().addMessage(
                            Text.literal("Explosive shot did ").formatted(Formatting.GREEN)
                                    .append(Text.literal(FORMAT.format(damagePerEntity)).formatted(Formatting.YELLOW))
                                    .append(Text.literal(" damage per enemy.").formatted(Formatting.GREEN))
                    );

                } catch (NumberFormatException ignored) {
                }
            }

        });
    }

}
