package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.components.CombineableNotification;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyNotification extends CombineableNotification {

    private final static Component BLOOD_KEY = Component.literal("§l§cBlood Key Dropped");
    private final static Component WITHER_KEY = Component.literal("§l§7Wither Key Dropped");

    private final static Pattern AUTOMATIC_PICKUP_PATTERN = Pattern.compile("A (Wither|Blood) Key was picked up!");
    private final static Pattern NORMAL_PICKUP_PATTERN = Pattern.compile("has obtained (Wither|Blood) Key!");

    private boolean hasKey = false;
    private boolean isBloodKey = false;

    public KeyNotification() {
        super("Key notifier");
    }

    public void init() {

        Events.ON_ENTITY_TRACKED.register((entity, _) -> {
            if (!Location.inDungeon() || hasKey || !Dungeons.enableKeyNotifier || Phase.inBoss()) return false;

            if (entity instanceof ArmorStand possibleKey) {

                Component customName = possibleKey.getCustomName();
                if (customName == null) return false;

                String name = customName.getString();
                if (name.equals("Wither Key")) {
                    hasKey = true;
                    if (isValidClass()) {
                        Scheduler.scheduleSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 2, 0.5f);
                    }
                } else if (name.equals("Blood Key")) {
                    isBloodKey = true;
                    hasKey = true;

                    if (isValidClass()) {
                        Scheduler.scheduleSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 2, 0.5f);
                    }
                }

            }

            return false;
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

        Events.ON_LOCATION_CHANGE.register(_ -> {
            if (Location.inDungeon()) {
                isBloodKey = false;
                hasKey = false;
            }
            return false;
        });
    }

    @Override
    public boolean shouldRender() {
        return Location.inDungeon() && hasKey && isValidClass() && !Phase.inBoss();
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.CLEAR);
    }

    @Override
    public boolean enabled() {
        return Dungeons.enableKeyNotifier;
    }

    @Override
    public Component getText() {
        return isBloodKey ? BLOOD_KEY : WITHER_KEY;
    }

    private boolean isValidClass() {
       return (Dungeons.displayKeyForAllClasses || DungeonClass.isClass(DungeonClass.ARCHER) || DungeonClass.isClass(DungeonClass.MAGE) && Dungeons.enableKeyNotifier);
    }
}
