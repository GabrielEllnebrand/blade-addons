package blade.addon.features.item;

import blade.addon.utils.Constants;
import blade.addon.utils.Debug;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.FolderUtility;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.dungeon.Phase;
import config.practical.manager.ConfigManager;
import config.practical.manager.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashSet;
import java.util.List;

public class ProtectItem {

    private static final Identifier SPRITE = Identifier.of(Constants.NAMESPACE, "lock");

    public static final ConfigManager itemManager = new ConfigManager(FolderUtility.OLD_PATH + FolderUtility.PROTECT_ITEMS_NAME,
            List.of(ProtectItem.class));

    @ConfigValue
    public static HashSet<String> protectedItems = new HashSet<>();

    public static void protectSelected() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            Debug.LOGGER.warn("Cant find client player to protect item");
            return;
        }

        ItemStack item = player.getInventory().getSelectedStack();
        String uuid = ItemUtil.getUuid(item);
        if (uuid == null) {
            Misc.addChatMessage(Text.literal("Cant protect this item, it does not have a uuid"));
            return;
        }
        ProtectedItemHolder holder = (ProtectedItemHolder) (Object) item;
        if (protectedItems.contains(uuid)) {
            protectedItems.remove(uuid);
            holder.blade_addons$setProtected(false);
            Misc.addChatMessage(Text.literal("Item ").append(item.getName()).append(" is NOT protected anymore"));
        } else {
            protectedItems.add(uuid);
            holder.blade_addons$setProtected(true);
            Misc.addChatMessage(Text.literal("Item ").append(item.getName()).append(" is protected"));
        }

        itemManager.save();
    }

    public static boolean protect(ItemStack stack) {

        String uuid = ItemUtil.getUuid(stack);

        //TODO: add smth for non uuid items
        if (uuid == null) {
            return false;
        }

        return protectedItems.contains(uuid);
    }

    public static void draw(DrawContext context, ItemStack stack, int x, int y) {
        ProtectedItemHolder holder = (ProtectedItemHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScannedProtection()) {
            String uuid = ItemUtil.getUuid(stack);
            if (uuid == null) return;
            holder.blade_addons$setProtected(protectedItems.contains(uuid));
        }

        if (!holder.blade_addons$isProtected()) return;

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SPRITE, x, y, 16, 16, 0x99ffffff);
    }

    public static boolean blockGUI(HandledScreen screen, ItemStack item) {
        if (!protect(item)) return false;


        Text title = screen.getTitle();
        if (title == null) return false;
        String name = title.getString();

        if (name.contains("Auction")) {
            Misc.addChatMessage(Text.literal("Protected ").append(item.getName()).append(" From being auctioned"));
            return true;
        }

        if (name.contains("Salvage")) {
            Misc.addChatMessage(Text.literal("Protected ").append(item.getName()).append(" From being salvaged"));
            return true;
        }

        ScreenHandler handler = screen.getScreenHandler();
        DefaultedList<Slot> slots = handler.slots;

        if (slots.size() > 49) {
            Slot slot = slots.get(49);
            ItemStack stack = slot.getStack();
            Text itemName = stack.getName();
            if (itemName != null && itemName.getString().contains("Sell Item") || Misc.containsLore(stack, "Click to buyback!")) {
                Misc.addChatMessage(Text.literal("Protected ").append(item.getName()).append(" From being sold"));
                return true;
            }

            slot = slots.get(4);
            stack = slot.getStack();
            itemName = stack.getName();
            if (itemName != null && itemName.getString().contains("⇦ Your stuff")) {
                Misc.addChatMessage(Text.literal("Protected ").append(item.getName()).append(" From being traded"));
                return true;
            }

        }
        return false;
    }

    public static boolean isInADungeon() {
        return Location.inDungeon() && Phase.runStarted() && !Phase.runOver() && Location.hasRecivedLocation();
    }
}
