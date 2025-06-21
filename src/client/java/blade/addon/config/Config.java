package blade.addon.config;

import blade.addon.utility.Constants;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.ArrayList;

public class Config {

    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of(Constants.NAMESPACE))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve(Constants.NAMESPACE + ".json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static Config configInstance;

    static {
        HANDLER.load();
        configInstance = HANDLER.instance(); // load on class load
    }

    public static Screen create(Screen parent) {

        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal(Constants.NAMESPACE + " Config"))
                .save(HANDLER::save)
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General Settings"))

                        //rendering option
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Toggle rendering through walls"))
                                .binding(HANDLER.instance().seeThroughRendering, () -> HANDLER.instance().seeThroughRendering, val -> HANDLER.instance().seeThroughRendering = val)
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        //hide skyblocker messages
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Toggle skyblocker messages in chat"))
                                .binding(HANDLER.instance().hideSkyblockerMessages, () -> HANDLER.instance().hideSkyblockerMessages, val -> HANDLER.instance().hideSkyblockerMessages = val)
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        //auto refill
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Auto fill stuff"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Auto refill pearl"))
                                        .binding(HANDLER.instance().autoFillPearl, () -> HANDLER.instance().autoFillPearl, val -> HANDLER.instance().autoFillPearl = val)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Auto refill superboom"))
                                        .binding(HANDLER.instance().autoFillSuperBoom, () -> HANDLER.instance().autoFillSuperBoom, val -> HANDLER.instance().autoFillSuperBoom = val)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())

                //foraging category
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Foraging"))
                        //azalea scan
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Azalea Render"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Toggle azalea render"))
                                        .binding(HANDLER.instance().enableBush, () -> HANDLER.instance().enableBush, val -> HANDLER.instance().enableBush = val)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.literal("Distance to scan for azaleas"))
                                        .binding(HANDLER.instance().bushDistance, () -> HANDLER.instance().bushDistance, val -> HANDLER.instance().bushDistance = val)
                                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                                .range(20.0, 100.0)
                                                .step(1.0)
                                                .formatValue(val -> Text.literal("Blocks: " + val)))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Growing color"))
                                        .binding(HANDLER.instance().growingColor, () -> HANDLER.instance().growingColor, val -> HANDLER.instance().growingColor = val)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(true))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Flowering color"))
                                        .binding(HANDLER.instance().floweringColor, () -> HANDLER.instance().floweringColor, val -> HANDLER.instance().floweringColor = val)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(true))
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Invisbug guesser"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Toggle bug render"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.literal("This uses predetermined positions with some guess distance. Will probably not be 100% accurate."))
                                                .build())
                                        .binding(HANDLER.instance().enableBug, () -> HANDLER.instance().enableBug, val -> HANDLER.instance().enableBug = val)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Bug highlight color"))
                                        .binding(HANDLER.instance().bugColor, () -> HANDLER.instance().bugColor, val -> HANDLER.instance().bugColor = val)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(true))
                                        .build())
                                .build())

                        .build())


                .category(ConfigCategory.createBuilder()
                        //entity highlight
                        .name(Text.literal("Highlight"))
                        .option(Option.<Color>createBuilder()
                                .name(Text.literal("Highlight color"))
                                .binding(HANDLER.instance().highlightColor, () -> HANDLER.instance().highlightColor, val -> HANDLER.instance().highlightColor = val)
                                .controller(opt -> ColorControllerBuilder.create(opt)
                                        .allowAlpha(true))
                                .build())
                        .group(ListOption.<String>createBuilder()
                                .name(Text.literal("Highlight entities: Hypixel armorstand name"))
                                .binding(
                                        new ArrayList<>(HANDLER.instance().entityHypixelNames),
                                        () -> new ArrayList<>(HANDLER.instance().entityHypixelNames),
                                        val -> HANDLER.instance().entityHypixelNames = new ArrayList<>(val)
                                )
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .group(ListOption.<String>createBuilder()
                                .name(Text.literal("Highlight entities: Vanilla name"))
                                .binding(
                                        new ArrayList<>(HANDLER.instance().entityVanillaNames),
                                        () -> new ArrayList<>(HANDLER.instance().entityVanillaNames),
                                        val -> HANDLER.instance().entityVanillaNames = new ArrayList<>(val)
                                )
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .build())

                .build()
                .generateScreen(parent);
    }


    @SerialEntry
    public boolean enableBush = false;

    @SerialEntry
    public double bushDistance = 100;

    @SerialEntry
    public Color growingColor = new Color(0.75f, 0, 0, 0.5f);

    @SerialEntry
    public Color floweringColor = new Color(0, 1f, 0, 0.5f);

    @SerialEntry
    public boolean enableBug = false;

    @SerialEntry
    public Color bugColor = new Color(0, 1f, 1f, 0.5f);

    @SerialEntry
    public boolean autoFillPearl = false;

    @SerialEntry
    public boolean autoFillSuperBoom = false;

    @SerialEntry
    public ArrayList<String> entityHypixelNames = new ArrayList<>();

    @SerialEntry
    public ArrayList<String> entityVanillaNames = new ArrayList<>();

    @SerialEntry
    public Color highlightColor = new Color(0.7f, 0, 0, 0.75f);

    @SerialEntry
    public boolean hideSkyblockerMessages = false;

    @SerialEntry
    public boolean seeThroughRendering = false;
}
