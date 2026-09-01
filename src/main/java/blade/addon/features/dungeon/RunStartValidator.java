package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.data.PartyUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunStartValidator extends CombineableNotification {

    private static final Pattern STARTING_PATTERN = Pattern.compile("^Starting in \\d second(s)?");
    private static final Pattern CLASS_PATTERN = Pattern.compile("\\[([ABHMT])]");

    private static final Component DUPE_CLASS_TEXT = Component.literal("§cDuplicate Class Detected");
    private static final Component PLAYER_COUNT_TEXT = Component.literal("§cNot enough players");

    private static boolean hasDupeClasses = false;
    private static boolean notEnoughPlayers = false;

    private static boolean hasTicked = false;

    public RunStartValidator() {
        super("Run start validator");
    }

    public void init() {
        Events.ON_GAME_MESSAGE.register(message -> {
            if (!Location.inDungeon() || Phase.runStarted() || !Dungeons.detectDuplicateClass) return false;

            Matcher matcher = STARTING_PATTERN.matcher(message.getString());
            if (!matcher.find()) return false;
            hasTicked = true;

            if (validate()) {
                Scheduler.scheduleSound(SoundEvents.NOTE_BLOCK_PLING.value(), 2, 1);
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            if (Location.inDungeon()) {
                PartyUtil.sendPacket();
                resetInfo();
            }

            return false;
        });

        Events.ON_PLAYER_ENTRY.register(_ -> {
            //will only check after the first game message in case of false positives
            if (!hasTicked) return false;
            if (!Location.inDungeon() || Phase.runStarted() || (!Dungeons.detectDuplicateClass && !Dungeons.detectPlayerCount))
                return false;
            validate();
            return false;
        });
    }

    @Override
    public boolean shouldRender() {
        return Location.inDungeon() && !Phase.runStarted() && (hasDupeClasses || notEnoughPlayers);
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.CLEAR);
    }

    @Override
    public boolean enabled() {
        return (Dungeons.detectDuplicateClass || Dungeons.detectPlayerCount);

    }

    @Override
    public Component getText() {
        if (hasDupeClasses) return DUPE_CLASS_TEXT;
        else if (notEnoughPlayers) return PLAYER_COUNT_TEXT;
        else return Component.literal("§cSome warning text");
    }

    private boolean validate() {
        ClientLevel world = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (world == null || player == null) return false;

        Scoreboard scoreboard = world.getScoreboard();
        HashMap<String, Integer> map = readScoreBoard(scoreboard);

        checkPlayerCount(map);
        checkDuplicateClasses(map);
        return hasDupeClasses || notEnoughPlayers;
    }

    private void checkDuplicateClasses(HashMap<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String clazz = entry.getKey();
            Integer count = entry.getValue();
            if (clazz.equals("M") && Dungeons.ignoreDupeMage) continue;

            if (count > 1) {
                hasDupeClasses = true;
                return;
            }
        }

        hasDupeClasses = false;
    }

    private void checkPlayerCount(HashMap<String, Integer> map) {
        if (!Dungeons.detectPlayerCount) {
            notEnoughPlayers = false;
            return;
        }
        int count = 0;
        for (int num : map.values()) {
            count += num;
        }

        notEnoughPlayers = count < PartyUtil.getPlayerCount();
    }

    private void resetInfo() {
        hasDupeClasses = false;
        notEnoughPlayers = false;
        hasTicked = false;
    }

    private HashMap<String, Integer> readScoreBoard(Scoreboard scoreboard) {
        HashMap<String, Integer> map = new HashMap<>();

        for (PlayerTeam team : scoreboard.getPlayerTeams()) {
            String teamStr = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();
            Matcher matcher = CLASS_PATTERN.matcher(teamStr);
            if (!matcher.find()) continue;

            String clazz = matcher.group(1);
            Integer count = map.get(clazz);
            if (count == null) count = 0;
            map.put(clazz, count + 1);
        }
        return map;
    }
}
