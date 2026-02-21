package blade.addon.features.item;

public enum ItemRarity {
    NONE(0x0),
    COMMON(0xffdddddd),
    UNCOMMON(0xff42ab42),
    RARE(0xff4c4cd0),
    EPIC(0xff671067),
    LEGENDARY(0xffcf8d0a),
    MYTHIC(0xffff55ff),
    DIVINE(0xff4fe4e4),
    SPECIAL(0xffff5555),
    VERY_SPECIAL(0xffc44747),
    ULTIMATE(0xffa10202),
    ADMIN(0xffaa0000);

    private final int color;

    public int getColor() {
        return this.color;
    }

    ItemRarity(int color) {
        this.color = color;
    }
}
