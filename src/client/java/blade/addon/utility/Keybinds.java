package blade.addon.utility;

import blade.addon.FillHelper;
import blade.addon.config.Config;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    private static final String CATEGORY = Constants.NAMESPACE;

    private static KeyBinding fillPearls;
    private static KeyBinding fillSuperBooms;
    private static KeyBinding openConfig;

    public static void register() {
        fillPearls = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "refills pearl",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY));
        fillSuperBooms = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "refills super boom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY));

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "opens Config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                CATEGORY));

    }

    public static void checkInputs(MinecraftClient client) {
        if (fillPearls.wasPressed()) {
            FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.ENDER_PEARL, 16, 16);
        }

        if (fillSuperBooms.wasPressed()) {
            FillHelper.fillItem(MinecraftClient.getInstance(), FillHelper.SUPERBOOM_TNT, 64, 64);
        }

        if (openConfig.wasPressed()) {
            client.setScreen(Config.create(null));
        }

    }
}
