package blade.addon.features.dungeon;

import blade.addon.utils.debug.Debug;
import blade.addon.utils.Misc;
import blade.addon.utils.dungeon.DungeonClass;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

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

        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel world = Minecraft.getInstance().level;
        if (player == null || world == null) {
            Debug.LOGGER.warn("Player or World is null");
            return;
        }

        Scoreboard scoreboard = world.getScoreboard();
        for (PlayerTeam team : scoreboard.getPlayerTeams()) {
            String teamStr = (team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString()).replaceAll("§.", "");
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
            Misc.addChatMessage(Component.literal("Cant find all players or the players class"));
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
        Misc.addChatMessage(Component.literal("Executing command: " + leapOrder));
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
