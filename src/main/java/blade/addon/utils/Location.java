package blade.addon.utils;

import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class Location {

    private static Locations currentLocation = Locations.NONE;

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

        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> packet.getMap().ifPresent(map -> {
            try {
                currentLocation = Locations.valueOf(map.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException ignored) {
                currentLocation = Locations.NONE;
            }
        }));
    }

    public static boolean in(Locations location) {
        return currentLocation == location;
    }

    public static boolean inDungeon() {
        return currentLocation == Locations.DUNGEON;
    }
}