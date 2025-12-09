package blade.addon.utils;

import blade.addon.utils.config.Config;
import blade.addon.utils.dungeon.Phase;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Keybinds {

    private static final String CATEGORY = Constants.NAMESPACE;

    private static KeyBinding openConfig;
    private static KeyBinding getItemLore;

    public static void init() {

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "opens Config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY));

        getItemLore = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Grabs the items lore",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY));

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

            Misc.addChatMessage(Text.literal("Rarity: " + Misc.getRarity(heldStack).name()));
        }
    }
}
