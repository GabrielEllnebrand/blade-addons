package blade.addon.utils.dungeon;

import blade.addon.utils.JsonUtility;
import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
    private static final Pattern STORM_KILL_PATTERN = Pattern.compile("^\\[BOSS] Storm: I should have known that I stood no chance\\.$");
    private static final Pattern GATE_BLOWN_PATTERN = Pattern.compile("^The gate has been destroyed!$");
    private static final Pattern TERMINALS_DONE_PATTERN = Pattern.compile("(activated|completed) (a terminal|a device|a lever)! \\((\\d)/(\\d)\\)$");
    private static final Pattern CORE_OPENING_PATTERN = Pattern.compile("^The Core entrance is opening!$");



    private static final Split DUMMY_SPLIT = new Split("test split", "if this is called idk", "if this is called idk", 43690);

    private static final HashMap<String, ArrayList<Split>> FLOOR_SPLITS = JsonUtility.readSplits("/data/splits.json");

    private static final int TEXT_HEIGHT = 10;
    private static final int LOOKUP_RATE = 10;
    private static final int DUMMY_SIZE = 9;

    private static ArrayList<Split> currentSplits;
    private static int currentPhase = -1;
    private static String floor = "";
    private static int tick = 0;

    private static int currentSection = 0;
    private static boolean termsDone = false;
    private static boolean gateBlownUp = false;

    private static boolean inFloor7 = false;
    private static boolean stormDead = false;
    private static boolean runOver = false;

    @ConfigValue
    public static boolean enableSplits = false;

    @ConfigValue
    public static boolean autoReque = false;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (currentSplits == null || runOver) return;
            for (Split split : currentSplits) {
                split.tick();
            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (newLocation.inDungeon()) {
                currentSplits = null;
                floor = null;
                currentPhase = -1;
                inFloor7 = false;
                stormDead = false;
                runOver = false;
                currentSection = 0;
                termsDone = false;
                gateBlownUp = false;
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (Location.inDungeon()) {
                Phase.parseMessage(message);
            }
        });
    }


    public static void parseMessage(Text message) {
        if (!Location.inDungeon()) return;
        String string = message.getString();
        if (currentSplits == null) return;
        if (runOver) return;

        for (int i = 0; i < currentSplits.size(); i++) {

            Split currentSplit = currentSplits.get(i);
            if (currentSplit.ended()) continue;

            currentSplit.parseMessage(string);

            if (currentSplit.ended()) {
                currentPhase = i + 1;
            }

            //just for starting the run
            if (currentSplit.started() && currentPhase == -1) {
                currentPhase = i;
            }


        }

        Matcher matcher = END_PATTERN.matcher(string);
        if (matcher.find()) {
            runOver = true;
            currentPhase = currentSplits.size();

            for (Split split : currentSplits) {
                split.end();
            }

            if (autoReque) {
                ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
                if (networkHandler == null) return;
                networkHandler.sendChatCommand("instancerequeue");
            }
        }

        if (inP2()) {
            matcher = STORM_KILL_PATTERN.matcher(string);
            if (matcher.find()) {
                stormDead = true;
                currentSection = 1;
            }
        }

        if (inP3()) {
            if (!termsDone) {
                matcher = TERMINALS_DONE_PATTERN.matcher(string);
                if (matcher.find()) {
                    String num1 = matcher.group(3);
                    String num2 = matcher.group(4);
                    if (num1.equals(num2)) {
                        termsDone = true;
                    }
                }
            }

            if (!gateBlownUp) {
                matcher = GATE_BLOWN_PATTERN.matcher(string);
                if (matcher.find()) {
                    gateBlownUp = true;
                }
            }

            if (gateBlownUp && termsDone) {
                currentSection++;
                gateBlownUp = false;
                termsDone = false;
            }

            matcher = CORE_OPENING_PATTERN.matcher(string);
            if (matcher.find()) {
                //so in "goldor tunnel" can be shown after terms are done
               currentSection = 5;
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

    public static boolean runStarted() {
        return currentPhase >= 0;
    }

    public static boolean inBoss() {
        return currentPhase > 3;
    }

    public static boolean inP1() {
        return currentPhase == 4 && inFloor7;
    }

    public static boolean inP2() {
        return currentPhase == 5 && inFloor7;
    }

    public static boolean stormDead() {
        return stormDead;
    }

    public static boolean inTerminals() {
        return currentPhase == 6 && inFloor7;
    }

    public static boolean inP3() {
        return (currentPhase == 6 || currentPhase == 7) && inFloor7;
    }

    public static boolean inSection(int section) {
        return currentSection == section && inP3();
    }

    public static boolean inP5() {
        return currentPhase == 9 && inFloor7;
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
    }

    @ConfigValue
    public static HUDComponent splitTimer = new HUDComponent(0, 0, Split.SPLIT_LENGTH, 90, 1, "Splits",
            Location::inDungeon,
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                long currentTime = System.currentTimeMillis();
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                if (currentSplits != null) {
                    for (int i = 0; i < currentSplits.size(); i++) {
                        currentSplits.get(i).drawSplit(drawContext, textRenderer, currentTime, x, y + TEXT_HEIGHT * i);

                    }
                } else {
                    for (int i = 0; i < DUMMY_SIZE; i++) {
                        DUMMY_SPLIT.drawSplit(drawContext, textRenderer, currentTime, x, y + TEXT_HEIGHT * i);
                    }
                }
            })
    );
}
