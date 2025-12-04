package blade.addon.utils;

import blade.addon.utils.events.Events;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public enum Location {
    NONE,
    DUNGEON,
    DUNGEON_HUB,
    PRIVATE_ISLAND,
    HUB;

    private static Location currentLocation = Location.NONE;
    private static boolean inSkyblock = false;

    public static void init() {

        HypixelModAPI instance = HypixelModAPI.getInstance();
        instance.createHandler(ClientboundLocationPacket.class, packet -> packet.getMap().ifPresent(map -> {

            if (packet.getServerType().isPresent()) {
                ServerType serverType = packet.getServerType().get();
                inSkyblock = serverType.getName().equals("SkyBlock");
            }

            System.out.println(map.toUpperCase().replace(" ", "_"));

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
        if (!inSkyblock) return false;
        return currentLocation == location;
    }

    public static boolean inDungeon() {
        if (!inSkyblock) return false;
        return currentLocation == Location.DUNGEON;
    }

    public static boolean inSkyblock() {
        return inSkyblock;
    }
}