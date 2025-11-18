package blade.addon.utils.events;

import blade.addon.utils.events.interfaces.EntityTrackEvent;
import blade.addon.utils.events.interfaces.LeapEvent;
import blade.addon.utils.events.interfaces.LocationChangeEvent;
import blade.addon.utils.events.interfaces.PlayerListEvent;
import blade.addon.utils.events.interfaces.RunEndEvent;
import blade.addon.utils.events.interfaces.ServerTickEvent;
import blade.addon.utils.events.interfaces.SlotChangeEvent;

public class Events {
    public static final EventHandler<ServerTickEvent> ON_SERVER_TICK = new EventHandler<>();
    public static final EventHandler<SlotChangeEvent> ON_SLOT_CHANGE = new EventHandler<>();
    public static final EventHandler<LocationChangeEvent> ON_LOCATION_CHANGE = new EventHandler<>();
    public static final EventHandler<LeapEvent> ON_LEAP = new EventHandler<>();
    public static final EventHandler<EntityTrackEvent> ON_ENTITY_TRACKED = new EventHandler<>();
    public static final EventHandler<RunEndEvent> ON_RUN_END = new EventHandler<>();
    public static final EventHandler<PlayerListEvent> ON_PLAYER_ENTRY = new EventHandler<>();
}
