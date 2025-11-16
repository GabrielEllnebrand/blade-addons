package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import config.practical.manager.ConfigValue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobHighlight {

    record TrackedData(MobType mob, ArmorStandEntity nametag) {
    }

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

    private static final Pattern STAR_PATTERN = Pattern.compile("✯");
    private static final Pattern KING_PATTERN = Pattern.compile("King Midas");
    private static final Pattern WEAPON_PATTERN = Pattern.compile("^Silent Death$");
    private static final Pattern BOOTS_PATTERN = Pattern.compile("^Leather Boots$");
    private static final Pattern FEL_PATTERN = Pattern.compile("Fel");

    private static final float[] BAT_HEALTHS = {100.0f, 200.0f, 400.0f, 800.0f};
    private static final String[] TANK_MOBS = {"Zombie Commander", "Zombie Lord", "Skeleton Lord", "Withermancer", "Super Archer"};
    private static final String[] MINI_BOSSES = {"Lost Adventurer", "Angry Archaeologist", "Frozen Adventurer"};

    private static final ConcurrentHashMap<Entity, TrackedData> trackedMobs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Entity, Integer> trackedArmourStands = new ConcurrentHashMap<>();

    @ConfigValue
    public static boolean mobHighlight = false;
    @ConfigValue
    public static boolean dontShowInvisibleMobs = true;
    @ConfigValue
    public static HighlightType currentHighlight = HighlightType.FILLED;

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
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (!Location.inDungeon() || !mobHighlight) return;
            ClientWorld world = minecraftClient.world;
            if (world == null) return;

            ArrayList<Entity> entities = new ArrayList<>();
            world.getEntities().forEach(entities::add);

            scanEntities(entities);
        });


        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity == null) return;
            TrackedData data = trackedMobs.get(entity);
            trackedMobs.remove(entity);
            if (data == null) return;
            if (data.nametag == null) return;
            trackedArmourStands.remove(data.nametag);
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            trackedMobs.clear();
            trackedArmourStands.clear();
        });
    }

    private static void scanEntities(ArrayList<Entity> entities) {
        ArrayList<Entity> usefulMobs = new ArrayList<>();
        ArrayList<ArmorStandEntity> armorStands = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity == null) continue;
            if (trackedMobs.containsKey(entity) || trackedArmourStands.containsKey(entity)) continue;

            if (entity instanceof ArmorStandEntity armorStand) {
                armorStands.add(armorStand);
            }

            if (entity instanceof ZombieEntity zombie) {
                if (zombie.isBaby()) {
                    trackedMobs.put(zombie, new TrackedData(MobType.MIMIC, null));
                    continue;
                }
            }

            if (entity instanceof BatEntity bat) {
                for (float health : BAT_HEALTHS) {
                    if (health == bat.getHealth()) {
                        trackedMobs.put(bat, new TrackedData(MobType.BAT, null));
                    }
                }
            }

            if (entity instanceof PlayerEntity player) {
                if (isShadowAssassin(player)) {
                    trackedMobs.put(player, new TrackedData(MobType.ASSASSIN, null));
                    continue;
                }
            }

            if (entity instanceof WitherEntity wither) {
                if (wither.getInvulnerableTimer() == 800) continue;
                trackedMobs.put(wither, new TrackedData(MobType.WITHER, null));
            }


            if (isAPossibleStaredMob(entity) || entity instanceof EndermanEntity) {
                usefulMobs.add(entity);
            }
        }

        armorStands.forEach(armorStand -> testArmourStand(armorStand, usefulMobs));
    }

    private static void testArmourStand(ArmorStandEntity armorStand, ArrayList<Entity> entities) {
        Text text = armorStand.getCustomName();
        if (text == null) return;
        String string = text.getString();
        Matcher matcher = STAR_PATTERN.matcher(string);
        if (matcher.find()) {

            matcher = FEL_PATTERN.matcher(string);
            if (matcher.find()) {
                Entity closet = getClosest(armorStand, entities, entity -> entity instanceof EndermanEntity);

                if (closet == null) {
                    return;
                }

                trackedMobs.put(closet, new TrackedData(MobType.FEL, armorStand));
                trackedArmourStands.put(armorStand, 0);
                return;
            }


            Entity closet = getClosest(armorStand, entities, MobHighlight::isAPossibleStaredMob);

            if (closet == null) {
                return;
            }


            if (isMiniBoss(string)) {
                trackedMobs.put(closet, new TrackedData(MobType.MINI, armorStand));
            } else if (isTankMob(string)) {
                trackedMobs.put(closet, new TrackedData(MobType.TANK, armorStand));
            } else {
                trackedMobs.put(closet, new TrackedData(MobType.STAR, armorStand));
            }
            trackedArmourStands.put(armorStand, 0);
            return;
        }

        matcher = KING_PATTERN.matcher(string);
        if (matcher.find()) {
            Entity closet = getClosest(armorStand, entities, entity -> !isARealPlayer(entity));

            if (closet == null) {
                return;
            }
            trackedMobs.put(closet, new TrackedData(MobType.MINI, armorStand));
            trackedArmourStands.put(armorStand, 0);
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

    private static Entity getClosest(ArmorStandEntity armorStand, ArrayList<Entity> entities, Predicate<Entity> requirements) {
        Entity closest = null;
        double smallestDistance = 3.5;
        double maxY = armorStand.getY();

        for (Entity current : entities) {
            if (trackedMobs.containsKey(current)) continue;
            if (!requirements.test(current)) continue;
            if (current.getY() > maxY) continue;

            double distance = armorStand.getPos().distanceTo(current.getPos());
            if (smallestDistance > distance) {
                closest = current;
                smallestDistance = distance;
            }

        }

        return closest;
    }

    private static boolean isARealPlayer(Entity entity) {
        if (entity instanceof PlayerEntity player) {

            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return false;

            PlayerListEntry entry = networkHandler.getPlayerListEntry(player.getUuid());
            return entry != null;
        }
        return false;
    }

    private static boolean isAPossibleStaredMob(Entity entity) {
        return switch (entity) {
            case ZombieEntity ignored -> true;
            case SkeletonEntity ignored -> true;
            case WitherSkeletonEntity ignored -> true;
            case PlayerEntity playerEntity -> !isARealPlayer(playerEntity);
            default -> false;
        };
    }

    private static boolean isShadowAssassin(PlayerEntity player) {
        if (isARealPlayer(player)) return false;
        ItemStack heldItem = player.getMainHandStack();
        ItemStack boots = player.getInventory().getStack(36);
        Text text = heldItem.getCustomName();

        if (text == null) return false;
        Matcher matcher = WEAPON_PATTERN.matcher(text.getString());
        if (!matcher.find()) return false;

        text = boots.getCustomName();
        if (text == null) return false;
        matcher = BOOTS_PATTERN.matcher(text.getString());
        return matcher.find();
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

    public static int getFilledColor(Entity entity) {
        return getFilledColor(trackedMobs.get(entity).mob);
    }

    public static int getOutlineColor(Entity entity) {
        return getOutLineColor(trackedMobs.get(entity).mob);
    }

    public static boolean renderFilled() {
        return currentHighlight == HighlightType.BOTH || currentHighlight == HighlightType.FILLED;
    }

    public static boolean renderOutline() {
        return currentHighlight == HighlightType.BOTH || currentHighlight == HighlightType.OUTLINE;
    }
}
