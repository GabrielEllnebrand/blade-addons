package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.debug.Debug;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class DragSpawnTimer {

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

    static Dragon currentDragon = Dragon.NONE;
    static boolean hasDoneSplit = false;
    private static int tick = 0;

    public static void init() {
        Events.ON_PARTICLE.register(packet -> {
            if (!validParticle(packet)) return false;
            Dragon dragon = Dragon.getDragon(packet.getX(), packet.getY(), packet.getZ());
            testDragon(dragon);
            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            tick = Math.max(tick - 1, 0);

            if (tick == 0 && currentDragon != Dragon.NONE) {
                currentDragon = Dragon.NONE;
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(newLocation -> {
            reset();
            return false;
        });
    }

    private static void testDragon(Dragon dragon) {
        if (dragon == Dragon.NONE) return;

        if (currentDragon == Dragon.NONE) {
            Debug.sendDebugMessage(Text.literal("Drag: " + currentDragon.name()));
            currentDragon = dragon;
            tick = SPAWN_DURATION;

            if (Floor7.sendSoundOnDragSpawn) {
                Misc.sendSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 0.75f, 1);
            }

        } else if (currentDragon != dragon && !hasDoneSplit) {
            Debug.sendDebugMessage(Text.literal("comparing: " + currentDragon.name() + " and " +  dragon.name()));
            currentDragon = Dragon.getPrio(dragon, currentDragon);
            hasDoneSplit = true;
        }
    }

    private static boolean validParticle(ParticleS2CPacket packet) {
        if (packet.getCount() != 20) return false;
        if (packet.getY() != 19) return false;
        if (packet.getParameters().getType() != ParticleTypes.FLAME) return false;
        if (packet.getOffsetX() != 2) return false;
        if (packet.getOffsetY() != 3) return false;
        if (packet.getOffsetZ() != 2) return false;
        if (packet.getX() % 1 != 0) return false;
        if (packet.getZ() % 1 != 0) return false;
        return true;
    }

    private static void reset() {
        hasDoneSplit = false;
        currentDragon = Dragon.NONE;
        tick = 0;
    }

    public static boolean display() {
        return Floor7.dragSpawnTimers && currentDragon != Dragon.NONE;
    }

    public static void render(HUDComponent component, DrawContext context) {
        RenderUtils.drawTimer(component, context, tick, currentDragon.color);
    }
}
