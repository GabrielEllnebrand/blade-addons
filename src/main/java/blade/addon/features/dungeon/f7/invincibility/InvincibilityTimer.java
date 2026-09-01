package blade.addon.features.dungeon.f7.invincibility;

import blade.addon.features.dungeon.f7.terms.device.DeviceNotifier;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.components.Components;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvincibilityTimer {

    private static final int HEAD_SLOT = 39;

    private static final Pattern BONZO_PATTERN = Pattern.compile("^Your (?:\\S+ )?Bonzo's Mask saved your life!$");

    private static final Pattern BONZO_ON_HEAD_PATTERN = Pattern.compile("Bonzo's Mask");
    private static final Pattern SPIRIT_ON_HEAD_PATTERN = Pattern.compile("Spirit Mask");

    //in ticks
    private static final int BONZO_MASK_COOLDOWN = 180 * 20;
    private static final int SPIRIT_MASK_COOLDOWN = 30 * 20;
    private static final int PHOENIX_COOLDOWN = 60 * 20;

    static int bonzoMaskTicks = 0;
    static int spiritMaskTicks = 0;
    static int phoenixTicks = 0;

    static boolean bonzoMaskOn = false;
    static boolean spiritMaskOn = false;
    static boolean phoenixOn = false;

    public enum DisplayWhen {
        ALWAYS("Always"), BOSS_ONLY("Only in Boss"), P3_ONLY("Only in P3"), USEFUL_PHASES("In p2 and p3");

        private final String label;

        DisplayWhen(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static void init() {
        Events.ON_SERVER_TICK.register(InvincibilityTimer::tick);
        Events.ON_SLOT_CHANGE.register(InvincibilityTimer::detectHelmet);

        Events.ON_LOCATION_CHANGE.register(_ -> {
            if (Location.inDungeon()) {
                bonzoMaskTicks = 0;
                spiritMaskTicks = 0;
                phoenixTicks = 0;
            }
            return false;
        });

        Events.ON_PET.register(name -> phoenixOn = name.equals("Phoenix"));


        Events.ON_GAME_MESSAGE.register(text -> {
            if (Location.inDungeon()) {
                parseMessage(text);
            }
            return false;
        });
    }

    private static boolean tick() {
        bonzoMaskTicks = Math.max(0, bonzoMaskTicks - 1);
        spiritMaskTicks = Math.max(0, spiritMaskTicks - 1);
        phoenixTicks = Math.max(0, phoenixTicks - 1);
        return false;
    }

    private static boolean detectHelmet(int slot, ItemStack item) {
        if (slot != HEAD_SLOT || !Location.inDungeon()) return false;
        String string = item.getHoverName().getString();

        Matcher matcher = BONZO_ON_HEAD_PATTERN.matcher(string);
        bonzoMaskOn = matcher.find();

        matcher = SPIRIT_ON_HEAD_PATTERN.matcher(string);
        spiritMaskOn = matcher.find();
        return false;
    }

    private static void parseMessage(Component message) {
        String string = message.getString();

        Matcher matcher = BONZO_PATTERN.matcher(string);
        if (matcher.matches()) {
            bonzoMaskTicks = BONZO_MASK_COOLDOWN;
            if (Dungeons.showProcTitle && !DeviceNotifier.at4thDev()) {
                Misc.setTitle(Component.literal("Bonzo"));
            }
            Components.invincibilityDurationDisplay.proc();
        }
        if (string.equals("Second Wind Activated! Your Spirit Mask saved your life!")) {
            spiritMaskTicks = SPIRIT_MASK_COOLDOWN;
            if (Dungeons.showProcTitle && !DeviceNotifier.at4thDev()) {
                Misc.setTitle(Component.literal("Spirit"));
            }
            Components.invincibilityDurationDisplay.proc();
        }
        if (string.equals("Your Phoenix Pet saved you from certain death!")) {
            phoenixTicks = PHOENIX_COOLDOWN;
            if (Dungeons.showProcTitle && !DeviceNotifier.at4thDev()) {
                Misc.setTitle(Component.literal("Phoenix"));
            }
            Components.invincibilityDurationDisplay.proc();
        }
    }

    public static double getBonzoProgress() {
        return (double) bonzoMaskTicks / BONZO_MASK_COOLDOWN;
    }

    public static double getSpiritProgress() {
        return (double) spiritMaskTicks / SPIRIT_MASK_COOLDOWN;
    }

    public static boolean spiritMaskUsed() {
        return spiritMaskTicks > 0;
    }


}
