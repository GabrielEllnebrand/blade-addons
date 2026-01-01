package blade.addon.utils.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EventHandler<T> {
    /**
     * This class handles some specified Event
     * to cancel the event if implemented return true
     * else return false
     */

    private final List<T> listeners = new ArrayList<>();

    public void register(T listener) {
        listeners.add(listener);
    }

    public boolean invoke(Predicate<T> action) {
        if (listeners.isEmpty()) return false;
        for (T listener : listeners) {
            if (action.test(listener)) {
                return true;
            }
        }
        return false;
    }
}
