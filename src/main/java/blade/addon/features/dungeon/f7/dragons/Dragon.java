package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.dungeon.DungeonClass;

public enum Dragon {
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
