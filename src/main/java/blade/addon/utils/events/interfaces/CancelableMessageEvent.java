package blade.addon.utils.events.interfaces;

import net.minecraft.text.Text;

public interface CancelableMessageEvent {

    boolean onGameMessage(Text text);
}
