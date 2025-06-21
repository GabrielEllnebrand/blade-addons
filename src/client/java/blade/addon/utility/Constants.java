package blade.addon.utility;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Constants {

    public static final String NAMESPACE = "blade-addons";

    //Used to disable clutter messages in chat
    public static final Text SKYBLOCKER_TEXT = Text.empty()
            .append(Text.literal("[").formatted(Formatting.GRAY))
            .append(Text.literal("S").withColor(0x00ff4c))
            .append(Text.literal("k").withColor(0x02fa60))
            .append(Text.literal("y").withColor(0x04f574))
            .append(Text.literal("b").withColor(0x07ef88))
            .append(Text.literal("l").withColor(0x09ea9c))
            .append(Text.literal("o").withColor(0x0be5af))
            .append(Text.literal("c").withColor(0x0de0c3))
            .append(Text.literal("k").withColor(0x10dad7))
            .append(Text.literal("e").withColor(0x12d5eb))
            .append(Text.literal("r").withColor(0x14d0ff))
            .append(Text.literal("] ").formatted(Formatting.GRAY));

}
