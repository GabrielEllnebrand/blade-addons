package blade.addon.utils;

public enum Locations {
    NONE,
    DUNGEON;

    public boolean inDungeon() {
        return this == Locations.DUNGEON;
    }
}
