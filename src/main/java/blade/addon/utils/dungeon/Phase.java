package blade.addon.utils.dungeon;

import blade.addon.features.dungeon.GoldorTickTimer;
import blade.addon.features.dungeon.PositionMessages;
import blade.addon.features.dungeon.StormTickTimer;
import blade.addon.utils.JsonUtility;
import blade.addon.utils.Location;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Phase {

    private static final Pattern END_PATTERN = Pattern.compile("^\\s*☠ Defeated (.+) in 0?([\\dhms ]+)\\s*(\\(NEW RECORD!\\))?$");
    private static final Pattern SEARCH_PATTERN = Pattern.compile("^ ⏣ The Catacombs .*$");

    private static final Split DUMMY_SPLIT = new Split("test split", "if this is called idk");

    private static final HashMap<String, ArrayList<Split>> FLOOR_SPLITS = JsonUtility.readSplits("/data/splits.json");

    private static final int TEXT_HEIGHT = 10;
    private static final int LOOKUP_RATE = 10;
    private static final int DUMMY_SIZE = 9;

    private static ArrayList<Split> currentSplits;
    private static int currentPhase = -1;
    private static String floor = "";
    private static int tick = 0;
    private static boolean inFloor7 = false;

    @ConfigValue
    public static boolean enableSplits = false;

    @ConfigValue
    public static boolean autoReque = false;

    public static void parseMessage(Text message) {
        if (!Location.inDungeon()) return;
        String string = message.getString();

        if (currentSplits == null) return;

        for (int i = currentPhase + 1; i < currentSplits.size(); i++) {
            if (currentSplits.get(i).matches(string)) {
                currentPhase = i;
                if (i - 1 >= 0) {
                    currentSplits.get(i - 1).end();
                }
            }
        }

        Matcher matcher = END_PATTERN.matcher(string);
        if (matcher.find()) {
            currentPhase = currentSplits.size();
            if (currentPhase - 1 >= 0) {
                currentSplits.get(currentPhase - 1).end();
            }
            if (autoReque) {
                ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
                if (networkHandler == null) return;
                networkHandler.sendChatCommand("instancerequeue");
            }
        }
    }


    public static void updateFloor(Scoreboard scoreboard) {
        for (Team team : scoreboard.getTeams()) {
            String teamStr = team.getPrefix().getString() + team.getSuffix().getString();
            Matcher matcher = SEARCH_PATTERN.matcher(teamStr);
            if (matcher.find()) {
                int start = teamStr.indexOf("(");
                int end = teamStr.indexOf(")");
                floor = teamStr.substring(start + 1, end);
                return;
            }
        }
        floor = null;
    }

    public static void reset() {
        currentSplits = null;
        floor = null;
        currentPhase = -1;
        inFloor7 = false;
        StormTickTimer.reset();
        GoldorTickTimer.reset();
        PositionMessages.reset();
    }

    public static boolean inP2() {
        return currentPhase == 4 && inFloor7;
    }

    public static boolean inP3() {
        return currentPhase == 5 && inFloor7;
    }

    public static void tick(MinecraftClient client) {
        if (!Location.inDungeon() || client.player == null) return;

        if (floor == null) {

            if (tick >= LOOKUP_RATE) {
                tick = 0;
                updateFloor(client.player.getScoreboard());
                currentSplits = FLOOR_SPLITS.get(floor);
                if (currentSplits != null) {
                    for (Split split : currentSplits) {
                        split.reset();
                    }
                }
                if (floor != null) {
                    if (floor.contains("7")) inFloor7 = true;
                }
            } else {
                tick++;
            }
        }

        if (currentSplits == null) return;
        if (currentPhase > -1 && currentPhase < currentSplits.size()) {
            currentSplits.get(currentPhase).tick();
        }

    }

    @ConfigValue
    public static HUDComponent splitTimer = new HUDComponent(0, 0, 80, 90, 1,
            Location::inDungeon,
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                int color = 0xffffffff;

                long currentTime = System.currentTimeMillis();

                if (currentSplits != null) {
                    for (int i = 0; i < currentSplits.size(); i++) {
                        drawContext.drawText(MinecraftClient.getInstance().textRenderer, currentSplits.get(i).makeSplitString(currentTime), x, y + TEXT_HEIGHT * i, color, true);
                    }
                } else {
                    for (int i = 0; i < DUMMY_SIZE; i++) {
                        drawContext.drawText(MinecraftClient.getInstance().textRenderer, DUMMY_SPLIT.makeSplitString(0), x, y + TEXT_HEIGHT * i, color, true);
                    }
                }
            })
    );
}
