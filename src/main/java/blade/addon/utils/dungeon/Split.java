package blade.addon.utils.dungeon;

import blade.addon.utils.Constants;

public class Split {
    private final String name, dialogue;
    private int tick;

    public Split(String name, String dialogue) {
        this.name = name;
        this.dialogue = dialogue;
        this.tick = 0;
    }

    public boolean matches(String string) {
        return string.contains(dialogue);
    }

    public void tick() {
        this.tick++;
    }

    public void reset() {
        tick = 0;
    }

    public String formatString() {
        double time = tick * Constants.TICK_DURATION;
        return name + " : " + Constants.DECIMAL_FORMAT.format(time) + "s";
    }
}
