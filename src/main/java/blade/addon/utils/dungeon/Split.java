package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;

public class Split {
    private final String name, dialogue;
    private int tick;
    private long startTime, endTime;
    private boolean started, ended;

    public Split(String name, String dialogue) {
        this.name = name;
        this.dialogue = dialogue;
        this.tick = 0;
        this.ended = false;
    }

    public boolean matches(String string) {
        if (string.contains(dialogue)) {
            startTime = System.currentTimeMillis();
            started = true;
            return true;
        }
        return false;
    }

    public void tick() {
        this.tick++;
    }

    public void reset() {
        tick = 0;
        ended = false;
        started = false;
    }

    public void end() {
        endTime = System.currentTimeMillis();
        started = false;
        ended = true;
    }

    public String makeSplitString(long currentTime) {
        double tickTime = tick * Constants.TICK_DURATION;

        double realTime;
        if (ended) {
            realTime = (endTime - startTime) / 1000.0;
        } else if (started ){
            realTime = (currentTime - startTime) / 1000.0;
        } else {
            realTime = 0;
        }

        return name + " : " + Constants.DECIMAL_FORMAT.format(realTime) + "s (" + Constants.DECIMAL_FORMAT.format(tickTime) + "s)";
    }
}
