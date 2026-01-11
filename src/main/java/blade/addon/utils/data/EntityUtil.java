package blade.addon.utils.data;

import blade.addon.utils.interfaces.PlayerDataHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class EntityUtil {

    public static boolean isClientPlayer(PlayerEntity entity) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
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

    public static boolean isARealPlayer(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            PlayerDataHolder dataHolder = (PlayerDataHolder) (Object) player;
            return dataHolder.blade_addons$isRealPlayer();

        }
        return false;
    }

    public static boolean isWearing(LivingEntity entity, EquipmentSlot slot, String name) {
        if (entity == null || slot == null || name == null) return false;
        ItemStack equippedStack = entity.getEquippedStack(slot);
        return equippedStack.getName().getString().contains(name);
    }
}
