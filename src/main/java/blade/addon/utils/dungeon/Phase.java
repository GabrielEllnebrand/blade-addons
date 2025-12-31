package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.JsonUtility;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.events.Events;
import blade.addon.utils.events.interfaces.PhaseEvent;
import blade.addon.utils.events.interfaces.RunEndEvent;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Phase {

    private static final Pattern END_PATTERN = Pattern.compile("^\\s*☠ Defeated (.+) in 0?([\\dhms ]+)\\s*(\\(NEW RECORD!\\))?$");
    private static final Pattern SEARCH_PATTERN = Pattern.compile("^ ⏣ The Catacombs .*$");

    private static final Split DUMMY_SPLIT = new Split("test split", "if this is called idk", "if this is called idk", 43690);

    private static final HashMap<String, ArrayList<Split>> FLOOR_SPLITS = JsonUtility.readSplits("/data/splits.json");

    private static final int DUMMY_SIZE = 10;
    public static final int SPLIT_LENGTH = 165;

    private static ArrayList<Split> currentSplits;
    private static int currentPhase = -1;
    private static String floor = "";

    private static boolean inFloor7 = false;
    private static boolean stormDead = false;
    private static boolean runOver = false;

    @ConfigValue
    public static boolean enableSplits = false;

    @ConfigValue
    public static boolean includeTotalTime = false;

    @ConfigValue
    public static boolean sendSplitInChat = false;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (currentSplits == null || runOver) return;
            for (Split split : currentSplits) {
                split.tick();
            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                reset();
            }
        });

        Events.ON_GAME_MESSAGE.register(Phase::parseGameMessage);
        Events.ON_TEAM.register(Phase::detectFloor);
    }

    private static void detectFloor(String line) {
        if (!Location.inDungeon() || floor != null) return;

        Matcher matcher = SEARCH_PATTERN.matcher(line);
        if (!matcher.find()) return;
        int start = line.indexOf("(");
        int end = line.indexOf(")");
        floor = line.substring(start + 1, end);

        currentSplits = FLOOR_SPLITS.get(floor);
        if (currentSplits!= null) {
            for (Split split : currentSplits) {
                split.reset();
            }
        }
        if (floor != null) {
            if (floor.contains("7")) inFloor7 = true;
        }
    }

    private static void reset() {
        currentSplits = null;
        floor = null;
        currentPhase = -1;
        inFloor7 = false;
        stormDead = false;
        runOver = false;
    }

    public static boolean parseGameMessage(Text message) {
        if (!Location.inDungeon()) return false;
        String string = message.getString();
        if (currentSplits == null) return false;
        if (runOver) return false;

        for (int i = 0; i < currentSplits.size(); i++) {

            Split currentSplit = currentSplits.get(i);
            if (currentSplit.ended()) continue;

            currentSplit.parseMessage(string);

            if (currentSplit.ended()) {
                if (sendSplitInChat) {
                    Misc.addChatMessage(currentSplit.createNameText().append(currentSplit.createTimeText()));
                }

                currentPhase = i + 1;
                if (Events.ON_PHASE_CHANGE.hasListeners()) {
                    Events.ON_PHASE_CHANGE.invoke(PhaseEvent::onPhaseChange);
                }
            }

            //just for starting the run
            if (currentSplit.started() && currentPhase == -1) {
                currentPhase = i;
                if (Events.ON_PHASE_CHANGE.hasListeners()) {
                    Events.ON_PHASE_CHANGE.invoke(PhaseEvent::onPhaseChange);
                }
            }

        }

        Matcher matcher = END_PATTERN.matcher(string);
        if (matcher.find()) {
           endRun();
        }

        if (inP2()) {
            if (string.equals("[BOSS] Storm: I should have known that I stood no chance.")) {
                stormDead = true;
            }
        }

        return false;
    }

    private static void endRun() {
        runOver = true;
        currentPhase = currentSplits.size();

        Misc.addChatMessage(Text.literal("§aSplits: "));
        for (Split split : currentSplits) {
            split.end();
            Misc.addChatMessage(split.createNameText().append(split.createTimeText()));
        }
        if (!currentSplits.isEmpty()) {
            double time = currentSplits.getLast().getTimeDiffrence();
            Text timeLost = Text.literal("§aApproximately §e" + Constants.DECIMAL_FORMAT.format(time) + "s §alost to lag.");
            Misc.addChatMessage(timeLost);
        }


        if (Events.ON_RUN_END.hasListeners()) {
            Events.ON_RUN_END.listeners.forEach(RunEndEvent::onRunEnd);
        }
    }

    public static double getPhase() {
        return currentPhase;
    }


    public static boolean isInFloor7() {
        return inFloor7;
    }

    public static boolean runStarted() {
        return currentPhase >= 0;
    }

    public static boolean runJustStarted() {
        return currentPhase == 0;
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

    public static boolean inGoldorTunnel() {
        return currentPhase == 7 && inFloor7;
    }

    public static boolean inP3() {
        return (currentPhase == 6 || currentPhase == 7) && inFloor7;
    }

    public static boolean inP5() {
        return currentPhase == 9 && inFloor7;
    }

    public static boolean runOver() {
        return runOver;
    }



    @ConfigValue
    public static HUDComponent splitTimer = new HUDComponent(0, 0, SPLIT_LENGTH, 100, 1, "Splits",
            () -> Location.inDungeon() && enableSplits && Phase.runStarted(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                if (currentSplits != null) {
                    int splitCount = currentSplits.size();
                    if (!includeTotalTime) splitCount--;

                    for (int i = 0; i < splitCount; i++) {
                        currentSplits.get(i).drawSplit(drawContext, textRenderer, x, y + Constants.TEXT_HEIGHT * i, SPLIT_LENGTH);

                    }
                } else {
                    for (int i = 0; i < DUMMY_SIZE; i++) {
                        DUMMY_SPLIT.drawSplit(drawContext, textRenderer, x, y + Constants.TEXT_HEIGHT * i, SPLIT_LENGTH);
                    }
                }
            }), () -> enableSplits
    );
}
