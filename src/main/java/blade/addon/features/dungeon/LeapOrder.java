package blade.addon.features.dungeon;

import blade.addon.utils.debug.Debug;
import blade.addon.utils.Misc;
import blade.addon.utils.dungeon.DungeonClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeapOrder {

    private static final Pattern PATTERN = Pattern.compile("\\[([TBMAH])] (\\w+) ");

    private static final DungeonClass[] ARCHER_ORDER = {DungeonClass.BERSERK, DungeonClass.HEALER, DungeonClass.MAGE, DungeonClass.TANK};
    private static final DungeonClass[] BERSERK_ORDER = {DungeonClass.ARCHER, DungeonClass.HEALER, DungeonClass.MAGE, DungeonClass.TANK};
    private static final DungeonClass[] MAGE_ORDER = {DungeonClass.ARCHER, DungeonClass.BERSERK, DungeonClass.HEALER, DungeonClass.TANK};
    private static final DungeonClass[] TANK_ORDER = {DungeonClass.TANK, DungeonClass.BERSERK, DungeonClass.HEALER, DungeonClass.MAGE};
    private static final DungeonClass[] HEALER_ORDER = {DungeonClass.ARCHER, DungeonClass.BERSERK, DungeonClass.MAGE, DungeonClass.TANK};

    private static final DungeonClass[] ALPHABETICAL_ORDER = {DungeonClass.ARCHER, DungeonClass.BERSERK, DungeonClass.HEALER, DungeonClass.MAGE, DungeonClass.TANK};

    public static void leapOrder(String backupMage, boolean odinOrder) {

        HashMap<DungeonClass, String> classNameMap = new HashMap<>();
        DungeonClass playersClass = null;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (player == null || world == null) {
            Debug.LOGGER.warn("Player or World is null");
            return;
        }

        Scoreboard scoreboard = world.getScoreboard();
        for (Team team : scoreboard.getTeams()) {
            String teamStr = team.getPrefix().getString() + team.getSuffix().getString();
            Matcher matcher = PATTERN.matcher(teamStr);
            if (matcher.find()) {
                DungeonClass dungeonClass = getClass(matcher.group(1));
                String name = matcher.group(2);
                if (dungeonClass == null) continue;

                if (name.equalsIgnoreCase(backupMage)) {
                    dungeonClass = DungeonClass.HEALER;
                }

                if (name.equalsIgnoreCase(player.getName().getString())) {
                    playersClass = dungeonClass;
                    continue;
                }

               classNameMap.put(dungeonClass, name);
            }
        }

        if (classNameMap.size() != 4 || playersClass == null) {
            Misc.addChatMessage(Text.literal("Cant find all players or the players class"));
            return;
        }
        DungeonClass[] order;
        if (odinOrder) {
            order = getOdinOrder(playersClass);
        } else {
            order = new DungeonClass[4];
            int index = 0;
            for (int i = 0; i < 5; i++) {
                if (ALPHABETICAL_ORDER[i] == playersClass) continue;
                order[index] = ALPHABETICAL_ORDER[i];
                index++;
            }
        }

        String leapOrder = "odin leaporder " + classNameMap.get(order[0]) + " " + classNameMap.get(order[1]) + " " + classNameMap.get(order[2]) + " " + classNameMap.get(order[3]);
        Misc.addChatMessage(Text.literal("Executing command: " + leapOrder));
        Misc.executeCommand(leapOrder);
    }

    private static DungeonClass[] getOdinOrder(DungeonClass playerClass) {
        return switch (playerClass) {
            case ARCHER -> ARCHER_ORDER;
            case BERSERK -> BERSERK_ORDER;
            case HEALER -> HEALER_ORDER;
            case MAGE -> MAGE_ORDER;
            case TANK -> TANK_ORDER;
        };
    }

    private static DungeonClass getClass(String string) {
        return switch (string) {
            case "A" -> DungeonClass.ARCHER;
            case "B" -> DungeonClass.BERSERK;
            case "H" -> DungeonClass.HEALER;
            case "M" -> DungeonClass.MAGE;
            case "T" -> DungeonClass.TANK;
            default -> null;
        };
    }

}
