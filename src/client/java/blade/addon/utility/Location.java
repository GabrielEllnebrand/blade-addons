package blade.addon.utility;

import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class Location {

    private static Locations currentLocation = Locations.NONE;

    public static void init() {
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> {
            packet.getMap().ifPresent(map -> {
                try {
                    currentLocation = Locations.valueOf(map.toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException ignored) {

                }
            });
        });
    }

    public static boolean in(Locations location) {
        return currentLocation == location;
    }
}
