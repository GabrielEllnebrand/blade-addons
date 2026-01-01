package blade.addon.utils.events.interfaces;

public interface TerminalEvent {
    boolean onComplete(String formattedName, String action, String objective, int current, int total);
}
