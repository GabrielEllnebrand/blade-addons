package blade.addon.utils.dungeon;

import blade.addon.features.dungeon.f7.dragons.DragSpawnTimer;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DungeonClass {
    ARCHER, BERSERK, HEALER, MAGE, TANK;

    private static final Pattern PATTERN = Pattern.compile("^\\[(Archer|Berserk|Healer|Mage|Tank)]");
    private static final Pattern NAME_CLASS_PATTERN = Pattern.compile("^\\[\\d+] (.+) \\((Archer|Berserk|Healer|Mage|Tank) ");

    private static final ConcurrentHashMap<String, DungeonClass> nameClassMap = new ConcurrentHashMap<>();
    public static DungeonClass currentClass;

    public static void init() {

        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.runJustStarted()) {
                reset();
            }
            return false;
        });

        Events.ON_RUN_END.register(() -> {
            reset();
            return false;
        });

        Events.ON_GAME_MESSAGE.register(message -> {
            if (!Phase.runStarted() && currentClass != null) return false;

            Matcher matcher = PATTERN.matcher(message.getString());
            if (matcher.find()) {
                String dungeonClass = matcher.group(1);
                currentClass = parseClass(dungeonClass);
            }

            return false;
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (receivedEntry == null) return false;
            Component text = receivedEntry.displayName();
            if (text == null) return false;
            String string = text.getString();
            Matcher matcher = NAME_CLASS_PATTERN.matcher(string);

            if (matcher.find()) {
                String name = matcher.group(1).replaceAll(" .+", "");
                DungeonClass className = parseClass(matcher.group(2));
                if (className == null) return false;

                if (EntityUtil.isClientPlayer(name)) {
                    currentClass =  className;
                }

                nameClassMap.put(name, className);
                return false;
            }

            return false;
        });

    }

    private static DungeonClass parseClass(String name) {
        try {
            return DungeonClass.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static DungeonClass getClass(String playerName) {
        if (playerName == null) return null;
        if (nameClassMap.containsKey(playerName)) {
            return nameClassMap.get(playerName);
        }
        return null;
    }

    public static DungeonClass getClass(Player player) {
        if (player == null) return null;
        return getClass(player.getName().getString());
    }

    public static boolean isTeammate(Player player) {
        if (player == null || EntityUtil.isClientPlayer(player)) return false;
        String name = player.getName().getString();
        return nameClassMap.containsKey(name);
    }

    public static String getNameFromMessage(String message) {
        for (Iterator<String> it = nameClassMap.keys().asIterator(); it.hasNext(); ) {
            String name = it.next();
            if (message.contains(name)) return name;
        }

        return null;
    }

    public static int getColor(DungeonClass dungeonClass) {
        if (dungeonClass == null) return 0xffffffff;

        return switch (dungeonClass) {
            case ARCHER -> Dungeons.archerColor;
            case BERSERK -> Dungeons.berserkColor;
            case HEALER -> Dungeons.healerColor;
            case MAGE -> Dungeons.mageColor;
            case TANK -> Dungeons.tankColor;
        };
    }

    public static String getChar(DungeonClass dungeonClass) {
        if (dungeonClass == null) return "?";

        return switch (dungeonClass) {
            case ARCHER -> "A";
            case BERSERK -> "B";
            case HEALER -> "H";
            case MAGE -> "M";
            case TANK -> "T";
        };
    }

    public static int getColor(String name) {
        return getColor(getClass(name));
    }

    public static boolean isClass(DungeonClass dungeonClass) {
        return currentClass == dungeonClass;
    }

    public static boolean isArchTeam() {
        return currentClass == DungeonClass.ARCHER || currentClass == DungeonClass.TANK || (currentClass == DungeonClass.HEALER && Floor7.healerTeam == DragSpawnTimer.Team.ARCHER_TEAM);
    }

    public static boolean isBersTeam() {
        return currentClass == DungeonClass.BERSERK || currentClass == DungeonClass.MAGE || (currentClass == DungeonClass.HEALER && Floor7.healerTeam == DragSpawnTimer.Team.BERS_TEAM);
    }

    private static void reset() {
        currentClass = null;
        nameClassMap.clear();
    }

    public static void printClasses() {
        Misc.addChatMessage(Component.literal("Classes"));
        nameClassMap.forEach((name, clazz) -> {
            Misc.addChatMessage(Component.literal("Name: " + name + "Class: " + (clazz != null? clazz.name(): null)));
        });
    }
}
