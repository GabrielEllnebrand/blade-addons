package blade.addon.features.dungeon;

import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobHighlight {

    private static final Pattern STAR_PATTERN = Pattern.compile("✯");

    private static final ConcurrentHashMap<Entity, Integer> mobs = new ConcurrentHashMap<>();

    public static void init() {
       /* Events.ON_ENTITY_TRACKED.register(((entity, world) -> {
            if (entity == null || world == null) return;
            Text text = entity.getCustomName();
            if (text == null) return;
            String string = text.getString();
            Matcher matcher = STAR_PATTERN.matcher(string);
            if (matcher.find()) {
                attemptToAddStaredMob(entity, world);
            }


        }));

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity == null) return;
            mobs.remove(entity);
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            mobs.clear();
        }); */
    }

    public static boolean hasEntity(Entity entity) {
        if (entity == null) return false;
        return mobs.containsKey(entity);
    }

    public static int getColor(Entity entity) {
        return mobs.get(entity);
    }

    private static void attemptToAddStaredMob(Entity entity, ClientWorld world) {
        /*if (entities.isEmpty()) return;

        Vec3d namePos = entity.getPos();
        Entity closet = null;
        double distance = Integer.MAX_VALUE;
        for (Entity curr: entities) {
            double currDistance = curr.getPos().distanceTo(namePos);
            if (currDistance < distance) {
                closet = curr;
                distance = currDistance;
            }
        }

        if (closet == null) return;
        mobs.put(closet, 0xffffffff); */

    }
}
