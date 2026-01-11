package blade.addon.utils.data;

import blade.addon.utils.debug.Debug;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket;

import java.util.Map;
import java.util.UUID;

public class PartyUtil {

    private static final int MIN_DELAY = 1000 * 60;
    private static final HypixelModAPI INSTANCE = HypixelModAPI.getInstance();

    private static long grabbedTime = 0;
    private static Map<UUID, ClientboundPartyInfoPacket.PartyMember> memberMap;
    private static boolean inParty;


    public static void init() {
        INSTANCE.createHandler(ClientboundPartyInfoPacket.class, packet -> {
            Debug.LOGGER.info("Received party info packet");
            memberMap = packet.getMemberMap();
            inParty = packet.isInParty();
        });
    }

    public static void sendPacket() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Instance not set");
        }

        if (System.currentTimeMillis() - grabbedTime < MIN_DELAY) return;
        if (INSTANCE.sendPacket(new ServerboundPartyInfoPacket())) {
            grabbedTime = System.currentTimeMillis();
        } else {
            Debug.LOGGER.warn("Server bound party info packet lost");
        }
    }

    public static int getPlayerCount() {
        sendPacket();
        if (!inParty) return 0;
        return memberMap.size();
    }

    public static boolean isInParty() {
        sendPacket();
        return inParty;
    }

}
