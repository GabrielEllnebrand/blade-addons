package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyNotifier {

    private final static Text BLOOD_KEY = Text.literal("§l§cBlood Key Dropped");
    private final static Text WITHER_KEY = Text.literal("§l§7Wither Key Dropped");

    //Thanks to mio for this key detection system
    private final static String WITHER_UUID = "2865274b-3097-394e-8149-ec629c72d850";
    private final static String BLOOD_UUID = "73f6d1f9-df41-3d1d-b98c-e1442d915885";

    private final static Pattern AUTOMATIC_PICKUP_PATTERN = Pattern.compile("A (Wither|Blood) Key was picked up!");
    private final static Pattern NORMAL_PICKUP_PATTERN = Pattern.compile("has obtained (Wither|Blood) Key!");

    private final static CopyOnWriteArrayList<Entity> foundKeys = new CopyOnWriteArrayList<>();

    private static boolean hasKey = false;
    private static boolean isBloodKey = false;

    public static void init() {
        ClientTickEvents.END_WORLD_TICK.register((world) -> {
            if (!Location.inDungeon() || hasKey || !Dungeons.enableKeyNotifier) return;

            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStandEntity armorStand && !foundKeys.contains(entity)) {
                    ItemStack head = armorStand.getEquippedStack(EquipmentSlot.HEAD);
                    ProfileComponent profile = head.get(DataComponentTypes.PROFILE);
                    if (profile == null) continue;
                    String uuid = profile.getGameProfile().id().toString();
                    if (uuid == null) continue;

                    if (uuid.equals(WITHER_UUID)) {
                        foundKeys.add(entity);
                        hasKey = true;
                        if (isValidClass()) {
                            Scheduler.scheduleSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 0.5f);
                        }
                        return;
                    }

                    if (uuid.equals(BLOOD_UUID)) {
                        foundKeys.add(entity);
                        isBloodKey = true;
                        hasKey = true;

                        if (isValidClass()) {
                            Scheduler.scheduleSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 0.5f);
                        }
                        return;
                    }
                }
            }
        });

        Events.ON_GAME_MESSAGE.register(message -> {
            if (!Location.inDungeon() || !hasKey || !Dungeons.enableKeyNotifier) return false;
            if (!isValidClass()) return false;

            Matcher matcher = AUTOMATIC_PICKUP_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                hasKey = false;
                isBloodKey = false;
                return false;
            }

            matcher = NORMAL_PICKUP_PATTERN.matcher(message.getString());
            if (matcher.find()) {
                hasKey = false;
                isBloodKey = false;
            }

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(locations -> {
            if (Location.inDungeon()) {
                isBloodKey = false;
                hasKey = false;
                foundKeys.clear();
            }
            return false;
        });
    }

    private static boolean isValidClass() {
       return Dungeons.displayKeyForAllClasses || DungeonClass.isClass(DungeonClass.ARCHER) || DungeonClass.isClass(DungeonClass.MAGE);
    }

    public static boolean display() {
        return Location.inDungeon() && hasKey && isValidClass();
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        Text text = isBloodKey ? BLOOD_KEY : WITHER_KEY;
        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, text, x, y, component.getWidth(), 0xffffffff);
    }
}
