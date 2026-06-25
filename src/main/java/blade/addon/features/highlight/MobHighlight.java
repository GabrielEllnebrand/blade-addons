package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import blade.addon.utils.rendering.RenderingEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MobHighlight {

    public enum MobType {
        STAR, TANK, MINI, FEL, ASSASSIN, MIMIC
    }

    public record DataHolder(Entity entity, MobHighlight.MobType type) {
    }

    public enum HighlightType {
        FILLED("Filled"), OUTLINE("Outline"), BOTH("Both");

        private final String label;

        HighlightType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final int LEATHER_BOOTS_ID = Item.getId(Items.LEATHER_BOOTS);

    private static final ConcurrentHashMap<Integer, Entity> foundEntities = new ConcurrentHashMap<>();
    public static final ConcurrentLinkedQueue<ArmorStand> nonStaredTags = new ConcurrentLinkedQueue<>();
    public static final ConcurrentLinkedQueue<DataHolder> savedEntities = new ConcurrentLinkedQueue<>();

    public static boolean dontRenderHighlight = false;
    @ConfigValue
    public static boolean mobHighlight = false;
    @ConfigValue
    public static boolean dontShowInvisibleMobs = true;
    @ConfigValue
    public static HighlightType currentHighlight = HighlightType.FILLED;
    @ConfigValue
    public static double witherExtraWidth = 0;
    @ConfigValue
    public static boolean highlightMimicChests = true;
    @ConfigValue
    public static boolean highlightSheep = false;
    @ConfigValue
    public static boolean hideSheep = false;

    @ConfigValue
    public static int outlineWidth = 4;

    @ConfigValue
    public static boolean hideNoneStaredNameTags = false;

    @ConfigValue
    public static int starFilledColor = 0xff00ff00;
    @ConfigValue
    public static int tankFilledColor = 0xffff0000;
    @ConfigValue
    public static int miniFilledColor = 0xffffff00;
    @ConfigValue
    public static int felFilledColor = 0xff00ffff;
    @ConfigValue
    public static int assassinFilledColor = 0xff800080;
    @ConfigValue
    public static int batFilledColor = 0xff2244ff;
    @ConfigValue
    public static int witherFilledColor = 0x88222222;
    @ConfigValue
    public static int mimicFilledColor = 0xffffffff;
    @ConfigValue
    public static int sheepFilledColor = 0xffffffff;

    @ConfigValue
    public static int starOutlineColor = 0xff00ff00;
    @ConfigValue
    public static int tankOutlineColor = 0xffff0000;
    @ConfigValue
    public static int miniOutlineColor = 0xffffff00;
    @ConfigValue
    public static int felOutlineColor = 0xff00ffff;
    @ConfigValue
    public static int assassinOutlineColor = 0xff800080;
    @ConfigValue
    public static int batOutlineColor = 0xff2244ff;
    @ConfigValue
    public static int witherOutlineColor = 0x88222222;
    @ConfigValue
    public static int mimicOutlineColor = 0xffffffff;
    @ConfigValue
    public static int sheepOutlineColor = 0xffffffff;


    public static void init() {

        Events.ON_ENTITY_SPAWNED.register((entity, world) -> {
            if (entity instanceof ArmorStand) return false;
            foundEntities.put(entity.getId(), entity);
            return false;
        });

        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;

            if (entity instanceof Player player) {
                if (MobHighlight.isShadowAssassin(player)) {
                    savedEntities.add(new DataHolder(player, MobType.ASSASSIN));
                }
            }

            if (entity instanceof ArmorStand armorStand) {
                testArmorStand(armorStand);
            }

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            savedEntities.clear();
            foundEntities.clear();
            nonStaredTags.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            LocalPlayer player = minecraftClient.player;
            if (player == null) return;
            dontRenderHighlight = player.hasEffect(MobEffects.BLINDNESS);
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity == null) return;
            int id = entity.getId();
            savedEntities.removeIf(dataHolder -> dataHolder.entity == entity);
            foundEntities.remove(id);
            if (entity instanceof ArmorStand) {
                nonStaredTags.remove(entity);
            }
        });

        RenderingEvents.FILLED.register(MobHighlight::renderFilled);
        RenderingEvents.LINE.register(MobHighlight::renderOutline);
    }

    private static void testArmorStand(ArmorStand armorStand) {
        MobType type = getType(armorStand);
        if (type == null) {
            if (isGeneralStaredMob(armorStand)) {
                nonStaredTags.add(armorStand);
            }
            return;
        }

        int idOffset = getIdOffset(armorStand);
        if (idOffset < 0) return;

        int id = armorStand.getId() - idOffset;
        Entity entity = foundEntities.getOrDefault(id, null);
        if (entity == null) return;
        foundEntities.remove(id);
        savedEntities.add(new DataHolder(entity, type));
    }

    public static MobType getType(ArmorStand armorStand) {
        Component text = armorStand.getCustomName();
        if (text == null) return null;
        String name = text.getString();
        if (name.contains("King Midas")) return MobType.MINI;
        if (name.contains("Mimic")) return MobType.MIMIC;
        if (!MobHighlight.containsStarText(text)) return null;
        if (name.contains("Fel")) return MobType.FEL;
        if (isMiniBoss(name)) return MobType.MINI;
        if (isTankMob(name)) return MobType.TANK;
        return MobType.STAR;
    }

    public static int getIdOffset(ArmorStand armorStand) {
        Component text = armorStand.getCustomName();
        if (text == null) return -1;
        String name = text.getString();

        if (name.toLowerCase().contains("withermancer")) return 3;
        return 1;
    }

    public static boolean containsStarText(Component text) {
        for (Component sib : text.getSiblings()) {
            TextColor color = sib.getStyle().getColor();
            if (color == null) continue;

            if (color.getValue() == 0xFFAA00 && sib.getString().equals("✯ ")) {
                return true;
            }
        }

        return false;
    }

    public static boolean isGeneralStaredMob(ArmorStand armorStand) {
        Component text = armorStand.getCustomName();
        if (text == null) return false;
        String name = text.getString();
        return name.contains("Lurker") || name.contains("Dreadlord") || name.contains("Souleater") || name.contains("Zombie") || name.contains("Skeleton") || name.contains("Skeletor")
                || name.contains("Sniper") || name.contains("Spider") || name.contains("Fel") || isTankMob(name) || isMiniBoss(name);
    }

    public static boolean isTankMob(String name) {
        return name.contains("Zombie Commander") || name.contains("Zombie Lord") || name.contains("Skeleton Lord") || name.contains("Withermancer") || name.contains("Super Archer");
    }

    public static boolean isMiniBoss(String name) {
        return name.contains("Lost Adventurer") || name.contains("Angry Archaeologist") || name.contains("Frozen Adventurer");
    }

    public static boolean isShadowAssassin(Player player) {
        if (EntityUtil.isARealPlayer(player)) return false;
        ItemStack heldItem = player.getMainHandItem();
        ItemStack boots = player.getInventory().getItem(36);
        Component text = heldItem.getCustomName();

        if (text == null) return false;
        if (!text.getString().equals("Silent Death")) return false;

        return Item.getId(boots.getItem()) == LEATHER_BOOTS_ID;
    }

    public static int getFilledColor(MobType mob) {
        return switch (mob) {
            case STAR -> starFilledColor;
            case TANK -> tankFilledColor;
            case MINI -> miniFilledColor;
            case FEL -> felFilledColor;
            case ASSASSIN -> assassinFilledColor;
            case MIMIC -> mimicFilledColor;
        };
    }

    public static int getOutlineColor(MobType mob) {
        return switch (mob) {
            case STAR -> starOutlineColor;
            case TANK -> tankOutlineColor;
            case MINI -> miniOutlineColor;
            case FEL -> felOutlineColor;
            case ASSASSIN -> assassinOutlineColor;
            case MIMIC -> mimicOutlineColor;
        };
    }


    public static boolean renderFilled() {
        return currentHighlight == HighlightType.BOTH || currentHighlight == HighlightType.FILLED;
    }

    public static boolean renderOutline() {
        return currentHighlight == HighlightType.BOTH || currentHighlight == HighlightType.OUTLINE;
    }

    public static AABB getBox(Entity entity) {
        AABB box = EntityUtil.getBox(entity);

        //only shows the head
        if (entity instanceof EnderMan && entity.isInvisible() && MobHighlight.dontShowInvisibleMobs) {
            box = box.inflate(0, -1.8, 0).move(0, -1.2, 0);
        }

        //bigger mimic highlight
        if (entity instanceof Zombie zombie) {
            if (zombie.isBaby()) {
                box = box.inflate(0.15, 0.2, 0.15);
            }
        }

        return box;
    }


    private static void renderFilled(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.mobHighlight || dontRenderHighlight || !MobHighlight.renderFilled()) return;

        for (DataHolder savedEntity : savedEntities) {
            Entity entity = savedEntity.entity;
            MobType type = savedEntity.type;

            if (entity.isInvisible() && MobHighlight.dontShowInvisibleMobs && entity instanceof Player) return;

            AABB box = getBox(entity);
            int filledColor = getFilledColor(type);
            float[] rgba = RenderUtils.toFloats(filledColor);

            RenderUtils.renderFilledBox(matrixStack, consumer,box, rgba);

        }
    }

    private static void renderOutline(LevelRenderContext context, PoseStack matrixStack, VertexConsumer consumer) {
        if (!MobHighlight.mobHighlight || dontRenderHighlight || !MobHighlight.renderOutline()) return;

        for (DataHolder savedEntity : savedEntities) {
            Entity entity = savedEntity.entity;
            MobType type = savedEntity.type;

            if (entity.isInvisible() && MobHighlight.dontShowInvisibleMobs && entity instanceof Player) return;

            AABB box = getBox(entity);
            int outlineColor = getOutlineColor(type);
            float[] rgba = RenderUtils.toFloats(outlineColor);

            RenderUtils.renderOutlinedBox(matrixStack, consumer,box, rgba);
        }
    }
}
