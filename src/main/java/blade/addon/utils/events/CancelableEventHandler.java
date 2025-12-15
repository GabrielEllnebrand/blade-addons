package blade.addon.utils.events;

import java.util.ArrayList;
import java.util.List;

public class CancelableEventHandler<T> {

    public final List<T> listeners = new ArrayList<>();

    public void register(T listener) {
        listeners.add(listener);
    }

    public boolean test(CancelableEvent<T> event) {
        for (T listener : listeners) {
            if (event.check(listener)) {
                return true;
            }
        }
        return false;
    }
}
