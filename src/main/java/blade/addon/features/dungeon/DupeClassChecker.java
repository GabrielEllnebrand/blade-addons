package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DupeClassChecker {

    private static final Pattern STARTING_PATTERN = Pattern.compile("^Starting in \\d second(s)?");
    private static final Pattern CLASS_PATTERN = Pattern.compile("\\[[ABHMT]]");

    private static final Text DISPLAY_TEXT = Text.literal("Duplicate Class Detected").formatted(Formatting.RED);

    private static boolean hasDuplicateClass = false;

    @ConfigValue
    public static boolean detectDuplicateClass = false;

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() || Phase.runStarted() || !detectDuplicateClass) return;

            Matcher matcher = STARTING_PATTERN.matcher(message.getString());
            if (!matcher.find()) return;

            hasDuplicateClass = hasDuplicateClasses();
        });
    }

    private static boolean hasDuplicateClasses() {

        ClientWorld world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (world == null || player == null) return false;

        Scoreboard scoreboard = world.getScoreboard();
        HashSet<String> set = new HashSet<>();

        for (Team team : scoreboard.getTeams()) {
            String teamStr = team.getPrefix().getString() + team.getSuffix().getString();
            Matcher matcher = CLASS_PATTERN.matcher(teamStr);
            if (matcher.find()) {
                String dungeonClass = matcher.group();

                if (set.contains(dungeonClass)) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2, 1);
                    return true;
                }

                set.add(dungeonClass);
            }
        }
        return false;
    }

    @ConfigValue
    public static HUDComponent duplicateClassDisplay = new HUDComponent(0, 0, 130, 10, 1, "",
            () -> Location.inDungeon() && hasDuplicateClass && !Phase.runStarted(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, DISPLAY_TEXT, x, y, 0xffffffff, true);
            }), () -> detectDuplicateClass
    );
}
