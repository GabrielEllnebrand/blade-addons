package blade.addon.utils.events.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public interface BlockInteractionEvent {

    boolean interact(BlockHitResult result, ItemStack item);

}
