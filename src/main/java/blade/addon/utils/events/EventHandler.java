package blade.addon.utils.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventHandler<T> {
    public final List<T> listeners = new ArrayList<>();

    public void register(T listener) {
        listeners.add(listener);
    }

    public void invoke(Consumer<T> action) {
        for (T listener : listeners) {
            action.accept(listener);
        }
    }

    public boolean hasListeners() {
        return !listeners.isEmpty();
    }

}
