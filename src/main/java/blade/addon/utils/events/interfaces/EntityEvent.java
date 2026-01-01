package blade.addon.utils.events.interfaces;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public interface EntityEvent {
    boolean onEntity(Entity entity, ClientWorld world);
}
