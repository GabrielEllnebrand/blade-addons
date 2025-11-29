package blade.addon.utils;

import blade.addon.utils.dungeon.Phase;
import net.minecraft.text.Text;

public class Debug {

    public static int parseOption(String string) {

        switch (string) {
            case "RUN_INFO":
                sendRunInfo();
                break;
        }


        return Constants.SUCCESS;
    }

    public static void sendRunInfo() {
        Misc.addChatMessage(Text.literal("Phase: " + Phase.getPhase()));
        Misc.addChatMessage(Text.literal("Section: " + Phase.getSection()));
        Misc.addChatMessage(Text.literal("Gateblown: " + Phase.isGateBlownUp()));
        Misc.addChatMessage(Text.literal("Terms done: " + Phase.isTermsDone()));
        Misc.addChatMessage(Text.literal("In floor 7: " + Phase.isInFloor7()));
    }

}
