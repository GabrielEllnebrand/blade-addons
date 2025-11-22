package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyNotifier {

    private final static Text BLOOD_KEY = Text.literal("Blood Key Dropped").setStyle(Style.EMPTY.withBold(true).withColor(Formatting.RED));
    private final static Text WITHER_KEY = Text.literal("Wither Key Dropped").setStyle(Style.EMPTY.withBold(true).withColor(Formatting.GRAY));

    private final static Pattern KEY_ENTITY_PATTERN = Pattern.compile("(Wither|Blood) Key");
    private final static Pattern AUTOMATIC_PICKUP_PATTERN = Pattern.compile("A (Wither|Blood) Key was picked up!");
    private final static Pattern NORMAL_PICKUP_PATTERN = Pattern.compile("has obtained (Wither|Blood) Key!");

    private static boolean hasKey = false;
    private static boolean isBloodKey = false;
    private static boolean sentSound = false;

    @ConfigValue
    public static boolean enableKeyNotifier = false;

    public static void init() {
        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon() || hasKey || !enableKeyNotifier) return;
            if (!DungeonClass.isClass(DungeonClass.ARCHER) && !DungeonClass.isClass(DungeonClass.MAGE)) return;


            Text text = entity.getCustomName();
            if (text == null) return;

            Matcher matcher = KEY_ENTITY_PATTERN.matcher(text.getString());
            if (matcher.find()) {
                String str = matcher.group(1);

                if (str.contains("Blood")) {
                    isBloodKey = true;
                }

                hasKey = true;
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Location.inDungeon() || !hasKey || !enableKeyNotifier) return;
            if (!DungeonClass.isClass(DungeonClass.ARCHER) && !DungeonClass.isClass(DungeonClass.MAGE)) return;

            Matcher matcher = AUTOMATIC_PICKUP_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                hasKey = false;
                isBloodKey = false;
                sentSound = false;
                return;
            }

            matcher = NORMAL_PICKUP_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                hasKey = false;
                isBloodKey = false;
                sentSound = false;
            }
        });

        Events.ON_LOCATION_CHANGE.register(locations -> {
            if (Location.inDungeon()) {
                isBloodKey = false;
                hasKey = false;
                sentSound = false;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (hasKey && !sentSound) {
                ClientPlayerEntity player = client.player;
                if (player == null) return;
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 0.5f);
                sentSound = true;
            }
        });

    }

    @ConfigValue
    public static HUDComponent keyNotifierDisplay = new HUDComponent(0, 0, 120, 10, 1, "",
            () -> Location.inDungeon() && hasKey,
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                if (isBloodKey) {
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, BLOOD_KEY, x, y, 0xffffffff, true);
                } else {
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, WITHER_KEY, x, y, 0xffffffff, true);
                }

            }), () -> enableKeyNotifier
    );
}
