package blade.addon.utils;

import blade.addon.features.item.ItemRarityHighlight;
import blade.addon.utils.config.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Keybinds {

    private static KeyBinding openConfig;
    private static KeyBinding getItemLore;
    private static KeyBinding getItemCustomData;
    private static KeyBinding trades;
    private static KeyBinding potions;


    public static void init() {

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(Constants.NAMESPACE));

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "opens Config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category));

        getItemLore = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Grabs the items lore",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        getItemCustomData = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Grabs the items custom data",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        trades = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Opens the trades menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        potions = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Opens the potion bag",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));


        ClientTickEvents.END_CLIENT_TICK.register(Keybinds::checkInputs);
    }

    public static void checkInputs(MinecraftClient client) {

        if (openConfig.wasPressed()) {
            client.setScreen(Config.createScreen(null));
        }

        if (getItemLore.wasPressed()) {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                Misc.addChatMessage(Text.literal("player is null"));
                return;
            }

            ItemStack heldStack = player.getMainHandStack();
            LoreComponent lore = heldStack.get(DataComponentTypes.LORE);
            if (lore == null) {
                Misc.addChatMessage(Text.literal("lore is null"));
                return;
            }

            List<Text> lines = lore.lines();
            for(Text line: lines) {
                Misc.addChatMessage(line);
            }

            Misc.addChatMessage(Text.literal("Rarity: " + ItemRarityHighlight.getRarity(heldStack).name()));
        }

        if (getItemCustomData.wasPressed()) {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                Misc.addChatMessage(Text.literal("player is null"));
                return;
            }

            ItemStack heldStack = player.getMainHandStack();
            NbtComponent nbt = heldStack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbt == null) {
                Misc.addChatMessage(Text.literal("nbt is null"));
                return;
            }

            Misc.addChatMessage(Text.literal(nbt.toString()));

        }

        if (trades.wasPressed()) {
            Misc.executeCommand("trades");
        }
        if (potions.wasPressed()) {
            Misc.executeCommand("potionbag");
        }
    }
}
