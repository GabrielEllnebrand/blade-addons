package blade.addon.utils.events.interfaces;

import blade.addon.utils.Location;

public interface LocationChangeEvent {
    boolean onLocationChange(Location newLocation);
}
