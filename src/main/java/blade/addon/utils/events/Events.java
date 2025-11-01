package blade.addon.utils.events;

import blade.addon.utils.events.interfaces.ServerTickEvent;
import blade.addon.utils.events.interfaces.SlotChangeEvent;

public class Events {
    public static final EventHandler<ServerTickEvent> ON_SERVER_TICK = new EventHandler<>();
    public static final EventHandler<SlotChangeEvent> ON_SLOT_CHANGE = new EventHandler<>();
}
