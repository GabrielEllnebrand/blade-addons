package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.dungeon.DungeonClass;
import net.minecraft.util.math.Vec3d;

public enum Dragon {
    PURPLE(0xffff55ff, 0, 4, new Vec3d(56.5, 14, 125.5)),
    BLUE(0xff55ffff, 1, 3, new Vec3d(84.5, 14, 94.5)),
    RED(0xffff5555, 2, 2, new Vec3d (27.5, 14, 59.5)),
    GREEN(0xff55ff55, 3, 1, new Vec3d(27.5, 14, 94.5)),
    ORANGE(0xffffaa00, 4, 0, new Vec3d(84.5, 14, 56.5)),
    NONE(0xffffffff, 99, 99, new Vec3d(0, 0, 0));

    final int color;
    final int archPrio;
    final int bersPrio;
    final Vec3d spawnPos;

    Dragon(int color, int archPrio, int bersPrio, Vec3d spawnPos) {
        this.color = color;
        this.archPrio = archPrio;
        this.bersPrio = bersPrio;
        this.spawnPos = spawnPos;
    }

    //checks are from valley addons
    public static Dragon getDragon(double x, double y, double z) {
        if (y >= 14 && y <= 19) {
            if (x >= 27 && x <= 32) {
                if (z == 59) {
                    return Dragon.RED;
                } else if (z == 94) {
                    return Dragon.GREEN;
                }
            } else if (x >= 79 && x <= 85) {
                if (z == 94) {
                    return Dragon.BLUE;
                } else if (z == 56) {
                    return Dragon.ORANGE;
                }
            } else if (x == 56) {
                return Dragon.PURPLE;
            }
        }
        return Dragon.NONE;
    }

    public static Dragon getPrio(Dragon dragon1, Dragon dragon2) {

        if (DungeonClass.isArchTeam()) {
            if (DungeonClass.isClass(DungeonClass.HEALER)) {
                if (dragon1 == Dragon.PURPLE) {
                    return dragon2;
                } else if (dragon2 == Dragon.PURPLE) {
                    return dragon1;
                }
            }

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
}
