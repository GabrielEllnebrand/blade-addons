package blade.addon.utils;

import blade.addon.utils.events.Events;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.text.Text;

public enum Location {
    NONE,
    DUNGEON,
    DUNGEON_HUB,
    PRIVATE_ISLAND,
    HUB;

    private static Location currentLocation = Location.NONE;
    private static boolean inSkyblock = false;
    private static boolean detectedNewLocation = false;

    public static void init() {

        HypixelModAPI instance = HypixelModAPI.getInstance();
        instance.createHandler(ClientboundLocationPacket.class, packet -> packet.getMap().ifPresent(map -> {

            if (packet.getServerType().isPresent()) {
                ServerType serverType = packet.getServerType().get();
                inSkyblock = serverType.getName().equals("SkyBlock");
            }

            try {
                currentLocation = Location.valueOf(map.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException ignored) {
                currentLocation = Location.NONE;
            }
            detectedNewLocation = true;
            Debug.sendDebugMessage(Text.literal("Location: " + currentLocation));

            Events.ON_LOCATION_CHANGE.invoke(locationChangeEvent -> locationChangeEvent.onLocationChange(currentLocation));
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

    public static void swapWorld() {
        detectedNewLocation = false;
    }

    public static boolean hasRecivedLocation() {
        return detectedNewLocation;
    }
}