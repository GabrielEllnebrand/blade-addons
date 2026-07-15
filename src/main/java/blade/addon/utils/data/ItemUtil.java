package blade.addon.utils.data;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

public class ItemUtil {

    public static String getId(ItemStack item) {
        CustomData nbt = item.get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return null;

        CompoundTag compound = nbt.copyTag();

        return compound.getStringOr("id", null);
    }

    public static String getUuid(ItemStack item) {
        CustomData nbt = item.get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return null;

        CompoundTag compound = nbt.copyTag();

        return compound.getStringOr("uuid", null);
    }

    public static boolean isHolding(String name) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.getMainHandItem().getHoverName().getString().contains(name);
    }

    public static boolean itemHasName(ItemStack itemStack, String name) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || itemStack == null) return false;
        return itemStack.getHoverName().getString().contains(name);
    }

    public static boolean containsLore(ItemStack item, String contain) {
        List<Component> lines = getLore(item);
        if (lines == null) return false;

        for (Component line : lines.reversed()) {
            String string = line.getString();
            if (string.contains(contain)) {
                return true;
            }
        }
        return false;
    }

    public static List<Component> getLore(ItemStack item) {
        if (item ==null) return null;
        ItemLore lore = item.get(DataComponents.LORE);
        if (lore == null) return null;

        List<Component> lines = lore.lines();
        return lines;
    }

    public static Component findLore(ItemStack item, String contain) {
        List<Component> lines = getLore(item);
        if (lines == null) return null;

        for (Component line : lines.reversed()) {
            String string = line.getString();
            if (string.contains(contain)) {
                return line;
            }
        }
        return null;
    }

    public static boolean containsIgnoreCaseLore(ItemStack item, String contain) {
        if (item ==null) return false;
        ItemLore lore = item.get(DataComponents.LORE);
        if (lore == null) return false;

        List<Component> lines = lore.lines();
        if (lines.isEmpty()) return false;

        for (Component line : lines.reversed()) {
            String string = line.getString();
            if (string.toLowerCase().contains(contain.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsNBT(ItemStack item, String contain) {
        CustomData nbt = item.get(DataComponents.CUSTOM_DATA);
        if (nbt == null) {
            return false;
        }

        return  nbt.toString().contains(contain);
    }

}
