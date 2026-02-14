package blade.addon.utils.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.List;

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

    public static boolean itemHasName(ItemStack itemStack, String name) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || itemStack == null) return false;
        return itemStack.getName().getString().contains(name);
    }

    public static boolean containsLore(ItemStack item, String contain) {
        if (item ==null) return false;
        LoreComponent lore = item.get(DataComponentTypes.LORE);
        if (lore == null) return false;

        List<Text> lines = lore.lines();
        if (lines.isEmpty()) return false;

        for (Text line : lines.reversed()) {
            String string = line.getString();
            if (string.contains(contain)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCaseLore(ItemStack item, String contain) {
        if (item ==null) return false;
        LoreComponent lore = item.get(DataComponentTypes.LORE);
        if (lore == null) return false;

        List<Text> lines = lore.lines();
        if (lines.isEmpty()) return false;

        for (Text line : lines.reversed()) {
            String string = line.getString();
            if (string.toLowerCase().contains(contain.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsNBT(ItemStack item, String contain) {
        NbtComponent nbt = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) {
            return false;
        }

        return  nbt.toString().contains(contain);
    }

}
