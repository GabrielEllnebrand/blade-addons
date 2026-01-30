package blade.addon.utils;

import blade.addon.features.item.ItemRarityHighlight;
import blade.addon.utils.config.Config;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Keybinds {

    private static KeyBinding openConfig;
    private static KeyBinding trades;
    private static KeyBinding potions;

    private static KeyBinding getItemLore;
    private static KeyBinding getItemCustomData;
    private static KeyBinding getBlockInfo;

    public static void init() {

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(Constants.NAMESPACE));

        //normal keybinds
        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "opens Config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
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

        //debug keybinds
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

        getBlockInfo = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Grabs the blocks data",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        ClientTickEvents.END_CLIENT_TICK.register(Keybinds::checkInputs);
    }

    public static void checkInputs(MinecraftClient client) {

        if (openConfig.wasPressed()) {
            client.setScreen(Config.createScreen(null));
        }

        if (trades.wasPressed()) {
            Misc.executeCommand("trades");
        }
        if (potions.wasPressed()) {
            Misc.executeCommand("potionbag");
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
            for (Text line : lines) {
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

        if (getBlockInfo.wasPressed()) {
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            if (player == null || world == null) {
                Misc.addChatMessage(Text.literal("player or world is null"));
                return;
            }

            HitResult result = player.raycast(4, client.getRenderTickCounter().getTickProgress(false), true);

            if (result instanceof BlockHitResult blockHitResult) {
                BlockPos pos = blockHitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                Misc.addChatMessage(Text.literal("Pos: " + pos));
                if (state.hasBlockEntity()) {
                    BlockEntity entity = world.getBlockEntity(pos);
                    Misc.addChatMessage(Text.literal(entity.toString()));

                    if (entity instanceof SkullBlockEntity skullEntity) {
                        ProfileComponent component = skullEntity.getOwner();
                        if (component != null) {
                            GameProfile profile = component.getGameProfile();
                            Misc.addChatMessage(Text.literal("name: " + profile.name() + " id: " + profile.id()));
                        }
                    }
                }
            }

            //wither essence uuid
            //e0f3e929-869e-3dca-9504-54c666ee6f23
        }
    }
}
