package blade.addon.utils.dungeon;

import blade.addon.features.dungeon.f7.DragSpawnTimer;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DungeonClass {
    ARCHER, BERSERK, HEALER, MAGE, TANK;

    private static final Pattern PATTERN = Pattern.compile("^\\[(Archer|Berserk|Healer|Mage|Tank)]");

    public static DungeonClass currentClass;

    public static void init() {

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                currentClass = null;
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Phase.runStarted() && currentClass != null) return;

            Matcher matcher = PATTERN.matcher(message.getString());
            if (matcher.find()) {
                String dungeonClass = matcher.group(1);
                currentClass = DungeonClass.valueOf(dungeonClass.toUpperCase());
            }
        });

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
