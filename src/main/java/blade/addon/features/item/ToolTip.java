package blade.addon.features.item;

import blade.addon.utils.config.values.Visual;
import blade.addon.utils.data.ItemUtil;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolTip {

    private static final Pattern PATTERN = Pattern.compile("\"candyUsed\":(\\d+),");

    public static void init() {
        ItemTooltipCallback.EVENT.register((item, tooltipContext, tooltipType, lines) -> {

            CompoundTag nbt = ItemUtil.getNbt(item);
            if (nbt == null) return;
            attemptItemQuality(nbt, lines);
            attemptCandy(nbt, lines);

        });
    }


    private static void attemptItemQuality(CompoundTag nbt, List<Component> lines) {
        if (!Visual.itemQuality) return;
        int statboost = nbt.getIntOr("baseStatBoostPercentage", -1);
        if (statboost == -1) return;

        int tier = nbt.getIntOr("item_tier", -1);
        if (tier == -1) return;

        String statBoost = "§4Stat boost: " + statboost + "/50";
        String reqString = "req: " + getFloor(tier);

        lines.add(Component.literal(statBoost + " " + reqString));

    }

    private static String getFloor(int tier) {
        return switch (tier) {
            case 0 -> "E";
            case 1 -> "F1";
            case 2 -> "F2";
            case 3 -> "F3";
            case 4 -> "F4";
            case 5 -> "F5";
            case 6 -> "F6";
            case 7 -> "F7";
            case 8 -> "M5";
            case 9 -> "M6";
            case 10 -> "M7";
            default -> "?";
        };
    }


    private static void attemptCandy(CompoundTag nbt, List<Component> lines) {
        if (!Visual.petCandy) return;
        if (!nbt.getStringOr("id", "").equals("PET")) return;

        String petInfo = nbt.getStringOr("petInfo", null);
        if (petInfo == null) return;

        Matcher matcher = PATTERN.matcher(petInfo);
        if (!matcher.find()) return;

        String candies = matcher.group(1);
        if (candies.equals("0")) return;

        lines.add(Component.literal("§6Candies : " + candies + "/10"));


    }

}
