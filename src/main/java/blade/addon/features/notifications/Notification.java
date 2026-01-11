package blade.addon.features.notifications;

import blade.addon.utils.Misc;
import blade.addon.utils.debug.Debug;
import config.practical.data.SoundData;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.regex.PatternSyntaxException;

public class Notification {

    private String matchString, notificationString, command;
    private boolean useRegex, sendCommand;
    private int ticks;
    private final SoundData sound;

    public Notification() {
        this.matchString = "";
        this.notificationString = "";
        this.command = "";
        this.useRegex = false;
        this.sendCommand = false;
        this.ticks = 20;
        this.sound = new SoundData(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1, 1);
    }

    public Notification(String matchString, String notificationString, String command, boolean useRegex, boolean sendCommand, int ticks, SoundData sound) {
        this.matchString = matchString;
        this.notificationString = notificationString;
        this.command = command;
        this.useRegex = useRegex;
        this.sendCommand = sendCommand;
        this.ticks = ticks;
        this.sound = sound;
    }

    public String getMatchString() {
        return matchString;
    }

    public String getNotificationString() {
        return notificationString;
    }

    public int getTicks() {
        return ticks;
    }

    public String getCommand() {
        return command;
    }

    public boolean sendCommand() {
        return sendCommand;
    }

    public SoundData getSound() {
        return sound;
    }

    public boolean useRegex() {
        return useRegex;
    }


    public void setMatchString(String matchString) {
        this.matchString = matchString;
    }

    public void setNotificationString(String notificationString) {
        this.notificationString = notificationString;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public void setSendCommand(boolean sendCommand) {
        this.sendCommand = sendCommand;
    }

    public void setUseRegex(boolean useRegex) {
        this.useRegex = useRegex;
    }

    public void testMessage(String message) {
        if (useRegex) {
            try {
                if (!message.matches(matchString)) return;
            } catch (PatternSyntaxException e) {
                return;
            }
        } else if (!message.equals(matchString)) return;

        Debug.sendDebugMessage(Text.literal("Debug: " + notificationString));
        Notifications.setNotification(this);
        Misc.sendSound(sound);
        if (sendCommand) {
            Misc.executeCommand(command);
        }
    }

    @Override
    public String toString() {
        return "Notification{" +
                "matchString='" + matchString + '\'' +
                ", notificationString='" + notificationString + '\'' +
                ", command='" + command + '\'' +
                ", useRegex=" + useRegex +
                ", sendCommand=" + sendCommand +
                ", ticks=" + ticks +
                ", sound=" + sound +
                '}';
    }
}
