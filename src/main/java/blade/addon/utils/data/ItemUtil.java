package blade.addon.utils.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemUtil {

    public static String getId(ItemStack item) {
        NbtComponent nbt = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) return null;

        NbtCompound compound = nbt.copyNbt();

        return compound.getString("id", null);
    }

    public static String getUuid(ItemStack item) {
        NbtComponent nbt = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) return null;

        NbtCompound compound = nbt.copyNbt();

        return compound.getString("uuid", null);
    }

    public static boolean isHolding(String name) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return player.getMainHandStack().getName().getString().contains(name);
    }

}
