package blade.addon.features.dungeon.f7.terms;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MelodyNotification extends CombineableNotification {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)%");

    private final CopyOnWriteArrayList<String> names = new CopyOnWriteArrayList<>();

    private  boolean melodyStarted = false;
    private  boolean ownUsername = false;
    private  String name;
    private  int furthestProgress = 0;

    public MelodyNotification() {
        super("melody warning notification");
    }

    public void init() {
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

        Events.ON_TERMINAL.register((formattedName, _, objective, _, _) -> {
            if (names.contains(formattedName) && objective.equals("terminal")) {
                resetProgress();
            }
            return false;
        });

        Events.ON_SECTION_CHANGE.register(() -> {
            resetProgress();
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            resetProgress();
            return false;
        });
    }

    private  void resetProgress() {
        names.clear();
        furthestProgress = 0;
        name = "";
        melodyStarted = false;
        ownUsername = false;
    }

    @Override
    public boolean shouldRender() {
        return melodyStarted && Floor7.notifiyMelody && !ownUsername;
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.P3);
    }

    @Override
    public boolean enabled() {
        return Floor7.notifiyMelody;
    }

    @Override
    public Component getText() {
        DungeonClass dungeonClass = DungeonClass.getClass(name);
        int num = Math.min(furthestProgress / 25, 3);

        int color = Constants.DARK_PURPLE;
        if (Dungeons.useClassColors) {
            color = DungeonClass.getColor(name);
        }

        MutableComponent nameText = Component.literal(dungeonClass != null ? dungeonClass.name() : name != null? name: "Someone").setStyle(Style.EMPTY.withColor(color).withBold(true));
        MutableComponent infoText = Component.literal(" §r§dhas melody! " + num + "/4").setStyle(Style.EMPTY);

        Component text = nameText.append(infoText);

        return text;
    }
}
