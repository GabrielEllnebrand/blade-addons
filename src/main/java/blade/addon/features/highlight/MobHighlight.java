package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.interfaces.ArmourStandHolder;
import blade.addon.utils.rendering.RenderLayers;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class MobHighlight {

    public enum MobType {
        STAR, TANK, MINI, FEL, ASSASSIN, MIMIC
    }

    record DataHolder(Entity entity, MobHighlight.MobType type) {}

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

    private static final int DIAMOND_HELMET_ID = Item.getRawId(Items.DIAMOND_HELMET);
    private static final int PLAYER_HEAD_ID = Item.getRawId(Items.PLAYER_HEAD);
    private static final int LEATHER_BOOTS_ID = Item.getRawId(Items.LEATHER_BOOTS);

    private static final ConcurrentLinkedQueue<DataHolder> savedEntities = new ConcurrentLinkedQueue<>();


    private static boolean dontRenderHighlight = false;
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
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!Location.inDungeon()) return;

            ClientWorld world = client.world;
            if (world == null) return;
            for (Entity entity: world.getEntities()) {
                if (entity instanceof ArmorStandEntity armorStand) {
                    ArmourStandHolder holder = (ArmourStandHolder) armorStand;
                    if (holder.blade_addons$hasBeenScanned()) continue;
                    holder.blade_addons$setScanned(true);
                    testArmourStand(armorStand, client);
                }
            }

            savedEntities.removeIf(dataHolder -> dataHolder.entity.isRemoved());
        });

        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;

            if (entity instanceof PlayerEntity player) {
                if (MobHighlight.isShadowAssassin(player)) {
                    savedEntities.add(new DataHolder(player, MobType.ASSASSIN));
                }
            }

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            savedEntities.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            ClientPlayerEntity player = minecraftClient.player;
            if (player == null) return;
            dontRenderHighlight = player.hasStatusEffect(StatusEffects.BLINDNESS);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(MobHighlight::renderFilled);
        WorldRenderEvents.AFTER_ENTITIES.register(MobHighlight::renderOutline);
    }

    public static void testArmourStand(ArmorStandEntity armorStand, MinecraftClient client) {
        Text text = armorStand.getCustomName();
        if (text == null) return;
        String name = text.getString();

        ClientWorld world = client.world;
        if (world == null) return;

        if (name.contains("Mimic")) {
            attemptAddMob(armorStand, MobHighlight::isAPossibleMimic, 3.5, MobHighlight.MobType.MIMIC, world);
            return;
        }

        if (!MobHighlight.containsStarText(text)) return;

        if (name.contains("Fel")) {
            attemptAddMob(armorStand, entity -> entity instanceof EndermanEntity, 4, MobHighlight.MobType.FEL, world);
        } else if (isMiniBoss(name)) {
            attemptAddMob(armorStand, MobHighlight::isMiniBoss, 4, MobHighlight.MobType.MINI, world);
        } else if (isTankMob(name)) {
            attemptAddMob(armorStand, MobHighlight::isAPossibleStaredMob, 3, MobHighlight.MobType.TANK, world);
        } else {
            attemptAddMob(armorStand, MobHighlight::isAPossibleStaredMob, 3, MobHighlight.MobType.STAR, world);
        }

    }

    public static boolean containsStarText(Text text) {
        for (Text sib : text.getSiblings()) {
            TextColor color = sib.getStyle().getColor();
            if (color == null) continue;

            if (color.getRgb() == 0xFFAA00 && sib.getString().equals("✯ ")) {
                return true;
            }

        }

        return false;
    }

    public static boolean isTankMob(String name) {
        return name.contains("Zombie Commander") || name.contains("Zombie Lord") || name.contains("Skeleton Lord")|| name.contains("Withermancer") || name.contains("Super Archer");
    }

    public static boolean isMiniBoss(String name) {
        return name.contains("Lost Adventurer") || name.contains("Angry Archaeologist") || name.contains("Frozen Adventurer");
    }

    public static void attemptAddMob(ArmorStandEntity armorStand, Predicate<Entity> requirements, double maxDistance, MobHighlight.MobType mobType, ClientWorld world) {
        Entity closest = null;
        double smallestDistance = maxDistance;
        double maxY = armorStand.getY();
        Box boundingBox = Box.of(armorStand.getEntityPos().subtract(0, 0.5, 0), 2, 3, 2);


        ArrayList<Entity> meetsRequirements = new ArrayList<>();

        Iterable<Entity> entities = world.getEntities();
        entities.forEach(entity -> {
            if (!entity.getBoundingBox().intersects(boundingBox)) return;
            if (!requirements.test(entity)) return;
            if (entity.getY() > maxY) return;

            meetsRequirements.add(entity);
        });

        for (Entity current : meetsRequirements) {
            if (!current.getBoundingBox().intersects(boundingBox)) continue;
            if (!requirements.test(current)) continue;
            if (current.getY() > maxY) continue;

            double distance = armorStand.getEntityPos().distanceTo(current.getEntityPos());
            if (smallestDistance > distance) {
                closest = current;
                smallestDistance = distance;
            }

        }

        if (closest == null) {
            return;
        }

        savedEntities.add(new DataHolder(closest, mobType));
    }

    public static boolean isShadowAssassin(PlayerEntity player) {
        if (EntityUtil.isARealPlayer(player)) return false;
        ItemStack heldItem = player.getMainHandStack();
        ItemStack boots = player.getInventory().getStack(36);
        Text text = heldItem.getCustomName();

        if (text == null) return false;
        if (!text.getString().equals("Silent Death")) return false;

        return Item.getRawId(boots.getItem()) == LEATHER_BOOTS_ID;
    }

    public static boolean isAPossibleStaredMob(Entity entity) {
        return switch (entity) {
            case ZombieEntity ignored -> true;
            case SkeletonEntity ignored -> true;
            case WitherSkeletonEntity ignored -> true;
            case PlayerEntity ignored -> !isMiniBoss(entity);
            default -> false;
        };
    }

    public static boolean isAPossibleMimic(Entity entity) {
        if (entity instanceof ZombieEntity zombie) {
            return zombie.isBaby();
        }
        return false;
    }

    public static boolean isMiniBoss(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (EntityUtil.isARealPlayer(player)) return false;
            ItemStack helmet = player.getInventory().getStack(39);
            int id = Item.getRawId(helmet.getItem());
            return id == DIAMOND_HELMET_ID || id == PLAYER_HEAD_ID;
        }

        return false;
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

    public static Box getBox(Entity entity, Vec3d pos) {
        EntityDimensions dimension = entity.getDimensions(entity.getPose());
        Box box = dimension.getBoxAt(pos);

        //only shows the head
        if (entity instanceof EndermanEntity && entity.isInvisible() && MobHighlight.dontShowInvisibleMobs) {
            box = box.expand(0, -1.8, 0).offset(0, -1.2, 0);
        }

        //bigger mimic highlight
        if (entity instanceof ZombieEntity zombie) {
            if (zombie.isBaby()) {
                box = box.expand(0.15, 0.2, 0.15);
            }
        }

        return box;
    }

    private static void renderFilled(WorldRenderContext context) {
        if (!mobHighlight || dontRenderHighlight) return;
        if (!renderFilled()) return;
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer filledConsumer = consumers.getBuffer(RenderLayers.FILLED_ENTITY_LAYER);

        double tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);

        savedEntities.forEach(dataHolder -> {

            Entity entity = dataHolder.entity;
            MobType type = dataHolder.type;

            if (entity.isInvisible() && MobHighlight.dontShowInvisibleMobs && entity instanceof PlayerEntity) return;

            Vec3d pos = Misc.getPos(entity, tickProgress);

            Box box = getBox(entity, pos);


            int filledColor = getFilledColor(type);
            float[] rgba = RenderUtils.toFloats(filledColor);
            VertexRendering.drawFilledBox(matrices, filledConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, rgba[0], rgba[1], rgba[2], rgba[3]);
        });


        matrices.pop();

    }

    private static void renderOutline(WorldRenderContext context) {
        if (!mobHighlight || dontRenderHighlight) return;
        if (!renderOutline()) return;
        Vec3d camera = context.worldState().cameraRenderState.pos;
        MatrixStack matrices = context.matrices();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer outlineConsumer = consumers.getBuffer(RenderLayers.getOutline(outlineWidth));

        double tickProgress = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);

        MatrixStack.Entry entry = matrices.peek();

        savedEntities.forEach(dataHolder -> {

            Entity entity = dataHolder.entity;
            MobType type = dataHolder.type;

            if (entity.isInvisible() && MobHighlight.dontShowInvisibleMobs && entity instanceof PlayerEntity) return;

            Vec3d pos = Misc.getPos(entity, tickProgress);
            Box box = getBox(entity, pos);

            int outlineColor = getOutlineColor(type);
            float[] rgba = RenderUtils.toFloats(outlineColor);
            VertexRendering.drawBox(entry, outlineConsumer, box, rgba[0], rgba[1], rgba[2], rgba[3]);
        });


        matrices.pop();

    }
}
