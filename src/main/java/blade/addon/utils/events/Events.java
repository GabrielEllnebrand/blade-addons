package blade.addon.utils.events;

import blade.addon.utils.events.interfaces.LeapEvent;
import blade.addon.utils.events.interfaces.LocationChangeEvent;
import blade.addon.utils.events.interfaces.ServerTickEvent;
import blade.addon.utils.events.interfaces.SlotChangeEvent;

public class Events {
    public static final EventHandler<ServerTickEvent> ON_SERVER_TICK = new EventHandler<>();
    public static final EventHandler<SlotChangeEvent> ON_SLOT_CHANGE = new EventHandler<>();
    public static final EventHandler<LocationChangeEvent> ON_LOCATION_CHANGE = new EventHandler<>();
    public static final EventHandler<LeapEvent> ON_LEAP = new EventHandler<>();
}
