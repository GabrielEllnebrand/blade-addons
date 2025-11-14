package blade.addon.utils;

import blade.addon.utils.config.Config;
import blade.addon.utils.dungeon.Phase;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    private static final String CATEGORY = Constants.NAMESPACE;

    private static KeyBinding openConfig;

    public static void register() {

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "opens Config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(Keybinds::checkInputs);
    }

    public static void checkInputs(MinecraftClient client) {

        if (openConfig.wasPressed()) {
            client.setScreen(Config.createScreen(null));
        }
    }
}
