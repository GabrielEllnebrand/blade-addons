package blade.addon.utils;

import blade.addon.features.item.ItemRarity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class Misc {
    public static final MinecraftClient instance = MinecraftClient.getInstance();

    public static boolean isClientPlayer(PlayerEntity entity) {
        ClientPlayerEntity clientPlayer = instance.player;
        if (clientPlayer == null) return false;
        return clientPlayer == entity;
    }

    public static void addChatMessage(Text text) {
        try {
            InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
            gameHud.getChatHud().addMessage(text);
        } catch (IndexOutOfBoundsException ignored) {
            Debug.addDebugLog("Chat message failed to get added");
        }
    }

    public static void setTitle(Text text) {
        MinecraftClient.getInstance().inGameHud.setTitle(text);
    }

    public static void executeCommand(String string) {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return;
        networkHandler.sendChatCommand(string);
    }

    public static ItemRarity getRarity(ItemStack item) {
        LoreComponent lore = item.get(DataComponentTypes.LORE);
        if (lore == null)  return ItemRarity.NONE;

        List<Text> lines = lore.lines();
        if (lines.isEmpty()) return ItemRarity.NONE;
        Text line = lines.getLast();

        if (line == null) return ItemRarity.NONE;
        String string = line.getString();
        String[] rarityStrings = string.split(" ");

        for (String testString: rarityStrings) {
            try {
               ItemRarity rarity = ItemRarity.valueOf(testString);
               return rarity;
            } catch (IllegalArgumentException ignored) {

            }
        }
        return ItemRarity.NONE;
    }
}
