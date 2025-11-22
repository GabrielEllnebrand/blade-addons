package blade.addon.utils.dungeon;

import blade.addon.utils.JsonUtility;
import blade.addon.utils.Location;
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
    private static final Pattern STORM_KILL_PATTERN = Pattern.compile("^\\[BOSS] Storm: I should have known that I stood no chance\\.$");
    private static final Pattern GATE_BLOWN_PATTERN = Pattern.compile("^The gate has been destroyed!$");
    private static final Pattern TERMINALS_DONE_PATTERN = Pattern.compile("(activated|completed) (a terminal|a device|a lever)! \\((\\d)/(\\d)\\)$");
    private static final Pattern CORE_OPENING_PATTERN = Pattern.compile("^The Core entrance is opening!$");


    private static final Split DUMMY_SPLIT = new Split("test split", "if this is called idk", "if this is called idk", 43690);

    private static final HashMap<String, ArrayList<Split>> FLOOR_SPLITS = JsonUtility.readSplits("/data/splits.json");

    private static final int TEXT_HEIGHT = 10;
    private static final int DUMMY_SIZE = 10;

    private static ArrayList<Split> currentSplits;
    private static int currentPhase = -1;
    private static String floor = "";

    private static int currentSection = 0;
    private static boolean termsDone = false;
    private static boolean gateBlownUp = false;

    private static boolean inFloor7 = false;
    private static boolean stormDead = false;
    private static boolean runOver = false;

    @ConfigValue
    public static boolean enableSplits = false;

    @ConfigValue
    public static boolean includeTotalTime = false;

    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (currentSplits == null || runOver) return;
            for (Split split : currentSplits) {
                split.tick();
            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
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

        Events.ON_GAME_MESSAGE.register(text ->  {
            if (Location.inDungeon()) {
                Phase.parseMessage(text);
            }
        });

        Events.ON_TEAM.register(text -> {
            if (!Location.inDungeon() || floor != null) return;

            Matcher matcher = SEARCH_PATTERN.matcher(text);
            if (!matcher.find()) return;
            int start = text.indexOf("(");
            int end = text.indexOf(")");
            floor = text.substring(start + 1, end);

            currentSplits = FLOOR_SPLITS.get(floor);
            if (currentSplits!= null) {
                for (Split split : currentSplits) {
                    split.reset();
                }
            }
            if (floor != null) {
                if (floor.contains("7")) inFloor7 = true;
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
                if (Events.ON_PHASE_CHANGE.hasListeners()) {
                    Events.ON_PHASE_CHANGE.invoke(PhaseEvent::onPhaseChange);
                }
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

            if (Events.ON_RUN_END.hasListeners()) {
                Events.ON_RUN_END.listeners.forEach(RunEndEvent::onRunEnd);
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

    @ConfigValue
    public static HUDComponent splitTimer = new HUDComponent(0, 0, Split.SPLIT_LENGTH, 100, 1, "Splits",
            () -> Location.inDungeon() && enableSplits && Phase.runStarted(),
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                long currentTime = System.currentTimeMillis();
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                if (currentSplits != null) {
                    int splitCount = currentSplits.size();
                    if (!includeTotalTime) splitCount--;

                    for (int i = 0; i < splitCount; i++) {
                        currentSplits.get(i).drawSplit(drawContext, textRenderer, currentTime, x, y + TEXT_HEIGHT * i);

                    }
                } else {
                    for (int i = 0; i < DUMMY_SIZE; i++) {
                        DUMMY_SPLIT.drawSplit(drawContext, textRenderer, currentTime, x, y + TEXT_HEIGHT * i);
                    }
                }
            }), () -> enableSplits
    );
}
