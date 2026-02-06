package blade.addon.utils.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DrawHandler <T> {

    private final List<T> listeners = new ArrayList<>();

    public void register(T listener) {
        listeners.add(listener);
    }

    public void invoke(Consumer<T> action) {
        if (listeners.isEmpty()) return;
        for (T listener : listeners) {
            action.accept(listener);
        }
    }

}
