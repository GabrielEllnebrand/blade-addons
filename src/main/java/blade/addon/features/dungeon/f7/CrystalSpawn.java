package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.times.PersonalBests;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrystalSpawn {

    private static final Pattern PATTERN_1 = Pattern.compile("^\\[BOSS] Maxor: THAT BEAM! IT HURTS! IT HURTS!!$");
    private static final Pattern PATTERN_2 = Pattern.compile("^\\[BOSS] Maxor: YOU TRICKED ME!$");
    private static final Pattern RELIC_PICK_UP = Pattern.compile("(\\w+) picked up an Energy Crystal!$");

    private static long pickupTime = 0;
    private static boolean pickedUp = false;

    private static final int TICK_SPAWN = 34;
    private static int tick = 0;

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon() || !Phase.inP1() || !Floor7.enableCrystalSpawnTime) return false;

            Matcher matcher = PATTERN_1.matcher(text.getString());
            if (matcher.find()) {
                tick = TICK_SPAWN;
                return false;
            }

            matcher = PATTERN_2.matcher(text.getString());
            if (matcher.find()) {
                tick = TICK_SPAWN;
                return false;
            }

            matcher = RELIC_PICK_UP.matcher(text.getString());
            if (matcher.find()) {
                String name = matcher.group(1);
                if (Misc.isClientPlayer(name)) {
                    pickupTime = System.currentTimeMillis();
                    pickedUp = true;
                }

            }
            return false;
        });
        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            tick = 0;
            pickedUp = false;
            return false;
        });

        Events.ON_ENTITY_SPAWNED.register((entity, world) -> {
            if (!pickedUp || !Location.inDungeon() || !Floor7.enableCrystalSpawnTime || !Phase.inP1()) return false;

            if (entity instanceof EndCrystalEntity crystal) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player == null) return false;

                double distance = Misc.getDistance(player, crystal);

                if (distance < 6 && crystal.getY() == 224.375) {
                    PersonalBests.crystalTime.testNewTime(Text.literal("§aCrystal placed in "), pickupTime);
                    pickedUp = false;
                }

            }
            return false;
        });
    }

    public static boolean display() {
        return tick > 0 && Location.inDungeon() && Floor7.enableCrystalSpawnTime;
    }

    public static void render(HUDComponent component, DrawContext context) {
        RenderUtils.drawTimer(component, context, tick, Constants.LIGHT_PURPLE);
    }
}
