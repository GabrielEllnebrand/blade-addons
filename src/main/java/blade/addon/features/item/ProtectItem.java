package blade.addon.features.item;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.FolderUtility;
import blade.addon.utils.config.values.Visual;
import blade.addon.utils.data.ItemUtil;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.rendering.DrawEvents;
import config.practical.manager.ConfigManager;
import config.practical.manager.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;

public class ProtectItem {

    private static final Identifier SPRITE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "lock");

    public static final ConfigManager itemManager = new ConfigManager(FolderUtility.OLD_PATH + FolderUtility.PROTECT_ITEMS_NAME,
            List.of(ProtectItem.class));

    @ConfigValue
    public static HashSet<String> protectedItems = new HashSet<>();

    public static boolean stopProtectItem = false;

    public static void init() {
        DrawEvents.INVENTORY_SLOT_BEFORE.register(ProtectItem::draw);
        DrawEvents.HUD_SLOT_BEFORE.register(ProtectItem::draw);
    }

    public static void protectSelected() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            Debug.LOGGER.warn("Cant find client player to protect item");
            return;
        }

        ItemStack item = player.getInventory().getSelectedItem();
        String saveString = ItemUtil.getUuid(item);

        if (saveString == null) {
            saveString = ItemUtil.getId(item);
        }

        if (saveString == null) {
            Misc.addChatMessage(Component.literal("Cant protect this item, the item does not have a uuid or id"));
            return;
        }

        ProtectedItemHolder holder = (ProtectedItemHolder) (Object) item;
        if (protectedItems.contains(saveString)) {
            protectedItems.remove(saveString);
            holder.blade_addons$setProtected(false);
            Misc.addChatMessage(Component.literal("Item ").append(item.getHoverName()).append(" is NOT protected anymore"));
        } else {
            protectedItems.add(saveString);
            holder.blade_addons$setProtected(true);
            Misc.addChatMessage(Component.literal("Item ").append(item.getHoverName()).append(" is protected"));
        }

        itemManager.save();
    }

    public static boolean protect(ItemStack stack) {
        if (stopProtectItem) return false;
        String saveString = ItemUtil.getUuid(stack);

        if (saveString == null) {
            saveString = ItemUtil.getId(stack);
        }

        if (saveString == null) {
            return false;
        }

        return protectedItems.contains(saveString);
    }

    private static void draw(GuiGraphicsExtractor context, ItemStack stack, int x, int y) {
        if (!Visual.highlightProtectedItem) return;
        ProtectedItemHolder holder = (ProtectedItemHolder) (Object) stack;
        assert holder != null;

        if (!holder.blade_addons$hasScannedProtection()) {
            String uuid = ItemUtil.getUuid(stack);
            if (uuid == null) return;
            holder.blade_addons$setProtected(protectedItems.contains(uuid));
        }

        if (!holder.blade_addons$isProtected()) return;

        context.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, x, y, 16, 16, 0x99ffffff);
    }

    public static boolean blockGUI(AbstractContainerScreen screen, ItemStack item) {
        if (!protect(item)) return false;


        Component title = screen.getTitle();
        if (title == null) return false;
        String name = title.getString();

        if (name.contains("Auction")) {
            Misc.addChatMessage(Component.literal("Protected ").append(item.getHoverName()).append(" From being auctioned"));
            return true;
        }

        if (name.contains("Salvage")) {
            Misc.addChatMessage(Component.literal("Protected ").append(item.getHoverName()).append(" From being salvaged"));
            return true;
        }

        AbstractContainerMenu handler = screen.getMenu();
        NonNullList<Slot> slots = handler.slots;

        if (slots.size() > 49) {
            Slot slot = slots.get(49);
            ItemStack stack = slot.getItem();
            Component itemName = stack.getHoverName();
            if (itemName != null && itemName.getString().contains("Sell Item") || ItemUtil.containsLore(stack, "Click to buyback!")) {
                Misc.addChatMessage(Component.literal("Protected ").append(item.getHoverName()).append(" From being sold"));
                return true;
            }

            slot = slots.get(4);
            stack = slot.getItem();
            itemName = stack.getHoverName();
            if (itemName != null && itemName.getString().contains("⇦ Your stuff")) {
                Misc.addChatMessage(Component.literal("Protected ").append(item.getHoverName()).append(" From being traded"));
                return true;
            }

        }
        return false;
    }

    public static boolean isInADungeon() {
        return Location.inDungeon() && Phase.runStarted() && !Phase.runOver() && Location.hasReceivedLocation();
    }
}
