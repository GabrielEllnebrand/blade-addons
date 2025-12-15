package blade.addon.features.dungeon.f7;

import blade.addon.utils.Constants;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

public class DragSpawnTimer {

    enum Dragon {
        PURPLE(0xffff55ff, 0, 4),
        BLUE(0xff55ffff, 1, 3),
        RED(0xffff5555, 2, 2),
        GREEN(0xff55ff55, 3, 1),
        ORANGE(0xffffaa00, 4, 0),
        NONE(0xffffffff, 99, 99);

        final int color;
        final int archPrio;
        final int bersPrio;

        Dragon(int color, int archPrio, int bersPrio) {
            this.color = color;
            this.archPrio = archPrio;
            this.bersPrio = bersPrio;
        }
    }

    public enum Team {
        ARCHER_TEAM("Archer team"), BERS_TEAM("Bers team");

        private final String label;

        Team(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final int SPAWN_DURATION = 100;
    private static final int DUPE_DELAY = 130;

    private static Dragon currentDragon = Dragon.NONE;
    private static Dragon prevDragon = Dragon.NONE;
    private static boolean hasDoneSplit = false;
    private static int tick = 0;
    private static int dupeTick = 0;

    public static void init() {
        Events.ON_PARTICLE.register((x, y, z, effect) -> {
            if (effect.getType() != ParticleTypes.FLAME) return;
            Dragon detectedDragon = getDragon(x, y, z);

            if (detectedDragon == Dragon.NONE) return;

            //the dupe check is to stop purple from just breaking it when it spawns
            if (currentDragon == Dragon.NONE) {
                if ((prevDragon == detectedDragon && dupeTick > 0)) return;
                currentDragon = detectedDragon;
                prevDragon = currentDragon;
                dupeTick = DUPE_DELAY;
                tick = SPAWN_DURATION;
                if (Floor7.sendSoundOnDragSpawn) {
                    Scheduler.scheduleSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 0.75f, 1);
                }
            } else if (detectedDragon != currentDragon && !hasDoneSplit) {
                currentDragon = getPrio(detectedDragon, currentDragon);
                prevDragon = currentDragon;
                dupeTick = DUPE_DELAY + 50;
                hasDoneSplit = true;
            }
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);
            dupeTick = Math.max(dupeTick - 1, 0);
            if (dupeTick == 0 && prevDragon != Dragon.NONE) {
                prevDragon = Dragon.NONE;
            }

            if (tick == 0 && currentDragon != Dragon.NONE) {
                currentDragon = Dragon.NONE;
            }
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> reset());
    }

    private static void reset() {
        hasDoneSplit = false;
        currentDragon = Dragon.NONE;
        prevDragon = Dragon.NONE;
        tick = 0;
    }

    //checks are from valley addons
    private static Dragon getDragon(double x, double y, double z) {
        // check if correct height
        if (y >= 14 && y <= 19) {
            // check if red/green
            if (x >= 27 && x <= 32) {
                // check if red
                if (z == 59) {
                    return Dragon.RED;
                    // check if green
                } else if (z == 94) {
                    return Dragon.GREEN;
                }
                // check if blue/orange
            } else if (x >= 79 && x <= 85) {
                // check if blue
                if (z == 94) {
                    return Dragon.BLUE;
                    // check if orange
                } else if (z == 56) {
                    return Dragon.ORANGE;
                }
                // check if purple
            } else if (x == 56) {
                return Dragon.PURPLE;
            }
        }
        return Dragon.NONE;
    }

    private static Dragon getPrio(Dragon dragon1, Dragon dragon2) {
        if (DungeonClass.isArchTeam()) {
            if (dragon1.archPrio < dragon2.archPrio) {
                return dragon1;
            } else {
                return dragon2;
            }
        } else if (DungeonClass.isBersTeam()) {
            if (dragon1.bersPrio < dragon2.bersPrio) {
                return dragon1;
            } else {
                return dragon2;
            }
        }
        return Dragon.NONE;
    }

    public static boolean display() {
        return Floor7.dragSpawnTimers && currentDragon != Dragon.NONE;
    }

    public static void render(HUDComponent component, DrawContext context) {
        int x = component.getScaledX();
        int y = component.getScaledY();

        double num = tick * Constants.TICK_DURATION;

        RenderUtils.drawCenteredText(context, MinecraftClient.getInstance().textRenderer, Constants.DECIMAL_FORMAT.format(num), x, y, component.getWidth(), currentDragon.color);

    }
}
