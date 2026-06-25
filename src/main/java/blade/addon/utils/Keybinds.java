package blade.addon.utils;

import blade.addon.features.item.ItemRarityHighlight;
import blade.addon.utils.config.Config;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Keybinds {

    private static KeyMapping openConfig;
    private static KeyMapping trades;
    private static KeyMapping potions;

    private static KeyMapping getItemLore;
    private static KeyMapping getItemCustomData;
    private static KeyMapping getBlockInfo;

    public static void init() {

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.parse(Constants.NAMESPACE));

        //normal keybinds
        openConfig = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "opens Config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category));

        trades = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Opens the trades menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        potions = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Opens the potion bag",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        //debug keybinds
        getItemLore = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Grabs the items lore",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        getItemCustomData = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Grabs the items custom data",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        getBlockInfo = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Grabs the blocks data",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category));

        ClientTickEvents.END_CLIENT_TICK.register(Keybinds::checkInputs);
    }

    public static void checkInputs(Minecraft client) {

        if (openConfig.consumeClick()) {
            client.setScreen(Config.createScreen(null));
        }

        if (trades.consumeClick()) {
            Misc.executeCommand("trades");
        }
        if (potions.consumeClick()) {
            Misc.executeCommand("potionbag");
        }

        if (getItemLore.consumeClick()) {
            LocalPlayer player = client.player;
            if (player == null) {
                Misc.addChatMessage(Component.literal("player is null"));
                return;
            }

            ItemStack heldStack = player.getMainHandItem();
            ItemLore lore = heldStack.get(DataComponents.LORE);
            if (lore == null) {
                Misc.addChatMessage(Component.literal("lore is null"));
                return;
            }

            List<Component> lines = lore.lines();
            for (Component line : lines) {
                Misc.addChatMessage(line);
            }

            Misc.addChatMessage(Component.literal("Rarity: " + ItemRarityHighlight.getRarity(heldStack).name()));
        }

        if (getItemCustomData.consumeClick()) {
            LocalPlayer player = client.player;
            if (player == null) {
                Misc.addChatMessage(Component.literal("player is null"));
                return;
            }

            ItemStack heldStack = player.getMainHandItem();
            CustomData nbt = heldStack.get(DataComponents.CUSTOM_DATA);
            if (nbt == null) {
                Misc.addChatMessage(Component.literal("nbt is null"));
                return;
            }

            Misc.addChatMessage(Component.literal(nbt.toString()));

        }

        if (getBlockInfo.consumeClick()) {
            LocalPlayer player = client.player;
            ClientLevel world = client.level;
            if (player == null || world == null) {
                Misc.addChatMessage(Component.literal("player or world is null"));
                return;
            }

            HitResult result = player.pick(4, client.getDeltaTracker().getGameTimeDeltaPartialTick(false), true);

            if (result instanceof BlockHitResult blockHitResult) {
                BlockPos pos = blockHitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                Misc.addChatMessage(Component.literal("Pos: " + pos));
                if (state.hasBlockEntity()) {
                    BlockEntity entity = world.getBlockEntity(pos);
                    Misc.addChatMessage(Component.literal(entity.toString()));

                    if (entity instanceof SkullBlockEntity skullEntity) {
                        ResolvableProfile component = skullEntity.getOwnerProfile();
                        if (component != null) {
                            GameProfile profile = component.partialProfile();
                            Misc.addChatMessage(Component.literal("name: " + profile.name() + " id: " + profile.id()));
                        }
                    }
                }
            }

            //wither essence uuid
            //e0f3e929-869e-3dca-9504-54c666ee6f23
        }
    }
}
