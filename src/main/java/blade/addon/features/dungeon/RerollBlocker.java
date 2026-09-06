package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RerollBlocker {

    private static final String[] CHESTS = {"Bedrock", "Obsidian"};
    private static final String[] BLOCKED_REROLLS = {"Wither Shield", "Implosion", "Shadow Warp", "Necron's Handle", "Wither Helmet"};

    private static boolean blockClick = false;
    private static int containerId = Integer.MIN_VALUE;

    public static void init() {

        Events.ON_SCREEN.register(screen -> {
            if (!Dungeons.blockExpensiveRerolls || !Location.in(Location.DUNGEON_HUB)) return false;

            blockClick = false;
            containerId = Integer.MIN_VALUE;

            if (screen instanceof ContainerScreen containerScreen) {

                Component title = containerScreen.getTitle();
                if (title == null) return false;

                if (!isValidGUI(title.getString())) return false;

                ChestMenu menu = containerScreen.getMenu();
                containerId = menu.containerId;
            }
            return false;
        });

        Events.ON_PACKET.register(packet -> {
            if (!Dungeons.blockExpensiveRerolls || !Location.in(Location.DUNGEON_HUB)) return false;

            if (blockClick) return false;
            if (packet instanceof ClientboundContainerSetSlotPacket containerSetSlotPacket) {
                if (containerSetSlotPacket.getContainerId() != containerId) return false;

                ItemStack itemStack = containerSetSlotPacket.getItem();
                Component component = itemStack.getCustomName();
                if (component == null) return false;

                if (isBlockedReroll(component.getString())) blockClick = true;
            }

            return false;
        });

        Events.ON_SLOT_CLICKED.register((slot, slotId, _, _) -> {
            if (!Dungeons.blockExpensiveRerolls || !Location.in(Location.DUNGEON_HUB)) return false;
            if (slotId != 50 || slot.getItem().getItem() != Items.FEATHER) return false;

            ItemStack itemStack = slot.getItem();
            Component customName = itemStack.getCustomName();
            if (customName == null) return false;

            if (!customName.getString().contains("Reroll Chest")) return false;

            return blockClick;
        });

    }

    private static boolean isValidGUI(String title) {
        for (String name : CHESTS) {
            if (name.equalsIgnoreCase(title)) return true;
        }
        return false;
    }

    private static boolean isBlockedReroll(String itemName) {
        for (String name : BLOCKED_REROLLS) {
            if (name.contains(itemName)) return true;
        }
        return false;
    }

}
