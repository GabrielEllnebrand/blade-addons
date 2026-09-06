package blade.addon.features.other;

import blade.addon.utils.config.values.Visual;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompactDamageNumbers {

    private static final Pattern DAMAGE_PATTERN = Pattern.compile("^✧?✯?((?:\\d+,?)*)✧?✯?❤?$");

    private static final String[] CRIT_COLORS = {"§e", "§f", "§c", "§6"};

    public static void init() {

        Events.ON_ENTITY_TRACKED.register((entity, _) -> {

            if (entity instanceof ArmorStand target) {
                if (!target.hasCustomName() || !target.isCustomNameVisible()) return false;

                Component component = target.getCustomName();
                assert component != null;
                String name = component.getString();

                Matcher matcher = DAMAGE_PATTERN.matcher(name);
                if (!matcher.find()) return false;

                String numFound = matcher.group(1);
                boolean isCrit = name.contains("✧") || name.contains("✯");
                boolean loveHit = name.contains("❤");

                if ((isCrit && Visual.hideCrits) || (!isCrit && Visual.hideNoneCrits)) {
                    target.setCustomNameVisible(false);
                    return false;

                } else if (Visual.compactDamage) {
                    String formatted = compact(numFound);

                    if (isCrit) {
                        char symbol = name.charAt(0);
                        formatted = symbol + " " + formatted + " " + symbol;
                        formatted = styleCrit(formatted);
                        if (loveHit) formatted += " §d❤";
                        target.setCustomName(Component.literal(formatted));

                    } else {
                        MutableComponent newNum = Component.literal(formatted);
                        Style style = getStyle(component);
                        if (style != null) {
                            newNum.setStyle(style);
                        }
                        target.setCustomName(newNum);

                    }
                }
            }

            return false;
        });
    }

    private static String compact(String numFound) {
        String numOnly = numFound.replace(",", "");

        try {
            float num = Float.parseFloat(numOnly);
            return RenderUtils.formatNumber(num);
        } catch (NumberFormatException e) {
            return numFound;
        }
    }

    private static Style getStyle(Component component) {
        List<Component> siblings = component.getSiblings();
        if (siblings.isEmpty()) return null;
        return siblings.getFirst().getStyle();
    }

    private static String styleCrit(String crit) {
        StringBuilder styledCrit = new StringBuilder();
        int cnt = 0;
        for (char c : crit.toCharArray()) {
            if (c != ' ') {
                styledCrit.append(CRIT_COLORS[cnt % CRIT_COLORS.length]);
                cnt ++;
            }
            styledCrit.append(c);
        }

        return styledCrit.toString();
    }

}
