package blade.addon.utils.dungeon;

import blade.addon.features.dungeon.f7.dragons.DragSpawnTimer;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

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

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                currentClass = null;
                nameClassMap.clear();
            }
            return false;
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Phase.runStarted() && currentClass != null) return;

            Matcher matcher = PATTERN.matcher(message.getString());
            if (matcher.find()) {
                String dungeonClass = matcher.group(1);
                currentClass = parseClass(dungeonClass);
            }
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (receivedEntry == null) return false;
            Text text = receivedEntry.displayName();
            if (text == null) return false;
            String string = text.getString();
            Matcher matcher = NAME_CLASS_PATTERN.matcher(string);

            if (matcher.find()) {
                String name = matcher.group(1).replaceAll(" .+", "");
                DungeonClass className = parseClass(matcher.group(2));
                if (className == null) return false;
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

    public static DungeonClass getClass(PlayerEntity player) {
        if (player == null) return null;
        return getClass(player.getName().getString());
    }

    public static boolean isTeammate(PlayerEntity player) {
        if (player == null || EntityUtil.isClientPlayer(player)) return false;
        String name = player.getName().getString();
        return nameClassMap.containsKey(name);
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
}
