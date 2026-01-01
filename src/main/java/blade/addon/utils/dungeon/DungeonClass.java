package blade.addon.utils.dungeon;

import blade.addon.features.dungeon.f7.DragSpawnTimer;
import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.Collection;
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

        Events.ON_PHASE_CHANGE.register(() -> {
            if (Phase.runJustStarted() && Location.inDungeon()) {
                Scheduler.scheduleTask(DungeonClass::detectClasses, 140);
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

    private static void detectClasses() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) return;
        Collection<PlayerListEntry> entryCollection = networkHandler.getPlayerList();

        for (PlayerListEntry entry : entryCollection) {
            Text text = entry.getDisplayName();
            if (text == null) continue;
            String string = text.getString();
            Matcher matcher = NAME_CLASS_PATTERN.matcher(string);

            if (matcher.find()) {
                String name = matcher.group(1).replaceAll(" .+", "");
                DungeonClass className = parseClass(matcher.group(2));
                if (className == null) continue;
                nameClassMap.put(name, className);
            }
        }

        //incase currentClass could not detect class before
        if (currentClass == null) {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            String name = player.getStringifiedName();
            currentClass = nameClassMap.get(name);

        }
    }

    public static DungeonClass getClass(String playerName) {
        if (playerName == null) return null;
        if (nameClassMap.containsKey(playerName)) {
            return nameClassMap.get(playerName);
        }
        return null;
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
