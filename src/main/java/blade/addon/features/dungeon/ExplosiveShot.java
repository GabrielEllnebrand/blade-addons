package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExplosiveShot {

    private static final Pattern PATTERN = Pattern.compile("^Your Explosive Shot hit (\\d+) enemies for ([\\d,?.]+) damage\\.$");
    private static final DecimalFormat FORMAT = new DecimalFormat("###,###,###,###");



    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() || !Dungeons.calculateCriticalHit) return;
            if (Dungeons.onlyInBoss && !Phase.inBoss()) return;

            String string = message.getString();

            Matcher matcher = PATTERN.matcher(string);

            if (matcher.matches()) {
                String count = matcher.group(1);
                String amount = matcher.group(2);

                try {
                    int countNum = Integer.parseInt(count);
                    double amountNum = Double.parseDouble(amount.replace(",", ""));

                    double damagePerEntity = amountNum / countNum;

                    Misc.addChatMessage(Text.literal("§aExplosive shot did §e" + FORMAT.format(damagePerEntity) +"§a damage per enemy."));

                } catch (NumberFormatException ignored) {
                }
            }

        });
    }

}
