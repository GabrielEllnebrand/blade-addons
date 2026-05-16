package blade.addon.utils.events.interfaces;

import net.minecraft.network.chat.Component;

public interface LeapEvent {
    boolean onLeap(Component message);
}
