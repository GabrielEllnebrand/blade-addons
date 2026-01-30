package blade.addon.utils.data;

import blade.addon.utils.Misc;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ConcurrentHashMap;

public class EntityUtil {

    private static final ConcurrentHashMap<PlayerEntity, Boolean> playerMap = new ConcurrentHashMap<>();

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
           if (entity instanceof PlayerEntity) {
               playerMap.remove(entity);
           }
        });
    }

    public static boolean isClientPlayer(Entity entity) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static boolean isClientPlayer(String name) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer == null) return false;
        return clientPlayer.getName().getString().equals(name);
    }

    public static boolean isClientPlayer(int id) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer == null) return false;
        return clientPlayer.getId() == id;
    }

    public static boolean isARealPlayer(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (playerMap.containsKey(player)) {
                return playerMap.get(player);
            }

            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return false;
            boolean result = checkPlayer(player, networkHandler);
            playerMap.put(player, result);
            return result;
        }
        return false;
    }

    private static boolean checkPlayer(PlayerEntity player, ClientPlayNetworkHandler networkHandler) {
        PlayerListEntry entry = networkHandler.getPlayerListEntry(player.getUuid());

        //this is a hack which will fail if someone has a really old bugged ign that includes a space
        if (entry != null) {
            String name = entry.getProfile().name();
            return !name.isEmpty() && !name.contains(" ");
        }

        return false;
    }

    public static boolean isWearing(LivingEntity entity, EquipmentSlot slot, String name) {
        if (entity == null || slot == null || name == null) return false;
        ItemStack equippedStack = entity.getEquippedStack(slot);
        return equippedStack.getName().getString().contains(name);
    }

    public static Box getBox(Entity entity) {
        double tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);

        Vec3d pos = Misc.getPos(entity, tickProgress);

        EntityDimensions dimension = entity.getDimensions(entity.getPose());
        return dimension.getBoxAt(pos);
    }

    public static Vec3d getLerpedPos(Entity entity) {
        double tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        return Misc.getPos(entity, tickProgress);
    }
}
