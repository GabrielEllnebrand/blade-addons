package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyNotifier {

    private final static Text BLOOD_KEY = Text.literal("§l§cBlood Key Dropped");
    private final static Text WITHER_KEY = Text.literal("§l§7Wither Key Dropped");

    private final static Pattern KEY_ENTITY_PATTERN = Pattern.compile("(Wither|Blood) Key");
    private final static Pattern AUTOMATIC_PICKUP_PATTERN = Pattern.compile("A (Wither|Blood) Key was picked up!");
    private final static Pattern NORMAL_PICKUP_PATTERN = Pattern.compile("has obtained (Wither|Blood) Key!");

    private static boolean hasKey = false;
    private static boolean isBloodKey = false;

    public static void init() {
        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon() || hasKey || !Dungeons.enableKeyNotifier) return false;
            if (!DungeonClass.isClass(DungeonClass.ARCHER) && !DungeonClass.isClass(DungeonClass.MAGE)) return false;


            Text text = entity.getCustomName();
            if (text == null) return false;

            Matcher matcher = KEY_ENTITY_PATTERN.matcher(text.getString());
            if (matcher.find()) {
                isBloodKey = matcher.group(1).matches("Blood");
                hasKey = true;
                Scheduler.scheduleSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 0.5f);
            }

            return false;
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() || !hasKey || !Dungeons.enableKeyNotifier) return;
            if (!DungeonClass.isClass(DungeonClass.ARCHER) && !DungeonClass.isClass(DungeonClass.MAGE)) return;

            Matcher matcher = AUTOMATIC_PICKUP_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                hasKey = false;
                isBloodKey = false;
                return;
            }

            matcher = NORMAL_PICKUP_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                hasKey = false;
                isBloodKey = false;
            }
        });

        Events.ON_LOCATION_CHANGE.register(locations -> {
            if (Location.inDungeon()) {
                isBloodKey = false;
                hasKey = false;
            }
            return false;
        });
    }

    public static boolean display() {
        return Location.inDungeon() && hasKey;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        Text text = isBloodKey ? BLOOD_KEY : WITHER_KEY;
        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, text, x, y, component.getWidth(), 0xffffffff);
    }
}
