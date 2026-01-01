package blade.addon.utils.dungeon;

import blade.addon.features.dungeon.f7.terms.TitleHider;
import blade.addon.utils.Constants;
import blade.addon.utils.Debug;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.events.Events;
import blade.addon.utils.events.interfaces.SectionEvent;
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
    private static final Pattern TERMINALS_DONE_PATTERN = Pattern.compile("^(\\w+) (activated|completed) a (terminal|device|lever)! \\((\\d)/(\\d)\\)$");

    public static int SPLIT_LENGTH = 120;
    private static final int TERM_PHASE_INDEX = 6;

    private static int currentSection = -1;
    private static int completed = 0;
    private static int total = 7;
    private static boolean gateBlownUp = false;

    @ConfigValue
    public static boolean enableTerminalSplits = false;

    @ConfigValue
    public static DisplayTerminalSplitsWhen displayTerminalSplitsWhen = DisplayTerminalSplitsWhen.TERMINALS_ONLY;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(Section::parseMessage);
        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            reset();
            return false;
        });
        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.inP2()) {
                currentSection = 0;
            }

            if (Phase.inTerminals()) {
                if (Debug.termInfo) {
                    Misc.addChatMessage(Text.literal("Terminals started"));
                }
                currentSection = 1;
                splits[0].start();
            } else if (Phase.inGoldorTunnel()) {
                if (TitleHider.shouldHideTitle()) {
                    Misc.forceTitle(Text.empty(), Text.literal("The Core entrance is opening!").formatted(Formatting.GREEN));
                }
                if (Debug.termInfo) {
                    Misc.addChatMessage(Text.literal("Terminals ended"));
                }
                currentSection = 5;
                endAllSections();
            }
            return false;
        });
        Events.ON_SERVER_TICK.register(() -> {
            if (currentSection < 1 || currentSection > 5) return false;
            for (Split split : splits) {
                split.tick();
            }
            return false;
        });
    }

    private static void reset() {
        currentSection = -1;
        resetSection();
        for (Split split : splits) {
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

        if (Debug.termInfo) {
            Misc.addChatMessage(Text.literal("section: " + currentSection));
        }

        Events.ON_SECTION_CHANGE.invoke(SectionEvent::onSection);
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
        for (Split split : splits) {
            split.end();
        }
        if (Debug.termInfo) {
            Misc.addChatMessage(Text.literal("ending all sections"));
        }
        Events.ON_SECTION_CHANGE.invoke(SectionEvent::onSection);
    }

    private static boolean parseMessage(Text message) {
        if (!Phase.inTerminals()) return false;

        String string = message.getString();
        Matcher matcher = TERMINALS_DONE_PATTERN.matcher(string);
        if (matcher.find()) {
            String name = matcher.group(1);
            String action = matcher.group(2);
            String objective = matcher.group(3);
            int currentCompleted;
            int totalNeeded;

            try {
                currentCompleted = Integer.parseInt(matcher.group(4));
                totalNeeded = Integer.parseInt(matcher.group(5));
            } catch (NumberFormatException e) {
                Debug.LOGGER.error("Failed to parse terminal message, {}", e.getMessage());
                return false;
            }

            if (Debug.termInfo) {
                Misc.addChatMessage(Text.literal("name:" + name + ":objective>" + objective + ":(" + currentCompleted + "/" + totalNeeded + ")"));
            }

            Events.ON_TERMINAL.invoke(terminalEvent -> terminalEvent.onComplete(name, action, objective, currentCompleted, totalNeeded));


            int recentlyCompleted = currentCompleted;
            if ((recentlyCompleted == total && gateBlownUp) || (recentlyCompleted < completed)) {
                incrementSection();
                if (TitleHider.shouldHideTitle()) {
                    Misc.forceTitle(Text.empty(), message);
                }
            } else {
                if (Misc.isClientPlayer(name) && TitleHider.shouldHideTitle()) {
                    Misc.forceTitle(Text.empty(), message);
                }
                total = totalNeeded;
                completed = recentlyCompleted;
            }

            if (Floor7.terminalTimeStamps) {
                Misc.addChatMessage(Text.literal(name + " §a" + action + " " + objective + "! (§c" + currentCompleted + "§a/ " + totalNeeded + ") §8(§7" + getSectionTime() + "s §8| §7" + Phase.getPhaseTime(TERM_PHASE_INDEX) + "s§8)"));
                return true;
            }


        } else if (!gateBlownUp) {
            if (string.equals("The gate has been destroyed!")) {
                gateBlownUp = true;

                if (TitleHider.shouldHideTitle()) {
                    Misc.forceTitle(Text.empty(), message);
                }

                if (completed == total) {
                    incrementSection();
                }

                if (Floor7.terminalTimeStamps) {
                    Misc.addChatMessage(Text.literal("§aThe gate has been destroyed! §8(§7" + getSectionTime() + "s §8| §7" + Phase.getPhaseTime(TERM_PHASE_INDEX) + "s§8)"));
                    return true;
                }
            }
        } else if (string.equals("The Core entrance is opening!")) {
            //so in "goldor tunnel" can be shown after terms are done
            currentSection = 5;
            endAllSections();
            Debug.sendDebugMessage(Text.literal("Core section"));
        }

        return false;
    }


    public static int getSection() {
        return currentSection;
    }

    public static boolean isGateBlownUp() {
        return gateBlownUp;
    }

    public static boolean inSection(int section) {
        if (section == 0 && (Phase.inP2() || Phase.inP3())) return true;
        return currentSection == section && Phase.inP3();
    }

    public static double getSectionTime() {
        if (currentSection < 0 || currentSection >= splits.length) return -1;
        return splits[currentSection].getRealTime();
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
