package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvincibilityTimer {

    private static final int TEXT_HEIGHT = 9;
    private static final int HEAD_SLOT = 39;

    private static final Identifier BONZO_SPRITE = Identifier.of(Constants.NAMESPACE, "bonzo-mask");
    private static final Identifier SPIRIT_SPRITE = Identifier.of(Constants.NAMESPACE, "spirit-mask");
    private static final Identifier PHOENIX_SPRITE = Identifier.of(Constants.NAMESPACE, "phoenix");

    private static final int GREEN_COLOR = 0x8800FF00;
    private static final int YELLOW_COLOR = 0x88FFFF00;
    private static final int RED_COLOR = 0x88FF0000;

    private static final int SPRITE_SIZE = TEXT_HEIGHT;

    private static final Pattern BONZO_PATTERN = Pattern.compile("^Your (?:\\S+ )?Bonzo's Mask saved your life!$");

    private static final Pattern BONZO_ON_HEAD_PATTERN = Pattern.compile("Bonzo's Mask");
    private static final Pattern SPIRIT_ON_HEAD_PATTERN = Pattern.compile("Spirit Mask");

    private static final Pattern MANUAL_EQUIP_PET_PATTERN = Pattern.compile("^You summoned your (\\D+)!$");
    private static final Pattern RULE_EQUIP_PET_PATTERN = Pattern.compile("^Autopet equipped your \\[Lvl [0-9]+] (\\D+)( ✦)?! VIEW RULE$");

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

    @ConfigValue
    public static boolean displayInvincibilityTimer = false;

    @ConfigValue
    public static boolean showProcTitle = false;

    @ConfigValue
    public static DisplayWhen displayWhen = DisplayWhen.ALWAYS;

    @ConfigValue
    public static boolean useSprites = false;

    public static void init() {
        Events.ON_SERVER_TICK.register(InvincibilityTimer::tick);
        ClientReceiveMessageEvents.GAME.register(InvincibilityTimer::detectPet);
        Events.ON_SLOT_CHANGE.register(InvincibilityTimer::detectHelmet);

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            if (Location.inDungeon()) {
                bonzoMaskTicks = 0;
                spiritMaskTicks = 0;
                phoenixTicks = 0;
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (Location.inDungeon()) {
                parseMessage(message);
            }
        });
    }

    private static void tick() {
        bonzoMaskTicks = Math.max(0, bonzoMaskTicks - 1);
        spiritMaskTicks = Math.max(0, spiritMaskTicks - 1);
        phoenixTicks = Math.max(0, phoenixTicks - 1);
    }

    private static void detectPet(Text message, boolean overlay) {
        if (!Location.inDungeon()) return;
        String string = message.getString().replaceAll("§.", "");
        Matcher matcher = MANUAL_EQUIP_PET_PATTERN.matcher(string);
        if (matcher.find()) {
            String petName = matcher.group(1);
            phoenixOn = petName.equals("Phoenix");
        }

        matcher = RULE_EQUIP_PET_PATTERN.matcher(string);
        if (matcher.find()) {
            String petName = matcher.group(1);
            phoenixOn = petName.equals("Phoenix");
        }
    }

    private static void detectHelmet(int slot, ItemStack item) {
        if (slot != HEAD_SLOT || !Location.inDungeon()) return;
        Text text = item.getCustomName();
        if (text == null) return;
        String string = text.getString();

        Matcher matcher = BONZO_ON_HEAD_PATTERN.matcher(string);
        bonzoMaskOn = matcher.find();

        matcher = SPIRIT_ON_HEAD_PATTERN.matcher(string);
        spiritMaskOn = matcher.find();
    }

    private static void parseMessage(Text message) {
        String string = message.getString();

        Matcher matcher = BONZO_PATTERN.matcher(string);
        if (matcher.matches()) {
            bonzoMaskTicks = BONZO_MASK_COOLDOWN;
            if (showProcTitle) {
                Misc.setTitle(Text.literal("Bonzo"));
            }
        }
        if (string.equals("Second Wind Activated! Your Spirit Mask saved your life!")) {
            spiritMaskTicks = SPIRIT_MASK_COOLDOWN;
            if (showProcTitle) {
                Misc.setTitle(Text.literal("Spirit"));
            }
        }
        if (string.equals("Your Phoenix Pet saved you from certain death!")) {
            phoenixTicks = PHOENIX_COOLDOWN;
            if (showProcTitle) {
                Misc.setTitle(Text.literal("Phoenix"));
            }
        }
    }

    private static Text formatTimer(int ticks) {
        return Text.literal("(").formatted(Formatting.DARK_GRAY)
                .append(Text.literal(Constants.DECIMAL_FORMAT.format(ticks * Constants.TICK_DURATION)).formatted(Formatting.GRAY))
                .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
    }


    private static Text getBonzoText() {
        if (bonzoMaskTicks > 0) {
            return Text.literal("Bonzo's Mask ").formatted(Formatting.RED).append(formatTimer(bonzoMaskTicks));
        } else {
            Formatting format = bonzoMaskOn ? Formatting.YELLOW : Formatting.GREEN;
            return Text.literal("Bonzo's Mask ").formatted(format);
        }
    }

    private static Text getSpiritText() {
        if (spiritMaskTicks > 0) {
            return Text.literal("Spirit Mask ").formatted(Formatting.RED).append(formatTimer(spiritMaskTicks));

        } else {
            Formatting format = spiritMaskOn ? Formatting.YELLOW : Formatting.GREEN;
            return Text.literal("Spirit Mask ").formatted(format);
        }
    }

    private static Text getPhoenixText() {
        if (phoenixTicks > 0) {
            return Text.literal("Phoenix ").formatted(Formatting.RED).append(formatTimer(phoenixTicks));
        } else {
            Formatting format = phoenixOn ? Formatting.YELLOW : Formatting.GREEN;
            return Text.literal("Phoenix ").formatted(format);
        }
    }

    private static void drawSprite(DrawContext context, Identifier identifier, int x, int y, boolean isOn, int ticks, Text timerText) {

        int color;

        if (ticks > 0) {
            color = RED_COLOR;
        } else if (isOn) {
            color = YELLOW_COLOR;
        } else {
            color = GREEN_COLOR;
        }


        context.fill(x, y, x + SPRITE_SIZE, y + SPRITE_SIZE, color);

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x, y, SPRITE_SIZE, SPRITE_SIZE, 0xffffffff);

        if (ticks > 0) {
            context.drawText(MinecraftClient.getInstance().textRenderer, timerText, x + TEXT_HEIGHT * 2, y, 0xffffffff, true);
        }

    }

    public static boolean spiritMaskUsed() {
        return spiritMaskTicks > 0;
    }

    @ConfigValue
    public static HUDComponent invincibilityTimer = new HUDComponent(0, 0, 110, 28, 1, "Invincibility timer",
            () -> {
                if (!displayInvincibilityTimer || !Location.inDungeon()) {
                    return false;
                }

                switch (displayWhen) {
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
            },
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                if (useSprites) {
                    drawSprite(drawContext, BONZO_SPRITE, x, y, bonzoMaskOn, bonzoMaskTicks, formatTimer(bonzoMaskTicks));
                    drawSprite(drawContext, SPIRIT_SPRITE, x, y + SPRITE_SIZE + 1, spiritMaskOn, spiritMaskTicks, formatTimer(spiritMaskTicks));
                    drawSprite(drawContext, PHOENIX_SPRITE, x, y + (SPRITE_SIZE + 1) * 2, phoenixOn, phoenixTicks, formatTimer(phoenixTicks));

                } else {
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, getBonzoText(), x, y, 0xffffffff, true);
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, getSpiritText(), x, y + TEXT_HEIGHT, 0xffffffff, true);
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, getPhoenixText(), x, y + TEXT_HEIGHT * 2, 0xffffffff, true);
                }
            }), () -> displayInvincibilityTimer
    );
}
