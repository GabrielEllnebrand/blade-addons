package blade.addon.features.dungeon;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvincibilityTimer {

    private static final int TEXT_HEIGHT = 9;
    private static final int HEAD_SLOT = 39;

    private static final Pattern BONZO_PATTERN = Pattern.compile("^Your (?:\\S+ )?Bonzo's Mask saved your life!$");
    private static final Pattern SPIRIT_PATTERN = Pattern.compile("^Second Wind Activated! Your Spirit Mask saved your life!$");
    private static final Pattern PHOENIX_PATTERN = Pattern.compile("^Your Phoenix Pet saved you from certain death!$");

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
        ALWAYS("Always"), BOSS_ONLY("Only in Boss"), P3_ONLY("Only in P3");

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
    public static DisplayWhen displayWhen = DisplayWhen.ALWAYS;


    public static void init() {
        Events.ON_SERVER_TICK.register(() -> {
            bonzoMaskTicks = Math.max(0, bonzoMaskTicks - 1);
            spiritMaskTicks = Math.max(0, spiritMaskTicks - 1);
            phoenixTicks = Math.max(0, phoenixTicks - 1);
        });

        Events.ON_SLOT_CHANGE.register(((slot, item) -> {
            if (slot == HEAD_SLOT && Location.inDungeon()) {
                Text text = item.getCustomName();
                if (text == null) return;
                String string = text.getString();

                Matcher matcher = BONZO_ON_HEAD_PATTERN.matcher(string);
                bonzoMaskOn = matcher.find();

                matcher = SPIRIT_ON_HEAD_PATTERN.matcher(string);
                spiritMaskOn = matcher.find();


            }
        }));
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
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

        });

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

    public static void parseMessage(Text message) {
        String string = message.getString();

        Matcher matcher = BONZO_PATTERN.matcher(string);
        if (matcher.matches()) {
            bonzoMaskTicks = BONZO_MASK_COOLDOWN;
        }
        matcher = SPIRIT_PATTERN.matcher(string);
        if (matcher.matches()) {
            spiritMaskTicks = SPIRIT_MASK_COOLDOWN;
        }
        matcher = PHOENIX_PATTERN.matcher(string);
        if (matcher.matches()) {
            phoenixTicks = PHOENIX_COOLDOWN;
        }
    }


    private static Text getBonzoText() {
        if (bonzoMaskTicks > 0) {
            return Text.literal("Bonzo's Mask ").formatted(Formatting.RED)
                    .append(Text.literal("(").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(Constants.DECIMAL_FORMAT.format(bonzoMaskTicks * Constants.TICK_DURATION)).formatted(Formatting.GRAY))
                    .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
        } else {
            Formatting format = bonzoMaskOn ? Formatting.YELLOW : Formatting.GREEN;
            return Text.literal("Bonzo's Mask ").formatted(format);
        }
    }

    private static Text getSpiritText() {
        if (spiritMaskTicks > 0) {
            return Text.literal("Spirit Mask ").formatted(Formatting.RED)
                    .append(Text.literal("(").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(Constants.DECIMAL_FORMAT.format(spiritMaskTicks * Constants.TICK_DURATION)).formatted(Formatting.GRAY))
                    .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
        } else {
            Formatting format = spiritMaskOn ? Formatting.YELLOW : Formatting.GREEN;
            return Text.literal("Spirit Mask ").formatted(format);
        }
    }

    private static Text getPhoenixText() {
        if (phoenixTicks > 0) {
            return Text.literal("Phoenix ").formatted(Formatting.RED)
                    .append(Text.literal("(").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(Constants.DECIMAL_FORMAT.format(phoenixTicks * Constants.TICK_DURATION)).formatted(Formatting.GRAY))
                    .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
        } else {
            Formatting format = phoenixOn ? Formatting.YELLOW : Formatting.GREEN;
            return Text.literal("Phoenix ").formatted(format);
        }
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
                    case null, default -> {
                        return false;
                    }
                }
            },
            ((hudComponent, drawContext) -> {
                int x = hudComponent.getScaledX();
                int y = hudComponent.getScaledY();

                drawContext.drawText(MinecraftClient.getInstance().textRenderer, getBonzoText(), x, y, 0xffffffff, true);
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, getSpiritText(), x, y + TEXT_HEIGHT, 0xffffffff, true);
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, getPhoenixText(), x, y + TEXT_HEIGHT * 2, 0xffffffff, true);

            })
    );
}
