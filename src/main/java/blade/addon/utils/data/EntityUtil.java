package blade.addon.utils.data;

import blade.addon.utils.Misc;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ConcurrentHashMap;

public class EntityUtil {

    private static final ConcurrentHashMap<Player, Boolean> playerMap = new ConcurrentHashMap<>();

    public static void init() {
        Events.ON_WORLD_CHANGE.register(() -> {
            playerMap.clear();
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            playerMap.clear();
            return false;
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
           if (entity instanceof Player) {
               playerMap.remove(entity);
           }
        });
    }

    public static boolean isClientPlayer(Entity entity) {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static boolean isClientPlayer(String name) {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return false;
        return clientPlayer.getName().getString().equals(name);
    }

    public static boolean isClientPlayer(int id) {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return false;
        return clientPlayer.getId() == id;
    }

    public static boolean isARealPlayer(Entity entity) {
        if (entity instanceof Player player) {
            if (playerMap.containsKey(player)) {
                return playerMap.get(player);
            }

            ClientPacketListener networkHandler = Minecraft.getInstance().getConnection();
            if (networkHandler == null) return false;
            boolean result = checkPlayer(player, networkHandler);
            playerMap.put(player, result);
            return result;
        }
        return false;
    }

    private static boolean checkPlayer(Player player, ClientPacketListener networkHandler) {
        PlayerInfo entry = networkHandler.getPlayerInfo(player.getUUID());

        //this is a hack which will fail if someone has a really old bugged ign that includes a space
        if (entry != null) {
            String name = entry.getProfile().name();
            return !name.isEmpty() && !name.contains(" ");
        }

        return false;
    }

    public static boolean isWearing(LivingEntity entity, EquipmentSlot slot, String name) {
        if (entity == null || slot == null || name == null) return false;
        ItemStack equippedStack = entity.getItemBySlot(slot);
        return equippedStack.getHoverName().getString().contains(name);
    }

    public static AABB getBox(Entity entity) {
        double tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);

        Vec3 pos = Misc.getPos(entity, tickProgress);

        EntityDimensions dimension = entity.getDimensions(entity.getPose());
        return dimension.makeBoundingBox(pos);
    }

    public static Vec3 getLerpedPos(Entity entity) {
        double tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        return Misc.getPos(entity, tickProgress);
    }
}
