package blade.addon.utils.events.interfaces;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public interface EntityEvent {
    boolean onEntity(Entity entity, ClientLevel world);
}
