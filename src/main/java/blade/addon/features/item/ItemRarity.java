package blade.addon.features.item;

public enum ItemRarity {
    NONE(0x0),
    COMMON(0x99dddddd),
    UNCOMMON(0x8842ab42),
    RARE(0x884c4cd0),
    EPIC(0x88671067),
    LEGENDARY(0x88cf8d0a),
    MYTHIC(0x88ff55ff),
    DIVINE(0x884fe4e4),
    SPECIAL(0x88ff5555),
    VERY_SPECIAL(0x88c44747),
    ULTIMATE(0x88a10202),
    ADMIN(0x88aa0000);

    private final int color;

    public int getColor() {
        return this.color;
    }

    ItemRarity(int color) {
        this.color = color;
    }
}
