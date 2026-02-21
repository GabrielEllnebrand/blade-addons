package blade.addon.features.other;

import blade.addon.utils.Constants;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectedPet {

    private static final int COLOR_OFFSET = 2;

    private static final Pattern TAB_PET_PATTERN = Pattern.compile(" \\[Lvl \\d+] (?:\\[[^\\]]+\\] )?(.+)");
    private static final Pattern MANUAL_EQUIP_PET_PATTERN = Pattern.compile("^You (summoned|despawned) your (?:\\[Lvl \\d+] )?(?:\\[[^\\]]+\\] )?(.+)!$");
    private static final Pattern RULE_EQUIP_PET_PATTERN = Pattern.compile("^Autopet equipped your \\[Lvl \\d+] (?:\\[[^\\]]+\\] )?(.+)! VIEW RULE$");
    private static final Pattern LEVEL_REGEX = Pattern.compile("\\[Lvl (\\d+)]");

    private static final int TOTAL_TICKS = 20;

    private static final MutableText NO_PET = Text.literal("§cNo pet");
    private static final String identifierPrefix = "pets/";

    private static MutableText currentPetText = NO_PET;
    private static int currentPetLevel = -1;
    private static String currentPetString = "";
    private static Identifier spriteId = null;
    private static int tick = 0;

    public static void init() {
        Events.ON_GAME_MESSAGE.register(message -> {
            String string = message.getString();
            Matcher matcher = MANUAL_EQUIP_PET_PATTERN.matcher(string);
            if (matcher.find()) {

                if (matcher.group(1).equals("despawned")) {
                    despawnPet();
                    return false;
                }

                String name = matcher.group(2).replace(" ✦", "");
                Style style = getStyle(message, name);
                summonPet(name, Text.literal(name).setStyle(style), getLevel(string), false);
                return false;
            }

            String unformattedLine = string.replaceAll("§.", "");
            matcher = RULE_EQUIP_PET_PATTERN.matcher(unformattedLine);
            if (matcher.find()) {
                String name = matcher.group(1).replace(" ✦", "");

                String textName = name;
                int index = string.indexOf(name);
                if (index > COLOR_OFFSET) {
                    textName = string.substring(index - COLOR_OFFSET, index) + textName;
                }

                summonPet(name, Text.literal(textName), getLevel(unformattedLine), true);
            }
            return false;
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (receivedEntry == null) return false;
            Text text = receivedEntry.displayName();
            if (text == null) return false;

            String string = text.getString();
            if (string.equals(" No pet selected")) {
                despawnPet();
                return false;
            }

            Matcher matcher = TAB_PET_PATTERN.matcher(string);
            if (matcher.find()) {
                String petName = matcher.group(1).replace(" ✦", "");
                Style style = getStyle(text, petName);
                summonPet(petName, Text.literal(petName).setStyle(style), getLevel(string), false);
            }

            return false;
        });

        ClientTickEvents.END_WORLD_TICK.register((world -> {
            tick = Math.max(tick - 1, 0);
        }));

    }

    private static void despawnPet() {
        Events.ON_PET.invoke(petEvent -> petEvent.onPet(""));
        currentPetText = NO_PET;
        currentPetLevel = -1;
        currentPetString = "";
        spriteId = null;

    }

    private static void summonPet(String stringName, MutableText textName, int level, boolean sendSound) {
        currentPetLevel = level;
        if (stringName.equals(currentPetString)) {
            return;
        }

        currentPetText = textName;
        currentPetString = stringName;

        Events.ON_PET.invoke(petEvent -> petEvent.onPet(currentPetString));
        updateSprite(stringName);

        if (sendSound && ExtraOptions.sendOnPetSound) {
            Misc.sendSound(ExtraOptions.petSound);
            tick = TOTAL_TICKS;
        }
    }

    private static Style getStyle(Text text, String name) {
        Style style = Style.EMPTY;
        List<Text> lines = text.getSiblings();

        for (Text line : lines) {
            if (line.getString().equals(name)) {
                style = line.getStyle();
            }
        }

        return style;
    }

    private static int getLevel(String line) {
        Matcher matcher = LEVEL_REGEX.matcher(line);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return -1;
    }

    private static void updateSprite(String petName) {
        if (petName == null) {
            spriteId = null;
            return;
        }
        String formattedName = petName.toLowerCase().replace(" ", "-");
        if (Identifier.isNamespaceValid(formattedName)) {
            spriteId = Identifier.of(Constants.NAMESPACE, identifierPrefix + formattedName);
        } else {
            Debug.LOGGER.warn("{} includes invalid chars", formattedName);
        }
    }

    public static boolean display() {
        return ExtraOptions.drawPetHUD;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        if (spriteId != null && ExtraOptions.includePetSprite) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, spriteId, x, y, 16, 16, 0xffffffff);
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int textX = x + (ExtraOptions.includePetSprite ? 18 : 0);
        int textY = y + component.getHeight() - textRenderer.fontHeight;

        if (ExtraOptions.displayPetLevel && currentPetText != NO_PET) {
            context.drawText(textRenderer, Text.literal("§7[Lvl " + (currentPetLevel != -1 ? currentPetLevel : "???") + "]"), textX, textY, 0xffffffff, true);
            textY -= textRenderer.fontHeight;
        }

        context.drawText(textRenderer, currentPetText, textX, textY, 0xffffffff, true);
    }

    public static boolean displayNotification() {
        return ExtraOptions.sendPetSwapNotification && tick > 0;
    }

    public static void renderNotification(HUDComponent component, DrawContext context) {
        RenderUtils.drawCenteredText(context, component, currentPetText);
    }

}
