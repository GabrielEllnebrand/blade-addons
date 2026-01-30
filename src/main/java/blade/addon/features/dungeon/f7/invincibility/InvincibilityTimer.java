package blade.addon.features.dungeon.f7.invincibility;

import blade.addon.features.dungeon.f7.terms.DeviceNotifier;
import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvincibilityTimer {

    private static final int TEXT_HEIGHT = 9;
    private static final int HEAD_SLOT = 39;

    private static final Identifier BONZO_SPRITE = Identifier.of(Constants.NAMESPACE, "bonzo-mask");
    private static final Identifier SPIRIT_SPRITE = Identifier.of(Constants.NAMESPACE, "spirit-mask");
    private static final Identifier PHOENIX_SPRITE = Identifier.of(Constants.NAMESPACE, "phoenix");

    private static final int SPRITE_SIZE = TEXT_HEIGHT;

    private static final Pattern BONZO_PATTERN = Pattern.compile("^Your (?:\\S+ )?Bonzo's Mask saved your life!$");

    private static final Pattern BONZO_ON_HEAD_PATTERN = Pattern.compile("Bonzo's Mask");
    private static final Pattern SPIRIT_ON_HEAD_PATTERN = Pattern.compile("Spirit Mask");

    //in ticks
    private static final int BONZO_MASK_COOLDOWN = 180 * 20;
    private static final int SPIRIT_MASK_COOLDOWN = 30 * 20;
    private static final int PHOENIX_COOLDOWN = 60 * 20;

    private static int bonzoMaskTicks = 0;
    private static int spiritMaskTicks = 0;
    private static int phoenixTicks = 0;

    private static boolean bonzoMaskOn = false;
    private static boolean spiritMaskOn = false;
    private static boolean phoenixOn = false;

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

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                bonzoMaskTicks = 0;
                spiritMaskTicks = 0;
                phoenixTicks = 0;
            }
            return false;
        });

        Events.ON_PET.register(name -> phoenixOn = name.equals("Phoenix"));


        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (Location.inDungeon()) {
                parseMessage(message);
            }
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
        String string = item.getName().getString();

        Matcher matcher = BONZO_ON_HEAD_PATTERN.matcher(string);
        bonzoMaskOn = matcher.find();

        matcher = SPIRIT_ON_HEAD_PATTERN.matcher(string);
        spiritMaskOn = matcher.find();
        return false;
    }

    private static void parseMessage(Text message) {
        String string = message.getString();

        Matcher matcher = BONZO_PATTERN.matcher(string);
        if (matcher.matches()) {
            bonzoMaskTicks = BONZO_MASK_COOLDOWN;
            if (Dungeons.showProcTitle && !DeviceNotifier.at4thDev()) {
                Misc.setTitle(Text.literal("Bonzo"));
            }
            InvincibilityDuration.proc();
        }
        if (string.equals("Second Wind Activated! Your Spirit Mask saved your life!")) {
            spiritMaskTicks = SPIRIT_MASK_COOLDOWN;
            if (Dungeons.showProcTitle && !DeviceNotifier.at4thDev()) {
                Misc.setTitle(Text.literal("Spirit"));
            }
            InvincibilityDuration.proc();
        }
        if (string.equals("Your Phoenix Pet saved you from certain death!")) {
            phoenixTicks = PHOENIX_COOLDOWN;
            if (Dungeons.showProcTitle && !DeviceNotifier.at4thDev()) {
                Misc.setTitle(Text.literal("Phoenix"));
            }
            InvincibilityDuration.proc();
        }
    }

    private static Text formatTimer(int ticks) {
        return Text.literal("§8(§7" + Constants.DECIMAL_FORMAT.format(ticks * Constants.TICK_DURATION) + "§8)");
    }

    private static Text getText(int ticks, boolean isOn, String string) {
        if (ticks > 0) {
            return Text.literal("§c" + string).append(formatTimer(ticks));
        } else {
            String color  = isOn ? "§e" : "§a";
            return Text.literal(color + string);
        }
    }

    public static double getBonzoProgress() {
        return (double) bonzoMaskTicks / BONZO_MASK_COOLDOWN;
    }

    public static double getSpiritProgress() {
        return (double) spiritMaskTicks / SPIRIT_MASK_COOLDOWN;
    }

    private static void drawSprite(DrawContext context, Identifier identifier, int x, int y, boolean isOn, int ticks, Text timerText) {
        int color = (ticks > 0? Constants.RED: isOn? Constants.YELLOW: Constants.GREEN);
        context.fill(x, y, x + SPRITE_SIZE, y + SPRITE_SIZE, color);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x, y, SPRITE_SIZE, SPRITE_SIZE, 0xffffffff);
        if (ticks > 0) {
            context.drawText(MinecraftClient.getInstance().textRenderer, timerText, x + TEXT_HEIGHT * 2, y, 0xffffffff, true);
        }

    }

    public static boolean spiritMaskUsed() {
        return spiritMaskTicks > 0;
    }

    public static boolean display() {
        if (!Dungeons.displayInvincibilityTimer || !Location.inDungeon()) {
            return false;
        }

        switch (Dungeons.displayWhen) {
            case BOSS_ONLY -> {
                return Phase.inBoss();
            }
            case P3_ONLY -> {
                return Phase.inP3();
            }
            case ALWAYS -> {
                return true;
            }
            case USEFUL_PHASES -> {
                return Phase.inP3() || Phase.inP2();
            }
            case null, default -> {
                return false;
            }
        }
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        if (Dungeons.useSprites) {
            drawSprite(context, BONZO_SPRITE, x, y, bonzoMaskOn, bonzoMaskTicks, formatTimer(bonzoMaskTicks));
            drawSprite(context, SPIRIT_SPRITE, x, y + SPRITE_SIZE + 1, spiritMaskOn, spiritMaskTicks, formatTimer(spiritMaskTicks));
            drawSprite(context, PHOENIX_SPRITE, x, y + (SPRITE_SIZE + 1) * 2, phoenixOn, phoenixTicks, formatTimer(phoenixTicks));
        } else {
            context.drawText(MinecraftClient.getInstance().textRenderer, getText(bonzoMaskTicks, bonzoMaskOn, "Bonzo's Mask "), x, y, 0xffffffff, true);
            context.drawText(MinecraftClient.getInstance().textRenderer, getText(spiritMaskTicks, spiritMaskOn, "Spirit Mask "), x, y + TEXT_HEIGHT, 0xffffffff, true);
            context.drawText(MinecraftClient.getInstance().textRenderer, getText(phoenixTicks, phoenixOn, "Phoenix "), x, y + TEXT_HEIGHT * 2, 0xffffffff, true);
        }
    }
}
