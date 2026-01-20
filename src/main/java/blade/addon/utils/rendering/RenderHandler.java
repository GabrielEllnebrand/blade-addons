package blade.addon.utils.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RenderHandler {

    private final List<RenderingEvent> listeners = new ArrayList<>();

    public void register(RenderingEvent listener) {
        listeners.add(listener);
    }

    public void invoke(Consumer<RenderingEvent> action) {
        if (listeners.isEmpty()) return;
        for (RenderingEvent listener : listeners) {
            action.accept(listener);
        }
    }

}
