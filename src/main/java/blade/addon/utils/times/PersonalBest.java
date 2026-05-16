package blade.addon.utils.times;

import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class PersonalBest {

    private double bestTime;
    private transient double newestTime;

    public PersonalBest() {
        bestTime = Long.MAX_VALUE;
        PersonalBests.register(this);
    }


    /**
     * @param text The text you want before the time
     * @param startTime The start time gotten from System.currentTimeMillis()
     */
    public void testNewTime(MutableComponent text, long startTime) {
        double diff = (System.currentTimeMillis() - startTime) / 1000.0;
        boolean isPb = false;
        newestTime = diff;

        if (diff < bestTime) {
            bestTime = diff;
            PersonalBests.save();
            isPb = true;
        }

        if (!ExtraOptions.showPbs) return;
        Misc.addChatMessage(text.append(getAsText(isPb)));
    }

    public double getNewestTime() {
        return newestTime;
    }

    public double getBestTime() {
        return bestTime;
    }

    private Component getAsText(boolean isPb) {
        if (isPb) {
            return Component.literal("§e" + Constants.DECIMAL_FORMAT.format(newestTime) + "s. §d§l(PB)").setStyle(Style.EMPTY);
        } else {
            return Component.literal("§e" + Constants.DECIMAL_FORMAT.format(newestTime) + "s. §8(§7" + Constants.DECIMAL_FORMAT.format(bestTime) + "§8)").setStyle(Style.EMPTY);
        }
    }

    public void reset() {
        bestTime = Long.MAX_VALUE;
        newestTime = Long.MAX_VALUE;
    }
}
