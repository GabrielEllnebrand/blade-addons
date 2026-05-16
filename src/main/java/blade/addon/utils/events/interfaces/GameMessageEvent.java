package blade.addon.utils.events.interfaces;

import net.minecraft.network.chat.Component;

public interface GameMessageEvent {
    boolean onGameMessage(Component text);
}
