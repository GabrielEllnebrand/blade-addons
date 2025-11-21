package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobHighlight {

    enum MobType {
        STAR, TANK, MINI, FEL, ASSASSIN, BAT, WITHER, MIMIC
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

    private static final int DIAMOND_LEGGINGS_ID = Item.getRawId(Items.DIAMOND_LEGGINGS);
    private static final int LEATHER_LEGGINGS_ID = Item.getRawId(Items.LEATHER_LEGGINGS);
    private static final int LEATHER_BOOTS_ID = Item.getRawId(Items.LEATHER_BOOTS);

    private static final Pattern STAR_PATTERN = Pattern.compile("✯");
    private static final Pattern MIMIC_PATTERN = Pattern.compile("Mimic");
    private static final Pattern WEAPON_PATTERN = Pattern.compile("^Silent Death$");
    private static final Pattern FEL_PATTERN = Pattern.compile("Fel");

    private static final float WITHER_BORN_HEALTH = 300f;
    private static final float[] BAT_HEALTHS = {100.0f, 200.0f, 400.0f, 800.0f};
    private static final String[] TANK_MOBS = {"Zombie Commander", "Zombie Lord", "Skeleton Lord", "Withermancer", "Super Archer"};
    private static final String[] MINI_BOSSES = {"Lost Adventurer", "Angry Archaeologist", "Frozen Adventurer"};

    private static final ConcurrentHashMap<Entity, MobType> trackedMobs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Entity, Integer> possibleEntities = new ConcurrentHashMap<>();

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


    public static void init() {
        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon() || !mobHighlight) return;

            if (Phase.inBoss()) {
                if (entity instanceof WitherEntity wither) {
                    if (wither.getHealth() != WITHER_BORN_HEALTH) {
                        trackedMobs.put(wither, MobType.WITHER);
                    }
                }
            } else {
                if (entity instanceof ArmorStandEntity armorStand) {
                    testArmourStand(armorStand);
                }

                if (entity instanceof BatEntity bat) {
                    for (float health : BAT_HEALTHS) {
                        if (health == bat.getHealth()) {
                            trackedMobs.put(bat, MobType.BAT);
                        }
                    }
                }

                if (entity instanceof PlayerEntity player) {
                    if (isShadowAssassin(player)) {
                        trackedMobs.put(player, MobType.ASSASSIN);
                    }
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            ClientPlayerEntity player = minecraftClient.player;
            if (player == null) return;
            dontRenderHighlight = player.hasStatusEffect(StatusEffects.BLINDNESS);
        });

        ClientEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
            if (entity instanceof PlayerEntity player) {
                if (isARealPlayer(player)) return;
            }

            if (isAPossibleStaredMob(entity) || entity instanceof EndermanEntity) {
                possibleEntities.put(entity, 0);
            }
        }));

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity == null) return;
            trackedMobs.remove(entity);
            possibleEntities.remove(entity);
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            trackedMobs.clear();
            possibleEntities.clear();
        });
    }

    private static void testArmourStand(ArmorStandEntity armorStand) {
        Text text = armorStand.getCustomName();
        if (text == null) return;
        String string = text.getString();

        Matcher matcher = MIMIC_PATTERN.matcher(string);
        if (matcher.find()) {
            attemptAddMob(armorStand, MobHighlight::isAPossibleMimic, 3.5, MobType.MIMIC);
            return;
        }

        matcher = STAR_PATTERN.matcher(string);
        if (matcher.find()) {
            matcher = FEL_PATTERN.matcher(string);
            if (matcher.find()) {
                attemptAddMob(armorStand, entity -> entity instanceof EndermanEntity, 4, MobType.FEL);
            } else if (isMiniBoss(string)) {
                attemptAddMob(armorStand, MobHighlight::isMiniBoss, 4, MobType.MINI);
            } else if (isTankMob(string)) {
                attemptAddMob(armorStand, MobHighlight::isAPossibleStaredMob, 3, MobType.TANK);
            } else {
                attemptAddMob(armorStand, MobHighlight::isAPossibleStaredMob, 3, MobType.STAR);
            }
        }
    }

    public static boolean isTankMob(String string) {
        for (String tank : TANK_MOBS) {
            if (string.contains(tank)) return true;
        }

        return false;
    }

    public static boolean isMiniBoss(String string) {
        for (String mini : MINI_BOSSES) {
            if (string.contains(mini)) return true;
        }

        return false;
    }

    private static void attemptAddMob(ArmorStandEntity armorStand, Predicate<Entity> requirements, double maxDistance, MobType mobType) {
        Entity closest = null;
        double smallestDistance = maxDistance;
        double maxY = armorStand.getY();
        Box boundingBox = Box.of(armorStand.getPos().subtract(0, 0.5, 0), 2, 3, 2);


        ArrayList<Entity> meetsRequirements = new ArrayList<>();

        possibleEntities.forEach((entity, integer) -> {
            if (!entity.getBoundingBox().intersects(boundingBox)) return;
            if (!requirements.test(entity)) return;
            if (entity.getY() > maxY) return;

            meetsRequirements.add(entity);
        });

        for (Entity current : meetsRequirements) {
            if (trackedMobs.containsKey(current)) continue;
            if (!current.getBoundingBox().intersects(boundingBox)) continue;
            if (!requirements.test(current)) continue;
            if (current.getY() > maxY) continue;

            double distance = armorStand.getPos().distanceTo(current.getPos());
            if (smallestDistance > distance) {
                closest = current;
                smallestDistance = distance;
            }

        }

        if (closest == null) {
            return;
        }

        possibleEntities.remove(closest);
        trackedMobs.put(closest, mobType);
    }

    private static boolean isShadowAssassin(PlayerEntity player) {
        if (isARealPlayer(player)) return false;
        ItemStack heldItem = player.getMainHandStack();
        ItemStack boots = player.getInventory().getStack(36);
        Text text = heldItem.getCustomName();

        if (text == null) return false;
        Matcher matcher = WEAPON_PATTERN.matcher(text.getString());
        if (!matcher.find()) return false;

        return Item.getRawId(boots.getItem()) == LEATHER_BOOTS_ID;
    }

    private static boolean isARealPlayer(Entity entity) {
        if (entity instanceof PlayerEntity player) {

            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return false;

            PlayerListEntry entry = networkHandler.getPlayerListEntry(player.getUuid());

            //this is a hack which will fail if someone has a really old bugged ign that includes a space
            if (entry != null) {
                String name = entry.getProfile().getName();
                return !name.isEmpty() && !name.contains(" ");
            }
        }
        return false;
    }

    private static boolean isAPossibleStaredMob(Entity entity) {
        return switch (entity) {
            case ZombieEntity ignored -> true;
            case SkeletonEntity ignored -> true;
            case WitherSkeletonEntity ignored -> true;
            case PlayerEntity ignored -> true;
            default -> false;
        };
    }

    private static boolean isAPossibleMimic(Entity entity) {
        if (entity instanceof ZombieEntity zombie) {
            return zombie.isBaby();
        }
        return false;
    }

    private static boolean isMiniBoss(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            ItemStack leggings = player.getInventory().getStack(37);
            int id = Item.getRawId(leggings.getItem());
            return id == DIAMOND_LEGGINGS_ID || id == LEATHER_LEGGINGS_ID;
        }

        return false;
    }

    public static boolean hasEntity(Entity entity) {
        if (entity == null) return false;
        return trackedMobs.containsKey(entity);
    }

    private static int getFilledColor(MobType mob) {
        return switch (mob) {
            case STAR -> starFilledColor;
            case TANK -> tankFilledColor;
            case MINI -> miniFilledColor;
            case FEL -> felFilledColor;
            case ASSASSIN -> assassinFilledColor;
            case BAT -> batFilledColor;
            case WITHER -> witherFilledColor;
            case MIMIC -> mimicFilledColor;
        };
    }

    private static int getOutLineColor(MobType mob) {
        return switch (mob) {
            case STAR -> starOutlineColor;
            case TANK -> tankOutlineColor;
            case MINI -> miniOutlineColor;
            case FEL -> felOutlineColor;
            case ASSASSIN -> assassinOutlineColor;
            case BAT -> batOutlineColor;
            case WITHER -> witherOutlineColor;
            case MIMIC -> mimicOutlineColor;
        };
    }

    public static boolean dontRender() {
        return dontRenderHighlight;
    }

    public static int getFilledColor(Entity entity) {
        return getFilledColor(trackedMobs.get(entity));
    }

    public static int getOutlineColor(Entity entity) {
        return getOutLineColor(trackedMobs.get(entity));
    }

    public static boolean renderFilled() {
        return currentHighlight == HighlightType.BOTH || currentHighlight == HighlightType.FILLED;
    }

    public static boolean renderOutline() {
        return currentHighlight == HighlightType.BOTH || currentHighlight == HighlightType.OUTLINE;
    }
}
