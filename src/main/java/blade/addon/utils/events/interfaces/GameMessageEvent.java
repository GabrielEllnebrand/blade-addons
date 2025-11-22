package blade.addon.utils.events.interfaces;

import net.minecraft.text.Text;

public interface GameMessageEvent {
    /**
     * Some mods cancel game messages
     * so to get around it, we use this Event
     * @param text the message
     */
    void onGameMessage(Text text);
}
