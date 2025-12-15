package blade.addon.utils.events;

public interface CancelableEvent<T> {
    boolean check(T t);

}
