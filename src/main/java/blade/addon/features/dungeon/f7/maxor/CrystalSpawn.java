package blade.addon.features.dungeon.f7.maxor;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.times.PersonalBests;
import config.practical.hud.HUDComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public class CrystalSpawn {

    private static final Pattern RELIC_PICK_UP = Pattern.compile("(\\w+) picked up an Energy Crystal!$");

    private static final int REMINDER_TICK = 240;
    private static final int TICK_SPAWN = 34;

    private static long pickupTime = 0;
    private static boolean pickedUp = false;

    private static int tick = 0;
    private static int tickSincePicked = 0;

    public static void init() {

        Events.ON_GAME_MESSAGE.register(text -> {
            if (!Location.inDungeon() || !Phase.inP1() || (!Floor7.enableCrystalSpawnTime && !Floor7.crystalPlaceReminder))
                return false;
            String string = text.getString();

            if (string.equals("[BOSS] Maxor: THAT BEAM! IT HURTS! IT HURTS!!")) {
                tick = TICK_SPAWN;
                return false;
            }

            if (string.equals("[BOSS] Maxor: YOU TRICKED ME!")) {
                tick = TICK_SPAWN;
                return false;
            }

            Matcher matcher = RELIC_PICK_UP.matcher(text.getString());
            if (matcher.find()) {
                String name = matcher.group(1);
                if (EntityUtil.isClientPlayer(name)) {
                    pickupTime = System.currentTimeMillis();
                    pickedUp = true;
                }

            }
            return false;
        });
        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            if (pickedUp) {
                tickSincePicked++;
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            tick = 0;
            tickSincePicked = 0;
            pickedUp = false;
            return false;
        });

        Events.ON_ENTITY_SPAWNED.register((entity, world) -> {
            if (!pickedUp || !Location.inDungeon() || !Floor7.enableCrystalSpawnTime || !Phase.inP1()) return false;

            if (entity instanceof EndCrystal crystal) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return false;

                double distance = Misc.getDistance(player, crystal);

                if (distance < 6 && crystal.getY() == 224.375) {
                    PersonalBests.crystalTime.testNewTime(Component.literal("§aCrystal placed in "), pickupTime);
                    pickedUp = false;
                    tickSincePicked = 0;
                }

            }
            return false;
        });
    }

    public static boolean display() {
        return tick > 0 && Location.inDungeon() && Phase.inP1() && Floor7.enableCrystalSpawnTime;
    }

    public static void render(HUDComponent component, GuiGraphics context) {
        RenderUtils.drawTimer(component, context, tick, Constants.LIGHT_PURPLE);
    }

    public static boolean displayNotification() {
        return (Floor7.instantlyDisplayCrystalReminder || tickSincePicked > REMINDER_TICK) && Location.inDungeon() && Phase.inP1() && Floor7.crystalPlaceReminder && pickedUp;
    }

    public static void renderNotification(HUDComponent component, GuiGraphics context) {
        RenderUtils.drawCenteredText(context, component, Component.literal("§bPlace Crystal!"));
    }


}
