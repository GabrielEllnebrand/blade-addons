package blade.addon.utils;

import blade.addon.utils.events.Events;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public enum Location {
    NONE,
    DUNGEON;

    private static Location currentLocation = Location.NONE;

    public static void init() {



           /*
            System.out.println(packet.getMap().toString());
            System.out.println(packet.getLobbyName().toString());
            System.out.println(packet.getMode().toString());
            System.out.println(packet.getServerName());
            System.out.println(packet.getServerType().toString());

                [STDOUT]: Optional[Private Island]
                [STDOUT]: Optional.empty
                [STDOUT]: Optional[dynamic]
                [STDOUT]: mini87DF
                [STDOUT]: Optional[SKYBLOCK]
            */

        HypixelModAPI instance = HypixelModAPI.getInstance();


        instance.createHandler(ClientboundLocationPacket.class, packet -> packet.getMap().ifPresent(map -> {
            try {
                currentLocation = Location.valueOf(map.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException ignored) {
                currentLocation = Location.NONE;
            }

            if (Events.ON_LOCATION_CHANGE.hasListeners()) {
                Events.ON_LOCATION_CHANGE.listeners.forEach(locationChangeEvent -> locationChangeEvent.onLocationChange(currentLocation));
            }
        }));

        instance.subscribeToEventPacket(ClientboundLocationPacket.class);


    }

    public static boolean in(Location location) {
        return currentLocation == location;
    }

    public static boolean inDungeon() {
        return currentLocation == Location.DUNGEON;
    }
}