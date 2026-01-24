package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MelodyWarning {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)%");

    private static final CopyOnWriteArrayList<String> names = new CopyOnWriteArrayList<>();

    private static boolean melodyStarted = false;
    private static boolean ownUsername = false;
    private static String name;
    private static int furthestProgress = 0;

    public static void init() {
        Events.ON_PARTY_MESSAGE.register((username, message) -> {
            if (!Floor7.notifiyMelody) return false;
            if (!Location.inDungeon() || !Phase.inTerminals()) return false;

            Matcher matcher = PATTERN.matcher(message);
            if (matcher.find()) {
                int progress = Integer.parseInt(matcher.group(1));
                if (progress > furthestProgress) {
                    melodyStarted = true;
                    name = username;
                    furthestProgress = progress;
                    ownUsername = EntityUtil.isClientPlayer(username);
                    if (!names.contains(username)) {
                        names.add(username);
                    }
                }
            }
            return false;
        });

        Events.ON_TERMINAL.register((formattedName, action, objective, current, total) -> {
            if (names.contains(formattedName) && objective.equals("terminal")) {
                reset();
            }
            return false;
        });

        Events.ON_SECTION_CHANGE.register(() -> {
            reset();
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
           reset();
            return false;
        });
    }

    private static void reset() {
        names.clear();
        furthestProgress = 0;
        name = "";
        melodyStarted = false;
        ownUsername = false;
    }

    public static boolean display() {
        return melodyStarted && Floor7.notifiyMelody && !ownUsername;
    }

    public static void render(HUDComponent component, DrawContext context) {
        DungeonClass dungeonClass = DungeonClass.getClass(name);
        int num = Math.min(furthestProgress / 25, 3);

        int color = Constants.DARK_PURPLE;
        if (Dungeons.useClassColors) {
            color = DungeonClass.getColor(name);
        }

        MutableText nameText = Text.literal(dungeonClass != null ? dungeonClass.name() : name != null? name: "Someone").setStyle(Style.EMPTY.withColor(color).withBold(true));
        MutableText infoText = Text.literal(" §r§dhas melody! " + num + "/4").setStyle(Style.EMPTY);

        Text text = nameText.append(infoText);

        RenderUtils.drawCenteredText(context, component, text);
    }

}
