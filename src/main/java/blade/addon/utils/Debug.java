package blade.addon.utils;

import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.dungeon.Section;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.text.Text;

public class Debug {

    private static boolean sendDebug = false;

    public static void init() {
       registerCommands();

    }

    private static void registerCommands() {
        Commands.registerDevCommand(ClientCommandManager.literal("runInfo").executes(context -> {
            sendRunInfo();
            return Constants.SUCCESS;
        }));

        Commands.registerDevCommand(ClientCommandManager.literal("toggleSendDebug").executes(context -> {
            sendDebug = !sendDebug;
            Misc.addChatMessage(Text.literal("Send debug: " + sendDebug));
            return Constants.SUCCESS;
        }));
    }

    //TODO: change this to a LOG object
    public static void addDebugLog(String message) {
        System.out.println("Blade-addons:" + message);
    }

    public static void sendDebugMessage(Text text) {
        if (sendDebug) {
            Misc.addChatMessage(text);
        }
    }

    public static void sendRunInfo() {
        Misc.addChatMessage(Text.literal("Phase: " + Phase.getPhase()));
        Misc.addChatMessage(Text.literal("Section: " + Section.getSection()));
        Misc.addChatMessage(Text.literal("Gateblown: " + Section.isGateBlownUp()));
        Misc.addChatMessage(Text.literal("In floor 7: " + Phase.isInFloor7()));
    }

}
