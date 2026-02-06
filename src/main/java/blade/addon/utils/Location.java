package blade.addon.utils;

import blade.addon.utils.debug.Debug;
import blade.addon.utils.events.Events;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.text.Text;

public enum Location {
    UNKNOWN("Unknown"),
    DUNGEON("Dungeon"),
    DUNGEON_HUB("Dungeon Hub"),
    PRIVATE_ISLAND("Private Island"),
    HUB("Hub"),
    GALATEA("Galatea"),
    THE_PARK("The Park"),
    THE_FARMING_ISLANDS("The Farming Islands"),
    GOLD_MINE("Gold Mine"),
    DEEP_CAVERNS("Deep Caverns"),
    DWARVEN_MINES("Dwarven Mines"),
    CRYSTAL_HOLLOWS("Crystal Hollows"),
    SPIDERS_DEN("Spider's Den"),
    THE_END("The End"),
    CRIMSON_ISLE("Crimson Isle"),
    GARDEN("Garden"),
    THE_RIFT("The Rift"),
    BACKWATER_BAYOU("Backwater Bayou"),
    JERRYS_WORKSHOP("Jerry's Workshop"),
    MINESHAFT("Mineshaft"),
    DARK_AUCTION("Dark Auction"),
    KUUDRA("Kuudra"),;

    final String name;

    Location(String name) {
        this.name = name;
    }


    private static Location currentLocation = Location.UNKNOWN;
    private static boolean inSkyblock = false;
    private static boolean detectedNewLocation = false;
    private static long lastChanged = System.currentTimeMillis();

    public static void init() {

        HypixelModAPI instance = HypixelModAPI.getInstance();
        instance.createHandler(ClientboundLocationPacket.class, packet -> packet.getMap().ifPresent(locationName -> {
            if (packet.getServerType().isPresent()) {
                ServerType serverType = packet.getServerType().get();
                inSkyblock = serverType.getName().equals("SkyBlock");
            }

            changeLocation(getLocation(locationName));
        }));

        instance.subscribeToEventPacket(ClientboundLocationPacket.class);

        Events.ON_WORLD_CHANGE.register(() -> {
            detectedNewLocation = false;
            return false;
        });
    }

    public static Location getLocation(String locationName) {
        Location location;

        try {
            location = Location.valueOf(locationName.toUpperCase().replace(" ", "_").replaceAll("'", ""));
        } catch (IllegalArgumentException ignored) {
            location = Location.UNKNOWN;
        }

        return location;
    }

    public static void changeLocation(Location location) {
        currentLocation = location;
        detectedNewLocation = true;
        lastChanged = System.currentTimeMillis();
        Debug.sendDebugMessage(Text.literal("Location: " + currentLocation));

        Events.ON_LOCATION_CHANGE.invoke(locationChangeEvent -> locationChangeEvent.onLocationChange(currentLocation));

    }

    public static boolean in(Location location) {
        if (!inSkyblock) return false;
        return currentLocation == location;
    }

    public static boolean inDungeon() {
        if (!inSkyblock) return false;
        return currentLocation == Location.DUNGEON;
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static boolean inSkyblock() {
        return inSkyblock;
    }

    public static boolean hasReceivedLocation() {
        return detectedNewLocation;
    }

    @Override
    public String toString() {
        return "Location: " + name + " Last changed: " +  (System.currentTimeMillis() - lastChanged) / 1000.0 + "s";
    }
}