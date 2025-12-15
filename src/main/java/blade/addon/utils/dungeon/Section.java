package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Debug;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Section {
    public enum DisplayTerminalSplitsWhen {
        BOSS("Entire Boss"), TERMINALS_ONLY("Terminals");

        private final String label;

        DisplayTerminalSplitsWhen(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final Split[] splits = {
            new Split("1st", "", "", 16755200),
            new Split("2nd", "", "", 16755200),
            new Split("3rd", "", "", 16755200),
            new Split("4th", "", "", 16755200),};

    private static final Pattern TERMINALS_DONE_PATTERN = Pattern.compile("(activated|completed) (a terminal|a device|a lever)! \\((\\d)/(\\d)\\)$");

    public static int SPLIT_LENGTH = 120;

    private static int currentSection = 0;
    private static int completed = 0;
    private static int total = 7;
    private static boolean gateBlownUp = false;

    @ConfigValue
    public static boolean enableTerminalSplits = false;

    @ConfigValue
    public static boolean sendTermianlSplits = false;

    @ConfigValue
    public static DisplayTerminalSplitsWhen displayTerminalSplitsWhen = DisplayTerminalSplitsWhen.TERMINALS_ONLY;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(Section::parseMessage);
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                reset();
            }
        });
        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.inTerminals()) {
                currentSection = 1;
                splits[0].start();
            } else if (Phase.inGoldorTunnel()) {
                endAllSections();
            }
        });
        Events.ON_SERVER_TICK.register(() -> {
            if (currentSection < 1 || currentSection > 5) return;
            for (Split split : splits) {
                split.tick();
            }
        });
    }

    private static void reset() {
        currentSection = 0;
        resetSection();
        for(Split split: splits) {
            split.reset();
        }
    }

    private static void resetSection() {
        completed = 0;
        gateBlownUp = false;
    }

    private static void incrementSection() {
        resetSection();
        endSplit(currentSection);
        currentSection++;
        startSplit(currentSection);
    }

    private static void endSplit(int section) {
        int index = section - 1;
        if (index < 0 || index >= splits.length) return;
        splits[index].end();
    }

    private static void startSplit(int section) {
        int index = section - 1;
        if (index < 0 || index >= splits.length) return;
        splits[index].start();
    }

    private static void endAllSections() {
        for(Split split: splits) {
            split.end();
        }
        if (sendTermianlSplits) {
            Misc.addChatMessage(Text.literal("Splits: ").formatted(Formatting.GREEN));
            for (Split split : splits) {
                split.end();
                Misc.addChatMessage(split.createNameText().append(split.createTimeText()));
            }
        }
    }

    private static void parseMessage(Text message) {
        if (!Phase.inTerminals() || !enableTerminalSplits) return;

        String string = message.getString();
        Matcher matcher = TERMINALS_DONE_PATTERN.matcher(string);
        if (matcher.find()) {
            try {
                int recentlyCompleted = Integer.parseInt(matcher.group(3));
                if ((recentlyCompleted == total && gateBlownUp) || (recentlyCompleted < completed)) {
                    incrementSection();
                } else {
                    total = Integer.parseInt(matcher.group(4));
                    completed = recentlyCompleted;
                }
            } catch (NumberFormatException e) {
                Debug.LOGGER.error("Failed to parse terminal message, {}", e.getMessage());
            }


        } else if (!gateBlownUp) {
            if (string.equals("The gate has been destroyed!")) {
                gateBlownUp = true;

                if (completed == total) {
                    incrementSection();
                }

            }
        } else if (string.equals("The Core entrance is opening!")) {
            //so in "goldor tunnel" can be shown after terms are done
            currentSection = 5;
            endAllSections();
            Debug.sendDebugMessage(Text.literal("Core section"));
        }
    }


    public static double getSection() {
        return currentSection;
    }

    public static boolean isGateBlownUp() {
        return gateBlownUp;
    }

    public static boolean inSection(int section) {
        return currentSection == section && Phase.inP3();
    }

    public static boolean display() {
        if (enableTerminalSplits && Location.inDungeon()) {

            return switch (displayTerminalSplitsWhen) {
                case BOSS -> Phase.inBoss();
                case TERMINALS_ONLY -> Phase.inTerminals();
            };

        }

        return false;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        for (int i = 0; i < splits.length; i++) {
            splits[i].drawSplit(context, textRenderer, x, y + Constants.TEXT_HEIGHT * i, SPLIT_LENGTH);
        }
    }

    @ConfigValue
    public static HUDComponent terminalSplits = new HUDComponent(0, 0, SPLIT_LENGTH, 50, 1, "Term splits", Section::display, Section::render, () -> enableTerminalSplits);
}
