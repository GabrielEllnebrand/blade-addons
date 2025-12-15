package blade.addon.features.other;

import blade.addon.utils.Constants;
import blade.addon.utils.Debug;
import blade.addon.utils.config.values.ExtraOptions;
import blade.addon.utils.events.Events;
import config.practical.hud.HUDComponent;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectedPet {

    private static final int COLOR_OFFSET = 2;

    private static final Pattern TAB_PET_PATTERN = Pattern.compile(" \\[Lvl \\d+] (\\D+)");
    private static final Pattern MANUAL_EQUIP_PET_PATTERN = Pattern.compile("^You (summoned|despawned) your (\\D+)!$");
    private static final Pattern RULE_EQUIP_PET_PATTERN = Pattern.compile("^Autopet equipped your \\[Lvl [0-9]+] (\\D+)! VIEW RULE$");

    private static final Text NO_PET = Text.literal("No pet").formatted(Formatting.RED);
    private static final String identifierPrefix = "pets/";

    private static Text currentPetText = NO_PET;
    private static String currentPetString = "";
    private static Identifier spriteId = null;

    public static void init() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String string = message.getString();
            Matcher matcher = MANUAL_EQUIP_PET_PATTERN.matcher(string);
            if (matcher.find()) {

                if (matcher.group(1).equals("despawned")) {
                    despawnPet();
                    return;
                }

                String name = matcher.group(2).replace(" ✦", "");
                Style style = getStyle(message, name);
                summonPet(name, Text.literal(name).setStyle(style));
                return;
            }

            matcher = RULE_EQUIP_PET_PATTERN.matcher(string.replaceAll("§.", ""));
            if (matcher.find()) {
                String name = matcher.group(1).replace(" ✦", "");

                String textName = name;
                int index = string.indexOf(name);
                if (index > COLOR_OFFSET) {
                    textName = string.substring(index - COLOR_OFFSET, index) + textName;
                }

                summonPet(name, Text.literal(textName));
            }
        });

        Events.ON_PLAYER_ENTRY.register(receivedEntry -> {
            if (receivedEntry == null) return;
            Text text = receivedEntry.displayName();
            if (text == null) return;

            String string = text.getString();
            if (string.equals(" No pet selected")) {
                despawnPet();
                return;
            }

            Matcher matcher = TAB_PET_PATTERN.matcher(string);
            if (matcher.find()) {
                String petName = matcher.group(1).replace(" ✦", "");
                Style style = getStyle(text, petName);
                summonPet(petName, Text.literal(petName).setStyle(style));
            }
        });
    }

    private static void despawnPet() {

        if (!currentPetString.isEmpty() && Events.ON_PET.hasListeners()) {
            Events.ON_PET.invoke(petEvent -> petEvent.onPet(""));
        }

        currentPetText = NO_PET;
        currentPetString = "";
        spriteId = null;

    }

    private static void summonPet(String stringName, Text textName) {
        if (stringName.equals(currentPetString)) {
            return;
        }

        currentPetText = textName;
        currentPetString = stringName;

        if (Events.ON_PET.hasListeners()) {
            Events.ON_PET.invoke(petEvent -> petEvent.onPet(currentPetString));
        }

        updateSprite(stringName);
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

        context.drawText(textRenderer, currentPetText, x + (ExtraOptions.includePetSprite? 18: 0), y + component.getHeight() - textRenderer.fontHeight, 0xffffffff, true);

    }

}
